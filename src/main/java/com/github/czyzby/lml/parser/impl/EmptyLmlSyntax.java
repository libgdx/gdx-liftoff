package com.github.czyzby.lml.parser.impl;

import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.asset.lazy.provider.ObjectProvider;
import com.github.czyzby.kiwi.util.gdx.collection.GdxMaps;
import com.github.czyzby.kiwi.util.gdx.collection.immutable.ImmutableObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.lazy.LazyObjectMap;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.LssSyntax;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTagProvider;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** This is a semi-abstract class that implements all {@link LmlSyntax} methods. Returns standard (default) values for
 * all markers. Implements internal mechanisms of storing tag and attribute providers, but registers none: while this
 * syntax recognizes all LML markers, it does not know about any tags, attributes or macros by default. Using this
 * syntax directly allows you to manually choose which tags and attributes are supported, optimizing
 *
 * @author MJ
 * @see DefaultLmlSyntax */
public class EmptyLmlSyntax implements LmlSyntax {
    /** Key: tag name (ignoring case); value: tag provider. */
    private final ObjectMap<String, LmlTagProvider> tagProviders = new IgnoreCaseStringMap<LmlTagProvider>();
    /** Key: tag name (ignoring case); value: macro tag provider. */
    private final ObjectMap<String, LmlTagProvider> macroTagProviders = new IgnoreCaseStringMap<LmlTagProvider>();
    /** Key: class of actor; value: map with attributes assigned to the widget (key: attribute name, ignoring case). */
    private final ObjectMap<Class<?>, ObjectMap<String, LmlAttribute<?>>> attributeProcessors = getLazyMapOfIgnoreCaseMaps();
    /** Key: class of actor builder; value: map with building attributes assigned to the builder (key: attribute name,
     * ignoring case, value: processor). */
    private final ObjectMap<Class<?>, ObjectMap<String, LmlBuildingAttribute<?>>> buildingAttributeProcessors = getLazyMapOfIgnoreCaseMaps();

    private final LssSyntax lssSyntax = createLssSyntax();

    /** @return a new instance of object map storing maps with keys as values, ignoring their case. Utility provider.
     * @param <Key> type of map keys.
     * @param <Value> type of stored maps values. */
    protected static <Key, Value> ObjectMap<Key, ObjectMap<String, Value>> getLazyMapOfIgnoreCaseMaps() {
        // This map returns a new IgnoreCaseStringMap on each get(Key) call if there is no map assigned to the passed
        // key. This is very convenient for maps of collections, as you do not have to go through the whole lazy
        // initiation process.
        return LazyObjectMap.newMap(new ObjectProvider<ObjectMap<String, Value>>() {
            @Override
            public ObjectMap<String, Value> provide() {
                return new IgnoreCaseStringMap<Value>();
            }
        });
    }

    /** @return {@link LssSyntax} implementation. */
    protected LssSyntax createLssSyntax() {
        return new DefaultLssSyntax();
    }

    @Override
    public LssSyntax getLssSyntax() {
        return lssSyntax;
    }

    // Warning: before any syntax changes, make sure that LmlUtilities are also updated.

    @Override
    public char getTagOpening() {
        return '<';
    }

    @Override
    public char getTagClosing() {
        return '>';
    }

    @Override
    public char getClosedTagMarker() {
        return '/';
    }

    @Override
    public char getCommentOpening() {
        return '!';
    }

    @Override
    public char getSchemaCommentMarker() {
        return '?';
    }

    @Override
    public char getCommentClosing() {
        return '-';
    }

    @Override
    public String getDocumentTypeOpening() {
        return "DOCTYPE";
    }

    @Override
    public char getArgumentOpening() {
        return '{';
    }

    @Override
    public char getArgumentClosing() {
        return '}';
    }

    @Override
    public char getMacroMarker() {
        return ':';
    }

    @Override
    public char getIdSeparatorMarker() {
        return '.';
    }

    @Override
    public char getPreferenceMarker() {
        return '#';
    }

    @Override
    public char getBundleLineMarker() {
        return '@';
    }

    @Override
    public char getBundleLineArgumentMarker() {
        return '|';
    }

    @Override
    public char getAttributeSeparator() {
        return '=';
    }

    @Override
    public char getMethodInvocationMarker() {
        return '$';
    }

    @Override
    public char getArrayElementSeparator() {
        return ';';
    }

    @Override
    public char getRangeArrayOpening() {
        return '[';
    }

    @Override
    public char getRangeArraySeparator() {
        return ',';
    }

    @Override
    public char getRangeArrayClosing() {
        return ']';
    }

    @Override
    public char getEquationMarker() {
        return '=';
    }

    @Override
    public char getConditionMarker() {
        return '?';
    }

    @Override
    public char getTernaryMarker() {
        return ':';
    }

