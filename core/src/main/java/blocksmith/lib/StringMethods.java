package blocksmith.lib;

import blocksmith.domain.value.ParamInput.MultilineText;
import java.util.List;
import blocksmith.domain.value.ParamInput.Password;
import blocksmith.infra.blockloader.annotations.Value;
import blocksmith.infra.blockloader.annotations.Block;
import java.util.Locale;

/**
 *
 * @author joostmeulenkamp
 */
public class StringMethods {

    @Block(
            type = "Input.password",
            aliases = {"String.password"},
            category = "Core",
            description = "Input a line of text. Warning: the value is not encrypted, only visually hidden.")
    public static String inputPassword(@Value(input = Password.class) String value) {
        return value.isEmpty() ? null : value;
    }

    @Block(
            type = "Input.text",
            aliases = {"String.newMultiline"},
            category = "Core",
            description = "Input text or observe output as text")
    public static String inputMultilineString(@Value(input = MultilineText.class) String text) {
        return inputString(text);
    }

    @Block(
            type = "Input.string",
            aliases = {"String.new"},
            category = "Core",
            description = "Input a line of text.")
    public static String inputString(@Value String string) {
        return string == null || string.isEmpty() ? null : string;
    }

    @Block(
            type = "String.length",
            category = "Core",
            description = "Returns the length of this string. The length is equal to the number of Unicode code units in the string.",
            tags = {"size"})
    public static Integer length(String string) {
        return string.length();
    }

    @Block(
            type = "String.toUpperCase",
            category = "Core",
            description = "Converts all of the characters in this String to upper case using the rules of the default locale.",
            name = "a > A")
    public static String toUpperCase(String string) {
        return string.toUpperCase();
    }

    @Block(
            type = "String.toLowerCase",
            category = "Core",
            description = "Converts all of the characters in this String to lower case using the rules of the default locale.",
            name = "A > a")
    public static String toLowerCase(String string) {
        return string.toLowerCase();
    }

    @Block(
            type = "String.stripLeading",
            category = "Core",
            description = "Returns a string whose value is this string, with all leading white space removed.",
            name = "_ strip")
    public static String stripLeading(String string) {
        return string.stripLeading();
    }

    @Block(
            type = "String.stripTrailing",
            category = "Core",
            description = "Returns a string whose value is this string, with all trailing white space removed.",
            name = "strip _")
    public static String stripTrailing(String string) {
        return string.stripTrailing();
    }

    @Block(
            type = "String.strip",
            category = "Core",
            description = "Returns a string whose value is this string, with all leading and trailing white space removed.",
            name = "_ strip _")
    public static String strip(String string) {
        return string.strip();
    }

    @Block(
            type = "String.substring",
            category = "Core",
            description = "Returns a string that is a substring of this string. The substring begins at the specified beginIndex and extends to the character at index endIndex - 1. Thus the length of the substring is endIndex-beginIndex.",
            name = "a > A")
    public static String substring(String string, Integer beginIndex, Integer endIndex) throws StringIndexOutOfBoundsException {
        beginIndex = beginIndex == null ? 0 : beginIndex;
        if (endIndex == null) {
            return string.substring(beginIndex);
        }
        return string.substring(beginIndex, endIndex);
    }

    @Block(
            type = "String.contains",
            category = "Core",
            description = "Returns true if and only if this string contains the specified sequence of char values.",
            tags = {})
    public static Boolean contains(String string, String sequence) {
        return string.contains(sequence);
    }

    @Block(
            type = "String.endsWith",
            category = "Core",
            description = "Tests if this string ends with the specified suffix.",
            tags = {})
    public static Boolean endsWith(String string, String suffix) {
        return string.endsWith(suffix);
    }

    @Block(
            type = "String.startsWith",
            category = "Core",
            description = "Tests if the substring of this string beginning at the specified index starts with the specified prefix. By default the offset is set to zero.",
            tags = {})
    public static Boolean startWith(String string, String prefix, Integer offset) {
        offset = offset == null ? 0 : offset;
        return string.startsWith(prefix, offset);
    }

    @Block(
            type = "String.equals",
            category = "Core",
            description = "Compares this string to the specified object. The result is true if and only if the argument is not null and is a String object that represents the same sequence of characters as this object.")
    public static Boolean equals(String a, String another) {
        return a.equals(another);
    }

    @Block(
            type = "String.equalsIgnoreCase",
            category = "Core",
            description = "Compares this String to another String, ignoring case considerations. Two strings are considered equal ignoring case if they are of the same length and corresponding Unicode code points in the two strings are equal ignoring case.")
    public static boolean equalsIgnoreCase(String a, String another) {
        return a.equalsIgnoreCase(another);
    }

