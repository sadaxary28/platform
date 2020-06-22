package com.infomaximum.testcomponent.domainobject.employee;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.testcomponent.TestComponent;

/**
 * Created by kris on 30.09.16.
 */
@Entity(
		namespace = TestComponent.UUID,
		name = "Employee",
		fields = {
				@Field(name = "first_name", number = EmployeeReadable.FIELD_FIRST_NAME,
						type = String.class),
				@Field(name = "second_name", number = EmployeeReadable.FIELD_SECOND_NAME,
						type = String.class)
		}
)
public class EmployeeReadable extends DomainObject {

	public final static int FIELD_FIRST_NAME = 0;
	public final static int FIELD_SECOND_NAME = 1;

	public EmployeeReadable(long id) {
		super(id);
	}

	@Override
	public long getId() {
		return super.getId();
	}

	public String getFirstName() {
		return getString(FIELD_FIRST_NAME);
	}

	public String getSecondName() {
		return getString(FIELD_SECOND_NAME);
	}

}
