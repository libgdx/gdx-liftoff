package gdx.liftoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import gdx.liftoff.ui.RootTable;

public class Main extends ApplicationAdapter {
    public static Stage stage;
    public static Skin skin;
    public static ScreenViewport screenViewport;
    public static FitViewport fitViewport;
    public static FillViewport bgViewport;

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("GDX-Liftoff");
        config.useVsync(true);
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        config.setWindowedMode(800, 800);
        config.setWindowIcon("icons/libgdx128.png", "icons/libgdx64.png", "icons/libgdx32.png", "icons/libgdx16.png");
        new Lwjgl3Application(new Main(), config);
    }

    @Override
    public void create() {
        screenViewport = new ScreenViewport();
        fitViewport = new FitViewport(1024, 576);
        bgViewport = new FillViewport(1024, 576);
        stage = new Stage(screenViewport);

        Gdx.input.setInputProcessor(stage);

        RootTable root = new RootTable();
        root.setFillParent(true);
        stage.addActor(root);
    }

    @Override
    public void render() {
        stage.act();
        stage.draw();
    }
}
