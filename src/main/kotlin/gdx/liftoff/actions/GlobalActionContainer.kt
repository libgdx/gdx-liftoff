package gdx.liftoff.actions

import com.badlogic.gdx.Gdx
import com.github.czyzby.autumn.mvc.stereotype.ViewActionContainer
import com.github.czyzby.kiwi.util.common.Exceptions
import com.github.czyzby.kiwi.util.common.Strings
import com.github.czyzby.lml.annotation.LmlAction
import com.github.czyzby.lml.parser.action.ActionContainer
import kotlin.system.exitProcess

/**
 * Contains actions available for all dialogs and views.
 */
@ViewActionContainer("global")
@Suppress("unused") // Class accessed via reflection.
class GlobalActionContainer : ActionContainer {

  private val blockedTypes: HashSet<String> = hashSetOf("absolutefilehandleresolver", "abstractgraphics", "abstractinput", "action", "actions", "actor", "actorgesturelistener", "addaction", "addlisteneraction", "affine2", "afteraction", "align", "alphaaction", "ambientcubemap", "animatedtiledmaptile", "animation", "animation", "animationcontroller", "annotation", "application", "applicationadapter", "applicationlistener", "applicationlogger", "array", "arraymap", "arrayreflection", "arrayselection", "arraytexturespritebatch", "arrowshapebuilder", "assetdescriptor", "asseterrorlistener", "assetloader", "assetloaderparameters", "assetmanager", "asyncexecutor", "asynchronousassetloader", "asyncresult", "asynctask", "atlastmxmaploader", "atomicqueue", "attribute", "attributes", "audio", "audiodevice", "audiorecorder", "base", "base64coder", "baseanimationcontroller", "basedrawable", "basejsonreader", "baselight", "baseshader", "baseshaderprovider", "baseshapebuilder", "basetmxmaploader", "batch", "batchtiledmaprenderer", "bezier", "billboardcontrollerrenderdata", "billboardparticlebatch", "billboardrenderer", "binaryheap", "bintree", "bitmapfont", "bitmapfontcache", "bitmapfontloader", "bits", "bittreedecoder", "bittreeencoder", "blendingattribute", "booleanarray", "boundingbox", "boxshapebuilder", "bresenham2", "bspline", "bufferedparticlebatch", "bufferutils", "button", "buttongroup", "bytearray", "camera", "cameragroupstrategy", "camerainputcontroller", "capsuleshapebuilder", "catmullromspline", "cell", "changelistener", "chararray", "checkbox", "circle", "circlemapobject", "classpathfilehandleresolver", "classreflection", "clicklistener", "clipboard", "collections", "color", "coloraction", "colorattribute", "colorinfluencer", "colors", "coneshapebuilder", "constructor", "container", "convexhull", "countdowneventaction", "cpuspritebatch", "crc", "cubemap", "cubemapattribute", "cubemapdata", "cubemaploader", "cullable", "cumulativedistribution", "cursor", "customtexture3ddata", "cylindershapebuilder", "cylinderspawnshapevalue", "databuffer", "datainput", "dataoutput", "decal", "decalbatch", "decalmaterial", "decoder", "decoder", "defaultrenderablesorter", "defaultshader", "defaultshaderprovider", "defaulttexturebinder", "delaunaytriangulator", "delayaction", "delayedremovalarray", "delegateaction", "depthshader", "depthshaderprovider", "depthtestattribute", "dialog", "directionallight", "directionallightsattribute", "directionalshadowlight", "disableable", "disposable", "distancefieldfont", "draganddrop", "draglistener", "dragscrolllistener", "drawable", "dynamicsinfluencer", "dynamicsmodifier", "earclippingtriangulator", "ellipse", "ellipsemapobject", "ellipseshapebuilder", "ellipsespawnshapevalue", "emitter", "encoder", "encoder", "environment", "etc1", "etc1texturedata", "event", "eventaction", "eventlistener", "extendviewport", "externalfilehandleresolver", "facedcubemapdata", "field", "filehandle", "filehandleresolver", "filehandlestream", "files", "filetexturearraydata", "filetexturedata", "fillviewport", "firstpersoncameracontroller", "fitviewport", "floataction", "floatarray", "floatattribute", "floatcounter", "floatframebuffer", "floattexturedata", "flushablepool", "focuslistener", "fpslogger", "framebuffer", "framebuffercubemap", "frustum", "frustumshapebuilder", "g3dmodelloader", "game", "gdx", "gdx2dpixmap", "gdxnativesloader", "gdxruntimeexception", "geometryutils", "gesturedetector", "gl20", "gl20interceptor", "gl30", "gl30interceptor", "gl31", "gl31interceptor", "gl32", "gl32interceptor", "glerrorlistener", "glframebuffer", "glinterceptor", "glonlytexturedata", "glprofiler", "gltexture", "glversion", "glyphlayout", "gradientcolorvalue", "graphics", "gridpoint2", "gridpoint3", "group", "groupplug", "groupstrategy", "hdpimode", "hdpiutils", "hexagonaltiledmaprenderer", "horizontalgroup", "httpparametersutils", "httprequestbuilder", "httprequestheader", "httpresponseheader", "httpstatus", "i18nbundle", "i18nbundleloader", "icodeprogress", "identitymap", "image", "imagebutton", "imageresolver", "imagetextbutton", "immediatemoderenderer", "immediatemoderenderer20", "indexarray", "indexbufferobject", "indexbufferobjectsubdata", "indexdata", "influencer", "input", "inputadapter", "inputevent", "inputeventqueue", "inputlistener", "inputmultiplexer", "inputprocessor", "instancebufferobject", "instancebufferobjectsubdata", "instancedata", "intaction", "intarray", "intattribute", "internalfilehandleresolver", "interpolation", "intersector", "intfloatmap", "intintmap", "intmap", "intset", "inwindow", "isometricstaggeredtiledmaprenderer", "isometrictiledmaprenderer", "json", "jsonreader", "jsonvalue", "jsonwriter", "ktxtexturedata", "label", "layout", "layoutaction", "lifecyclelistener", "linespawnshapevalue", "list", "littleendianinputstream", "localfilehandleresolver", "logger", "longarray", "longmap", "longqueue", "lzma", "map", "mapgrouplayer", "maplayer", "maplayers", "mapobject", "mapobjects", "mapproperties", "maprenderer", "material", "mathutils", "matrix3", "matrix4", "mesh", "meshbuilder", "meshpart", "meshpartbuilder", "meshspawnshapevalue", "method", "mipmapgenerator", "mipmaptexturedata", "model", "modelanimation", "modelbatch", "modelbuilder", "modelcache", "modeldata", "modelinfluencer", "modelinstance", "modelinstancecontrollerrenderdata", "modelinstanceparticlebatch", "modelinstancerenderer", "modelloader", "modelmaterial", "modelmesh", "modelmeshpart", "modelnode", "modelnodeanimation", "modelnodekeyframe", "modelnodepart", "modeltexture", "movebyaction", "movetoaction", "music", "musicloader", "nativeinputconfiguration", "net", "netjavaimpl", "netjavaserversocketimpl", "netjavasocketimpl", "ninepatch", "ninepatchdrawable", "node", "nodeanimation", "nodekeyframe", "nodepart", "null", "numberutils", "numericvalue", "objectfloatmap", "objectintmap", "objectlongmap", "objectmap", "objectset", "objloader", "octree", "orderedmap", "orderedset", "orientedboundingbox", "orthocachedtiledmaprenderer", "orthogonaltiledmaprenderer", "orthographiccamera", "outwindow", "parallelaction", "parallelarray", "particlebatch", "particlechannels", "particlecontroller", "particlecontrollercomponent", "particlecontrollercontrollerrenderer", "particlecontrollerfinalizerinfluencer", "particlecontrollerinfluencer", "particlecontrollerrenderdata", "particlecontrollerrenderer", "particleeffect", "particleeffect", "particleeffectactor", "particleeffectloader", "particleeffectloader", "particleeffectpool", "particleemitter", "particleshader", "particlesorter", "particlesystem", "particlevalue", "patchshapebuilder", "path", "pauseablethread", "performancecounter", "performancecounters", "perspectivecamera", "pixmap", "pixmapio", "pixmaploader", "pixmappacker", "pixmappackerio", "pixmaptexturedata", "plane", "pluggablegroupstrategy", "pointlight", "pointlightsattribute", "pointspawnshapevalue", "pointspritecontrollerrenderdata", "pointspriteparticlebatch", "pointspriterenderer", "polygon", "polygonbatch", "polygonmapobject", "polygonregion", "polygonregionloader", "polygonsprite", "polygonspritebatch", "polyline", "polylinemapobject", "pool", "pooledlinkedlist", "pools", "predicate", "preferences", "prefixfilehandleresolver", "primitivespawnshapevalue", "progressbar", "propertiesutils", "quadtreefloat", "quaternion", "queue", "quickselect", "randomxs128", "rangednumericvalue", "ray", "rectangle", "rectanglemapobject", "rectanglespawnshapevalue", "reflectionexception", "reflectionpool", "regioninfluencer", "regularemitter", "relativetemporalaction", "remoteinput", "remotesender", "removeaction", "removeactoraction", "removelisteneraction", "renderable", "renderableprovider", "renderableshapebuilder", "renderablesorter", "rendercontext", "repeatablepolygonsprite", "repeataction", "resolutionfileresolver", "resourcedata", "rotatebyaction", "rotatetoaction", "runnableaction", "scalebyaction", "scalednumericvalue", "scaleinfluencer", "scaletoaction", "scaling", "scalingviewport", "scissorstack", "screen", "screenadapter", "screenutils", "screenviewport", "scrollpane", "segment", "select", "selectbox", "selection", "sequenceaction", "serializationexception", "serversocket", "serversockethints", "shader", "shaderprogram", "shaderprogramloader", "shaderprovider", "shadowmap", "shape2d", "shapecache", "shaperenderer", "shortarray", "simpleinfluencer", "simpleorthogroupstrategy", "sizebyaction", "sizetoaction", "skin", "skinloader", "slider", "snapshotarray", "socket", "sockethints", "sort", "sortedintlist", "sound", "soundloader", "spawninfluencer", "spawnshapevalue", "sphere", "sphereshapebuilder", "sphericalharmonics", "splitpane", "spotlight", "spotlightsattribute", "sprite", "spritebatch", "spritecache", "spritedrawable", "stack", "stage", "statictiledmaptile", "streamutils", "stretchviewport", "stringbuilder", "synchronousassetloader", "table", "temporalaction", "textarea", "textbutton", "textfield", "textinputwrapper", "texttooltip", "texture", "texture3d", "texture3ddata", "texturearray", "texturearraydata", "textureatlas", "textureatlasloader", "textureattribute", "texturebinder", "texturedata", "texturedescriptor", "textureloader", "texturemapobject", "textureprovider", "textureregion", "textureregiondrawable", "threadutils", "tidemaploader", "tileddrawable", "tiledmap", "tiledmapimagelayer", "tiledmaprenderer", "tiledmaptile", "tiledmaptilelayer", "tiledmaptilemapobject", "tiledmaptileset", "tiledmaptilesets", "timer", "timescaleaction", "timeutils", "tmxmaploader", "tooltip", "tooltipmanager", "touchable", "touchableaction", "touchpad", "transformdrawable", "tree", "ubjsonreader", "ubjsonwriter", "uiutils", "unweightedmeshspawnshapevalue", "value", "vector", "vector2", "vector3", "vector4", "version", "vertexarray", "vertexattribute", "vertexattributes", "vertexbufferobject", "vertexbufferobjectsubdata", "vertexbufferobjectwithvao", "vertexdata", "verticalgroup", "viewport", "visibleaction", "weightmeshspawnshapevalue", "widget", "widgetgroup", "window", "windowedmean", "xmlreader", "xmlwriter")

