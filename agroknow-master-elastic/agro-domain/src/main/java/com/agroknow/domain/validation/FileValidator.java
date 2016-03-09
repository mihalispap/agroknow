package com.agroknow.domain.validation;

import com.agroknow.domain.parser.ParserException;
import com.agroknow.domain.parser.factory.SimpleMetadataParserFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileValidator {

    private static final Logger LOG = LoggerFactory.getLogger(FileValidator.class);
    private static final Options OPTS = new Options();
    private static final int NUMBER_OF_THREADS = 8;
    private static String fileFormat;
    private static File rootFolder;
    private static File failFolder;
    private static final AtomicLong validCount = new AtomicLong(0);
    private static final AtomicLong failCount = new AtomicLong(0);

    /**
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        parseOptions(args);
        ExecutorService threadPool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        Collection<File> files = FileUtils.listFiles(rootFolder, new String[] { "json" }, true);
        final Semaphore semaphore = new Semaphore(files.size());

        long startTime = System.currentTimeMillis();
        for (final File file : files) {
            threadPool.execute(new Runnable() {
                public void run() {
                    try {
                        semaphore.acquire();
                        SimpleMetadataParserFactory.load(fileFormat, file.getAbsolutePath());
                        validCount.incrementAndGet();
                    } catch (ParserException e) {
                        failCount.incrementAndGet();
                        LOG.error("NOK : " + file + " [ " + e.getMessage() + " ]");
                        String targetFilePath = file.getAbsolutePath().replace(rootFolder.getAbsolutePath(), failFolder.getAbsolutePath());
                        try {
                            FileUtils.moveFileToDirectory(file, new File(targetFilePath), true);
                        } catch (IOException e1) {
                            LOG.error("Could not move file {} to {}", file.getAbsolutePath(), targetFilePath);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                    }
                }
            });
        }

        // block until all finish
        semaphore.acquire(files.size());

        // shutdown the deferredManager and the threadpool.
        threadPool.shutdown();

        LOG.info("All files checked successfully after {} seconds.", (System.currentTimeMillis() - startTime) / 1000);
        LOG.info("Valid files {}", validCount.longValue());
        LOG.info("Fail files {}", failCount.longValue());
    }

    public static Options getOptions() {
        if (OPTS.getOptions().size() < 1) {
            OPTS.addOption(new Option("help", "print this message"));
            OPTS.addOption(Option.builder("format").argName("fileFormat").hasArg().desc("the file format (AKIF, AGRIF etc) that the filechecker is going to check.").required().build());
            OPTS.addOption(Option.builder("rootFolder").argName("rootFolderPath").hasArg().desc("the folder where the files are located.").required().build());
            OPTS.addOption(Option.builder("failFolder").argName("failFolderPath").hasArg().desc("the folder where the NOT OK files will be transfered.").required().build());
        }
        return OPTS;
    }

    public static void printHelp() {
        new HelpFormatter().printHelp("agro-file-checker", getOptions(), true);
    }

    public static void parseOptions(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = getOptions();

        try {
            CommandLine cli = parser.parse(options, args);

            // if --help, print help and exit
            if (cli.hasOption("help")) {
                printHelp();
                System.exit(0);
            }

            fileFormat = cli.getOptionValue("format");
            if (!SimpleMetadataParserFactory.AKIF.equals(fileFormat) && !SimpleMetadataParserFactory.AGRIF.equals(fileFormat)) {
                printHelp();
                System.exit(1);
            }
            rootFolder = new File(cli.getOptionValue("rootFolder"));
            failFolder = new File(cli.getOptionValue("failFolder"));
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), (LOG.isDebugEnabled() ? ex : null));
            printHelp();
            System.exit(1);
        }
    }

}
