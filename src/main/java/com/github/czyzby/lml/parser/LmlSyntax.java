package com.github.czyzby.lml.parser;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

/** Contains markers and signs parsed using in LML templates and parsed by LML parsers.
 *
 * @author MJ
 *
 * @see com.github.czyzby.lml.parser.impl.DefaultLmlSyntax */
public interface LmlSyntax {
    /** @return character that opens LML tag, starting a new entity. Defaults to '{@literal <}'d. For example:
     *         <blockquote>
     *
     *         <pre>
     * &lt;tag&gt;
     *         </pre>
     *
     *         </blockquote> */
    char getTagOpening();

    /** @return character that closes LML tag, ending an entity. Defaults to '{@literal >}'. For example: <blockquote>
     *
     *         <pre>
     * &lt;tag&gt;
     *         </pre>
     *
     *         </blockquote> */
    char getTagClosing();

    /** @return character that indicates that the tag is closed. Defaults to '/'. If appears at the end of the tag, the
     *         entity is considered a "child". If appears at the beginning of a tag, the tag - along with its unclosed
     *         pair tag - is considered "parental". For example: <blockquote>
     *
     *         <pre>
     *         &lt;parent&gt;
     *             &lt;child/&gt;
     *             This line is processed by parent.
     *             &lt;nestedParent&gt; This text will be processed
     *                 by nested parent. &lt;/nestedParent&gt;
     *         &lt;/parent&gt;
     *         </pre>
     *
     *         </blockquote> In this example, child widget and nestedParent widget will be appended to parent widget
     *         (which decides how he manages them; if parent was a Table, for example, it might just add them as their
     *         cells). Parental entities decide how plain text and other tags between them are parsed. The "nearest"
     *         parent in the hierarchy parses the text and other widgets. */
    char getClosedTagMarker();

    /** @return character that follows tag opening and indicated that this tag is a comment. Defaults to '!'. For
     *         example: <blockquote>
     *
     *         <pre>
     * &lt;! This is a comment -&gt;
     * &lt;!-- This is also a comment --&gt;
     *         </pre>
     *
     *         </blockquote> */
    char getCommentOpening();

    /** @return character that follows tag opening and proceeds tag closing, indicating that the tag is a schema
     *         comment. Defaults to '?'. This might be useful if to keep your templates as valid XML files and you want
     *         to specify encoding, version (and so on) with a valid XML schema comment. Most parsers will treat allow
     *         to mix both schema comment and regular comment openings and closings. For example: <blockquote>
     *
     *         <pre>
     * &lt;?xml version="1.0" encoding="UTF-8" ?&gt;
     *         </pre>
     *
     *         </blockquote> */
    char getSchemaCommentMarker();

    /** @return character that proceeds tag closing and indicated that this tag was a comment and now should be ended.
     *         Defaults to '-'. For example: <blockquote>
     *
     *         <pre>
     * &lt;! This is a comment -&gt;
     * &lt;!-- This is also a comment --&gt;
     *         </pre>
     *
     *         </blockquote> */
    char getCommentClosing();

    /** @return value that begins DTD schema comment. Defaults to "DOCTYPE". When this value is detected at the
     *         beginning of a comment (started with !), parser treats it differently. For example: <blockquote>
     *
     *         <pre>
     * &lt;!DOCTYPE table SYSTEM "lml.dtd"&gt;
     *         </pre>
     *
     *         </blockquote> */
    String getDocumentTypeOpening();

    /** @return character that indicates that the following text is an ID of LML parser argument. Defaults to '{'. For
     *         example: <blockquote>
     *
     *         <pre>
     *         { name }
     *         </pre>
     *
     *         </blockquote> This will look for argument mapped to "name" key, convert it to string and replace the
     *         argument entity in the template with the string value. Note that whitespaces are optional and trimmed. */
    char getArgumentOpening();

    /** @return character that indicates that the following text is an ID of LML parser argument. Defaults to '}'. For
     *         example: <blockquote>
     *
     *         <pre>
     *         { name }
     *         </pre>
     *
     *         </blockquote> This will look for argument mapped to "name" key, convert it to string and replace the
     *         argument entity in the template with the string value. Note that whitespaces are optional and trimmed. */
    char getArgumentClosing();

