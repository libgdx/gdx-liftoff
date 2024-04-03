package com.ray3k.stripe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.ray3k.stripe.PopTable.PopTableStyle;

public class PopTableTextHoverListener extends PopTableHoverListener {
    protected LabelStyle labelStyle;
    protected float wrapWidth;
    private static GlyphLayout layout = new GlyphLayout();

    public PopTableTextHoverListener(String text, float wrapWidth, int edge, int align, Skin skin) {
        this(text, wrapWidth, edge, align, skin, "default");
    }

    public PopTableTextHoverListener(String text, float wrapWidth, int edge, int align, Skin skin, String style) {
        this(text, wrapWidth, edge, align, createPopTableStyle(skin, style), createLabelStyle(skin, style));
    }

    public PopTableTextHoverListener(String text, float wrapWidth, int edge, int align, TextTooltipStyle style) {
        this(text, wrapWidth, edge, align, createPopTableStyle(style), style.label);
    }

    public PopTableTextHoverListener(String text, float wrapWidth, int edge, int align, PopTableStyle style, LabelStyle labelStyle) {
        super(edge, align, style);
        this.labelStyle = labelStyle;
        this.wrapWidth = wrapWidth;
        populate(text);
    }

    private static LabelStyle createLabelStyle(Skin skin, String style) {
        if (skin.has(style, LabelStyle.class)) {
            return skin.get(style, LabelStyle.class);
        }

        if (skin.has(style, TextTooltipStyle.class)) {
            TextTooltipStyle textTooltipStyle = skin.get(style, TextTooltipStyle.class);
            if (textTooltipStyle != null) {
                return textTooltipStyle.label;
            }
        }

        return null;
    }

    private static PopTableStyle createPopTableStyle(Skin skin, String style) {
        if (skin.has(style, PopTableStyle.class)) return skin.get(style, PopTableStyle.class);

        if (skin.has(style, TextTooltipStyle.class)) {
            TextTooltipStyle textTooltipStyle = skin.get(style, TextTooltipStyle.class);
            if (textTooltipStyle != null) {
                PopTableStyle popTableStyle = new PopTableStyle();
                popTableStyle.background = textTooltipStyle.background;
                return popTableStyle;
            }
        }

        if (skin.has(style, WindowStyle.class)) {
            WindowStyle windowStyle = skin.get(style, WindowStyle.class);
            if (windowStyle != null) {
                return new PopTableStyle(windowStyle);
            }
        }
        return null;
    }

    private static PopTableStyle createPopTableStyle(TextTooltipStyle style) {
        PopTableStyle popTableStyle = new PopTableStyle();
        popTableStyle.background = style.background;
        return popTableStyle;
    }

    private void populate(String text) {
        Label label = new Label(text, labelStyle);
        label.setWrap(true);
        Cell cell = getPopTable().add(label);
        if (wrapWidth != 0) {
            cell.width(wrapWidth);
        } else {
            layout.setText(label.getStyle().font, text);
            cell.minWidth(0).prefWidth(layout.width);
        }
    }
}
