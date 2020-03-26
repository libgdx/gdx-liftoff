package com.github.czyzby.lml.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.kiwi.util.common.Strings;
import com.github.czyzby.kiwi.util.gdx.collection.GdxArrays;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.action.StageAttacher;
import com.github.czyzby.lml.parser.impl.action.DefaultStageAttacher;
import com.github.czyzby.lml.parser.impl.action.DefaultStageAttacher.StandardPositionConverter;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.scene2d.ui.reflected.GenericTreeNode;

/** Custom user object set to LML actors when additional data needs to be stored.
 *
 * @author MJ */
public class LmlUserObject {
    private Cell<?> cell;
    private Tree.Node node;
    private StageAttacher stageAttacher;
    private Object data;
    private Array<ActorConsumer<?, Object>> onCreateActions;
    private Array<ActorConsumer<?, Object>> onCloseActions;
    private TableTarget tableTarget = StandardTableTarget.MAIN;

    /** @return cell of a table in which the actor is stored. */
    public Cell<?> getCell() {
        return cell;
    }

    /** @param cell cell of a table in which the actor is stored. */
    public void setCell(final Cell<?> cell) {
        this.cell = cell;
    }

    /** @return custom widget data that would be too specific to include in all widgets. Most actors - if they do need a
     *         value like this - usually require one such property (and can use a custom container, if they need more),
     *         so only 1 such field is provided. Null for most widgets. */
    public Object getData() {
        return data;
    }

    /** @param data custom widget data. */
    public void setData(final Object data) {
        this.data = data;
    }

    /** @return optional stage attacher. */
    public StageAttacher getStageAttacher() {
        return stageAttacher;
    }

    /** @param stageAttacher will attach the widget to stage. */
    public void setStageAttacher(final StageAttacher stageAttacher) {
        this.stageAttacher = stageAttacher;
    }

    /** Creates a default stage attacher if the widget does not have one already. Use on widgets that are usually
     * template roots, like windows or dialogs. */
    public void initiateStageAttacher() {
        if (stageAttacher == null) {
            stageAttacher = new DefaultStageAttacher();
        }
    }

    /** @param onCreateAction stores this action to be invoked when the actor is fully initiated. */
    public void addOnCreateAction(final ActorConsumer<?, Object> onCreateAction) {
        if (onCreateActions == null) {
            onCreateActions = GdxArrays.newArray();
        }
        if (onCreateAction != null) {
            onCreateActions.add(onCreateAction);
        }
    }

    /** @param onActor will invoke all currently stored on create actions on this actor and clear the actions queue. */
    public void invokeOnCreateActions(final Actor onActor) {
        if (onCreateActions == null) {
            return;
        }
        for (final ActorConsumer<?, Object> onCreateAction : onCreateActions) {
            onCreateAction.consume(onActor);
        }
        onCreateActions = null;
    }

    /** @param onCloseAction stores this action to be invoked when the actor's tag is closed. */
    public void addOnCloseAction(final ActorConsumer<?, Object> onCloseAction) {
        if (onCloseActions == null) {
            onCloseActions = GdxArrays.newArray();
        }
        if (onCloseAction != null) {
            onCloseActions.add(onCloseAction);
        }
    }

    /** @param onActor will invoke all currently stored on close actions on this actor and clear the actions queue. */
    public void invokeOnCloseActions(final Actor onActor) {
        if (onCloseActions == null) {
            return;
        }
        for (final ActorConsumer<?, Object> onCloseAction : onCloseActions) {
            onCloseAction.consume(onActor);
        }
        onCloseActions = null;
    }

    /** @param actor is supposed to become a tree node.
     * @param parent optional actor's parent, used to validate if the actor can be a tree node (it has to have a tree
     *            parent in the structure).
     * @param parser used to parse the actor. */
    public void prepareTreeNode(final Actor actor, final LmlTag parent, final LmlParser parser) {
        if (parent == null) {
            parser.throwErrorIfStrict("Actor cannot be tree node if it has no parent.");
            return;
        } else if (!hasTreeParent(parent)) {
            parser.throwErrorIfStrict("Actor cannot be a tree node if it has no tree parent in the structure.");
            return;
        }
        node = new GenericTreeNode(actor);
    }

    /** @return non-null tree node containing the actor or null if the actor is not a tree node. */
    public Tree.Node getNode() {
        return node;
    }

