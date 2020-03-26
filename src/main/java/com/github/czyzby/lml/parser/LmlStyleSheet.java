package com.github.czyzby.lml.parser;

import com.badlogic.gdx.utils.ObjectMap;

/** Contains default values of attribute in tags.
 *
 * @author MJ */
public interface LmlStyleSheet {
    /** @param tag name of the tag. Case might be ignored.
     * @param attribute name of the attribute. Case might be ignored.
     * @return default value for the chosen attribute or null if not set. */
    String getStyle(String tag, String attribute);

    /** @param tag name of the tag. Case might be ignored.
     * @return map of tag's styles or null if none set. */
    ObjectMap<String, String> getStyles(String tag);

    /** @param tag name of the tag. Case might be ignored.
     * @param attribute name of the attribute. Case might be ignored.
     * @param defaultValue will be the value of the chosen attribute in selected tags if it was not set already. */
    void addStyle(String tag, String attribute, String defaultValue);

    /** @param tag name of the tag. Case might be ignored.
     * @param styles map of default attribute values mapped by their attribute names. Will be added.
     * @see #addStyle(String, String, String) */
    void addStyles(String tag, ObjectMap<String, String> styles);

    /** @param tag name of the tag. Case might be ignored.
     * @param attribute name of the attribute. Case might be ignored. If any default value is assigned to the attribute,
     *            it will be removed. */
    void removeStyle(String tag, String attribute);

    /** @param tag name of the tag. Case might be ignored. All styles assigned to the tag will be removed. */
    void removeStyles(String tag);

    /** All stored styles will be removed. */
    void clearStyles();
}
