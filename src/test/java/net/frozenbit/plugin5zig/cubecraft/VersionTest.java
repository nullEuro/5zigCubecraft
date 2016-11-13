package net.frozenbit.plugin5zig.cubecraft;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class VersionTest {
    @Test
    public void testFromString() throws Exception {
        assertEquals("version parsed from string", new Version(15, 2, 7),
                Version.fromString("15.2.7"));

        assertEquals("version parsed from string with prefix", new Version(15, 2, 7),
                Version.fromString("v15.2.7"));
    }

    @Test
    public void compareTo() throws Exception {
        assertEquals(0, new Version(1, 2, 3).compareTo(new Version(1, 2, 3)));
        assertTrue(new Version(1, 2, 3).compareTo(new Version(2, 0, 5)) < 0);
        assertTrue(new Version(1, 2, 3).compareTo(new Version(1, 3, 0)) < 0);
        assertTrue(new Version(1, 2, 3).compareTo(new Version(1, 2, 4)) < 0);
        assertTrue(new Version(2, 3, 4).compareTo(new Version(1, 6, 8)) > 0);
        assertTrue(new Version(2, 3, 4).compareTo(new Version(2, 0, 8)) > 0);
        assertTrue(new Version(2, 3, 4).compareTo(new Version(2, 3, 3)) > 0);
    }
}