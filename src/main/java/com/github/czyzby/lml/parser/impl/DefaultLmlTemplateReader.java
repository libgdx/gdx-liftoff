package com.github.czyzby.lml.parser.impl;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;
import com.github.czyzby.lml.parser.LmlTemplateReader;

/** Standard template reader, working on plain char sequences (which mostly comes down to simple strings). Does not do
 * buffered reading of template files which will be fine for most templates. However, if you do have enormous files to
 * read or if this implementation causes significant performance penalties, consider using a custom template reader.
 *
 * @author MJ */
public class DefaultLmlTemplateReader implements LmlTemplateReader {
    protected final PooledList<CharSequenceEntry> sequencesQueue = new PooledList<CharSequenceEntry>();

    /** First sequence appended to the reader since the time it was empty. Basically, any time the reader is empty and
     * gets a sequence, it will be set as the original one to determine current line number. */
    protected CharSequenceEntry originalSequence;
    protected CharSequenceEntry currentSequence;

    @Override
    public void append(final char[] template) {
        if (template != null && template.length > 0) {
            appendSequence(new String(template), null);
        }
    }

    @Override
    public void append(final String template) {
        appendSequence(template, null);
    }

    @Override
    public void append(final String template, final String templateName) {
        appendSequence(template, templateName);
    }

    @Override
    public void append(final FileHandle templateFile) {
        appendSequence(templateFile.readString("UTF-8"), templateFile.name());
    }

    @Override
    public void append(final CharSequence template) {
        appendSequence(template, null);
    }

    @Override
    public void append(final CharSequence template, final String templateName) {
        appendSequence(template, templateName);
    }

    /** Actual appending method, referenced by all others.
     *
     * @param sequence should be appended to the reader and set as currently parsed sequence.
     * @param name name of the template for debugging purposes. */
    protected void appendSequence(final CharSequence sequence, final String name) {
        if (Strings.isNotEmpty(sequence)) {
            queueCurrentSequence();
            setCurrentSequence(new CharSequenceEntry(sequence, name));
        }
    }

    /** Adds current sequence with its character index to the queue. */
    protected void queueCurrentSequence() {
        if (currentSequence != null) {
            sequencesQueue.addFirst(currentSequence);
        }
    }

    /** @param sequence becomes currently parsed sequence. */
    protected void setCurrentSequence(final CharSequenceEntry sequence) {
        currentSequence = sequence;
        if (sequence == null) {
            originalSequence = null; // There are no more parsed sequences, so original is irrelevant.
        } else if (originalSequence == null) {
            // This is the first sequence during this parsing.
            originalSequence = sequence;
        }
    }

    @Override
    public boolean hasNextCharacter() {
        return currentSequence != null && currentSequence.hasNext() || isAnyQueuedSequenceNonEmpty();
    }

