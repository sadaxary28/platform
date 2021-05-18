package com.infomaximum.platform.update.updates;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.platform.update.TestSubsystem;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import com.infomaximum.platform.update.exception.UpdateException;

@Update(
        componentUUID = TestSubsystem.UUID,
        version = "1.0.1",
        previousVersion = "1.0.0"
)
public class TestUpdate0_1_0_1 extends UpdateTask<TestSubsystem> {

    public TestUpdate0_1_0_1(TestSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    public void updateComponent(Transaction transaction) {
        try {
            Schema schema = getSchema(transaction);
//            schema.createTable(new Table());
//            schema.renameTable(); //
//            schema.dropTable(); //
//            schema.createField();
//            schema.dropField();
//            schema.renameField();
//            schema.createIndex();
//            schema.dropIndex();

        } catch (DatabaseException e) {
            throw new UpdateException(e);
        }
    }
}
