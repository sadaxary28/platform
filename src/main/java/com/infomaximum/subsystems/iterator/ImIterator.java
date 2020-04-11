package com.infomaximum.subsystems.iterator;

import com.infomaximum.subsystems.exception.SubsystemException;

public interface ImIterator<T> extends AutoCloseable {

    boolean hasNext() throws SubsystemException;

    T next() throws SubsystemException;

    void close() throws SubsystemException;
}
