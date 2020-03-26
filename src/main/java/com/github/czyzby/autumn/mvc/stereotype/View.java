package com.github.czyzby.autumn.mvc.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.badlogic.gdx.Files.FileType;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewInitializer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewPauser;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewRenderer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewResizer;
import com.github.czyzby.autumn.mvc.component.ui.controller.ViewShower;
import com.github.czyzby.autumn.mvc.component.ui.dto.ThemeOrdering;

/** Should annotate classes that manage a single view. Partial control over how the view is managed can be gained by
 * implementing a {@link ViewInitializer},
 * {@link ViewRenderer},
 * {@link ViewResizer},
 * {@link ViewShower} or
 * {@link ViewPauser} and passing it in appropriate annotation
 * parameter. Full control can be taken by implementing
 * {@link com.github.czyzby.autumn.mvc.component.ui.controller.ViewController} interface - all partial control classes
 * will be ignored and view management has to be implemented manually. (Be careful though, as it sacrifices a lot of
 * features.)
 *
 * @author MJ */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface View {
    /** @return path to the LML view file. */
    String value();

    /** @return type of view's file. Defaults to internal. */
    FileType fileType() default FileType.Internal;

    /** @return class of the renderer used to draw the view. Note that the annotated component can implement this
     *         interface by itself and doesn't not have to pass its class, as it will be immediately recognized - this
     *         parameter should be set only if the implementation is meant to be in another class. The chosen class will
     *         be created using no-arg constructor. */
    Class<? extends ViewRenderer>renderer() default ViewRenderer.class;

    /** @return class of the resizer used to resize the view. Note that the annotated component can implement this
     *         interface by itself and doesn't not have to pass its class, as it will be immediately recognized - this
     *         parameter should be set only if the implementation is meant to be in another class. The chosen class will
     *         be created using no-arg constructor. */
    Class<? extends ViewResizer>resizer() default ViewResizer.class;

    /** @return class of the pauser used to pause and resume the view. Note that the annotated component can implement
     *         this interface by itself and doesn't not have to pass its class, as it will be immediately recognized -
     *         this parameter should be set only if the implementation is meant to be in another class. The chosen class
     *         will be created using no-arg constructor. */
    Class<? extends ViewPauser>pauser() default ViewPauser.class;

    /** @return class of the shower used to show and hide the view. Note that the annotated component can implement this
     *         interface by itself and doesn't not have to pass its class, as it will be immediately recognized - this
     *         parameter should be set only if the implementation is meant to be in another class. The chosen class will
     *         be created using no-arg constructor. */
    Class<? extends ViewShower>shower() default ViewShower.class;

    /** @return class of the initializer, triggered each time the view is created (might trigger multiple times if views
     *         are reloaded). Note that the annotated component can implement this interface by itself and doesn't not
     *         have to pass its class, as it will be immediately recognized - this parameter should be set only if the
     *         implementation is meant to be in another class. The chosen class will be created using no-arg
     *         constructor. */
    Class<? extends ViewInitializer>initializer() default ViewInitializer.class;

    /** @return true if the screen is initial. There should be only one initial screen. Defaults to false. */
    boolean first() default false;

    /** @return ID of the action container extracted if the annotated class implements
     *         {@link com.github.czyzby.lml.parser.action.ActionContainer} and ID of the screen used for screen
     *         transitions from within views. */
    String id() default "default";

    /** @return list of paths to music files played while the screen is shown. By default, themes are chosen at random;
     *         if there are multiple themes, there is no possibility of one theme played twice in a row. Ordering can be
     *         changed with {@link View#themeOrdering()}. When view is changed to another, current theme: a) slowly
     *         lowers its volume and another theme is played, b) continues to play if it is also among available themes
     *         for the next view. Note that this behavior might change if the default screen transition actions are
     *         changed. */
    String[]themes() default {};

    /** @return if true, music themes are loaded at once, when the controller is constructed. If false, music themes
     *         will be scheduled to be loaded and injected when the loading is finished. Defaults to false. */
    boolean loadThemesEagerly() default false;

    /** @return determines the way next theme is determined. Defaults to random. */
    ThemeOrdering themeOrdering() default ThemeOrdering.RANDOM;

    /** @return if true, {@link com.github.czyzby.lml.util.LmlUtilities#clearLmlUserObjects(Iterable)} will be called
     *         after view parsing to clear LML data. If you need these objects or somehow managed to create a view with
     *         circular actor references and this causes problems, set this to false. */
    boolean clearLmlMetaData() default true;
}
