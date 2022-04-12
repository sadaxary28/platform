package com.infomaximum.platform.update.updates;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.platform.update.TestSubsystem;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;

@Update(
        componentUUID = TestSubsystem.UUID,
        version = "1.0.4.x",
        previousVersion = "1.0.5.x"
)
public class TestUpdate1_0_5IncorrectVersionOrder extends UpdateTask<TestSubsystem> {

    public TestUpdate1_0_5IncorrectVersionOrder(TestSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    public void updateComponent(Transaction transaction) {

    }
}