package com.infomaximum.platform.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.filter.EmptyFilter;
import com.infomaximum.database.domainobject.filter.Filter;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;

import java.util.Set;

class ReadableResourceImpl<T extends DomainObject> implements ReadableResource<T> {

    protected final Class<T> tClass;

    ReadableResourceImpl(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public Class<T> getDomainClass() {
        return tClass;
    }

    @Override
    public T get(long id, QueryTransaction transaction) throws PlatformException {
        return get(id, null, transaction);
    }

    @Override
    public T get(long id, Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        try {
            return transaction.getDBTransaction().get(tClass, id, loadingFields);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public T find(final Filter filter, QueryTransaction transaction) throws PlatformException {
        return find(filter, null, transaction);
    }

    @Override
    public T find(Filter filter, Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        try (com.infomaximum.database.domainobject.iterator.IteratorEntity<T> iter = transaction.getDBTransaction().find(tClass, filter, loadingFields)) {
            return iter.hasNext() ? iter.next() : null;
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public IteratorEntity<T> iterator(QueryTransaction transaction) throws PlatformException {
        return iterator(null, transaction);
    }

    @Override
    public IteratorEntity<T> iterator(Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        try {
            return new IteratorEntity<>(transaction.getDBTransaction().find(tClass, EmptyFilter.INSTANCE, loadingFields));
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public IteratorEntity<T> findAll(final Filter filter, QueryTransaction transaction) throws PlatformException {
        return findAll(filter, null, transaction);
    }

    @Override
    public IteratorEntity<T> findAll(Filter filter, Set<Integer> loadingFields, QueryTransaction transaction) throws PlatformException {
        try {
            return new IteratorEntity<>(transaction.getDBTransaction().find(tClass, filter, loadingFields));
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
