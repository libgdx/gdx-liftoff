package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link List} actor. Converts label and text button children to items by extracting their text; does not
 * accept any other child tags. Appends plain text lines between tags as its list items. Mapped to "list".
 *
 * @author MJ */
public class ListLmlTag extends AbstractActorLmlTag {
    public ListLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new List<String>(getSkin(builder), builder.getStyleName());
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        if (childTag.getActor() instanceof Label) {
            addListElement(((Label) childTag.getActor()).getText().toString());
        } else if (childTag.getActor() instanceof TextButton) {
            addListElement(((TextButton) childTag.getActor()).getText().toString());
        } else {
            getParser().throwErrorIfStrict(
                    "Lists can handle only text based children: Label and TextButton. Received child: "
                            + childTag.getTagName() + " with actor: " + childTag.getActor());
        }
    }

    /** @param element will be added to the list. */
    protected void addListElement(final String element) {
        getList().getItems().add(element);
    }

    /** @return casted actor. */
    @SuppressWarnings("unchecked")
    protected List<String> getList() {
        return (List<String>) getActor();
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        addListElement(getParser().parseString(plainTextLine, getActor()));
    }

    @Override
    protected void doOnTagClose() {
        // Forcing items update.
        getList().invalidateHierarchy();
    }
}