    private static boolean hasTreeParent(LmlTag parent) {
        while (parent != null) {
            if (parent.getActor() instanceof Tree) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /** @param parser parses an attribute.
     * @param actor wants to set its position.
     * @param rawAttributeData will be parsed. */
    public void parseXPosition(final LmlParser parser, final Actor actor, final String rawAttributeData) {
        initiateStageAttacher();
        final String parsedValue = parser.parseString(rawAttributeData).trim();
        try {
            final DefaultStageAttacher stageAttacher = (DefaultStageAttacher) this.stageAttacher;
            if (Strings.endsWith(parsedValue, '%')) {
                stageAttacher.setXConverter(StandardPositionConverter.PERCENT);
                stageAttacher.setX(Float.parseFloat(LmlUtilities.stripEnding(parsedValue)));
            } else {
                stageAttacher.setXConverter(StandardPositionConverter.ABSOLUTE);
                stageAttacher.setX(Float.parseFloat(parsedValue));
            }
        } catch (final Exception parsingException) {
            // Might happen due to invalid number format or class cast.
            parser.throwError(
                    "Unable to parse position: " + rawAttributeData
                            + ". Is it a valid float or a float ending with %? Did you use a custom stage attacher that cannot parse position attribute?",
                    parsingException);
        }
    }

    /** @param parser parses an attribute.
     * @param actor wants to set its position.
     * @param rawAttributeData will be parsed. */
    public void parseYPosition(final LmlParser parser, final Actor actor, final String rawAttributeData) {
        initiateStageAttacher();
        final String parsedValue = parser.parseString(rawAttributeData, actor).trim();
        try {
            final DefaultStageAttacher stageAttacher = (DefaultStageAttacher) this.stageAttacher;
            if (Strings.endsWith(parsedValue, '%')) {
                stageAttacher.setYConverter(StandardPositionConverter.PERCENT);
                stageAttacher.setY(Float.parseFloat(LmlUtilities.stripEnding(parsedValue)));
            } else {
                stageAttacher.setYConverter(StandardPositionConverter.ABSOLUTE);
                stageAttacher.setY(Float.parseFloat(parsedValue));
            }
        } catch (final Exception parsingException) {
            // Might happen due to invalid number format or class cast.
            parser.throwError(
                    "Unable to parse position: " + rawAttributeData
                            + ". Is it a valid float or a float ending with %? Did you use a custom stage attacher that cannot parse position attribute?",
                    parsingException);
        }
    }

    /** @return object that determines how the actor is added to a table. */
    public TableTarget getTableTarget() {
        return tableTarget;
    }

    /** @param tableTarget determines how the actor is added to a table. */
    public void setTableTarget(final TableTarget tableTarget) {
        if (tableTarget == null) {
            throw new IllegalArgumentException("Table target cannot be null.");
        }
        this.tableTarget = tableTarget;
    }

    /** Determines how the actor is added to a table. If a table-extending actor contains multiple tables (for example:
     * Window also has a title table, Dialog has additional content and buttons tables), these objects allow to choose
     * which table is actually used.
     *
     * @author MJ */
    public static interface TableTarget {
        /** @param table may consist of multiple tables.
         * @return table that should be chosen with this target. */
        Table extract(Table table);

        /** @param table will contain the actor.
         * @param actor will be added to the table.
         * @return cell of the table with the actor. */
        Cell<?> add(Table table, Actor actor);
    }

    /** Used to extract a {@link Table} instance from another table. Used for complex, multipart widgets that consist of
     * multiple tables.
     *
     * @author MJ */
    public static interface TableExtractor {
        /** @param table may consist of multiple tables.
         * @return table chosen by this extractor. */
        Table extract(Table table);
    }

    /** Determines how the actor is added to a table. If a table-extending actor contains multiple tables (for example:
     * Window also has a title table, Dialog has additional content and buttons tables), this enum allows to choose
     * which table is actually used.
     *
     * @author MJ */
    public static enum StandardTableTarget implements TableTarget {
        /** Adds actors directly to the main table of the widget. For most tables, actor will be appended to the table
         * itself; if the actor is a dialog, actor is appended to the content table. */
        MAIN(new TableExtractor() {
            @Override
            public Table extract(final Table table) {
                return table instanceof Dialog ? ((Dialog) table).getContentTable() : table;
            }
        }),
        /** Adds actors to the title table. Available only for windows. */
        TITLE(new TableExtractor() {
            @Override
            public Table extract(final Table table) {
                return table instanceof Window ? ((Window) table).getTitleTable() : table;
            }
        }),
        /** Adds actors to the buttons table. Available only for dialogs. */
        BUTTON(new TableExtractor() {
            @Override
            public Table extract(final Table table) {
                return table instanceof Dialog ? ((Dialog) table).getButtonTable() : table;
            }
        }),
        /** Adds actors directly to the table, even if actor has another main table (for example, {@link Dialog} has the
         * {@link Dialog#getContentTable()}, which would be ignored using this attacher). */
        DIRECT(new TableExtractor() {
            @Override
            public Table extract(final Table table) {
                return table;
            }
        });

        private TableExtractor tableExtractor;

        private StandardTableTarget(final TableExtractor tableExteractor) {
            tableExtractor = tableExteractor;
        }

        @Override
        public final Cell<?> add(final Table table, final Actor actor) {
            return extract(table).add(actor);
        }

        @Override
        public final Table extract(final Table table) {
            return tableExtractor.extract(table);
        }

        /** @param tableExtractor will change behavior of the chosen table target. Use with care! */
        public void setTableExtractor(final TableExtractor tableExtractor) {
            this.tableExtractor = tableExtractor;
        }
    }
}
