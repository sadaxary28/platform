package com.infomaximum.platform.sdk.iterator;

import com.infomaximum.subsystems.exception.SubsystemException;

public interface Iterator<T> extends AutoCloseable {

    boolean hasNext() throws SubsystemException;

    T next() throws SubsystemException;

    void close() throws SubsystemException;
}
