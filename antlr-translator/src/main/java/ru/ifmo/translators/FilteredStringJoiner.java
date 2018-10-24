package ru.ifmo.translators;

import java.util.Objects;

public class FilteredStringJoiner {
    private final String prefix;
    private final String delimiter;
    private final String suffix;

    private StringBuilder value;

    private String emptyValue;


    public FilteredStringJoiner(CharSequence delimiter) {
        this(delimiter, "", "");
    }

    public FilteredStringJoiner(CharSequence delimiter,
                                CharSequence prefix,
                                CharSequence suffix) {
        Objects.requireNonNull(prefix, "The prefix must not be null");
        Objects.requireNonNull(delimiter, "The delimiter must not be null");
        Objects.requireNonNull(suffix, "The suffix must not be null");
        // make defensive copies of arguments
        this.prefix = prefix.toString();
        this.delimiter = delimiter.toString();
        this.suffix = suffix.toString();
        this.emptyValue = this.prefix + this.suffix;
    }

    @Override
    public String toString() {
        if (value == null) {
            return emptyValue;
        } else {
            if (suffix.equals("")) {
                return value.toString();
            } else {
                int initialLength = value.length();
                String result = value.append(suffix).toString();
                // reset value to pre-append initialLength
                value.setLength(initialLength);
                return result;
            }
        }
    }

    public FilteredStringJoiner add(CharSequence newElement) {
        if (newElement.length() > 0) prepareBuilder().append(newElement);
        return this;
    }

    private StringBuilder prepareBuilder() {
        if (value != null) {
            value.append(delimiter);
        } else {
            value = new StringBuilder().append(prefix);
        }
        return value;
    }

    public int length() {
        return (value != null ? value.length() + suffix.length() :
                emptyValue.length());
    }
}
