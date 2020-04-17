package com.infomaximum.subsystems.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.database.domainobject.filter.Filter;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.iterator.IteratorEntity;

public interface RemovableResource<T extends DomainObject & DomainObjectEditable> extends EditableResource<T> {

    void remove(T obj, QueryTransaction transaction) throws SubsystemException;

    void clear(QueryTransaction transaction) throws SubsystemException;

    default boolean removeAll(Filter filter, QueryTransaction transaction) throws SubsystemException {
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
