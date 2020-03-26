package com.github.czyzby.lml.vis.parser.impl.attribute.spinner;

import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

/**
 * See {@link Spinner#setTextFieldEventPolicy(Spinner.TextFieldEventPolicy)}. Expects a name of
 * {@link Spinner.TextFieldEventPolicy} enum instance. Mapped to "textFieldEventPolicy".
 * @author MJ
 */
public class SpinnerTextFieldEventPolicyLmlAttribute implements LmlAttribute<Spinner> {
    @Override
    public Class<Spinner> getHandledType() {
        return Spinner.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, Spinner spinner, String rawAttributeData) {
        spinner.setTextFieldEventPolicy(determinePolicy(parser, spinner, rawAttributeData));
    }

    /**
     * @param parser parses the tag.
     * @param actor requires text field event policy..
     * @param rawAttributeData unparsed attribute's value.
     * @return chosen {@link FileChooser.FileSorting}. Throws exception if ID not valid and parser is strict; returns
     * default value if parser not strict.
     */
    protected Spinner.TextFieldEventPolicy determinePolicy(final LmlParser parser, final Spinner actor,
            final String rawAttributeData) {
        final String modeName = Strings.toUpperCase(parser.parseString(rawAttributeData, actor).trim());
        try {
            return Spinner.TextFieldEventPolicy.valueOf(modeName);
        } catch (final Exception exception) {
            // Unknown ID.
            parser.throwErrorIfStrict("Unable to determine text field event policy with data: " + rawAttributeData,
                    exception);
        }
        return Spinner.TextFieldEventPolicy.ON_FOCUS_LOST;
    }
}
