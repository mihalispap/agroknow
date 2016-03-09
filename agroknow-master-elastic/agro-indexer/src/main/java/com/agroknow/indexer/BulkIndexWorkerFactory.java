
package com.agroknow.indexer;

import com.agroknow.domain.agrif.Agrif;
import com.agroknow.domain.akif.Akif;
import com.agroknow.domain.parser.factory.SimpleMetadataParserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import org.elasticsearch.client.Client;

/**
 *
 * @author aggelos
 */
public class BulkIndexWorkerFactory {

    /**
     * Create a BulkIndexWorker instance.
     *
     * @param fileFormat The fileFormat under processing
     * @param files The list of Files to process
     * @param charset The charset to read files with
     * @param objectMapper  The objectMapper to use to serialize/deserialize json
     * @param lastCheck The timestamp of indexer's last run
     * @param esClient The elasticsearch client to use for (bulk) indexing
     *
     * @return the BulkIndexWorker instance
     */
    public static BulkIndexWorker getWorker(String fileFormat, List<File> files, Charset charset, ObjectMapper objectMapper, long lastCheck, Client esClient) {
        BulkIndexWorker worker = null;
        Class fileFormatClass = null;

        // create worker instance for fileFormat
        if(SimpleMetadataParserFactory.AKIF.equalsIgnoreCase(fileFormat)) {
            fileFormatClass = Akif.class;
            worker = new BulkIndexWorker<Akif>();

        } else if(SimpleMetadataParserFactory.AGRIF.equalsIgnoreCase(fileFormat)) {
            fileFormatClass = Agrif.class;
            worker = new BulkIndexWorker<Agrif>();
        }


        // if worker created for fileFormat, initialize it
        if(worker != null) {
            worker.init(fileFormat, fileFormatClass, files, charset, objectMapper, lastCheck, esClient);
        }

        // return worker (it will be null if fileFormat is not supported)
        return worker;
    }
}
