package com.infomaximum.subsystems.querypool.iterator;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.iterator.ImIterator;

public class IteratorEntity<E extends DomainObject> implements ImIterator<E> {

    private final com.infomaximum.database.domainobject.iterator.IteratorEntity<E> ie;

    public IteratorEntity(com.infomaximum.database.domainobject.iterator.IteratorEntity<E> ie) {
        this.ie = ie;
    }

    @Override
    public boolean hasNext() {
        return ie.hasNext();
    }

    @Override
    public E next() throws SubsystemException {
        try {
            return ie.next();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public void close() throws SubsystemException {
        try {
            ie.close();
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
