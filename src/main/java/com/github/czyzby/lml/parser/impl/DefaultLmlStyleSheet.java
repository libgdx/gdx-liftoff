package com.github.czyzby.lml.parser.impl;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.lml.parser.LmlStyleSheet;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Default implementation of {@link LmlStyleSheet}. Ignores case of tags and attributes.
 *
 * @author MJ */
public class DefaultLmlStyleSheet implements LmlStyleSheet {
    private final ObjectMap<String, ObjectMap<String, String>> styleSheet = new IgnoreCaseStringMap<ObjectMap<String, String>>();

    @Override
    public String getStyle(final String tag, final String attribute) {
        if (styleSheet.containsKey(tag)) {
            return styleSheet.get(tag).get(attribute);
        }
        return null;
    }

    @Override
    public ObjectMap<String, String> getStyles(final String tag) {
        return styleSheet.get(tag);
    }

    @Override
    public void addStyle(final String tag, final String attribute, final String defaultValue) {
        getTagStylesMap(tag).put(attribute, defaultValue);
    }

    /** @param tag name of the tag. Case ignored.
     * @return non-null map storing styles of the tag. */
    protected ObjectMap<String, String> getTagStylesMap(final String tag) {
        ObjectMap<String, String> styles = styleSheet.get(tag);
        if (styles == null) {
            styles = new IgnoreCaseStringMap<String>();
            styleSheet.put(tag, styles);
        }
        return styles;
    }

    @Override
    public void addStyles(final String tag, final ObjectMap<String, String> styles) {
        if (GdxMaps.isNotEmpty(styles)) {
            getTagStylesMap(tag).putAll(styles);
        }
    }

    @Override
    public void removeStyle(final String tag, final String attribute) {
        if (styleSheet.containsKey(tag)) {
            styleSheet.get(tag).remove(attribute);
        }
    }

    @Override
    public void removeStyles(final String tag) {
        styleSheet.remove(tag);
    }

    @Override
    public void clearStyles() {
        styleSheet.clear();
    }
}
