package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.action.VisStageAttacher.PopupAttacher;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;

/** Constructs a {@link FileChooser} that will be shown after the widget is clicked (unless it's disabled). Attribute
 * expects a reference to a method that consumes a single {@link FileHandle} or an {@link Array} of files. File chooser
 * allows to select files (not directories). If the file chooser is cancelled, file handle-consuming method will receive
 * null; array-consuming method will receive empty array. Mapped to "fileChooser".
 *
 * @author MJ
 * @see DirectoryChooserLmlAttribute
 * @see AnyFileChooserLmlAttribute */
public class FileChooserLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final FileChooser fileChooser = new FileChooser(Mode.OPEN);
        fileChooser.setSelectionMode(getSelectionMode());
        FileChooserListenerLmlAttribute.addFileChooserListener(parser, rawAttributeData, fileChooser);
        actor.addListener(new PopupAttacher(fileChooser, actor));
    }

    /** @return type of file chooser selection mode. */
    protected SelectionMode getSelectionMode() {
        return SelectionMode.FILES;
    }
}
