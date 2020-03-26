package com.github.czyzby.lml.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** "Artificial" mock-up tag wrapping around an already constructed actor.
 *
 * @author MJ */
public class MockLmlTag implements LmlTag {
    private final LmlTag parent;
    private final Actor actor;

    public MockLmlTag(final Actor actor, final LmlTag parent) {
        this.actor = actor;
        this.parent = parent;
    }

    @Override
    public boolean isParent() {
        return false;
    }

    @Override
    public boolean isChild() {
        return true;
    }

    @Override
    public boolean isMacro() {
        return false;
    }

    @Override
    public boolean isAttachable() {
        return false;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        throw new IllegalStateException("This is programatically created mock-up LML tag that cannot be attached.");
    }

    @Override
    public void handleDataBetweenTags(final CharSequence rawData) {
        throw new IllegalStateException("This is programatically created mock-up LML tag that cannot have children.");
    }

    @Override
    public Actor getActor() {
        return actor;
    }

    @Override
    public Object getManagedObject() {
        return actor;
    }

    @Override
    public LmlTag getParent() {
        return parent;
    }

    @Override
    public String getTagName() {
        return Strings.EMPTY_STRING;
    }

    @Override
    public Array<String> getAttributes() {
        return null;
    }

    @Override
    public ObjectMap<String, String> getNamedAttributes() {
        return null;
    }

    @Override
    public boolean hasAttribute(final String name) {
        return false;
    }

    @Override
    public String getAttribute(final String name) {
        return null;
    }

    @Override
    public void closeTag() {
    }

    @Override
    public void handleChild(final LmlTag childTag) {
        throw new IllegalStateException("This is programatically created mock-up LML tag that cannot have children.");
    }
}
