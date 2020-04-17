package com.infomaximum.subsystems.querypool.iterator;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.iterator.ImIterator;
import com.infomaximum.subsystems.querypool.QueryTransaction;
import com.infomaximum.subsystems.querypool.ReadableResource;

import java.util.Iterator;
import java.util.Set;

public class PrimaryKeyIteratorEntity<E extends DomainObject> implements ImIterator<E> {

    private Iterator<Long> iterator;
    private ReadableResource<E> readableResource;
    private QueryTransaction transaction;

    public PrimaryKeyIteratorEntity(Set<Long> pKeys, ReadableResource<E> readableResource, QueryTransaction transaction) {
        this.iterator = pKeys.iterator();
        this.readableResource = readableResource;
        this.transaction = transaction;
    }

    @Override
    public boolean hasNext() throws SubsystemException {
        return iterator.hasNext();
    }

    @Override
    public E next() throws SubsystemException {
        Long pKey = iterator.next();
        return pKey != null ? readableResource.get(pKey, transaction) : null;
    }

    @Override
    public void close() throws SubsystemException {

    }
}
