package com.agroknow.linkchecker;

import com.agroknow.domain.parser.ParserException;
import com.agroknow.linkchecker.domain.FileMetadata;
import com.agroknow.linkchecker.domain.LinkCheckerOptions;
import com.agroknow.linkchecker.exceptions.LinkCheckingException;
import com.agroknow.linkchecker.service.FileMetadataService;
import com.agroknow.linkchecker.service.LinkCheckingService;
import com.agroknow.linkchecker.service.MetricsRegistryHolder;
import com.agroknow.linkchecker.service.RedirectionRulesService;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private static final Options OPTS = new Options();
    private static final int NUMBER_OF_THREADS = 25;

    private App() {
        super();
    }

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        final LinkCheckerOptions options = parseOptions(args);
        LOG.info("Starting the linkchecker with the given options {}", options);

        // read files from root-directory
        Collection<File> files = FileUtils.listFiles(new File(options.getRootFolderPath()), new String[] { "json" }, true);
        int filesSize = files.size();

        if (CollectionUtils.isEmpty(files)) {
            LOG.error("The specified rootFolderPath is empty or does not contains any json files. Exiting....");
            return;
        }
        LOG.info("Found {} files to process.", filesSize);

        // initialize service instances
        final RedirectionRulesService redirectionRulesService = new RedirectionRulesService(options);
        final FileMetadataService fileMetaService = new FileMetadataService(options);
        final LinkCheckingService linkCheckingService = new LinkCheckingService(redirectionRulesService);

        final Semaphore semaphore = new Semaphore(filesSize);
        ExecutorService threadPool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        DefaultDeferredManager deferredManager = new DefaultDeferredManager(threadPool);

        for (File f : files) {
            try {
                final FileMetadata fileMeta = fileMetaService.readFile(f.getPath());
                if (fileMeta == null) {
                    throw new LinkCheckingException("Could not read file " + f.getPath() + " into FileMetadata");
                }
                
                processFile(fileMeta, semaphore, deferredManager, linkCheckingService, fileMetaService, options);
                
                MetricsRegistryHolder.getCounter("FILES[ALL]").inc();

            } catch (Exception ex) {
                MetricsRegistryHolder.getCounter("FILES[MALFORMED]").inc();
                LOG.error("Error reading file {}. Skipping...", f.getPath(), ex);
            }
        }

        // block until all finish
        semaphore.acquire(filesSize);

        // shutdown the deferredManager and the threadpool.
        deferredManager.shutdown();

        MetricsRegistryHolder.report();
        LOG.info("All files processed successfully.");
    }

    public static void processFile(final FileMetadata fileMeta, final Semaphore semaphore, final DefaultDeferredManager deferredManager, final LinkCheckingService linkCheckingService, final FileMetadataService fileMetaService, final LinkCheckerOptions options) throws InterruptedException {
        // 1. call the checkFileLocations to update the url status in
        // fileMeta
        semaphore.acquire();
        deferredManager.when(new Callable<FileMetadata>() {
            public FileMetadata call() {
                LOG.debug("Start link checking for file: {}", fileMeta.getFilePath());
                return linkCheckingService.checkFileLocations(fileMeta);
            }

            // 2. when link checking completes try to move/copy the file
            // to the correct location
        }).then(new DoneFilter<FileMetadata, FileMetadata>() {
            public FileMetadata filterDone(FileMetadata fileMeta) {
                try {
                    LOG.trace("Successfully checked links for file: {}", fileMeta.getFilePath());
                    MetricsRegistryHolder.getCounter(fileMeta.isFailed() ? "FILES[ERROR]" : "FILES[SUCCESS]").inc();
                    fileMetaService.updateOrCopyFile(fileMeta);
                } catch (IOException ex) {
                    throw new LinkCheckingException("Error handling file " + fileMeta.getFilePath() + " on result.", ex);
                } catch (ParserException ex) {
                    throw new LinkCheckingException("Error handling file " + fileMeta.getFilePath() + " on result.", ex);
                }

                return fileMeta;
            }

            // just in case link checking failed, log error and continue
        }, new FailFilter() {
            public Throwable filterFail(Throwable ex) {
                LOG.debug("Failed to check links for file: {}", fileMeta.getFilePath());
                return ex;
            }

            // 3. when moving/copying completes too just log happiness
        }).done(new DoneCallback<FileMetadata>() {
            public void onDone(FileMetadata fileMeta) {
                LOG.debug("Successfully processed file: {}", fileMeta.getFilePath());
            }

            // just in case moving / copying failed, log error and
            // continue
        }).fail(new FailCallback() {
            public void onFail(Throwable ex) {
                LOG.error("Failed to move/copy result file: {}", fileMeta.getFilePath(), ex);
            }

            // and release a permit on the semaphore please :) this is
            // crusial!
        }).always(new AlwaysCallback<FileMetadata>() {
            public void onAlways(Promise.State state, FileMetadata d, Throwable r) {
                semaphore.release();
            }
        });
    }

    public static void printHelp() {
        new HelpFormatter().printHelp("agro-indexer", getOptions(), true);
    }

    public static Options getOptions() {
        if (OPTS.getOptions().size() < 1) {
            OPTS.addOption(new Option(LinkCheckerOptions.SupportedOptions.HELP.getOptionName(), "print this message"));
            OPTS.addOption(Option.builder(LinkCheckerOptions.SupportedOptions.MODE.getOptionName()).argName("mode").hasArg().desc("the mode that the linkchecker is about to run. use 'support' for Suport mode, use 'live' for new incoming files.").required().build());
            OPTS.addOption(Option.builder(LinkCheckerOptions.SupportedOptions.FILE_FORMAT.getOptionName()).argName("fileFormat").hasArg().desc("the file format (AKIF, AGRIF etc) that the linkchecker is going to check.").required().build());
            OPTS.addOption(Option.builder(LinkCheckerOptions.SupportedOptions.ROOT_FOLDER_PATH.getOptionName()).argName("rootFolderPath").hasArg().desc("the folder where the files are located.").required().build());
            OPTS.addOption(Option.builder(LinkCheckerOptions.SupportedOptions.SUCCESS_FOLDER_PATH.getOptionName()).argName("successFolderPath").hasArg().desc("the folder where the OK files will be transfered in case of Active mode.").required(false).build());
            OPTS.addOption(Option.builder(LinkCheckerOptions.SupportedOptions.ERROR_FOLDER_PATH.getOptionName()).argName("errorFolderPath").hasArg().desc("the folder where the NOT OK files will be transfered in case of Active mode.").required(false).build());
            OPTS.addOption(Option.builder(LinkCheckerOptions.SupportedOptions.RULES_PATH.getOptionName()).argName("rulesPath").hasArg().desc("the path for the redirection rules file.").required(false).build());
        }
        return OPTS;
    }

    public static LinkCheckerOptions parseOptions(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();

        try {
            CommandLine cli = parser.parse(options, args);

            // if --help, print help and exit
            if (cli.hasOption("help")) {
                printHelp();
                System.exit(0);
            }

            LinkCheckerOptions linkCheckerOptions = LinkCheckerOptions.newInstance(cli, options);
            linkCheckerOptions.validate();

            return linkCheckerOptions;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), (LOG.isDebugEnabled() ? ex : null));
            printHelp();
            System.exit(1);
            return null;
        }
    }
}