  private val forbiddenNames: String = "(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|double|do|else|enum|extends|false|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|true|try|void|volatile|while|_|con|prn|aux|nul|(com[1-9])|(lpt[1-9]))"

  @LmlAction("showSite")
  fun showLibGdxWebsite() = Gdx.net.openURI("https://libgdx.com/")

  @LmlAction("fileNameFilter")
  fun isValidFileNameCharacter(character: Char): Boolean = Character.isDigit(character) ||
    Character.isLetter(character) || character == '-' || character == '_'

  @LmlAction("isValidFile")
  fun isValidFileName(input: String): Boolean = Strings.isNotBlank(input) && input.matches(Regex("[-\\w]+"))

  @LmlAction("isSdk")
  fun isAndroidSdkDirectory(path: String): Boolean {
    try {
      val file = Gdx.files.absolute(path)
      if (file.isDirectory) {
        return (
          file.child("tools").isDirectory ||
            file.child("cmdline-tools").isDirectory ||
            file.child("build-tools").isDirectory
          ) && file.child("platforms").isDirectory
      }
    } catch (exception: Exception) {
      Exceptions.ignore(exception) // Probably not the Android SDK.
    }
    return false
  }

  @LmlAction("javaClassFilter")
  fun isValidJavaCharacter(character: Char): Boolean = Character.isJavaIdentifierPart(character)

