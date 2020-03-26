package com.github.czyzby.lml.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.I18NBundle;
import com.github.czyzby.kiwi.util.gdx.reflection.Reflection;
import com.github.czyzby.lml.annotation.processor.OnChangeProcessor;
import com.github.czyzby.lml.parser.LmlData;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.LmlTemplateReader;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.impl.AbstractLmlParser;
import com.github.czyzby.lml.parser.impl.DefaultLmlData;
import com.github.czyzby.lml.parser.impl.DefaultLmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;

/** Simplifies construction of {@code LmlParser} by initiating an instance of {@link DefaultLmlParser}. Note that
 * {@link LmlParserBuilder#build()} method returns an instance of parser that is actually created in builder's
 * constructor - calling building method multiple times always returns the same parser instance. In a sense, this
 * builder is more of a "preference setter" rather than "complex object constructor".
 *
 * @author MJ */
public class LmlParserBuilder {
    private final AbstractLmlParser parser;

    /** Constructs a new builder that wraps around an instance of {@link DefaultLmlParser}. */
    public LmlParserBuilder() {
        this(new DefaultLmlData());
    }

    /** Constructs a new builder that wraps around an instance of {@link DefaultLmlParser}.
     *
     * @param lmlData stores data needed to properly parse LML templates. */
    public LmlParserBuilder(final LmlData lmlData) {
        parser = getInstanceOfParser(lmlData);
    }

    /** @param lmlData contains LML parsing data.
     * @return a new instance of an extension of {@link AbstractLmlParser}. */
    protected AbstractLmlParser getInstanceOfParser(final LmlData lmlData) {
        return new DefaultLmlParser(lmlData);
    }

    /** @param defaultSkin will be set as the default {@link Skin} instance used when no ID is specified.
     * @return this for chaining. */
    public LmlParserBuilder skin(final Skin defaultSkin) {
        parser.getData().setDefaultSkin(defaultSkin);
        return this;
    }

    /** @param id will register value under this ID (ignoring case).
     * @param skin will be available in LML templates through the chosen ID.
     * @return this for chaining. */
    public LmlParserBuilder skin(final String id, final Skin skin) {
        parser.getData().addSkin(id, skin);
        return this;
    }

    /** @param tag all tags with this name (case ignored) will have the attribute applied.
     * @param attribute name of the attribute.
     * @param defaultValue default value of the attribute, used if attribute is not already set in the tag.
     * @return this for chaining. */
    public LmlParserBuilder style(final String tag, final String attribute, final String defaultValue) {
        parser.getStyleSheet().addStyle(tag, attribute, defaultValue);
        return this;
    }

    /** @param styleSheetPath LML style sheet data file.
     * @return this for chaining. */
    public LmlParserBuilder styles(final FileHandle styleSheetPath) {
        parser.parseStyleSheet(styleSheetPath);
        return this;
    }

    /** @param styleSheetPath path to internal file storing LML style sheet data.
     * @return this for chaining. */
    public LmlParserBuilder stylesPath(final String styleSheetPath) {
        parser.parseStyleSheet(Gdx.files.internal(styleSheetPath));
        return this;
    }

    /** @param defaultPreferences will be set as the default {@link Preferences} instance used when no ID is specified.
     * @return this for chaining. */
    public LmlParserBuilder preferences(final Preferences defaultPreferences) {
        parser.getData().setDefaultPreferences(defaultPreferences);
        return this;
    }

    /** @param id will register value under this ID (ignoring case).
     * @param preferences will be available in LML templates through the chosen ID.
     * @return this for chaining. */
    public LmlParserBuilder preferences(final String id, final Preferences preferences) {
        parser.getData().addPreferences(id, preferences);
        return this;
    }

    /** @param defaultI18nBundle will be set as the default {@link I18NBundle} instance used when no ID is specified.
     * @return this for chaining. */
    public LmlParserBuilder i18nBundle(final I18NBundle defaultI18nBundle) {
        parser.getData().setDefaultI18nBundle(defaultI18nBundle);
        return this;
    }

    /** @param id will register value under this ID (ignoring case).
     * @param i18nBundle will be available in LML templates through the chosen ID.
     * @return this for chaining. */
    public LmlParserBuilder i18nBundle(final String id, final I18NBundle i18nBundle) {
        parser.getData().addI18nBundle(id, i18nBundle);
        return this;
    }

