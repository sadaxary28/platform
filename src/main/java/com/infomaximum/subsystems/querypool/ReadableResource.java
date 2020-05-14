package com.infomaximum.subsystems.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.filter.EmptyFilter;
import com.infomaximum.database.domainobject.filter.Filter;
import com.infomaximum.platform.sdk.function.Consumer;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.iterator.IteratorEntity;

import java.util.ArrayList;
import java.util.Set;

public interface ReadableResource<T extends DomainObject>  {

    Class<T> getDomainClass();

    T get(long id, QueryTransaction transaction) throws SubsystemException;

    T get(long id, Set<Integer> loadingFields, QueryTransaction transaction) throws SubsystemException;

    T find(final Filter filter, QueryTransaction transaction) throws SubsystemException;

    T find(final Filter filter, Set<Integer> loadingFields, QueryTransaction transaction) throws SubsystemException;

    IteratorEntity<T> iterator(QueryTransaction transaction) throws SubsystemException;

    IteratorEntity<T> iterator(Set<Integer> loadingFields, QueryTransaction transaction) throws SubsystemException;

    IteratorEntity<T> findAll(final Filter filter, QueryTransaction transaction) throws SubsystemException;

    IteratorEntity<T> findAll(final Filter filter, Set<Integer> loadingFields, QueryTransaction transaction) throws SubsystemException;

    default void forEach(final Filter filter, Consumer<T> action, QueryTransaction transaction) throws SubsystemException {
        try(IteratorEntity<T> it = findAll(filter, transaction)) {
            while (it.hasNext()) {
                action.accept(it.next());
            }
        }
    }

    default void forEach(Consumer<T> action, QueryTransaction transaction) throws SubsystemException {
        forEach(EmptyFilter.INSTANCE, action, transaction);
    }

    default ArrayList<T> getAll(final Filter filter, QueryTransaction transaction) throws SubsystemException {
        ArrayList<T> result = new ArrayList<>();
        forEach(filter, result::add, transaction);
        return result;
    }
}
