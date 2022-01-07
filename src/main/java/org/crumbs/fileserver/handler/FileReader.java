package org.crumbs.fileserver.handler;

import org.crumbs.core.logging.Logger;
import org.crumbs.mvc.exception.InternalServerErrorException;
import org.crumbs.mvc.exception.NotFoundException;
import org.crumbs.mvc.http.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class FileReader {

    private static Logger logger = Logger.getLogger(FileReader.class);

    public FileReader(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    private int bufferSize;

    public void writeFileToResponse(String absolutePath, String relativePath, Response target) throws IOException {
        URI uri;
        try {
            uri = URI.create("file:" + absolutePath);
        } catch (IllegalArgumentException ex) {
            throw new InternalServerErrorException("Invalid uri format for path: " + absolutePath, ex);
        }
        File file = new File(uri);
        if(!file.exists()) {
            logger.debug("Could not find file {}", absolutePath);
            throw new NotFoundException(relativePath);
        }
        copyBuffered(new FileInputStream(file), target.getOutputStream());
    }

    private void copyBuffered(FileInputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[bufferSize];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
        target.close();
    }
}