    @Block(
            type = "String.indexOf",
            category = "Core",
            description = "Returns the index within this string of the first occurrence of the specified substring.")
    public static Integer indexOf(String string, String str) {
        return string.indexOf(str);
    }

    @Block(
            type = "String.lastIndexOf",
            category = "Core",
            description = "Returns the index within this string of the last occurrence of the specified substring.")
    public static Integer lastIndexOf(String string, String str) {
        return string.lastIndexOf(str);
    }

    @Block(
            type = "String.replace",
            category = "Core",
            description = "Replaces each substring of this string that matches the literal target sequence with the specified literal replacement sequence. The replacement proceeds from the beginning of the string to the end, for example, replacing \"aa\" with \"b\" in the string \"aaa\" will result in \"ba\" rather than \"ab\".")
    public static String replace(String string, String target, String replacement) {
        replacement = replacement == null ? "" : replacement;
        return string.replace(target, replacement);
    }

    @Block(
            type = "String.replaceAll",
            category = "Core",
            description = "Replaces each substring of this string that matches the given regular expression with the given replacement.")
    public static String replaceAll(String string, String regex, String replacement) {
        replacement = replacement == null ? "" : replacement;
        return string.replaceAll(regex, replacement);
    }

    @Block(
            type = "String.replaceFirst",
            category = "Core",
            description = "Replaces the first substring of this string that matches the given regular expression with the given replacement.")
    public static String replaceFirst(String string, String regex, String replacement) {
        replacement = replacement == null ? "" : replacement;
        return string.replaceFirst(regex, replacement);
    }

    @Block(
            type = "String.concatList",
            category = "Core",
            description = "Concatenates a list of string values into a single string.")
    public static String concatList(List<String> values) {
        return concat(values.stream().toArray(String[]::new));
    }

    @Block(
            type = "String.concat",
            category = "Core",
            description = "Concatenates multiple string values into a single string.")
    public static String concat(String... value) {
        if (value.length == 0) {
            return null;
        }
        String result = "";
        for (String v : value) {
            result += v;
        }
        return result;
    }

    @Block(
            type = "String.joinList",
            category = "Core",
            description = "Returns a new String composed of the values joined together with the specified delimiter.")
    public static String joinList(String delimiter, List<String> values) {
        return String.join(delimiter, values.stream().toArray(String[]::new));
    }

    @Block(
            type = "String.join",
            category = "Core",
            description = "Returns a new String composed of the values joined together with the specified delimiter.")
    public static String join(String delimiter, String... value) {
        return String.join(delimiter, value);
    }

    @Block(
            type = "String.matches",
            category = "Core",
            description = "Tells whether or not this string matches the given regular expression.")
    public static Boolean matches(String value, String regex) {
        return value.matches(regex);
    }

    @Block(
            type = "String.split",
            category = "Core",
            description = "Splits this string around matches of the given regular expression.")
    public static List<String> split(String value, String regex) {
        return List.of(value.split(regex));
    }

    @Block(
            type = "String.isBlank",
            category = "Core",
            description = "Returns true if the string is empty or contains only white space codepoints, otherwise false.")
    public static Boolean isBlank(String value) {
        return value.isBlank();
    }

    @Block(
            type = "String.isEmpty",
            category = "Core",
            description = "Returns true if, and only if, length() is 0.")
    public static Boolean isEmpty(String value) {
        return value.isEmpty();
    }

    @Block(
            type = "String.format",
            category = "Core",
            description = "Returns a formatted string using the specified format string and arguments. The letter after % determines the type: s for strings, d for integers, f for floats, b for booleans. %n inserts a platform-safe newline. A number sets minimum field width, - left-aligns, and . sets decimal precision. For example, \"Hello, %s! You are ~%d years old.\" with \"World\" and 6000 produces \"Hello, World! You are ~6000 years old.\".")
    public static String format(String format, Object... arg) {
        return String.format(format, arg);
    }

    @Block(
            type = "String.formatLocale",
            category = "Core",
            description = "Returns a formatted string using the specified locale, format string, and arguments. The letter after % determines the type: s for strings, d for integers, f for floats, b for booleans. %n inserts a platform-safe newline. A number sets minimum field width, - left-aligns, and . sets decimal precision. For example, \"Hello, %s! You are ~%d years old.\" with \"World\" and 6000 produces \"Hello, World! You are ~6000 years old.\".")
    public static String formatByLocale(Locale l, String format, Object... arg) {
        return String.format(l, format, arg);
    }

}