    /** @return character following tag opening that indicates that this tag should be parsed as a macro, not an actor.
     *         Defaults to ':'. For example:<blockquote>
     *
     *         <pre>
     * &lt;:macro&gt;...&lt;/:macro&gt;
     *         </pre>
     *
     *         </blockquote> */
    char getMacroMarker();

    /** @return character that separates ID of a container from ID of a value stored in the container. This applies to
     *         preferences, bundles, etc. Defaults to '.'. For example: <blockquote>
     *
     *         <pre>
     * &#064;bundleKey.textKey
     * #preferencesKey.settingKey
     *         </pre>
     *
     *         </blockquote> The first line will look for i18n bundle mapped to "bundleKey" and retrieve value mapped to
     *         "textKey". The second line will look for preferences mapped to "peferencesKey" and retrieve current
     *         preference mapped to "settingKey". */
    char getIdSeparatorMarker();

    /** @return character that indicates that the following text is a key of a preference and should be replaced by the
     *         preference value converted to string. Defaults to '#'. For example: <blockquote>
     *
     *         <pre>
     * &lt;label&gt;#volume&lt;/label&gt;
     * &lt;checkBox value=#soundPreferences.soundOn /&gt;
     *         </pre>
     *
     *         </blockquote> The first value will look for default preferences (mapped to default ID) and look for
     *         "volume" preference. The second value will look for preferences mapped to "soundPreferences" key and look
     *         for "soundOn" preference. */
    char getPreferenceMarker();

    /** @return character that indicates that the following text is a key of a i18n bundle line and should be replaced
     *         by the formatted bundle key. Defaults to '&#064;'. For example: <blockquote>
     *
     *         <pre>
     * &lt;label&gt;&#064;line&lt;/label&gt;
     * &lt;label&gt;&#064;bundle.line&lt;/label&gt;
     *         </pre>
     *
     *         </blockquote> First label's text will be value mapped to "line" in the default i18n bundle. The second
     *         will match value mapped to "line" in a bundle mapped to "bundle" key.
     * @see #getBundleLineArgumentMarker() */
    char getBundleLineMarker();

    /** @return character inside a bundle text that separates bundle line key from its arguments. Defaults to '|'.
     *         Multiple markers can be used to pass multiple arguments to the line. Each argument is parsed as any other
     *         value, so can be a separate bundle line (without arguments!), preference or method invocation. For
     *         example: <blockquote>
     *
     *         <pre>
     * &#064;line|MJ|9
     * &#064;bundle.line|&#064;otherLine|#somePreference
     *         </pre>
     *
     *         </blockquote>The first line will look for the default bundle and format value mapped to "line" key with
     *         "MJ" and "9" arguments. The second line will look for bundle mapped to "bundle" key and format value
     *         mapped to "line" key with formatted "otherLine" bundle line from default bundle and value of
     *         "somePreference" from default preferences.
     * @see #getBundleLineMarker() */
    char getBundleLineArgumentMarker();

    /** @return character that separates tag attribute names from their values. Defaults to '='. For example:
     *         <blockquote>
     *
     *         <pre>
     * &lt;tag attribute="value"/&gt;
     *         </pre>
     *
     *         </blockquote> Note parsers, by default, consider quotations as optional and spaces have to be escaped
     *         with a backslash in tags. */
    char getAttributeSeparator();

    /** @return character that indicates that the following text is an ID of a method that should be invoked and its
     *         result will should be returned. Defaults to '$'. For example: <blockquote>
     *
     *         <pre>
     * &lt;label&gt;$someMethod&lt;label/&gt;
     *         </pre>
     *
     *         </blockquote>Label's text will be the result of LML action mapped with "someMethod" (which might actually
     *         be a method named "someMethod"). Note that attributes expecting ONLY methods - like, for example,
     *         attributes that create listeners - do not need to be proceeded with this marker. */
    char getMethodInvocationMarker();

    /** @return character that separates elements in an array type. Some tags (like iteration macros) expect an array of
     *         arguments and this character allows to split the string into separate array elements. Defaults to ';'.
     *         For example: <blockquote>
     *
     *         <pre>
     * &lt;:forEach elem=val1;val2;val3&gt; &lt;label&gt;{elem}&lt;/label&gt; &lt;/:forEach&gt;.
     *         </pre>
     *
     *         </blockquote> This macro will iterate over array consisting of "val1", "val2" and "val3", assigning each
     *         element to "elem" argument and parsing its content. As a result, it will produce three labels with their
     *         texts matching, as you might guess, "val1", "val2" and "val3". */
    char getArrayElementSeparator();

