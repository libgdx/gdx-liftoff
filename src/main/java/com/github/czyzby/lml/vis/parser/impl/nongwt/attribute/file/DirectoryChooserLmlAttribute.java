package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.vis.parser.impl.action.VisStageAttacher;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;

/** Constructs a {@link FileChooser} that will be shown after the widget is clicked (unless it's disabled). Attribute
 * expects a reference to a method that consumes a single {@link FileHandle} or an {@link Array} of files. File chooser
 * allows to select only directories. If the file chooser is cancelled, file handle-consuming method will receive null;
 * array-consuming method will receive empty array. This can select hidden directories. Mapped to "directoriesChooser".
 *
 * @author MJ */
public class DirectoryChooserLmlAttribute extends FileChooserLmlAttribute {
    @Override
    protected SelectionMode getSelectionMode() {
        return SelectionMode.DIRECTORIES;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, Actor actor, String rawAttributeData) {
        final FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(getSelectionMode());
        fileChooser.setFileFilter(file -> true);
        FileChooserListenerLmlAttribute.addFileChooserListener(parser, rawAttributeData, fileChooser);
        actor.addListener(new VisStageAttacher.PopupAttacher(fileChooser, actor));
    }
}
