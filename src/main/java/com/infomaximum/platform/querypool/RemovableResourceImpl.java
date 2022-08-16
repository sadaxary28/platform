package com.infomaximum.platform.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;

class RemovableResourceImpl<T extends DomainObject & DomainObjectEditable> extends EditableResourceImpl<T> implements RemovableResource<T> {

	RemovableResourceImpl(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public void remove(T obj, QueryTransaction transaction) throws PlatformException {
        try {
            transaction.getDBTransaction().remove(obj);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public void clear(QueryTransaction transaction) throws PlatformException {
        try {
            transaction.getDBTransaction().removeAll(getDomainClass());
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
