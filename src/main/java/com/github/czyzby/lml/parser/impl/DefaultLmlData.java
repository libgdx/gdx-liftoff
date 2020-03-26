package com.github.czyzby.lml.parser.impl;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.common.Nullables;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.scene2d.Actors;
import com.github.czyzby.kiwi.util.gdx.scene2d.InterfaceSkin;
import com.github.czyzby.lml.annotation.processor.OnChangeProcessor;
import com.github.czyzby.lml.parser.LmlData;
import com.github.czyzby.lml.parser.action.ActionContainer;
import com.github.czyzby.lml.parser.action.ActionContainerWrapper;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.impl.annotation.processor.ButtonOnChangeProcessor;
import com.github.czyzby.lml.parser.impl.annotation.processor.ListOnChangeProcessor;
import com.github.czyzby.lml.parser.impl.annotation.processor.ListSingleItemOnChangeProcessor;
import com.github.czyzby.lml.parser.impl.annotation.processor.ProgressBarOnChangeProcessor;
import com.github.czyzby.lml.parser.impl.annotation.processor.SelectBoxOnChangeProcessor;
import com.github.czyzby.lml.parser.impl.annotation.processor.SelectBoxSingleItemOnChangeProcessor;
import com.github.czyzby.lml.parser.impl.annotation.processor.TextFieldOnChangeProcessor;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** Uses ignore-case maps to store LML parser arguments, i18n bundles, preferences, tooltip managers, actor consumers
 * and action containers. Provides Skin instance and registered on change processors.
 *
 * @author MJ */
public class DefaultLmlData implements LmlData {
    /** By default, this key is used to store default values. Accessing managers, bundles or preferences with this key
     * matches the default getters behavior. Defaults to "default". */
    public static final String DEFAULT_KEY = Actors.DEFAULT_STYLE;

    private final Array<OnChangeProcessor> onChangeProcessors = GdxArrays.newArray();
    private final ObjectMap<String, Skin> skins = new IgnoreCaseStringMap<Skin>();
    private final ObjectMap<String, String> arguments = new IgnoreCaseStringMap<String>();
    private final ObjectMap<String, I18NBundle> bundles = new IgnoreCaseStringMap<I18NBundle>();
    private final ObjectMap<String, Preferences> preferences = new IgnoreCaseStringMap<Preferences>();
    private final ObjectMap<String, TooltipManager> tooltipManagers = new IgnoreCaseStringMap<TooltipManager>();
    private final ObjectMap<String, ActorConsumer<?, ?>> actorConsumers = new IgnoreCaseStringMap<ActorConsumer<?, ?>>();
    private final ObjectMap<String, ActionContainerWrapper> actionContainers = new IgnoreCaseStringMap<ActionContainerWrapper>();

    /** Creates a new data container with default on change processors. If you have set global interface skin object
     * with Kiwi utilities, your selected skin will be used.
     *
     * @see InterfaceSkin */
    public DefaultLmlData() {
        this(InterfaceSkin.get());
    }

    /** Since skin is the only object required to create pretty much any LML view, a constructor with default skin is
     * provided.
     *
     * @param skin will become the default skin instance. */
    public DefaultLmlData(final Skin skin) {
        setDefaultSkin(skin);
        addDefaultOnChangeProcessors();
    }

    /** Adds default on change processors. Warning: used by the constructor. */
    protected void addDefaultOnChangeProcessors() {
        onChangeProcessors.add(new ButtonOnChangeProcessor());
        onChangeProcessors.add(new ListOnChangeProcessor());
        onChangeProcessors.add(new ListSingleItemOnChangeProcessor());
        onChangeProcessors.add(new ProgressBarOnChangeProcessor());
        onChangeProcessors.add(new SelectBoxOnChangeProcessor());
        onChangeProcessors.add(new SelectBoxSingleItemOnChangeProcessor());
        onChangeProcessors.add(new TextFieldOnChangeProcessor());
    }

    @Override
    public void addSkin(final String name, final Skin skin) {
        skins.put(name, skin);
    }

    @Override
    public void removeSkin(final String name) {
        skins.remove(name);
    }

