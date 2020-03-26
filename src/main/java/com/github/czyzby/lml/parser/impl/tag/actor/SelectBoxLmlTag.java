package com.github.czyzby.lml.parser.impl.tag.actor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Handles {@link SelectBox} actor. Like the List widget it wraps, select box can handle only string data. Converts
 * label and text button children to items by extracting their text; does not accept any other child tags. Appends plain
 * text lines between tags as its items. Mapped to "selectBox".
 *
 * @author MJ */
public class SelectBoxLmlTag extends AbstractActorLmlTag {
    public SelectBoxLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        return new SelectBox<String>(getSkin(builder), builder.getStyleName());
    }

    @Override
    protected void handlePlainTextLine(final String plainTextLine) {
        addChild(getParser().parseString(plainTextLine, getActor()));
    }

    /** @param child will be appended to the select box. */
    protected void addChild(final String child) {
        getSelectBox().getItems().add(child);
    }

    @SuppressWarnings("unchecked")
    private SelectBox<String> getSelectBox() {
        return (SelectBox<String>) getActor();
    }

    @Override
    protected void handleValidChild(final LmlTag childTag) {
        if (childTag.getActor() instanceof Label) {
            addChild(((Label) childTag.getActor()).getText().toString());
        } else if (childTag.getActor() instanceof TextButton) {
            addChild(((TextButton) childTag.getActor()).getText().toString());
        } else {
            getParser().throwErrorIfStrict(
                    "Select boxes can handle only text based children: Label and TextButton. Received child: "
                            + childTag.getTagName() + " with actor: " + childTag.getActor());
        }
    }

    @Override
    protected void doOnTagClose() {
        // Forcing items update:
        getSelectBox().setItems(GdxArrays.newArray(getSelectBox().getItems()));
    }

    @Override
    protected boolean hasComponentActors() {
        return true;
    }

    @Override
    protected Actor[] getComponentActors(final Actor actor) {
        @SuppressWarnings("unchecked") final SelectBox<String> selectBox = (SelectBox<String>) actor;
        return new Actor[] { selectBox.getList(), selectBox.getScrollPane() };
    }
}
