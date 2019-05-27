package com.github.czyzby.setup.data.templates.unofficial

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.libs.unofficial.VisRuntime
import com.github.czyzby.setup.data.platforms.Assets
import com.github.czyzby.setup.data.platforms.Core
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.data.templates.Template
import com.github.czyzby.setup.views.ProjectTemplate

/**
 * Example application showing how to load exported VisEditor project. Project can be also opened in VisEditor.
 * @author Kotcrab
 */
@ProjectTemplate
class VisEditorBasicTemplate : Template {
    override val id = "visEditorBasicTemplate"
    override val description: String
        get() = "Project template included simple launchers and an `ApplicationAdapter` extension showing how to load project exported from VisEditor."

    override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.scene.Scene;
import com.kotcrab.vis.runtime.scene.SceneConfig;
import com.kotcrab.vis.runtime.scene.SceneFeature;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;
import com.kotcrab.vis.runtime.scene.VisAssetManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
	SpriteBatch batch;
	VisAssetManager manager;

	Scene scene;

	@Override
	public void create () {
		batch = new SpriteBatch();

		manager = new VisAssetManager(batch); //creates new asset manager
		manager.getLogger().setLevel(Logger.ERROR); //enable detailed error logging

		// allows to change default runtime settings
		RuntimeConfiguration configuration = new RuntimeConfiguration();
		manager.getSceneLoader().setRuntimeConfig(configuration);

		// SceneParameter allows to add custom systems into artemis-odb
		SceneParameter parameter = new SceneParameter();
		parameter.config.addSystem(CameraController.class, SceneConfig.Priority.LOW); // add custom camera controller
		parameter.config.disable(SceneFeature.GROUP_ID_MANAGER); // SceneParameter also allows to disable built-in systems
		scene = manager.loadSceneNow("scene/example.scene", parameter); // load example scene
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		scene.render();
	}

	@Override
	public void resize (int width, int height) {
		scene.resize(width, height);
	}

	@Override
	public void dispose () {
		batch.dispose();
		manager.dispose();
	}
}"""

    override fun apply(project: Project) {
        super.apply(project)
        VisRuntime().initiate(project)

        // Adding exported project:
        arrayOf("textures.atlas", "textures.png").forEach {
            project.files.add(CopiedFile(projectName = Assets.ID, path = path(it),
                    original = path("generator", "templates", "viseditor", "exported", it)))
        }
        arrayOf("icon.png", "libgdx.png", "plus.png").forEach {
            project.files.add(CopiedFile(projectName = Assets.ID, path = path("gfx", it),
                    original = path("generator", "templates", "viseditor", "exported", "gfx", it)))
        }
        project.files.add(CopiedFile(projectName = Assets.ID, path = path("scene", "example.scene"),
                original = path("generator", "templates", "viseditor", "exported", "scene", "example.scene")))

        // Adding VisEditor project:
        arrayOf("textures.atlas", "textures.png").forEach {
            project.files.add(CopiedFile(projectName = Assets.ID, path = path("assets", it),
                    original = path("generator", "templates", "viseditor", "exported", it)))
        }
        project.files.add(CopiedFile(projectName = "vis", path = path("project.json"),
                original = path("generator", "templates", "viseditor", "project", "project.json")))
        project.files.add(CopiedFile(projectName = "vis", path = path("modules", "version.json"),
                original = path("generator", "templates", "viseditor", "project", "modules", "version.json")))
        project.files.add(CopiedFile(projectName = "vis", path = path("modules", "settings", "exportSettings"),
                original = path("generator", "templates", "viseditor", "project", "modules", "settings", "exportSettings")))
        project.files.add(CopiedFile(projectName = "vis", path = path("assets", "scene", "example.scene"),
                original = path("generator", "templates", "viseditor", "project", "assets", "scene", "example.scene")))
        arrayOf("icon.png", "libgdx.png", "plus.png").forEach {
            project.files.add(CopiedFile(projectName = "vis", path = path("assets", "gfx", it),
                    original = path("generator", "templates", "viseditor", "project", "assets", "gfx", it)))
        }

        addSourceFile(project = project, platform = Core.ID, packageName = project.basic.rootPackage,
                fileName = "CameraController.java", content = """package ${project.basic.rootPackage};

import com.artemis.Aspect;
import com.artemis.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.kotcrab.vis.runtime.system.CameraManager;

/** Example of custom {@link EntitySystem} allowing to control camera position and zoom using keyboard. */
public class CameraController extends EntitySystem {
	private CameraManager cameraManager; // auto injected by Artemis

	private static final float CAMERA_MOVE_DELTA = 0.1f;
	private static final float CAMERA_ZOOM_DELTA = 0.01f;

	public CameraController () {
		super(Aspect.all());
	}

	@Override
	protected void processSystem () {
		OrthographicCamera camera = cameraManager.getCamera();
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.position.y += CAMERA_MOVE_DELTA;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			camera.position.y -= CAMERA_MOVE_DELTA;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.position.x -= CAMERA_MOVE_DELTA;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.position.x += CAMERA_MOVE_DELTA;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.Q) && camera.zoom > 0.1f) {
			camera.zoom -= CAMERA_ZOOM_DELTA;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E) && camera.zoom < 3f) {
			camera.zoom += CAMERA_ZOOM_DELTA;
		}
	}
}""");

    }
}
