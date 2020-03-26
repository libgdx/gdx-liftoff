package com.github.czyzby.lml.parser;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Common interface for all LML parsers. Provides methods allowing to configure template parsing. Note that
 * implementations are consider NOT thread-safe and templates should be either handled by multiple parsers or one by
 * one.
 *
 * @author MJ
 * @see com.github.czyzby.lml.parser.impl.DefaultLmlParser */
public interface LmlParser {
    /** @param lmlData contains references to skins, bundles and preferences needed to parse LML templates. Cannot be
     *            null. */
    void setData(LmlData lmlData);

    /** @return data container with references to skins, bundles and preferences needed to parse LML templates. */
    LmlData getData();

    /** @param templateReader will become parser's reader, used to process raw text data. Cannot be null. */
    void setTemplateReader(LmlTemplateReader templateReader);

    /** @return direct access to template reader used by the LML parser. Templates should not be appended directly to
     *         the reader, especially during templates parsing - this object is mostly for internal parser's use. */
    LmlTemplateReader getTemplateReader();

    /** @param lmlSyntax will become current LML syntax, determining how templates are parsed. Cannot be null. */
    void setSyntax(LmlSyntax lmlSyntax);

    /** @return current used template syntax. Can be safely modified most of the time, but generally should be left
     *         alone if template parsing is currently in progress. */
    LmlSyntax getSyntax();

    /** @param styleSheet will be used to provide default values for tags' attributes. */
    void setStyleSheet(LmlStyleSheet styleSheet);

    /** @return style sheet currently used to provide default values for tags' attributes. Can safely modified. */
    LmlStyleSheet getStyleSheet();

    /** @return true if parser is strict and throws errors for unknown tags, attributes, etc. */
    boolean isStrict();

    /** @param strict if true, parser throws errors for unknown tags, attributes, etc. Set to false for more HTML-like
     *            feel of everything generally working even if something is terribly wrong. */
    void setStrict(boolean strict);

    /** @param nestedComments if true, comments are nested and NEED to be valid. In HTML, comments are not nested, so
     *            commenting-out a whole file full of other comments is problematic; this is also the default behavior
     *            of LML, since this way comments you don't need to validate all your tags (for example, you might open
     *            a comment tag by mistake inside another comment). However, if you're sure you'll keep for comments
     *            clean and need a way to quickly comment-out huge portions of templates, set this value to true. */
    void setNestedComments(boolean nestedComments);

    /** @param lmlTemplate will be parsed.
     * @return parsed root actors, in the order that they appear in the template. */
    Array<Actor> parseTemplate(String lmlTemplate);

    /** @param lmlTemplateFile will be read and parsed.
     * @return parsed root actors, in the order that they appear in the template. */
    Array<Actor> parseTemplate(FileHandle lmlTemplateFile);

    /** @param styleSheet LML style sheet code. Will be processed.
     * @see #getStyleSheet() */
    void parseStyleSheet(String styleSheet);

    /** @param styleSheetFile path to a file storing LML style sheet code. Will be processed.
     * @see #getStyleSheet() */
    void parseStyleSheet(FileHandle styleSheetFile);

    /** @param stage will have the parsed actors appended.
     * @param lmlTemplate will be parsed. Actors parsed from the template will be added directly into the stage. */
    void fillStage(Stage stage, String lmlTemplate);

    /** @param stage will have the parsed actors appended.
     * @param lmlTemplateFile will be read and parsed. Actors parsed from the template will be added directly into the
     *            stage. */
    void fillStage(Stage stage, FileHandle lmlTemplateFile);

    /** @param view an instance of view object, containing annotated fields and methods that need to be filled and
     *            invoked. See LML annotations for more data.
     * @param lmlTemplate will be parsed.
     * @return array of actors parsed from the template.
     * @see LmlView
     * @param <View> class of the view to be initiated. */
    <View> Array<Actor> createView(View view, String lmlTemplate);

    /** @param view an instance of view object, containing annotated fields and methods that need to be filled and
     *            invoked. See LML annotations for more data.
     * @param lmlTemplateFile will be read and parsed.
     * @return array of actors parsed from the template.
     * @see LmlView
     * @param <View> class of the view to be initiated. */
    <View> Array<Actor> createView(View view, FileHandle lmlTemplateFile);

    /** @param viewClass class of the view, containing annotated fields and methods that need to be filled and invoked.
     *            See LML annotations for more data.
     * @param lmlTemplate will be parsed.
     * @return a new instance of the view, created with no-argument constructor and filled.
     * @see LmlView
     * @param <View> class of the view to be initiated. */
    <View> View createView(Class<View> viewClass, String lmlTemplate);

