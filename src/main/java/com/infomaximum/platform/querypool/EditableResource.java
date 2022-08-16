package com.infomaximum.platform.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.platform.exception.PlatformException;

public interface EditableResource<T extends DomainObject & DomainObjectEditable> extends ReadableResource<T> {

    T create(QueryTransaction transaction) throws PlatformException;

    void save(T newObj, QueryTransaction transaction) throws PlatformException;
}
