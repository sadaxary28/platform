package com.infomaximum.platform.sdk.iterator;

import com.infomaximum.platform.exception.PlatformException;

public interface Iterator<T> extends AutoCloseable {

    boolean hasNext() throws PlatformException;

    T next() throws PlatformException;

    void close() throws PlatformException;
}
