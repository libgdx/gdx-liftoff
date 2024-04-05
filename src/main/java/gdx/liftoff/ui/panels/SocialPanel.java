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

        TextButton textButton = new TextButton(prop.getProperty("libgdxButton"), skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("libgdxTip"));
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("libGdxUrl")));

        textButton = new TextButton(prop.getProperty("discordButton"), skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("discordTip"));
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("discordUrl")));

        textButton = new TextButton(prop.getProperty("wikiButton"), skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("wikiTip"));
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("wikiUrl")));
    }
}
