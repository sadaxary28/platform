package com.infomaximum.subsystems.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.subsystems.exception.SubsystemException;

class RemovableResourceImpl<T extends DomainObject & DomainObjectEditable> extends EditableResourceImpl<T> implements RemovableResource<T> {

	RemovableResourceImpl(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public void remove(T obj, QueryTransaction transaction) throws SubsystemException {
        try {
            transaction.getDBTransaction().remove(obj);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public void clear(QueryTransaction transaction) throws SubsystemException {
        try {
            transaction.getDBTransaction().removeAll(getDomainClass());
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
