package org.crumbs.fileserver.handler;
import lombok.Getter;
import org.crumbs.mvc.common.model.Mime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum FileType {
    TXT(Mime.TEXT_PLAIN, ".txt"),
    HTML(Mime.TEXT_HTML, ".htm", ".html"),
    CSS(Mime.TEXT_CSS, ".css"),
    JAVASCRIPT(Mime.TEXT_JAVASCRIPT, ".js"),
    JSON(Mime.APPLICATION_JSON, ".json"),
    XHTML(Mime.XHTML, ".xhtm", ".xhtml"),
    XML(Mime.XML, ".xml"),
    ICO(Mime.ICO, ".ico"),
    BMP(Mime.BMP, ".bmp"),
    GIF(Mime.GIF, ".gif"),
    JPEG(Mime.JPEG, ".jpg", ".jpeg"),
    PNG(Mime.PNG, ".png"),
    SVG(Mime.SVG, ".svg"),
    PDF(Mime.PDF, ".pdf"),
    ZIP(Mime.ZIP, ".zip"),
    _NULL(Mime.APPLICATION_OCTET_STREAM);


    private Set<String> extensions;
    private Mime mime;

    FileType(Mime mime, String... extensions) {
        this.mime = mime;
        this.extensions = Set.of(extensions);
    }

    private static Map<String, FileType> fileTypes = new HashMap<>();

    static {
        Arrays.stream(FileType.values()).forEach(fileType -> {
            fileType.extensions.forEach(extension -> {fileTypes.put(extension, fileType);});
        });
    }

    public static FileType fromExtension(String extension) {
        return fileTypes.getOrDefault(extension, FileType._NULL);
    }
}
