package com.github.czyzby.autumn.mvc.component.ui.dto;

import com.github.czyzby.kiwi.util.gdx.file.CommonFileExtension;

/** Data holder of a single skin.
 *
 * @author MJ */
public class SkinData {
    private final String path;
    private final String[] fonts;
    private final String[] fontsNames;

    public SkinData(final String path, final String[] fonts, final String[] fontNames) {
        if (path.endsWith(CommonFileExtension.JSON)) {
            this.path = path.substring(0, path.length() - CommonFileExtension.JSON.length());
        } else {
            this.path = path;
        }
        this.fonts = fonts;
        fontsNames = fontNames;
    }

    /** @return internal path to the skin. */
    public String getPath() {
        return path;
    }

    /** @return internal paths to the fonts. */
    public String[] getFonts() {
        return fonts;
    }

    /** @return names of the fonts as they appear in the skin, in the order matching {@link #getFonts()} array. */
    public String[] getFontsNames() {
        return fontsNames;
    }
}
