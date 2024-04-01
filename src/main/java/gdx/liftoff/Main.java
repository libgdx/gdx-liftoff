package gdx.liftoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTableHoverListener;
import com.ray3k.stripe.SystemCursorListener;
import gdx.liftoff.ui.RootTable;

public class Main extends ApplicationAdapter {
    public static Skin skin;
    public static Stage stage;
    public static ScreenViewport screenViewport;
    public static FitViewport fitViewport;
    public static SpriteBatch batch;
    public static RootTable root;
    public static SystemCursorListener handListener;
    public static SystemCursorListener ibeamListener;
    public static Color CLEAR_WHITE = new Color(1, 1, 1, 0);
    public static Image bgImage = new Image();
    public static boolean resizingWindow;

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

        fitViewport = new FitViewport(1920, 1080);
        screenViewport = new ScreenViewport();
        batch = new SpriteBatch();
        stage = new Stage(screenViewport, batch);

        Gdx.input.setInputProcessor(stage);

        handListener = new SystemCursorListener(SystemCursor.Hand);
        ibeamListener = new SystemCursorListener(SystemCursor.Ibeam);

        bgImage = new Image(skin, "bg");
        bgImage.setFillParent(true);
        bgImage.setScaling(Scaling.fill);
        stage.addActor(bgImage);

        root = new RootTable();
        root.setFillParent(true);
        stage.addActor(root);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);

        //draw stage
        stage.getViewport().apply();
        stage.act();
        stage.draw();

        resizingWindow = false;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        resizingWindow = true;
    }

    public static void addHandListener(Actor actor) {
        actor.addListener(handListener);
    }

    public static void addIbeamListener(Actor actor) {
        actor.addListener(ibeamListener);
    }

    public static void onChange(Actor actor, Runnable runnable) {
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        });
    }

    public static void onClick(Actor actor, Runnable runnable) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runnable.run();
            }
        });
    }

    public static PopTable addTooltip(Actor actor, int align, String description) {
        return addTooltip(actor, null, align, 0, description);
    }

    public static PopTable addTooltip(Actor actor, int align, float wrapWidth, String description) {
        return addTooltip(actor, null, align, wrapWidth,  description);
    }

    public static PopTable addTooltip(Actor actor, Actor attachedActor, int align, float wrapWidth, String description) {
        String style = align == Align.bottom ? "tooltip-arrow-up" : align == Align.top ? "tooltip-arrow-down" : align == Align.left ? "tooltip-arrow-right" : "tooltip-arrow-left";
        PopTableHoverListener listener = new PopTableHoverListener(align, align, skin, style);
        listener.attachedActor = attachedActor;
        actor.addListener(listener);

        PopTable pop = listener.getPopTable();

        Label label = new Label(description, skin, "tooltip");
        Cell cell = pop.add(label);
        if (wrapWidth != 0) {
            label.setWrap(true);
            cell.width(wrapWidth);
        }

        return pop;
    }
}
