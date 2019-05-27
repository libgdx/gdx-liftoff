Guide to HTML Deployment with GWT
#####

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
as Github Pages and can be served statically. You can edit the files in the `html/webapp` folder to change the
appearance and content of the page.

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
    written in Java code as one value, will print as being larger than `Integer.MAX_VALUE` or smaller than
    `Integer.MIN_VALUE`, and, if it gets far enough away from 0, will lose precision, eventually being unable to
    represent large spans of valid integers. You can force a Number that has gone out-of-range back into a 32-bit
    value between `Integer.MIN_VALUE` and `Integer.MAX_VALUE` by using any bitwise math on it. The simplest thing
    to recommend is when a value `int over;` has potentially overflowed, to assign it `over = over | 0`, which
    works like overflow on desktop as long as `over` hasn't already lost precision from going too far from 0.
    Multipliers should be used carefully with ints on GWT; if a multiplier is larger than about 2000000 (0x1fffff,
    specifically) or smaller than about -2000000, and it is multiplied by an arbitrary int (one which can be any
    32-bit value), then precision loss may occur even if you do a bitwise operation afterwards.
