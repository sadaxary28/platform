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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UpdateUtilTest {

    @Test
    void checkIntegrityTest() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate1_0_1.class);
        tasks.add(TestUpdate1_0_2.class);
        tasks.add(TestUpdate1_0_3.class);
        tasks.add(TestUpdate1_0_4.class);

        UpdateUtil.checkIntegrity(tasks);
    }

    @Test
    void checkIntegrityTest2() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate1_0_1.class);
        tasks.add(TestUpdate1_0_2.class);
        tasks.add(TestUpdate1_0_3.class);
        tasks.add(TestUpdate1_0_4.class);
        tasks.add(TestUpdate1_1_0.class);

        UpdateUtil.checkIntegrity(tasks);
    }

    @Test
    void checkIntegrityFailBecauseDoesntContains1_0_3Test() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate1_0_1.class);
        tasks.add(TestUpdate1_0_2.class);
        tasks.add(TestUpdate1_0_4.class);
        Assertions.assertThatThrownBy(() -> UpdateUtil.checkIntegrity(tasks))
                .isExactlyInstanceOf(UpdateException.class)
                .hasMessage("Integrity error. Can't find previous update version: " + new Version(1,0,3, 0));
    }

    @Test
    void checkIntegrityFailBecauseDuplicate1_0_3Test() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate1_0_1.class);
        tasks.add(TestUpdate1_0_2.class);
        tasks.add(TestUpdate1_0_3.class);
        tasks.add(TestUpdate1_0_3Repeat.class);
        tasks.add(TestUpdate1_0_4.class);
        Assertions.assertThatThrownBy(() -> UpdateUtil.checkIntegrity(tasks))
                .isExactlyInstanceOf(UpdateException.class)
                .hasMessage("Integrity error. Previous update version is already exist: " + new Version(1,0,2, 0));
    }

    @Test
    void checkIntegrityFailBecauseIncorrectVersionOrderTest() {
        Set<Class<? extends UpdateTask<TestSubsystem>>> tasks = new HashSet<>();
        tasks.add(TestUpdate1_0_1.class);
        tasks.add(TestUpdate1_0_2.class);
        tasks.add(TestUpdate1_0_3.class);
        tasks.add(TestUpdate1_0_5IncorrectVersionOrder.class);
        tasks.add(TestUpdate1_0_4.class);
        Assertions.assertThatThrownBy(() -> UpdateUtil.checkIntegrity(tasks))
                .isExactlyInstanceOf(UpdateException.class)
                .hasMessage("Integrity error. Update version: 1.0.4.0 is less or equal to previous: " + new Version(1,0,5, 0));
    }

    @Test
    public void isNotConsistentUpdateTest() {
        assertTrue(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 3, 0),
                new Version(3, 24, 4, 0))
        );
        assertTrue(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 3, 0),
                new Version(1, 26, 4, 0))
        );
        assertTrue(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 3, 0),
                new Version(1, 24, 5, 0))
        );
        assertTrue(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 12, 0),
                new Version(1, 24, 13, 0))
        );
        assertTrue(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 12, 0),
                new Version(1, 25, 2, 0))
        );
        assertTrue(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 12, 0),
                new Version(1, 26, 1, 0))
        );
        assertTrue(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 12, 0),
                new Version(1, 25, 0, 0))
        );
        assertTrue(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 3, 0),
                new Version(1, 25, 4, 0))
        );

        assertFalse(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 3, 0),
                new Version(1, 24, 4, 0))
        );
        assertFalse(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 12, 0),
                new Version(1, 25, 1, 0))
        );
        assertFalse(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 4, 0),
                new Version(2, 24, 5, 0))
        );
        assertFalse(UpdateUtil.isNotConsistentVersions(
                new Version(1, 24, 4, 0),
                new Version(1, 24, 4, 0))
        );
    }
}
