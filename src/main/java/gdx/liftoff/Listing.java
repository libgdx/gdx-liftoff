package gdx.liftoff;

import gdx.liftoff.data.languages.Groovy;
import gdx.liftoff.data.languages.Kotlin;
import gdx.liftoff.data.languages.Language;
import gdx.liftoff.data.languages.Scala;
import gdx.liftoff.data.libraries.Library;
import gdx.liftoff.data.libraries.official.*;
import gdx.liftoff.data.libraries.unofficial.*;
import gdx.liftoff.data.platforms.*;
import gdx.liftoff.data.templates.Template;
import gdx.liftoff.data.templates.official.*;
import gdx.liftoff.data.templates.unofficial.*;

import java.util.*;

import static gdx.liftoff.Maker.*;

/**
 * Fully-static class that exists so that we don't have to deal with Autumn stupid-magic from Java.
 */
public final class Listing {
    private Listing() {
    }

    public static final ArrayList<Platform> platforms = makeList(
        new Android(),
        new Assets(),
        new Core(),
        new GWT(),
        new Headless(),
        new IOS(),
        new IOSMOE(),
        new Lwjgl2(),
        new Lwjgl3(),
        new Server(),
        new Shared(),
        new TeaVM()
    );
    public static final LinkedHashMap<String, Platform> platformsByName = new LinkedHashMap<>(platforms.size());

    static {
        for (Platform p : platforms) {
            platformsByName.put(p.getId(), p);
        }
    }

    public static final ArrayList<Language> languages = makeList(
        new Kotlin(), new Groovy(), new Scala()
    );

    public static final LinkedHashMap<String, String> languageVersions = new LinkedHashMap<>(languages.size());

    static {
        for (Language l : languages) {
            languageVersions.put(l.getId(), l.getVersion());
        }
    }

    public static ArrayList<Language> chooseLanguages(Collection<String> names) {
        ArrayList<Language> cpy = new ArrayList<>(languages.size());
        for (Language l : languages) {
            if (names.contains(l.getId()))
                cpy.add(l);
        }
        return cpy;
    }

    public static final ArrayList<Library> officialLibraries = makeList(
        new AI(),
        new Ashley(),
        new Box2D(),
        new Box2DLights(),
        new Bullet(),
        new Controllers(),
        new Freetype(),
        new Tools()
    );
    public static final TreeSet<Library> unofficialLibraries = new TreeSet<>(Comparator.comparing(Library::getId));

