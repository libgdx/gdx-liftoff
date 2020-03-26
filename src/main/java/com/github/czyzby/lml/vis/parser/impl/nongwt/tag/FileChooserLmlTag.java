package com.github.czyzby.lml.vis.parser.impl.nongwt.tag;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;
import com.github.czyzby.lml.vis.parser.impl.action.VisStageAttacher;
import com.github.czyzby.lml.vis.parser.impl.action.VisStageAttacher.PopupAttacher;
import com.github.czyzby.lml.vis.parser.impl.tag.VisWindowLmlTag;
import com.github.czyzby.lml.vis.parser.impl.tag.builder.VisWindowLmlActorBuilder;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;

/** Handles {@link FileChooser} actor. Works like any other window - can append extra children with table cell
 * attributes, converts plain text into labels. Instead of being added directly to its parent, it is attached with a
 * {@link ClickListener}, which shows the file chooser each time the widget is not disabled and clicked. Attaching a
 * listener with {@link com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file.FileChooserListenerLmlAttribute} is
 * strongly advised. Mapped to "fileChooser".
 *
 * @author MJ
 * @see com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file.FileChooserLmlAttribute */
public class FileChooserLmlTag extends VisWindowLmlTag {
    public FileChooserLmlTag(final LmlParser parser, final LmlTag parentTag, final StringBuilder rawTagData) {
        super(parser, parentTag, rawTagData);
    }

    @Override
    protected VisWindow getNewInstanceOfVisWindow(final VisWindowLmlActorBuilder builder) {
        final FileChooser fileChooser = new FileChooser(builder.getStyleName(), builder.getText(), Mode.OPEN);
        LmlUtilities.getLmlUserObject(fileChooser).setStageAttacher(new VisStageAttacher());
        fileChooser.setSkin(getSkin(builder));
        return fileChooser;
    }

    @Override
    public boolean isAttachable() {
        return true;
    }

    @Override
    public void attachTo(final LmlTag tag) {
        final Actor actor = tag.getActor();
        actor.addListener(new PopupAttacher((FileChooser) getActor(), actor));
    }
}
