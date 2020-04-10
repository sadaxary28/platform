package com.infomaximum.subsystems.utils;

public class Version {

    public final int major;
    public final int minor;
    public final int patch;

    private final String toString;

    public Version(int major, int minor, int patch) {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException();
        }

        this.major = major;
        this.minor = minor;
        this.patch = patch;

        this.toString = major + "." + minor + "." + patch;
    }

    public static Version parse(String source) throws IllegalArgumentException {
        String[] parts = source.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Version string must be contains 3 parts: " + source);
        }

        return new Version(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }

    public static int compare(Version left, Version right) {
        if (left.major != right.major) {
            return Integer.compare(left.major, right.major);
        }
        if (left.minor != right.minor) {
            return Integer.compare(left.minor, right.minor);
        }
        return Integer.compare(left.patch, right.patch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;

        Version version = (Version) o;

        if (major != version.major) return false;
        if (minor != version.minor) return false;
        return patch == version.patch;
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
        return toString;
    }
}
