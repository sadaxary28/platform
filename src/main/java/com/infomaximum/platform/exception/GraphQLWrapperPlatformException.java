package com.infomaximum.platform.exception;

import com.infomaximum.cluster.graphql.executor.struct.GSourceLocation;

import java.util.List;

public class GraphQLWrapperPlatformException extends PlatformException {

    private List<GSourceLocation> sourceLocations;

    public GraphQLWrapperPlatformException(PlatformException platformException) {
        this(platformException, null);
    }

    public GraphQLWrapperPlatformException(PlatformException subsystemException, List<GSourceLocation> sourceLocations) {
        super("wrapper", null, null, subsystemException);
        this.sourceLocations = sourceLocations;
    }

    public PlatformException getPlatformException() {
        return (PlatformException) getCause();
    }

    public List<GSourceLocation> getSourceLocations() {
        return sourceLocations;
    }
}
