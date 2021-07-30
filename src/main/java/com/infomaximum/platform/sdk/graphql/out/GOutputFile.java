package com.infomaximum.platform.sdk.graphql.out;

import com.infomaximum.cluster.core.io.ClusterFile;
import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.platform.component.frontend.struct.MimeType;
import com.infomaximum.platform.component.frontend.utils.MimeTypeUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GOutputFile implements RemoteObject {

    public final String fileName;

    public final URI uri;
    public final byte[] body;

    public final MimeType mimeType;

    public final boolean temp;

    public GOutputFile(ClusterFile clusterFile, String fileName, boolean temp) {
        this(clusterFile.getUri(), fileName, temp);
    }

    private GOutputFile(URI uri, String fileName, boolean temp) {
        this.fileName = fileName;

        this.uri = uri;
        this.body = null;

        this.mimeType = MimeTypeUtils.findAutoMimeType(fileName);

        this.temp = temp;
    }

    private GOutputFile(String fileName, byte[] body, MimeType mimeType) {
        this.fileName = fileName;

        this.uri = null;
        this.body = body;

        this.mimeType = mimeType;

        this.temp = false;
    }

    public long getSize() {
        if (uri != null) {
            try {
                return Files.size(Paths.get(uri));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (body != null) {
            return body.length;
        } else {
            throw new RuntimeException("Not support mode");
        }
    }
}
