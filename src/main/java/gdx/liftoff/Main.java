package gdx.liftoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import com.ray3k.stripe.*;
import com.ray3k.stripe.PopTable.PopTableStyle;
import gdx.liftoff.ui.RootTable;

import java.io.IOException;
import java.util.Properties;

public class Main extends ApplicationAdapter {
    public static Skin skin;
    public static Stage stage;
    public static ScreenViewport screenViewport;
    public static FitViewport fitViewport;
    public static SpriteBatch batch;
    public static RootTable root;
    public static SystemCursorListener handListener;
    public static SystemCursorListener ibeamListener;
    public static ScrollFocusListener scrollFocusListener;
    public static Color CLEAR_WHITE = new Color(1, 1, 1, 0);
    public static Image bgImage = new Image();
    public static boolean resizingWindow;
    public static Properties prop;
    private static GlyphLayout layout = new GlyphLayout();

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("GDX-Liftoff");
        config.useVsync(true);
        config.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        config.setWindowedMode(800, 800);
        config.setWindowSizeLimits(400,410, -1, -1);
        config.setWindowIcon("icons/libgdx128.png", "icons/libgdx64.png", "icons/libgdx32.png", "icons/libgdx16.png");
        new Lwjgl3Application(new Main(), config);
    }

    @Override
    public void create() {
        prop = new Properties();
        try {
            prop.load(Gdx.files.internal("i18n/nls.properties").read());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        skin = new Skin(Gdx.files.internal("ui-skin/skin.json"));

        fitViewport = new FitViewport(1920, 1080);
        screenViewport = new ScreenViewport();
        batch = new SpriteBatch();
        stage = new Stage(screenViewport, batch);

        Gdx.input.setInputProcessor(stage);

        handListener = new SystemCursorListener(SystemCursor.Hand);
        ibeamListener = new SystemCursorListener(SystemCursor.Ibeam);
        scrollFocusListener = new ScrollFocusListener(stage);

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

    public static void addScrollFocusListener(Actor actor) {
        actor.addListener(scrollFocusListener);
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

    public static PopTable addTooltip(Actor actor, int align, String text) {
        return addTooltip(actor, null, align, 0, text);
    }

    public static PopTable addTooltip(Actor actor, int align, float wrapWidth, String text) {
        return addTooltip(actor, null, align, wrapWidth,  text);
    }

    public static PopTable addTooltip(Actor actor, Actor attachedActor, int align, float wrapWidth, String text) {
        String style = (align & Align.bottom) != 0 ? "tooltip-arrow-up" : (align & Align.top) != 0  ? "tooltip-arrow-down" : (align & Align.left) != 0  ? "tooltip-arrow-right" : "tooltip-arrow-left";
        PopTableTextHoverListener listener = new PopTableTextHoverListener(text, wrapWidth, align, align, skin, style);
        listener.attachedActor = attachedActor;
        actor.addListener(listener);
        return listener.getPopTable();
    }

    public static PopTable addPopTableClickListener(Actor actor, int align, String description) {
        return addPopTableClickListener(actor, null, align, 0, description);
    }

    public static PopTable addPopTableClickListener(Actor actor, int align, float wrapWidth, String description) {
        return addPopTableClickListener(actor, null, align, wrapWidth,  description);
    }

    public static PopTable addPopTableClickListener(Actor actor, Actor attachedActor, int align, float wrapWidth, String text) {
        String style = (align & Align.bottom) != 0 ? "tooltip-arrow-up" : (align & Align.top) != 0  ? "tooltip-arrow-down" : (align & Align.left) != 0  ? "tooltip-arrow-right" : "tooltip-arrow-left";
        PopTableClickListener listener = new PopTableClickListener(align, align, skin, style);
        listener.attachedActor = attachedActor;
        actor.addListener(listener);
        PopTable pop = listener.getPopTable();

        Label label = new Label(text, skin, "tooltip");
        Cell cell = pop.add(label);
        if (wrapWidth != 0) {
            cell.width(wrapWidth);
        } else {
            layout.setText(label.getStyle().font, text);
            cell.minWidth(0).prefWidth(layout.width);
        }

        return pop;
    }
}
