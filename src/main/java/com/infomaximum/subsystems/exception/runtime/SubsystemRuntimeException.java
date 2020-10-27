package com.infomaximum.subsystems.exception.runtime;

import com.infomaximum.subsystems.exception.SubsystemException;

public class SubsystemRuntimeException extends RuntimeException {

    private SubsystemException subsystemException;

    public SubsystemRuntimeException(SubsystemException subsystemException) {
        super(subsystemException);
        this.subsystemException = subsystemException;
    }

    public SubsystemRuntimeException(String message, SubsystemException subsystemException) {
        super(message, subsystemException);
        this.subsystemException = subsystemException;
    }

    public SubsystemException getSubsystemException() {
        return subsystemException;
    }
}
