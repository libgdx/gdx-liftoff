package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;

/** Constructs a {@link FileChooser} that will be shown after the widget is clicked (unless it's disabled). Attribute
 * expects a reference to a method that consumes a single {@link FileHandle} or an {@link Array} of files. File chooser
 * allows to select both files and directories. If the file chooser is cancelled, file handle-consuming method will
 * receive null; array-consuming method will receive empty array. Mapped to "fileAndDirectoryChooser", "anyFileChooser".
 *
 * @author MJ */
public class AnyFileChooserLmlAttribute extends FileChooserLmlAttribute {
    @Override
    protected SelectionMode getSelectionMode() {
        return SelectionMode.FILES_AND_DIRECTORIES;
    }
}