  @LmlAction("javaPackageFilter")
  fun isValidJavaPackageCharacter(character: Char): Boolean = Character.isJavaIdentifierPart(character) || character == '.'

  @LmlAction("isValidClass")
  fun isValidClassName(input: String): Boolean {
    if (Strings.isBlank(input) || !Character.isJavaIdentifierStart(input[0])) {
      return false
    } else if (input.length == 1) {
      return true
    }
    for (id in 1 until input.length) {
      if (!Character.isJavaIdentifierPart(input[id])) {
        return false
      }
    }
    return !input.matches(Regex("(?i)$forbiddenNames")) && !blockedTypes.contains(input.lowercase())
  }

  @LmlAction("isValidPackage")
  fun isValidPackageName(input: String): Boolean {
    if (Strings.isBlank(input) || !Character.isJavaIdentifierStart(input[0]) || input.contains("..") || input.endsWith('.')) {
      return false
    }
    var previousDot = false
    for (id in 1 until input.length) {
      if (input[id] == '.') {
        previousDot = true
      } else {
        if ((previousDot && !Character.isJavaIdentifierStart(input[id])) || !Character.isJavaIdentifierPart(input[id])) {
          return false
        }
        previousDot = false
      }
    }
    // case-insensitive check for any Java reserved word, then keep checking for Win32 reserved file/folder names.
    return !(
      !input.contains('.') || input.matches(
        Regex(
          // case-insensitive check for any Java reserved word, then keep checking for Win32 reserved file/folder names.
          "(?i).*(^|\\.)$forbiddenNames(\\.|$).*"
        )
      )
      )
  }

  @LmlAction("androidPluginVersion")
  fun getDefaultAndroidPluginVersion(): String = "8.1.4"

  @LmlAction("roboVMVersion")
  fun getDefaultRoboVMVersion(): String = "2.3.20"

  @LmlAction("gwtPluginVersion")
  fun getDefaultGwtPluginVersion(): String = "1.1.29"

  @LmlAction("close")
  fun noOp() {
    // Empty dialog closing utility.
  }

  @LmlAction("exit")
  fun exit() {
    Gdx.app.exit()
    exitProcess(0)
  }
}