    /** @return character that starts range array. Defaults to '['. Range array is useful for printing multiple values
     *         that vary only by an index number that they end with. This might not seem so useful at first, but it
     *         might be used in conjunction with i18n bundles for long texts. For example: <blockquote>
     *
     *         <pre>
     * &lt;:forEach elem=line[0,2]&gt; &lt;label&gt;@{elem}&lt;/label&gt; &lt;/:forEach&gt;.
     *         </pre>
     *
     *         </blockquote> This macro will iterate over array consisting of "line0", "line1" and "val2", assigning
     *         each element to "elem" argument and parsing its content. As a result, it will produce three labels with
     *         their texts formatted with default i18n bundle, matching "line0", "line1" and "line2" bundle line
     *         keys. */
    char getRangeArrayOpening();

    /** @return character that separates ranges' starting and ending values. Defaults to ','. Range array is useful for
     *         printing multiple values that vary only by an index number that they end with. This might not seem so
     *         useful at first, but it might be used in conjunction with i18n bundles for long texts. For example:
     *         <blockquote>
     *
     *         <pre>
     * &lt;:forEach elem=line[0,2]&gt; &lt;label&gt;@{elem}&lt;/label&gt; &lt;/:forEach&gt;.
     *         </pre>
     *
     *         </blockquote> This macro will iterate over array consisting of "line0", "line1" and "val2", assigning
     *         each element to "elem" argument and parsing its content. As a result, it will produce three labels with
     *         their texts formatted with default i18n bundle, matching "line0", "line1" and "line2" bundle line
     *         keys. */
    char getRangeArraySeparator();

    /** @return character that ends range array. Defaults to ']'. Range array is useful for printing multiple values
     *         that vary only by an index number that they end with. This might not seem so useful at first, but it
     *         might be used in conjunction with i18n bundles for long texts. For example: <blockquote>
     *
     *         <pre>
     * &lt;:forEach elem=line[0,2]&gt; &lt;label&gt;@{elem}&lt;/label&gt; &lt;/:forEach&gt;.
     *         </pre>
     *
     *         </blockquote> This macro will iterate over array consisting of "line0", "line1" and "val2", assigning
     *         each element to "elem" argument and parsing its content. As a result, it will produce three labels with
     *         their texts formatted with default i18n bundle, matching "line0", "line1" and "line2" bundle line
     *         keys. */
    char getRangeArrayClosing();

    /** @return character that marks that selected block should be evaluated as an equation rather than regular
     *         argument. Defaults to '='. Changes the way argument is parsed. For example:<blockquote>
     *
     *         <pre>
     * {=4+5}
     *         </pre>
     *
     *         </blockquote> Rather than looking for argument named "=4+5", this block will evaluate to "9". Equations
     *         can also handle preferences, actions and bundle lines. For example: <blockquote>
     *
     *         <pre>
     * # Given .properties i18n bundle file with line:
     * player=Player
     *
     * # ...this equation:
     * {={@literal @}player + 1}
     *
     * # ...evaluates to:
     * Player1
     *         </pre>
     *
     *         </blockquote>See {@link com.github.czyzby.lml.parser.impl.tag.macro.CalculationLmlMacroTag calculation
     *         macro docs} for more informations about equations. */
    char getEquationMarker();

    /** @return character that marks that selected block should be evaluated as condition (similar to ternary operator)
     *         rather than regular argument. Defaults to '?'. Changes the way argument is parsed. For example:
     *         <blockquote>
     *
     *         <pre>
     * {?4&gt;6 ? onTrue : onFalse}
     *         </pre>
     *
     *         </blockquote>Rather than looking for argument named "?(4&gt;6) onTrue : onFalse", this argument would
     *         evaluate condition (4 is lower than 6 - false), and print "onFalse". See
     *         {@link com.github.czyzby.lml.parser.impl.tag.macro.CalculationLmlMacroTag calculation macro docs} for
     *         more informations about equations. */
    char getConditionMarker();

    /** @return character that separates true and false values in conditions. Defaults to ':'.
     * @see #getConditionMarker() */
    char getTernaryMarker();

