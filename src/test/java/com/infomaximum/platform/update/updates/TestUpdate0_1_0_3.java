package com.infomaximum.platform.update.updates;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.platform.update.TestSubsystem;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;

@Update(
        componentUUID = TestSubsystem.UUID,
        version = "1.0.3",
        previousVersion = "1.0.2"
)
public class TestUpdate0_1_0_3 extends UpdateTask<TestSubsystem> {

    public TestUpdate0_1_0_3(TestSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    public void updateComponent(Transaction transaction) {

    }
}