package com.github.czyzby.lml.parser;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.lml.annotation.processor.OnChangeProcessor;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.action.ActionContainerWrapper;
import com.github.czyzby.lml.parser.action.ActorConsumer;

/** Interface for LML data container. Manages skins, i18n bundles and preferences that can be accessed through
 * appropriate LML signs defined by LML syntax.
 *
 * @author MJ
 * @see com.github.czyzby.lml.parser.impl.DefaultLmlData */
public interface LmlData {
    /** @param name ID of the registered skin. Implementation might make the ID ignore case, so be careful not to repeat
     *            IDs for multiple skins.
     * @param skin will be accessible with the chosen ID. */
    void addSkin(String name, Skin skin);

    /** @param skin will be accessible with the default ID. */
    void setDefaultSkin(Skin skin);

    /** @param name value previously associated with this name will be removed (if any). */
    void removeSkin(String name);

    /** @param name ID of the skin. Might ignore case.
     * @return skin associated with the passed ID. */
    Skin getSkin(String name);

    /** @return skin associated with the default ID. */
    Skin getDefaultSkin();

    /** @param name ID of the registered bundle. Implementation might make the ID ignore case, so be careful not to
     *            repeat IDs for multiple bundles.
     * @param i18nBundle will be accessible with the chosen ID. */
    void addI18nBundle(String name, I18NBundle i18nBundle);

    /** @param i18nBundle will be accessible with the default ID. */
    void setDefaultI18nBundle(I18NBundle i18nBundle);

    /** @param name value previously associated with this name will be removed (if any). */
    void removeI18nBundle(String name);

    /** @param name ID of the bundle. Might ignore case.
     * @return bundle associated with the chosen ID. */
    I18NBundle getI18nBundle(String name);

    /** @return bundle associated with the default ID. */
    I18NBundle getDefaultI18nBundle();

    /** @param name ID of the registered preferences. Implementation might make the ID ignore case, so be careful not to
     *            repeat IDs for multiple preferences.
     * @param preferences will be accessible with the chosen ID. */
    void addPreferences(String name, Preferences preferences);

    /** @param preferences will be accessible with the default ID. */
    void setDefaultPreferences(Preferences preferences);

    /** @param name value previously associated with this name will be removed (if any). */
    void removePreferences(String name);

    /** @param name ID of the preferences. Might ignore case.
     * @return preferences associated with the passed ID. */
    Preferences getPreferences(String name);

    /** @return preferences associated with the default ID. */
    Preferences getDefaultPreferences();

    /** @param name ID of the registered manager. Implementation might make the ID ignore case, so be careful not to
     *            repeat IDs for multiple managers.
     * @param tooltipManager will be accessible with the chosen ID. */
    void addTooltipManager(String name, TooltipManager tooltipManager);

    /** @param tooltipManager will be accessible with the default ID. */
    void setDefaultTooltipManager(TooltipManager tooltipManager);

    /** @param name value previously associated with this name will be removed (if any). */
    void removeTooltipManager(String name);

    /** @param name ID of the manager. Might ignore case.
     * @return manager associated with the passed ID. */
    TooltipManager getTooltipManager(String name);

    /** @return tooltip manager associated with the default ID. By default, returns LibGDX static tooltip manager
     *         instance. */
    TooltipManager getDefaultTooltipManager();

    /** @param name name of the action as it should appear in LML templates.
     * @param actorConsumer allows to invoke an action, consuming the connected widget. */
    void addActorConsumer(String name, ActorConsumer<?, ?> actorConsumer);

    /** @param name value previously associated with this name will be removed (if any). */
    void removeActorConsumer(String name);

    /** @param name name of the action container, optional to provide in LML templates. If multiple containers have the
     *            same method name (or ID), container name (and a dot) should proceed the method name to choose the
     *            correct container.
     * @param actionContainer properly reflected container, which methods can be referenced in LML templates. */
    void addActionContainer(String name, ActionContainer actionContainer);

    /** @param name value previously associated with this name will be removed (if any). */
    void removeActionContainer(String name);

    /** @param name name of the chosen actor consumer.
     * @return actor consumer associated with the passed name or null if absent. */
    ActorConsumer<?, ?> getActorConsumer(String name);

    /** @param name name of the registered action container.
     * @return wrapped action container instance or null if unknown name. */
    ActionContainerWrapper getActionContainer(String name);

    /** @return all currently registered action containers. */
    Iterable<ActionContainerWrapper> getActionContainers();

    /** @param onChangeProcessor handles fields annotated with {@link com.github.czyzby.lml.annotation.OnChange}. By
     *            adding a custom processor, it is possible to associate new types of fields with specified widgets.
     * @see OnChangeProcessor */
    void addOnChangeProcessor(OnChangeProcessor onChangeProcessor);

    /** @return current collection of registered on change processors. */
    Iterable<OnChangeProcessor> getOnChangeProcessors();

    /** @param name name of the argument as it should be referenced in LML templates.
     * @param value value associated with the argument name. Will be converted to string (if null is passed, "null"
     *            string will be used instead). If this is an object array or an iterable, this will be converted to a
     *            LML array with the default syntax. If you want to change the syntax, override and modify default
     *            implementation. */
    void addArgument(String name, Object value);

    /** @param name value previously associated with this name will be removed (if any). */
    void removeArgument(String name);

    /** @param name name of the argument as it appears in the LML template.
     * @return value associated with the name converted to string. */
    String getArgument(String name);

    /** @return all current LML arguments. Should not be modified manually - might return internal container's map. */
    ObjectMap<String, String> getArguments();
}
