package com.infomaximum.platform.update.updates;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.update.TestSubsystem;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;

@Update(
        componentUUID = TestSubsystem.UUID,
        version = "1_0_1_1"
)
public class TestUpdate1_0_1_1 extends UpdateTask<TestSubsystem> {

    public TestUpdate1_0_1_1(TestSubsystem component) {
        super(component);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {

    }
}
