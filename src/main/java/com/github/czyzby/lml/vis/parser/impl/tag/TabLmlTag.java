package com.github.czyzby.lml.vis.parser.impl.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.tag.builder.TextLmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.ui.VisTabTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * Manages an instance of {@link Tab} through specialized table extension:
 * {@link VisTabTable}. Handles children like a regular table tag. Mapped to "tab".
 * @author MJ
 */
public class TabLmlTag extends VisTableLmlTag {
    private Tab tab;

    public TabLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
        if (!(parentTag instanceof TabbedPaneLmlTag)) {
            parser.throwErrorIfStrict("Only tabbed panes can be parents of tabs. Found parent: " + parentTag);
        }
    }

    @Override
    protected LmlActorBuilder getNewInstanceOfBuilder() {
        return new TextLmlActorBuilder();
    }

    @Override
    protected Actor getNewInstanceOfActor(final LmlActorBuilder builder) {
        VisTabTable tabTable = new VisTabTable(((TextLmlActorBuilder) builder).getText());
        tab = tabTable.getTab();
        return tabTable;
    }

    /** @return {@link Tab} instance managed by the {@link VisTabTable}. */
    @Override
    public Object getManagedObject() {
        return tab;
    }
}
