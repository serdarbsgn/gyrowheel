package com.serdarbsgn.gyrowheel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MACAddressValidator {

    private static final Pattern MAC_ADDRESS_PATTERN =
            Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");

    public static boolean isValidMACAddress(String macAddress) {
        if (macAddress == null) {
            return false;
        }
        Matcher matcher = MAC_ADDRESS_PATTERN.matcher(macAddress);
        return matcher.matches();
    }
}
