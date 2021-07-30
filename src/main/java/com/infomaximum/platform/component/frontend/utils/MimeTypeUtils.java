package com.infomaximum.platform.component.frontend.utils;

import com.infomaximum.platform.component.frontend.struct.MimeType;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeUtils {

    private static final Map<String, MimeType> mimeTypes = new HashMap<>();
    static {
        mimeTypes.put("html", MimeType.HTML);
        mimeTypes.put("css", MimeType.CSS);
        mimeTypes.put("js", MimeType.JAVASCRIPT);
        mimeTypes.put("png", MimeType.PNG);
        mimeTypes.put("xls", MimeType.XLS);
        mimeTypes.put("xlsx", MimeType.XLSX);
        mimeTypes.put("exe", MimeType.EXE);
        mimeTypes.put("txt", MimeType.TEXT);
        mimeTypes.put("msi", MimeType.MSI);
        mimeTypes.put("dmg", MimeType.DMG);
        mimeTypes.put("zip", MimeType.ZIP);
    }

    public static MimeType findAutoMimeType(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return mimeTypes.getOrDefault(extension, new MimeType("application/octet-stream") );
    }

}