    static {
        unofficialLibraries.addAll(makeList(
            new ArtemisOdb(),
            new LibgdxUtils(),
            new LibgdxUtilsBox2D(),
            new Facebook(),
            new Dialogs(),
            new Fleks(),
            new InGameConsole(),
            new Jaci(),
            new JaciGwt(),
            new KotlinxCoroutines(),
            new Noise4J(),
            new BladeInk(),
            new Joise(),
            new MakeSomeNoise(),
            new TypingLabel(),
            new TextraTypist(),
            new ShapeDrawer(),
            new SimpleGraphs(),
            new Formic(),
            new Colorful(),
            new Anim8(),
            new TenPatch(),
            new Stripe(),
            new GdxGltf(),
            new HackLights(),
            new SpineRuntime(),
            new ControllerUtils(),
            new ControllerScene2D(),
            new ControllerMapping(),
            new GdxVfxCore(),
            new GdxVfxStandardEffects(),
            new RegExodus(),
            new VisUI(),
            new PieMenu(),
            new JBump(),
            new CommonsCollections(),
            new Fury(),
            new Kryo(),
            new KryoNet(),
            new Guacamole(),
            new LibgdxOboe(),
            new LibgdxScreenManager(),
            new TuningFork(),
            new TinyVG(),
            new GdxPsx(),
            new GdxFlexBox(),
            new GdxUnBox2D(),
            new GdxBasisUniversal(),
            new Lombok(),
            new HyperLap2DRuntime(),
            new HyperLap2DSpineExtension(),
            new HyperLap2DTinyVGExtension(),
            new HyperLap2DTypingLabelExtension(),
            new GdxMiniAudio(),
            new UniversalTween(),
            new Crux(),
            new Gand(),
            new Cringe(),
            new Digital(),
            new Funderby(),
            new Juniper(),
            new Jdkgdxds(),
            new JdkgdxdsInterop(),
            new KryoRegExodus(),
            new KryoDigital(),
            new KryoJuniper(),
            new KryoJdkgdxds(),
            new KryoCringe(),
            new KryoGand(),
            new TantrumLibgdx(),
            new TantrumRegExodus(),
            new TantrumDigital(),
            new TantrumJdkgdxds(),

            new SquidLibUtil(),
            new SquidLib(),
            new SquidLibExtra(),
            new SquidSquadCore(),
            new SquidSquadGrid(),
            new SquidSquadPath(),
            new SquidSquadPlace(),
            new SquidSquadSmooth(),
            new SquidSquadWorld(),
            new SquidSquadGlyph(),
            new SquidSquadOld(),
            new SquidSquadText(),
            new SquidSquadPress(),
            new SquidSquadStoreCore(),
            new SquidSquadStoreGrid(),
            new SquidSquadStoreOld(),
            new SquidSquadStorePath(),
            new SquidSquadStoreText(),
            new SquidSquadFreezeCore(),
            new SquidSquadFreezeGrid(),
            new SquidSquadFreezeOld(),
            new SquidSquadFreezePath(),
            new SquidSquadFreezeText(),
            new SquidSquadWrathCore(),
            new SquidSquadWrathGrid(),
            new SquidSquadWrathOld(),
            new SquidSquadWrathPath(),
            new SquidSquadWrathText(),

            new Kiwi(),
            new LML(),
            new LMLVis(),
            new Autumn(),
            new AutumnMVC(),
            new WebSocket(),
            new WebSocketSerialization(),

            new KtxActors(),
            new KtxApp(),
            new KtxAi(),
            new KtxArtemis(),
            new KtxAshley(),
            new KtxAssets(),
            new KtxAssetsAsync(),
            new KtxAsync(),
            new KtxBox2D(),
            new KtxCollections(),
            new KtxFreetype(),
            new KtxFreetypeAsync(),
            new KtxGraphics(),
            new KtxI18n(),
            new KtxInject(),
            new KtxJson(),
            new KtxLog(),
            new KtxMath(),
            new KtxPreferences(),
            new KtxReflect(),
            new KtxScene2D(),
            new KtxStyle(),
            new KtxTiled(),
            new KtxVis(),
            new KtxVisStyle()
        ));
    }

    public static final LinkedHashMap<String, Library> officialByName = new LinkedHashMap<>(officialLibraries.size());

    static {
        for (Library l : officialLibraries) {
            officialByName.put(l.getId(), l);
        }
    }

    public static ArrayList<Library> chooseOfficialLibraries(Collection<String> names) {
        LinkedHashMap<String, Library> cpy = new LinkedHashMap<>(officialByName);
        cpy.keySet().retainAll(names);
        return new ArrayList<>(cpy.values());
    }

    public static final LinkedHashMap<String, Library> unofficialByName = new LinkedHashMap<>(unofficialLibraries.size());
    static {
        for (Library l : unofficialLibraries) {
            unofficialByName.put(l.getId(), l);
        }
    }
    public static final LinkedHashSet<String> unofficialNames = new LinkedHashSet<>(unofficialByName.keySet());

    public static ArrayList<Library> chooseUnofficialLibraries(Collection<String> names) {
        LinkedHashMap<String, Library> cpy = new LinkedHashMap<>(unofficialByName);
        cpy.keySet().retainAll(names);
        return new ArrayList<>(cpy.values());
    }

    public static final ArrayList<Template> templates = makeList(
        new ClassicTemplate(),
        new ApplicationAdapterTemplate(),
        new ApplicationListenerTemplate(),
        new EmptyTemplate(),
        new GameTemplate(),
        new InputProcessorTemplate(),
        new KotlinBasicTemplate(),
        new KotlinClassicTemplate(),
        new Scene2DTemplate(),
        new SuperKoalioTemplate(),

        new AutumnMvcBasicTemplate(),
        new AutumnMvcBox2dTemplate(),
        new AutumnMvcVisTemplate(),
        new KiwiInputTemplate(),
        new KiwiTemplate(),
        new KtxTemplate(),
        new LmlTemplate(),
        new Noise4JTemplate(),
        new VisUIBasicTemplate(),
        new VisUIShowcaseTemplate()
    );
    public static final LinkedHashMap<String, Template> templatesByName = new LinkedHashMap<>(templates.size());

    static {
        for (Template t : templates) {
            templatesByName.put(t.getId(), t);
        }
    }
}
