package com.infomaximum.subsystems.exception;

import graphql.language.SourceLocation;

import java.util.List;

public class GraphQLWrapperSubsystemException extends SubsystemException {

    private List<SourceLocation> sourceLocations;

    public GraphQLWrapperSubsystemException(SubsystemException subsystemException) {
        this(subsystemException, null);
    }

    public GraphQLWrapperSubsystemException(SubsystemException subsystemException, List<SourceLocation> sourceLocations) {
        super("wrapper", null, null, subsystemException);
        this.sourceLocations = sourceLocations;
    }

    public SubsystemException getSubsystemException() {
        return (SubsystemException) getCause();
    }

    public List<SourceLocation> getSourceLocations() {
        return sourceLocations;
    }
}
