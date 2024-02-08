# Guide to HTML Deployment with GWT

**NOTE:** This guide has been mostly superseded by [this libGDX wiki page](https://libgdx.com/wiki/html5-backend-and-gwt-specifics).
It is here as just another source that might help.

Google Web Toolkit is nice to have because it gives libGDX applications the option to deploy to a web target.
But, it's incredibly finicky. All sorts of parts of a program can act differently when GWT is the target.
gdx-liftoff allows the HTML platform to be selected on the first screen, and when the project is generated, the
Gradle tasks for the `:html` project will include `gwt/superDev` and `other/dist`; the first is preferred during
development and the second is meant for building a distributable single-page application. When you use superDev,
the GWT compiler will run for a short while compiling your application code, then you will be presented with
some links. Those links are probably not what you'd expect. Under normal circumstances:
 
  - [Your application will run at http://localhost:8080/index.html with superDev.](http://localhost:8080/index.html)
  - [You can get important info at http://localhost:9876/ , including the bookmarklets that let you recompile after changing the code.](http://localhost:9876/) 

In SuperDev, you may need to click the circular arrow button in the upper left to reload changes, or to use the
"SuperDev Mode On" bookmarklet (either of which you can do after just saving your files in your editor, it doesn't need
you to stop and re-run a task if you recompile using the button in the webpage or the bookmarklet).

When you use dist, the compiler may take significantly longer, but will produce much faster code. It will output
a static webpage in `html/build/dist`; all the files inside that `dist` folder can be copied to a web server such
as GitHub Pages and can be served statically. You can edit the files in the `html/webapp` folder to change the
appearance and content of the page. It should be emphasized that if your project seems unusually slow when using
superDev, it might not be slow at all when using dist. If checking the performance of your app, dist is absolutely
what you should be using.

As mentioned before, GWT tends to act rather differently in some key ways. Basic numeric behavior isn't identical, and
this can be very bad in procedurally-generated games where a seed should produce a specific result. There's usually a
way to work around GWT's oddities, though.

  - `long` is emulated by GWT, producing identical results for math with that type on HTML and desktop (it's much
    slower, though), but a `long` field can't be seen by reflection, making libGDX's `Json` class and
    other reflection-using classes unable to automatically save a long (it can be done manually).
  - `int` has its own issues, since GWT will internally represent an `int` with a JavaScript `Number`, and that's
    effectively a `double`. Math with `int` is as fast as it gets on HTML, but instead of a result overflowing
    numerically, which all other platforms do in a standardized way (on desktop, Android, and iOS, you can rely on
    `Integer.MAX_VALUE + 1 == Integer.MIN_VALUE` being `true`), the value will go up to a number that can't be
    written in Java code as one value. This Number will print as being larger than `Integer.MAX_VALUE` or smaller than
    `Integer.MIN_VALUE`, and, if it gets far enough away from 0, will lose precision, eventually being unable to
    represent large spans of valid integers. Initially, this manifests as very large odd numbers being impossible to
    store, meaning something like `i++` in a loop just won't change `i`.
    - You can force an int/Number that has gone out-of-range back into a 32-bit value between `Integer.MIN_VALUE` and
      `Integer.MAX_VALUE` by using any bitwise math on it. The simplest thing to recommend is when a value `int over;` 
      has potentially overflowed, to assign it `over = over | 0;`, which works like overflow on desktop as long as
      `over` hasn't already lost precision from going too far from 0.
    - Large multipliers should be used carefully with ints on GWT; if a multiplier is larger than about 2000000
      (0x1fffff, specifically) or smaller than about -2000000, and it is multiplied by an arbitrary int (one which can
      be any 32-bit value), then precision loss may occur even if you do a bitwise operation afterward.
  - The third-party extension `digital` provides access to some of JavaScript's built-in functions that help resolve
    parts of this problem.
    - You can use `BitConversion.imul(int, int)` to multiply any two `int`s and get an int, and it will stay in-range.
    - There's `BitConversion.lowestOneBit(long)`, which fixes broken behavior by GWT for some inputs.
    - `BitConversion.countLeadingZeros(int)` is equivalent to `Integer.numberOfLeadingZeros(int)` on other platforms,
      but on GWT it calls JavaScript's built-in `Math.clz32()` function, which should be much faster than imitating what
      it does in GWT's `Integer` class.
  - Some standard library functions in GWT have unexpected quirks to their implementation, or lack thereof.
    - `String.format()`, and in fact anything that uses `Formatter`, is just not present.
      - You can partly get around this by using the third-party extension Formic, which provides a `Stringf.format()`
        function that acts exactly like `String.format()` on most platforms, or mostly like it on GWT.
    - Regular expressions are poorly-supported by GWT out-of-the-box, though libGDX adds part of the `java.util.regex`
      package to the standard library. When regular expressions are available, they use JS behavior on GWT, and regular
      desktop-JVM behavior on desktop. [JS behavior is missing some key features.](https://www.regular-expressions.info/javascript.html)
      - The third-party extension RegExodus provides an alternative regular expression API that works the same on GWT
        and other platforms, and adds a few other features that aren't in `java.util.regex`.
    - `Long.rotateLeft(long, int)`, `Long.rotateRight(long, int)`, `Integer.rotateLeft(int, int)`, and
      `Integer.rotateRight(int, int)` all work in GWT, but are implemented in a bizarre and very slow way.
      You should just write these inline if you intend to use them; their replacements are:
      - `Long.rotateLeft(num, amt)` is equivalent to `(num << amt | num >>> 64 - amt)`.
      - `Long.rotateRight(num, amt)` is equivalent to `(num << 64 - amt | num >>> amt)`.
      - `Integer.rotateLeft(num, amt)` is equivalent to `(num << amt | num >>> 32 - amt)`.
      - `Integer.rotateRight(num, amt)` is equivalent to `(num << 32 - amt | num >>> amt)`.

GWT has had a few new releases since GDX-Liftoff's debut. For a very long time, libGDX used GWT 2.8.2, which was
compatible up to Java 8. This is what's used by libGDX 1.12.1, unless you use the alternative backend that Liftoff
defaults to. The alternative backend is built on MrStahlfelge's work isolating the libGDX backends for easier expansion,
and it currently can use GWT 2.10.0 or 2.11.0 with libGDX 1.12.1 . Both of these newer GWT versions are compatible with
Java 11 to some extent, allowing you to use `var` and parts of the new API in Java 11.

[The part of the standard library that GWT 2.8.2 emulates can be viewed here.](https://github.com/gwtproject/gwt-site/blob/44d0195c987929871fb4710337bae74d195be1e4/src/main/markdown/doc/latest/RefJreEmulation.md)

[The part of the standard library that GWT 2.11.0 emulates can be viewed here.](https://github.com/gwtproject/gwt-site/blob/becd13c39f53d45e55233ac29c8dfa7ebcf92dca/src/main/markdown/doc/latest/RefJreEmulation.md)
