package com.agroknow.linkchecker.service;

import com.agroknow.domain.SimpleMetadata;
import com.agroknow.domain.parser.ParserException;
import com.agroknow.domain.parser.factory.SimpleMetadataParserFactory;
import com.agroknow.linkchecker.domain.FileMetadata;
import com.agroknow.linkchecker.domain.LinkCheckerOptions;
import com.agroknow.linkchecker.domain.URLMetadata;
import com.agroknow.linkchecker.exceptions.NotSupportedProtocolException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status.Family;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileMetadataService {
    private static final Logger LOG = LoggerFactory.getLogger(FileMetadataService.class);

    private LinkCheckerOptions options;

    public FileMetadataService() {}

    public FileMetadataService(LinkCheckerOptions options) {
        this.options = options;
    }

    public FileMetadata readFile(String filePath) throws ParserException {
        SimpleMetadata tmpMetadata = SimpleMetadataParserFactory.load(options.getFileFormat(), filePath);
        
        if (CollectionUtils.isEmpty(tmpMetadata.getIdentifiers()) || tmpMetadata.getIdentifiers().size() > 1) {
            LOG.error("Unsupported identifiers size {} for file {}. skipping", tmpMetadata.getIdentifiers().size(), filePath);
            return null;
        }

        FileMetadata dto = new FileMetadata(filePath, CollectionUtils.get(tmpMetadata.getIdentifiers(), 0).toString(), options.getFileFormat());
        for (String url : tmpMetadata.getLocations()) {
            try {
                if (!url.startsWith("http")) {
                    throw new NotSupportedProtocolException("Protocol not supported : " + url);
                }

                URL u = new URL(url);
                if (!u.getProtocol().equals("http")) {
                    throw new NotSupportedProtocolException("Protocol not supported : " + url);
                }
                dto.addLocation(new URLMetadata(u.getHost(), url));
            } catch (MalformedURLException e) {
                LOG.debug("Malformed URL {} in file with id : {}", url, dto.getIdentifier());
                dto.addLocation(new URLMetadata(null, url, Family.OTHER, -1));
                dto.setFailed(true);
            }
        }
        return dto;
    }

    public void updateOrCopyFile(FileMetadata fileMeta) throws IOException, ParserException {
        if (options.isSupportMode()) {
            this.updateFile(fileMeta);
        } else {
            this.copyFile(fileMeta.getFilePath(), !fileMeta.isFailed());
        }
    }
    
    public void copyFile(String filePath, boolean success) {
        LOG.trace("Ready to move file {} to {} folder.", filePath, success ? "success" : "error");

        File rootFolder = new File(options.getRootFolderPath());
        File successFolder = new File(options.getSuccessFolderPath());
        File errorFolder = new File(options.getErrorFolderPath());

        String targetFilePath = filePath.replace(rootFolder.getPath(), success ? successFolder.getPath() : errorFolder.getPath());
        try {
            FileUtils.copyFile(new File(filePath), new File(targetFilePath));
        } catch (Exception e) {
            LOG.error("Error while moving {} to {}", new Object[] { filePath, targetFilePath }, e);
        }
    }

    @SuppressWarnings("unchecked")
    public void updateFile(FileMetadata fileMeta) throws ParserException, IOException {
        if(CollectionUtils.isEmpty(fileMeta.getLocations())) {
            return;
        }
        
        Map<String, Boolean> urlsMap = new HashMap<String, Boolean>();
        for(URLMetadata url : fileMeta.getLocations()) {
            urlsMap.put(url.getUrl(), url.isBroken());
        }
        SimpleMetadataParserFactory.updateFileUrls(options.getFileFormat(), fileMeta.getFilePath(), fileMeta.getIdentifier(), urlsMap);
    }

    public void setOptions(LinkCheckerOptions options) {
        this.options = options;
    }
}
