package net.frozenbit.plugin5zig.cubecraft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a software version with a major, minor and patch part (example: 3.7.10).
 */
// note: if you refactor this class, the gradle task generateSources might break
public class Version implements Comparable<Version> {
    private static final Pattern VERSION_PATTERN =
            Pattern.compile("^v?(\\d+)(\\.(\\d+))?(\\.(\\d+))?$");
    public final int major;
    public final int minor;
    public final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version fromString(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Cannot parse version string '" + version + "'");
        }

        int major = Integer.parseInt(matcher.group(1)),
                minor = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3)),
                patch = matcher.group(5) == null ? 0 : Integer.parseInt(matcher.group(5));
        return new Version(major, minor, patch);
    }

    @Override
    public int compareTo(Version o) {
        int cmp = Integer.compare(major, o.major);
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(minor, o.minor);
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(patch, o.patch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        return major == version.major && minor == version.minor && patch == version.patch;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        return result;
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}