    @Override
    public LmlTagProvider getTagProvider(final String tagName) {
        return tagProviders.get(tagName);
    }

    @Override
    public void addTagProvider(final LmlTagProvider provider, final String... supportedTagNames) {
        for (final String name : supportedTagNames) {
            tagProviders.put(name, provider);
        }
    }

    @Override
    public void removeTagProvider(final String tagName) {
        tagProviders.remove(tagName);
    }

    @Override
    public LmlTagProvider getMacroTagProvider(final String tagName) {
        return macroTagProviders.get(tagName);
    }

    @Override
    public void addMacroTagProvider(final LmlTagProvider provider, final String... supportedTagNames) {
        for (final String name : supportedTagNames) {
            macroTagProviders.put(name, provider);
        }
    }

    @Override
    public void removeMacroTagProvider(final String tagName) {
        macroTagProviders.remove(tagName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Actor> LmlAttribute<Actor> getAttributeProcessor(final Actor forActor, final String attributeName) {
        return (LmlAttribute<Actor>) getAttributeProcessor(forActor.getClass(), attributeName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Actor> LmlAttribute<Actor> getAttributeProcessor(final Class<Actor> forActorType,
            final String attributeName) {
        Class<?> actorClass = forActorType;
        while (actorClass != null) {
            if (attributeProcessors.containsKey(actorClass)) {
                final ObjectMap<String, LmlAttribute<?>> processors = attributeProcessors.get(actorClass);
                if (processors.containsKey(attributeName)) {
                    return (LmlAttribute<Actor>) processors.get(attributeName);
                }
            }
            actorClass = actorClass.getSuperclass();
        }
        return null;
    }

    @Override
    public <Actor> void addAttributeProcessor(final LmlAttribute<Actor> attributeProcessor, final String... names) {
        final ObjectMap<String, LmlAttribute<?>> processors = attributeProcessors
                .get(attributeProcessor.getHandledType());
        for (final String name : names) {
            processors.put(name, attributeProcessor);
        }
    }

    @Override
    public void removeAttributeProcessor(final String name, final Class<?> handledActorType) {
        attributeProcessors.get(handledActorType).remove(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Builder extends LmlActorBuilder> LmlBuildingAttribute<Builder> getBuildingAttributeProcessor(
            final Builder builder, final String attributeName) {
        Class<?> builderClass = builder.getClass();
        while (builderClass != null) {
            if (buildingAttributeProcessors.containsKey(builderClass)) {
                final ObjectMap<String, LmlBuildingAttribute<?>> processors = buildingAttributeProcessors
                        .get(builderClass);
                if (processors.containsKey(attributeName)) {
                    return (LmlBuildingAttribute<Builder>) processors.get(attributeName);
                }
            }
            builderClass = builderClass.getSuperclass();
        }
        return null;
    }

    @Override
    public <Builder extends LmlActorBuilder> void addBuildingAttributeProcessor(
            final LmlBuildingAttribute<Builder> buildingAttributeProcessor, final String... names) {
        final ObjectMap<String, LmlBuildingAttribute<?>> processors = buildingAttributeProcessors
                .get(buildingAttributeProcessor.getBuilderType());
        for (final String name : names) {
            processors.put(name, buildingAttributeProcessor);
        }
    }

    @Override
    public void removeBuildingAttributeProcessor(final String name, final Class<?> handledActorType) {
        buildingAttributeProcessors.get(handledActorType).remove(name);
    }

    // DTD utility methods:
    @Override
    public ObjectMap<String, LmlAttribute<?>> getAttributesForActor(final Object actor) {
        final ObjectMap<String, LmlAttribute<?>> attributes = GdxMaps.newObjectMap();
        if (actor == null) {
            return attributes;
        }
        Class<?> actorClass = actor.getClass();
        while (actorClass != null) {
            attributes.putAll(attributeProcessors.get(actorClass));
            actorClass = actorClass.getSuperclass();
        }
        return attributes;
    }

    @Override
    public ObjectMap<String, LmlBuildingAttribute<?>> getAttributesForBuilder(final LmlActorBuilder builder) {
        final ObjectMap<String, LmlBuildingAttribute<?>> attributes = GdxMaps.newObjectMap();
        if (builder == null) {
            return attributes;
        }
        Class<?> builderClass = builder.getClass();
        while (builderClass != null) {
            attributes.putAll(buildingAttributeProcessors.get(builderClass));
            builderClass = builderClass.getSuperclass();
        }
        return attributes;
    }

    @Override
    public ObjectMap<String, LmlTagProvider> getMacroTags() {
        return ImmutableObjectMap.copyOf(macroTagProviders);
    }

    @Override
    public ObjectMap<String, LmlTagProvider> getTags() {
        return ImmutableObjectMap.copyOf(tagProviders);
    }
}
