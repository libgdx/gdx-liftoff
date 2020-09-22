package com.github.czyzby.kiwi.util.common;

import java.util.Locale;

/** Utility class for {@link String} and {@link CharSequence} instances. The latter are sometimes expected or returned
 * by Scene2D API.
 *
 * @author MJ */
public class Strings extends UtilitiesClass {
    /** A string with length of 0, not null. */
    public static final String EMPTY_STRING = "";
    /** A {@link CharSequence} with length of 0, not null.
     *
     * @see Strings#EMPTY_STRING */
    public static final CharSequence EMPTY_CHAR_SEQUENCE = EMPTY_STRING;
    /** Common regex. Allows to determine if string contains no characters or only whitespaces. */
    public static final String WHITESPACE_REGEX = "\\s*";
    /** Common regex. Allows to split a sentence into trimmed words. */
    public static final String WHITESPACE_SPLITTER_REGEX = "\\s+";
    /** Empty immutable array of strings. Might be used as utility for methods returning empty arrays to avoid object
     * allocation. */
    public static final String[] EMPTY_ARRAY = new String[] {};
    /** If this value is returned by {@link String#indexOf(int)}, the character was not found. This value aims to reduce
     * the amount of magic numbers in string-handling methods.
     *
     * @see #isCharacterPresent(int)
     * @see #isCharacterAbsent(int) */
    public static final int CHARACTER_UNAVAILABLE = -1;

    private Strings() {
    }

