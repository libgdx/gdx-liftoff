package gdx.liftoff.data.templates.official

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * A 2.5D isometric demo with a procedural map made of voxel terrain.
 * This is a tiny complete game, with win and loss conditions.
 * @author tommyettinger
 * @author bergice
 */
@ProjectTemplate(official = true)
@Suppress("unused") // Referenced via reflection.
class IsometricVoxelTemplate : Template {
  override val id = "isometricVoxel"
  override val description: String
    get() =
      "A sample project implementing an isometric pixel-art game with procedural terrain. " +
        "Includes launchers for each platform and a small but winnable game."

  override fun apply(project: Project) {
    super.apply(project)
    arrayOf(
      "Cozette-License.txt",
      "CozetteOutlined-standard.fnt",
      "isometric-trpg.atlas",
      "isometric-trpg.json",
      "isometric-trpg.png",
      "isometric-trpg-License.txt",
      "Komiku - Road 4 Fight.ogg",
      "Komiku - Road 4 Fight - License.txt",
      "Skin-License.txt",
    ).forEach {
      project.files.add(
        CopiedFile(
          projectName = Assets.ID,
          path = path(it),
          original = path("generator", "templates", "isometric-voxel", it),
        ),
      )
    }
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage,
      fileName = "LocalMap.java",
      content =
        """package ${project.basic.rootPackage};

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import ${project.basic.rootPackage}.game.AssetData;
import ${project.basic.rootPackage}.game.Mover;
import ${project.basic.rootPackage}.util.HasPosition3D;
import ${project.basic.rootPackage}.util.MathSupport;
import ${project.basic.rootPackage}.util.MiniNoise;
import ${project.basic.rootPackage}.util.VoxelCollider;

import static com.badlogic.gdx.math.MathUtils.round;

/**
 * Stores a current "level" of the game and its contents, including moving beings and immobile terrain.
 * One of the more important and widely-used classes here.
 */
public class LocalMap {

    /**
     * Grid-aligned terrain storage, this uses an int ID per voxel for convenience. In this case, you could also use
     * byte IDs, and some games might require short IDs, but int is used just because Java can directly create ints.
     */
    public int[][][] tiles;
    /**
     * The critical, sortable mapping of Vector4 positions to visible IsoSprites. This includes two IsoSprites for each
     * voxel of terrain: one is the terrain cube itself, and one is its outline, which has a substantial depth modifier
     * so it will only render if no other terrain is covering it. Movers' IsoSprite (and AnimatedIsoSprite) instances
     * are also stored in here.
     */
    public OrderedMap<Vector4, IsoSprite> everything;
    /**
     * The Array of voxel types that can be shown here, typically drawn from a TextureAtlas.
     */
    public Array<TextureAtlas.AtlasRegion> tileset;
    /**
     * The reused Sprite for all voxel edges, each of which is a translucent outline above the upper edges of the voxel.
     */
    public Sprite edge;
    /**
     * The center position, in fractional tiles, of the entire map's f-size.
     */
    public float fCenter;
    /**
     * The center position, in fractional tiles, of the entire map's g-size.
     */
    public float gCenter;
    /**
     * How many fish we started out needing to rescue.
     */
    public int totalFish = 10;
    /**
     * How many fish have been saved so far.
     */
    public int fishSaved = 0;

    /**
     * A collision tracker for Movers so we can tell when the player should be damaged by touching an enemy.
     */
    public VoxelCollider<Mover> movers;

    /**
     * @return the rotation of the map in degrees
     */
    public float getRotationDegrees() {
        return rotationDegrees;
    }

    /**
     * Sets {@link #rotationDegrees}, but also {@link #cosRotation} and {@link #sinRotation}.
     * @param rotationDegrees the desired rotation of the map, in degrees
     */
    public void setRotationDegrees(float rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
        cosRotation = MathUtils.cosDeg(rotationDegrees);
        sinRotation = MathUtils.sinDeg(rotationDegrees);
    }

    /**
     * The map's current rotation, in degrees.
     */
    public float rotationDegrees = 0f;
    /**
     * The cosine of the map's current rotation, used to avoid repeated calls to {@link MathUtils#cosDeg(float)}.
     */
    public float cosRotation = 0f;
    /**
     * The sine of the map's current rotation, used to avoid repeated calls to {@link MathUtils#sinDeg(float)}.
     */
    public float sinRotation = 0f;
    /**
     * During a rotation animation, this is the starting rotation, in degrees.
     */
    public float previousRotation = 0f;
    /**
     * During a rotation animation, this is the target rotation, in degrees.
     */
    public float targetRotation = 0f;

    /**
     * Mutated often in-place and used to check for positions in {@link #everything}.
     */
    private static final Vector4 tempVec4 = new Vector4();

    /**
     * Present for serialization only, this creates a LocalMap but needs many fields initialized.
     * {@link #tileset}, {@link #tiles}, {@link #edge}, {@link #fCenter}, {@link #gCenter}, {@link #movers}, and of
     * course {@link #everything} need to be initialized if you use this.
     */
    public LocalMap() {

    }
    /**
     * Creates a new LocalMap and initializes all fields.
     * @param width the f-size of the map
     * @param height the g-size of the map
     * @param layers the h-size of the map
     * @param atlas a TextureAtlas this will pull all "tile" regions from and the Sprite for "edge"
     */
    public LocalMap(int width, int height, int layers, TextureAtlas atlas) {
        this.tileset = atlas.findRegions("tile");
        this.edge = atlas.createSprite("edge");
        tiles = new int[width][height][layers];
        fCenter = (width - 1) * 0.5f;
        gCenter = (height - 1) * 0.5f;
        for (int f = 0; f < width; f++) {
            for (int g = 0; g < height; g++) {
                for (int h = 0; h < layers; h++) {
                    tiles[f][g][h] = -1;
                }
            }
        }
        everything = new OrderedMap<>(width * height * layers * 3 >>> 2, 0.625f);

        movers = new VoxelCollider<>();
    }

    /**
     * Checks if the given f,g,h isometric tile position is a valid position that can be looked up in {@link #tiles}.
     * @param f f-coordinate; if negative or too high, this returns false
     * @param g g-coordinate; if negative or too high, this returns false
     * @param h h-coordinate; if negative or too high, this returns false
     * @return true if the given coordinates are valid for array indices into {@link #tiles}, or false otherwise
     */
    public boolean isValid(int f, int g, int h) {
        return f >= 0 && g >= 0 && h >= 0 && f < tiles.length && g < tiles[0].length && h < tiles[0][0].length;
    }

    /**
     * Rounds f, g, and h and passes them to {@link #isValid(int, int, int)}. Note that this permits small negative
     * inputs due to rounding bringing them up to 0.0f if they are greater than -0.5f .
     * @param f f-coordinate; if too low or too high, this returns false
     * @param g g-coordinate; if too low or too high, this returns false
     * @param h h-coordinate; if too low or too high, this returns false
     * @return true if the given coordinates, after rounding, are valid for array indices into {@link #tiles}, or false otherwise
     */
    public boolean isValid(float f, float g, float h) {
        return isValid(round(f), round(g), round(h));
    }
    /**
     * Delegates to {@link #isValid(float, float, float)} using only the x, y, and z coordinates of {@code point}.
     * @param point a Vector4 of which only x, y, and z will be checked
     * @return true if the given point, after rounding x, y, and z, is valid for array indices into {@link #tiles}, or false otherwise
     */
    public boolean isValid(Vector4 point) {
        return isValid(point.x, point.y, point.z);
    }

    /**
     * If f, g, and h are valid, this returns the tile at that location; if no tile is present or if the coordinates are
     * invalid, this returns -1.
     * @param f f-coordinate; if negative or too high, this returns false
     * @param g g-coordinate; if negative or too high, this returns false
     * @param h h-coordinate; if negative or too high, this returns false
     * @return the tile ID at the given location, or -1 if no tile is present or the location is invalid
     */
    public int getTile(int f, int g, int h) {
        return isValid(f, g, h) ? tiles[f][g][h] : -1;
    }

    /**
     * Rounds the given float coordinates and passes them to {@link #getTile(int, int, int)}. Note that this permits
     * small negative inputs due to rounding bringing them up to 0.0f if they are greater than -0.5f .
     * @param f f-coordinate; if too low or too high, this returns false
     * @param g g-coordinate; if too low or too high, this returns false
     * @param h h-coordinate; if too low or too high, this returns false
     * @return the tile ID at the given location, or -1 if no tile is present or the location is invalid
     */
    public int getTile(float f, float g, float h) {
        return getTile(round(f), round(g), round(h));
    }

    /**
     * Rounds the x, y, and z of {@code point} and passes them to {@link #getTile(int, int, int)}. Note that this
     * permits small negative inputs due to rounding bringing them up to 0.0f if they are greater than -0.5f .
     * @param point a Vector4 of which only x, y, and z will be checked
     * @return the tile ID at the given location, or -1 if no tile is present or the location is invalid
     */
    public int getTile(Vector4 point) {
        return getTile(round(point.x), round(point.y), round(point.z));
    }

    /**
     * Gets the IsoSprite with the appropriate depth for a terrain voxel at the given isometric position (not rounded).
     * @param f f position of the terrain, almost always an integer stored in a float
     * @param g g position of the terrain, almost always an integer stored in a float
     * @param h h position of the terrain, almost always an integer stored in a float
     * @return the IsoSprite for terrain at the given position or {@code null} if none is present
     */
    public IsoSprite getIsoSpriteTerrain(float f, float g, float h) {
        return everything.get(tempVec4.set(f, g, h, 0));
    }

    /**
     * Modifies the given Vector4 so it holds the given [f,g,h] position with the depth a fish can have.
     * This rounds f, g, and h because a fish is always at an all-integer position. If {@link #everything} does not
     * have anything present at the Vector4 this produces, there is no fish present at that position, but if it does
     * have any IsoSprite present, it will be a fish.
     *
     * @param changing a Vector4 that will be modified in-place
     * @param f the "France to Finland" isometric coordinate; will be rounded and assigned to changing
     * @param g the "Germany to Greenland" isometric coordinate; will be rounded and assigned to changing
     * @param h the "heel to head" isometric coordinate; will be rounded and assigned to changing
     */
    public void setToFishPosition(Vector4 changing, float f, float g, float h) {
        changing.set(round(f), round(g), round(h), Mover.FISH_W);
    }

    /**
     * Adds a {@link Mover} to {@link #everything} and {@link #movers}. If the Mover's position is already occupied by
     * a terrain tile, or if this would collide with another Mover, the Mover's position is randomized until it finds
     * a valid location.
     * @param mover a {@link Mover} that will have its position potentially altered if invalid
     * @param depth the depth modifier to use for the Mover, such as {@link Mover#PLAYER_W}
     * @return the used Vector4 position the Mover was placed into, which may be different from its original position
     */
    public Vector4 addMover(Mover mover, float depth) {
        mover.getPosition().z = getLayers() - 1;
        Vector4 pos = new Vector4(mover.getPosition(), depth);
        while (getTile(pos) != -1 || movers.collisionsWith(mover).notEmpty()) {
            pos.x = MathUtils.random(getWidth() - 1);
            pos.y = MathUtils.random(getHeight() - 1);
            mover.getPosition().set(pos.x, pos.y, pos.z);
        }
        mover.place(depth);
        movers.entities.add(mover);
        return pos;
    }

    /**
     * When point.w is 0, this selects terrain; when it is ENTITY_W, it selects an entity.
     * @param point
     * @return
     */
    public IsoSprite getIsoSprite(Vector4 point) {
        return everything.get(point);
    }

    /**
     * Sets the voxel terrain tile at the given isometric tile position to the tile with the given ID.
     * IDs can be seen in {@link AssetData}.
     * This also places an {@link #edge} at the same isometric position but a lower depth, so it only shows if there is
     * empty space behind the voxel in the depth sort.
     * @param f f-position as an int
     * @param g g-position as an int
     * @param h h-position as an int
     * @param tileId an ID for a tile, typically from {@link AssetData}
     */
    public void setTile(int f, int g, int h, int tileId) {
        if (isValid(f, g, h)) {
            tiles[f][g][h] = tileId;
            if (tileId == -1) {
                everything.remove(tempVec4.set(f, g, h, 0));
                everything.remove(tempVec4.set(f, g, h, -1.5f));
            } else {
                IsoSprite iso;
                if ((iso = everything.get(tempVec4.set(f, g, h, 0))) != null) {
                    iso.setSprite(new TextureAtlas.AtlasSprite(tileset.get(tileId)));
                } else {
                    everything.put(new Vector4(f, g, h, 0), new IsoSprite(new TextureAtlas.AtlasSprite(tileset.get(tileId)), f, g, h));
                    // Environment tiles have an outline that may render if there is empty space behind them.
                    // The position has -1.5 for w, and w is added to the depth for the purpose of sorting.
                    // Adjacent environment tiles should have a depth that is +1 or -1 from this tile.
                    // Because the outline is -1.5 behind this tile, adjacent environment tiles will render over it,
                    // but if there is empty space behind a tile, the outline will be in front of the further tiles.
                    everything.put(new Vector4(f, g, h, -1.5f), new IsoSprite(edge, f, g, h));
                }
            }
        }
    }

    /**
     * Sets the voxel terrain tile at the given isometric tile position (as a Vector4, which will be rounded when used
     * in {@link #tiles}) to the tile with the given ID. IDs can be seen in {@link AssetData}.
     * This also places an {@link #edge} at the same isometric position but a lower depth, so it only shows if there is
     * empty space behind the voxel in the depth sort.
     * @param point a Vector4 of which x, y, and z will be used for a position and w will genrerally be treated as 0
     * @param tileId an ID for a tile, typically from {@link AssetData}
     */
    public void setTile(Vector4 point, int tileId) {
        int f = round(point.x);
        int g = round(point.y);
        int h = round(point.z);
        if (isValid(f, g, h)) {
            tiles[f][g][h] = tileId;
            if (tileId == -1) {
                everything.remove(point);
                // remove the outline, too
                everything.remove(point.add(0,0,0,-1.5f));
            } else {
                IsoSprite iso;
                if ((iso = everything.get(point)) != null) {
                    iso.setPosition(point.x, point.y, point.z);
                } else {
                    everything.put(point, new IsoSprite(new TextureAtlas.AtlasSprite(tileset.get(tileId)), point.x, point.y, point.z));
                    // Environment tiles have an outline that may render if there is empty space behind them.
                    // The position has -1.5 for w, and w is added to the depth for the purpose of sorting.
                    // Adjacent environment tiles should have a depth that is +1 or -1 from this tile.
                    // Because the outline is -1.5 behind this tile, adjacent environment tiles will render over it,
                    // but if there is empty space behind a tile, the outline will be in front of the further tiles.
                    everything.put(new Vector4(point.x, point.y, point.z, -1.5f), new IsoSprite(edge, point.x, point.y, point.z));

                }
            }
        }
    }

    /**
     * Adds an {@link IsoSprite} to {@link #everything} at the requested f, g, h, depth position, if and only if the
     * position (with rounded float coordinates) is valid in the bounds of {@link #tiles}. This removes whatever tile
     * may be present at the position this places the entity into.
     * @param f the "France to Finland" isometric coordinate; will be rounded and assigned to changing
     * @param g the "Germany to Greenland" isometric coordinate; will be rounded and assigned to changing
     * @param h the "heel to head" isometric coordinate; will be rounded and assigned to changing
     * @param depth the depth modifier to use, such as {@link Mover#PLAYER_W}
     * @param sprite the {@link IsoSprite} to place into {@link #everything}
     */
    public void setEntity(float f, float g, float h, float depth, IsoSprite sprite) {
        int rf = round(f), rg = round(g), rh = round(h);
        if (isValid(rf, rg, rh)) {
            tiles[rf][rg][rh] = -1;
            sprite.setPosition(f, g, h);
            everything.put(new Vector4(f, g, h, depth), sprite);
        }
    }

    public int getFSize() {
        return tiles.length;
    }

    public int getGSize() {
        return tiles[0].length;
    }

    public int getHSize() {
        return tiles[0][0].length;
    }

    /**
     * The same as {@link #getFSize()}.
     * @return the f-size of the map.
     */
    public int getWidth() {
        return tiles.length;
    }

    /**
     * The same as {@link #getGSize()}.
     * @return the g-size of the map.
     */
    public int getHeight() {
        return tiles[0].length;
    }

    /**
     * The same as {@link #getHSize()}.
     * @return the h-size of the map.
     */
    public int getLayers() {
        return tiles[0][0].length;
    }

    /**
     * Used to allow paths to meander across the map area, without changing directions completely at random.
     */
    private static final GridPoint2[] DIRECTIONS = {new GridPoint2(1, 0), new GridPoint2(0, 1), new GridPoint2(-1, 0), new GridPoint2(0, -1)};
    /**
     * Generates a simple test map that assumes a specific tileset (using {@code isometric-trpg.atlas} as
     * {@code tileset}, {@code tileset.findRegions("tile")}). Allows setting a specific seed to get the same map every
     * time. This requires a minimum {@code mapSize} of 11 and a minimum {@code mapPeak} of 4.
     * <br>
     * CUSTOM TO YOUR GAME.
     * @param seed if this {@code long} is the same, the same map will be produced on each call
     * @param mapSize the width and height of the map, or the dimensions of the ground plane in tiles
     * @param mapPeak the layer count or max elevation of the map
     * @param atlas should probably be the TextureAtlas loaded from {@code isometric-trpg.atlas}
     * @return a new LocalMap
     */
    public static LocalMap generateTestMap(long seed, int mapSize, int mapPeak, TextureAtlas atlas) {

        // noise that gradually moves a little
        MiniNoise baseNoise = new MiniNoise((int) (seed), 0.06f, MiniNoise.FBM, 3);
        // noise that is usually a low value, but has ridges of high values
        MiniNoise ridgeNoise = new MiniNoise((int) (seed >> 32), 0.1f, MiniNoise.RIDGED, 1);
        // This makes calls to MathUtils random number methods predictable, including after this call completes!
        // You may want to re-randomize MathUtils' random number generator after this completes, using:
        //  MathUtils.random.setSeed(System.currentTimeMillis());
        MathUtils.random.setSeed(seed);

        mapSize = Math.max(11, mapSize);
        mapPeak = Math.max(mapPeak, 4);

        LocalMap map = new LocalMap(mapSize, mapSize, mapPeak, atlas);
        // Random voxels as a base, with height determined by noise. Either dirt 25% of the time, or grass the rest.
        for (int f = 0; f < mapSize; f++) {
            for (int g = 0; g < mapSize; g++) {
                // I fiddled with this for a while to get results I liked.
                // This combines baseNoise's slowly changing shallow hills with a little of ridgeNoise's sharp crests.
                // The result is scaled and moved into the -1.99 to -0.01 range, then fed into
                // Math.pow with a base of 7, which is pretty much a complete guess that was refined over a few tries.
                // Then that pow call (which can produce values from close to 0 to almost 1) is scaled by mapPeak.
                int height = (int)(mapPeak * Math.pow(7.0, baseNoise.getNoise(f, g) * 0.56 + ridgeNoise.getNoise(f, g) * 0.43 - 1.0));
                // Some tiles are dirt, but most are grass; the 1.1f + 0.6f * baseNoise... is usually 1, but sometimes 0.
                int tile = (int)(1.1f + 0.6f * baseNoise.getNoiseWithSeed(f * 2.3f, g * 2.3f, ~baseNoise.getSeed()));
                map.setTile(f, g, height, tile);
                // Anything below one of these tiles must be dirt.
                for (int h = height - 1; h >= 0; h--) {
                    map.setTile(f, g, h, AssetData.DIRT);
                }
            }
        }

        // Here we add a little pathway to the map.
        // We start at a random position with centered f-position and low g-position.
        int pathF = mapSize / 2 + MathUtils.random(mapSize / -4, mapSize / 4);
        int pathG = mapSize / 4 + MathUtils.random(mapSize / -6, mapSize / 6);
        // angle is 0, 1, 2, or 3.
        float angle = 1f;
        // We randomly may swap f and g...
        if(MathUtils.randomBoolean()){
            int temp = pathG;
            pathG = pathF;
            pathF = temp;
            angle = 0f;
        }
        // and randomly may make them start on the opposite side, making pathG high instead of low (f if swapped).
        if(MathUtils.randomBoolean()){
            pathF = mapSize - 1 - pathF;
            pathG = mapSize - 1 - pathG;
            angle += 2f;
        }

        // We use 1D noise to make the path change angle.
        baseNoise.setOctaves(1);
        for (int i = 0, n = mapSize + mapSize; i < n; i++) {
            if(map.isValid(pathF, pathG, 0)) {
                for (int h = mapPeak - 1; h >= 0; h--) {
                    if(map.getTile(pathF, pathG, h) != -1) {
                        // we have an 80% change to place a path at any valid position, if we still have one.
                        if(MathUtils.randomBoolean(0.8f))
                            map.setTile(pathF, pathG, h, AssetData.PATH_GRASS_FGTR);
                        break;
                    }
                }
            } else {
                // If the current position is invalid, try again with a new start position.
                pathF = mapSize / 2 + MathUtils.random(mapSize / -4, mapSize / 4);
                pathG = mapSize / 4 + MathUtils.random(mapSize / -6, mapSize / 6);
                angle = 1f;
                if(MathUtils.randomBoolean()){
                    int temp = pathG;
                    pathG = pathF;
                    pathF = temp;
                    angle = 0f;
                }
                if(MathUtils.randomBoolean()){
                    pathF = mapSize - 1 - pathF;
                    pathG = mapSize - 1 - pathG;
                    angle += 2f;
                }

            }
            // Gradually change the angle of the path using continuous noise.
            angle = (angle + baseNoise.getNoise(i * 25f) * 0.7f + 4f) % 4f;
            GridPoint2 dir = DIRECTIONS[(int) angle];
            pathF += dir.x;
            pathG += dir.y;
        }

        // When we're done, we just need to take all the all-connected path tiles and change them to linear paths.
        AssetData.realignPaths(map);
        return map;
    }

    /**
     * Places berry bushes, which were used in an earlier version instead of goldfish.
     * Berry bushes are harder to notice than goldfish at small sizes, though.
     * <br>
     * This should be customized for your game, if you use it.
     * @param seed if this {@code long} is the same, the same map will be produced on each call
     * @param bushCount how many bushes to try to place
     * @return this LocalMap, for chaining
     */
    public LocalMap placeBushes(long seed, int bushCount) {
        GridPoint2 point = new GridPoint2();
        int fs = getFSize(), gs = getGSize(), hs = getHSize();
        seed = (seed ^ 0x9E3779B97F4A7C15L) * 0xD1B54A32D192ED03L;
        PER_BUSH:
        for (int i = 0; i < bushCount; i++) {
            MathSupport.fillR2(point, seed + i, fs, gs);
            for (int h = hs - 2; h >= 0; h--) {
                int below = getTile(point.x, point.y, h);
                if (below == AssetData.DECO_HEDGE) {
                    bushCount++;
                    continue PER_BUSH; // labeled break; we want to try to place a bush in another location.
                }
                if (below != -1) {
                    tiles[point.x][point.y][h + 1] = AssetData.DECO_HEDGE;
                    everything.put(new Vector4(point.x, point.y, h + 1, Mover.FISH_W),
                        new IsoSprite(new TextureAtlas.AtlasSprite(tileset.get(AssetData.DECO_HEDGE)), point.x, point.y, h + 1));
                    setTile(point.x, point.y, h, AssetData.DIRT);
                    setTile(point.x + 1, point.y, h, AssetData.DIRT);
                    setTile(point.x - 1, point.y, h, AssetData.DIRT);
                    setTile(point.x, point.y + 1, h, AssetData.DIRT);
                    setTile(point.x, point.y - 1, h, AssetData.DIRT);
                    break;
                }
            }
        }
        return this;
    }

    /**
     * Places goldfish {@link AnimatedIsoSprite} instances at various valid locations, chosen sub-randomly.
     * Sub-random here means it is extremely unlikely two goldfish will spawn nearby each other, but their position is
     * otherwise random-seeming.
     * <br>
     * This should be customized for your game.
     * @param seed if this {@code long} is the same, the same map will be produced on each call
     * @param fishCount how many fish to place
     * @param animations used to get the animation for a fish so we can make {@link AnimatedIsoSprite}s per fish
     * @return this LocalMap for chaining
     */
    public LocalMap placeFish(long seed, int fishCount, Array<Array<Animation<TextureAtlas.AtlasSprite>>> animations) {
        GridPoint2 point = new GridPoint2();
        int fs = getFSize(), gs = getGSize(), hs = getHSize();
        seed = (seed ^ 0x9E3779B97F4A7C15L) * 0xD1B54A32D192ED03L;
        for (int i = 0; i < fishCount; i++) {
            MathSupport.fillR2(point, seed + i, fs, gs);
            for (int h = hs - 2; h >= 0; h--) {
                int below = getTile(point.x, point.y, h);
                if (below != -1) {
                    setEntity(point.x, point.y, h + 1, Mover.FISH_W, new AnimatedIsoSprite(animations.get(0).get(AssetData.FISH), point.x, point.y, h + 1));
                    break;
                }
            }
        }
        return this;
    }

    /**
     * Simply wraps {@link VoxelCollider#collisionsWith(HasPosition3D)}, using this LocalMap's {@link #movers}.
     * @param mover the Mover to check if any existing Movers collide with
     * @return an Array of colliding Movers, which will be empty if nothing collides with {@code mover}
     */
    public Array<Mover> checkCollision(Mover mover) {
        return movers.collisionsWith(mover);
    }
}
""",
    )
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage,
      fileName = "IsoSprite.java",
      content =
        """package ${project.basic.rootPackage};

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.NumberUtils;
import ${project.basic.rootPackage}.game.AssetData;

/**
 * Wraps a {@link Sprite} so its position can be set using isometric coordinates.
 * There are many possible isometric coordinate systems, so the one used here intentionally avoids calling the isometric
 * axes x, y, or z - here, the axes are f, g, and h, with mnemonics to help remember them. This uses an origin point at
 * the bottom center of the rotated rectangular map, typically the lowest corner of the map. The f and g axes are
 * diagonal, and correspond to movement at a shallow angle on both world x and world y. The h axis is used for
 * elevation, and corresponds to movement on world y. The mnemonics here work as if on a world map, with the origin
 * somewhere in Belgium or the Netherlands:
 * <ul>
 *     <li>The f axis is roughly the diagonal from France to Finland.</li>
 *     <li>The g axis is roughly the diagonal from Germany to Greenland (or Greece to Germany, or Greece to Greenland).</li>
 *     <li>The h axis is the vertical line from your heel to your head (or Hell to Heaven).</li>
 * </ul>
 */
public class IsoSprite implements Comparable<IsoSprite> {
    /**
     * The "cube side length" for one voxel.
     */
    public static float UNIT = AssetData.TILE_HEIGHT;
    /**
     * The Sprite this wraps and knows how to display.
     */
    public Sprite sprite;
    /**
     * The f-coordinate, on the diagonal axis from "France to Finland".
     */
    public float f;
    /**
     * The g-coordinate, on the diagonal axis from "Germany to Greenland".
     */
    public float g;
    /**
     * The h-coordinate, on the vertical/elevation axis from "heel to head".
     */
    public float h;

    /**
     * Creates an IsoSprite with an empty {@link Sprite#Sprite()} for its visual.
     */
    public IsoSprite() {
        this(new Sprite());
    }

    /**
     * Creates an IsoSprite with the given Sprite for its visual.
     * @param sprite the Sprite to show
     */
    public IsoSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    /**
     * Creates an IsoSprite with the given Sprite for its visual and the given isometric tile position.
     * @param sprite the Sprite to show
     * @param f isometric tile f-coordinate
     * @param g isometric tile g-coordinate
     * @param h isometric tile h-coordinate
     */
    public IsoSprite(Sprite sprite, float f, float g, float h) {
        this.sprite = sprite;
        setPosition(f, g, h);
    }

    /**
     * Creates an IsoSprite with the given Sprite for its visual and the given isometric tile position.
     * @param sprite the Sprite to show
     * @param position an isometric tile position, storing f,g,h in the Vector3's x,y,z
     */
    public IsoSprite(Sprite sprite, Vector3 position) {
        this.sprite = sprite;
        setPosition(position.x, position.y, position.z);
    }

    /**
     * Creates an IsoSprite with the given Sprite for its visual and the given isometric tile position.
     * @param sprite the Sprite to show
     * @param position an isometric tile position, storing f,g,h in the GridPoint3's x,y,z
     */
    public IsoSprite(Sprite sprite, GridPoint3 position) {
        this.sprite = sprite;
        setPosition(position.x, position.y, position.z);
    }

    /**
     * Sets the position to the given isometric tile coordinates.
     * @param f isometric tile f-coordinate
     * @param g isometric tile g-coordinate
     * @param h isometric tile h-coordinate
     */
    public void setPosition(float f, float g, float h) {
        this.f = f;
        this.g = g;
        this.h = h;
        float worldX = (f - g) * (2 * UNIT);
        float worldY = (f + g) * UNIT + h * (2 * UNIT);
        sprite.setPosition(worldX, worldY);
    }

    /**
     * Sets the position to the given isometric tile coordinates.
     * @param point an isometric tile position, storing f,g,h in the GridPoint3's x,y,z
     */
    public void setPosition(GridPoint3 point) {
        setPosition(point.x, point.y, point.z);
    }

    /**
     * Sets the position to the given isometric tile coordinates.
     * @param point an isometric tile position, storing f,g,h in the Vector3's x,y,z
     */
    public void setPosition(Vector3 point) {
        setPosition(point.x, point.y, point.z);
    }

    /**
     * Sets the Sprite's origin-based position to the given isometric tile coordinates.
     * @see Sprite#setOriginBasedPosition(float, float)
     * @param f isometric tile f-coordinate
     * @param g isometric tile g-coordinate
     * @param h isometric tile h-coordinate
     */
    public void setOriginBasedPosition(float f, float g, float h) {
        this.f = f;
        this.g = g;
        this.h = h;
        float worldX = (f - g) * (2 * UNIT);
        float worldY = (f + g) * UNIT + h * (2 * UNIT);
        sprite.setOriginBasedPosition(worldX, worldY);
    }

    /**
     * Sets the Sprite's origin, used for {@link #setOriginBasedPosition(float, float, float)} and so on.
     * @param originX x relative to the Sprite's position, in world coordinates
     * @param originY y relative to the Sprite's position, in world coordinates
     */
    public void setOrigin(float originX, float originY) {
        sprite.setOrigin(originX, originY);
    }

    /**
     * Sets the Sprite's origin to its center, as with {@link Sprite#setOriginCenter()}.
     */
    public void setOriginCenter() {
        sprite.setOriginCenter();
    }

    public float getOriginX() {
        return sprite.getOriginX();
    }

    public float getOriginY() {
        return sprite.getOriginY();
    }

    /**
     * Gets the visual currently used for this IsoSprite.
     * @return the Sprite this uses
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Sets the visual Sprite to the given parameter, first setting its position, its color, and its origin to match
     * what this IsoSprite uses, then making the {@link #sprite} the same reference as the parameter.
     * @param sprite a Sprite that will be modified to match this IsoSprite's color, position, and origin
     */
    public void setSprite(Sprite sprite) {
        float worldX = (f - g) * (2 * UNIT);
        float worldY = (f + g) * UNIT + h * (2 * UNIT);
        sprite.setPosition(worldX, worldY);
        sprite.setPackedColor(this.sprite.getPackedColor());
        sprite.setOrigin(this.sprite.getOriginX(), this.sprite.getOriginY());
        this.sprite = sprite;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public float getG() {
        return g;
    }

    public void setG(float g) {
        this.g = g;
    }

    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }

    /**
     * Gets a float that roughly determines how close this IsoSprite is to the viewer/camera, for sorting purposes.
     * This won't work for IsoSprites that can have f or g go outside the -1023 to 1023 range.
     * The distance this returns is only useful relative to other results of this method, not in-general position.
     * Higher returned values mean the IsoSprite is closer to the camera, and so should be rendered later.
     * @return an estimate of how close this IsoSprite is to the viewer/camera, in no particular scale
     */
    public float getViewDistance() {
        return (h * 3 - f - g) + (f - g) * (1f/2048);
    }

    /**
     * Calculates the distance from the camera to the given f,g,h position, using the given cos and sin of the rotation
     * of the map around the given origin point.
     * @param f isometric tile f-coordinate
     * @param g isometric tile g-coordinate
     * @param h isometric tile h-coordinate
     * @param originF the f-coordinate of the rotational center of the map
     * @param originG the g-coordinate of the rotational center of the map
     * @param cosRotation the pre-calculated cosine of the map's rotation
     * @param sinRotation the pre-calculated sine of the map's rotation
     * @return the view distance to the given position, with the given rotation around the given origin
     */
    public static float viewDistance(float f, float g, float h, float originF, float originG, float cosRotation, float sinRotation) {
        f -= originF;
        g -= originG;
        float rf = cosRotation * f - sinRotation * g + originF, rg = cosRotation * g + sinRotation * f + originG;
        return (h * 3 - rf - rg) + (rf - rg) * (1f/2048);
    }

    /**
     * Just like {@link #getViewDistance()}, but this returns an int for cases where sorting ints is easier.
     * Higher returned values mean the IsoSprite is closer to the camera, and so should be rendered later.
     * @return an int code that will be greater for IsoSprites that are closer to the camera
     */
    public int getSortCode() {
        int bits = NumberUtils.floatToIntBits((h * 3 - f - g) + (f - g) * (1f/2048) + 0f);
        return bits ^ (bits >> 31 & 0x7FFFFFFF);
    }

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    public void draw(Batch batch, float alphaModulation) {
        sprite.draw(batch, alphaModulation);
    }

    public void draw(Batch batch, float originF, float originG, float cosRotation, float sinRotation) {
        float af = f - originF;
        float ag = g - originG;
        float rf = cosRotation * af - sinRotation * ag + originF;
        float rg = cosRotation * ag + sinRotation * af + originG;
        float worldX = (rf - rg) * (2 * UNIT);
        float worldY = (rf + rg) * UNIT + h * (2 * UNIT);
        sprite.setPosition(worldX, worldY);
        sprite.draw(batch);
    }

    public void draw(Batch batch, float alphaModulation, float originF, float originG, float cosRotation, float sinRotation) {
        float af = f - originF;
        float ag = g - originG;
        float rf = cosRotation * af - sinRotation * ag + originF;
        float rg = cosRotation * ag + sinRotation * af + originG;
        float worldX = (rf - rg) * (2 * UNIT);
        float worldY = (rf + rg) * UNIT + h * (2 * UNIT);
        sprite.setPosition(worldX, worldY);
        sprite.draw(batch, alphaModulation);
    }

    /**
     * Does nothing here, but can be overridden in subclasses to do something with a current time.
     * @param stateTime time, typically in seconds, and typically since some event started (like creating this object)
     * @return this object, for chaining
     */
    public IsoSprite update(float stateTime) {
        return this;
    }


    /**
     * Not actually used. We always use an explicit Comparator that takes rotations into account.
     * @param other the object to be compared.
     * @return a negative int, 0, or a positive int, depending on if the view distance for this is less than, equal to, or greater than other's view distance
     */
    @Override
    public int compareTo(IsoSprite other) {
        return NumberUtils.floatToIntBits(getViewDistance() - other.getViewDistance() + 0f);
    }

    @Override
    public String toString() {
        return "IsoSprite{" +
            "f=" + f +
            ", g=" + g +
            ", h=" + h +
            '}';
    }
}
""",
    )
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage,
      fileName = "AnimatedIsoSprite.java",
      content =
        """package ${project.basic.rootPackage};

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A variant on {@link IsoSprite} that changes its visual Sprite based on an animation, typically a looping one.
 */
public class AnimatedIsoSprite extends IsoSprite {
    /**
     * The Animation of Sprite that determines which Sprite will be currently shown.
     */
    public Animation<? extends Sprite> animation;

    /**
     * Creates an empty AnimatedIsoSprite with no animation set.
     */
    private AnimatedIsoSprite() {
        super();
    }

    /**
     * Creates an AnimatedIsoSprite with the given Animation of Sprite, setting the initial visual to its first frame.
     * @param animation an Animation of Sprite or subclasses of Sprite
     */
    public AnimatedIsoSprite(Animation<? extends Sprite> animation) {
        super(animation.getKeyFrame(0, true));
        this.animation = animation;
    }

    /**
     * Creates an AnimatedIsoSprite with the given Animation of Sprite, setting the initial visual to its first frame.
     * Places it at the given isometric tile position.
     * @param animation an Animation of Sprite or subclasses of Sprite
     * @param f isometric tile f-coordinate
     * @param g isometric tile g-coordinate
     * @param h isometric tile h-coordinate
     */
    public AnimatedIsoSprite(Animation<? extends Sprite> animation, float f, float g, float h) {
        super(animation.getKeyFrame(0, true));
        this.animation = animation;
        setPosition(f, g, h);
    }


    /**
     * Acts just like the superclass implementation, {@link IsoSprite#setPosition(float, float, float)}, but also sets
     * each Sprite's position in the animation.
     * @param f isometric tile f-coordinate
     * @param g isometric tile g-coordinate
     * @param h isometric tile h-coordinate
     */
    @Override
    public void setPosition(float f, float g, float h) {
        this.f = f;
        this.g = g;
        this.h = h;
        float worldX = (f - g) * (2 * UNIT);
        float worldY = (f + g) * UNIT + h * (2 * UNIT);
        for (Sprite s : animation.getKeyFrames()) {
            s.setPosition(worldX, worldY);
        }
    }

    /**
     * Acts just like the superclass implementation, {@link IsoSprite#setOriginBasedPosition(float, float, float)} , but
     * also sets each Sprite's origin-based position in the animation.
     * @param f isometric tile f-coordinate
     * @param g isometric tile g-coordinate
     * @param h isometric tile h-coordinate
     */
    @Override
    public void setOriginBasedPosition(float f, float g, float h) {
        this.f = f;
        this.g = g;
        this.h = h;
        float worldX = (f - g) * (2 * UNIT);
        float worldY = (f + g) * UNIT + h * (2 * UNIT);
        for (Sprite s : animation.getKeyFrames()) {
            s.setOriginBasedPosition(worldX, worldY);
        }
    }

    /**
     * Acts just like the superclass implementation, {@link IsoSprite#setOrigin(float, float)}, but also sets each
     * Sprite's origin in the animation.
     * @param originX x relative to the Sprite's position, in world coordinates
     * @param originY y relative to the Sprite's position, in world coordinates
     */
    @Override
    public void setOrigin(float originX, float originY) {
        for (Sprite s : animation.getKeyFrames()) {
            s.setOrigin(originX, originY);
        }
    }

    /**
     * Acts just like the superclass implementation, {@link IsoSprite#setOriginCenter()}, but also sets the origin to
     * center for each Sprite in the animation.
     */
    @Override
    public void setOriginCenter() {
        for (Sprite s : animation.getKeyFrames()) {
            s.setOriginCenter();
        }
    }

    /**
     * Changes the currently drawn Sprite based on the current key frame in {@link #animation} given {@code stateTime}.
     * This delegates to {@link #update(float, boolean)} with {@code looping} set to true.
     *
     * @param stateTime time, in seconds; typically since this was constructed or since the last time the game un-paused
     */
    @Override
    public AnimatedIsoSprite update(float stateTime) {
        return update(stateTime, true);
    }

    /**
     * Changes the currently drawn Sprite based on the current key frame in {@link #animation} given {@code stateTime}.
     * If {@code looping} is true, then sequential {@link Animation.PlayMode} settings will always loop, or if looping
     * is false, then if stateTime is greater than {@link Animation#getAnimationDuration()}, the last Sprite will be
     * used instead of wrapping around.
     *
     * @param stateTime time, in seconds; typically since this was constructed or since the last time the game un-paused
     * @param looping if true, the animation will always loop to the beginning; if false, it will end on the last Sprite
     *               in the animation if stateTime is too high
     */
    public AnimatedIsoSprite update(float stateTime, boolean looping) {
        super.setSprite(animation.getKeyFrame(stateTime, looping));
        return this;
    }
}
""",
    )
    // util folder
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage + ".util",
      fileName = "HasPosition3D.java",
      content =
        """package ${project.basic.rootPackage}.util;

import com.badlogic.gdx.math.Vector3;

/**
 * A simple interface for anything that can have its position requested as a Vector3. This usually means an isometric
 * tile position here, but could be another 3D position in other code. This is used mainly by {@link VoxelCollider}.
 */
public interface HasPosition3D {
    /**
     * Gets the position of this object as a Vector3
     * @return this object's Vector3 position, which should be a direct reference for if code needs to modify it
     */
    Vector3 getPosition();
}
""",
    )
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage + ".util",
      fileName = "MathSupport.java",
      content =
        """package ${project.basic.rootPackage}.util;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Static utility methods and constants for basic math operations, and some not-so-basic ones.
 */
public final class MathSupport {

    /**
     * Cannot be instantiated.
     */
    private MathSupport(){}

    /**
     * The square root of 2, as a float. Useful as the length of a diagonal on a square with length-1 sides.
     */
    public static final float ROOT_2 = (float) Math.sqrt(2f);
    /**
     * 1 divided by the square root of 2, as a float. Scaling by this is useful to take a vector such as
     * {@code [1, 1]} and make it the same length as the vector {@code [1,0]} or {@code [0,-1]}.
     */
    public static final float INVERSE_ROOT_2 = 1f / ROOT_2;

    /**
     * Sets {@code changing} to the point at the given {@code index} into the "R2 Sequence", a sequence of points that
     * remain separated from each other while seeming random for many sequential indices. Scales the point so it fits
     * between 0,0 (inclusive) and the given width and height (exclusive).
     * <br>
     * <a href="https://extremelearning.com.au/unreasonable-effectiveness-of-quasirandom-sequences/">See this article</a>
     * for more on the R2 sequence, including both pretty pictures and serious math.
     * @param changing the GridPoint2 that will be modified
     * @param index the index into the R2 sequence, often a small positive number but can be any long
     * @param width the width of the area to potentially place {@code changing}
     * @param height the height of the area to potentially place {@code changing}
     * @return {@code changing}, after modifications
     */
    public static GridPoint2 fillR2(GridPoint2 changing, long index, int width, int height) {
        long ix = index * 0xC13FA9A902A6328FL;
        long iy = index * 0x91E10DA5C79E7B1DL;
        double x = (ix >>> 1) * (1.0842021724855043E-19 * width); //1.0842021724855043E-19 is just under pow(2, -63)
        double y = (iy >>> 1) * (1.0842021724855043E-19 * height); //1.0842021724855043E-19 is just under pow(2, -63)
        changing.set((int)x, (int)y);
        return changing;
    }
    /**
     * Reads in a CharSequence containing only decimal digits (only 0-9) with an optional sign at the start
     * and returns the long they represent, reading at most 19 characters (20 if there is a sign) and returning the
     * result if valid, or 0 if nothing could be read. The leading sign can be '+' or '-' if present. This can also
     * represent negative numbers as they are printed as unsigned longs. This means "18446744073709551615" would
     * return the long -1 when passed to this, though you could also simply use "-1" . If you use both '-' at the start
     * and have the number as greater than {@link Long#MAX_VALUE}, such as with "-18446744073709551615", then both
     * indicate a negative number, but the digits will be processed first (producing -1) and then the whole thing will
     * be multiplied by -1 to flip the sign again (returning 1).
     * <br>
     * Should be fairly close to Java 8's Long.parseUnsignedLong method, which is an odd omission from earlier JDKs.
     * This doesn't throw on invalid input, though, instead returning 0 if the first char is not a decimal digit, or
     * stopping the parse process early if a non-0-9 char is read before end is reached. If the parse is stopped
     * early, this behaves as you would expect for a number with fewer digits, and simply doesn't fill the larger places.
     * <br>
     * This code was taken from <a href="https://github.com/tommyettinger/digital">digital</a> and its Base class, with
     * any numbers for that class' variable radix hard-coded to 10 because that's all we need here. Though there is a
     * similar method in Java 8, it isn't available to RoboVM, and even then it's not nearly as tolerant of invalid
     * inputs as this method (this one parses what it can and returns what it can, rather than throwing an exception).
     *
     * @param cs    a CharSequence, such as a String, containing decimal digits with an optional sign
     * @param start the (inclusive) first character position in cs to read
     * @param end   the (exclusive) last character position in cs to read (this stops after 20 characters if end is too large)
     * @return the long that cs represents
     */
    public static long longFromDec(final CharSequence cs, final int start, int end) {
        int sign, h, lim;
        if (cs == null || start < 0 || end <= 0 || (end = Math.min(end, cs.length())) - start <= 0)
            return 0;
        char c = cs.charAt(start);
        if (c == '-') {
            sign = -1;
            h = 0;
            lim = 21;
        } else if (c == '+') {
            sign = 1;
            h = 0;
            lim = 21;
        } else {
            if (!(c >= '0' && c <= '9'))
                return 0;
            else {
                sign = 1;
                lim = 20;
            }
            h = (c - '0');
        }
        long data = h;
        for (int i = start + 1; i < end && i < start + lim; i++) {
            c = cs.charAt(i);
            if (!(c >= '0' && c <= '9'))
                return data * sign;
            data *= 10;
            data += (c - '0');
        }
        return data * sign;
    }


    /**
     * Reads in a CharSequence containing only decimal digits (only 0-9) with an optional sign at the start
     * and returns the int they represent, reading at most 10 characters (11 if there is a sign) and returning the
     * result if valid, or 0 if nothing could be read. The leading sign can be '+' or '-' if present. This can also
     * represent negative numbers as they are printed as unsigned integers. This means "4294967295" would return the int
     * -1 when passed to this, though you could also simply use "-1" . If you use both '-' at the start and have the
     * number as greater than {@link Integer#MAX_VALUE}, such as with "-4294967295", then both indicate a negative
     * number, but the digits will be processed first (producing -1) and then the whole thing will be multiplied by -1
     * to flip the sign again (returning 1).
     * <br>
     * Should be fairly close to Java 8's Integer.parseUnsignedInt method, which is an odd omission from earlier JDKs.
     * This doesn't throw on invalid input, though, instead returning 0 if the first char is not a decimal digit, or
     * stopping the parse process early if a non-0-9 char is read before end is reached. If the parse is stopped
     * early, this behaves as you would expect for a number with fewer digits, and simply doesn't fill the larger places.
     * <br>
     * This code was taken from <a href="https://github.com/tommyettinger/digital">digital</a> and its Base class, with
     * any numbers for that class' variable radix hard-coded to 10 because that's all we need here. Though there is a
     * similar method in Java 8, it isn't available to RoboVM, and even then it's not nearly as tolerant of invalid
     * inputs as this method (this one parses what it can and returns what it can, rather than throwing an exception).
     *
     * @param cs    a CharSequence, such as a String, containing decimal digits with an optional sign
     * @param start the (inclusive) first character position in cs to read
     * @param end   the (exclusive) last character position in cs to read (this stops after 10 characters if end is too large)
     * @return the int that cs represents
     */
    public static int intFromDec(final CharSequence cs, final int start, int end) {
        int sign, h, lim;
        if (cs == null || start < 0 || end <= 0 || (end = Math.min(end, cs.length())) - start <= 0)
            return 0;
        char c = cs.charAt(start);
        if (c == '-') {
            sign = -1;
            h = 0;
            lim = 11;
        } else if (c == '+') {
            sign = 1;
            h = 0;
            lim = 11;
        } else {
            if (!(c >= '0' && c <= '9'))
                return 0;
            else {
                sign = 1;
                lim = 10;
            }
            h = (c - '0');
        }
        int data = h;
        for (int i = start + 1; i < end && i < start + lim; i++) {
            c = cs.charAt(i);
            if (!(c >= '0' && c <= '9'))
                return data * sign;
            data *= 10;
            data += (c - '0');
        }
        return data * sign;
    }

    /**
     * Reads a float in from the String {@code str}, using only the range from {@code start} (inclusive) to {@code end}
     * (exclusive). This effectively returns {@code Float.parseFloat(str.substring(start, Math.min(str.length(), end)))}
     * . Unlike the other number-reading methods here, this doesn't do much to validate its input, so the end of the
     * String must be after the full float number. Because of how parseFloat() works, whitespace will be trimmed out of
     * the substring if present, including space and any ASCII control chars. If the parse fails, this returns 0f.
     * This can handle a wider variety of literals than one might expect; it tolerates a trailing {@code f} or even
     * {@code d} at the end, and in addition to the standard {@code 12.34} decimal notation, it can parse scientific
     * notation, {@code 1.234e+01}, as well as hexadecimal float notation, {@code 0x1.8ae148p3}.
     * @param str a String containing a valid float in the specified range
     * @param start the start index (inclusive) to read from
     * @param end the end index (exclusive) to stop reading before
     * @return the parsed float from the given range, or 0f if the parse failed.
     */
    public static float floatFromDec(final String str, final int start, int end) {
        try {
            return Float.parseFloat(str.substring(start, Math.min(str.length(), end)));
        } catch (NumberFormatException ignored) {
            return 0f;
        }
    }

}
""",
    )
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage + ".util",
      fileName = "MiniNoise.java",
      content =
        """package ${project.basic.rootPackage}.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;

import static com.badlogic.gdx.math.MathUtils.floor;
import static com.badlogic.gdx.math.MathUtils.lerp;
import static ${project.basic.rootPackage}.util.MiniNoise.GradientVectors.*;

/**
 * The noise class, for "continuous noise" in the signal-vs.-noise sense. This doesn't make sounds!
 * If you request similar points in space from this, you will almost always get similar results as floats.
 * If you request very distant points in space from this, you should not notice any patterns between inputs and outputs.
 * This is useful for making organic-looking maps, though you might need to get creative to turn one float into a piece
 * of map terrain!
 * <br>
 * This was adapted from <a href="https://github.com/tommyettinger/cringe">Cringe</a> and its ContinuousNoise class,
 * which can also use the type of noise algorithm here, "Perlue Noise". This whole class was originally more than one
 * file, and it's long because so much has been smashed together here. But, this file is at least standalone, requiring
 * libGDX but nothing else.
 * <br>
 * If you are using this project as a basis for your own game, it probably makes more sense to depend on one of:
 * <ul>
 *     <li><a href="https://github.com/tommyettinger/cringe">Cringe</a></li>
 *     <li><a href="https://github.com/czyzby/noise4j">Noise4J</a></li>
 *     <li><a href="https://github.com/SudoPlayGames/Joise">Joise</a></li>
 *     <li><a href="https://github.com/yellowstonegames/SquidSquad">SquidSquad</a> (maybe)</li>
 * </ul>
 * And to use any of those libraries for their more robust continuous noise code.
 * Each of the suggested libraries is an option in gdx-liftoff's third-party extensions.
 */
@SuppressWarnings("DefaultNotLastCaseInSwitch")
public class MiniNoise {

    /**
     * "Standard" layered octaves of noise, where each octave has a different frequency and weight.
     * Tends to look cloudy with more octaves, and generally like a natural process. This only has
     * an effect with 2 octaves or more.
     * <br>
     * Meant to be used with {@link #setMode(int)}.
     */
    public static final int FBM = 0;
    /**
     * A less common way to layer octaves of noise, where most results are biased toward higher values,
     * but "valleys" show up filled with much lower values.
     * This probably has some good uses in 3D or higher noise, but it isn't used too frequently.
     * <br>
     * Meant to be used with {@link #setMode(int)}.
     */
    public static final int BILLOW = 1;
    /**
     * A way to layer octaves of noise so most values are biased toward low values but "ridges" of high
     * values run across the noise. This can be a good way of highlighting the least-natural aspects of
     * some kinds of noise; Perlin Noise has mostly ridges along 45-degree angles,
     * Simplex Noise has many ridges along a triangular grid, and so on. The Perlue Noise used here doesn't
     * look nearly as unnatural as the Perlin and Simplex types that are more common.
     * <br>
     * Meant to be used with {@link #setMode(int)}.
     */
    public static final int RIDGED = 2;
    /**
     * Layered octaves of noise, where each octave has a different frequency and weight, and the results of
     * earlier octaves affect the inputs to later octave calculations. Tends to look cloudy but with swirling
     * distortions, and generally like a natural process. This only has an effect with 2 octaves or more.
     * <br>
     * Meant to be used with {@link #setMode(int)}.
     */
    public static final int WARP = 3;

    /**
     * The names that correspond to the numerical mode constants, with the constant value matching the index here.
     */
    public static final String[] MODES = {"FBM", "Billow", "Ridged", "Warp"};

    /**
     * A ContinuousNoise always wraps a noise algorithm, which here is always a PerlueNoise.
     * If you want a more general solution, you can use the very similar API in
     * <a href="https://github.com/tommyettinger/cringe">Cringe</a>, a third-party dependency.
     */
    public PerlueNoise wrapped;
    /**
     * If high, details show up very often, if low, details are stretched out and take longer to change.
     * This works by multiplying the frequency with each coordinate to a noise call, like x, y, or z.
     */
    public float frequency;
    /**
     * One of 0, 1, 2, or 3, corresponding to {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}.
     */
    public int mode;
    /**
     * How many layers of noise with different frequencies to use. Using 2 octaves or more is necessary with
     * {@link #WARP} mode, and other modes tend to look better with more octaves, but the more octaves you request, the
     * slower the noise is to calculate, and there's typically no benefit to using more than 8 or so octaves here.
     */
    protected int octaves;

    /**
     * Creates a MiniNoise with seed 123, frequency (1f/32f), FBM mode, and 1 octave.
     */
    public MiniNoise() {
        this(123, 0.03125f, FBM, 1);

    }

    /**
     * Creates a MiniNoise with the given PerlueNoise to wrap, frequency (1f/32f), FBM mode, and 1 octave.
     */
    public MiniNoise(PerlueNoise toWrap){
        this(toWrap, 0.03125f, FBM, 1);
    }

    /**
     * Creates a MiniNoise wrapping the given PerlueNoise, with the given frequency (which should usually be small, less
     * than 0.5f), mode (which can be 0, 1, 2, or 3, and is usually a constant from this class), and octaves (usually 1
     * to at most 8 or so).
     * @param toWrap the PerlueNoise to wrap; this will use its seed
     * @param frequency the desired frequency, which is always greater than 0.0f but only by a small amount
     * @param mode the mode, which can be {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}
     * @param octaves how many layers of noise to use with different frequencies; usually between 1 and 8
     */
    public MiniNoise(PerlueNoise toWrap, float frequency, int mode, int octaves){
        wrapped = toWrap;
        this.frequency = frequency;
        this.mode = mode;
        this.octaves = octaves;
    }

    /**
     * Creates a MiniNoise with the given seed, with the given frequency (which should usually be small, less
     * than 0.5f), mode (which can be 0, 1, 2, or 3, and is usually a constant from this class), and octaves (usually 1
     * to at most 8 or so).
     * @param seed a PerlueNoise will be created for this to use with this seed
     * @param frequency the desired frequency, which is always greater than 0.0f but only by a small amount
     * @param mode the mode, which can be {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}
     * @param octaves how many layers of noise to use with different frequencies; usually between 1 and 8
     */
    public MiniNoise(int seed, float frequency, int mode, int octaves){
        wrapped = new PerlueNoise(seed);
        this.frequency = frequency;
        this.mode = mode;
        this.octaves = octaves;
    }

    /**
     * Copies another MiniNoise, including copying its internal PerlueNoise.
     * @param other another MiniNoise to copy
     */
    public MiniNoise(MiniNoise other) {
        setWrapped(other.getWrapped().copy());
        setSeed(other.getSeed());
        setFrequency(other.getFrequency());
        setFractalType(other.getFractalType());
        setFractalOctaves(other.getFractalOctaves());
    }

    /**
     * Gets the internal PerlueNoise this wraps.
     * @return a PerlueNoise, which is the only algorithm here
     */
    public PerlueNoise getWrapped() {
        return wrapped;
    }

    /**
     * Sets the internal PerlueNoise this wraps.
     * @param wrapped the PerlueNoise to wrap; it's the only algorithm here
     */
    public void setWrapped(PerlueNoise wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * High frequency makes details occur more often; low frequency makes details stretch out over a larger area.
     * Typically, this is a low float, such as {@code 1f/32f}.
     * @return the current frequency
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * High frequency makes details occur more often; low frequency makes details stretch out over a larger area.
     * Typically, this is a low float, such as {@code 1f/32f}.
     * @param frequency the frequency to use
     */
    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    /**
     * Wraps {@link #getFractalType()}.
     * @return an int between 0 and 3, corresponding to {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}
     */
    public int getMode() {
        return getFractalType();
    }

    /**
     * Wraps {@link #setFractalType(int)}
     * @param mode an int between 0 and 3, corresponding to {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}
     */
    public void setMode(int mode) {
        setFractalType(mode);
    }

    /**
     * Gets the current mode, which is {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}.
     * @return an int between 0 and 3, corresponding to {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}
     */
    public int getFractalType() {
        return mode;
    }

    /**
     * Sets the current mode to one of {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}.
     * @param mode an int between 0 and 3, corresponding to {@link #FBM}, {@link #BILLOW}, {@link #RIDGED}, or {@link #WARP}
     */
    public void setFractalType(int mode) {
        this.mode = (mode & 3);
    }

    /**
     * Wraps {@link #getFractalOctaves()}.
     * @return how many octaves this uses to increase detail
     */
    public int getOctaves() {
        return getFractalOctaves();
    }

    /**
     * Wraps {@link #setFractalOctaves(int)}.
     * @param octaves how many octaves to use to increase detail; must be at least 1.
     */
    public void setOctaves(int octaves) {
        setFractalOctaves(octaves);
    }

    /**
     * Gets how many octaves this uses to increase detail.
     * @return how many octaves this uses to increase detail
     */
    public int getFractalOctaves() {
        return octaves;
    }

    /**
     * Sets how many octaves this uses to increase detail; always at least 1.
     * @param octaves how many octaves to use to increase detail; must be at least 1.
     */
    public void setFractalOctaves(int octaves) {
        this.octaves = Math.max(1, octaves);
    }

    /**
     *
     * @return PerlueNoise's minimum dimension, which is 1.
     */
    public int getMinDimension() {
        return wrapped.getMinDimension();
    }

    /**
     * @return PerlueNoise's maximum dimension, which is currently 4.
     */
    public int getMaxDimension() {
        return wrapped.getMaxDimension();
    }

    /**
     * Used by Cringe, at least, to serialize its noise concisely. This doesn't really need to be used here.
     * @return a short String describing what noise class this is
     */
    public String getTag() {
        return "MiniNoise";
    }

    /**
     * Saves this MiniNoise to a String and returns it.
     * @return a String that can be fed to {@link #stringDeserialize(String)} to recreate this MiniNoise
     */
    public String stringSerialize() {
        return "`" + wrapped.seed + '~' +
                frequency + '~' +
                mode + '~' +
                octaves + '`';
    }

    /**
     * Reassigns this MiniNoise to use the serialized state produced earlier from {@link #stringSerialize()}.
     * @param data a String produced by {@link #stringSerialize()}
     * @return this MiniNoise, after restoring the state in data
     */
    public MiniNoise stringDeserialize(String data) {
        int pos = data.indexOf('`', data.indexOf('`', 2) + 1)+1;
        setWrapped(new PerlueNoise(MathSupport.intFromDec(data, 1, pos)));
        setFrequency(MathSupport.floatFromDec(data, pos+1, pos = data.indexOf('~', pos+2)));
        setMode(MathSupport.intFromDec(data, pos+1, pos = data.indexOf('~', pos+2)));
        setOctaves(MathSupport.intFromDec(data, pos+1, pos = data.indexOf('`', pos+2)));
        return this;
    }

    /**
     * Creates a MiniNoise from a String produced by {@link #stringSerialize()}.
     * @param data a serialized String, typically produced by {@link #stringSerialize()}
     * @return a new MiniNoise, using the restored state from data
     */
    public static PerlueNoise recreateFromString(String data) {
        return new PerlueNoise(1).stringDeserialize(data);
    }

    /**
     * @return returns a copy of this MiniNoise made with {@link #MiniNoise(MiniNoise)}
     */
    public MiniNoise copy() {
        return new MiniNoise(this);
    }

    public String toString() {
        return "MiniNoise{" +
                "wrapped=" + wrapped +
                ", frequency=" + frequency +
                ", mode=" + mode +
                ", octaves=" + octaves +
                '}';
    }

    /**
     * Gets a more readable version of toString(), with the mode named rather than shown as a number.
     * @return a human-readable summary of this MiniNoise
     */
    public String toHumanReadableString() {
        return getTag() + " wrapping (" + wrapped.toHumanReadableString() + "), with frequency " + frequency +
                ", " + octaves + " octaves, and mode " + MODES[mode];
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MiniNoise that = (MiniNoise) o;

        if (Float.compare(that.frequency, frequency) != 0) return false;
        if (mode != that.mode) return false;
        if (octaves != that.octaves) return false;
        return wrapped.equals(that.wrapped);
    }

    public int hashCode() {
        int result = wrapped.hashCode();
        result = 31 * result + NumberUtils.floatToIntBits(frequency + 0f);
        result = 31 * result + mode;
        result = 31 * result + octaves;
        return result;
    }

    // The big part.

    /**
     * 1D noise using the current seed, frequency, octaves, and mode.
     * @param x 1D coordinate
     * @return a float between -1 and 1
     */
    public float getNoise(float x) {
        final int seed = wrapped.getSeed();
        switch (mode) {
            default:
            case 0: return fbm(x * frequency, seed);
            case 1: return billow(x * frequency, seed);
            case 2: return ridged(x * frequency, seed);
            case 3: return warp(x * frequency, seed);
        }
    }

    /**
     * 2D noise using the current seed, frequency, octaves, and mode.
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return a float between -1 and 1
     */
    public float getNoise(float x, float y) {
        final int seed = wrapped.getSeed();
        switch (mode) {
            default:
            case 0: return fbm(x * frequency, y * frequency, seed);
            case 1: return billow(x * frequency, y * frequency, seed);
            case 2: return ridged(x * frequency, y * frequency, seed);
            case 3: return warp(x * frequency, y * frequency, seed);
        }
    }
    /**
     * 3D noise using the current seed, frequency, octaves, and mode.
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param z depth coordinate (can also be time)
     * @return a float between -1 and 1
     */
    public float getNoise(float x, float y, float z) {
        final int seed = wrapped.getSeed();
        switch (mode) {
            default:
            case 0: return fbm(x * frequency, y * frequency, z * frequency, seed);
            case 1: return billow(x * frequency, y * frequency, z * frequency, seed);
            case 2: return ridged(x * frequency, y * frequency, z * frequency, seed);
            case 3: return warp(x * frequency, y * frequency, z * frequency, seed);
        }
    }

    /**
     * 4D noise using the current seed, frequency, octaves, and mode.
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param z depth coordinate
     * @param w higher-dimensional coordinate (time?)
     * @return a float between -1 and 1
     */
    public float getNoise(float x, float y, float z, float w) {
        final int seed = wrapped.getSeed();
        switch (mode) {
            default:
            case 0: return fbm(x * frequency, y * frequency, z * frequency, w * frequency, seed);
            case 1: return billow(x * frequency, y * frequency, z * frequency, w * frequency, seed);
            case 2: return ridged(x * frequency, y * frequency, z * frequency, w * frequency, seed);
            case 3: return warp(x * frequency, y * frequency, z * frequency, w * frequency, seed);
        }
    }

    public void setSeed(int seed) {
        wrapped.setSeed(seed);
    }

    public int getSeed() {
        return wrapped.getSeed();
    }

    /**
     * 1D noise forcing the given seed and using the current frequency, octaves, and mode.
     * @param x 1D coordinate
     * @param seed any int; must be the same between noise that should connect seamlessly
     * @return a float between -1 and 1
     */
    public float getNoiseWithSeed(float x, int seed) {
        switch (mode) {
            default:
            case 0: return fbm(x * frequency, seed);
            case 1: return billow(x * frequency, seed);
            case 2: return ridged(x * frequency, seed);
            case 3: return warp(x * frequency, seed);
        }
    }

    /**
     * 2D noise forcing the given seed and using the current frequency, octaves, and mode.
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param seed any int; must be the same between noise that should connect seamlessly
     * @return a float between -1 and 1
     */
    public float getNoiseWithSeed(float x, float y, int seed) {
        switch (mode) {
            default:
            case 0: return fbm(x * frequency, y * frequency, seed);
            case 1: return billow(x * frequency, y * frequency, seed);
            case 2: return ridged(x * frequency, y * frequency, seed);
            case 3: return warp(x * frequency, y * frequency, seed);
        }
    }

    /**
     * 3D noise forcing the given seed and using the current frequency, octaves, and mode.
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param z depth coordinate (can also be time)
     * @param seed any int; must be the same between noise that should connect seamlessly
     * @return a float between -1 and 1
     */
    public float getNoiseWithSeed(float x, float y, float z, int seed) {
        switch (mode) {
            default:
            case 0: return fbm(x * frequency, y * frequency, z * frequency, seed);
            case 1: return billow(x * frequency, y * frequency, z * frequency, seed);
            case 2: return ridged(x * frequency, y * frequency, z * frequency, seed);
            case 3: return warp(x * frequency, y * frequency, z * frequency, seed);
        }
    }

    /**
     * 4D noise forcing the given seed and using the current frequency, octaves, and mode.
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param z depth coordinate
     * @param w higher-dimensional coordinate (time?)
     * @param seed any int; must be the same between noise that should connect seamlessly
     * @return a float between -1 and 1
     */
    public float getNoiseWithSeed(float x, float y, float z, float w, int seed) {
        switch (mode) {
            default:
            case 0: return fbm(x * frequency, y * frequency, z * frequency, w * frequency, seed);
            case 1: return billow(x * frequency, y * frequency, z * frequency, w * frequency, seed);
            case 2: return ridged(x * frequency, y * frequency, z * frequency, w * frequency, seed);
            case 3: return warp(x * frequency, y * frequency, z * frequency, w * frequency, seed);
        }
    }

    // 1D noise variants.

    protected float fbm(float x, int seed) {
        float sum = wrapped.getNoiseWithSeed(x, seed);
        if(octaves <= 1) return sum;

        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;

            amp *= 0.5f;
            sum += wrapped.getNoiseWithSeed(x, seed + i) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }
    protected float billow(float x, int seed) {
        float sum = Math.abs(wrapped.getNoiseWithSeed(x, seed)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;

            amp *= 0.5f;
            sum += (Math.abs(wrapped.getNoiseWithSeed(x, seed + i)) * 2 - 1) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }

    protected float ridged(float x, int seed) {
        float sum = 0f, exp = 1f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(wrapped.getNoiseWithSeed(x, seed + i));
            sum += spike * exp;
            correction += (exp *= 0.5f);
            x *= 2f;
        }
        return sum / correction - 1f;
    }

    protected float warp(float x, int seed) {
        float latest = wrapped.getNoiseWithSeed(x, seed);
        if(octaves <= 1) return latest;

        float sum = latest;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x = x * 2f;
            final float idx = latest * 180;
            float a = MathUtils.sinDeg(idx);
            amp *= 0.5f;
            sum += (latest = wrapped.getNoiseWithSeed(x + a, seed + i)) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }

    // 2D noise variants.

    protected float fbm(float x, float y, int seed) {
        float sum = wrapped.getNoiseWithSeed(x, y, seed);
        if(octaves <= 1) return sum;

        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;

            amp *= 0.5f;
            sum += wrapped.getNoiseWithSeed(x, y, seed + i) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }
    protected float billow(float x, float y, int seed) {
        float sum = Math.abs(wrapped.getNoiseWithSeed(x, y, seed)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;

            amp *= 0.5f;
            sum += (Math.abs(wrapped.getNoiseWithSeed(x, y, seed + i)) * 2 - 1) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }

    protected float ridged(float x, float y, int seed) {
        float sum = 0f, exp = 1f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(wrapped.getNoiseWithSeed(x, y, seed + i));
            sum += spike * exp;
            correction += (exp *= 0.5f);
            x *= 2f;
            y *= 2f;
        }
        return sum / correction - 1f;
    }

    protected float warp(float x, float y, int seed) {
        float latest = wrapped.getNoiseWithSeed(x, y, seed);
        if(octaves <= 1) return latest;

        float sum = latest;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x = x * 2f;
            y = y * 2f;
            final float idx = latest * 180;
            float a = MathUtils.sinDeg(idx);
            float b = MathUtils.sinDeg(idx + (180/2f));
            amp *= 0.5f;
            sum += (latest = wrapped.getNoiseWithSeed(x + a, y + b, seed + i)) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }

    // 3D noise variants.

    protected float fbm(float x, float y, float z, int seed) {
        float sum = wrapped.getNoiseWithSeed(x, y, z, seed);
        if(octaves <= 1) return sum;

        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;
            z *= 2f;

            amp *= 0.5f;
            sum += wrapped.getNoiseWithSeed(x, y, z, seed + i) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }
    protected float billow(float x, float y, float z, int seed) {
        float sum = Math.abs(wrapped.getNoiseWithSeed(x, y, z, seed)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;
            z *= 2f;

            amp *= 0.5f;
            sum += (Math.abs(wrapped.getNoiseWithSeed(x, y, z, seed + i)) * 2 - 1) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }

    protected float ridged(float x, float y, float z, int seed) {
        float sum = 0f, exp = 1f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(wrapped.getNoiseWithSeed(x, y, z, seed + i));
            sum += spike * exp;
            correction += (exp *= 0.5f);
            x *= 2f;
            y *= 2f;
            z *= 2f;
        }
        return sum / correction - 1f;
    }

    protected float warp(float x, float y, float z, int seed) {
        float latest = wrapped.getNoiseWithSeed(x, y, z, seed);
        if(octaves <= 1) return latest;

        float sum = latest;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;
            z *= 2f;

            final float idx = latest * 180;
            float a = MathUtils.sinDeg(idx);
            float b = MathUtils.sinDeg(idx + (180/3f));
            float c = MathUtils.sinDeg(idx + (180*2/3f));

            amp *= 0.5f;
            sum += (latest = wrapped.getNoiseWithSeed(x + a, y + b, z + c, seed + i)) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }

    // 4D noise variants.

    protected float fbm(float x, float y, float z, float w, int seed) {
        float sum = wrapped.getNoiseWithSeed(x, y, z, w, seed);
        if(octaves <= 1) return sum;

        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;
            z *= 2f;
            w *= 2f;

            amp *= 0.5f;
            sum += wrapped.getNoiseWithSeed(x, y, z, w, seed + i) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }
    protected float billow(float x, float y, float z, float w, int seed) {
        float sum = Math.abs(wrapped.getNoiseWithSeed(x, y, z, w, seed)) * 2 - 1;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;
            z *= 2f;
            w *= 2f;

            amp *= 0.5f;
            sum += (Math.abs(wrapped.getNoiseWithSeed(x, y, z, w, seed + i)) * 2 - 1) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }

    protected float ridged(float x, float y, float z, float w, int seed) {
        float sum = 0f, exp = 1f, correction = 0f, spike;
        for (int i = 0; i < octaves; i++) {
            spike = 1f - Math.abs(wrapped.getNoiseWithSeed(x, y, z, w, seed + i));
            sum += spike * exp;
            correction += (exp *= 0.5f);
            x *= 2f;
            y *= 2f;
            z *= 2f;
            w *= 2f;
        }
        return sum / correction - 1f;
    }

    protected float warp(float x, float y, float z, float w, int seed) {
        float latest = wrapped.getNoiseWithSeed(x, y, z, w, seed);
        if(octaves <= 1) return latest;

        float sum = latest;
        float amp = 1;

        for (int i = 1; i < octaves; i++) {
            x *= 2f;
            y *= 2f;
            z *= 2f;
            w *= 2f;

            final float idx = latest * 180;
            float a = MathUtils.sinDeg(idx);
            float b = MathUtils.sinDeg(idx + (180/4f));
            float c = MathUtils.sinDeg(idx + (180*2/4f));
            float d = MathUtils.sinDeg(idx + (180*3/4f));

            amp *= 0.5f;
            sum += (latest = wrapped.getNoiseWithSeed(x + a, y + b, z + c, w + d, seed + i)) * amp;
        }

        return sum / (amp * ((1 << octaves) - 1));
    }

    /**
     * Stores arrays representing vectors on the unit hypersphere in 2D through 4D. This is used by {@link PerlueNoise},
     * as well as indirectly by all classes that use it. Each constant in this class stores 256
     * unit vectors in a 1D array, one after the next, but sometimes with padding.  See the docs for each constant for more
     * information, but {@link #GRADIENTS_2D} and {@link #GRADIENTS_4D} have no padding, and {@link #GRADIENTS_3D} has
     * one ignored float after each vector.
     */
    public static final class GradientVectors {
        /**
         * No need to instantiate.
         */
        private GradientVectors() {}

//<editor-fold defaultstate="collapsed" desc="Huge constant arrays">
        /**
         * 256 equidistant points on the 2D unit circle.
         * Each point is stored in 2 floats, and there no padding. The distance from each
         * point to the origin should be 1.0, subject to rounding error.
         * <br>
         * The points were randomly rotated to try to avoid correlation on any typical axis; all points used the same
         * rotation to keep their distance to each other and shape.
         * <br>
         * This particular set of 256 gradient vectors is either optimal or very close to optimal for this size of a
         * set of vectors.
         */
        public static final float[] GRADIENTS_2D = {
                -0.9995640469f, -0.0295248389f,
                -0.9999875727f, -0.0049854169f,
                -0.9998087434f, +0.0195570081f,
                -0.9990276667f, +0.0440876528f,
                -0.9976448131f, +0.0685917407f,
                -0.9956610156f, +0.0930545115f,
                -0.9930774690f, +0.1174612297f,
                -0.9898957298f, +0.1417971937f,
                -0.9861177144f, +0.1660477444f,
                -0.9817456985f, +0.1901982741f,
                -0.9767823158f, +0.2142342354f,
                -0.9712305559f, +0.2381411501f,
                -0.9650937630f, +0.2619046175f,
                -0.9583756337f, +0.2855103233f,
                -0.9510802148f, +0.3089440483f,
                -0.9432119007f, +0.3321916771f,
                -0.9347754311f, +0.3552392059f,
                -0.9257758877f, +0.3780727520f,
                -0.9162186915f, +0.4006785612f,
                -0.9061095994f, +0.4230430166f,
                -0.8954547008f, +0.4451526467f,
                -0.8842604137f, +0.4669941335f,
                -0.8725334813f, +0.4885543205f,
                -0.8602809673f, +0.5098202206f,
                -0.8475102522f, +0.5307790241f,
                -0.8342290286f, +0.5514181061f,
                -0.8204452967f, +0.5717250345f,
                -0.8061673592f, +0.5916875772f,
                -0.7914038166f, +0.6112937093f,
                -0.7761635620f, +0.6305316210f,
                -0.7604557754f, +0.6493897240f,
                -0.7442899187f, +0.6678566589f,
                -0.7276757296f, +0.6859213020f,
                -0.7106232159f, +0.7035727717f,
                -0.6931426493f, +0.7208004354f,
                -0.6752445595f, +0.7375939160f,
                -0.6569397276f, +0.7539430975f,
                -0.6382391798f, +0.7698381319f,
                -0.6191541805f, +0.7852694447f,
                -0.5996962260f, +0.8002277404f,
                -0.5798770368f, +0.8147040089f,
                -0.5597085515f, +0.8286895302f,
                -0.5392029186f, +0.8421758798f,
                -0.5183724900f, +0.8551549343f,
                -0.4972298132f, +0.8676188753f,
                -0.4757876238f, +0.8795601952f,
                -0.4540588377f, +0.8909717009f,
                -0.4320565436f, +0.9018465186f,
                -0.4097939947f, +0.9121780977f,
                -0.3872846013f, +0.9219602148f,
                -0.3645419221f, +0.9311869775f,
                -0.3415796565f, +0.9398528280f,
                -0.3184116360f, +0.9479525463f,
                -0.2950518163f, +0.9554812534f,
                -0.2715142685f, +0.9624344144f,
                -0.2478131705f, +0.9688078409f,
                -0.2239627992f, +0.9745976937f,
                -0.1999775211f, +0.9798004853f,
                -0.1758717840f, +0.9844130818f,
                -0.1516601083f, +0.9884327046f,
                -0.1273570782f, +0.9918569325f,
                -0.1029773330f, +0.9946837029f,
                -0.0785355581f, +0.9969113131f,
                -0.0540464763f, +0.9985384211f,
                -0.0295248389f, +0.9995640469f,
                -0.0049854169f, +0.9999875727f,
                +0.0195570081f, +0.9998087434f,
                +0.0440876528f, +0.9990276667f,
                +0.0685917407f, +0.9976448131f,
                +0.0930545115f, +0.9956610156f,
                +0.1174612297f, +0.9930774690f,
                +0.1417971937f, +0.9898957298f,
                +0.1660477444f, +0.9861177144f,
                +0.1901982741f, +0.9817456985f,
                +0.2142342354f, +0.9767823158f,
                +0.2381411501f, +0.9712305559f,
                +0.2619046175f, +0.9650937630f,
                +0.2855103233f, +0.9583756337f,
                +0.3089440483f, +0.9510802148f,
                +0.3321916771f, +0.9432119007f,
                +0.3552392059f, +0.9347754311f,
                +0.3780727520f, +0.9257758877f,
                +0.4006785612f, +0.9162186915f,
                +0.4230430166f, +0.9061095994f,
                +0.4451526467f, +0.8954547008f,
                +0.4669941335f, +0.8842604137f,
                +0.4885543205f, +0.8725334813f,
                +0.5098202206f, +0.8602809673f,
                +0.5307790241f, +0.8475102522f,
                +0.5514181061f, +0.8342290286f,
                +0.5717250345f, +0.8204452967f,
                +0.5916875772f, +0.8061673592f,
                +0.6112937093f, +0.7914038166f,
                +0.6305316210f, +0.7761635620f,
                +0.6493897240f, +0.7604557754f,
                +0.6678566589f, +0.7442899187f,
                +0.6859213020f, +0.7276757296f,
                +0.7035727717f, +0.7106232159f,
                +0.7208004354f, +0.6931426493f,
                +0.7375939160f, +0.6752445595f,
                +0.7539430975f, +0.6569397276f,
                +0.7698381319f, +0.6382391798f,
                +0.7852694447f, +0.6191541805f,
                +0.8002277404f, +0.5996962260f,
                +0.8147040089f, +0.5798770368f,
                +0.8286895302f, +0.5597085515f,
                +0.8421758798f, +0.5392029186f,
                +0.8551549343f, +0.5183724900f,
                +0.8676188753f, +0.4972298132f,
                +0.8795601952f, +0.4757876238f,
                +0.8909717009f, +0.4540588377f,
                +0.9018465186f, +0.4320565436f,
                +0.9121780977f, +0.4097939947f,
                +0.9219602148f, +0.3872846013f,
                +0.9311869775f, +0.3645419221f,
                +0.9398528280f, +0.3415796565f,
                +0.9479525463f, +0.3184116360f,
                +0.9554812534f, +0.2950518163f,
                +0.9624344144f, +0.2715142685f,
                +0.9688078409f, +0.2478131705f,
                +0.9745976937f, +0.2239627992f,
                +0.9798004853f, +0.1999775211f,
                +0.9844130818f, +0.1758717840f,
                +0.9884327046f, +0.1516601083f,
                +0.9918569325f, +0.1273570782f,
                +0.9946837029f, +0.1029773330f,
                +0.9969113131f, +0.0785355581f,
                +0.9985384211f, +0.0540464763f,
                +0.9995640469f, +0.0295248389f,
                +0.9999875727f, +0.0049854169f,
                +0.9998087434f, -0.0195570081f,
                +0.9990276667f, -0.0440876528f,
                +0.9976448131f, -0.0685917407f,
                +0.9956610156f, -0.0930545115f,
                +0.9930774690f, -0.1174612297f,
                +0.9898957298f, -0.1417971937f,
                +0.9861177144f, -0.1660477444f,
                +0.9817456985f, -0.1901982741f,
                +0.9767823158f, -0.2142342354f,
                +0.9712305559f, -0.2381411501f,
                +0.9650937630f, -0.2619046175f,
                +0.9583756337f, -0.2855103233f,
                +0.9510802148f, -0.3089440483f,
                +0.9432119007f, -0.3321916771f,
                +0.9347754311f, -0.3552392059f,
                +0.9257758877f, -0.3780727520f,
                +0.9162186915f, -0.4006785612f,
                +0.9061095994f, -0.4230430166f,
                +0.8954547008f, -0.4451526467f,
                +0.8842604137f, -0.4669941335f,
                +0.8725334813f, -0.4885543205f,
                +0.8602809673f, -0.5098202206f,
                +0.8475102522f, -0.5307790241f,
                +0.8342290286f, -0.5514181061f,
                +0.8204452967f, -0.5717250345f,
                +0.8061673592f, -0.5916875772f,
                +0.7914038166f, -0.6112937093f,
                +0.7761635620f, -0.6305316210f,
                +0.7604557754f, -0.6493897240f,
                +0.7442899187f, -0.6678566589f,
                +0.7276757296f, -0.6859213020f,
                +0.7106232159f, -0.7035727717f,
                +0.6931426493f, -0.7208004354f,
                +0.6752445595f, -0.7375939160f,
                +0.6569397276f, -0.7539430975f,
                +0.6382391798f, -0.7698381319f,
                +0.6191541805f, -0.7852694447f,
                +0.5996962260f, -0.8002277404f,
                +0.5798770368f, -0.8147040089f,
                +0.5597085515f, -0.8286895302f,
                +0.5392029186f, -0.8421758798f,
                +0.5183724900f, -0.8551549343f,
                +0.4972298132f, -0.8676188753f,
                +0.4757876238f, -0.8795601952f,
                +0.4540588377f, -0.8909717009f,
                +0.4320565436f, -0.9018465186f,
                +0.4097939947f, -0.9121780977f,
                +0.3872846013f, -0.9219602148f,
                +0.3645419221f, -0.9311869775f,
                +0.3415796565f, -0.9398528280f,
                +0.3184116360f, -0.9479525463f,
                +0.2950518163f, -0.9554812534f,
                +0.2715142685f, -0.9624344144f,
                +0.2478131705f, -0.9688078409f,
                +0.2239627992f, -0.9745976937f,
                +0.1999775211f, -0.9798004853f,
                +0.1758717840f, -0.9844130818f,
                +0.1516601083f, -0.9884327046f,
                +0.1273570782f, -0.9918569325f,
                +0.1029773330f, -0.9946837029f,
                +0.0785355581f, -0.9969113131f,
                +0.0540464763f, -0.9985384211f,
                +0.0295248389f, -0.9995640469f,
                +0.0049854169f, -0.9999875727f,
                -0.0195570081f, -0.9998087434f,
                -0.0440876528f, -0.9990276667f,
                -0.0685917407f, -0.9976448131f,
                -0.0930545115f, -0.9956610156f,
                -0.1174612297f, -0.9930774690f,
                -0.1417971937f, -0.9898957298f,
                -0.1660477444f, -0.9861177144f,
                -0.1901982741f, -0.9817456985f,
                -0.2142342354f, -0.9767823158f,
                -0.2381411501f, -0.9712305559f,
                -0.2619046175f, -0.9650937630f,
                -0.2855103233f, -0.9583756337f,
                -0.3089440483f, -0.9510802148f,
                -0.3321916771f, -0.9432119007f,
                -0.3552392059f, -0.9347754311f,
                -0.3780727520f, -0.9257758877f,
                -0.4006785612f, -0.9162186915f,
                -0.4230430166f, -0.9061095994f,
                -0.4451526467f, -0.8954547008f,
                -0.4669941335f, -0.8842604137f,
                -0.4885543205f, -0.8725334813f,
                -0.5098202206f, -0.8602809673f,
                -0.5307790241f, -0.8475102522f,
                -0.5514181061f, -0.8342290286f,
                -0.5717250345f, -0.8204452967f,
                -0.5916875772f, -0.8061673592f,
                -0.6112937093f, -0.7914038166f,
                -0.6305316210f, -0.7761635620f,
                -0.6493897240f, -0.7604557754f,
                -0.6678566589f, -0.7442899187f,
                -0.6859213020f, -0.7276757296f,
                -0.7035727717f, -0.7106232159f,
                -0.7208004354f, -0.6931426493f,
                -0.7375939160f, -0.6752445595f,
                -0.7539430975f, -0.6569397276f,
                -0.7698381319f, -0.6382391798f,
                -0.7852694447f, -0.6191541805f,
                -0.8002277404f, -0.5996962260f,
                -0.8147040089f, -0.5798770368f,
                -0.8286895302f, -0.5597085515f,
                -0.8421758798f, -0.5392029186f,
                -0.8551549343f, -0.5183724900f,
                -0.8676188753f, -0.4972298132f,
                -0.8795601952f, -0.4757876238f,
                -0.8909717009f, -0.4540588377f,
                -0.9018465186f, -0.4320565436f,
                -0.9121780977f, -0.4097939947f,
                -0.9219602148f, -0.3872846013f,
                -0.9311869775f, -0.3645419221f,
                -0.9398528280f, -0.3415796565f,
                -0.9479525463f, -0.3184116360f,
                -0.9554812534f, -0.2950518163f,
                -0.9624344144f, -0.2715142685f,
                -0.9688078409f, -0.2478131705f,
                -0.9745976937f, -0.2239627992f,
                -0.9798004853f, -0.1999775211f,
                -0.9844130818f, -0.1758717840f,
                -0.9884327046f, -0.1516601083f,
                -0.9918569325f, -0.1273570782f,
                -0.9946837029f, -0.1029773330f,
                -0.9969113131f, -0.0785355581f,
                -0.9985384211f, -0.0540464763f,
        };

        /**
         * The 32 vertices of a <a href="https://en.wikipedia.org/wiki/Rhombic_triacontahedron">rhombic triacontahedron</a>,
         * normalized to lie on the unit sphere in 3D.
         * Each point is stored in 3 floats, and there is 1 float of padding after each point
         * (to allow easier access to points using bitwise operations). The distance from each
         * point to the origin should be 1.0, subject to rounding error.
         * <br>
         * The points were randomly rotated to try to avoid correlation on any typical axis; all points used the same
         * rotation to keep their distance to each other and shape. Each group of 32 vertices is repeated 8 times, each time
         * shuffled differently within that group. That means this holds 256 points, with 8 repeats of each point included
         * in that. If you only look at the first 32 points, each point will be unique.
         * <br>
         * This particular set of 32 gradient vectors is either optimal or very close to optimal for a power-of-two-sized
         * set of vectors (except for very small sets like the vertices of a tetrahedron).
         */
        public static final float[] GRADIENTS_3D = {
                -0.0752651785f, -0.7150730443f, +0.6949861108f, 0.0f,
                +0.3108026609f, +0.8973122849f, +0.3134204354f, 0.0f,
                -0.2868654574f, +0.8018987923f, +0.5240863825f, 0.0f,
                +0.8054770269f, -0.3120841869f, -0.5037958113f, 0.0f,
                +0.1282915969f, -0.1762626708f, +0.9759470975f, 0.0f,
                -0.5488958567f, -0.2924528438f, +0.7830610913f, 0.0f,
                +0.6888417674f, +0.0066053064f, +0.7248816383f, 0.0f,
                -0.1262089260f, +0.9809321878f, -0.1477949592f, 0.0f,
                +0.2902352898f, +0.5000838360f, +0.8158919252f, 0.0f,
                -0.5501830181f, -0.7897659550f, +0.2712349220f, 0.0f,
                -0.9653400724f, +0.1883955081f, -0.1806257930f, 0.0f,
                +0.9255483484f, +0.2703878154f, -0.2650484559f, 0.0f,
                +0.0752651785f, +0.7150730443f, -0.6949861108f, 0.0f,
                -0.8075596979f, -0.4925853301f, -0.3243563271f, 0.0f,
                -0.9255483484f, -0.2703878154f, +0.2650484559f, 0.0f,
                -0.2902352898f, -0.5000838360f, -0.8158919252f, 0.0f,
                +0.8075596979f, +0.4925853301f, +0.3243563271f, 0.0f,
                +0.5488958567f, +0.2924528438f, -0.7830610913f, 0.0f,
                +0.5501830181f, +0.7897659550f, -0.2712349220f, 0.0f,
                -0.8054770269f, +0.3120841869f, +0.5037958113f, 0.0f,
                -0.6869453017f, +0.7261211210f, +0.0292278996f, 0.0f,
                -0.6888417674f, -0.0066053064f, -0.7248816383f, 0.0f,
                -0.3077341151f, +0.2882639790f, +0.9067544281f, 0.0f,
                +0.1262089260f, -0.9809321878f, +0.1477949592f, 0.0f,
                +0.5455292986f, -0.6017663059f, +0.5833310359f, 0.0f,
                -0.5455292986f, +0.6017663059f, -0.5833310359f, 0.0f,
                +0.2868654574f, -0.8018987923f, -0.5240863825f, 0.0f,
                -0.3108026609f, -0.8973122849f, -0.3134204354f, 0.0f,
                +0.9653400724f, -0.1883955081f, +0.1806257930f, 0.0f,
                -0.1282915969f, +0.1762626708f, -0.9759470975f, 0.0f,
                +0.3077341151f, -0.2882639790f, -0.9067544281f, 0.0f,
                +0.6869453017f, -0.7261211210f, -0.0292278996f, 0.0f,
        };
        /**
         * 256 quasi-random 4D unit vectors, generated by taking the "Super-Fibonacci spiral" and gradually adjusting
         * its points until they reached equilibrium. 128 vectors were generated this way, and the remaining
         * 128 are the polar opposite points of each of those vectors (their antipodes).
         * <a href="https://marcalexa.github.io/superfibonacci/">This page by Marc Alexa</a> covers the spiral, though
         * an empirical search found better constants for our specific number of points (128, plus their antipodes).
         * <br>
         * Each point is stored in 4 floats, and there is no padding between points. The distance from each
         * point to the origin should be 1.0, subject to rounding error.
         * Each point is unique, and as long as you sample a point in the expected way (starting on a multiple
         * of four, using four sequential floats as the x, y, z, w components), the points will all be unit vectors.
         * The points were shuffled here to avoid any correlation when sampling in sequence.
         * <br>
         * This particular set of 256 gradient vectors is fairly close to optimal for a power-of-two-sized
         * set of vectors. A set of 256 unit vectors with all-equal distances to their neighbors can't be made.
         * The minimum distance (in 4D Euclidean space) between any two points in this set is 0.369142885554421 .
         * <br>
         * This also doesn't have any easily-noticed visual artifacts, which is much better than the previous
         * set of 64 gradient vectors using a truncated tesseract. Those 64 points were much more likely to
         * lie on straight lines when projected using a certain method, and also had rather clear angles.
         * The "certain method" is to take points on the surface of a sphere in 4D (properly, the "3-sphere"
         * because it has 3 degrees of freedom), and ignore two coordinates while keeping the other two.
         * This should produce 2D points uniformly distributed across the inside of a circle (properly, the
         * "2-ball" because it has 2 degrees of freedom, and apparently a ball is solid, but a sphere is not).
         * Repeating this for each possible pair that can be dropped shows some good views of an otherwise
         * hard-to-contemplate shape. This method does show spirals for certain coordinates (xy and zw show
         * spirals, but xz, zw, and yw do not). This shouldn't be too obvious in practice.
         */
        public static final float[] GRADIENTS_4D = {
                -0.1500775665f, +0.2370168269f, +0.0776990876f, +0.9566935301f,
                -0.2168772668f, +0.5047958493f, +0.1017048284f, -0.8293379545f,
                +0.0763605088f, -0.8615859151f, -0.2528905869f, +0.4334572554f,
                +0.1154861227f, -0.4066920578f, -0.8755832911f, +0.2337058932f,
                -0.1197163314f, -0.6982775331f, -0.1660969406f, -0.6859214902f,
                +0.3497577906f, -0.2025230676f, +0.1025419161f, +0.9089220166f,
                +0.7692472339f, +0.4585693777f, -0.3172645569f, +0.3119552135f,
                +0.9011059999f, +0.1688479185f, -0.1417298466f, -0.3733778596f,
                +0.5451716781f, -0.3883906007f, +0.3582917750f, +0.6508207321f,
                +0.0551428460f, +0.3906531036f, +0.6245298982f, -0.6740266681f,
                -0.0720681995f, +0.3346361816f, -0.8879311085f, -0.3072509468f,
                +0.5837373734f, +0.4879808724f, +0.3614648283f, -0.5389514565f,
                +0.4248800874f, -0.2106733918f, -0.5714059472f, -0.6697677970f,
                +0.2736921608f, +0.1859380007f, -0.3482927382f, -0.8770473003f,
                -0.9203012586f, -0.2347503006f, -0.2557854056f, -0.1803098917f,
                +0.4820273817f, -0.3926214278f, +0.0709615499f, -0.7800400257f,
                -0.4847539961f, -0.1899734437f, -0.3473681211f, +0.7799096107f,
                -0.1541055739f, -0.6454571486f, -0.2610019147f, +0.7010810971f,
                -0.9011059999f, -0.1688479185f, +0.1417298466f, +0.3733778596f,
                +0.3607165217f, +0.3201324046f, -0.8760028481f, -0.0042417473f,
                +0.7660096288f, +0.3920440078f, +0.5009348989f, -0.0927091315f,
                +0.3528687060f, -0.3726457357f, +0.8218995333f, -0.2471843809f,
                +0.2098862231f, +0.4600366652f, -0.7274983525f, -0.4637458026f,
                -0.9244774580f, +0.1559440494f, -0.3474252224f, -0.0178527124f,
                -0.6069507003f, -0.0521510802f, +0.7704458237f, +0.1878946275f,
                -0.7102735639f, +0.5383709669f, +0.4519570172f, +0.0374585539f,
                -0.6069343090f, +0.3758327067f, +0.6232035160f, +0.3193713129f,
                +0.4562335312f, -0.5830481052f, +0.5214208961f, -0.4242948592f,
                -0.0197402649f, +0.8283793926f, -0.5044059157f, +0.2428428233f,
                -0.8456494808f, -0.0475203022f, -0.3905497193f, +0.3606795967f,
                -0.7692472339f, -0.4585693777f, +0.3172645569f, -0.3119552135f,
                -0.3915876150f, -0.5137497783f, +0.1563476324f, +0.7471785545f,
                +0.6474077702f, -0.5600305796f, +0.4030532837f, +0.3236929178f,
                +0.8314927220f, -0.4754710495f, -0.1289689094f, +0.2567377090f,
                -0.5837687254f, +0.5748279691f, +0.1695513278f, +0.5477584600f,
                -0.3913940787f, -0.4500352442f, +0.3034601212f, -0.7430955172f,
                -0.0479935408f, -0.1738562137f, -0.9190009832f, -0.3505822122f,
                +0.1591796726f, -0.1220099106f, -0.8619921803f, +0.4655586779f,
                +0.3481253982f, -0.0606551990f, +0.8087866306f, -0.4700999558f,
                -0.2736921608f, -0.1859380007f, +0.3482927382f, +0.8770473003f,
                +0.3593838513f, -0.7619267702f, +0.5353092551f, +0.0612759069f,
                +0.2731977105f, -0.5373794436f, -0.3627216220f, -0.7106472254f,
                +0.5682965517f, +0.6156543493f, -0.3069647551f, -0.4514216185f,
                -0.3481253982f, +0.0606551990f, -0.8087866306f, +0.4700999558f,
                -0.7229727507f, +0.1000640318f, +0.3482705951f, +0.5882220864f,
                +0.6173728108f, -0.1686241180f, +0.4670835435f, -0.6101226807f,
                -0.0058717611f, -0.5279526114f, +0.3622214794f, +0.7681323290f,
                -0.5682965517f, -0.6156543493f, +0.3069647551f, +0.4514216185f,
                +0.1812587827f, +0.5881740451f, -0.6412660480f, +0.4582296312f,
                -0.1640486717f, +0.4619357288f, -0.5720989704f, +0.6575760841f,
                -0.6290537119f, +0.7484779954f, +0.1338096410f, +0.1617629528f,
                +0.1370694786f, -0.8893449903f, -0.0286894143f, -0.4352636933f,
                -0.6868565679f, -0.1498538405f, +0.4144212902f, -0.5779507160f,
                +0.0627557933f, -0.1148957163f, -0.4865127206f, +0.8638090491f,
                +0.7229727507f, -0.1000640318f, -0.3482705951f, -0.5882220864f,
                -0.5501986146f, -0.3024502695f, +0.5412290096f, +0.5593536496f,
                -0.4975182116f, +0.0216092784f, +0.1241487861f, +0.8582515717f,
                -0.3732183576f, -0.0930780172f, -0.9224595428f, -0.0333623886f,
                -0.3935349584f, +0.5759038925f, +0.7137228251f, -0.0637555048f,
                +0.7688309550f, -0.0562318675f, +0.5756873488f, +0.2726191282f,
                -0.4923638999f, +0.7683649659f, -0.1948917806f, +0.3594581783f,
                +0.7245696187f, -0.3282309175f, -0.4578595459f, +0.3970240057f,
                -0.3815847635f, -0.3854267299f, -0.6985180974f, +0.4668103755f,
                -0.1925185025f, -0.3090348840f, -0.1709938943f, +0.9155299664f,
                -0.0763605088f, +0.8615859151f, +0.2528905869f, -0.4334572554f,
                +0.8098008633f, -0.0068325223f, -0.5799278021f, +0.0886552259f,
                -0.0311528668f, +0.5825442076f, +0.6982141733f, +0.4149323702f,
                +0.2348341793f, -0.7289867997f, +0.1708114296f, +0.6198827624f,
                +0.8799446225f, -0.2076391280f, -0.3413696587f, -0.2570025623f,
                -0.7166067362f, -0.2055816799f, -0.0187202729f, +0.6662286520f,
                -0.2871971726f, +0.3454537392f, +0.8530229926f, +0.2655773759f,
                +0.0479935408f, +0.1738562137f, +0.9190009832f, +0.3505822122f,
                +0.3427777886f, +0.6960333586f, +0.5767480731f, -0.2557395995f,
                +0.0352323204f, +0.4647250772f, -0.1351369023f, +0.8743725419f,
                +0.5837687254f, -0.5748279691f, -0.1695513278f, -0.5477584600f,
                +0.1640486717f, -0.4619357288f, +0.5720989704f, -0.6575760841f,
                -0.0346745104f, +0.2332650721f, +0.5832293630f, +0.7773215175f,
                +0.3666544557f, +0.6371533275f, -0.6673937440f, -0.1191042140f,
                +0.2688287199f, -0.9276711345f, -0.2462313175f, -0.0807942897f,
                +0.2168772668f, -0.5047958493f, -0.1017048284f, +0.8293379545f,
                -0.7688309550f, +0.0562318675f, -0.5756873488f, -0.2726191282f,
                -0.2891888618f, -0.0525769927f, +0.7722691298f, +0.5632103682f,
                +0.5796207190f, +0.5517915487f, +0.0019261374f, +0.5996351242f,
                +0.8315265775f, -0.2538107038f, +0.1608182788f, +0.4672057331f,
                -0.4562335312f, +0.5830481052f, -0.5214208961f, +0.4242948592f,
                +0.0321676619f, +0.7899427414f, +0.6058839560f, +0.0886588320f,
                +0.4847539961f, +0.1899734437f, +0.3473681211f, -0.7799096107f,
                +0.1886299253f, +0.1110786423f, -0.2090769559f, +0.9530829787f,
                +0.9647145867f, -0.0993151814f, -0.2088250220f, +0.1259146184f,
                +0.4292620420f, -0.0465219989f, -0.3925911188f, +0.8120603561f,
                -0.2002530843f, -0.5264436007f, -0.8260012269f, +0.0218623430f,
                -0.6173728108f, +0.1686241180f, -0.4670835435f, +0.6101226807f,
                +0.4251541793f, -0.7684340477f, -0.4242341518f, +0.2208584994f,
                -0.5605144501f, +0.7617027164f, -0.0315162092f, -0.3234800994f,
                -0.3830938637f, -0.5762633681f, -0.5179871321f, -0.5028410554f,
                -0.5804424882f, -0.4247357547f, +0.6436215043f, -0.2616055906f,
                +0.0746164620f, -0.6295408010f, +0.7720863223f, -0.0446486510f,
                -0.2014599144f, +0.9114251137f, -0.2199151367f, -0.2834706008f,
                -0.8888183832f, +0.4347586334f, +0.0185530689f, +0.1436756402f,
                -0.0321676619f, -0.7899427414f, -0.6058839560f, -0.0886588320f,
                -0.3263846636f, -0.2539949715f, -0.7520564198f, -0.5131967068f,
                +0.9244774580f, -0.1559440494f, +0.3474252224f, +0.0178527124f,
                +0.2957792580f, -0.7487596273f, -0.4506414831f, -0.3857408762f,
                +0.7796046734f, -0.2941354215f, +0.1434659064f, -0.5339647532f,
                -0.1591796726f, +0.1220099106f, +0.8619921803f, -0.4655586779f,
                +0.1925185025f, +0.3090348840f, +0.1709938943f, -0.9155299664f,
                +0.5500568151f, +0.6789613962f, +0.4516335726f, +0.1802109927f,
                -0.1282755584f, -0.9451470971f, -0.2913866341f, +0.0730489343f,
                +0.4975182116f, -0.0216092784f, -0.1241487861f, -0.8582515717f,
                -0.1235827729f, +0.6694590449f, +0.5823508501f, -0.4443190992f,
                +0.7102735639f, -0.5383709669f, -0.4519570172f, -0.0374585539f,
                -0.3593838513f, +0.7619267702f, -0.5353092551f, -0.0612759069f,
                -0.7660096288f, -0.3920440078f, -0.5009348989f, +0.0927091315f,
                -0.1886299253f, -0.1110786423f, +0.2090769559f, -0.9530829787f,
                +0.4360558987f, +0.1391784698f, -0.7543152571f, +0.4706306756f,
                -0.0551428460f, -0.3906531036f, -0.6245298982f, +0.6740266681f,
                +0.2002530843f, +0.5264436007f, +0.8260012269f, -0.0218623430f,
                -0.0623782203f, -0.3003311753f, +0.9268308878f, -0.2165521234f,
                -0.4292620420f, +0.0465219989f, +0.3925911188f, -0.8120603561f,
                +0.6668652296f, -0.3556269109f, +0.6501035094f, -0.0786493346f,
                -0.1370694786f, +0.8893449903f, +0.0286894143f, +0.4352636933f,
                +0.3815847635f, +0.3854267299f, +0.6985180974f, -0.4668103755f,
                +0.1565428525f, +0.0181281455f, +0.3355644941f, +0.9287422895f,
                -0.2957792580f, +0.7487596273f, +0.4506414831f, +0.3857408762f,
                -0.5796207190f, -0.5517915487f, -0.0019261374f, -0.5996351242f,
                -0.2652045786f, +0.1773467511f, -0.4390128851f, +0.8399300575f,
                -0.6503618360f, -0.0984814689f, -0.7063113451f, +0.2616396546f,
                -0.2203493118f, +0.3028321564f, -0.5794181228f, -0.7238878608f,
                +0.1772385240f, -0.7025631666f, +0.2201232612f, -0.6530982852f,
                -0.7717817426f, +0.5101420879f, -0.3310671151f, +0.1857492775f,
                +0.9335333705f, +0.3035431206f, -0.1906893849f, -0.0038217760f,
                -0.8314927220f, +0.4754710495f, +0.1289689094f, -0.2567377090f,
                -0.4938579202f, +0.2063272744f, -0.8139879704f, -0.2257363349f,
                -0.4935885072f, -0.8018989563f, -0.2325375825f, +0.2434232235f,
                +0.2652045786f, -0.1773467511f, +0.4390128851f, -0.8399300575f,
                +0.4923638999f, -0.7683649659f, +0.1948917806f, -0.3594581783f,
                +0.3263846636f, +0.2539949715f, +0.7520564198f, +0.5131967068f,
                +0.2014599144f, -0.9114251137f, +0.2199151367f, +0.2834706008f,
                -0.2445772290f, +0.0590920746f, +0.9656262994f, +0.0652375147f,
                -0.5198469758f, +0.5825917125f, +0.1809202135f, -0.5980083942f,
                -0.9647145867f, +0.0993151814f, +0.2088250220f, -0.1259146184f,
                +0.0311528668f, -0.5825442076f, -0.6982141733f, -0.4149323702f,
                -0.2313889712f, -0.8562101722f, -0.2715738714f, -0.3736455441f,
                -0.2083578706f, -0.3882177770f, -0.3572764695f, -0.8235457540f,
                +0.0692037940f, +0.9831219912f, -0.1130947843f, +0.1260616034f,
                +0.2747297585f, +0.8059790730f, -0.0180938281f, -0.5240172148f,
                -0.5578742027f, +0.1918342263f, +0.7655780911f, -0.2566442788f,
                -0.6668652296f, +0.3556269109f, -0.6501035094f, +0.0786493346f,
                +0.1551940888f, +0.2166104019f, -0.6768955588f, +0.6861539483f,
                +0.0623782203f, +0.3003311753f, -0.9268308878f, +0.2165521234f,
                +0.6290537119f, -0.7484779954f, -0.1338096410f, -0.1617629528f,
                +0.6069343090f, -0.3758327067f, -0.6232035160f, -0.3193713129f,
                +0.6724284291f, -0.1377824694f, -0.0431847833f, +0.7259415984f,
                +0.3322996795f, -0.5608487725f, +0.6636862755f, +0.3668054342f,
                -0.1812587827f, -0.5881740451f, +0.6412660480f, -0.4582296312f,
                +0.5804424882f, +0.4247357547f, -0.6436215043f, +0.2616055906f,
                -0.6724284291f, +0.1377824694f, +0.0431847833f, -0.7259415984f,
                +0.7561704516f, +0.3272831738f, -0.5194675922f, -0.2263747603f,
                -0.1154861227f, +0.4066920578f, +0.8755832911f, -0.2337058932f,
                +0.2203493118f, -0.3028321564f, +0.5794181228f, +0.7238878608f,
                +0.1282755584f, +0.9451470971f, +0.2913866341f, -0.0730489343f,
                +0.6868565679f, +0.1498538405f, -0.4144212902f, +0.5779507160f,
                +0.3935349584f, -0.5759038925f, -0.7137228251f, +0.0637555048f,
                +0.3732183576f, +0.0930780172f, +0.9224595428f, +0.0333623886f,
                +0.7658822536f, +0.5263185501f, +0.0733225495f, -0.3619902432f,
                -0.3427777886f, -0.6960333586f, -0.5767480731f, +0.2557395995f,
                -0.8799446225f, +0.2076391280f, +0.3413696587f, +0.2570025623f,
                -0.0627557933f, +0.1148957163f, +0.4865127206f, -0.8638090491f,
                -0.5451716781f, +0.3883906007f, -0.3582917750f, -0.6508207321f,
                -0.0615775883f, -0.0751815885f, -0.9757779241f, +0.1959938258f,
                -0.3120108247f, -0.7524400353f, +0.3078546822f, -0.4916389883f,
                -0.5250310302f, -0.7412517071f, +0.4086282849f, -0.0889459178f,
                -0.4820273817f, +0.3926214278f, -0.0709615499f, +0.7800400257f,
                +0.0197402649f, -0.8283793926f, +0.5044059157f, -0.2428428233f,
                +0.5501986146f, +0.3024502695f, -0.5412290096f, -0.5593536496f,
                -0.8761548996f, -0.1062743291f, +0.1077154949f, -0.4576633871f,
                -0.4559036791f, -0.8604559898f, +0.0381671526f, -0.2243002802f,
                +0.0058717611f, +0.5279526114f, -0.3622214794f, -0.7681323290f,
                -0.9335333705f, -0.3035431206f, +0.1906893849f, +0.0038217760f,
                -0.5222824216f, -0.2446532995f, -0.1422012448f, -0.8044530153f,
                -0.0788757727f, -0.7464979887f, +0.4882335961f, +0.4451375306f,
                -0.5693851709f, -0.3627055287f, -0.7218432426f, -0.1522749811f,
                -0.5837373734f, -0.4879808724f, -0.3614648283f, +0.5389514565f,
                +0.1500775665f, -0.2370168269f, -0.0776990876f, -0.9566935301f,
                +0.4935885072f, +0.8018989563f, +0.2325375825f, -0.2434232235f,
                -0.7796046734f, +0.2941354215f, -0.1434659064f, +0.5339647532f,
                +0.5693851709f, +0.3627055287f, +0.7218432426f, +0.1522749811f,
                +0.5222824216f, +0.2446532995f, +0.1422012448f, +0.8044530153f,
                +0.4178254604f, -0.3877674937f, -0.5196145773f, +0.6364424825f,
                -0.3528687060f, +0.3726457357f, -0.8218995333f, +0.2471843809f,
                +0.3915876150f, +0.5137497783f, -0.1563476324f, -0.7471785545f,
                +0.0788757727f, +0.7464979887f, -0.4882335961f, -0.4451375306f,
                +0.5578742027f, -0.1918342263f, -0.7655780911f, +0.2566442788f,
                +0.5198469758f, -0.5825917125f, -0.1809202135f, +0.5980083942f,
                -0.0746164620f, +0.6295408010f, -0.7720863223f, +0.0446486510f,
                -0.3666544557f, -0.6371533275f, +0.6673937440f, +0.1191042140f,
                +0.7558788657f, +0.5948066711f, +0.1050633341f, +0.2526143789f,
                +0.6503618360f, +0.0984814689f, +0.7063113451f, -0.2616396546f,
                -0.3607165217f, -0.3201324046f, +0.8760028481f, +0.0042417473f,
                +0.7397086620f, +0.2649133801f, +0.3316588402f, +0.5221632123f,
                +0.7166067362f, +0.2055816799f, +0.0187202729f, -0.6662286520f,
                +0.8456494808f, +0.0475203022f, +0.3905497193f, -0.3606795967f,
                +0.4559036791f, +0.8604559898f, -0.0381671526f, +0.2243002802f,
                +0.7394540310f, +0.6632617712f, -0.0962664708f, -0.0634394437f,
                -0.1824457794f, -0.9027240276f, +0.3662159443f, +0.1329995543f,
                +0.3913940787f, +0.4500352442f, -0.3034601212f, +0.7430955172f,
                -0.7245696187f, +0.3282309175f, +0.4578595459f, -0.3970240057f,
                -0.4251541793f, +0.7684340477f, +0.4242341518f, -0.2208584994f,
                +0.9203012586f, +0.2347503006f, +0.2557854056f, +0.1803098917f,
                -0.0692037940f, -0.9831219912f, +0.1130947843f, -0.1260616034f,
                -0.1772385240f, +0.7025631666f, -0.2201232612f, +0.6530982852f,
                +0.0346745104f, -0.2332650721f, -0.5832293630f, -0.7773215175f,
                +0.1197163314f, +0.6982775331f, +0.1660969406f, +0.6859214902f,
                -0.1551940888f, -0.2166104019f, +0.6768955588f, -0.6861539483f,
                -0.4248800874f, +0.2106733918f, +0.5714059472f, +0.6697677970f,
                -0.1565428525f, -0.0181281455f, -0.3355644941f, -0.9287422895f,
                -0.4360558987f, -0.1391784698f, +0.7543152571f, -0.4706306756f,
                -0.5562461615f, -0.0117739718f, -0.5588256121f, -0.6149517298f,
                +0.3120108247f, +0.7524400353f, -0.3078546822f, +0.4916389883f,
                +0.5250310302f, +0.7412517071f, -0.4086282849f, +0.0889459178f,
                -0.3322996795f, +0.5608487725f, -0.6636862755f, -0.3668054342f,
                +0.1235827729f, -0.6694590449f, -0.5823508501f, +0.4443190992f,
                -0.7394540310f, -0.6632617712f, +0.0962664708f, +0.0634394437f,
                +0.2891888618f, +0.0525769927f, -0.7722691298f, -0.5632103682f,
                -0.5500568151f, -0.6789613962f, -0.4516335726f, -0.1802109927f,
                +0.1824457794f, +0.9027240276f, -0.3662159443f, -0.1329995543f,
                -0.2348341793f, +0.7289867997f, -0.1708114296f, -0.6198827624f,
                -0.6474077702f, +0.5600305796f, -0.4030532837f, -0.3236929178f,
                +0.0720681995f, -0.3346361816f, +0.8879311085f, +0.3072509468f,
                -0.2747297585f, -0.8059790730f, +0.0180938281f, +0.5240172148f,
                +0.2871971726f, -0.3454537392f, -0.8530229926f, -0.2655773759f,
                -0.8098008633f, +0.0068325223f, +0.5799278021f, -0.0886552259f,
                -0.2098862231f, -0.4600366652f, +0.7274983525f, +0.4637458026f,
                +0.1541055739f, +0.6454571486f, +0.2610019147f, -0.7010810971f,
                -0.3497577906f, +0.2025230676f, -0.1025419161f, -0.9089220166f,
                -0.2688287199f, +0.9276711345f, +0.2462313175f, +0.0807942897f,
                +0.4938579202f, -0.2063272744f, +0.8139879704f, +0.2257363349f,
                -0.7561704516f, -0.3272831738f, +0.5194675922f, +0.2263747603f,
                -0.2731977105f, +0.5373794436f, +0.3627216220f, +0.7106472254f,
                +0.6069507003f, +0.0521510802f, -0.7704458237f, -0.1878946275f,
                -0.0352323204f, -0.4647250772f, +0.1351369023f, -0.8743725419f,
                -0.7658822536f, -0.5263185501f, -0.0733225495f, +0.3619902432f,
                -0.8315265775f, +0.2538107038f, -0.1608182788f, -0.4672057331f,
                +0.3830938637f, +0.5762633681f, +0.5179871321f, +0.5028410554f,
                +0.5562461615f, +0.0117739718f, +0.5588256121f, +0.6149517298f,
                -0.7397086620f, -0.2649133801f, -0.3316588402f, -0.5221632123f,
                +0.2313889712f, +0.8562101722f, +0.2715738714f, +0.3736455441f,
                +0.2083578706f, +0.3882177770f, +0.3572764695f, +0.8235457540f,
                -0.4178254604f, +0.3877674937f, +0.5196145773f, -0.6364424825f,
                -0.7558788657f, -0.5948066711f, -0.1050633341f, -0.2526143789f,
                +0.7717817426f, -0.5101420879f, +0.3310671151f, -0.1857492775f,
                +0.0615775883f, +0.0751815885f, +0.9757779241f, -0.1959938258f,
                +0.2445772290f, -0.0590920746f, -0.9656262994f, -0.0652375147f,
                +0.8888183832f, -0.4347586334f, -0.0185530689f, -0.1436756402f,
                +0.8761548996f, +0.1062743291f, -0.1077154949f, +0.4576633871f,
                +0.5605144501f, -0.7617027164f, +0.0315162092f, +0.3234800994f,
        };
//</editor-fold>

    }

    /**
     * A mix of "Classic" Perlin noise, written by Ken Perlin before he created Simplex Noise, with value noise calculated
     * at the same time. This uses cubic interpolation throughout (instead of the quintic interpolation used in Simplex
     * Noise, because cubic looks a little smoother and doesn't alternate as badly between sharp change and low change), and
     * has a single {@code int} seed. Perlue Noise can have significant grid-aligned and 45-degree-diagonal artifacts when
     * too few octaves are used, but sometimes this is irrelevant, such as when sampling 3D noise on the surface of a
     * sphere. These artifacts sometimes manifest as "waves" of quickly-changing and then slowly-changing noise, when 3D
     * noise uses time as the z axis.
     * <br>
     * This tends to look fairly different from vanilla PerlinNoise or ValueNoise; it is capable of more chaotic
     * arrangements of high and low values than either of those, but it still tends to have clusters of values of a specific
     * size more often than clusters with very different sizes.
     */
    public static class PerlueNoise {
        public static final PerlueNoise instance = new PerlueNoise();

        private static final float SCALE2 = 1.41421330f; //towardsZero(1f/ (float) Math.sqrt(2f / 4f));
        private static final float SCALE3 = 1.15470030f; //towardsZero(1f/ (float) Math.sqrt(3f / 4f));
        private static final float SCALE4 = 0.99999990f; //towardsZero(1f)                            ;
        private static final float EQ_ADD_2 = 1.0f / 0.85f;
        private static final float EQ_ADD_3 = 0.8f / 0.85f;
        private static final float EQ_ADD_4 = 0.6f / 0.85f;
        private static final float EQ_MUL_2 = 1.2535664f;
        private static final float EQ_MUL_3 = 1.2071217f;
        private static final float EQ_MUL_4 = 1.1588172f;

        public int seed;

        public PerlueNoise() {
            this(0x1337BEEF);
        }

        public PerlueNoise(final int seed) {
            this.seed = seed;
        }

        public PerlueNoise(PerlueNoise other) {
            this.seed = other.seed;
        }

        /**
         * Gets the minimum dimension supported by this generator, which is 1.
         *
         * @return the minimum supported dimension, 1
         */
        public int getMinDimension() {
            return 1;
        }

        /**
         * Gets the maximum dimension supported by this generator, which is 4.
         *
         * @return the maximum supported dimension, 4
         */
        public int getMaxDimension() {
            return 4;
        }

        /**
         * Sets the seed to the given int.
         * @param seed an int seed, with no restrictions
         */
        public void setSeed(int seed) {
            this.seed = seed;
        }

        /**
         * Gets the current seed of the generator, as an int.
         *
         * @return the current seed, as an int
         */
        public int getSeed() {
            return seed;
        }

        /**
         * Returns the constant String {@code "PerlueNoise"} that identifies this in serialized Strings.
         *
         * @return a short String constant that identifies this RawNoise type, {@code "PerlueNoise"}
         */
        public String getTag() {
            return "PerlueNoise";
        }

        /**
         * Creates a copy of this PerlueNoise, which should be a deep copy for any mutable state but can be shallow for immutable
         * types such as functions. This almost always just calls a copy constructor.
         *
         * @return a copy of this PerlueNoise
         */
        public PerlueNoise copy() {
            return new PerlueNoise(this.seed);
        }

        /**
         * Hashes x, y, and the seed, then uses that hash both to look up a gradient vector and as a value noise result.
         * @param seed any int
         * @param x x position of a lattice point
         * @param y y position of a lattice point
         * @param xd how far on x the requested point is from the lattice point
         * @param yd how far on y the requested point is from the lattice point
         * @return a float that's uh... close to 0, and not too much outside -1 to 1...
         */
        protected static float gradCoord2D(int seed, int x, int y,
                                           float xd, float yd) {
            final int h = hashAll(x, y, seed);
            final int hash = h & (255 << 1);
            return (h * 0x1p-32f) + xd * GRADIENTS_2D[hash] + yd * GRADIENTS_2D[hash + 1];
        }

        /**
         * Hashes x, y, z, and the seed, then uses that hash both to look up a gradient vector and as a value noise result.
         * @param seed any int
         * @param x x position of a lattice point
         * @param y y position of a lattice point
         * @param z z position of a lattice point
         * @param xd how far on x the requested point is from the lattice point
         * @param yd how far on y the requested point is from the lattice point
         * @param zd how far on z the requested point is from the lattice point
         * @return a float that's uh... close to 0, and not too much outside -1 to 1...
         */
        protected static float gradCoord3D(int seed, int x, int y, int z, float xd, float yd, float zd) {
            final int h = hashAll(x, y, z, seed);
            final int hash = h & (31 << 2);
            return (h * 0x1p-32f) + xd * GRADIENTS_3D[hash] + yd * GRADIENTS_3D[hash + 1] + zd * GRADIENTS_3D[hash + 2];
        }

        /**
         * Hashes x, y, z, w, and the seed, then uses that hash both to look up a gradient vector and as a value noise result.
         * @param seed any int
         * @param x x position of a lattice point
         * @param y y position of a lattice point
         * @param z z position of a lattice point
         * @param w w position of a lattice point
         * @param xd how far on x the requested point is from the lattice point
         * @param yd how far on y the requested point is from the lattice point
         * @param zd how far on z the requested point is from the lattice point
         * @param wd how far on w the requested point is from the lattice point
         * @return a float that's uh... close to 0, and not too much outside -1 to 1...
         */
        protected static float gradCoord4D(int seed, int x, int y, int z, int w,
                                           float xd, float yd, float zd, float wd) {
            final int h = hashAll(x, y, z, w, seed);
            final int hash = h & (255 << 2);
            return (h * 0x1p-32f) + xd * GRADIENTS_4D[hash] + yd * GRADIENTS_4D[hash + 1] + zd * GRADIENTS_4D[hash + 2] + wd * GRADIENTS_4D[hash + 3];
        }

        /**
         * Given inputs as {@code x} in the range -1.0 to 1.0 that are too biased towards 0.0, this "squashes" the range
         * softly to widen it and spread it away from 0.0 without increasing bias anywhere else.
         * <br>
         * This starts with a common sigmoid function, {@code x / sqrt(x * x + add)}, but instead of approaching -1 and 1
         * but never reaching them, this multiplies the result so the line crosses -1 when x is -1, and crosses 1 when x is
         * 1. It has a smooth derivative, if that matters to you.
         *
         * @param x a float between -1 and 1
         * @param add if greater than 1, this will have nearly no effect; the lower this goes below 1, the more this will
         *           separate results near the center of the range. This must be greater than or equal to 0.0
         * @param mul typically the result of calling {@code (float) Math.sqrt(add + 1f)}
         * @return a float with a slightly different distribution from {@code x}, but still between -1 and 1
         */
        public static float equalize(float x, float add, float mul) {
            return x * mul / (float) Math.sqrt(x * x + add);
        }

        /**
         * Just calls {@link #getNoiseWithSeed(float, int)} using our internal {@link #seed}.
         * @param x a distance traveled; should change by less than 1 between calls, and should be less than about 10000
         * @return a smoothly-interpolated swaying value between -1 and 1, both exclusive
         */
        public float getNoise(final float x) {
            return getNoiseWithSeed(x, seed);
        }

        /**
         * Sway smoothly using bicubic interpolation between 4 points (the two integers before x and the two after).
         * This pretty much never produces steep changes between peaks and valleys; this may make it more useful for
         * things like generating terrain that can be walked across in a side-scrolling game.
         *
         * @param x    a distance traveled; should change by less than 1 between calls, and should be less than about 10000
         * @param seed any long
         * @return a smoothly-interpolated swaying value between -1 and 1, both exclusive
         */
        public float getNoiseWithSeed(float x, int seed)
        {
            final long floor = (long) Math.floor(x);
            // what we add here ensures that at the very least, the upper half will have some non-zero bits.
            long s = ((seed & 0xFFFFFFFFL) ^ (seed >>> 16)) + 0x9E3779B97F4A7C15L;
            // fancy XOR-rotate-rotate is a way to mix bits both up and down without multiplication.
            s = (s ^ (s << 21 | s >>> 43) ^ (s << 50 | s >>> 14)) + floor;
            // we use a different technique here, relative to other wobble methods.
            // to avoid frequent multiplication and replace it with addition by constants, we track 3 variables, each of
            // which updates with a different large, negative long increment. when we want to get a result, we just XOR
            // m, n, and o, and use only the upper bits (by multiplying by a tiny fraction).
            // the multipliers used for m, n, and o are each 2 to the 64 divided by "very irrational numbers" or their
            // powers, with these three numbers all part of a related series.
            final long m = s * 0xD1B54A32D192ED03L;
            final long n = s * 0xABC98388FB8FAC03L;
            final long o = s * 0x8CB92BA72F3D8DD7L;

            // each of the complicated hex constants is a multiple of a "very irrational" multiplier from above.
            // a uses 0 times the m, n, or o multiplier, b uses 1 times that, c uses 2 times that, and d uses 3 times.
            final float a = (m ^ n ^ o);
            final float b = (m + 0xD1B54A32D192ED03L ^ n + 0xABC98388FB8FAC03L ^ o + 0x8CB92BA72F3D8DD7L);
            final float c = (m + 0xA36A9465A325DA06L ^ n + 0x57930711F71F5806L ^ o + 0x1972574E5E7B1BAEL);
            final float d = (m + 0x751FDE9874B8C709L ^ n + 0x035C8A9AF2AF0409L ^ o + 0xA62B82F58DB8A985L);

            // get the fractional part of x.
            x -= floor;
            // this is bicubic interpolation, inlined
            final float p = (d - c) - (a - b);
            // 7.7.228014483236334E-20 , or 0x1.5555555555428p-64 , is just inside {@code -2f/3f/Long.MIN_VALUE} .
            // it gets us about as close as we can go to 1.0 .
            return (x * (x * x * p + x * (a - b - p) + c - a) + b) * 7.228014E-20f;
        }

        /**
         * Uses the {@link #seed} of this PerlueNoise
         * @param x x-coordinate; usually different by a small fraction between calls
         * @param y y-coordinate; usually different by a small fraction between calls
         * @return a float between -1 and 1
         */
        public float getNoise(final float x, final float y) {
            return getNoiseWithSeed(x, y, seed);
        }

        /**
         *
         * @param x x-coordinate; usually different by a small fraction between calls
         * @param y y-coordinate; usually different by a small fraction between calls
         * @param seed may be any int; should be the same between calls to the same area of noise
         * @return a float between -1 and 1
         */
        public float getNoiseWithSeed(float x, float y, final int seed) {
            final int
                    xi = floor(x), x0 = xi * X2,
                    yi = floor(y), y0 = yi * Y2;
            final float xf = x - xi, yf = y - yi;

            final float xa = xf * xf * (1 - xf - xf + 2);//* xf * (xf * (xf * 6.0f - 15.0f) + 9.999998f);
            final float ya = yf * yf * (1 - yf - yf + 2);//* yf * (yf * (yf * 6.0f - 15.0f) + 9.999998f);
            return
                    equalize(lerp(lerp(gradCoord2D(seed, x0, y0, xf, yf), gradCoord2D(seed, x0+ X2, y0, xf - 1, yf), xa),
                                    lerp(gradCoord2D(seed, x0, y0+ Y2, xf, yf-1), gradCoord2D(seed, x0+ X2, y0+ Y2, xf - 1, yf - 1), xa),
                                    ya) * SCALE2, EQ_ADD_2, EQ_MUL_2);//* 0.875;// * 1.4142;
        }

        /**
         * Uses the {@link #seed} of this PerlueNoise
         * @param x x-coordinate; usually different by a small fraction between calls
         * @param y y-coordinate; usually different by a small fraction between calls
         * @param z z-coordinate; usually different by a small fraction between calls
         * @return a float between -1 and 1
         */
        public float getNoise(final float x, final float y, final float z) {
            return getNoiseWithSeed(x, y, z, seed);
        }

        /**
         *
         * @param x x-coordinate; usually different by a small fraction between calls
         * @param y y-coordinate; usually different by a small fraction between calls
         * @param z z-coordinate; usually different by a small fraction between calls
         * @param seed may be any int; should be the same between calls to the same area of noise
         * @return a float between -1 and 1
         */
        public float getNoiseWithSeed(float x, float y, float z, final int seed) {
            final int
                    xi = floor(x), x0 = xi * X3,
                    yi = floor(y), y0 = yi * Y3,
                    zi = floor(z), z0 = zi * Z3;
            final float xf = x - xi, yf = y - yi, zf = z - zi;

            final float xa = xf * xf * (1 - xf - xf + 2);//* xf * (xf * (xf * 6.0f - 15.0f) + 9.999998f);
            final float ya = yf * yf * (1 - yf - yf + 2);//* yf * (yf * (yf * 6.0f - 15.0f) + 9.999998f);
            final float za = zf * zf * (1 - zf - zf + 2);//* zf * (zf * (zf * 6.0f - 15.0f) + 9.999998f);
             return
                     equalize(
                             lerp(
                                     lerp(
                                             lerp(
                                                     gradCoord3D(seed, x0, y0, z0, xf, yf, zf),
                                                     gradCoord3D(seed, x0+ X3, y0, z0, xf - 1, yf, zf),
                                                     xa),
                                             lerp(
                                                     gradCoord3D(seed, x0, y0+ Y3, z0, xf, yf-1, zf),
                                                     gradCoord3D(seed, x0+ X3, y0+ Y3, z0, xf - 1, yf - 1, zf),
                                                     xa),
                                             ya),
                                     lerp(
                                             lerp(
                                                     gradCoord3D(seed, x0, y0, z0+ Z3, xf, yf, zf-1),
                                                     gradCoord3D(seed, x0+ X3, y0, z0+ Z3, xf - 1, yf, zf-1),
                                                     xa),
                                             lerp(
                                                     gradCoord3D(seed, x0, y0+ Y3, z0+ Z3, xf, yf-1, zf-1),
                                                     gradCoord3D(seed, x0+ X3, y0+ Y3, z0+ Z3, xf - 1, yf - 1, zf-1),
                                                     xa),
                                             ya),
                                     za) * SCALE3, EQ_ADD_3, EQ_MUL_3); // 1.0625f
        }

        /**
         * Uses the {@link #seed} of this PerlueNoise
         * @param x x-coordinate; usually different by a small fraction between calls
         * @param y y-coordinate; usually different by a small fraction between calls
         * @param z z-coordinate; usually different by a small fraction between calls
         * @param w w-coordinate; usually different by a small fraction between calls
         * @return a float between -1 and 1
         */
        public float getNoise(final float x, final float y, final float z, final float w) {
            return getNoiseWithSeed(x, y, z, w, seed);
        }

        /**
         *
         * @param x x-coordinate; usually different by a small fraction between calls
         * @param y y-coordinate; usually different by a small fraction between calls
         * @param z z-coordinate; usually different by a small fraction between calls
         * @param w w-coordinate; usually different by a small fraction between calls
         * @param seed may be any int; should be the same between calls to the same area of noise
         * @return a float between -1 and 1
         */
        public float getNoiseWithSeed(float x, float y, float z, float w, final int seed) {
            final int
                    xi = floor(x), x0 = xi * X4,
                    yi = floor(y), y0 = yi * Y4,
                    zi = floor(z), z0 = zi * Z4,
                    wi = floor(w), w0 = wi * W4;
            final float xf = x - xi, yf = y - yi, zf = z - zi, wf = w - wi;

            final float xa = xf * xf * (1 - xf - xf + 2);//* xf * (xf * (xf * 6.0f - 15.0f) + 9.999998f);
            final float ya = yf * yf * (1 - yf - yf + 2);//* yf * (yf * (yf * 6.0f - 15.0f) + 9.999998f);
            final float za = zf * zf * (1 - zf - zf + 2);//* zf * (zf * (zf * 6.0f - 15.0f) + 9.999998f);
            final float wa = wf * wf * (1 - wf - wf + 2);//* wf * (wf * (wf * 6.0f - 15.0f) + 9.999998f);
            return
                    equalize(
                            lerp(
                                    lerp(
                                            lerp(
                                                    lerp(
                                                            gradCoord4D(seed, x0, y0, z0, w0, xf, yf, zf, wf),
                                                            gradCoord4D(seed, x0+ X4, y0, z0, w0, xf - 1, yf, zf, wf),
                                                            xa),
                                                    lerp(
                                                            gradCoord4D(seed, x0, y0+ Y4, z0, w0, xf, yf-1, zf, wf),
                                                            gradCoord4D(seed, x0+ X4, y0+ Y4, z0, w0, xf - 1, yf - 1, zf, wf),
                                                            xa),
                                                    ya),
                                            lerp(
                                                    lerp(
                                                            gradCoord4D(seed, x0, y0, z0+ Z4, w0, xf, yf, zf-1, wf),
                                                            gradCoord4D(seed, x0+ X4, y0, z0+ Z4, w0, xf - 1, yf, zf-1, wf),
                                                            xa),
                                                    lerp(
                                                            gradCoord4D(seed, x0, y0+ Y4, z0+ Z4, w0, xf, yf-1, zf-1, wf),
                                                            gradCoord4D(seed, x0+ X4, y0+ Y4, z0+ Z4, w0, xf - 1, yf - 1, zf-1, wf),
                                                            xa),
                                                    ya),
                                            za),
                                    lerp(
                                            lerp(
                                                    lerp(
                                                            gradCoord4D(seed, x0, y0, z0, w0+ W4, xf, yf, zf, wf - 1),
                                                            gradCoord4D(seed, x0+ X4, y0, z0, w0+ W4, xf - 1, yf, zf, wf - 1),
                                                            xa),
                                                    lerp(
                                                            gradCoord4D(seed, x0, y0+ Y4, z0, w0+ W4, xf, yf-1, zf, wf - 1),
                                                            gradCoord4D(seed, x0+ X4, y0+ Y4, z0, w0+ W4, xf - 1, yf - 1, zf, wf - 1),
                                                            xa),
                                                    ya),
                                            lerp(
                                                    lerp(
                                                            gradCoord4D(seed, x0, y0, z0+ Z4, w0+ W4, xf, yf, zf-1, wf - 1),
                                                            gradCoord4D(seed, x0+ X4, y0, z0+ Z4, w0+ W4, xf - 1, yf, zf-1, wf - 1),
                                                            xa),
                                                    lerp(
                                                            gradCoord4D(seed, x0, y0+ Y4, z0+ Z4, w0+ W4, xf, yf-1, zf-1, wf - 1),
                                                            gradCoord4D(seed, x0+ X4, y0+ Y4, z0+ Z4, w0+ W4, xf - 1, yf - 1, zf-1, wf - 1),
                                                            xa),
                                                    ya),
                                            za),
                                    wa) * SCALE4, EQ_ADD_4, EQ_MUL_4);//0.555f);
        }

        /**
         * Produces a String that describes everything needed to recreate this RawNoise in full. This String can be read back
         * in by {@link #stringDeserialize(String)} to reassign the described state to another RawNoise.
         * @return a String that describes this PerlueNoise for serialization
         */
        public String stringSerialize() {
            return "`" + seed + "`";
        }

        /**
         * Given a serialized String produced by {@link #stringSerialize()}, reassigns this PerlueNoise to have the
         * described state from the given String. The serialized String must have been produced by a PerlueNoise.
         *
         * @param data a serialized String, typically produced by {@link #stringSerialize()}
         * @return this PerlueNoise, after being modified (if possible)
         */
        public PerlueNoise stringDeserialize(String data) {
            seed = MathSupport.intFromDec(data, 1, data.indexOf('`', 1));
            return this;
        }

        /**
         * Creates a PerlueNoise from a String produced by {@link #stringSerialize()}.
         * @param data a serialized String, typically produced by {@link #stringSerialize()}
         * @return a new PerlueNoise, using the restored state from data
         */
        public static PerlueNoise recreateFromString(String data) {
            return new PerlueNoise(MathSupport.intFromDec(data, 1, data.indexOf('`', 1)));
        }
        /**
         * Gets a simple human-readable String that describes this noise generator. This should use names instead of coded
         * numbers, and should be enough to differentiate any two generators.
         * @return a String that describes this noise generator for human consumption
         */
        public String toHumanReadableString(){
            return getTag() + " with seed " + getSeed();
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PerlueNoise that = (PerlueNoise) o;

            return (seed == that.seed);
        }

        public int hashCode() {
            return seed;
        }

        public String toString() {
            return "PerlueNoise{seed=" + seed + '}';
        }

        /**
         * A 32-bit point hash that needs 2 dimensions pre-multiplied by constants {@link #X2} and {@link #Y2}, as
         * well as an int seed.
         * @param x x position, as an int pre-multiplied by {@link #X2}
         * @param y y position, as an int pre-multiplied by {@link #Y2}
         * @param s any int, a seed to be able to produce many hashes for a given point
         * @return 8-bit hash of the x,y point with the given state s, shifted for {@link GradientVectors#GRADIENTS_2D}
         */
        public static int hashAll(int x, int y, int s) {
            final int h = (s ^ x ^ y) * 0x125493;
            return (h ^ (h << 11 | h >>> 21) ^ (h << 23 | h >>> 9));
        }
        /**
         * A 32-bit point hash that needs 3 dimensions pre-multiplied by constants {@link #X3} through {@link #Z3}, as
         * well as an int seed.
         * @param x x position, as an int pre-multiplied by {@link #X3}
         * @param y y position, as an int pre-multiplied by {@link #Y3}
         * @param z z position, as an int pre-multiplied by {@link #Z3}
         * @param s any int, a seed to be able to produce many hashes for a given point
         * @return 8-bit hash of the x,y,z point with the given state s, shifted for {@link GradientVectors#GRADIENTS_3D}
         */
        public static int hashAll(int x, int y, int z, int s) {
            final int h = (s ^ x ^ y ^ z) * 0x125493;
            return (h ^ (h << 11 | h >>> 21) ^ (h << 23 | h >>> 9));
        }

        /**
         * A 32-bit point hash that needs 4 dimensions pre-multiplied by constants {@link #X4} through {@link #W4}, as
         * well as an int seed.
         * @param x x position, as an int pre-multiplied by {@link #X4}
         * @param y y position, as an int pre-multiplied by {@link #Y4}
         * @param z z position, as an int pre-multiplied by {@link #Z4}
         * @param w w position, as an int pre-multiplied by {@link #W4}
         * @param s any int, a seed to be able to produce many hashes for a given point
         * @return 8-bit hash of the x,y,z,w point with the given state s, shifted for {@link GradientVectors#GRADIENTS_4D}
         */
        public static int hashAll(int x, int y, int z, int w, int s) {
            final int h = (s ^ x ^ y ^ z ^ w) * 0x125493;
            return (h ^ (h << 11 | h >>> 21) ^ (h << 23 | h >>> 9));
        }

        // Predefined constants as 21-bit prime numbers, these are used as multipliers for x, y, z, and w, to make
        // changes to one component different from changes to another when used for hashing. These are 21-bit numbers
        // for GWT reasons; GWT loses precision past 53 bits, and multiplication of an unknown-size int with a constant
        // multiplier fits in 53 bits as long as the multiplier is 21 bits or smaller.
        public static final int
            X2 = 0x1827F5, Y2 = 0x123C3B,
            X3 = 0x1A36BF, Y3 = 0x157931, Z3 = 0x119749,
            X4 = 0x1B69E5, Y4 = 0x177C1F, Z4 = 0x141E75, W4 = 0x113C33;
    }
}
""",
    )
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage + ".util",
      fileName = "VoxelCollider.java",
      content =
        """package ${project.basic.rootPackage}.util;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

/**
 * Allows checking a group of {@code T} entities, where an entity is any type that can have a Vector3 position retrieved
 * from it via the {@link HasPosition3D} interface, for collisions with another entity or a member of its own group.
 * This doesn't currently allow getting a minimum translation vector to undo the collision, but since this only is meant
 * to check one moving entity at a time against all other entities, you can potentially refuse the movement that would
 * result in the collision by tracking the previous position and reverting to it if any collision occurs.
 * @param <T> Any type that you can get a Vector3 position from; must implement HasPosition3D, often {@link ${project.basic.rootPackage}.game.Mover}
 */
public class VoxelCollider<T extends HasPosition3D> {

    /**
     * The Array of all HasPosition3D entities that can enter a collision.
     */
    public Array<T> entities;

    /**
     * Temporary Vector3 to avoid allocating these all the time,
     */
    private final Vector3 tempVector3 = new Vector3();

    /**
     * Stores the most recent Array of colliding entities, which can be empty if there are no overlaps.
     */
    public final Array<T> colliding = new Array<>();

    /**
     * Creates a VoxelCollider with an empty entities Array.
     */
    public VoxelCollider() {
        entities = new Array<>(16);
    }

    /**
     * Creates a VoxelCollider with a copy of the given Array of entities.
     * @param colliders an Array of entities that will be copied
     */
    public VoxelCollider(Array<T> colliders){
        entities = new Array<>(colliders);
    }

    /**
     * Gets the Array of T entities that collide with the given Vector3 position.
     * If {@code collder} is a T in {@link #entities} (by identity), then the returned Array will never contain
     * that T entity.
     * <br>
     * This uses the Separated Axis Theorem to limit how much work must be done to process entities. It takes the list
     * of entities and adds any candidates for a collision to {@link #colliding}, determined at first by being close
     * enough on the x-axis. Once it has whatever entities are close on the x-axis, it checks only those to see if they
     * are close on y, removing any that aren't, repeating that step for entities close on z, and finally leaving only
     * the entities that have been close on x, y, and z in {@link #colliding}, which this reuses.
     * @param collider an entity to check for collision; will never be considered self-colliding, and may be in entities
     * @return a reused Array (which will change on the next call to this method) of all T entities that overlap with {@code collider}, not including itself.
     */
    public Array<T> collisionsWith(T collider) {
        Vector3 voxelPosition = collider.getPosition();
        // We reuse the position in tempVector3, which belongs to the VoxelCollider so the comparator can use it.
        tempVector3.set(voxelPosition);
        // The reused colliding Array must be cleared to avoid the last calculation remaining here.
        colliding.clear();
        float c = tempVector3.x;
        for (T e : entities) {
            if(Math.abs(c - e.getPosition().x) < 1f){
                // The distance between x coordinates is close enough for them to be overlapping
                colliding.add(e);
            }
        }
        // Remove the collider we are checking by identity, so it can't collide with itself.
        colliding.removeValue(collider, true);
        // If there's nothing else to remove, we're done!
        if(colliding.isEmpty()) return colliding;
        // Do these steps again on y, but without sorting now. There should be very few entities to check now.
        c = tempVector3.y;
        // We have to get an Iterator for the Array so we can remove during iteration.
        for (Array.ArrayIterator<T> iterator = colliding.iterator(); iterator.hasNext(); ) {
            T e = iterator.next();
            if (Math.abs(c - e.getPosition().y) >= 1f) {
                iterator.remove();
            }
        }
        // If there's nothing else to remove, we're done!
        if(colliding.isEmpty()) return colliding;
        // Do the previous steps for y, but on z this time.
        c = tempVector3.z;
        for (Array.ArrayIterator<T> iterator = colliding.iterator(); iterator.hasNext(); ) {
            T e = iterator.next();
            if (Math.abs(c - e.getPosition().z) >= 1f) {
                iterator.remove();
            }
        }
        // Any entities left in the Array will overlap on x, y, and z, meaning they collide.
        return colliding;
    }
}
""",
    )
    // game folder
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage + ".game",
      fileName = "AssetData.java",
      content =
        """package ${project.basic.rootPackage}.game;

import com.badlogic.gdx.utils.*;
import ${project.basic.rootPackage}.LocalMap;

/**
 * Data specific to the art assets used here. If the art assets change, you will need to update this class.
 * This tracks not just the numbers associated with unnamed art assets, but also which tiles are rotations of other
 * tiles for when the map rotates, which path tiles can connect to which other path tiles, and so on. The
 * {@link #realignPaths(LocalMap)} method takes a LocalMap and updates the rotations of paths in it so they all line up
 * coherently. There are also some numbers and names for entities, but the code here mostly doesn't use them because the
 * specific assets were already sorted how we needed them -- entities 0-3 are playable humans, and 4-7 are enemy orcs.
 * <br>
 * This has constants to provide names for the numbered tiles and entities in the {@code isometric-trpg.atlas}, a map
 * {@link #TILES} that allows looking up tile constants with a String name, and a map {@link #ENTITIES} that allows
 * looking up entity constants (for use in the four Animation Arrays). Importantly, this also stores data for how to
 * render some tiles when they rotate, to prevent paths from becoming disconnected when the map rotates.
 * <br>
 * In the project as it is here, all tiles have already been stored in a single atlas on one page, sharing that page
 * with a bitmap font and UI widgets. Using just one atlas page helps performance quite a bit, because it avoids any
 * "texture swaps" that SpriteBatch would otherwise need to do when a different page or Texture was needed. If you have
 * your own assets, packing them into an atlas is strongly recommended! For this project, the very convenient
 * <a href="https://github.com/crashinvaders/gdx-texture-packer-gui">CrashInvaders' GDX Texture Packer GUI</a> was used,
 * but you can also use the texture packer supplied in libGDX as part of its gdx-tools, or launch that packer as part of
 * your Gradle configuration so your atlas is always up-to-date. Packing an atlas does best with individual tiny images
 * with the appropriate names you want to look up, OR some common prefix followed by an underscore {@code _} and a
 * number to make (usually) an animation frame with that index. The numbers can be finicky; make sure to start at 0 and
 * not include any initial 0 otherwise ("0", "1", "2", is good, but "00", "01", "02" is not).
 * <br>
 * If you use a different set of assets, you will not need the values in this file, but you could use it as an example
 * for how to make your own. You could also just use int indices throughout your project, or name the regions in your
 * atlas according to your own rules. Other free assets may have names you can apply to the atlas instead of numbers,
 * but this tends to be rather rare in practice. It isn't isometric normally, but
 * <a href="https://github.com/tommyettinger/DawnLikeAtlas">DawnLikeAtlas</a> is a large, free set of assets that has
 * names attached to each file in a libGDX atlas. Its entities are about the same size as the ones used here, though
 * they don't have facing and have just two animation frames. The DawnLike creatures could be used instead of the assets
 * here, though, since there are many more of them. Ideally, you would repack any extra assets into an atlas like the
 * one this uses (with a font and any terrain you want). You can either fetch the raw assets from the independent repo
 * used for in-progress parts of this project,
 * <a href="https://github.com/tommyettinger/IsometricVoxelDemo">which is here</a>, or unpack the atlas used here (the
 * GUI linked above has an "Unpack atlas" option in the top bar), then get the parts you want from any atlases you want
 * to mix in, and pack those all together into one central atlas. Using just one folder works well.
 * <br>
 * There are several terms used in the tile names for grouping logically.
 * <ul>
 * <li>Tiles that are meant to be approximately half as tall as a unit voxel contain "HALF" in the constant name.</li>
 * <li>Tiles that are meant to just barely cover the top of a unit voxel contain "BASE" in their constant name.</li>
 * <li>Tiles that are meant to flow over the top of a surface of their height contain "COVER" in their constant name.</li>
 * <li>Cover tiles have a suffix containing "F", "G", and/or "H" depending on whether they cover the f or g side edges,
 * or the h face on top.</li>
 * <li>Tiles that have a path on them that changes depending on view angle have "PATH" in their constant name. Path tiles
 * have a suffix containing "F", "G", "T", and/or "R" depending on which faces are connected to the path, where fgtr are
 * the recommended keys on a QWERTY keyboard to move in those directions, or on a map of Europe, the locations of
 * France, Germany, Tallinn (in Estonia), and Reykjavík (in Iceland) relative to Amsterdam in the center.</li>
 * <li>Tiles that are not meant to be stackable have "DECO" in their constant name.</li>
 * <li>Bed tiles have "BED" in the name and a suffix indicating the faces that touch the footboard and headboard.</li>
 * <li>Some tiles have "CAP" in their name to indicate that only the top face is a given type, and the rest of the unit
 * voxel is simply dirt.</li>
 * </ul>
 */
@SuppressWarnings("PointlessArithmeticExpression")
public final class AssetData {
    /**
     * The horizontal distance in pixels between adjacent tiles. This is equivalent to the distance of one diagonal side
     * of the diamond-shaped top of any solid tile here, measured from left to right for a single side.
     * <br>
     * This depends on your exact terrain assets, and it will potentially change if your art does.
     */
    public static final int TILE_WIDTH = 8;
    /**
     * The vertical distance in pixels between adjacent tiles. This is equivalent to the distance of one diagonal side
     * of the diamond-shaped top of any solid tile here, measured from bottom to top for a single side.
     * <br>
     * This depends on your exact terrain assets, and it will potentially change if your art does.
     */
    public static final int TILE_HEIGHT = 4;
    /**
     * The vertical distance in pixels between stacked tiles. This is equivalent to the distance of a vertical side of
     * any full-sized solid tile (on the left or right side of the block), measured from bottom to top of a solid side.
     * <br>
     * This depends on your exact terrain assets, and it will potentially change if your art does.
     */
    public static final int TILE_DEPTH = 8;

    /**
     * No need to instantiate.
     */
    private AssetData() {}

    public static final int DIRT                 =   0 +  0;
    public static final int GRASS                =   1 +  0;
    public static final int BASALT               =   2 +  0;
    public static final int SAND                 =   3 +  0;
    public static final int HALF_COVER_WATER_FGH =   4 +  0;
    public static final int HALF_COVER_WATER_FH  =   5 +  0;
    public static final int HALF_COVER_WATER_GH  =   6 +  0;
    public static final int HALF_COVER_WATER_H   =   7 +  0;
    public static final int DECO_ROCKS           =   8 +  0;
    public static final int DECO_BOULDER         =   9 +  0;
    public static final int DECO_CATTAIL         =  10 +  0;
    public static final int HALF_DIRT            =   0 + 11;
    public static final int HALF_GRASS           =   1 + 11;
    public static final int HALF_BASALT          =   2 + 11;
    public static final int HALF_SAND            =   3 + 11;
    public static final int BASE_COVER_WATER_FGH =   4 + 11;
    public static final int BASE_COVER_WATER_FH  =   5 + 11;
    public static final int BASE_COVER_WATER_GH  =   6 + 11;
    public static final int BASE_COVER_WATER_H   =   7 + 11;
    public static final int DECO_BUSH            =   8 + 11;
    public static final int DECO_STUMP           =   9 + 11;
    public static final int DECO_FLAME           =  10 + 11;
    public static final int SNOW                 =   0 + 22;
    public static final int ICE                  =   1 + 22;
    public static final int LAVA                 =   2 + 22;
    public static final int DRY                  =   3 + 22;
    public static final int HALF_COVER_SWAMP_FGH =   4 + 22;
    public static final int HALF_COVER_SWAMP_FH  =   5 + 22;
    public static final int HALF_COVER_SWAMP_GH  =   6 + 22;
    public static final int HALF_COVER_SWAMP_H   =   7 + 22;
    public static final int DECO_BUNDLE          =   8 + 22;
    public static final int DECO_HEDGE           =   9 + 22;
    public static final int DECO_CRYSTALS        =  10 + 22;
    public static final int HALF_SNOW            =   0 + 33;
    public static final int HALF_ICE             =   1 + 33;
    public static final int HALF_LAVA            =   2 + 33;
    public static final int HALF_DRY             =   3 + 33;
    public static final int BASE_COVER_SWAMP_FGH =   4 + 33;
    public static final int BASE_COVER_SWAMP_FH  =   5 + 33;
    public static final int BASE_COVER_SWAMP_GH  =   6 + 33;
    public static final int BASE_COVER_SWAMP_H   =   7 + 33;
    public static final int DECO_LOG             =   8 + 33;
    public static final int DECO_CACTUS          =   9 + 33;
    public static final int DECO_SPIKE           =  10 + 33;
    public static final int PATH_GRASS_GR        =   0 + 44;
    public static final int PATH_GRASS_FT        =   1 + 44;
    public static final int PATH_GRASS_FTR       =   2 + 44;
    public static final int PATH_GRASS_FGT       =   3 + 44;
    public static final int PATH_GRASS_FGTR      =   4 + 44;
    public static final int PATH_GRASS_GTR       =   5 + 44;
    public static final int PATH_GRASS_FGR       =   6 + 44;
    public static final int PATH_GRASS_FG        =   7 + 44;
    public static final int PATH_GRASS_GT        =   8 + 44;
    public static final int PATH_GRASS_TR        =   9 + 44;
    public static final int PATH_GRASS_FR        =  10 + 44;
    public static final int HALF_PATH_GRASS_GR   =   0 + 55;
    public static final int HALF_PATH_GRASS_FT   =   1 + 55;
    public static final int HALF_PATH_GRASS_FTR  =   2 + 55;
    public static final int HALF_PATH_GRASS_FGT  =   3 + 55;
    public static final int HALF_PATH_GRASS_FGTR =   4 + 55;
    public static final int HALF_PATH_GRASS_GTR  =   5 + 55;
    public static final int HALF_PATH_GRASS_FGR  =   6 + 55;
    public static final int HALF_PATH_GRASS_FG   =   7 + 55;
    public static final int HALF_PATH_GRASS_GT   =   8 + 55;
    public static final int HALF_PATH_GRASS_TR   =   9 + 55;
    public static final int HALF_PATH_GRASS_FR   =  10 + 55;
    public static final int PATH_DRY_GR          =   0 + 66;
    public static final int PATH_DRY_FT          =   1 + 66;
    public static final int PATH_DRY_FTR         =   2 + 66;
    public static final int PATH_DRY_FGT         =   3 + 66;
    public static final int PATH_DRY_FGTR        =   4 + 66;
    public static final int PATH_DRY_GTR         =   5 + 66;
    public static final int PATH_DRY_FGR         =   6 + 66;
    public static final int PATH_DRY_FG          =   7 + 66;
    public static final int PATH_DRY_GT          =   8 + 66;
    public static final int PATH_DRY_TR          =   9 + 66;
    public static final int PATH_DRY_FR          =  10 + 66;
    public static final int HALF_PATH_DRY_GR     =   0 + 77;
    public static final int HALF_PATH_DRY_FT     =   1 + 77;
    public static final int HALF_PATH_DRY_FTR    =   2 + 77;
    public static final int HALF_PATH_DRY_FGT    =   3 + 77;
    public static final int HALF_PATH_DRY_FGTR   =   4 + 77;
    public static final int HALF_PATH_DRY_GTR    =   5 + 77;
    public static final int HALF_PATH_DRY_FGR    =   6 + 77;
    public static final int HALF_PATH_DRY_FG     =   7 + 77;
    public static final int HALF_PATH_DRY_GT     =   8 + 77;
    public static final int HALF_PATH_DRY_TR     =   9 + 77;
    public static final int HALF_PATH_DRY_FR     =  10 + 77;
    public static final int SANDSTONE            =   0 + 88;
    public static final int SLATE                =   1 + 88;
    public static final int BRICK                =   2 + 88;
    public static final int WOOD                 =   3 + 88;
    public static final int CAP_SANDSTONE        =   4 + 88;
    public static final int CAP_SLATE            =   5 + 88;
    public static final int CAP_WOOD             =   6 + 88;
    public static final int CAP_THATCHED         =   7 + 88;
    public static final int DECO_BED_GR          =   8 + 88;
    public static final int DECO_BED_FT          =   9 + 88;
    public static final int DECO_FENCE_GR        =  10 + 88;
    public static final int HALF_SANDSTONE       =   0 + 99;
    public static final int HALF_SLATE           =   1 + 99;
    public static final int HALF_BRICK           =   2 + 99;
    public static final int HALF_WOOD            =   3 + 99;
    public static final int BASE_SANDSTONE       =   4 + 99;
    public static final int BASE_SLATE           =   5 + 99;
    public static final int BASE_WOOD            =   6 + 99;
    public static final int BASE_THATCHED        =   7 + 99;
    public static final int HALF_DECO_BED_GR     =   8 + 99;
    public static final int HALF_DECO_BED_FT     =   9 + 99;
    public static final int DECO_FENCE_FT        =  10 + 99;

    public static final int KNIGHT      = 0;
    public static final int LANCER      = 1;
    public static final int ARCHER      = 2;
    public static final int ROGUE       = 3;
    public static final int ORC_BRUTE   = 4;
    public static final int ORC_GUARD   = 5;
    public static final int ORC_BUTCHER = 6;
    public static final int ORC_SLINGER = 7;
    public static final int SHADOW      = 8;
    public static final int WIZARD      = 9;
    public static final int DEMON       = 10;
    public static final int CROSSBOWMAN = 11;
    public static final int MITE        = 12;
    public static final int FISH        = 13;
    public static final int BAT         = 14;
    public static final int JELLY       = 15;

    /** Special index; not an animation. */
    public static final int TOMBSTONE   = 128;
    /** Special index; not an animation. */
    public static final int UNKNOWN     = 129;

    public static final ObjectIntMap<String> TILES = new ObjectIntMap<>(128);
    public static final ObjectIntMap<String> ENTITIES = new ObjectIntMap<>(24);
    public static final IntSet UNIT_VOXELS;
    public static final IntSet HALF_VOXELS;
    public static final IntSet BASE_VOXELS;
    public static final IntSet HALF_COVERS;
    public static final IntSet BASE_COVERS;
    public static final IntSet DECORATIONS;
    public static final IntSet HALF_DECORATIONS;
    public static final IntSet UNIT_ANY;
    public static final IntSet HALF_ANY;
    public static final IntSet BASE_ANY;

    /**
     * This maps indices of rotation-dependent tiles to their appropriate rotated versions when rotated 0 degrees, 90
     * degrees counterclockwise, 180 degrees, and 270 degrees counterclockwise. The rotated versions are also in the
     * form of indices, each an array of exactly 4 int indices (for the listed rotations, in that order).
     */
    public static final IntMap<int[]> ROTATIONS = new IntMap<>(58);
    /**
     * Maps keys that are ints storing data on which adjacent cells are also paths, to indices to the appropriate tiles
     * to change to. This defaults to returning paths over grass; to get paths over half-voxel grass, add 11. For paths
     * over dry brush, add 22, and for paths over half-voxel dry brush, add 33.
     */
    public static final IntIntMap PATHS = new IntIntMap(16);

    static {
        TILES.put("dirt"                , DIRT                );
        TILES.put("grass"               , GRASS               );
        TILES.put("basalt"              , BASALT              );
        TILES.put("sand"                , SAND                );
        TILES.put("half cover water fgh", HALF_COVER_WATER_FGH);
        TILES.put("half cover water fh" , HALF_COVER_WATER_FH );
        TILES.put("half cover water gh" , HALF_COVER_WATER_GH );
        TILES.put("half cover water h"  , HALF_COVER_WATER_H  );
        TILES.put("deco rocks"          , DECO_ROCKS          );
        TILES.put("deco boulder"        , DECO_BOULDER        );
        TILES.put("deco cattail"        , DECO_CATTAIL        );
        TILES.put("half dirt"           , HALF_DIRT           );
        TILES.put("half grass"          , HALF_GRASS          );
        TILES.put("half basalt"         , HALF_BASALT         );
        TILES.put("half sand"           , HALF_SAND           );
        TILES.put("base cover water fgh", BASE_COVER_WATER_FGH);
        TILES.put("base cover water fh" , BASE_COVER_WATER_FH );
        TILES.put("base cover water gh" , BASE_COVER_WATER_GH );
        TILES.put("base cover water h"  , BASE_COVER_WATER_H  );
        TILES.put("deco bush"           , DECO_BUSH           );
        TILES.put("deco stump"          , DECO_STUMP          );
        TILES.put("deco flame"          , DECO_FLAME          );
        TILES.put("snow"                , SNOW                );
        TILES.put("ice"                 , ICE                 );
        TILES.put("lava"                , LAVA                );
        TILES.put("dry"                 , DRY                 );
        TILES.put("half cover swamp fgh", HALF_COVER_SWAMP_FGH);
        TILES.put("half cover swamp fh" , HALF_COVER_SWAMP_FH );
        TILES.put("half cover swamp gh" , HALF_COVER_SWAMP_GH );
        TILES.put("half cover swamp h"  , HALF_COVER_SWAMP_H  );
        TILES.put("deco bundle"         , DECO_BUNDLE         );
        TILES.put("deco hedge"          , DECO_HEDGE          );
        TILES.put("deco crystals"       , DECO_CRYSTALS       );
        TILES.put("half snow"           , HALF_SNOW           );
        TILES.put("half ice"            , HALF_ICE            );
        TILES.put("half lava"           , HALF_LAVA           );
        TILES.put("half dry"            , HALF_DRY            );
        TILES.put("base cover swamp fgh", BASE_COVER_SWAMP_FGH);
        TILES.put("base cover swamp fh" , BASE_COVER_SWAMP_FH );
        TILES.put("base cover swamp gh" , BASE_COVER_SWAMP_GH );
        TILES.put("base cover swamp h"  , BASE_COVER_SWAMP_H  );
        TILES.put("deco log"            , DECO_LOG            );
        TILES.put("deco cactus"         , DECO_CACTUS         );
        TILES.put("deco spike"          , DECO_SPIKE          );
        TILES.put("path grass gr"       , PATH_GRASS_GR       );
        TILES.put("path grass ft"       , PATH_GRASS_FT       );
        TILES.put("path grass ftr"      , PATH_GRASS_FTR      );
        TILES.put("path grass fgt"      , PATH_GRASS_FGT      );
        TILES.put("path grass fgtr"     , PATH_GRASS_FGTR     );
        TILES.put("path grass gtr"      , PATH_GRASS_GTR      );
        TILES.put("path grass fgr"      , PATH_GRASS_FGR      );
        TILES.put("path grass fg"       , PATH_GRASS_FG       );
        TILES.put("path grass gt"       , PATH_GRASS_GT       );
        TILES.put("path grass tr"       , PATH_GRASS_TR       );
        TILES.put("path grass fr"       , PATH_GRASS_FR       );
        TILES.put("half path grass gr"  , HALF_PATH_GRASS_GR  );
        TILES.put("half path grass ft"  , HALF_PATH_GRASS_FT  );
        TILES.put("half path grass ftr" , HALF_PATH_GRASS_FTR );
        TILES.put("half path grass fgt" , HALF_PATH_GRASS_FGT );
        TILES.put("half path grass fgtr", HALF_PATH_GRASS_FGTR);
        TILES.put("half path grass gtr" , HALF_PATH_GRASS_GTR );
        TILES.put("half path grass fgr" , HALF_PATH_GRASS_FGR );
        TILES.put("half path grass fg"  , HALF_PATH_GRASS_FG  );
        TILES.put("half path grass gt"  , HALF_PATH_GRASS_GT  );
        TILES.put("half path grass tr"  , HALF_PATH_GRASS_TR  );
        TILES.put("half path grass fr"  , HALF_PATH_GRASS_FR  );
        TILES.put("path dry gr"         , PATH_DRY_GR         );
        TILES.put("path dry ft"         , PATH_DRY_FT         );
        TILES.put("path dry ftr"        , PATH_DRY_FTR        );
        TILES.put("path dry fgt"        , PATH_DRY_FGT        );
        TILES.put("path dry fgtr"       , PATH_DRY_FGTR       );
        TILES.put("path dry gtr"        , PATH_DRY_GTR        );
        TILES.put("path dry fgr"        , PATH_DRY_FGR        );
        TILES.put("path dry fg"         , PATH_DRY_FG         );
        TILES.put("path dry gt"         , PATH_DRY_GT         );
        TILES.put("path dry tr"         , PATH_DRY_TR         );
        TILES.put("path dry fr"         , PATH_DRY_FR         );
        TILES.put("half path dry gr"    , HALF_PATH_DRY_GR    );
        TILES.put("half path dry ft"    , HALF_PATH_DRY_FT    );
        TILES.put("half path dry ftr"   , HALF_PATH_DRY_FTR   );
        TILES.put("half path dry fgt"   , HALF_PATH_DRY_FGT   );
        TILES.put("half path dry fgtr"  , HALF_PATH_DRY_FGTR  );
        TILES.put("half path dry gtr"   , HALF_PATH_DRY_GTR   );
        TILES.put("half path dry fgr"   , HALF_PATH_DRY_FGR   );
        TILES.put("half path dry fg"    , HALF_PATH_DRY_FG    );
        TILES.put("half path dry gt"    , HALF_PATH_DRY_GT    );
        TILES.put("half path dry tr"    , HALF_PATH_DRY_TR    );
        TILES.put("half path dry fr"    , HALF_PATH_DRY_FR    );
        TILES.put("sandstone"           , SANDSTONE           );
        TILES.put("slate"               , SLATE               );
        TILES.put("brick"               , BRICK               );
        TILES.put("wood"                , WOOD                );
        TILES.put("cap sandstone"       , CAP_SANDSTONE       );
        TILES.put("cap slate"           , CAP_SLATE           );
        TILES.put("cap wood"            , CAP_WOOD            );
        TILES.put("cap thatched"        , CAP_THATCHED        );
        TILES.put("deco bed gr"         , DECO_BED_GR         );
        TILES.put("deco bed ft"         , DECO_BED_FT         );
        TILES.put("deco fence gr"       , DECO_FENCE_GR       );
        TILES.put("half sandstone"      , HALF_SANDSTONE      );
        TILES.put("half slate"          , HALF_SLATE          );
        TILES.put("half brick"          , HALF_BRICK          );
        TILES.put("half wood"           , HALF_WOOD           );
        TILES.put("base sandstone"      , BASE_SANDSTONE      );
        TILES.put("base slate"          , BASE_SLATE          );
        TILES.put("base wood"           , BASE_WOOD           );
        TILES.put("base thatched"       , BASE_THATCHED       );
        TILES.put("half deco bed gr"    , HALF_DECO_BED_GR    );
        TILES.put("half deco bed ft"    , HALF_DECO_BED_FT    );
        TILES.put("deco fence ft"       , DECO_FENCE_FT       );

        ENTITIES.put("knight"      , KNIGHT      );
        ENTITIES.put("lancer"      , LANCER      );
        ENTITIES.put("archer"      , ARCHER      );
        ENTITIES.put("rogue"       , ROGUE       );
        ENTITIES.put("orc brute"   , ORC_BRUTE   );
        ENTITIES.put("orc guard"   , ORC_GUARD   );
        ENTITIES.put("orc butcher" , ORC_BUTCHER );
        ENTITIES.put("orc slinger" , ORC_SLINGER );
        ENTITIES.put("shadow"      , SHADOW      );
        ENTITIES.put("wizard"      , WIZARD      );
        ENTITIES.put("demon"       , DEMON       );
        ENTITIES.put("crossbowman" , CROSSBOWMAN );
        ENTITIES.put("mite"        , MITE        );
        ENTITIES.put("fish"        , FISH        );
        ENTITIES.put("bat"         , BAT         );
        ENTITIES.put("jelly"       , JELLY       );

        ROTATIONS.put(HALF_COVER_WATER_FGH, new int[]{HALF_COVER_WATER_FGH, HALF_COVER_WATER_GH, HALF_COVER_WATER_H, HALF_COVER_WATER_FH, });
        ROTATIONS.put(HALF_COVER_WATER_FH, new int[]{HALF_COVER_WATER_FH, HALF_COVER_WATER_GH, HALF_COVER_WATER_H, HALF_COVER_WATER_H, });
        ROTATIONS.put(HALF_COVER_WATER_GH, new int[]{HALF_COVER_WATER_GH, HALF_COVER_WATER_H, HALF_COVER_WATER_H, HALF_COVER_WATER_FH, });
        ROTATIONS.put(BASE_COVER_WATER_FGH, new int[]{BASE_COVER_WATER_FGH, BASE_COVER_WATER_GH, BASE_COVER_WATER_H, BASE_COVER_WATER_FH, });
        ROTATIONS.put(BASE_COVER_WATER_FH, new int[]{BASE_COVER_WATER_FH, BASE_COVER_WATER_GH, BASE_COVER_WATER_H, BASE_COVER_WATER_H, });
        ROTATIONS.put(BASE_COVER_WATER_GH, new int[]{BASE_COVER_WATER_GH, BASE_COVER_WATER_H, BASE_COVER_WATER_H, BASE_COVER_WATER_FH, });
        ROTATIONS.put(HALF_COVER_SWAMP_FGH, new int[]{HALF_COVER_SWAMP_FGH, HALF_COVER_SWAMP_GH, HALF_COVER_SWAMP_H, HALF_COVER_SWAMP_FH, });
        ROTATIONS.put(HALF_COVER_SWAMP_FH, new int[]{HALF_COVER_SWAMP_FH, HALF_COVER_SWAMP_GH, HALF_COVER_SWAMP_H, HALF_COVER_SWAMP_H, });
        ROTATIONS.put(HALF_COVER_SWAMP_GH, new int[]{HALF_COVER_SWAMP_GH, HALF_COVER_SWAMP_H, HALF_COVER_SWAMP_H, HALF_COVER_SWAMP_FH, });
        ROTATIONS.put(BASE_COVER_SWAMP_FGH, new int[]{BASE_COVER_SWAMP_FGH, BASE_COVER_SWAMP_GH, BASE_COVER_SWAMP_H, BASE_COVER_SWAMP_FH, });
        ROTATIONS.put(BASE_COVER_SWAMP_FH, new int[]{BASE_COVER_SWAMP_FH, BASE_COVER_SWAMP_GH, BASE_COVER_SWAMP_H, BASE_COVER_SWAMP_H, });
        ROTATIONS.put(BASE_COVER_SWAMP_GH, new int[]{BASE_COVER_SWAMP_GH, BASE_COVER_SWAMP_H, BASE_COVER_SWAMP_H, BASE_COVER_SWAMP_FH, });
        ROTATIONS.put(PATH_GRASS_GR, new int[]{PATH_GRASS_GR, PATH_GRASS_FT, PATH_GRASS_GR, PATH_GRASS_FT, });
        ROTATIONS.put(PATH_GRASS_FT, new int[]{PATH_GRASS_FT, PATH_GRASS_GR, PATH_GRASS_FT, PATH_GRASS_GR, });
        ROTATIONS.put(PATH_GRASS_FTR, new int[]{PATH_GRASS_FTR, PATH_GRASS_FGR, PATH_GRASS_FGT, PATH_GRASS_GTR, });
        ROTATIONS.put(PATH_GRASS_FGR, new int[]{PATH_GRASS_FGR, PATH_GRASS_FGT, PATH_GRASS_GTR, PATH_GRASS_FTR, });
        ROTATIONS.put(PATH_GRASS_FGT, new int[]{PATH_GRASS_FGT, PATH_GRASS_GTR, PATH_GRASS_FTR, PATH_GRASS_FGR, });
        ROTATIONS.put(PATH_GRASS_GTR, new int[]{PATH_GRASS_GTR, PATH_GRASS_FTR, PATH_GRASS_FGR, PATH_GRASS_FGT, });
        ROTATIONS.put(PATH_GRASS_FG, new int[]{PATH_GRASS_FG, PATH_GRASS_GT, PATH_GRASS_TR, PATH_GRASS_FR, });
        ROTATIONS.put(PATH_GRASS_GT, new int[]{PATH_GRASS_GT, PATH_GRASS_TR, PATH_GRASS_FR, PATH_GRASS_FG, });
        ROTATIONS.put(PATH_GRASS_TR, new int[]{PATH_GRASS_TR, PATH_GRASS_FR, PATH_GRASS_FG, PATH_GRASS_GT, });
        ROTATIONS.put(PATH_GRASS_FR, new int[]{PATH_GRASS_FR, PATH_GRASS_FG, PATH_GRASS_GT, PATH_GRASS_TR, });
        ROTATIONS.put(HALF_PATH_GRASS_GR, new int[]{HALF_PATH_GRASS_GR, HALF_PATH_GRASS_FT, HALF_PATH_GRASS_GR, HALF_PATH_GRASS_FT, });
        ROTATIONS.put(HALF_PATH_GRASS_FT, new int[]{HALF_PATH_GRASS_FT, HALF_PATH_GRASS_GR, HALF_PATH_GRASS_FT, HALF_PATH_GRASS_GR, });
        ROTATIONS.put(HALF_PATH_GRASS_FTR, new int[]{HALF_PATH_GRASS_FTR, HALF_PATH_GRASS_FGR, HALF_PATH_GRASS_FGT, HALF_PATH_GRASS_GTR, });
        ROTATIONS.put(HALF_PATH_GRASS_FGR, new int[]{HALF_PATH_GRASS_FGR, HALF_PATH_GRASS_FGT, HALF_PATH_GRASS_GTR, HALF_PATH_GRASS_FTR, });
        ROTATIONS.put(HALF_PATH_GRASS_FGT, new int[]{HALF_PATH_GRASS_FGT, HALF_PATH_GRASS_GTR, HALF_PATH_GRASS_FTR, HALF_PATH_GRASS_FGR, });
        ROTATIONS.put(HALF_PATH_GRASS_GTR, new int[]{HALF_PATH_GRASS_GTR, HALF_PATH_GRASS_FTR, HALF_PATH_GRASS_FGR, HALF_PATH_GRASS_FGT, });
        ROTATIONS.put(HALF_PATH_GRASS_FG, new int[]{HALF_PATH_GRASS_FG, HALF_PATH_GRASS_GT, HALF_PATH_GRASS_TR, HALF_PATH_GRASS_FR, });
        ROTATIONS.put(HALF_PATH_GRASS_GT, new int[]{HALF_PATH_GRASS_GT, HALF_PATH_GRASS_TR, HALF_PATH_GRASS_FR, HALF_PATH_GRASS_FG, });
        ROTATIONS.put(HALF_PATH_GRASS_TR, new int[]{HALF_PATH_GRASS_TR, HALF_PATH_GRASS_FR, HALF_PATH_GRASS_FG, HALF_PATH_GRASS_GT, });
        ROTATIONS.put(HALF_PATH_GRASS_FR, new int[]{HALF_PATH_GRASS_FR, HALF_PATH_GRASS_FG, HALF_PATH_GRASS_GT, HALF_PATH_GRASS_TR, });
        ROTATIONS.put(PATH_DRY_GR, new int[]{PATH_DRY_GR, PATH_DRY_FT, PATH_DRY_GR, PATH_DRY_FT, });
        ROTATIONS.put(PATH_DRY_FT, new int[]{PATH_DRY_FT, PATH_DRY_GR, PATH_DRY_FT, PATH_DRY_GR, });
        ROTATIONS.put(PATH_DRY_FTR, new int[]{PATH_DRY_FTR, PATH_DRY_FGR, PATH_DRY_FGT, PATH_DRY_GTR, });
        ROTATIONS.put(PATH_DRY_FGR, new int[]{PATH_DRY_FGR, PATH_DRY_FGT, PATH_DRY_GTR, PATH_DRY_FTR, });
        ROTATIONS.put(PATH_DRY_FGT, new int[]{PATH_DRY_FGT, PATH_DRY_GTR, PATH_DRY_FTR, PATH_DRY_FGR, });
        ROTATIONS.put(PATH_DRY_GTR, new int[]{PATH_DRY_GTR, PATH_DRY_FTR, PATH_DRY_FGR, PATH_DRY_FGT, });
        ROTATIONS.put(PATH_DRY_FG, new int[]{PATH_DRY_FG, PATH_DRY_GT, PATH_DRY_TR, PATH_DRY_FR, });
        ROTATIONS.put(PATH_DRY_GT, new int[]{PATH_DRY_GT, PATH_DRY_TR, PATH_DRY_FR, PATH_DRY_FG, });
        ROTATIONS.put(PATH_DRY_TR, new int[]{PATH_DRY_TR, PATH_DRY_FR, PATH_DRY_FG, PATH_DRY_GT, });
        ROTATIONS.put(PATH_DRY_FR, new int[]{PATH_DRY_FR, PATH_DRY_FG, PATH_DRY_GT, PATH_DRY_TR, });
        ROTATIONS.put(HALF_PATH_DRY_GR, new int[]{HALF_PATH_DRY_GR, HALF_PATH_DRY_FT, HALF_PATH_DRY_GR, HALF_PATH_DRY_FT, });
        ROTATIONS.put(HALF_PATH_DRY_FT, new int[]{HALF_PATH_DRY_FT, HALF_PATH_DRY_GR, HALF_PATH_DRY_FT, HALF_PATH_DRY_GR, });
        ROTATIONS.put(HALF_PATH_DRY_FTR, new int[]{HALF_PATH_DRY_FTR, HALF_PATH_DRY_FGR, HALF_PATH_DRY_FGT, HALF_PATH_DRY_GTR, });
        ROTATIONS.put(HALF_PATH_DRY_FGR, new int[]{HALF_PATH_DRY_FGR, HALF_PATH_DRY_FGT, HALF_PATH_DRY_GTR, HALF_PATH_DRY_FTR, });
        ROTATIONS.put(HALF_PATH_DRY_FGT, new int[]{HALF_PATH_DRY_FGT, HALF_PATH_DRY_GTR, HALF_PATH_DRY_FTR, HALF_PATH_DRY_FGR, });
        ROTATIONS.put(HALF_PATH_DRY_GTR, new int[]{HALF_PATH_DRY_GTR, HALF_PATH_DRY_FTR, HALF_PATH_DRY_FGR, HALF_PATH_DRY_FGT, });
        ROTATIONS.put(HALF_PATH_DRY_FG, new int[]{HALF_PATH_DRY_FG, HALF_PATH_DRY_GT, HALF_PATH_DRY_TR, HALF_PATH_DRY_FR, });
        ROTATIONS.put(HALF_PATH_DRY_GT, new int[]{HALF_PATH_DRY_GT, HALF_PATH_DRY_TR, HALF_PATH_DRY_FR, HALF_PATH_DRY_FG, });
        ROTATIONS.put(HALF_PATH_DRY_TR, new int[]{HALF_PATH_DRY_TR, HALF_PATH_DRY_FR, HALF_PATH_DRY_FG, HALF_PATH_DRY_GT, });
        ROTATIONS.put(HALF_PATH_DRY_FR, new int[]{HALF_PATH_DRY_FR, HALF_PATH_DRY_FG, HALF_PATH_DRY_GT, HALF_PATH_DRY_TR, });
        ROTATIONS.put(DECO_BED_GR, new int[]{DECO_BED_GR, DECO_BED_FT, DECO_BED_GR, DECO_BED_FT, });
        ROTATIONS.put(DECO_BED_FT, new int[]{DECO_BED_FT, DECO_BED_GR, DECO_BED_FT, DECO_BED_GR, });
        ROTATIONS.put(HALF_DECO_BED_GR, new int[]{HALF_DECO_BED_GR, HALF_DECO_BED_FT, HALF_DECO_BED_GR, HALF_DECO_BED_FT, });
        ROTATIONS.put(HALF_DECO_BED_FT, new int[]{HALF_DECO_BED_FT, HALF_DECO_BED_GR, HALF_DECO_BED_FT, HALF_DECO_BED_GR, });
        ROTATIONS.put(DECO_FENCE_GR, new int[]{DECO_FENCE_GR, DECO_FENCE_FT, DECO_FENCE_GR, DECO_FENCE_FT, });
        ROTATIONS.put(DECO_FENCE_FT, new int[]{DECO_FENCE_FT, DECO_FENCE_GR, DECO_FENCE_FT, DECO_FENCE_GR, });

        PATHS.put( 1, PATH_GRASS_FT);
        PATHS.put( 2, PATH_GRASS_GR);
        PATHS.put( 3, PATH_GRASS_FG);
        PATHS.put( 4, PATH_GRASS_FT);
        PATHS.put( 5, PATH_GRASS_FT);
        PATHS.put( 6, PATH_GRASS_GT);
        PATHS.put( 7, PATH_GRASS_FGT);
        PATHS.put( 8, PATH_GRASS_GR);
        PATHS.put( 9, PATH_GRASS_FR);
        PATHS.put(10, PATH_GRASS_GR);
        PATHS.put(11, PATH_GRASS_FGR);
        PATHS.put(12, PATH_GRASS_TR);
        PATHS.put(13, PATH_GRASS_FTR);
        PATHS.put(14, PATH_GRASS_GTR);
        PATHS.put(15, PATH_GRASS_FGTR);

        UNIT_VOXELS = new IntSet(128);
        HALF_VOXELS = new IntSet(64);
        BASE_VOXELS = new IntSet(64);
        HALF_COVERS = new IntSet(16);
        BASE_COVERS = new IntSet(16);
        DECORATIONS = new IntSet(32);
        HALF_DECORATIONS = new IntSet(4);
        UNIT_ANY = new IntSet(64);
        HALF_ANY = new IntSet(64);
        BASE_ANY = new IntSet(64);

        UNIT_VOXELS.addAll(TILES.values().toArray());

        for(ObjectIntMap.Entry<String> e : TILES.entries()){
            if(e.key.contains("half")){
                if(e.key.contains("cover")) HALF_COVERS.add(e.value);
                else if(!e.key.contains("deco")) HALF_VOXELS.add(e.value);
                UNIT_VOXELS.remove(e.value);
            }
            if(e.key.contains("base")){
                if(e.key.contains("cover")) BASE_COVERS.add(e.value);
                else BASE_VOXELS.add(e.value);
                UNIT_VOXELS.remove(e.value);
            }
            if(e.key.contains("deco")){
                if(e.key.contains("half")) HALF_DECORATIONS.add(e.value);
                else DECORATIONS.add(e.value);
                UNIT_VOXELS.remove(e.value);
            }
        }
        UNIT_ANY.addAll(UNIT_VOXELS);
        UNIT_ANY.addAll(DECORATIONS);
        HALF_ANY.addAll(HALF_VOXELS);
        HALF_ANY.addAll(HALF_COVERS);
        HALF_ANY.addAll(HALF_DECORATIONS);
        BASE_ANY.addAll(BASE_VOXELS);
        BASE_ANY.addAll(BASE_COVERS);
    }

    public static final IntArray UNIT_VOXELS_ARRAY = UNIT_VOXELS.iterator().toArray();
    public static final IntArray HALF_VOXELS_ARRAY = HALF_VOXELS.iterator().toArray();
    public static final IntArray BASE_VOXELS_ARRAY = BASE_VOXELS.iterator().toArray();
    public static final IntArray HALF_COVERS_ARRAY = HALF_COVERS.iterator().toArray();
    public static final IntArray BASE_COVERS_ARRAY = BASE_COVERS.iterator().toArray();
    public static final IntArray DECORATIONS_ARRAY = DECORATIONS.iterator().toArray();
    public static final IntArray HALF_DECORATIONS_ARRAY = HALF_DECORATIONS.iterator().toArray();
    public static final IntArray UNIT_ANY_ARRAY = UNIT_ANY.iterator().toArray();
    public static final IntArray HALF_ANY_ARRAY = HALF_ANY.iterator().toArray();
    public static final IntArray BASE_ANY_ARRAY = BASE_ANY.iterator().toArray();

    public static boolean isGrass(int index) {
        return index == GRASS || index == DIRT ||
            (index >= PATH_GRASS_GR && index <= PATH_GRASS_FR);
    }
    public static boolean isHalfGrass(int index) {
        return index == HALF_GRASS || index == HALF_DIRT ||
            (index >= HALF_PATH_GRASS_GR && index <= HALF_PATH_GRASS_FR);
    }
    public static boolean isDry(int index) {
        return index == DRY || index == SAND ||
            (index >= PATH_DRY_GR && index <= PATH_DRY_FR);
    }
    public static boolean isHalfDry(int index) {
        return index == HALF_DRY || index == HALF_SAND ||
            (index >= HALF_PATH_DRY_GR && index <= HALF_PATH_DRY_FR);
    }
    public static boolean isGrassPath(int index) {
        return (index >= PATH_GRASS_GR && index <= PATH_GRASS_FR);
    }
    public static boolean isHalfGrassPath(int index) {
        return (index >= HALF_PATH_GRASS_GR && index <= HALF_PATH_GRASS_FR);
    }
    public static boolean isDryPath(int index) {
        return (index >= PATH_DRY_GR && index <= PATH_DRY_FR);
    }
    public static boolean isHalfDryPath(int index) {
        return (index >= HALF_PATH_DRY_GR && index <= HALF_PATH_DRY_FR);
    }

    /**
     * Meant to be called on a map where paths have been placed but don't necessarily line up or connect fully. If this
     * can return a path or path-like (walkable) tile, it does that, so it should only be called where you want a path
     * to form.
     * @param centerIndex the tile index of the tile you could turn into a path or connect properly to other paths
     * @param adjacentF the tile index of the tile in the F direction (negative F, towards the front and left)
     * @param adjacentG the tile index of the tile in the G direction (negative G, towards the front and right)
     * @param adjacentT the tile index of the tile in the T direction (positive F, towards the back and right)
     * @param adjacentR the tile index of the tile in the R direction (positive G, towards the back and left)
     * @return the tile index to use instead of {@code centerIndex} to make it become a path
     */
    public static int getPathIndex(int centerIndex, int adjacentF, int adjacentG, int adjacentT, int adjacentR) {
        int bits = 0;

        if(centerIndex == LAVA || centerIndex == BASALT) centerIndex = BASALT;
        else if(centerIndex == HALF_LAVA || centerIndex == HALF_BASALT) centerIndex = HALF_BASALT;
        else if(centerIndex == ICE || centerIndex == SNOW) centerIndex = SNOW;
        else if(centerIndex == HALF_ICE || centerIndex == HALF_SNOW) centerIndex = HALF_SNOW;
        else if(centerIndex >= HALF_COVER_WATER_FGH && centerIndex <= HALF_COVER_WATER_H) centerIndex = DRY;
        else if(centerIndex >= BASE_COVER_WATER_FGH && centerIndex <= BASE_COVER_WATER_H) centerIndex = HALF_DRY;
        else if(centerIndex >= HALF_COVER_SWAMP_FGH && centerIndex <= HALF_COVER_SWAMP_H) centerIndex = GRASS;
        else if(centerIndex >= BASE_COVER_SWAMP_FGH && centerIndex <= BASE_COVER_SWAMP_H) centerIndex = HALF_GRASS;

        if(isGrass(centerIndex)) {
            if(isGrassPath(adjacentF)) bits |= 1;
            if(isGrassPath(adjacentG)) bits |= 2;
            if(isGrassPath(adjacentT)) bits |= 4;
            if(isGrassPath(adjacentR)) bits |= 8;
            centerIndex = PATHS.get(bits, PATH_GRASS_GR);
        } else if(isHalfGrass(centerIndex)) {
            if(isHalfGrassPath(adjacentF)) bits |= 1;
            if(isHalfGrassPath(adjacentG)) bits |= 2;
            if(isHalfGrassPath(adjacentT)) bits |= 4;
            if(isHalfGrassPath(adjacentR)) bits |= 8;
            centerIndex = PATHS.get(bits, PATH_GRASS_GR) + 11;
        } else if(isDry(centerIndex)) {
            if(isDryPath(adjacentF)) bits |= 1;
            if(isDryPath(adjacentG)) bits |= 2;
            if(isDryPath(adjacentT)) bits |= 4;
            if(isDryPath(adjacentR)) bits |= 8;
            centerIndex = PATHS.get(bits, PATH_GRASS_GR) + 22;
        } else if(isHalfDry(centerIndex)) {
            if(isHalfDryPath(adjacentF)) bits |= 1;
            if(isHalfDryPath(adjacentG)) bits |= 2;
            if(isHalfDryPath(adjacentT)) bits |= 4;
            if(isHalfDryPath(adjacentR)) bits |= 8;
            centerIndex = PATHS.get(bits, PATH_GRASS_GR) + 33;
        }
        return centerIndex;
    }

    /**
     * Given a 3D int array indexed in {@code [f][g][h]} order, this takes existing paths that may have become unaligned
     * and resets their connections so they link up to each other. This also takes into account neighboring paths with
     * up to one tile difference on h, higher or lower.
     * @param area a 3D int array indexed in {@code [f][g][h]} order that will be modified in-place
     * @return {@code area}, potentially after modifications, for chaining
     */
    public static LocalMap realignPaths(LocalMap area) {
        final int fSize = area.getFSize(), gSize = area.getGSize(), hSize = area.getHSize();
        final int fLimit = fSize - 1, gLimit = gSize - 1, hLimit = hSize - 1;
        final int[][][] tiles = area.tiles;
        for (int f = 0; f < fSize; f++) {
            for (int g = 0; g < gSize; g++) {
                for (int h = hLimit; h >= 0; h--) {
                    int t = tiles[f][g][h];
                    if(t == -1) continue;
                    int bits = 0;
                    if(isGrassPath(t)) {
                        if(f == 0 || isGrassPath(tiles[f-1][g][h]) || (h > 0 && isGrassPath(tiles[f-1][g][h-1])) || (h < hLimit && isGrassPath(tiles[f-1][g][h+1]))) bits |= 1;
                        if(g == 0 || isGrassPath(tiles[f][g-1][h]) || (h > 0 && isGrassPath(tiles[f][g-1][h-1])) || (h < hLimit && isGrassPath(tiles[f][g-1][h+1]))) bits |= 2;
                        if(f == fLimit || isGrassPath(tiles[f+1][g][h]) || (h > 0 && isGrassPath(tiles[f+1][g][h-1])) || (h < hLimit && isGrassPath(tiles[f+1][g][h+1]))) bits |= 4;
                        if(g == gLimit || isGrassPath(tiles[f][g+1][h]) || (h > 0 && isGrassPath(tiles[f][g+1][h-1])) || (h < hLimit && isGrassPath(tiles[f][g+1][h+1]))) bits |= 8;
                        area.setTile(f, g, h, PATHS.get(bits, PATH_GRASS_GR));
                    } else if(isHalfGrassPath(t)) {
                        if(f == 0 || isHalfGrassPath(tiles[f-1][g][h]) || (h > 0 && isHalfGrassPath(tiles[f-1][g][h-1])) || (h < hLimit && isHalfGrassPath(tiles[f-1][g][h+1]))) bits |= 1;
                        if(g == 0 || isHalfGrassPath(tiles[f][g-1][h]) || (h > 0 && isHalfGrassPath(tiles[f][g-1][h-1])) || (h < hLimit && isHalfGrassPath(tiles[f][g-1][h+1]))) bits |= 2;
                        if(f == fLimit || isHalfGrassPath(tiles[f+1][g][h]) || (h > 0 && isHalfGrassPath(tiles[f+1][g][h-1])) || (h < hLimit && isHalfGrassPath(tiles[f+1][g][h+1]))) bits |= 4;
                        if(g == gLimit || isHalfGrassPath(tiles[f][g+1][h]) || (h > 0 && isHalfGrassPath(tiles[f][g+1][h-1])) || (h < hLimit && isHalfGrassPath(tiles[f][g+1][h+1]))) bits |= 8;
                        area.setTile(f, g, h, PATHS.get(bits, PATH_GRASS_GR) + 11);
                    } else if(isDryPath(t)) {
                        if(f == 0 || isDryPath(tiles[f-1][g][h]) || (h > 0 && isDryPath(tiles[f-1][g][h-1])) || (h < hLimit && isDryPath(tiles[f-1][g][h+1]))) bits |= 1;
                        if(g == 0 || isDryPath(tiles[f][g-1][h]) || (h > 0 && isDryPath(tiles[f][g-1][h-1])) || (h < hLimit && isDryPath(tiles[f][g-1][h+1]))) bits |= 2;
                        if(f == fLimit || isDryPath(tiles[f+1][g][h]) || (h > 0 && isDryPath(tiles[f+1][g][h-1])) || (h < hLimit && isDryPath(tiles[f+1][g][h+1]))) bits |= 4;
                        if(g == gLimit || isDryPath(tiles[f][g+1][h]) || (h > 0 && isDryPath(tiles[f][g+1][h-1])) || (h < hLimit && isDryPath(tiles[f][g+1][h+1]))) bits |= 8;
                        area.setTile(f, g, h, PATHS.get(bits, PATH_GRASS_GR) + 22);
                    } else if(isHalfDryPath(t)) {
                        if(f == 0 || isHalfDryPath(tiles[f-1][g][h]) || (h > 0 && isHalfDryPath(tiles[f-1][g][h-1])) || (h < hLimit && isHalfDryPath(tiles[f-1][g][h+1]))) bits |= 1;
                        if(g == 0 || isHalfDryPath(tiles[f][g-1][h]) || (h > 0 && isHalfDryPath(tiles[f][g-1][h-1])) || (h < hLimit && isHalfDryPath(tiles[f][g-1][h+1]))) bits |= 2;
                        if(f == fLimit || isHalfDryPath(tiles[f+1][g][h]) || (h > 0 && isHalfDryPath(tiles[f+1][g][h-1])) || (h < hLimit && isHalfDryPath(tiles[f+1][g][h+1]))) bits |= 4;
                        if(g == gLimit || isHalfDryPath(tiles[f][g+1][h]) || (h > 0 && isHalfDryPath(tiles[f][g+1][h-1])) || (h < hLimit && isHalfDryPath(tiles[f][g+1][h+1]))) bits |= 8;
                        area.setTile(f, g, h, PATHS.get(bits, PATH_GRASS_GR) + 33);
                    }

                    break;
                }
            }
        }
        return area;
    }

}
""",
    )
    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage + ".game",
      fileName = "Mover.java",
      content =
        """package ${project.basic.rootPackage}.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import ${project.basic.rootPackage}.AnimatedIsoSprite;
import ${project.basic.rootPackage}.${project.basic.mainClass};
import ${project.basic.rootPackage}.LocalMap;
import ${project.basic.rootPackage}.util.HasPosition3D;
import ${project.basic.rootPackage}.util.MiniNoise;

/**
 * A creature, hero, or hazard that can move around of its own accord.
 * A Mover can be an {@link #npc} or not; NPCs move on their own in somewhat-random paths, while the player character
 * should be the only Mover with {@code npc = false}, and should be moved by the player's input.
 */
public class Mover implements HasPosition3D {
    /**
     * The depth modifier used by the player, so they can't remove terrain voxels by overlapping them.
     */
    public static final float PLAYER_W = (1f/8f);
    /**
     * The depth modifier used by all moving NPCs; this is the same as {@link #PLAYER_W}.
     */
    public static final float NPC_W = PLAYER_W;
    /**
     * The depth modifier used by goldfish, which is slightly different from the player or NPC depth modifiers so
     * goldfish can't get removed by an overlapping NPC, and so an overlapping player doesn't remove the goldfish before
     * its rescue can be processed.
     */
    public static final float FISH_W = PLAYER_W + (1f/1024f);
    /**
     * Can be retrieved with {@link #getPosition()}, which satisfies our interface requirement.
     */
    private final Vector3 position = new Vector3();
    /**
     * The direction this Mover is... moving in. Carries over between frames.
     */
    public final Vector3 velocity = new Vector3(0, 0, 0);
    /**
     * Used to make queries to {@link LocalMap#everything}, when we don't know if a position has anything at it.
     */
    private final Vector4 tempVectorA = new Vector4();
    /**
     * While "grounded" this Mover is stepping on the ground, preventing it from falling and allowing it to jump.
     */
    public boolean isGrounded;
    /**
     * The unique identifier for this Mover; this is usually a sequential int starting at 1, where 1 is the player only.
     * {@link #ID_COUNTER} is usually used to determine this, and reset when the map changes.
     */
    public final int id;

    /**
     * If true, this Mover will move on its own; if false, it depends on player input to move.
     */
    public final boolean npc;

    /**
     * Each Mover knows what map it is on, and uses this to check for collisions with terrain and other Movers.
     * This is "transient" to avoid any infinite loops while saving or loading a Mover to file.
     */
    private transient LocalMap map;

    /**
     * Typically calculated at game start in the main game class, this is an Array of four groups of sprite Animations,
     * with each group referring to a different facing ({@link #currentDirection}) and whether it is attacking.
     * <br>
     * This will likely need to change if the assets change.
     */
    private final Array<Array<Animation<TextureAtlas.AtlasSprite>>> animations;
    /**
     * The index into the inner Arrays of {@link #animations}, determining which creature this appears as.
     * <br>
     * This may need to change if the assets change.
     */
    public final int animationIndex;
    /**
     * Goes up until it reaches a threshold, then physics steps run a number of times based on how much the accumulator
     * went past that threshold. A common concept in game physics.
     */
    private transient float accumulator;
    /**
     * The amount of time, in seconds, this Mover has been on-screen and able to move. Used for both the invincibility
     * flash for the player when damaged, and to determine the meandering path of NPCs.
     */
    private transient float totalMoveTime = 0f;
    /**
     * If the player is invincible due to just taking damage, this timestamp (in seconds) will be greater than
     * {@link #totalMoveTime}. The player is also invincible when they first spawn.
     */
    private transient float invincibilityEndTime = -100f;
    /**
     * An int denoting the current facing direction of the sprite and whether it is attacking. 0 and 1 are facing down
     * and up, respectively, without attacking, and 2 and 3 are down and up, respectively, while attacking.
     * <br>
     * This will likely need to change if the assets change.
     */
    private int currentDirection;

    /**
     * This only matters for the player currently, but it determines how many times they can take damage.
     */
    public int health = 3;

    /**
     * Added to velocity on the h-axis (or z-axis) when a Mover is in the air.
     * <br>
     * These constants are hard to adjust, but can be changed to fit your game, very carefully.
     */
    private static final float GRAVITY = -0.04f;
    /**
     * The terminal velocity a falling Mover can reach on the h-axis due to gravity.
     * <br>
     * These constants are hard to adjust, but can be changed to fit your game, very carefully.
     */
    private static final float MAX_GRAVITY = -0.3f;
    /**
     * When a Mover jumps, their h-axis velocity is set to this immediately.
     */
    private static final float JUMP_FORCE = 0.6f;
    /**
     * May be adjusted to make the player character move faster or slower.
     */
    public static final float PC_MOVE_SPEED = 0.15f;
    /**
     * May be adjusted to make all NPCs move faster or slower.
     */
    public static final float NPC_MOVE_SPEED = 0.07f;

    /**
     * Goes up every time an {@link #id} needs to be assigned, and is reset to 0 if the map resets.
     */
    public static int ID_COUNTER = 1;

    /**
     * The animated sprite used as the visual representation of this Mover; also has an isometric tile position.
     */
    public AnimatedIsoSprite visual;

    /**
     * Creates a Mover in the given LocalMap, drawing an index from animations, and using the given f,g,h position.
     * @param map a LocalMap that this Mover exists in
     * @param animations typically created in the main game class from a TextureAtlas
     * @param index the index into the inner Array of animations
     * @param fPos isometric tile f-coordinate
     * @param gPos isometric tile g-coordinate
     * @param hPos isometric tile h-coordinate
     */
    public Mover(LocalMap map, Array<Array<Animation<TextureAtlas.AtlasSprite>>> animations, int index,
                 float fPos, float gPos, float hPos) {
        this.map = map;
        this.position.set(fPos, gPos, hPos);
        this.accumulator = 0;
        this.currentDirection = 0; // Default: facing down
        this.animationIndex = index;

        this.animations = animations;

        visual = new AnimatedIsoSprite(animations.get(currentDirection).get(index), fPos, gPos, hPos);
        id = ID_COUNTER++;
        npc = id > 1;
        if(!npc) invincibilityEndTime = totalMoveTime + 2f;
    }

    /**
     * Updates this Mover's movement, physics, appearance, and for NPCs, "AI" as much as it can be called that.
     * <br>
     * Chasing NPCs would require some additional dependency, such as gdx-ai (which is hard to use for pathfinding),
     * simple-graphs (which is definitely simpler to use), or Gand (which is very close to simple-graphs with some extra
     * features added and a key feature removed). This project only depends on libGDX by default.
     * Alternatively, you could just average the direction to the player and the random direction this already gets for
     * NPC movement, and NPCs would "lurch" in the rough direction of the player.
     * @param deltaTime the amount of time in seconds since the last update
     */
    public void update(float deltaTime) {
        totalMoveTime += deltaTime;
        // NPCs move in meandering, lazily-changing paths with no rhyme or reason.
        // If you include a pathfinding library as a dependency, then you can use that to make NPCs chase the PC.
        if(npc){
            // Gets 1D noise (as a wiggly line, essentially) for distance to move on the f axis...
            float df = MiniNoise.PerlueNoise.instance.getNoiseWithSeed(totalMoveTime * 1.7548f, id);
            // and on the g axis, as a different wiggly line. These values are always between -1 and 1.
            float dg = MiniNoise.PerlueNoise.instance.getNoiseWithSeed(totalMoveTime * 1.5698f, ~id);
            float c = map.cosRotation;
            float s = map.sinRotation;
            float rf = c * df + s * dg;
            float rg = c * dg - s * df;

            if(isGrounded && df * df + dg * dg > 0.5f) jump();
            move(rf, rg, NPC_MOVE_SPEED);
        }
        accumulator += deltaTime;
        while (accumulator > (1f/60f)) {
            accumulator -= (1f / 60f);
            tempVectorA.set(position, PLAYER_W);

            applyGravity();
            handleCollision();
            position.add(velocity);

            // while jumping, show attack animation; while standing, show idle animation. NPCs are always attacking.
            if (npc || velocity.z != 0) {
                /* The "currentDirection + 2" gets an attack animation instead of an idle one for the appropriate facing. */
                visual.animation = animations.get(currentDirection + 2).get(animationIndex);
            } else {
                visual.animation = animations.get(currentDirection).get(animationIndex);
            }

            visual.setPosition(position);
            map.everything.remove(tempVectorA);
            map.everything.put(tempVectorA.set(position, PLAYER_W), visual);
            // uses not greater than or equal to so if invincibilityEndTime is NaN, the player will always be invincible
            // we set the player to be permanently invincible when they win.
            if(!(totalMoveTime >= invincibilityEndTime))
                visual.sprite.setAlpha(Math.min(Math.max(MathUtils.sin(totalMoveTime * 100f) * 0.75f + 0.5f, 0f), 1f));
            else
                visual.sprite.setAlpha(1f);
        }
    }

    /**
     * If this Mover is midair, makes their velocity go more negative unless it has already reached terminal velocity.
     */
    private void applyGravity() {
        if (!isGrounded) {
            velocity.z = Math.max(velocity.z + GRAVITY, MAX_GRAVITY); // Apply gravity to H axis (z in a Vector)
        }
    }

    /**
     * If this Mover is on the ground, makes their velocity suddenly spike upward, and makes them no longer grounded.
     */
    public void jump() {
        if (isGrounded) {
            velocity.z = JUMP_FORCE; // Jump should affect H axis (heel to head, stored as z in a Vector)
            isGrounded = false;
        }
    }

    /**
     * Mostly meant for the player at this point, this checks if the player is currently invincible, and if they aren't,
     * their health goes down by one, and they become invincible for two seconds. This also updates the health label
     * with {@link ${project.basic.mainClass}#updateHealth()}.
     */
    public void takeDamage() {
        // uses not greater than or equal to so if invincibilityEndTime is NaN, the player will always be invincible
        if(!(totalMoveTime >= invincibilityEndTime)) return;
        health--;
        if(health <= 0) {
            if(npc) map.movers.entities.removeValue(this, true);
        } else {
            invincibilityEndTime = totalMoveTime + 2f;
        }
        if(!npc) ((${project.basic.mainClass}) Gdx.app.getApplicationListener()).updateHealth();
    }

    /**
     * Makes this mover invincible for the given time in seconds. If duration is {@link Float#NaN}, the invincibility
     * will be permanent.
     * @param duration in seconds; may be {@link Float#NaN} to make invincibility permanent
     */
    public void makeInvincible(float duration) {
        invincibilityEndTime = totalMoveTime + duration;
    }

    /**
     * Changes the velocity of this Mover in the given direction (as df and dg) at the given speed (which is a constant
     * rate usually). The velocity is considered by {@link #update(float)}, which allows part of it to move the player
     * based on delta time for that physics update.
     * @param df the direction on the isometric tile f-axis
     * @param dg the direction on the isometric tile g-axis
     * @param speed how fast the movement should be
     */
    public void move(float df, float dg, float speed) {
        boolean movingDiagonally = (df != 0 && dg != 0);

        if (movingDiagonally) {
            // Normalize to maintain consistent movement speed
            float length = 1f / (float) Math.sqrt(df * df + dg * dg);
            df *= length;
            dg *= length;
        }

        velocity.x = df * speed;
        velocity.y = dg * speed;

        if (df == 0 && dg == 0) return;

        // Determine direction based on movement
        if (MathUtils.cosDeg(-45f - map.rotationDegrees) * dg - MathUtils.sinDeg(-45f - map.rotationDegrees) * df > 0.1f) currentDirection = 1; // Up
        else currentDirection = 0; // Down
    }

    /**
     * Every {@link #update(float)}, this checks for collisions before finally moving a Mover to its next location.
     * If a collision occurs, the movement is refused. If a collision occurs between movers and this is the player, then
     * the player takes damage.
     */
    private void handleCollision() {
        // bottom of map
        final float groundLevel = 1f;
        // a mover can't move below the lowest tiles.
        if (position.z < groundLevel) {
            position.z = groundLevel;
            velocity.z = 0;
            isGrounded = true;
        }

        // If there was an earlier collision, it shouldn't affect the current frame.
        map.movers.colliding.clear();
        boolean lateralCollision = false;

        // these blocks are, sadly, mostly-repeated code.
        // Each block defines lo and hi differently, and checks them against different axes.

        // tile collision from the side, one axis
        if (velocity.x >= 0 &&
            (!map.isValid(position.x + 1, position.y, position.z) ||
                map.getTile(position.x + 1, position.y, position.z) != -1 ||
                map.checkCollision(this).notEmpty())) {
            int lo = MathUtils.round(position.x);
            int hi = MathUtils.round(position.x + 1);

            if (position.x >= lo && position.x <= hi) {
                position.x = lo;
                velocity.x = 0;
                lateralCollision = true;
            }
            if(!npc && map.movers.colliding.notEmpty()) {
                takeDamage();
                map.movers.colliding.clear();
            }
        }
        if (velocity.y >= 0 &&
            (!map.isValid(position.x, position.y + 1, position.z) ||
                map.getTile(position.x, position.y + 1, position.z) != -1 ||
                map.checkCollision(this).notEmpty())) {
            int lo = MathUtils.round(position.y    );
            int hi = MathUtils.round(position.y + 1);

            if (position.y >= lo && position.y <= hi) {
                position.y = lo;
                velocity.y = 0;
                lateralCollision = true;
            }
            if(!npc && map.movers.colliding.notEmpty()) {
                takeDamage();
                map.movers.colliding.clear();
            }
        }
        if (velocity.x <= 0 &&
            (!map.isValid(position.x - 1, position.y, position.z) ||
            map.getTile(position.x - 1, position.y, position.z) != -1 ||
                map.checkCollision(this).notEmpty())) {
            int lo = MathUtils.round(position.x - 1);
            int hi = MathUtils.round(position.x    );

            if (position.x >= lo && position.x <= hi) {
                position.x = hi;
                velocity.x = 0;
                lateralCollision = true;
            }
            if(!npc && map.movers.colliding.notEmpty()) {
                takeDamage();
                map.movers.colliding.clear();
            }
        }
        if (velocity.y <= 0 &&
            (!map.isValid(position.x, position.y - 1, position.z) ||
                map.getTile(position.x, position.y - 1, position.z) != -1 ||
                map.checkCollision(this).notEmpty())) {
            int lo = MathUtils.round(position.y - 1);
            int hi = MathUtils.round(position.y    );

            if (position.y >= lo && position.y <= hi) {
                position.y = hi;
                velocity.y = 0;
                lateralCollision = true;
            }
            if(!npc && map.movers.colliding.notEmpty()) {
                takeDamage();
                map.movers.colliding.clear();
            }
        }

        // these blocks define both loX and loY, and hiX/hiY, because they involve collisions on two axes.
        // x and y can be considered equivalent to f and g here, but because this code could be used for
        // non-isometric games, this uses x and y here.

        // tile collision from the side, two axes
        if (velocity.x > 0 && velocity.y > 0 &&
            (!map.isValid(position.x + 1, position.y + 1, position.z) ||
                map.getTile(position.x + 1, position.y + 1, position.z) != -1 ||
                map.checkCollision(this).notEmpty())) {
            int loX = MathUtils.round(position.x    );
            int hiX = MathUtils.round(position.x + 1);
            int loY = MathUtils.round(position.y    );
            int hiY = MathUtils.round(position.y + 1);

            if (position.x >= loX && position.x <= hiX && position.y >= loY && position.y <= hiY) {
                position.x = loX;
                position.y = loY;
                velocity.x = 0;
                velocity.y = 0;
                lateralCollision = true;
            }
            if(!npc && map.movers.colliding.notEmpty()) {
                takeDamage();
                map.movers.colliding.clear();
            }
        }
        if (velocity.x > 0 && velocity.y < 0 &&
            (!map.isValid(position.x + 1, position.y - 1, position.z) ||
                map.getTile(position.x + 1, position.y - 1, position.z) != -1 ||
                map.checkCollision(this).notEmpty())) {
            int loX = MathUtils.round(position.x    );
            int hiX = MathUtils.round(position.x + 1);
            int loY = MathUtils.round(position.y - 1);
            int hiY = MathUtils.round(position.y    );

            if (position.x >= loX && position.x <= hiX && position.y >= loY && position.y <= hiY) {
                position.x = loX;
                position.y = hiY;
                velocity.x = 0;
                velocity.y = 0;
                lateralCollision = true;
            }
            if(!npc && map.movers.colliding.notEmpty()) {
                takeDamage();
                map.movers.colliding.clear();
            }
        }
        if (velocity.x < 0 && velocity.y > 0 &&
            (!map.isValid(position.x - 1, position.y + 1, position.z) ||
                map.getTile(position.x - 1, position.y + 1, position.z) != -1 ||
                map.checkCollision(this).notEmpty())) {
            int loX = MathUtils.round(position.x - 1);
            int hiX = MathUtils.round(position.x    );
            int loY = MathUtils.round(position.y    );
            int hiY = MathUtils.round(position.y + 1);

            if (position.x >= loX && position.x <= hiX && position.y >= loY && position.y <= hiY) {
                position.x = hiX;
                position.y = loY;
                velocity.x = 0;
                velocity.y = 0;
                lateralCollision = true;
            }
            if(!npc && map.movers.colliding.notEmpty()) {
                takeDamage();
                map.movers.colliding.clear();
            }
        }
        if (velocity.x < 0 && velocity.y < 0 &&
            (!map.isValid(position.x - 1, position.y - 1, position.z) ||
                map.getTile(position.x - 1, position.y - 1, position.z) != -1 ||
                map.checkCollision(this).notEmpty())) {
            int loX = MathUtils.round(position.x - 1);
            int hiX = MathUtils.round(position.x    );
            int loY = MathUtils.round(position.y - 1);
            int hiY = MathUtils.round(position.y    );

            if (position.x >= loX && position.x <= hiX && position.y >= loY && position.y <= hiY) {
                position.x = hiX;
                position.y = hiY;
                velocity.x = 0;
                velocity.y = 0;
                lateralCollision = true;
            }
            if(!npc && map.movers.colliding.notEmpty()) {
                takeDamage();
                map.movers.colliding.clear();
            }
        }


        // Here, we look for any lower-elevation tile in the four possible tiles below the player.
        // If any are solid, and if the Mover is falling, we may stop them before they overlap the ground.
        if (   map.getTile(position.x - 0.5f, position.y - 0.5f, position.z - 1) != -1
            || map.getTile(position.x - 0.5f, position.y + 0.5f, position.z - 1) != -1
            || map.getTile(position.x + 0.5f, position.y - 0.5f, position.z - 1) != -1
            || map.getTile(position.x + 0.5f, position.y + 0.5f, position.z - 1) != -1
        ) {
            if (velocity.z < 0) {

                position.z = MathUtils.round(position.z);
                isGrounded = true;
                velocity.z = 0;
                // If we start to overlap with another tile, force a jump to avoid an unwanted collision.
                if(!lateralCollision) {
                    if     (map.getTile(position.x - 0.5f, position.y - 0.5f, position.z) != -1 ||
                            map.getTile(position.x - 0.5f, position.y + 0.5f, position.z) != -1 ||
                            map.getTile(position.x + 0.5f, position.y - 0.5f, position.z) != -1 ||
                            map.getTile(position.x + 0.5f, position.y + 0.5f, position.z) != -1) {
                        jump();
                    }
                }
            }
        } else {
            // If nothing is below the Mover in the 4 nearby cells below, they are falling.
            isGrounded = false;
        }
    }

    public LocalMap getMap() {
        return map;
    }

    public void setMap(LocalMap map) {
        this.map = map;
    }

    public int getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(int currentDirection) {
        this.currentDirection = currentDirection;
    }

    /**
     * Puts this Mover into {@link LocalMap#everything} at the given depth modifier, such as {@link Mover#PLAYER_W}.
     * If a Mover's {@link #position} changes any coordinates, this should be called when those changes are complete.
     * @param depth a depth modifier like {@link Mover#NPC_W} or {@link Mover#FISH_W}
     * @return this Mover, for chaining
     */
    public Mover place(float depth) {
        map.setEntity(position.x, position.y, position.z, depth, visual);
        return this;
    }

    public Vector3 getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Mover { visual: " +  visual + ", type: " + AssetData.ENTITIES.findKey(animationIndex) + " }";
    }
}
""",
    )
  }

  override fun getApplicationListenerContent(project: Project): String =
    """package ${project.basic.rootPackage};

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ${project.basic.rootPackage}.game.AssetData;
import ${project.basic.rootPackage}.game.Mover;

import java.util.Comparator;

import static ${project.basic.rootPackage}.util.MathSupport.INVERSE_ROOT_2;

/**
 * This is the primary starting point in the core module, and the only platform-specific code should be in "Launcher"
 * classes in other modules. This is an isometric pixel art demo project where the player (a little person in blue) runs
 * around trying to save goldfish (little orange fish out of water) without bumping into enemies (green-skinned, brawny
 * orcs). Bumping into enemies will take away your health, and reaching 0 health is a game-over condition. Saving all 10
 * goldfish is the win condition.
 * <br>
 * This uses a special kind of coordinates because isometric coordinates just don't correspond nicely to x, y,
 * and z with any common convention. Here, when referring to isometric tiles, we use "f, g, h" positions.The f and g
 * axes are diagonal, and correspond to movement at a shallow angle on both world x and world y. The h axis is used for
 * elevation, and corresponds to movement on world y. The mnemonics here work as if on a world map, with the origin
 * somewhere in Belgium or the Netherlands:
 * <ul>
 *     <li>The f axis is roughly the diagonal from France to Finland.</li>
 *     <li>The g axis is roughly the diagonal from Germany to Greenland (or Greece to Germany, or Greece to Greenland).</li>
 *     <li>The h axis is the vertical line from your heel to your head (or Hell to Heaven).</li>
 * </ul>
 * The letters "t" and "r" also show up, with the geography mnemonics "Tallinn" (in Estonia, near Finland) and Reykjavík
 * (in Iceland, on the way to Greenland); these refer specifically to the positive f direction and positive g direction,
 * or the back faces of a voxel, while f and g refer to the faces on the front. "fgtr" are the recommended keys on a
 * QWERTY keyboard to move in those directions, or on a map of Europe, the locations of France, Germany, Tallinn (in
 * Estonia), and Reykjavík (in Iceland) relative to Amsterdam in the center. The "fgtr" keys also are close in shape to
 * matching the X-shape of directions you can travel on one axis at a time in isometric coordinates, at least on a
 * QWERTY keyboard.
 * <br>
 * When a Vector3 is used for an isometric position, x,y,z refer to f,g,h. This code also sometimes uses Vector4 when
 * camera distance needs to vary; in that case, the w coordinate refers to camera depth. While movement on the f and g
 * axes moves an object on both screen x and y, movement on the h axis only moves an object on screen y, and movement on
 * depth only changes its sort order (potentially rendering it before or after other objects, and sometimes covering it
 * up entirely). A major goal of this demo is to show how sort order works using only a SpriteBatch to draw things with
 * 3D (or really, 2.5D) positions, using only 2D sprites.
 */
public class ${project.basic.mainClass} extends ApplicationAdapter {
    /**
     * Used to draw things from back to front as 2D sprites. Even though our tiles use 3D positions. Well, 4D. Don't run
     * away, I explained all of this in the class comment!
     */
    private SpriteBatch batch;
    /**
     * This is the file name of the atlas of 2D assets used in the game. It uses
     * <a href="https://gvituri.itch.io/isometric-trpg">these free-to-use assets by Gustavo Vituri</a> and
     * <a href="https://ray3k.wordpress.com/clean-crispy-ui-skin-for-libgdx/">a mangled, pixelated skin originally by Raymond Buckley</a>.
     * <br>
     * CUSTOM TO YOUR GAME. This is closely related to {@link AssetData}, and if one changes, both should.
     */
    public static final String ATLAS_FILE_NAME = "isometric-trpg.atlas";
    /**
     * This is the actual TextureAtlas of 2D assets used in the game. It uses
     * <a href="https://gvituri.itch.io/isometric-trpg">these free-to-use assets by Gustavo Vituri</a> and
     * <a href="https://ray3k.wordpress.com/clean-crispy-ui-skin-for-libgdx/">a mangled, pixelated skin originally by Raymond Buckley</a>.
     * <br>
     * CUSTOM TO YOUR GAME. This is closely related to {@link AssetData}, and if one changes, both should.
     */
    private TextureAtlas atlas;
    /**
     * Animations taken from {@link #atlas} to be loaded in a more game-runtime-friendly format.
     * Index 0 is front-facing idle animations.
     * Index 1 is rear-facing idle animations.
     * Index 2 is front-facing attack animations.
     * Index 3 is rear-facing attack animations.
     * Inside each of those four Arrays, there is an Array of many Animations of AtlasSprites, with each Animation for a
     * different type of monster or character.
     * <br>
     * CUSTOM TO YOUR GAME.
     */
    private Array<Array<Animation<TextureAtlas.AtlasSprite>>> animations;
    /**
     * Can be changed to make the game harder with more fish to save, or easier with fewer.
     */
    public static int FISH_COUNT = 10;
    /**
     * Can be changed to make the game harder with more enemies, or easier with fewer.
     */
    public static int ENEMY_COUNT = 10;
    /**
     * Used frequently here, this is the current location map that gameplay takes place in, which also stores the
     * inhabitants of that level.
     * <br>
     * CUSTOM TO YOUR GAME.
     */
    private LocalMap map;
    /**
     * The camera we use to show things with an isometric, or for sticklers, "dimetric" camera projection.
     * We can't use a PerspectiveCamera here, even with sort-of 3D positions, because it wouldn't be pixel-perfect.
     */
    private OrthographicCamera camera;
    /**
     * ScreenViewport is used here with a simple fraction for its {@link ScreenViewport#setUnitsPerPixel(float)}. Using
     * 1f, 1f/2f, 1f/3f, 1f/4f, etc. will ensure pixels stay all square consistently, and don't form ugly artifacts.
     */
    private ScreenViewport viewport;
    /**
     * Mover represents any moving creature or hazard, and can be a player character or non-player character (NPC).
     * This is the player, which has {@code npc = false;} and so won't move on their own.
     */
    private Mover player;
    /**
     * The enemies are stored in a simple Array. There aren't ever so many of them that the data structure could matter.
     * There's currently no logic for an enemy receiving damage; the player just tries to avoid the orc enemies.
     */
    private Array<Mover> enemies;
    /**
     * Shows current frames per second on the screen; you can remove this in production.
     */
    private Label fpsLabel;
    /**
     * Shows how many goldfish you need to rescue, if you have "won" by saving all goldfish, or if you have "died" by
     * taking 3 separate hits (you are hit by touching an enemy, which is always a green orc here).
     * <br>
     * CUSTOM TO YOUR GAME.
     */
    public Label goalLabel;
    /**
     * Only shows the current health of the player, using {@code "♥ "} for each point of health.
     * <br>
     * CUSTOM TO YOUR GAME.
     */
    public Label healthLabel;
    /**
     * This currently plays a public domain song by Komiku, "Road 4 Fight", the entire time.
     * I hope it isn't too annoying to be playing on loop...
     * <br>
     * CUSTOM TO YOUR GAME.
     */
    public Music backgroundMusic;

    /**
     * This is the limit the current FPS will max out at unless the player un-limits FPS by pressing 'C'.
     * With simple pixel art graphics, a higher frame rate is likely not noticeable, though input might be affected.
     */
    public static final int FRAME_RATE_LIMIT = 60;

    /**
     * Currently, pressing 'c' will toggle the frame rate cap, so you can see if any physics changes are still
     * frame-rate-independent.
     */
    private int cap = FRAME_RATE_LIMIT;
    /**
     * The base width and height in tiles of a map; this may vary slightly when the map is created, for variety.
     * The variance only goes up by 0 to 3 width and height (by the same amount).
     */
    public static final int MAP_SIZE = 40;
    /**
     * The maximum number of voxels and creatures that can be stacked on top of each other in the map; typically also
     * includes some room to jump higher.
     */
    public static final int MAP_PEAK = 10;
    /**
     * The computed width in pixels of a full map at its largest possible {@link #MAP_SIZE}.
     */
    public static final int SCREEN_HORIZONTAL = (MAP_SIZE+3) * 2 * AssetData.TILE_WIDTH;
    /**
     * The computed height in pixels of a full map at its largest possible {@link #MAP_SIZE} and {@link #MAP_PEAK}.
     */
    public static final int SCREEN_VERTICAL = (MAP_SIZE+3) * 2 * AssetData.TILE_HEIGHT + MAP_PEAK * AssetData.TILE_DEPTH;
    /**
     * The position in fractional tiles of the very center of the map, measured from bottom center.
     */
    public float mapCenter = (MAP_SIZE - 1f) * 0.5f;

    /**
     * Can be changed to any fraction that is {@code 1.0f} divided by any integer greater than 0, which makes the screen
     * zoom to double size if this is {@code 1.0f / 2}, or triple size if this is {@code 1.0f / 3}, and so on.
     */
    public float CAMERA_ZOOM = 1f;
    /**
     * In milliseconds, the time since the map was generated or regenerated.
     */
    public long startTime;
    /**
     * In milliseconds, the time at which any multi-frame animation started (usually a map rotation).
     */
    public long animationStart = -1000000L;

    /**
     * A temporary Vector3 used to store either pixel or world positions being projected or unprojected in 3D.
     */
    private static final Vector3 projectionTempVector = new Vector3();
    /**
     * A temporary Vector3 used to store tile positions (which are world positions on the diagonal grid) in 3D.
     */
    private static final Vector3 isoTempVector = new Vector3();
    /**
     * A temporary Vector2 used to store screen positions in pixels.
     */
    private static final Vector2 screenTempVector = new Vector2();
    /**
     * A temporary Vector4 used to store positions in {@link LocalMap#everything}, which stores every object and Mover
     * so they can be sorted correctly and then displayed in that order. This Vector4 is commonly set to some values
     * that should be checked if they exist in "everything", such as with {@link OrderedMap#containsKey(Object)}.
     * The x, y, and z coordinates correspond directly to a tile's isometric f, g, and h coordinates, while the
     * Vector4's w coordinate corresponds to the depth modifier for any sprite at that f,g,h position. A position with
     * the same x,y,z position (or f,g,h) but a different w (or depth modifier) is treated as different in "everything"
     * and this allows more than one sprite to share a position. This is how the outlines on terrain are handled, and
     * how goldfish can briefly occupy the same area as the player or an enemy.
     */
    private static final Vector4 tempVector4 = new Vector4();

    /**
     * Used to depth-sort isometric points, including if the map is mid-rotation. This gets the center of the LocalMap
     * directly from its size, and permits {@link LocalMap#rotationDegrees} to be any finite value in degrees.
     * It uses the map's pre-calculated {@link LocalMap#cosRotation} and sinRotation instead of needing to repeatedly
     * call cos() and sin(). The isometric points here are Vector4, but for the most part, only the x, y,
     * and z components are used. The fourth component, w, is only used to create another point at the same x, y, z
     * location but with a different depth. The depth change is currently used to draw outlines behind terrain tiles,
     * but have them be overdrawn by other terrain tiles if nearby. The outlines only appear if there is empty space
     * behind a terrain tile.
     */
    public final Comparator<? super Vector4> comparator =
        (a, b) -> Float.compare(
            IsoSprite.viewDistance(a.x, a.y, a.z, map.fCenter, map.gCenter, map.cosRotation, map.sinRotation) + a.w,
            IsoSprite.viewDistance(b.x, b.y, b.z, map.fCenter, map.gCenter, map.cosRotation, map.sinRotation) + b.w);

    // You can use this block of code instead; it may perform better if framerate is an issue in practice, but it isn't
    // quite as clear to read. Internally, this uses {@link NumberUtils#floatToIntBits(float)} instead of
    // {@link Float#compare(float, float)} because it still returns a completely valid comparison value (it only
    // distinguishes between an int that is positive, negative, or zero), and seems a tiny bit faster. To avoid
    // {@code -0.0f} being treated as a negative comparison value, this adds {@code 0.0f} to the difference of the two
    // compared depths. This is absolutely a magic trick, and the whole thing is probably unnecessary and gratuitous!
//    public final Comparator<? super Vector4> comparator =
//        (a, b) -> NumberUtils.floatToIntBits(
//            IsoSprite.viewDistance(a.x, a.y, a.z, map.fCenter, map.gCenter, map.cosRotation, map.sinRotation) + a.w -
//                IsoSprite.viewDistance(b.x, b.y, b.z, map.fCenter, map.gCenter, map.cosRotation, map.sinRotation) - b.w + 0.0f);

    @Override
    public void create() {
        // You can use LOG_INFO in development, but change this to LOG_ERROR or LOG_NONE when releasing anything.
//        Gdx.app.setLogLevel(Application.LOG_INFO);
        Gdx.app.setLogLevel(Application.LOG_ERROR);

        // Create and play looping background music.
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Komiku - Road 4 Fight.ogg"));
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // We draw everything as 2D graphics with a carefully determined sort order.
        batch = new SpriteBatch();

        // Loads the atlas from an internal path, in "assets/".
        atlas = new TextureAtlas(ATLAS_FILE_NAME);
        // All regions in the atlas for creatures start with "entity" and have an index.
        Array<TextureAtlas.AtlasRegion> entities = atlas.findRegions("entity");
        // Extract animations from the atlas.
        // This step will be different for every game's assets.
        animations = new Array<>(4);
        // Apologies for the duplicated lines; these use libGDX 1.13.5's preferred way of initializing an Array.
        animations.add(new Array<Animation<TextureAtlas.AtlasSprite>>(true, 16, Animation.class));
        animations.add(new Array<Animation<TextureAtlas.AtlasSprite>>(true, 16, Animation.class));
        animations.add(new Array<Animation<TextureAtlas.AtlasSprite>>(true, 16, Animation.class));
        animations.add(new Array<Animation<TextureAtlas.AtlasSprite>>(true, 16, Animation.class));
        // Entities are stored in an odd order because of the tile sheet originally used for the atlas.
        // The original tile sheet is stored in the development repo for this demo:
        // https://github.com/tommyettinger/IsometricVoxelDemo/blob/a9c31f3891567958c3a4b581772defa2e902a5af/raw-assets/isometric-trpg-originals/IsometricTRPGAssetPack_Entities.png?raw=true
        // It stores each 2-frame animation on the same row as another animation, and the next row has backwards-facing
        // versions of the forwards-facing animations above them. The tile sheet determined what indices each
        // AtlasRegion received, so we have to keep the tricky numbering convention. Your own game will probably have
        // different art anyway!
        for (int i = 0, outer = 0; i < 16; i++, outer += 8) {
            /* Index 0 is front-facing idle animations.   */
            /* Index 1 is rear-facing idle animations.    */
            /* Index 2 is front-facing attack animations. */
            /* Index 3 is rear-facing attack animations.  */
            animations.get(0).add(new Animation<>(0.4f, Array.with(new TextureAtlas.AtlasSprite(entities.get(outer  )), new TextureAtlas.AtlasSprite(entities.get(outer+1))), Animation.PlayMode.LOOP));
            animations.get(1).add(new Animation<>(0.4f, Array.with(new TextureAtlas.AtlasSprite(entities.get(outer+4)), new TextureAtlas.AtlasSprite(entities.get(outer+5))), Animation.PlayMode.LOOP));
            animations.get(2).add(new Animation<>(0.2f, Array.with(new TextureAtlas.AtlasSprite(entities.get(outer+2)), new TextureAtlas.AtlasSprite(entities.get(outer+3))), Animation.PlayMode.LOOP));
            animations.get(3).add(new Animation<>(0.2f, Array.with(new TextureAtlas.AtlasSprite(entities.get(outer+6)), new TextureAtlas.AtlasSprite(entities.get(outer+7))), Animation.PlayMode.LOOP));
        }

        // Initialize a Camera with the width and height of the area to be shown.
        camera = new OrthographicCamera(Gdx.graphics.getWidth() * CAMERA_ZOOM, Gdx.graphics.getHeight() * CAMERA_ZOOM);
        // Center the camera in the middle of the map.
        camera.position.set(AssetData.TILE_WIDTH, SCREEN_VERTICAL * 0.5f, 0);
        // Updating the camera allows the changes we made to actually take effect.
        camera.update();
        // ScreenViewport is not always a great choice, but here we want only pixel-perfect zooms, and it can do that.
        viewport = new ScreenViewport(camera);

        // Calling regenerate() does the procedural map generation, and chooses a random player character.
        regenerate(
            /* The seed will change after just over one hour, and will stay the same for over an hour. */
            TimeUtils.millis() >>> 22);

        // Not currently used, but present in the assets.
        // See <a href="https://github.com/raeleus/skin-composer/wiki/From-the-Ground-Up:-Scene2D.UI-Tutorials">some scene2d.ui docs</a>
        // for more information on how to use a Skin.
        Skin skin = new Skin(Gdx.files.internal("isometric-trpg.json"), atlas);
        // The goal label text changes when updateFish() or updateHealth() is called.
        goalLabel = new Label("", skin);
        goalLabel.setPosition(0, SCREEN_VERTICAL - 30, Align.center);
        updateFish();
        // The health label shows red hearts (using BitmapFont markup to make them red) for your current health.
        // It shows " :( " if the player reaches 0 health, using darker red.
        healthLabel = new Label("[SCARLET]♥ ♥ ♥ ", skin);
        healthLabel.setPosition(-300, SCREEN_VERTICAL - 30, Align.left);
        updateHealth();

        // The FPS label can be removed if you want in production.
        fpsLabel = new Label("0 FPS", skin);
        fpsLabel.setPosition(0, SCREEN_VERTICAL - 50, Align.center);

        // These enforce the FPS cap and VSync settings from the first frame rendered.
        // Pressing 'C' will toggle the frame rate cap on or off.
        Gdx.graphics.setForegroundFPS(cap);
        Gdx.graphics.setVSync(cap != 0);
    }

    /**
     * Re-creates the map with the given seed for procedurally-generating the map and its inhabitants.
     * @param seed may be any long
     */
    public void regenerate(long seed) {

        // Needed so the PC Mover always has id 1
        Mover.ID_COUNTER = 1;
        startTime = TimeUtils.millis();
        map = LocalMap.generateTestMap(
            seed,
            /* Used for both dimensions of the ground plane. */
            MAP_SIZE + ((int)seed & 3),
            /* Used for the number of layers of the map, in elevation. */
            MAP_PEAK,
            /* All terrain tiles in the tileset. */
            atlas);
        map.totalFish = FISH_COUNT;
        map.fishSaved = 0;
        map.placeFish(seed, map.totalFish, animations);
        mapCenter = (map.getFSize() - 1f) * 0.5f;
        // Random initial position for the player.
        int rf = MathUtils.random(1, MAP_SIZE - 2), rg = MathUtils.random(1, MAP_SIZE - 2);
        // Random character graphic for the player; id 0-3 will always be a human wearing blue.
        int id = MathUtils.random(3);
        player = new Mover(map, animations, id, rf, rg, MAP_PEAK - 1);
        map.addMover(player, Mover.PLAYER_W);
        enemies = new Array<>(ENEMY_COUNT);
        for (int i = 0; i < ENEMY_COUNT; i++) {
            // enemies can be anywhere except the very edges of the map.
            rf = MathUtils.random(1, MAP_SIZE - 2);
            rg = MathUtils.random(1, MAP_SIZE - 2);
            // id 4-7 will always be a green-skinned, brawny orc.
            id = MathUtils.random(4, 7);
            Mover enemy = new Mover(map, animations, id, rf, rg, MAP_PEAK - 1.6f);
            // We track enemies here as well as tracking them as general Movers in the map so that we can handle the
            // semi-random movement of enemies when we update them in ${project.basic.mainClass}, without semi-randomly moving the player.
            enemies.add(enemy);
            map.addMover(enemy, Mover.NPC_W);
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        // handleInput() is where all keyboard input is handled. Mouse input isn't really handled right now, except to
        // add or remove blocks as a bit of debugging play.
        handleInput(delta);
        // Calling update() on a Mover makes them do all their logic for an unpaused game.
        player.update(delta);
        for(Mover e : enemies) e.update(delta);

        // If the player is dead, show them as a translucent ghost.
        if(player.health <= 0) player.visual.sprite.setAlpha(0.5f);

        // This bit of code gets a little complex to handle rotating the map...
        // But rotating the map is so cool! You can do it by pressing '[' or ']' .
        float time = TimeUtils.timeSinceMillis(startTime) * 0.001f;
        // Rotations stop on a 90-degree angle increment, stored as an int from 0 to 3.
        int prevRotationIndex = (int)((map.rotationDegrees + 45f) * (1f / 90f)) & 3;

        // A rotation completes in half a second, which is quick enough to conceal some of the roughness during parts
        // of the animation.
        map.setRotationDegrees(MathUtils.lerpAngleDeg(map.previousRotation, map.targetRotation,
            Math.min(TimeUtils.timeSinceMillis(animationStart) * 0.002f, 1f)));

        // We sort the "everything" OrderedMap here using our custom comparator.
        final Array<Vector4> order = map.everything.orderedKeys();
        order.sort(comparator);

        // Our current rotation index, in 90-degree increments, so from 0 to 3.
        int rotationIndex = (int)((map.rotationDegrees + 45f) * (1f / 90f)) & 3;
        if(prevRotationIndex != rotationIndex) {
            for (int i = 0, n = order.size; i < n; i++) {
                Vector4 pt = order.get(i);
                if(pt.w != 0f) continue; // 0f is used for terrain, higher values for creatures, lower for outlines.
                int[] rots = AssetData.ROTATIONS.get(map.getTile(pt)); // some tiles change appearance when rotated
                if(rots != null)
                    map.everything.get(pt).sprite.setRegion(map.tileset.get(rots[rotationIndex]));
            }
        }

        // When the rotation has finished, we set the previous rotation to what we just ended on.
        if(MathUtils.isEqual(map.rotationDegrees, map.targetRotation))
            map.previousRotation = map.targetRotation;
        // Very dark blue for the background color.
        ScreenUtils.clear(.14f, .15f, .2f, 1f);
        // Vital to get things to display. I don't actually know what the "combined" matrix is here.
        batch.setProjectionMatrix(camera.combined);
        // We need to apply() the viewport here in case it changed for any reason, such as from key inputs.
        viewport.apply();
        batch.begin();
        for (int i = 0, n = order.size; i < n; i++) {
            Vector4 pos = order.get(i);
            // Updates each voxel in "everything" and then draws it with the parameters needed for rotation.
            map.everything.get(pos).update(time).draw(batch, (map.getFSize() - 1) * 0.5f, (map.getGSize() - 1) * 0.5f, map.cosRotation, map.sinRotation);
        }

        Vector3 pos = player.getPosition();
        // Makes tempVector4 store the position we want to check: the players's location, rounded, at the fish depth.
        // If there is anything at that position, it is a fish the player is touching, and so has rescued.
        map.setToFishPosition(tempVector4, pos.x, pos.y, pos.z);
        IsoSprite fish = map.everything.get(tempVector4);
        if(fish instanceof AnimatedIsoSprite && fish != player.visual){
            ++map.fishSaved;
            map.everything.remove(tempVector4);
            ((${project.basic.mainClass})Gdx.app.getApplicationListener()).updateFish();
        }

        fpsLabel.getText().clear();
        fpsLabel.getText().append(Gdx.graphics.getFramesPerSecond()).append(" FPS");
        // Allows the FPS label to be drawn with the correct width.
        fpsLabel.invalidate();
        goalLabel.draw(batch, 1f);
        fpsLabel.draw(batch, 1f);
        healthLabel.draw(batch, 1f);
        batch.end();
    }

    /**
     * Only handles movement input for the player character. This is called from the main input handling in
     * {@link #handleInput(float)}.
     */
    private void handleInputPlayer() {
        // Our difference (delta) on the f and g isometric axes.
        float df = 0, dg = 0;
        // We allow f,g,t,r to move on one isometric axis, or the numpad to move in all 8 directions.
        // You can also hold a pair of f,g,t,r (adjacent on a QWERTY keyboard) to move north, south, east, or west.
             if (Gdx.input.isKeyPressed(Input.Keys.F) && Gdx.input.isKeyPressed(Input.Keys.G)) { df = -INVERSE_ROOT_2; dg = -INVERSE_ROOT_2; }
        else if (Gdx.input.isKeyPressed(Input.Keys.F) && Gdx.input.isKeyPressed(Input.Keys.R)) { df = -INVERSE_ROOT_2; dg = INVERSE_ROOT_2; }
        else if (Gdx.input.isKeyPressed(Input.Keys.G) && Gdx.input.isKeyPressed(Input.Keys.T)) { df = INVERSE_ROOT_2; dg = -INVERSE_ROOT_2; }
        else if (Gdx.input.isKeyPressed(Input.Keys.T) && Gdx.input.isKeyPressed(Input.Keys.R)) { df = INVERSE_ROOT_2; dg = INVERSE_ROOT_2; }
        else if (Gdx.input.isKeyPressed(Input.Keys.F) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1)) df = -1;
        else if (Gdx.input.isKeyPressed(Input.Keys.G) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_3)) dg = -1;
        else if (Gdx.input.isKeyPressed(Input.Keys.T) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_9)) df = 1;
        else if (Gdx.input.isKeyPressed(Input.Keys.R) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_7)) dg = 1;
        else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) { df = -INVERSE_ROOT_2; dg = -INVERSE_ROOT_2; }
        else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)) { df = -INVERSE_ROOT_2; dg = INVERSE_ROOT_2; }
        else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)) { df = INVERSE_ROOT_2; dg = -INVERSE_ROOT_2; }
        else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)) { df = INVERSE_ROOT_2; dg = INVERSE_ROOT_2; }

        // We account for the map's rotation so the visual rotation of the map (for the player) also affects the
        // direction in tiles for their chosen direction as they perceive it.
        float c = map.cosRotation;
        float s = map.sinRotation;
        float rf = c * df + s * dg;
        float rg = c * dg - s * df;

        player.move(rf, rg, Mover.PC_MOVE_SPEED);

        // Whee! Space or Numpad 0 or 5 make the player character jump really high.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
         || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)
         || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5)) {
            player.jump();
        }
    }

    /**
     * Input for the game itself, though this also calls {@link #handleInputPlayer()}.
     * @param delta the amount of time in seconds since the last unpaused render
     */
    private void handleInput(float delta) {
        // Get out of here!
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            return;
        }
        // Zero-state, or a brand-new map and fresh healthy character, also with 10 enemies and 10 goldfish.
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            reset();
            return;
        }
        // cap for frame rate
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            // If cap is at the limit, set it to 0; if cap is 0, set it to the limit. This toggles cap.
            cap ^= FRAME_RATE_LIMIT;
            // If cap is 0, there is no limit on FPS unless the user's drivers force VSync on.
            Gdx.graphics.setForegroundFPS(cap);
            // If the cap is disabled (at 0), then we try to turn VSync off to enable more FPS than can be seen.
            Gdx.graphics.setVSync(cap != 0);
            return;
        }
        handleInputPlayer();

        // The arrow keys move the camera, which isn't really necessary here.
        // You can comment out the next 4 lines if you want.
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.translate(0, 200 * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.translate(0, -200 * delta);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) camera.translate(-200 * delta, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) camera.translate(200 * delta, 0);
        // I and O zoom in and out, respectively.
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) camera.zoom *= .5f; // In
        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) camera.zoom *= 2f; // Out

        // The square bracket keys handle rotation, which can be important to spot goldfish behind terrain.
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT_BRACKET)) {
            map.previousRotation = map.rotationDegrees;
            map.targetRotation = (MathUtils.round(map.rotationDegrees * (1f/90f)) + 1 & 3) * 90;
            animationStart = TimeUtils.millis();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT_BRACKET)) {
            map.previousRotation = map.rotationDegrees;
            map.targetRotation = (MathUtils.round(map.rotationDegrees * (1f/90f)) - 1 & 3) * 90;
            animationStart = TimeUtils.millis();
        }
        // Because the arrow keys, as well as I and O, can change the camera, we update it here.
        camera.update();

        /*
        // These are sort-of present (in a block comment) for debugging.
        // Left-clicking the top of a voxel will stack a random voxel on it.
        // Right-clicking the top of a voxel will remove it.
        if (Gdx.input.justTouched()) {
            Vector3 targetBlock = raycastToBlock(Gdx.input.getX(), Gdx.input.getY());

            // raycastToBlock() returns a Vector3 full of integers; they just need to be cast to int.
            int f = (int)targetBlock.x, g = (int)targetBlock.y, h = (int)targetBlock.z;
            Gdx.app.log("CLICK", "targetBlock " + targetBlock);

            if (map.isValid(f, g, h)) {
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    if(map.getTile(f, g, h) == -1)
                        map.setTile(f, g, h, MathUtils.random(3));
                    else
                        map.setTile(f, g, h + 1, MathUtils.random(3));
                } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                    map.setTile(f, g, h, -1);
                }
            }
        }
        */
    }

    /**
     * Used to take a screen position and find the isometric tile position of the voxel shown at that screen position.
     * This returns {@link #isoTempVector} and modifies it every time this is called, so you should use its value
     * immediately or set() it into another Vector3.
     * <br>
     * This is only used in a comment here, but if you uncomment the end of {@link #handleInput(float)}, then this will
     * be in use, and clicking the top of a block will use this method to figure out which block was clicked.
     * @param screenX screen coordinate, horizontal
     * @param screenY screen coordinate, vertical; note that this uses y-down by libGDX convention
     * @return an isometric tile position of a voxel or an empty space at ground level; always {@link #isoTempVector}
     */
    public Vector3 raycastToBlock(float screenX, float screenY) {
        Vector3 worldPos = camera.unproject(projectionTempVector.set(screenX, screenY, 0));
        // Why is the projection slightly off on both x and y? It is a mystery!
        worldPos.x -= AssetData.TILE_WIDTH;
        worldPos.y -= AssetData.TILE_HEIGHT * 4;
        // Check from highest to lowest Z
        for (int h = MAP_PEAK - 1; h >= 0; h--) {
            int f = MathUtils.ceil(worldPos.y * (0.5f / AssetData.TILE_HEIGHT) + worldPos.x * (0.5f / AssetData.TILE_WIDTH) - h);
            int g = MathUtils.ceil(worldPos.y * (0.5f / AssetData.TILE_HEIGHT) - worldPos.x * (0.5f / AssetData.TILE_WIDTH) - h);
            if (f >= 0 && f < MAP_SIZE && g >= 0 && g < MAP_SIZE) {
                if (map.getTile(f, g, h) != -1) { // Found a solid block
                    return isoTempVector.set(f, g, h); // Return the first valid block found
                }
            }
        }

        // No block was hit, return ground level
        Vector3 groundCoords = worldToIso(worldPos.x, worldPos.y);
        return groundCoords.set(MathUtils.ceil(groundCoords.x), MathUtils.ceil(groundCoords.y), 0);
    }

    /**
     * Currently unused; converts an un-rotated isometric tile position to a world position in viewport units.
     * Because this doesn't understand rotation, it isn't as useful.
     * World positions use y-up.
     * @param f France to Finland axis
     * @param g Germany to Greenland axis
     * @param h heel to head axis
     * @return {@link #screenTempVector}, modified in-place and returned directly
     */
    public static Vector2 isoToWorld(float f, float g, float h) {
        float screenX = (f - g) * AssetData.TILE_WIDTH;
        float screenY = (f + g) * AssetData.TILE_HEIGHT + h * AssetData.TILE_DEPTH;
        return screenTempVector.set(screenX, screenY);
    }

    /**
     * Currently unused; converts a world position in viewport units to an un-rotated isometric tile position.
     * Because this doesn't understand rotation, it isn't as useful.
     * @param worldX world position, horizontal in viewport units
     * @param worldY world position; vertical in viewport units; uses y-up like most of libGDX
     * @return {@link #isoTempVector}, modified in-place and returned directly
     */
    public static Vector3 worldToIso(float worldX, float worldY) {
        float f = worldY * (0.5f / AssetData.TILE_HEIGHT) + worldX * (0.5f / AssetData.TILE_WIDTH) + 1;
        float g = worldY * (0.5f / AssetData.TILE_HEIGHT) - worldX * (0.5f / AssetData.TILE_WIDTH) + 1;
        return isoTempVector.set(f, g, 0);
    }

    /**
     * Resets the map to a procedurally-generated new map, and updates Labels showing gameplay progress.
     */
    private void reset() {
        regenerate(MathUtils.random.nextLong());
        updateFish();
        updateHealth();
    }

    @Override
    public void dispose() {
        batch.dispose();
        atlas.dispose();
        backgroundMusic.stop();
        backgroundMusic.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // If unitsPerPixel are a fraction like 1f/2 or 1f/3, then that makes each pixel 2x or 3x the size, resp.
        // This will only divide 1f by an integer amount 1 or greater, which makes pixels always the exact right size.
        // This meant to fit an isometric map that is about MAP_SIZE by MAP_PEAK by MAP_SIZE, where MAP_PEAK is how many
        // layers of voxels can be stacked on top of each other.
        viewport.setUnitsPerPixel(1f / Math.max(1, (int) Math.min(
            width  / ((MAP_SIZE+1f) * (AssetData.TILE_WIDTH * 2f)),
            height / ((MAP_SIZE+1f) * (AssetData.TILE_HEIGHT * 2f) + AssetData.TILE_DEPTH * MAP_PEAK))));
        viewport.update(width, height);
    }

    /**
     * Updates the goal Label to show the appropriate number of goldfish you need to save, or if you have won.
     */
    public void updateFish() {
        if(player.health > 0) {
            if (map.totalFish == map.fishSaved) {
                goalLabel.setText("YOU SAVED THEM ALL! Great job!");
                player.makeInvincible(Float.NaN);
            }
            else
                goalLabel.setText("SAVE THE GOLDFISH!!! " + (map.totalFish - map.fishSaved) + " still " +
                    ((map.totalFish - map.fishSaved) == 1 ? "needs" : "need") + " your help!");
        }
        goalLabel.setAlignment(Align.center);
        goalLabel.setPosition(goalLabel.getX(), goalLabel.getY(), Align.center);
    }

    /**
     * Updates the health and sometimes goal Labels to show your current health and if you have died.
     */
    public void updateHealth() {
        if(player.health <= 0)
        {
            goalLabel.setText("YOU FAILED.. BY DYING...");
            goalLabel.setAlignment(Align.center);
            goalLabel.setPosition(goalLabel.getX(), goalLabel.getY(), Align.center);
            healthLabel.setText("[FIREBRICK]:(");
        }
        else {
            healthLabel.getText().clear();
            // Shows one red heart per point of health.
            healthLabel.getText().append("[SCARLET]");
            for (int i = 0; i < player.health; i++) {
                healthLabel.getText().append(" ♥");
            }
            healthLabel.setText(healthLabel.getText().toString());
            healthLabel.invalidate();
        }
    }
}"""
}
