package gdx.liftoff.ui.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import static gdx.liftoff.Main.*;

/**
 * A table to display libGDX related links including libgdx.com, Discord, the wiki, and a recommended JDK.
 */
public class SocialPanel extends Table implements Panel {
    public SocialPanel(boolean fullscreen) {
        populate(fullscreen);
    }

    @Override
    public void populate(boolean fullscreen) {
        defaults().space(SPACE_MEDIUM);

        //libgdx.com
        TextButton textButton = new TextButton(prop.getProperty("libgdxButton"), skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("libgdxTip"));
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("libGdxUrl")));

        //discord
        textButton = new TextButton(prop.getProperty("discordButton"), skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("discordTip"));
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("discordUrl")));

        //wiki
        textButton = new TextButton(prop.getProperty("wikiButton"), skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("wikiTip"));
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("wikiUrl")));

        //jdk
        textButton = new TextButton(prop.getProperty("jdkButton"), skin, "link");
        add(textButton);
        addHandListener(textButton);
        addTooltip(textButton, Align.top, prop.getProperty("jdkTip"));
        onChange(textButton, () -> Gdx.net.openURI(prop.getProperty("jdkUrl")));
    }

    @Override
    public void captureKeyboardFocus() {

    }
}
