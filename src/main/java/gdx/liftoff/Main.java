package gdx.liftoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import gdx.liftoff.ui.RootTable;

public class Main extends ApplicationAdapter {
    public static Skin skin;
    public static Stage stage;
    public static ScreenViewport screenViewport;
    public static FitViewport fitViewport;
    public static FillViewport bgViewport;
    public static SpriteBatch batch;
    private TextureRegion bgTextureRegion;

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
        skin = new Skin(Gdx.files.internal("ui-skin/skin.json"));
        bgTextureRegion = skin.getRegion("bg");

        screenViewport = new ScreenViewport();
        fitViewport = new FitViewport(1920, 1080);
        bgViewport = new FillViewport(1920, 1080);
        batch = new SpriteBatch();
        stage = new Stage(screenViewport, batch);

        Gdx.input.setInputProcessor(stage);

        RootTable root = new RootTable();
        root.setFillParent(true);
        stage.addActor(root);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);

        //draw background
        bgViewport.apply();
        batch.setProjectionMatrix(bgViewport.getCamera().combined);
        batch.begin();
        batch.draw(bgTextureRegion, 0, 0);
        batch.end();

        //draw stage
        stage.getViewport().apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        bgViewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }
}
