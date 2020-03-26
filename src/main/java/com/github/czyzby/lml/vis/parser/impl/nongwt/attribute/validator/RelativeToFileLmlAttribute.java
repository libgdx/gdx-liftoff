package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.validator;

import java.io.File;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.util.form.FormValidator.FileExistsValidator;
import com.kotcrab.vis.ui.widget.VisTextField;

/** See {@link FileExistsValidator#setRelativeToFile(File)} and
 * {@link FileExistsValidator#setRelativeToTextField(VisTextField)}. Can handle both text field references and file
 * paths. Expects an ID of a {@link VisTextField} parsed BEFORE validator tag appears (as in: text field's tag was
 * already closed when validator's tag was opened). If text field is not found, it is assumed that the passed attribute
 * data is a path to a file. A new {@link File} is created with {@link File#File(String)} constructor and passed to the
 * validator with {@link FileExistsValidator#setRelativeToFile(File)}. Mapped to "relativeTo".
 *
 * @author MJ */
public class RelativeToFileLmlAttribute implements LmlAttribute<FileExistsValidator> {
    @Override
    public Class<FileExistsValidator> getHandledType() {
        return FileExistsValidator.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileExistsValidator actor,
            final String rawAttributeData) {
        final String fileId = parser.parseString(rawAttributeData, actor);
        if (parser.getActorsMappedByIds().containsKey(fileId)) {
            final Actor relative = parser.getActorsMappedByIds().get(fileId);
            if (relative instanceof VisTextField) {
                actor.setRelativeToTextField((VisTextField) relative);
            } else {
                parser.throwErrorIfStrict("Relative file attribute found ID: " + fileId + " referencing another actor: "
                        + relative + ", but he does not extend VisTextField.");
            }
        }
        actor.setRelativeToFile(new File(fileId));
    }
}
