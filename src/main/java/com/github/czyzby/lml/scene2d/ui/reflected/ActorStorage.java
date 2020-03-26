package com.github.czyzby.lml.scene2d.ui.reflected;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.kiwi.util.gdx.collection.pooled.PooledList;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.util.collection.IgnoreCaseStringMap;

/** This is a LML internal utility actor container. It extends {@link Actor} class, but not
 * {@link Group Group}. While it contains multiple actors, it does not display them in
 * any way, nor is it cleared when the actors are added to a {@link Group Group}.
 *
 * @author MJ
 * @see #getActors()
 * @see #getActor(String) */
public class ActorStorage extends Actor {
    private final Array<Actor> actors = GdxArrays.newArray();

    /** @param actor will be stored. */
    public void addActor(final Actor actor) {
        actors.add(actor);
    }

    /** @return direct reference to the array storing managed actors.
     * @see #getActor(String) */
    public Array<Actor> getActors() {
        return actors;
    }

    /** @param id ID of the actor set in a LML template with the "id" attribute.
     * @return actor with the selected ID, or null if: ID was null, no actor was found or the actor with this ID was not
     *         a direct child of storage in LML template. Use {@link #findActor(String)} to search for actors
     *         recursively.
     * @see #getActor(String, Class)
     * @see #findActor(String) */
    public Actor getActor(final String id) {
        if (id == null) {
            return null;
        }
        for (final Actor actor : actors) {
            if (id.equals(LmlUtilities.getActorId(actor))) {
                return actor;
            }
        }
        return null;
    }

    /** @param id ID of the actor set in a LML template with the "id" attribute.
     * @param withType class of the actor. The actor will be automatically casted for convenience.
     * @return actor with the selected ID, or null if: ID was null, no actor was found or the actor with this ID was not
     *         a direct child of storage in LML template. Use {@link #findActor(String, Class)} to search for actors
     *         recursively.
     * @see #getActor(String)
     * @see #findActor(String, Class)
     * @param <Widget> type of the actor. */
    @SuppressWarnings("unchecked")
    public <Widget extends Actor> Widget getActor(final String id, final Class<Widget> withType) {
        return (Widget) getActor(id);
    }

    /** @param id ID of the actor set in a LML template with the "id" attribute.
     * @return actor with the selected ID, or null if no actor was found or the id parameter is null. Note that this
     *         method performs a somewhat expensive recursive search for the actor; if the actor that you want to get
     *         the reference to was a direct child of the storage in the LML file, you should use the
     *         {@link #getActor(String)} method.
     * @see #getActor(String)
     * @see #findActor(String, Class) */
    public Actor findActor(final String id) {
        if (id == null) {
            return null;
        }
        for (final Actor actor : actors) {
            if (id.equals(LmlUtilities.getActorId(actor))) {
                return actor;
            } else if (actor instanceof Group) {
                final Actor child = LmlUtilities.getActorWithId((Group) actor, id);
                if (child != null) {
                    return child;
                }
            }
        }
        return null;
    }

    /** @param id ID of the actor set in a LML template with the "id" attribute.
     * @param withType class of the actor. The actor will be automatically casted for convenience.
     * @return actor with the selected ID, or null if no actor was found or the id parameter is null. Note that this
     *         method performs a somewhat expensive recursive search for the actor; if the actor that you want to get
     *         the reference to was a direct child of the storage in the LML file, you should use the
     *         {@link #getActor(String, Class)} method.
     * @see #getActor(String, Class)
     * @see #findActor(String)
     * @param <Widget> type of the actor. */
    @SuppressWarnings("unchecked")
    public <Widget extends Actor> Widget findActor(final String id, final Class<Widget> withType) {
        return (Widget) findActor(id);
    }

    /** @param searchRecursively if true, any {@link Group} extending actors will also be searched through and any of
     *            their children with IDs will be added to the map. Note that recursive search might be an expensive
     *            operation and if you are sure that all actors with IDs (that you need) were _direct_ children of the
     *            storage in LML, you should run this method with "false".
     * @return stored actors mapped by their IDs. Note that actors without IDs will be ignored and won't be added to the
     *         map. */
    public ObjectMap<String, Actor> getActorsMappedByIds(final boolean searchRecursively) {
        final ObjectMap<String, Actor> actorsByIds = new IgnoreCaseStringMap<Actor>();
        for (final Actor actor : actors) {
            final String id = LmlUtilities.getActorId(actor);
            if (id != null) {
                actorsByIds.put(id, actor);
            }
        }
        if (!searchRecursively) {
            return actorsByIds;
        }

        // Recursive ID search - need to scan all groups:
        final PooledList<Group> groupsToSearch = new PooledList<Group>();
        for (final Actor actor : actors) {
            if (actor instanceof Group) {
                groupsToSearch.add((Group) actor);
            }
        }
        while (!groupsToSearch.isEmpty()) {
            for (final Actor actor : groupsToSearch.removeFirst().getChildren()) {
                final String id = LmlUtilities.getActorId(actor);
                if (id != null) {
                    actorsByIds.put(id, actor);
                }
                if (actor instanceof Group) {
                    groupsToSearch.add((Group) actor);
                }
            }
        }
        return actorsByIds;
    }
}