    @Override
    public void setDefaultSkin(final Skin skin) {
        skins.put(DEFAULT_KEY, skin);
    }

    @Override
    public Skin getSkin(final String name) {
        return skins.get(name);
    }

    @Override
    public Skin getDefaultSkin() {
        return skins.get(DEFAULT_KEY);
    }

    @Override
    public void addI18nBundle(final String name, final I18NBundle i18nBundle) {
        bundles.put(name, i18nBundle);
    }

    @Override
    public void removeI18nBundle(final String name) {
        bundles.remove(name);
    }

    @Override
    public void setDefaultI18nBundle(final I18NBundle i18nBundle) {
        bundles.put(DEFAULT_KEY, i18nBundle);
    }

    @Override
    public I18NBundle getI18nBundle(final String name) {
        return bundles.get(name);
    }

    @Override
    public I18NBundle getDefaultI18nBundle() {
        return bundles.get(DEFAULT_KEY);
    }

    @Override
    public void addPreferences(final String name, final Preferences preferences) {
        this.preferences.put(name, preferences);
    }

    @Override
    public void removePreferences(final String name) {
        preferences.remove(name);
    }

    @Override
    public void setDefaultPreferences(final Preferences preferences) {
        this.preferences.put(DEFAULT_KEY, preferences);
    }

    @Override
    public Preferences getPreferences(final String name) {
        return preferences.get(name);
    }

    @Override
    public Preferences getDefaultPreferences() {
        return preferences.get(DEFAULT_KEY);
    }

    @Override
    public void addTooltipManager(final String name, final TooltipManager tooltipManager) {
        tooltipManagers.put(name, tooltipManager);
    }

    @Override
    public void setDefaultTooltipManager(final TooltipManager tooltipManager) {
        tooltipManagers.put(DEFAULT_KEY, tooltipManager);
    }

    @Override
    public void removeTooltipManager(final String name) {
        tooltipManagers.remove(name);
    }

    @Override
    public TooltipManager getTooltipManager(final String name) {
        return tooltipManagers.get(name, TooltipManager.getInstance());
    }

    @Override
    public TooltipManager getDefaultTooltipManager() {
        return tooltipManagers.get(DEFAULT_KEY, TooltipManager.getInstance());
    }

    @Override
    public void addActorConsumer(final String name, final ActorConsumer<?, ?> actorConsumer) {
        actorConsumers.put(name, actorConsumer);
    }

    @Override
    public void removeActorConsumer(final String name) {
        actorConsumers.remove(name);
    }

    @Override
    public void addActionContainer(final String name, final ActionContainer actionContainer) {
        actionContainers.put(name, new ActionContainerWrapper(actionContainer));
    }

    @Override
    public void removeActionContainer(final String name) {
        actionContainers.remove(name);
    }

    @Override
    public ActorConsumer<?, ?> getActorConsumer(final String name) {
        return actorConsumers.get(name);
    }

    @Override
    public ActionContainerWrapper getActionContainer(final String name) {
        return actionContainers.get(name);
    }

    @Override
    public Iterable<ActionContainerWrapper> getActionContainers() {
        return actionContainers.values();
    }

    @Override
    public void addOnChangeProcessor(final OnChangeProcessor onChangeProcessor) {
        onChangeProcessors.add(onChangeProcessor);
    }

    @Override
    public Iterable<OnChangeProcessor> getOnChangeProcessors() {
        return onChangeProcessors;
    }

    @Override
    public void addArgument(final String name, final Object value) {
        final String argument;
        if (value instanceof Object[]) {
            argument = LmlUtilities.toArrayArgument((Object[]) value);
        } else if (value instanceof Iterable<?>) {
            argument = LmlUtilities.toArrayArgument((Iterable<?>) value);
        } else {
            argument = Nullables.toString(value);
        }
        arguments.put(name, argument);
    }

    @Override
    public void removeArgument(final String name) {
        arguments.remove(name);
    }

    @Override
    public String getArgument(final String name) {
        return arguments.get(name);
    }

    @Override
    public ObjectMap<String, String> getArguments() {
        return arguments;
    }
}
