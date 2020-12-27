package com.infomaximum.platform.component.frontend.utils;

import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeUtils {

    private static final Map<String, String> mimeTypes = new HashMap<>();
    static {
        mimeTypes.put("html", "text/html; charset=UTF-8");
        mimeTypes.put("css", "text/css; charset=UTF-8");
        mimeTypes.put("js", "text/javascript; charset=UTF-8");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("xls", "application/vnd.ms-excel");
        mimeTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mimeTypes.put("exe", "application/x-msdownload");
        mimeTypes.put("txt", "text/plain; charset=UTF-8");
        mimeTypes.put("msi", "application/x-msi");
        mimeTypes.put("dmg", "application/x-apple-diskimage");
        mimeTypes.put("zip", "application/zip");
    }

    public static String findAutoMimeType(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return mimeTypes.getOrDefault(extension, "application/octet-stream");
    }

}
