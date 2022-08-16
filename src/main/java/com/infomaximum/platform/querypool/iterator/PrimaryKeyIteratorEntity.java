package com.infomaximum.platform.querypool.iterator;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.sdk.iterator.Iterator;

import java.util.Set;

public class PrimaryKeyIteratorEntity<E extends DomainObject> implements Iterator<E> {

    private java.util.Iterator<Long> iterator;
    private ReadableResource<E> readableResource;
    private QueryTransaction transaction;

    public PrimaryKeyIteratorEntity(Set<Long> pKeys, ReadableResource<E> readableResource, QueryTransaction transaction) {
        this.iterator = pKeys.iterator();
        this.readableResource = readableResource;
        this.transaction = transaction;
    }

    @Override
    public boolean hasNext() throws PlatformException {
        return iterator.hasNext();
    }

    @Override
    public E next() throws PlatformException {
        Long pKey = iterator.next();
        return pKey != null ? readableResource.get(pKey, transaction) : null;
    }

    @Override
    public void close() throws PlatformException {

    }
}
