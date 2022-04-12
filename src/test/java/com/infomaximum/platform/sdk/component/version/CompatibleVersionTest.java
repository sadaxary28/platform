package com.infomaximum.platform.sdk.component.version;

import org.junit.Assert;
import org.junit.Test;

public class CompatibleVersionTest {

    @Test
    public void constructor() {
        CompatibleVersion ver = new CompatibleVersion(new Version(1, 5, 3, 1));
        Assert.assertEquals(new Version(1, 5, 3,0), ver.getMinimum());
        Assert.assertEquals(new Version(1, 5, 3, 1), ver.getTarget());

        ver = new CompatibleVersion(new Version(1, 0, 1 ,1), new Version(2, 0, 2, 2));
        Assert.assertEquals(new Version(1, 0, 1, 1), ver.getMinimum());
        Assert.assertEquals(new Version(2, 0, 2, 2), ver.getTarget());

        ver = new CompatibleVersion(new Version(3, 0, 4, 5), new Version(2, 0, 3, 4));
        Assert.assertEquals(new Version(2, 0, 3, 4), ver.getMinimum());
        Assert.assertEquals(new Version(2, 0, 3, 4), ver.getTarget());
    }

    @Test
    public void isCompatibleWith() {
        Assert.assertTrue(new CompatibleVersion(new Version(1, 0, 0, 0)).isCompatibleWith(new Version(1, 0, 0, 0)));
        Assert.assertFalse(new CompatibleVersion(new Version(1, 0, 0, 0)).isCompatibleWith(new Version(2, 0, 0, 0)));

        Assert.assertFalse(new CompatibleVersion(new Version(2, 1, 0, 0)).isCompatibleWith(new Version(2, 0, 0, 0)));
        Assert.assertTrue(new CompatibleVersion(new Version(2, 1, 0, 0)).isCompatibleWith(new Version(2, 1, 1, 0)));

        Assert.assertTrue(new CompatibleVersion(new Version(2, 1, 2, 2)).isCompatibleWith(new Version(2, 1, 2, 1)));
        Assert.assertTrue(new CompatibleVersion(new Version(2, 1, 2, 0)).isCompatibleWith(new Version(2, 1, 3, 0)));

        Assert.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(1, 1, 2, 0)));
        Assert.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(2, 1, 1, 0)));
        Assert.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(3, 1, 0, 0)));
        Assert.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(3, 1, 10, 0)));
        Assert.assertTrue(new CompatibleVersion(new Version(1, 1, 2, 0), new Version(3, 1, 2, 0)).isCompatibleWith(new Version(3, 1, 10, 11)));
    }
}
