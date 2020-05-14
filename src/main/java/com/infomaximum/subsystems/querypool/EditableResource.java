package com.infomaximum.subsystems.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.subsystems.exception.SubsystemException;

public interface EditableResource<T extends DomainObject & DomainObjectEditable> extends ReadableResource<T> {

    T create(QueryTransaction transaction) throws SubsystemException;

    void save(T newObj, QueryTransaction transaction) throws SubsystemException;
}
