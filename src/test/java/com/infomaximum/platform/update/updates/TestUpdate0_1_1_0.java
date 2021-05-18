package com.infomaximum.platform.update.updates;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.platform.update.TestSubsystem;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;

@Update(
        componentUUID = TestSubsystem.UUID,
        version = "1.1.0",
        previousVersion = "1.0.4"
)
public class TestUpdate0_1_1_0 extends UpdateTask<TestSubsystem> {

    public TestUpdate0_1_1_0(TestSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    public void updateComponent(Transaction transaction) {

    }
}