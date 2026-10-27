package com.studentmgmtsys.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/** Small server-side validation helpers used by the registration controller. */
public final class Validator {
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.-]{3,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\d{10}$");
    private static final Pattern NAME = Pattern.compile("^[\\p{L} .'-]{2,100}$");
    private static final Pattern DIGITS = Pattern.compile("^\\d{3,20}$");

    private Validator() { }

    public static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean username(String s) { return s != null && USERNAME.matcher(s).matches(); }
    public static boolean password(String s) { return s != null && s.length() >= 6 && s.length() <= 100; }
    public static boolean email(String s) { return s != null && s.length() <= 100 && EMAIL.matcher(s).matches(); }
    public static boolean phone(String s) { return s != null && PHONE.matcher(s).matches(); }
    public static boolean personName(String s) { return s != null && NAME.matcher(s.trim()).matches(); }
    public static boolean digits(String s) { return s != null && DIGITS.matcher(s).matches(); }

    public static boolean pastDate(String iso) {
        try {
            LocalDate d = LocalDate.parse(iso);
            return d.isBefore(LocalDate.now()) && d.isAfter(LocalDate.of(1950, 1, 1));
        } catch (Exception e) {
            return false;
        }
    }

    /** Parses an int and returns Integer.MIN_VALUE when the text is not a number. */
    public static int toInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return Integer.MIN_VALUE;
        }
    }
}