    /** @return true if any sequence on the queue is not empty. */
    protected boolean isAnyQueuedSequenceNonEmpty() {
        if (sequencesQueue.isEmpty()) {
            return false;
        }
        for (final CharSequenceEntry sequence : sequencesQueue) {
            if (sequence.hasNext()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public char nextCharacter() {
        while (currentSequence.isEmpty()) {
            getSequenceFromQueue();
        }
        final char character = currentSequence.nextChar();
        if (character == '\n') {
            currentSequence.incrementLine();
        }
        return character;
    }

    @Override
    public char peekCharacter() {
        return currentSequence.currentChar();
    }

    /** Dequeues last sequence, if any. */
    protected void getSequenceFromQueue() {
        setCurrentSequence(sequencesQueue.removeFirst());
    }

    @Override
    public boolean hasNextCharacter(final int additionalIndexes) {
        if (currentSequence == null) {
            return false;
        }
        if (currentSequence.charsLeft() > additionalIndexes) {
            // There are enough characters in the current sequence alone.
            return true;
        }
        if (sequencesQueue.isEmpty()) {
            // There aren't enough characters in the current sequence and there are no sequences left.
            return false;
        }
        int indexesLeft = additionalIndexes - currentSequence.charsLeft();
        for (final CharSequenceEntry sequence : sequencesQueue) {
            indexesLeft -= sequence.charsLeft();
            if (indexesLeft < 0) {
                // Stored sequences had enough characters.
                return true;
            }
        }
        return false;
    }

    @Override
    public char peekCharacter(final int additionalIndexes) {
        if (currentSequence.charsLeft() > additionalIndexes) {
            return currentSequence.peekChar(additionalIndexes);
        }
        int indexesLeft = additionalIndexes - currentSequence.charsLeft();
        for (final CharSequenceEntry sequence : sequencesQueue) {
            final int charactersLeft = sequence.charsLeft();
            if (charactersLeft > indexesLeft) {
                return sequence.peekChar(indexesLeft);
            }
            indexesLeft -= charactersLeft;
        }
        throw new IllegalStateException(
                "Not enough characters left to peek value with: " + additionalIndexes + " additional indexes.");
    }

    @Override
    public boolean startsWith(final CharSequence value) {
        for (int index = 0, length = value.length(); index < length; index++) {
            if (peekCharacter(index) != value.charAt(index)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCurrentLine() {
        return originalSequence == null ? 0 : originalSequence.getLine();
    }

    @Override
    public int getCurrentSequenceLine() {
        return currentSequence == null ? 0 : currentSequence.getLine();
    }

    @Override
    public String getCurrentTemplateName() {
        return originalSequence == null ? null : originalSequence.getName();
    }

    @Override
    public String getCurrentSequenceName() {
        return currentSequence == null ? null : currentSequence.getName();
    }

    @Override
    public String getCurrentSequence() {
        return currentSequence == null ? null : currentSequence.sequence.toString();
    }

    @Override
    public String getOriginalSequence() {
        return originalSequence == null ? null : originalSequence.sequence.toString();
    }

    @Override
    public boolean isParsingOriginalTemplate() {
        return currentSequence == originalSequence && originalSequence != null;
    }

    @Override
    public void clear() {
        currentSequence = null;
        originalSequence = null;
        sequencesQueue.clear();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Currently parsing: ");
        appendDebugMessage(builder, originalSequence);
        // Slow, but this is for debug only.
        final Array<CharSequenceEntry> sequences = GdxArrays.newArray(sequencesQueue);
        for (int index = sequencesQueue.size() - 2; index >= 0; index--) {
            builder.append("\nWhich spawned:");
            appendDebugMessage(builder, sequences.get(index));
        }
        if (currentSequence != originalSequence) {
            builder.append("\nWhich spawned:");
            appendDebugMessage(builder, currentSequence);
        }
        return builder.toString();
    }

    private static void appendDebugMessage(final StringBuilder builder, final CharSequenceEntry sequence) {
        builder.append('\n').append(sequence.name).append(": line ").append(sequence.getLine())
                .append(" of template part: \n").append(sequence.sequence);
    }

    /** Data container for a single template part.
     *
     * @author MJ */
    // This is preferred to keeping two separate queues for char sequences and indexes, since A) linked list creates
    // nodes for its values anyway, so it's not like we're adding extra object allocation, B) there's no integer boxing,
    // C) working with objects is much easier than managing a bunch of indexes, lengths and sequences "by hand" in the
    // reader itself.
    protected static class CharSequenceEntry {
        private final CharSequence sequence;
        private final String name;
        private final int length;
        private int index;
        private int line = 1; // Counting lines from 1 is more natural to humans.

        public CharSequenceEntry(final CharSequence sequence, final String name) {
            this.sequence = sequence;
            this.name = name;
            length = sequence.length();
        }

        /** @return next character stored in sequence. Modifies iteration index. */
        public char nextChar() {
            return sequence.charAt(index++);
        }

        /** @return character currently pointed by the iteration index. */
        public char currentChar() {
            return sequence.charAt(index);
        }

        /** @param additionalIndexes temporarily modifies iteration index.
         * @return character at the selected position. */
        public char peekChar(final int additionalIndexes) {
            return sequence.charAt(index + additionalIndexes);
        }

        /** @return amount of unparsed characters. */
        public int charsLeft() {
            return length - index;
        }

        /** @return true if the sequence has any characters left. */
        public boolean hasNext() {
            return index < length;
        }

        /** @return true if has no characters left. */
        public boolean isEmpty() {
            return index == length;
        }

        /** Increments lines count. */
        public void incrementLine() {
            line++;
        }

        /** @return currently parsed line of the sequence. */
        public int getLine() {
            return line;
        }

        /** @return name of the template. */
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return sequence.toString();
        }
    }
}
