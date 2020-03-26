package com.github.czyzby.lml.parser.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/** Common interface for LML tag handlers.
 *
 * @author MJ */
public interface LmlTag {
    /** @return true if the tag was not closed upon creation. For example, this is a parental tag: <blockquote>
     *
     *         <pre>
     * &lt;parent&gt;&lt;/parent&gt;
     *         </pre>
     *
     *         </blockquote> */
    boolean isParent();

    /** @return true if the tag was immediately closed upon creation. For example, this is a child tag: <blockquote>
     *
     *         <pre>
     * &lt;child/&gt;
     *         </pre>
     *
     *         </blockquote> */
    boolean isChild();

    /** @return true if tag name was proceeded with macro sign. */
    boolean isMacro();

    /** @return true if the actor or object managed by the tag can be attached to any widget, even if the widget does
     *         not accept any regular children. An example might be a tooltip, which can be attached to any actor. */
    boolean isAttachable();

    /** @param tag will attach to actor stored by this tag.
     * @throws IllegalStateException if tag is not attachable. */
    void attachTo(LmlTag tag);

    /** @param rawData unparsed LML data between tags. Contains new line characters and whitespaces that need to be
     *            trimmed. This object MIGHT (and probably will) be modified - if you want to append this data to
     *            current template reader, make sure to call {@link CharSequence#toString()} first. */
    void handleDataBetweenTags(CharSequence rawData);

    /** @return actor created by this tag. Might be null or might return its parent tag actor in cases where text and
     *         children should be handled by its parent. */
    Actor getActor();

    /** @return the actual object represented by this tag. In case of most actors, this method is practically an
     *         equivalent to {@link #getActor()}. Most macro tags return null. */
    Object getManagedObject();

    /** @return if this tag is nested in another parental tag, this will return the reference to the parent. Might be
     *         null. */
    LmlTag getParent();

    /** @return original name of the tag with which the tag was opened. Any markers are stripped. */
    String getTagName();

    /** @return array of unparsed tag attributes. If the tag has no attributes, this array might not be initiated and
     *         the method will return null. */
    Array<String> getAttributes();

    /** @return direct reference to attributes of the tag. If the tag has no attributes or does not support named
     *         attributes, this map might not be initiated and the method will return null. */
    ObjectMap<String, String> getNamedAttributes();

    /** Null-safe method that checks if the tag contains selected named attribute. Returns false even if named attribute
     * map is not initiated.
     *
     * @param name name of the attribute. Cannot be null.
     * @return true if this tag has the selected attribute. */
    boolean hasAttribute(String name);

    /** @param name name of the attribute. Cannot be null.
     * @return value assigned to the attribute. Note that this will not perform any checks and will return null if the
     *         attribute is absent. */
    String getAttribute(String name);

    /** Performs actions fired upon tag closing. */
    void closeTag();

    /** @param childTag is a fully initiated and closed widget that needs to be processed. */
    void handleChild(LmlTag childTag);
}
