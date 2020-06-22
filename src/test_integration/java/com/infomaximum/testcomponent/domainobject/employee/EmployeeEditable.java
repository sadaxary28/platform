package com.infomaximum.testcomponent.domainobject.employee;

import com.infomaximum.database.domainobject.DomainObjectEditable;


public class EmployeeEditable extends EmployeeReadable implements DomainObjectEditable {

    public EmployeeEditable(long id) {
        super(id);
    }

    public void setFirstName(String firstName) {
        set(FIELD_FIRST_NAME, firstName);
    }

    public void setSecondName(String secondName) {
        set(FIELD_SECOND_NAME, secondName);
    }

}
