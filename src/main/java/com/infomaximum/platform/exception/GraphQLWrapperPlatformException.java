package com.infomaximum.platform.exception;

import graphql.language.SourceLocation;

import java.util.List;

public class GraphQLWrapperPlatformException extends PlatformException {

    private List<SourceLocation> sourceLocations;

    public GraphQLWrapperPlatformException(PlatformException platformException) {
        this(platformException, null);
    }

    public GraphQLWrapperPlatformException(PlatformException subsystemException, List<SourceLocation> sourceLocations) {
        super("wrapper", null, null, subsystemException);
        this.sourceLocations = sourceLocations;
    }

    public PlatformException getPlatformException() {
        return (PlatformException) getCause();
    }

    public List<SourceLocation> getSourceLocations() {
        return sourceLocations;
    }
}
