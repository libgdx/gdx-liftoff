package com.github.czyzby.lml.vis.parser.impl.tag.validator;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxSets;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractLmlTag;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.vis.parser.impl.tag.FormValidatorLmlTag;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.form.FormInputValidator;
import com.kotcrab.vis.ui.util.form.ValidatorWrapper;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/** Abstract base for {@link InputValidator} tags.
 *
 * @author MJ */
public abstract class AbstractValidatorLmlTag extends AbstractLmlTag {
    public AbstractValidatorLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        if (Strings.isNotBlank(rawData)) {
            getParser().throwErrorIfStrict("Validators cannot parse plain text between tags.");
        }
    }

    @Override
    public Actor getActor() {
        return null;
    }

    /** @return managed {@link InputValidator}. */
    @Override
    public Object getManagedObject() {
        return getInputValidator();
    }

    @Override
    public void closeTag() {
        if (getParent() == null) {
            getParser().throwError("Validators need to be attached to a tag. No parent found for tag: " + getTagName());
        }
    }

    @Override
    public void handleChild(final LmlTag childTag) {
        getParser().throwErrorIfStrict("Validators cannot have children.");
    }

    /** @return {@link InputValidator} supplied by this tag. Invoked once, when validator is attached to the actor. */
    public abstract InputValidator getInputValidator();

    @Override
    protected boolean supportsNamedAttributes() {
        return true;
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        final InputValidator validator = initiateValidator();
        doBeforeAttach(validator);
        if (tag.getActor() instanceof VisValidatableTextField) {
            ((VisValidatableTextField) tag.getActor()).addValidator(validator);
        } else {
            getParser().throwErrorIfStrict("Validators can be attached only to VisValidatableTextField actors.");
        }
    }

    /** Invoked before the validator is attached.
     *
     * @param validator has been initiated. */
    protected void doBeforeAttach(final InputValidator validator) {
    }

    private InputValidator initiateValidator() {
        final InputValidator validator = getInputValidator();
        if (isInForm() && !(validator instanceof FormInputValidator)) {
            return wrapValidator(validator);
        }
        processAttributes(validator);
        return validator;
    }

    private InputValidator wrapValidator(final InputValidator validator) {
        final ObjectSet<String> processedAttributes = GdxSets.newSet();
        processAttributes(validator, processedAttributes, false);
        // Processing form validator-specific attributes:
        final FormInputValidator formValidator = new ValidatorWrapper(Strings.EMPTY_STRING, validator);
        processAttributes(formValidator, processedAttributes, true);
        return formValidator;
    }

    /** @return true if the validator is attached to a widget in a form. */
    protected boolean isInForm() {
        LmlTag parent = getParent();
        while (parent != null) {
            if (parent instanceof FormValidatorLmlTag) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    // Utility named attribute processing methods:

    private void processAttributes(final InputValidator validator) {
        processAttributes(validator, null, true);
    }

    private void processAttributes(final InputValidator validator, final ObjectSet<String> processedAttributes,
            final boolean throwExceptionIfAttributeUnknown) {
        LmlUtilities.processAttributes(validator, this, getParser(), processedAttributes,
                throwExceptionIfAttributeUnknown);
    }
}