    /** @param charSequence can be null.
     * @return true if passed char sequence is null or has no characters. */
    public static boolean isEmpty(final CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    /** @param charSequence can be null.
     * @return true if passed char sequence is not null and has at least one character. */
    public static boolean isNotEmpty(final CharSequence charSequence) {
        return charSequence != null && charSequence.length() > 0;
    }

    /** @param charSequence will be checked.
     * @return true if the passed sequence is null or contains only whitespace characters.
     * @see Strings#isWhitespace(CharSequence) */
    public static boolean isBlank(final CharSequence charSequence) {
        return isWhitespace(charSequence);
    }

    /** @param charSequence can be null.
     * @return true if the passed sequence is null or contains only whitespace characters. */
    public static boolean isWhitespace(final CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return true;
        }
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            final char character = charSequence.charAt(index);
            if (isNotWhitespace(character)) {
                return false;
            }
        }
        return true;
    }

    /** @param charSequence can contain whitespace characters.
     * @return the passed sequence without any whitespace characters. Never null; might be an empty string. */
    public static String stripWhitespaces(final CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return EMPTY_STRING;
        }
        final StringBuilder builder = new StringBuilder(charSequence.length());
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            final char character = charSequence.charAt(index);
            if (isNotWhitespace(character)) {
                builder.append(character);
            }
        }
        if (isEmpty(builder)) {
            return EMPTY_STRING;
        }
        return builder.toString();
    }

    /** @param resultOfIndexOf result of {@link String#indexOf(int)} or its overloaded methods.
     * @return true if the index is valid and the character was found in the string. */
    public static boolean isCharacterPresent(final int resultOfIndexOf) {
        return resultOfIndexOf > CHARACTER_UNAVAILABLE;
    }

    /** @param resultOfIndexOf result of {@link String#indexOf(int)} or its overloaded methods.
     * @return true is invalid and the character was not found in the string. */
    public static boolean isCharacterAbsent(final int resultOfIndexOf) {
        return resultOfIndexOf <= CHARACTER_UNAVAILABLE;
    }

    /** @param valueToModify will be modified internally. Can be null.
     * @param valueToReplace all occurrences will be replaced.
     * @param replacement will replace all occurrences of valueToReplace.
     * @return valueToModify with properly replaced occurrences of valueToReplace. */
    public static StringBuilder replace(final StringBuilder valueToModify, final char valueToReplace,
            final char replacement) {
        if (Strings.isEmpty(valueToModify)) {
            return valueToModify;
        }
        for (int index = 0, length = valueToModify.length(); index < length; index++) {
            if (valueToModify.charAt(index) == valueToReplace) {
                valueToModify.setCharAt(index, replacement);
            }
        }
        return valueToModify;
    }

    /** @param valueToModify will be modified internally. Can be null.
     * @param valueToReplace all occurrences will be replaced.
     * @param replacement will replace all occurrences of valueToReplace.
     * @return valueToModify with properly replaced occurrences of valueToReplace. */
    public static StringBuilder replace(final StringBuilder valueToModify, final char valueToReplace,
            final CharSequence replacement) {
        if (Strings.isEmpty(valueToModify)) {
            return valueToModify;
        } else if (replacement.length() == 1) {
            return replace(valueToModify, valueToReplace, replacement.charAt(0));
        }
        final CharSequence rest = replacement.subSequence(1, replacement.length());
        for (int index = 0, length = valueToModify.length(); index < length; index++) {
            if (valueToModify.charAt(index) == valueToReplace) {
                valueToModify.setCharAt(index, replacement.charAt(0));
                valueToModify.insert(index + 1, rest);
                length += rest.length();
            }
        }
        return valueToModify;
    }

    /** @param valueToModify will be modified internally. Can be null.
     * @param valueToReplace all occurrences will be replaced.
     * @param replacement will replace all occurrences of valueToReplace.
     * @return valueToModify with properly replaced occurrences of valueToReplace. */
    public static StringBuilder replace(final StringBuilder valueToModify, final CharSequence valueToReplace,
            final CharSequence replacement) {
        if (valueToReplace.length() == 1) {
            return replace(valueToModify, valueToReplace.charAt(0), replacement);
        } else if (Strings.isEmpty(valueToModify)) {
            return valueToModify;
        }
        final String replace = replacement.toString(); // String returns this.
        for (int index = 0, length = valueToModify.length(); index < length; index++) {
            if (contains(valueToModify, valueToReplace, index)) {
                valueToModify.replace(index, index + valueToReplace.length(), replace);
                index += replace.length();
            }
        }
        return valueToModify;
    }

    /** @param parent cannot be null. Might contain child sequence.
     * @param child cannot be null. Might be a part of parent.
     * @param at starting index at which child should be located in the parent.
     * @return true if parent contains child in the selected position. */
    public static boolean contains(final CharSequence parent, final CharSequence child, final int at) {
        if (at + child.length() > parent.length()) {
            return false;
        }
        for (int index = 0, length = child.length(); index < length; index++) {
            if (parent.charAt(at + index) != child.charAt(index)) {
                return false;
            }
        }
        return true;
    }

    /** @param parent can contain child. Can be null or empty.
     * @param child can be a part of the parent. Can be null or empty.
     * @return true if the parent char sequence contains the child (ignoring case). */
    public static boolean containsIgnoreCase(CharSequence parent, CharSequence child) {
        int parentLength;
        int childLength;
        if (Strings.isEmpty(parent) || Strings.isEmpty(child) ||
                (parentLength = parent.length()) < (childLength = child.length())) {
            return false;
        }
        int correctIndexes = 0;
        for (int index = 0; index < parentLength; index++) {
            if (compareIgnoreCase(parent.charAt(index), child.charAt(correctIndexes))) {
                if (++correctIndexes == childLength) {
                    return true;
                }
            } else {
                correctIndexes = 0;
            }
        }
        return false;
    }

    /** @param charSequence will be checked.
     * @return false if the passed sequence is null or contains only whitespace characters.
     * @see Strings#isNotWhitespace(CharSequence) */
    public static boolean isNotBlank(final CharSequence charSequence) {
        return isNotWhitespace(charSequence);
    }

    /** @param charSequence can be null.
     * @return true if the passed sequence is not null and contains at least one non-whitespace character. */
    public static boolean isNotWhitespace(final CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return false;
        }
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            final char character = charSequence.charAt(index);
            if (isNotWhitespace(character)) {
                return true;
            }
        }
        return false;
    }

    /** GWT utility. {@link Character#isWhitespace(char)} is not supported.
     *
     * @param character will be validated.
     * @return true if character is a whitespace. */
    public static boolean isWhitespace(final char character) {
        return character == ' ' || character == '\t' || character == '\n' || character == '\r' || character == '\f';
    }

    /** GWT utility. {@link Character#isWhitespace(char)} is not supported.
     *
     * @param character will be validated.
     * @return true if character is not a whitespace. */
    public static boolean isNotWhitespace(final char character) {
        return character != ' ' && character != '\t' && character != '\n' && character != '\r' && character != '\f';
    }

    /** @param character will be validated.
     * @return true if passed character is a new line char. */
    public static boolean isNewLine(final char character) {
        return character == '\n' || character == '\r';
    }

    /** @param character will be validated.
     * @return true if passed character is not a new line char. */
    public static boolean isNotNewLine(final char character) {
        return character != '\n' && character != '\r';
    }

    /** @param charSequence will be validated. Can be null.
     * @param length required length.
     * @return true if passed sequence is not null and its length is lower than passed value. */
    public static boolean isShortherThan(final CharSequence charSequence, final int length) {
        return charSequence != null && charSequence.length() < length;
    }

    /** @param charSequence will be validated. Can be null.
     * @param length required length.
     * @return true if passed sequence is not null and its length is higher than passed value. */
    public static boolean isLongerThan(final CharSequence charSequence, final int length) {
        return charSequence != null && charSequence.length() > length;
    }

    /** @param charSequence can be null.
     * @param character will be validated.
     * @return true if the passed sequence starts with the given character. */
    public static boolean startsWith(final CharSequence charSequence, final char character) {
        return charSequence != null && charSequence.length() > 0 && charSequence.charAt(0) == character;
    }

    /** @param charSequence can be null.
     * @param character0 will be validated.
     * @param character1 will be validated.
     * @return true if the passed sequence starts with the given characters. */
    public static boolean startsWith(final CharSequence charSequence, final char character0, final char character1) {
        return charSequence != null && charSequence.length() > 1 && charSequence.charAt(0) == character0
                && charSequence.charAt(1) == character1;
    }

    /** @param charSequence can be null.
     * @param character0 will be validated.
     * @param character1 will be validated.
     * @param character2 will be validated.
     * @return true if the passed sequence starts with the given characters. */
    public static boolean startsWith(final CharSequence charSequence, final char character0, final char character1,
            final char character2) {
        return charSequence != null && charSequence.length() > 2 && charSequence.charAt(0) == character0
                && charSequence.charAt(1) == character1 && charSequence.charAt(2) == character2;
    }

    /** @param charSequence can be null.
     * @param character0 will be validated.
     * @param character1 will be validated.
     * @param character2 will be validated.
     * @param character3 will be validated.
     * @return true if the passed sequence starts with the given characters. */
    public static boolean startsWith(final CharSequence charSequence, final char character0, final char character1,
            final char character2, final char character3) {
        return charSequence != null && charSequence.length() > 3 && charSequence.charAt(0) == character0
                && charSequence.charAt(1) == character1 && charSequence.charAt(2) == character2
                && charSequence.charAt(3) == character3;
    }

    /** @param charSequence can be null.
     * @param characters will be validated.
     * @return true if the passed sequence starts with the given characters. */
    public static boolean startsWith(final CharSequence charSequence, final char... characters) {
        if (charSequence == null) {
            return false;
        }
        if (charSequence.length() >= characters.length) {
            for (int index = 0; index < characters.length; index++) {
                if (charSequence.charAt(index) != characters[index]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /** @param charSequence can be null.
     * @param character will be validated.
     * @return true if the passed sequence ends with the given character. */
    public static boolean endsWith(final CharSequence charSequence, final char character) {
        return charSequence != null && charSequence.length() > 0
                && charSequence.charAt(charSequence.length() - 1) == character;
    }

    /** @param charSequence can be null.
     * @param character0 will be validated.
     * @param character1 will be validated.
     * @return true if the passed sequence ends with the given characters. */
    public static boolean endsWith(final CharSequence charSequence, final char character0, final char character1) {
        return charSequence != null && charSequence.length() > 1
                && charSequence.charAt(charSequence.length() - 1) == character1
                && charSequence.charAt(charSequence.length() - 2) == character0;
    }

    /** @param charSequence can be null.
     * @param character0 will be validated.
     * @param character1 will be validated.
     * @param character2 will be validated.
     * @return true if the passed sequence ends with the given characters. */
    public static boolean endsWith(final CharSequence charSequence, final char character0, final char character1,
            final char character2) {
        return charSequence != null && charSequence.length() > 2
                && charSequence.charAt(charSequence.length() - 1) == character2
                && charSequence.charAt(charSequence.length() - 2) == character1
                && charSequence.charAt(charSequence.length() - 3) == character0;
    }

    /** @param charSequence can be null.
     * @param character0 will be validated.
     * @param character1 will be validated.
     * @param character2 will be validated.
     * @param character3 will be validated.
     * @return true if the passed sequence ends with the given characters. */
    public static boolean endsWith(final CharSequence charSequence, final char character0, final char character1,
            final char character2, final char character3) {
        return charSequence != null && charSequence.length() > 3
                && charSequence.charAt(charSequence.length() - 1) == character3
                && charSequence.charAt(charSequence.length() - 2) == character2
                && charSequence.charAt(charSequence.length() - 3) == character1
                && charSequence.charAt(charSequence.length() - 4) == character0;
    }

    /** @param charSequence can be null.
     * @param characters will be validated.
     * @return true if the passed sequence ends with the given characters. */
    public static boolean endsWith(final CharSequence charSequence, final char... characters) {
        if (charSequence == null) {
            return false;
        }
        if (charSequence.length() >= characters.length) {
            for (int index = 0, modifier = charSequence.length()
                    - characters.length; index < characters.length; index++) {
                if (charSequence.charAt(index + modifier) != characters[index]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /** @param charSequence cannot be null.
     * @return last character index in the passed sequence. */
    public static int getLastIndex(final CharSequence charSequence) {
        return charSequence.length() - 1;
    }

    /** @param charSequence can be null.
     * @param character will be validated.
     * @return true if the given sequence contains selected character. */
    public static boolean contains(final CharSequence charSequence, final char character) {
        if (isEmpty(charSequence)) {
            return false;
        }
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            if (charSequence.charAt(index) == character) {
                return true;
            }
        }
        return false;
    }

    /** @param stringBuilder will have its length set as 0. Cannot be null. */
    public static void clearBuilder(final com.badlogic.gdx.utils.StringBuilder stringBuilder) {
        stringBuilder.setLength(0);
    }

    /** @param stringBuilder will have its length set as 0. Cannot be null.
     * @return value previously stored in the builder. */
    public static String getAndClear(final com.badlogic.gdx.utils.StringBuilder stringBuilder) {
        final String value = stringBuilder.toString();
        clearBuilder(stringBuilder);
        return value;
    }

    /** @param stringBuilder will have its length set as 0. Cannot be null. */
    public static void clearBuilder(final StringBuilder stringBuilder) {
        stringBuilder.setLength(0);
    }

    /** @param stringBuilder will have its length set as 0. Cannot be null.
     * @return value previously stored in the builder. */
    public static String getAndClear(final StringBuilder stringBuilder) {
        final String value = stringBuilder.toString();
        clearBuilder(stringBuilder);
        return value;
    }

    /** As opposed to string's split, this method allows to split a char sequence without a regex, provided that the
     * separator is a single character. Since it does not require pattern compiling and is a simple iteration, this
     * method should be always preferred when it can be used.
     *
     * @param charSequence will be split. Can be null.
     * @param separator character that will be used to split the sequence.
     * @return array of strings. Is never null - if an empty or null sequence is passed, empty array will be
     *         returned. */
    public static String[] split(final CharSequence charSequence, final char separator) {
        if (isEmpty(charSequence)) {
            return EMPTY_ARRAY;
        }
        final int originalSeparatorsCount = countSeparatedCharAppearances(charSequence, separator);
        int separatorsCount = originalSeparatorsCount;
        // If the sequence starts or ends with the separator (and its not the same char index), we don't need as many
        // characters.
        if (startsWith(charSequence, separator)) {
            separatorsCount--;
        }
        if (charSequence.length() > 1 && endsWith(charSequence, separator)) {
            // Length has to be at least 2, as we want to check two different chars and subtract count only once.
            separatorsCount--;
        }
        if (separatorsCount <= 0) {
            if (originalSeparatorsCount == 0) {
                // No separators at all.
                return new String[] { charSequence.toString() };
            } else if (isSameChar(charSequence)) {
                // We've confirmed that the sequence contains a separator and consists of only 1 type of character. It
                // means that the whole string is made of separators, so we're returning empty array.
                return EMPTY_ARRAY;
            }
            // No separators inside the sequence, but found on edges. Returning the whole string, possibly with stripped
            // separators.
            return new String[] { removeCharacter(charSequence.toString(), separator) };
        }
        final String[] result = new String[separatorsCount + 1];
        int currentResultIndex = 0;
        final StringBuilder builder = new StringBuilder();
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            final char character = charSequence.charAt(index);
            if (character == separator) {
                if (isNotEmpty(builder)) {
                    result[currentResultIndex++] = builder.toString();
                    clearBuilder(builder);
                }
            } else {
                builder.append(character);
            }
        }
        if (isNotEmpty(builder)) {
            // The whole thing may not end with a separator, so we're appending whatever we got left.
            result[currentResultIndex++] = builder.toString();
        }
        return result;
    }

    /** @param charSequence can contain the separators. Can be null.
     * @param separator will be searched for in the sequence.
     * @return sequence separated into parts between separators. As opposed to {@link #split(CharSequence, char)}, this
     *         method does not merge multiple separators next to each other - instead, it will add empty strings to the
     *         result. This can be useful to separate files by new lines to preserve line order, for example. */
    public static String[] separate(final CharSequence charSequence, final char separator) {
        if (isEmpty(charSequence)) {
            return EMPTY_ARRAY;
        }
        final int separatorsCount = countCharAppearances(charSequence, separator);
        if (separatorsCount == 0) {
            // No separators at all.
            return new String[] { charSequence.toString() };
        }
        final String[] result = new String[separatorsCount + 1];
        int currentResultIndex = 0;
        final StringBuilder builder = new StringBuilder();
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            final char character = charSequence.charAt(index);
            if (character == separator) {
                result[currentResultIndex++] = builder.toString();
                clearBuilder(builder);
            } else {
                builder.append(character);
            }
        }
        result[currentResultIndex++] = builder.toString();
        return result;
    }

    /** @param firstChar first character to compare.
     * @param secondChar second character to compare.
     * @return true if characters are equal, ignoring case. */
    public static boolean compareIgnoreCase(final char firstChar, final char secondChar) {
        return Character.toLowerCase(firstChar) == Character.toLowerCase(secondChar);
    }

    /** @param firstCharSequence first sequence to compare.
     * @param secondCharSequence second sequence to compare.
     * @return true if characters represented by the sequences are equal, ignoring case. */
    public static boolean compareIgnoreCase(final CharSequence firstCharSequence,
            final CharSequence secondCharSequence) {
        if (firstCharSequence == secondCharSequence) {
            return true;
        }
        if (isEmpty(firstCharSequence)) {
            return isEmpty(secondCharSequence);
        } else if (isEmpty(secondCharSequence)) {
            return false;
        }
        if (firstCharSequence.length() != secondCharSequence.length()) {
            return false;
        }
        for (int index = 0, length = firstCharSequence.length(); index < length; index++) {
            if (!compareIgnoreCase(firstCharSequence.charAt(index), secondCharSequence.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    /** @param charSequence might contain undesired character.
     * @param characterToRemove all appearances of this character will be removed.
     * @return a string which does not contain the passed character. */
    public static String stripCharacter(final CharSequence charSequence, final char characterToRemove) {
        if (contains(charSequence, characterToRemove)) {
            return removeCharacter(charSequence, characterToRemove);
        }
        return charSequence.toString();
    }

    private static String removeCharacter(final CharSequence charSequence, final char characterToRemove) {
        final StringBuilder builder = new StringBuilder(charSequence.length());
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            final char character = charSequence.charAt(index);
            if (character != characterToRemove) {
                builder.append(character);
            }
        }
        return builder.toString();
    }

    /** @param charSequence may consist of a single repeated character.
     * @return true if all characters in the sequence are equal or the sequence is empty or null. */
    public static boolean isSameChar(final CharSequence charSequence) {
        if (Strings.isEmpty(charSequence)) {
            return true;
        }
        int index = 0;
        final char character = charSequence.charAt(index++);
        for (final int length = charSequence.length(); index < length; index++) {
            if (charSequence.charAt(index) != character) {
                return false;
            }
        }
        return true;
    }

    /** @param charSequence may contain the passed character. Can be null.
     * @param value character to look for.
     * @return amount of appearances of the selected character in the passed sequence. */
    public static int countCharAppearances(final CharSequence charSequence, final char value) {
        if (isEmpty(charSequence)) {
            return 0;
        }
        int count = 0;
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            if (charSequence.charAt(index) == value) {
                count++;
            }
        }
        return count;
    }

    /** @param charSequence may contain the passed character. Might be null.
     * @param value character to look for.
     * @return amount of appearances of the selected character in the passed sequence. If multiple characters with the
     *         same value are neighbors to each other, only one of them is counted. This method might be useful for
     *         splitting strings by characters rather than regexes. */
    public static int countSeparatedCharAppearances(final CharSequence charSequence, final char value) {
        if (isEmpty(charSequence)) {
            return 0;
        }
        int count = 0;
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            if (charSequence.charAt(index) == value && (index == 0 || charSequence.charAt(index - 1) != value)) {
                count++;
            }
        }
        return count;
    }

    /** @param separator will be used to separate joined strings from each other. Passing null or empty string will
     *            result in merging strings without any separation.
     * @param objectsToJoin will be converted to strings and joined using the selected separator. Nulls are converted
     *            into "null" strings.
     * @return passed objects as strings joined into one object. Note that this string will never be null - if
     *         objectsToJoin are empty, an empty string is returned. */
    public static String join(final CharSequence separator, final Object... objectsToJoin) {
        if (objectsToJoin == null || objectsToJoin.length == 0) {
            return EMPTY_STRING;
        }
        if (objectsToJoin.length == 1) {
            // Avoiding unnecessary operations.
            return objectsToJoin[0] == null ? Nullables.DEFAULT_NULL_STRING : objectsToJoin[0].toString();
        }

        final StringBuilder stringBuilder = new StringBuilder();
        if (isEmpty(separator)) {
            // No separator - merging strings.
            for (final Object element : objectsToJoin) {
                stringBuilder.append(element);
            }
            return stringBuilder.toString();
        }
        // A separator is selected - joining strings with selected separator.
        int index = 0;
        stringBuilder.append(objectsToJoin[index++]);
        for (; index < objectsToJoin.length; index++) {
            stringBuilder.append(separator);
            stringBuilder.append(objectsToJoin[index]);
        }
        return stringBuilder.toString();
    }

    /** @param separator will be used to separate joined strings from each other. Passing null or empty string will
     *            result in merging strings without any separation.
     * @param objectsToJoin will be converted to strings and joined using the selected separator. Nulls are converted
     *            into "null" strings.
     * @return passed objects as strings joined into one object. Note that this string will never be null - if
     *         objectsToJoin are empty, an empty string is returned. */
    public static String join(final CharSequence separator, final Iterable<?> objectsToJoin) {
        if (objectsToJoin == null) {
            return EMPTY_STRING;
        }
        final StringBuilder stringBuilder = new StringBuilder();
        if (isEmpty(separator)) {
            // No separator - merging strings.
            for (final Object element : objectsToJoin) {
                stringBuilder.append(element);
            }
            return stringBuilder.toString();
        }
        // A separator is selected - joining strings with selected separator.
        int index = 0;
        for (final Object object : objectsToJoin) {
            if (index++ > 0) {
                stringBuilder.append(separator);
            }
            stringBuilder.append(object);
        }
        return stringBuilder.toString();
    }

    /** @param objectsToMerge will be converted and merged into one string, without using any separator. Note that nulls
     *            will be added as "null" strings.
     * @return merged objects as strings. Is never null - for empty objectsToMerge, returns empty string. Equivalent to
     *         using join method with null or empty separator. */
    public static String merge(final Object... objectsToMerge) {
        return join(null, objectsToMerge);
    }

    /** @param separator will be used to separate joined strings from each other. Passing null or empty string will
     *            result in merging strings without any separation.
     * @param objectsToJoin will be converted to strings and joined using the selected separator. Nulls are completely
     *            ignored - they are not added to the string and do not invoke adding separators.
     * @return passed objects joined as strings into one object. Note that this string will never be null - if
     *         objectsToJoin are empty, an empty string is returned. */
    public static String joinIgnoringNulls(final CharSequence separator, final Object... objectsToJoin) {
        if (objectsToJoin == null || objectsToJoin.length == 0) {
            return EMPTY_STRING;
        }
        if (objectsToJoin.length == 1) {
            // Avoiding unnecessary operations.
            return objectsToJoin[0] == null ? EMPTY_STRING : objectsToJoin[0].toString();
        }

        final StringBuilder stringBuilder = new StringBuilder();
        if (isEmpty(separator)) {
            // No separator - merging strings.
            for (final Object element : objectsToJoin) {
                if (element != null) {
                    stringBuilder.append(element);
                }
            }
            return stringBuilder.toString();
        }
        // A separator is selected - joining strings with selected separator.
        int index = 0;
        for (; index < objectsToJoin.length; index++) {
            if (objectsToJoin[index] != null) {
                stringBuilder.append(objectsToJoin[index]);
                break;
            }
        }
        for (; index < objectsToJoin.length; index++) {
            if (objectsToJoin[index] == null) {
                continue;
            }
            stringBuilder.append(separator);
            stringBuilder.append(objectsToJoin[index]);
        }
        return stringBuilder.toString();
    }

    /** @param objectsToMerge will be converted and merged into one string, without using any separator. Note that nulls
     *            will not be added at all.
     * @return merged objects as strings. Is never null - for empty objectsToMerge, returns empty string. Equivalent to
     *         using joinIgnoringNulls method with null or empty separator. */
    public static String mergeIgnoringNulls(final Object... objectsToMerge) {
        return joinIgnoringNulls(null, objectsToMerge);
    }

    /** @param nullable can be null.
     * @return nullable object converted to string. If parameter is null, empty string is returned. As long as toString
     *         is properly implemented in the object, this method never returns null. */
    public static String toString(final Object nullable) {
        return nullable == null ? EMPTY_STRING : nullable.toString();
    }

    /** @param nullable can be null.
     * @param onNull will be returned if nullable is null.
     * @return nullable object converted to string. If first parameter is null, onNull parameter is returned. */
    public static String toString(final Object nullable, final String onNull) {
        return nullable == null ? onNull : nullable.toString();
    }

    /** @param charSequence can be null.
     * @return true if the passed charSequence contains legal characters for an int. Note that the value of int is not
     *         validated and can be too big or small. */
    public static boolean isInt(final CharSequence charSequence) {
        if (isEmpty(charSequence)) {
            return false;
        }
        int index = 0;
        if (charSequence.charAt(0) == '-') {
            if (charSequence.length() > 1) {
                index++;
            } else {
                return false;
            }
        }
        for (final int length = charSequence.length(); index < length; index++) {
            final char character = charSequence.charAt(index);
            if (character < '0' || character > '9') {
                return false;
            }
        }
        return true;
    }

    /** @param charSequence can be null.
     * @return true if the passed charSequence contains legal characters for a float. Note that the value of float is
     *         not validated and can be too big or small. */
    public static boolean isFloat(final CharSequence charSequence) {
        if (Strings.isEmpty(charSequence)) {
            return false;
        }
        boolean foundDot = false;
        for (int index = 0, length = charSequence.length(); index < length; index++) {
            final char character = charSequence.charAt(index);
            if (character < '0' || character > '9') {
                if (length > 1) {
                    if (index == 0 && character == '-') {
                        continue;
                    } else if (!foundDot && character == '.') {
                        foundDot = true;
                        continue;
                    } else if ((character == 'f' || character == 'F') && index + 1 == length) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    /** @param value can be null.
     * @return true only if the value matches "true" or "equals" ignoring case. If the value has at least one whitespace
     *         char, false will be returned. This check should be generally cheaper than
     *         {@link String#equalsIgnoreCase(String)}, as it does not iterate over chars and works directly on char
     *         sequence methods. */
    public static boolean isBoolean(final CharSequence value) {
        if (value == null) {
            return false;
        }
        if (value.length() == 4) {
            final char t = value.charAt(0);
            final char r = value.charAt(1);
            final char u = value.charAt(2);
            final char e = value.charAt(3);
            return (t == 't' || t == 'T') && (r == 'r' || r == 'R') && (u == 'u' || u == 'U') && (e == 'e' || e == 'E');
        } else if (value.length() == 5) {
            final char f = value.charAt(0);
            final char a = value.charAt(1);
            final char l = value.charAt(2);
            final char s = value.charAt(3);
            final char e = value.charAt(4);
            return (f == 'f' || f == 'F') && (a == 'a' || a == 'A') && (l == 'l' || l == 'L') && (s == 's' || s == 'S')
                    && (e == 'e' || e == 'E');
        }
        return false;
    }

    /** @param values can be null.
     * @return a new array with the passed values. Can be empty, but is never null. */
    public static String[] newArray(final String... values) {
        if (values == null) {
            return EMPTY_ARRAY;
        }
        return values;
    }

    /** Null-safe compare of characters stored in the sequences. This method does not invoke {@link #equals(Object)}
     * methods and allows to compare different {@link CharSequence} implementations.
     *
     * @param first first value to check.
     * @param second second value to check.
     * @return true if both values are null or if stored characters are equal to each other. */
    public static boolean equals(final CharSequence first, final CharSequence second) {
        if (first == null) {
            return second == null;
        } else if (first == second) {
            return true;
        } else if (first.length() != second.length()) {
            return false;
        }
        for (int index = 0, length = first.length(); index < length; index++) {
            if (first.charAt(index) != second.charAt(index)) {
                return false;
            }
        }
        return true;
    }

    /** @param string cannot be null.
     * @return passed string converted to lower case with the root locale. */
    public static String toLowerCase(String string) {
        return string.toLowerCase(Locale.ROOT);
    }

    /** @param string cannot be null.
     * @return passed string converted to upper case with the root locale. */
    public static String toUpperCase(String string) {
        return string.toUpperCase(Locale.ROOT);
    }
}