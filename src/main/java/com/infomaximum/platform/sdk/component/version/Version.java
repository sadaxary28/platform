package com.infomaximum.platform.sdk.component.version;

import java.util.Objects;

public class Version {

    public final int product;
    public final int major;
    public final int minor;
    public final int patch;

    public Version(int product, int major, int minor, int patch) {
        if (product < 0 || major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException();
        }

        this.product = product;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version parse(String source) throws IllegalArgumentException {
        String[] parts = source.split("\\.");
        if (parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException("Version string must be contains 3 or 4 parts: " + source);
        }

        if (parts.length == 3) {
            return new Version(
                    0,
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        } else {
            return new Version(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );
        }
    }

    public static int compare(Version left, Version right) {
        if (left.product != right.product) {
            return Integer.compare(left.major, right.major);
        }
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
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return product == version.product &&
                major == version.major &&
                minor == version.minor &&
                patch == version.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, major, minor, patch);
    }

    @Override
    public String toString() {
        return product + "." + major + "." + minor + "." + patch;
    }
}
