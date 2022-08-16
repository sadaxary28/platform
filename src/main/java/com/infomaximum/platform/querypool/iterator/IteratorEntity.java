package com.infomaximum.platform.querypool.iterator;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.platform.sdk.iterator.Iterator;

public class IteratorEntity<E extends DomainObject> implements Iterator<E> {

    private final com.infomaximum.database.domainobject.iterator.IteratorEntity<E> ie;

    public IteratorEntity(com.infomaximum.database.domainobject.iterator.IteratorEntity<E> ie) {
        this.ie = ie;
    }

    @Override
    public boolean hasNext() {
        return ie.hasNext();
    }

    @Override
    public E next() throws PlatformException {
        try {
            return ie.next();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public void close() throws PlatformException {
        try {
            ie.close();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
