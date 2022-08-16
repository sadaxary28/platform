package com.infomaximum.platform.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.database.domainobject.filter.Filter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;

public interface RemovableResource<T extends DomainObject & DomainObjectEditable> extends EditableResource<T> {

    void remove(T obj, QueryTransaction transaction) throws PlatformException;

    void clear(QueryTransaction transaction) throws PlatformException;

    default boolean removeAll(Filter filter, QueryTransaction transaction) throws PlatformException {
        boolean result = false;
        try (IteratorEntity<T> i = findAll(filter, transaction)) {
            while (i.hasNext()) {
                remove(i.next(), transaction);
                result = true;
            }
        }
        return result;
    }
}
