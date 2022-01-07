package org.crumbs.fileserver.handler;

import org.crumbs.core.annotation.CrumbInit;
import org.crumbs.core.annotation.Property;
import org.crumbs.core.logging.Logger;
import org.crumbs.mvc.common.model.HttpMethod;
import org.crumbs.mvc.common.model.HttpStatus;
import org.crumbs.mvc.common.model.Mime;
import org.crumbs.mvc.exception.CrumbsMVCInitException;
import org.crumbs.mvc.exception.HttpMethodNotAllowedException;
import org.crumbs.mvc.exception.InternalServerErrorException;
import org.crumbs.mvc.http.Request;
import org.crumbs.mvc.http.Response;
import org.crumbs.mvc.interceptor.HandlerInterceptor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

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
    public void init() throws MalformedURLException {
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
        int extensionIdx = finalPath.lastIndexOf(".");
        String type = extensionIdx > 0 ? finalPath.substring(finalPath.lastIndexOf(".")) : "";
        logger.debug("Serving file of type: {}", type);
        switch (type) {
            case ".txt":
                response.setMime(Mime.TEXT_PLAIN);
                break;
            case ".html":
            case ".htm":
                response.setMime(Mime.TEXT_HTML);
                break;
            case ".css":
                response.setMime(Mime.TEXT_CSS);
                break;
            case ".js":
                response.setMime(Mime.TEXT_JAVASCRIPT);
                break;
            case ".json":
                response.setMime(Mime.APPLICATION_JSON);
                break;
            case ".xhtml":
                response.setMime(Mime.XHTML);
                break;
            case ".xml":
                response.setMime(Mime.XML);
                break;
            case ".ico":
                response.setMime(Mime.ICO);
                break;
            case ".bmp":
                response.setMime(Mime.BMP);
                break;
            case ".gif":
                response.setMime(Mime.GIF);
                break;
            case ".jpg":
            case ".jpeg":
                response.setMime(Mime.JPEG);
                break;
            case ".png":
                response.setMime(Mime.PNG);
                break;
            case ".svg":
                response.setMime(Mime.SVG);
                break;
            case ".pdf":
                response.setMime(Mime.PDF);
                break;
            case ".zip":
                response.setMime(Mime.ZIP);
                break;
            default:
                response.setMime(Mime.APPLICATION_OCTET_STREAM);
        }
        response.setStatus(HttpStatus.OK);
        try {
            reader.writeFileToResponse(finalPath, path,  response);
        } catch (IOException ex) {
            throw new InternalServerErrorException("IO error: Unable to copy from file to output stream", ex);
        }
        return false;
    }
}
