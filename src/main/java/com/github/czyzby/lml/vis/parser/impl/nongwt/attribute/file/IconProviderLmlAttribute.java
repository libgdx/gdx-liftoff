package com.github.czyzby.lml.vis.parser.impl.nongwt.attribute.file;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.FileIconProvider;

/** See {@link FileChooser#setIconProvider(FileIconProvider)}. Expects ID of an action that references a method
 * consuming {@link FileChooser} and returning a {@link FileIconProvider}. Mapped to "iconProvider".
 *
 * @author MJ */
public class IconProviderLmlAttribute implements LmlAttribute<FileChooser> {
    @Override
    public Class<FileChooser> getHandledType() {
        return FileChooser.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final FileChooser actor,
            final String rawAttributeData) {
        @SuppressWarnings("unchecked") final ActorConsumer<FileIconProvider, FileChooser> provider = (ActorConsumer<FileIconProvider, FileChooser>) parser
                .parseAction(rawAttributeData, actor);
        if (provider == null) {
            parser.throwError(
                    "Icon provider requires an action ID of method returning FileIconProvider. No action found for data: "
                            + rawAttributeData);
        }
        actor.setIconProvider(provider.consume(actor));
    }
}
