package com.infomaximum.platform.update.util;

import com.infomaximum.platform.sdk.component.version.Version;
import com.infomaximum.platform.update.TestSubsystem;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.exception.UpdateException;
import com.infomaximum.platform.update.updates.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class UpdateUtilTest {

    @Test
    void checkIntegrityTest() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate0_1_0_1.class);
        tasks.add(TestUpdate0_1_0_2.class);
        tasks.add(TestUpdate0_1_0_3.class);
        tasks.add(TestUpdate0_1_0_4.class);

        UpdateUtil.checkIntegrity(tasks);
    }

    @Test
    void checkIntegrityTest2() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate0_1_0_1.class);
        tasks.add(TestUpdate0_1_0_2.class);
        tasks.add(TestUpdate0_1_0_3.class);
        tasks.add(TestUpdate0_1_0_4.class);
        tasks.add(TestUpdate0_1_1_0.class);

        UpdateUtil.checkIntegrity(tasks);
    }

    @Test
    void checkIntegrityFailBecauseDoesntContains1_0_3Test() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate0_1_0_1.class);
        tasks.add(TestUpdate0_1_0_2.class);
        tasks.add(TestUpdate0_1_0_4.class);
        Assertions.assertThatThrownBy(() -> UpdateUtil.checkIntegrity(tasks))
                .isExactlyInstanceOf(UpdateException.class)
                .hasMessage("Integrity error. Can't find previous update version: " + new Version(0, 1,0,3));
    }

    @Test
    void checkIntegrityFailBecauseDuplicate1_0_3Test() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate0_1_0_1.class);
        tasks.add(TestUpdate0_1_0_2.class);
        tasks.add(TestUpdate0_1_0_3.class);
        tasks.add(TestUpdate0_1_0_3Repeat.class);
        tasks.add(TestUpdate0_1_0_4.class);
        Assertions.assertThatThrownBy(() -> UpdateUtil.checkIntegrity(tasks))
                .isExactlyInstanceOf(UpdateException.class)
                .hasMessage("Integrity error. Previous update version is already exist: " + new Version(0, 1,0,2));
    }

    @Test
    void checkIntegrityFailBecauseIncorrectVersionOrderTest() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate0_1_0_1.class);
        tasks.add(TestUpdate0_1_0_2.class);
        tasks.add(TestUpdate0_1_0_3.class);
        tasks.add(TestUpdate0_1_0_5IncorrectVersionOrder.class);
        tasks.add(TestUpdate0_1_0_4.class);
        Assertions.assertThatThrownBy(() -> UpdateUtil.checkIntegrity(tasks))
                .isExactlyInstanceOf(UpdateException.class)
                .hasMessage("Integrity error. Update version: 0.1.0.4 is less or equal to previous: " + new Version(0, 1,0,5));
    }
}