    /** @param viewClass class of the view, containing annotated fields and methods that need to be filled and invoked.
     *            See LML annotations for more data.
     * @param lmlTemplateFile will be read and parsed.
     * @return a new instance of the view, created with no-argument constructor and filled.
     * @see LmlView
     * @param <View> class of the view to be initiated. */
    <View> View createView(Class<View> viewClass, FileHandle lmlTemplateFile);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual string value.
     * @return parsed string value. */
    String parseString(String rawLmlData);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual float value.
     * @return parsed float value. */
    float parseFloat(String rawLmlData);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual int value.
     * @return parsed int value. */
    int parseInt(String rawLmlData);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual boolean value.
     * @return parsed boolean value. */
    boolean parseBoolean(String rawLmlData);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual array value.
     * @return string value separated and parsed as an array of values according to syntax rules. Note that arrays do
     *         NOT fully parse its elements: for example, an array of bundle texts will be split from a raw string to an
     *         actual Java array of strings, but will not be immediately converted to formatted bundle lines. This is
     *         because arrays are mostly either used by macros or processed further by tag or attribute parsers. Only
     *         ranges and actions are parsed. */
    String[] parseArray(String rawLmlData);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual string value.
     * @return string value separated and parsed as an array of values according to syntax rules. On contrary to
     *         {@link #parseArray(String)}, this method fully evaluates each array element. */
    String[] fullyParseArray(String rawLmlData);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual action object.
     * @return action referenced with the raw data. Might be null. */
    ActorConsumer<?, Object> parseAction(String rawLmlData);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual string value.
     * @param forActor some types of data (for example: method invocations) require a parameter to be properly
     *            retrieved. Although for most types this object is optional, others might produce invalid results
     *            without it or even will not work at all.
     * @return parsed string value. */
    String parseString(String rawLmlData, Object forActor);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual float value.
     * @param forActor some types of data (for example: method invocations) require a parameter to be properly
     *            retrieved. Although for most types this object is optional, others might produce invalid results
     *            without it or even will not work at all.
     * @return parsed float value. */
    float parseFloat(String rawLmlData, Object forActor);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual int value.
     * @param forActor some types of data (for example: method invocations) require a parameter to be properly
     *            retrieved. Although for most types this object is optional, others might produce invalid results
     *            without it or even will not work at all.
     * @return parsed int value. */
    int parseInt(String rawLmlData, Object forActor);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual boolean value.
     * @param forActor some types of data (for example: method invocations) require a parameter to be properly
     *            retrieved. Although for most types this object is optional, others might produce invalid results
     *            without it or even will not work at all.
     * @return parsed boolean value. */
    boolean parseBoolean(String rawLmlData, Object forActor);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual array value.
     * @param forActor some types of data (for example: method invocations) require a parameter to be properly
     *            retrieved. Although for most types this object is optional, others might produce invalid results
     *            without it or even will not work at all.
     * @return string value separated and parsed as an array of values according to syntax rules. Note that arrays do
     *         NOT fully parse its elements: for example, an array of bundle texts will be split from a raw string to an
     *         actual Java array of strings, but will not be immediately converted to formatted bundle lines. This is
     *         because arrays are mostly either used by macros or processed further by tag or attribute parsers. Only
     *         ranges and actions are parsed. */
    String[] parseArray(String rawLmlData, Object forActor);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual string value.
     * @param forActor some types of data (for example: method invocations) require a parameter to be properly
     *            retrieved. Although for most types this object is optional, others might produce invalid results
     *            without it or even will not work at all.
     * @return string value separated and parsed as an array of values according to syntax rules. On contrary to
     *         {@link #parseArray(String, Object)}, this method fully evaluates each array element. */
    String[] fullyParseArray(String rawLmlData, Object forActor);

    /** Mostly for internal use, although can be very useful for checking how each text part is parsed in your current
     * parser setup or in custom attributes.
     *
     * @param rawLmlData unparsed part of LML template that should be parsed to actual action object.
     * @param forActor some types of data (for example: method invocations) require a parameter to be properly
     *            retrieved. Although for most types this object is optional, others might produce invalid results
     *            without it or even will not work at all.
     * @return action referenced with the raw data. Might be null.
     * @param <ActorType> type of actor that can be consumed by the action. Performs an unchecked cast, might not
     *            actually match consumed actor type - the user should be reference correct actions that can actually
     *            process the selected actor. */
    <ActorType> ActorConsumer<?, ActorType> parseAction(String rawLmlData, ActorType forActor);

    /** @return direct access to map containing all previously parsed actors that had their ID set with "id" tag
     *         attribute. This map is filled during template parsing. Since the actual, internal map is returned with
     *         this method, it can be used to clear the actors map if you no longer want to keep references to actors,
     *         but need the parser itself for further use. The map is not cleared internally: previously parsed actors
     *         will be still available if not other widgets override their ID. Note that by default, this map ignores
     *         string case - actor mapped to "myId" would be also returned for "myid", "MYID", "MyID", etc. */
    ObjectMap<String, Actor> getActorsMappedByIds();

    /** Constructs a complex and (hopefully) meaningful exception message with currently parsed line number.
     *
     * @param message description of the error. */
    void throwError(String message);

    /** Constructs a complex and (hopefully) meaningful exception message with currently parsed line number.
     *
     * @param message description of the error.
     * @param optionalCause original cause of the message. */
    void throwError(String message, Throwable optionalCause);

    /** Constructs a complex and (hopefully) meaningful exception message with currently parsed line number. Exception
     * is created and thrown only if the parser is strict.
     *
     * @param message description of the error. */
    void throwErrorIfStrict(String message);

    /** Constructs a complex and (hopefully) meaningful exception message with currently parsed line number. Exception
     * is created and thrown only if the parser is strict.
     *
     * @param message description of the error.
     * @param optionalCause original cause of the message. */
    void throwErrorIfStrict(String message, Throwable optionalCause);

    /** Utility internal method. If parsing is currently in progress, this method allows to append the actor to the
     * result collection that will be eventually returned (or used to fill stage/view).
     *
     * @param actor will be added to the result collection and optionally mapped by its ID, if it has one. */
    void addActor(Actor actor);

    /** @param listener will be invoked after each parsed template. If returns {@link LmlParserListener#REMOVE}, will be
     *            removed after invocation. */
    void doBeforeParsing(LmlParserListener listener);

    /** @param listener will be invoked after each parsed template. If returns {@link LmlParserListener#REMOVE}, will be
     *            removed after invocation. */
    void doAfterParsing(LmlParserListener listener);
}
