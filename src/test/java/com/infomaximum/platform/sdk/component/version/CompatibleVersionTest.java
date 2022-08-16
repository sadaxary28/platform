package com.infomaximum.platform.sdk.component.version;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompatibleVersionTest {

    @Test
    public void constructor() {
        CompatibleVersion ver = new CompatibleVersion(new Version(1, 5, 3, 1));
        Assertions.assertEquals(new Version(1, 5, 3,0), ver.getMinimum());
        Assertions.assertEquals(new Version(1, 5, 3, 1), ver.getTarget());

        ver = new CompatibleVersion(new Version(1, 0, 1 ,1), new Version(2, 0, 2, 2));
        Assertions.assertEquals(new Version(1, 0, 1, 1), ver.getMinimum());
        Assertions.assertEquals(new Version(2, 0, 2, 2), ver.getTarget());

        ver = new CompatibleVersion(new Version(3, 0, 4, 5), new Version(2, 0, 3, 4));
        Assertions.assertEquals(new Version(2, 0, 3, 4), ver.getMinimum());
        Assertions.assertEquals(new Version(2, 0, 3, 4), ver.getTarget());
    }

    @Test
    public void isCompatibleWith() {
        Assertions.assertTrue(new CompatibleVersion(new Version(1, 0, 0, 0)).isCompatibleWith(new Version(1, 0, 0, 0)));
        Assertions.assertFalse(new CompatibleVersion(new Version(1, 0, 0, 0)).isCompatibleWith(new Version(2, 0, 0, 0)));

        Assertions.assertFalse(new CompatibleVersion(new Version(2, 1, 0, 0)).isCompatibleWith(new Version(2, 0, 0, 0)));
        Assertions.assertTrue(new CompatibleVersion(new Version(2, 1, 0, 0)).isCompatibleWith(new Version(2, 1, 1, 0)));

        Assertions.assertTrue(new CompatibleVersion(new Version(2, 1, 2, 2)).isCompatibleWith(new Version(2, 1, 2, 1)));
        Assertions.assertTrue(new CompatibleVersion(new Version(2, 1, 2, 0)).isCompatibleWith(new Version(2, 1, 3, 0)));

        Assertions.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(1, 1, 2, 0)));
        Assertions.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(2, 1, 1, 0)));
        Assertions.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(3, 1, 0, 0)));
        Assertions.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(3, 1, 10, 0)));
        Assertions.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(3, 1, 10, 11)));
    }
}
