package org.crumbs.fileserver.handler;

import org.crumbs.core.annotation.CrumbInit;
import org.crumbs.core.annotation.Property;
import org.crumbs.core.logging.Logger;
import org.crumbs.mvc.common.model.HttpMethod;
import org.crumbs.mvc.common.model.HttpStatus;
import org.crumbs.mvc.exception.CrumbsMVCInitException;
import org.crumbs.mvc.exception.HttpMethodNotAllowedException;
import org.crumbs.mvc.exception.InternalServerErrorException;
import org.crumbs.mvc.http.Request;
import org.crumbs.mvc.http.Response;
import org.crumbs.mvc.interceptor.HandlerInterceptor;

import java.io.File;
import java.io.IOException;

public class FileServer implements HandlerInterceptor {

    private Logger logger = Logger.getLogger(FileServer.class);

    private FileReader reader;

    @Property("fileserver.root.path")
    private String rootPath;

    @Property("fileserver.root.indexfile")
    private String rootIndexFile;

    @Property("fileserver.buffer.size")
    private Integer bufferSize;

    @CrumbInit
    public void init() {
        reader = new FileReader(bufferSize);
        if(rootPath.equals("/")) {
            throw new CrumbsMVCInitException("Fileserver init requires property \"fileserver.root.path\" " +
                    "to be defined different than filesystem root");
        }
        File file = new File(rootPath);
        if(!file.exists() || !file.isDirectory()) {
            throw new CrumbsMVCInitException("Root path needs to be an existing directory in filesystem");
        }

        rootPath = file.toURI().getPath();

        logger.info("Successfully initialized root path to {}", rootPath);
    }

    public boolean handle(Request request, Response response) {
        String path = request.getUrlPath();

        if(request.getMethod() != HttpMethod.GET) {
            throw new HttpMethodNotAllowedException("Only http GET allowed");
        }

        if(path.equals("/")) {
            path = "/" + rootIndexFile;
        }

        String finalPath = rootPath + path;
        String type = getFileExtension(finalPath);
        logger.debug("Serving file of type: {}", type);

        response.setMime(FileType.fromExtension(type).getMime());
        response.setStatus(HttpStatus.OK);
        try {
            reader.writeFileToResponse(finalPath, path,  response);
        } catch (IOException ex) {
            throw new InternalServerErrorException("IO error: Unable to copy from file to output stream", ex);
        }
        return false;
    }

    private String getFileExtension(String finalPath) {
        int extensionIdx = finalPath.lastIndexOf(".");
        return extensionIdx > 0 ? finalPath.substring(finalPath.lastIndexOf(".")) : "";
    }
}
