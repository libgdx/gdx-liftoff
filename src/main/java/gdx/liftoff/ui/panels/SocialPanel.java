package gdx.liftoff.ui.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import gdx.liftoff.Main;

import static gdx.liftoff.Main.*;

public class SocialPanel extends Table {
    public SocialPanel() {
        defaults().space(15);

        TextButton textButton = new TextButton("LIBGDX.COM", skin, "link");
        add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {

        });

        textButton = new TextButton("DISCORD", skin, "link");
        add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {

        });

        textButton = new TextButton("WIKI", skin, "link");
        add(textButton);
        addHandListener(textButton);
        onChange(textButton, () -> {

        });
    }
}
