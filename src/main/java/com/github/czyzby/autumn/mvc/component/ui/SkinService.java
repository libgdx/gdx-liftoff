package com.github.czyzby.autumn.mvc.component.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import com.github.czyzby.autumn.annotation.Inject;
import com.github.czyzby.autumn.mvc.component.asset.AssetService;
import com.github.czyzby.autumn.mvc.component.ui.dto.SkinData;
import com.github.czyzby.autumn.mvc.component.ui.processor.SkinAnnotationProcessor;
import com.github.czyzby.autumn.mvc.component.ui.processor.SkinAssetAnnotationProcessor;
import com.github.czyzby.autumn.mvc.config.AutumnActionPriority;
import com.github.czyzby.autumn.mvc.config.AutumnMessage;
import com.github.czyzby.autumn.processor.event.MessageDispatcher;
import com.github.czyzby.kiwi.util.gdx.asset.Disposables;
import com.github.czyzby.kiwi.util.gdx.collection.disposable.DisposableArray;
import com.github.czyzby.kiwi.util.gdx.file.CommonFileExtension;

/** Manages application's {@link Skin}.
 *
 * @author MJ */
public class SkinService {
    @Inject InterfaceService interfaceService;
    final DisposableArray<Skin> skins = DisposableArray.newArray();

    @Initiate(priority = AutumnActionPriority.TOP_PRIORITY)
    private void initiateSkin(final SkinAssetAnnotationProcessor skinAssetProcessor,
            final SkinAnnotationProcessor skinProcessor, final AssetService assetService,
            final MessageDispatcher messageDispatcher) {
        final ObjectMap<String, SkinData> skinsData = skinProcessor.getSkinsData();
        for (final Entry<String, SkinData> skinData : skinsData) {
            final Skin skin = initiateSkin(skinAssetProcessor, skinData.value, assetService, messageDispatcher);
            skins.add(skin);
            interfaceService.getParser().getData().addSkin(skinData.key, skin);
        }
        messageDispatcher.postMessage(AutumnMessage.SKINS_LOADED);
    }

    private static Skin initiateSkin(final SkinAssetAnnotationProcessor skinAssetProcessor, final SkinData skinData,
            final AssetService assetService, final MessageDispatcher messageDispatcher) {
        final Skin skin = new Skin();
        final String atlasPath = skinData.getPath() + CommonFileExtension.ATLAS;
        assetService.load(atlasPath, TextureAtlas.class);
        final TextureAtlas skinAtlas = assetService.finishLoading(atlasPath, TextureAtlas.class);

        final String[] fontPaths = skinData.getFonts();
        loadFonts(atlasPath, fontPaths, assetService);
        skin.addRegions(skinAtlas);
        assignFonts(skin, skinData, fontPaths, assetService);

        skin.load(Gdx.files.internal(skinData.getPath() + CommonFileExtension.JSON));
        return skin;
    }

    private static void loadFonts(final String atlasPath, final String[] fontPaths, final AssetService assetService) {
        if (fontPaths.length != 0) {
            final BitmapFontParameter loadingParameters = new BitmapFontParameter();
            loadingParameters.atlasName = atlasPath;
            for (final String fontPath : fontPaths) {
                assetService.finishLoading(fontPath, BitmapFont.class, loadingParameters);
            }
        }
    }

    private static void assignFonts(final Skin skin, final SkinData skinData, final String[] fontPaths,
            final AssetService assetService) {
        if (fontPaths.length != 0) {
            final String[] fontNames = skinData.getFontsNames();
            for (int fontIndex = 0; fontIndex < fontPaths.length; fontIndex++) {
                skin.add(fontNames[fontIndex], assetService.get(fontPaths[fontIndex], BitmapFont.class),
                        BitmapFont.class);
            }
        }
    }

    /** @return application's main {@link Skin} used to build views. */
    public Skin getSkin() {
        return interfaceService.getParser().getData().getDefaultSkin();
    }

    /** @param id ID of the requested skin. By default, case is ignored.
     * @return {@link Skin} with the selected ID. */
    public Skin getSkin(final String id) {
        return interfaceService.getParser().getData().getSkin(id);
    }

    /** @param id ID of the skin. By default, case is ignored.
     * @param skin will be registered in LML parser and disposed by this service when the application is closed. */
    public void addSkin(final String id, final Skin skin) {
        skins.add(skin);
        interfaceService.getParser().getData().addSkin(id, skin);
    }

    /** @return internally stored array of all current skins. */
    public DisposableArray<Skin> getSkins() {
        return skins;
    }

    /** Removes all internally stored skins. Does not affect LML parser. */
    public void clear() {
        skins.clear();
    }

    @Destroy(priority = AutumnActionPriority.VERY_LOW_PRIORITY)
    private void dispose() {
        Disposables.gracefullyDisposeOf((Disposable) skins);
    }
}