    /** @param defaultTooltipManager will be set as the default {@link TooltipManager} instance used when no ID is
     *            specified.
     * @return this for chaining. */
    public LmlParserBuilder tooltipManager(final TooltipManager defaultTooltipManager) {
        parser.getData().setDefaultTooltipManager(defaultTooltipManager);
        return this;
    }

    /** @param id will register value under this ID (ignoring case).
     * @param tooltipManager will be available in LML templates through the chosen ID.
     * @return this for chaining. */
    public LmlParserBuilder tooltipManager(final String id, final TooltipManager tooltipManager) {
        parser.getData().addTooltipManager(id, tooltipManager);
        return this;
    }

    /** @param id name of the argument, as it will be available in LML templates.
     * @param argument will be converted to string according to its type. Object arrays and iterables are automatically
     *            converted to LML arrays.
     * @return this for chaining.
     * @see LmlUtilities#toAction(String)
     * @see LmlUtilities#toAction(String, String)
     * @see LmlUtilities#toArrayArgument(Iterable)
     * @see LmlUtilities#toArrayArgument(Object...)
     * @see LmlUtilities#toBundleLine(String)
     * @see LmlUtilities#toBundleLine(String, String)
     * @see LmlUtilities#toPreference(String)
     * @see LmlUtilities#toPreference(String, String)
     * @see LmlUtilities#toRangeArrayArgument(Object, int, int) */
    public LmlParserBuilder argument(final String id, final Object argument) {
        parser.getData().addArgument(id, argument);
        return this;
    }

    /** @param id ID of the action as available in LML views. If the passed ID collides with a name of registered
     *            {@link ActionContainer} method, {@link ActorConsumer} takes priority.
     * @param action can consume one argument (usually an actor or a string) and return a value. If a method with the
     *            specified ID appears in a LML template, this action will be found and invoked (or attached as a
     *            listener, or used otherwise). {@link ActorConsumer} does not rely on reflection, so might be somewhat
     *            noticeably faster on some devices, but usually the overhead is very small as opposed to using
     *            reflection-based actions.
     * @return this for chaining.
     * @see #actions(String, ActionContainer) */
    public LmlParserBuilder action(final String id, final ActorConsumer<?, ?> action) {
        parser.getData().addActorConsumer(id, action);
        return this;
    }

    /** @param id ID of the action container, that can be optionally added to method invocations in LML templates. ID
     *            has to be present only if there are method name collisions, otherwise undetermined method will be
     *            chosen.
     * @param actionContainer its one-arg and no-arg methods will be available as actions in LML templates. This uses
     *            reflection, so action containers should be properly included in GWT reflection pool.
     * @return this for chaining.
     * @see com.github.czyzby.lml.annotation.LmlAction */
    public LmlParserBuilder actions(final String id, final ActionContainer actionContainer) {
        parser.getData().addActionContainer(id, actionContainer);
        return this;
    }

    /** @param id ID of the action container, that can be optionally added to method invocations in LML templates. ID
     *            has to be present only if there are method name collisions, otherwise undetermined method will be
     *            chosen.
     * @param actionContainerClass initiated with no-arg constructor using reflection. Its one-arg and no-arg methods
     *            will be available as actions in LML templates. This uses reflection, so action containers should be
     *            properly included in GWT reflection pool.
     * @return this for chaining.
     * @see com.github.czyzby.lml.annotation.LmlAction */
    public LmlParserBuilder actions(final String id, final Class<? extends ActionContainer> actionContainerClass) {
        return actions(id, Reflection.newInstance(actionContainerClass));
    }

    /** @param processor can process attributes of specified actor base class. Every object of handled class (or class
     *            that extends it) will be able to parse the attribute using this processor.
     * @param names aliases of the attribute, as available in LML templates.
     * @return this for chaining.
     * @see LmlAttribute
     * @see com.github.czyzby.lml.parser.impl.tag.macro.NewAttributeLmlMacroTag */
    public LmlParserBuilder attribute(final LmlAttribute<? extends Actor> processor, final String... names) {
        parser.getSyntax().addAttributeProcessor(processor, names);
        return this;
    }