    /** @return current LML style sheet files syntax. */
    LssSyntax getLssSyntax();

    /** @param tagName name of the tag as it appears in LML template.
     * @return provider of tags associated with the selected name. Might be null, if tag was not registered.
     * @see #getMacroTagProvider(String) */
    LmlTagProvider getTagProvider(String tagName);

    /** @param provider provides tag wrappers for selected tag names.
     * @param supportedTagNames name of the tags supported by the provider as they can appear in LML templates. */
    void addTagProvider(LmlTagProvider provider, String... supportedTagNames);

    /** @param tagName tag wrapper provider registered with this name will be removed. */
    void removeTagProvider(String tagName);

    /** @param tagName name of the macro tag with stripped marker as it appears in LML template.
     * @return provider of macro tags associated with the selected name. Might be null, if tag was not registered.
     * @see #getTagProvider(String) */
    LmlTagProvider getMacroTagProvider(String tagName);

    /** @param provider provides macro tag wrappers for selected tag names.
     * @param supportedTagNames name of the macro tags with stripped markers supported by the provider as they can
     *            appear in LML templates. */
    void addMacroTagProvider(LmlTagProvider provider, String... supportedTagNames);

    /** @param tagName macro tag wrapper provider registered with this name will be removed. */
    void removeMacroTagProvider(String tagName);

    /** @param forActor actor that needs to parse an attribute. Cannot be null.
     * @param attributeName name of the attribute to parse.
     * @return attribute processor connected with the selected attribute name and actor's class or null.
     * @param <Actor> type of actor accepted by the processor. */
    <Actor> LmlAttribute<Actor> getAttributeProcessor(Actor forActor, String attributeName);

    /** @param forActorType type of actor that needs to parse an attribute. Cannot be null.
     * @param attributeName name of the attribute to parse.
     * @return attribute processor connected with the selected attribute name and actor's class or null.
     * @param <Actor> type of actor accepted by the processor. */
    <Actor> LmlAttribute<Actor> getAttributeProcessor(Class<Actor> forActorType, String attributeName);

    /** @param attributeProcessor will be registered under passed names for the handled actor class type.
     * @param names names under which the attribute should be registered.
     * @param <Actor> type of actor accepted by the processor. */
    <Actor> void addAttributeProcessor(LmlAttribute<Actor> attributeProcessor, String... names);

    /** @param name attribute processor registered with this name will be removed.
     * @param handledActorType removed attribute processor must handle this exact base actor type. */
    void removeAttributeProcessor(String name, Class<?> handledActorType);

    /** @param builder will be used by the attribute processor. Cannot be null.
     * @param attributeName name of the attribute to parse.
     * @return attribute processor connected with the selected attribute name and actor's class or null.
     * @param <Builder> type of handled builder. */
    <Builder extends LmlActorBuilder> LmlBuildingAttribute<Builder> getBuildingAttributeProcessor(Builder builder,
                                                                                                  String attributeName);

    /** @param buildingAttributeProcessor will be registered under passed names for the handled actor class type.
     * @param names names under which the attribute should be registered.
     * @param <Builder> type of handled builder. */
    <Builder extends LmlActorBuilder> void addBuildingAttributeProcessor(
            LmlBuildingAttribute<Builder> buildingAttributeProcessor, String... names);

    /** @param name building attribute processor registered with this name will be removed.
     * @param handledActorType removed building attribute processor must handle this exact base actor type. */
    void removeBuildingAttributeProcessor(String name, Class<?> handledActorType);

    /** @param actor an instance of LML actor.
     * @return all attributes that can be applied to this actor. This is a debug method. */
    ObjectMap<String, LmlAttribute<?>> getAttributesForActor(Object actor);

    /** @param builder builder of a LML actor.
     * @return all attributes that can be applied to this builder. This is a debug method. */
    ObjectMap<String, LmlBuildingAttribute<?>> getAttributesForBuilder(LmlActorBuilder builder);

    /** @return all actor tags available in this syntax. Should not be modified manually. Might return a copy of tags
     *         instead of the internal collection. This is a debug method. */
    ObjectMap<String, LmlTagProvider> getTags();

    /** @return all macro tags available in this syntax. Should not be modified manually. Might return a copy of tags
     *         instead of the internal collection. This is a debug method. */
    ObjectMap<String, LmlTagProvider> getMacroTags();
}
