package com.infomaximum.subsystems.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.subsystems.exception.SubsystemException;

class EditableResourceImpl<T extends DomainObject & DomainObjectEditable> extends ReadableResourceImpl<T> implements EditableResource<T> {

    EditableResourceImpl(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public T create(QueryTransaction transaction) throws SubsystemException {
        try {
            return transaction.getDBTransaction().create(tClass);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }

    @Override
    public void save(T newObj, QueryTransaction transaction) throws SubsystemException {
        try {
            transaction.getDBTransaction().save(newObj);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }
    }
}
