package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import static gdx.liftoff.Main.*;
import static gdx.liftoff.ui.data.Data.*;

public class SocialPanel extends Table {
    public SocialPanel() {
        defaults().space(15);

        TextButton textButton = new TextButton("LIBGDX.COM", skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, libgdxTooltipDescription);
        onChange(textButton, () -> Gdx.net.openURI(libgdxURL));

        textButton = new TextButton("DISCORD", skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, discordTooltipDescription);
        onChange(textButton, () -> Gdx.net.openURI(discordURL));

        textButton = new TextButton("WIKI", skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, wikiTooltipDescription);
        onChange(textButton, () -> Gdx.net.openURI(wikiURL));
    }
}
