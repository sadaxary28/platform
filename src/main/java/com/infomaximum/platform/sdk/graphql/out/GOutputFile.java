package com.infomaximum.platform.sdk.graphql.out;

import com.infomaximum.cluster.core.io.ClusterFile;
import com.infomaximum.cluster.core.remote.struct.RemoteObject;

import java.net.URI;

public class GOutputFile implements RemoteObject {

    public final URI uri;
    public final String fileName;
    public final boolean temp;

    public GOutputFile(ClusterFile clusterFile, String fileName, boolean temp) {
        this(clusterFile.getUri(), fileName, temp);
    }

    private GOutputFile(URI uri, String fileName, boolean temp) {
        this.uri = uri;
        this.fileName = fileName;
        this.temp = temp;
    }
}