    /** @param tagProvider creates instances of {@link LmlTag}. {@link LmlTagProvider} is basically a simple functional
     *            interface - it's the LmlTag implementation that manages the actor. The constructed LmlTag instances
     *            usually handle a single actor at a time.
     * @param names aliases of the tag, as available in LML templates.
     * @return this for chaining.
     * @see LmlTag
     * @see com.github.czyzby.lml.parser.impl.tag.AbstractActorLmlTag
     * @see com.github.czyzby.lml.parser.impl.tag.macro.NewTagLmlMacroTag */
    public LmlParserBuilder tag(final LmlTagProvider tagProvider, final String... names) {
        parser.getSyntax().addTagProvider(tagProvider, names);
        return this;
    }

    /** @param macroTagProvider creates instances of {@link LmlTag}. {@link LmlTagProvider} is basically a simple
     *            functional interface - it's the LmlTag implementation that manages the macro. Macro tags manage text
     *            between their tags, allowing to modify template's structure.
     * @param names aliases of the macro, as available in LML templates.
     * @return this for chaining.
     * @see LmlTag
     * @see com.github.czyzby.lml.parser.impl.tag.AbstractMacroLmlTag
     * @see com.github.czyzby.lml.parser.impl.tag.macro.MetaLmlMacroTag */
    public LmlParserBuilder macro(final LmlTagProvider macroTagProvider, final String... names) {
        parser.getSyntax().addMacroTagProvider(macroTagProvider, names);
        return this;
    }

    /** @param processor updates a field through reflection when a specified actor changes its state.
     * @return this for chaining.
     * @see OnChangeProcessor
     * @see com.github.czyzby.lml.parser.impl.annotation.processor.AbstractOnChangeProcessor */
    public LmlParserBuilder onChangeProcessor(final OnChangeProcessor processor) {
        parser.getData().addOnChangeProcessor(processor);
        return this;
    }

    /** @param strict if false, some template syntax errors will be ignored. Useful for prototyping or planning and
     *            checking unfinished views
     * @return this for chaining. */
    public LmlParserBuilder strict(final boolean strict) {
        parser.setStrict(strict);
        return this;
    }

    /** @param nested if true, regular comments can be nested - basically, comments can contain comments, which isn't
     *            normal HTML behavior. This, however, forces you to keep your comments valid even when they are
     *            "commented out" (otherwise parser doesn't know when to exit from nested comments), so comment macros
     *            are generally preferred to using nested comments.
     * @return this for chaining. */
    public LmlParserBuilder nestedComments(final boolean nested) {
        parser.setNestedComments(nested);
        return this;
    }

    /** @param amount amount of lines (included in exception message) before and after the line that caused the error.
     *            Cannot be negative.
     * @return this for chaining. */
    public LmlParserBuilder debugLinesOnException(final int amount) {
        parser.setLinesAmountPrintedOnException(amount);
        return this;
    }

    /** @param reader determines how LML templates are read. Do not modify unless you are sure what you're doing.
     * @return this for chaining.
     * @see com.github.czyzby.lml.parser.impl.DefaultLmlTemplateReader */
    public LmlParserBuilder templateReader(final LmlTemplateReader reader) {
        parser.setTemplateReader(reader);
        return this;
    }

    /** @param syntax contains the operators, markers, tags, attributes and macros that are parsed and handled by LML
     *            parser. Make sure to register your custom data with {@link #attribute(LmlAttribute, String...)},
     *            {@link #tag(LmlTagProvider, String...)} and {@link #macro(LmlTagProvider, String...)} after invoking
     *            this method, otherwise changes will take places in the previous syntax.
     * @return this for chaining.
     * @see com.github.czyzby.lml.parser.impl.DefaultLmlSyntax */
    public LmlParserBuilder syntax(final LmlSyntax syntax) {
        parser.setSyntax(syntax);
        return this;
    }

    /** @return an instance of {@link LmlParser}. Each builder returns the same instance of each call of this method, so
     *         if you need multiple parsers for any reason, use a separate builder to construct it.
     * @see LmlParser#parseTemplate(String)
     * @see LmlParser#parseTemplate(FileHandle)
     * @see LmlParser#fillStage(com.badlogic.gdx.scenes.scene2d.Stage, String)
     * @see LmlParser#fillStage(com.badlogic.gdx.scenes.scene2d.Stage, FileHandle)
     * @see LmlParser#createView(Class, String)
     * @see LmlParser#createView(Object, String)
     * @see LmlParser#createView(Class, FileHandle)
     * @see LmlParser#createView(Object, FileHandle) */
    public LmlParser build() {
        return parser;
    }
}
