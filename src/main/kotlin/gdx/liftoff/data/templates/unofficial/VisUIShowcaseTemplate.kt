package gdx.liftoff.data.templates.unofficial

import gdx.liftoff.data.files.CopiedFile
import gdx.liftoff.data.files.path
import gdx.liftoff.data.libraries.unofficial.VisUI
import gdx.liftoff.data.platforms.Assets
import gdx.liftoff.data.platforms.Core
import gdx.liftoff.data.project.Project
import gdx.liftoff.data.templates.Template
import gdx.liftoff.views.ProjectTemplate

/**
 * VisUI application example with widget showcase and menu bar
 * @author Kotcrab
 */
@ProjectTemplate
@Suppress("unused") // Referenced via reflection.
class VisUIShowcaseTemplate : Template {
  override val id = "visUiShowcaseTemplate"
  override val width: String
    get() = "800"
  override val height: String
    get() = "600"
  override val description: String
    get() = "Project template included simple launchers and an `ApplicationAdapter` extension with a showcase " +
      "of widgets created using the [VisUI](https://github.com/kotcrab/vis-ui) library."

  override fun getApplicationListenerContent(project: Project): String = """package ${project.basic.rootPackage};

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ${project.basic.mainClass} extends ApplicationAdapter {
    private MenuBar menuBar;
    private Stage stage;

    @Override
    public void create () {
        VisUI.setSkipGdxVersionCheck(true);
        VisUI.load(SkinScale.X1);

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        menuBar = new MenuBar();
        root.add(menuBar.getTable()).growX().row();
        root.add().grow();

        createMenus();

        stage.addActor(new TestWindow());
    }

    private void createMenus () {
        Menu startTestMenu = new Menu("start test");
        Menu fileMenu = new Menu("file");
        Menu editMenu = new Menu("edit");

        startTestMenu.addItem(new MenuItem("listview", new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                stage.addActor(new TestListView());
            }
        }));

        startTestMenu.addItem(new MenuItem("tabbed pane", new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                stage.addActor(new TestTabbedPane());
            }
        }));

        startTestMenu.addItem(new MenuItem("collapsible", new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                stage.addActor(new TestCollapsible());
            }
        }));

        // Creating dummy menu items for showcase
        fileMenu.addItem(new MenuItem("menuitem #1"));
        fileMenu.addItem(new MenuItem("menuitem #2").setShortcut("f1"));
        fileMenu.addItem(new MenuItem("menuitem #3").setShortcut("f2"));

        editMenu.addItem(new MenuItem("menuitem #4"));
        editMenu.addItem(new MenuItem("menuitem #5"));
        editMenu.addSeparator();
        editMenu.addItem(new MenuItem("menuitem #6"));
        editMenu.addItem(new MenuItem("menuitem #7"));

        menuBar.addMenu(startTestMenu);
        menuBar.addMenu(fileMenu);
        menuBar.addMenu(editMenu);
    }

    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose () {
        VisUI.dispose();
        stage.dispose();
    }
}
"""

  override fun apply(project: Project) {
    super.apply(project)
    project.files.add(
      CopiedFile(
        projectName = Assets.ID,
        original = path("generator", "assets", ".gitkeep"),
        path = ".gitkeep"
      )
    )
    VisUI().initiate(project)

    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage,
      fileName = "TestWindow.java",
      content = """package ${project.basic.rootPackage};

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.spinner.ArraySpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.SimpleFloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;

public class TestWindow extends VisWindow {
  private static final Drawable white = VisUI.getSkin().getDrawable("white");

  public TestWindow () {
    super("example window");

    TableUtils.setSpacingDefaults(this);
    columnDefaults(0).left();

    add(createLabels()).row();
    add(createButtons()).row();
    add(createCheckboxes()).row();
      add(createTextFields()).row();
        add(createProgressBars()).row();
        add(createSpinners()).row();
        add(createSelectBox()).row();
        if (Gdx.app.getType() == ApplicationType.Desktop) add(createFileChooser()).row();
        add(createColorPicker()).padBottom(3f);

        pack();
        centerWindow();
    }

    private VisTable createLabels () {
        VisLabel label = new VisLabel("label (hover for tooltip)");
        new Tooltip.Builder("this label has a tooltip").target(label).build();

        VisTable labelTable = new VisTable(true);
        labelTable.add(label);
        return labelTable;
    }

    private VisTable createButtons () {
        VisTextButton normalButton = new VisTextButton("button");
        VisTextButton normalBlueButton = new VisTextButton("button blue", "blue");
        VisTextButton toggleButton = new VisTextButton("toggle", "toggle");
        VisTextButton disabledButton = new VisTextButton("disabled");
        disabledButton.setDisabled(true);

        VisTable buttonTable = new VisTable(true);
        buttonTable.add(normalButton);
        buttonTable.add(normalBlueButton);
        buttonTable.add(toggleButton);
        buttonTable.add(disabledButton);
        return buttonTable;
    }

    private VisTable createCheckboxes () {
        VisCheckBox normalCheckbox = new VisCheckBox("checkbox");
        VisCheckBox disabledCheckbox = new VisCheckBox("disabled");
        disabledCheckbox.setDisabled(true);

        VisTable checkboxTable = new VisTable(true);
        checkboxTable.add(normalCheckbox);
        checkboxTable.add(disabledCheckbox);
        return checkboxTable;
    }

    private Actor createTextFields () {
        VisTextField normalTextField = new VisTextField("textbox");
        VisTextField disabledTextField = new VisTextField("disabled");
        VisTextField passwordTextField = new VisTextField("password");
        disabledTextField.setDisabled(true);
        passwordTextField.setPasswordMode(true);

        VisTable textFieldTable = new VisTable(true);
        textFieldTable.defaults().width(120);
        textFieldTable.add(normalTextField);
        textFieldTable.add(disabledTextField);
        textFieldTable.add(passwordTextField);
        return textFieldTable;
    }

    private Actor createProgressBars () {
        VisProgressBar progressbar = new VisProgressBar(0, 100, 1, false);
        VisSlider slider = new VisSlider(0, 100, 1, false);
        VisSlider sliderDisabled = new VisSlider(0, 100, 1, false);

        progressbar.setValue(50);
        slider.setValue(50);
        sliderDisabled.setValue(50);
        sliderDisabled.setDisabled(true);

        VisTable progressbarTable = new VisTable(true);
        progressbarTable.add(progressbar);
        progressbarTable.add(slider);
        progressbarTable.add(sliderDisabled);
        return progressbarTable;
    }

    private Actor createSpinners () {
        Array<String> stringArray = new Array<String>();
        stringArray.add("a");
        stringArray.add("b");
        stringArray.add("c");
        stringArray.add("d");
        stringArray.add("e");
        final ArraySpinnerModel<String> arrayModel = new ArraySpinnerModel<String>(stringArray);
        Spinner arraySpinner = new Spinner("array", arrayModel);

        final IntSpinnerModel intModel = new IntSpinnerModel(10, 5, 20, 2);
        Spinner intSpinner = new Spinner("integers", intModel);

        arraySpinner.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                System.out.println("changed array spinner to: " + arrayModel.getCurrent());
            }
        });

        intSpinner.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                System.out.println("changed int spinner to: " + intModel.getValue());
            }
        });

        VisTable spinnerTable = new VisTable(true);
        spinnerTable.add(intSpinner);
        spinnerTable.add(new Spinner("floats", new SimpleFloatSpinnerModel(10f, 5f, 20f, 1.5f, 1)));
        spinnerTable.add(arraySpinner);
        return spinnerTable;
    }

    private VisTable createSelectBox () {
        VisTable selectBoxTable = new VisTable(true);
        VisSelectBox<String> selectBox = new VisSelectBox<String>();
        selectBox.setItems("item 1", "item 2", "item 3", "item 4");

        selectBoxTable.add(new VisLabel("select box: "));
        selectBoxTable.add(selectBox);
        return selectBoxTable;
    }

    private VisTable createFileChooser () {
        // The following example can't be used on GWT, feel free to uncomment if you are not targeting GWT.

        // These imports must be added:
        //import com.kotcrab.vis.ui.widget.file.FileChooser;
        //import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;

        /*
        FileChooser.setFavoritesPrefsName("${project.basic.mainClass}");
        final FileChooser chooser = new FileChooser(FileChooser.Mode.OPEN);
        VisTextButton showButton = new VisTextButton("show file chooser");
        final VisLabel selectedFileLabel = new VisLabel("");

        chooser.setSelectionMode(FileChooser.SelectionMode.FILES);
        chooser.setMultiSelectionEnabled(false);
        chooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected (FileHandle file) {
                Dialogs.showOKDialog(getStage(), "message", "selected: " + file.path());
            }
        });

        showButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                getStage().addActor(chooser.fadeIn());
            }
        });

        VisTable fileChooserTable = new VisTable(true);
        fileChooserTable.add(showButton);
        fileChooserTable.add(selectedFileLabel).width(100);
        return fileChooserTable;
        */
        return new VisTable();
    }

    private VisTable createColorPicker () {
        final Image image = new Image(white);
        final ColorPicker picker = new ColorPicker("color picker", new ColorPickerAdapter() {
            @Override
            public void finished (Color newColor) {
                image.setColor(newColor);
            }
        });

        VisTextButton showPickerButton = new VisTextButton("show color picker");
        showPickerButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                getStage().addActor(picker.fadeIn());
            }
        });

        Color c = new Color(27 / 255.0f, 161 / 255.0f, 226 / 255.0f, 1);
        picker.setColor(c);
        image.setColor(c);

        VisTable pickerTable = new VisTable(true);
        pickerTable.add(showPickerButton);
        pickerTable.add(image).size(32).pad(3);
        return pickerTable;
    }
}
"""
    )

    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage,
      fileName = "TestListView.java",
      content = """package ${project.basic.rootPackage};

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.adapter.ArrayAdapter;
import com.kotcrab.vis.ui.util.form.SimpleFormValidator;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.ListView.ItemClickListener;
import com.kotcrab.vis.ui.widget.ListView.UpdatePolicy;

import java.util.Comparator;

public class TestListView extends VisWindow {
    public TestListView () {
        super("listview");

        TableUtils.setSpacingDefaults(this);
        columnDefaults(0).left();

        addCloseButton();
        closeOnEscape();

        Array<Model> array = new Array<Model>();
        for (int i = 1; i <= 3; i++) {
            array.add(new Model("Windows" + i, VisUI.getSkin().getColor("vis-red")));
            array.add(new Model("Linux" + i, Color.GREEN));
            array.add(new Model("OSX" + i, Color.WHITE));
        }

        final TestAdapter adapter = new TestAdapter(array);
        ListView<Model> view = new ListView<Model>(adapter);
        view.setUpdatePolicy(UpdatePolicy.ON_DRAW);

        VisTable footerTable = new VisTable();
        footerTable.addSeparator();
        footerTable.add("Table Footer");
        view.setFooter(footerTable);

        final VisValidatableTextField nameField = new VisValidatableTextField();
        VisTextButton addButton = new VisTextButton("Add");

        SimpleFormValidator validator = new SimpleFormValidator(addButton);
        validator.notEmpty(nameField, "");

        add(new VisLabel("New Name:"));
        add(nameField);
        add(addButton);
        row();
        add(view.getMainTable()).colspan(3).grow();

        addButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                //by changing array using adapter view will be invalidated automatically
                adapter.add(new Model(nameField.getText(), Color.GRAY));
                nameField.setText("");
            }
        });

        view.setItemClickListener(new ItemClickListener<Model>() {
            @Override
            public void clicked (Model item) {
                System.out.println("Clicked: " + item.name);
            }
        });

        setSize(300, 300);
        centerWindow();
    }

    private static class Model {
        public String name;
        public Color color;

        public Model (String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }

    private static class TestAdapter extends ArrayAdapter<Model, VisTable> {
        private final Drawable bg = VisUI.getSkin().getDrawable("window-bg");
        private final Drawable selection = VisUI.getSkin().getDrawable("list-selection");

        public TestAdapter (Array<Model> array) {
            super(array);
            setSelectionMode(SelectionMode.SINGLE);

            setItemsSorter(new Comparator<Model>() {
                @Override
                public int compare (Model o1, Model o2) {
                    return o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
                }
            });
        }

        @Override
        protected VisTable createView (Model item) {
            VisLabel label = new VisLabel(item.name);
            label.setColor(item.color);

            VisTable table = new VisTable();
            table.left();
            table.add(label);
            return table;
        }

        @Override
        protected void updateView (VisTable view, Model item) {
            super.updateView(view, item);
        }

        @Override
        protected void selectView (VisTable view) {
            view.setBackground(selection);
        }

        @Override
        protected void deselectView (VisTable view) {
            view.setBackground(bg);
        }
    }
}
"""
    )

    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage,
      fileName = "TestTabbedPane.java",
      content = """package ${project.basic.rootPackage};

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

public class TestTabbedPane extends VisWindow {

    public TestTabbedPane () {
        super("tabbed pane");

        TableUtils.setSpacingDefaults(this);

        setResizable(true);
        addCloseButton();
        closeOnEscape();

        final VisTable container = new VisTable();

        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab (Tab tab) {
                container.clearChildren();
                container.add(tab.getContentTable()).expand().fill();
            }
        });

        add(tabbedPane.getTable()).expandX().fillX();
        row();
        add(container).expand().fill();

        tabbedPane.add(new TestTab("tab1"));
        tabbedPane.add(new TestTab("tab2"));
        tabbedPane.add(new TestTab("tab3"));
        tabbedPane.add(new TestTab("tab4"));
        tabbedPane.add(new TestTab("tab5"));
        tabbedPane.add(new TestTab("tab6"));
        tabbedPane.add(new TestTab("tab7"));
        tabbedPane.add(new TestTab("tab8"));
        tabbedPane.add(new TestTab("tab9"));

        setSize(300, 200);
        centerWindow();
    }

    private class TestTab extends Tab {
        private String title;
        private Table content;

        public TestTab (String title) {
            super(false, true);
            this.title = title;

            content = new VisTable();
            content.add(new VisLabel(title));
        }

        @Override
        public String getTabTitle () {
            return title;
        }

        @Override
        public Table getContentTable () {
            return content;
        }
    }
}
"""
    )

    addSourceFile(
      project = project,
      platform = Core.ID,
      packageName = project.basic.rootPackage,
      fileName = "TestCollapsible.java",
      content = """package ${project.basic.rootPackage};

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.*;

public class TestCollapsible extends VisWindow {

    public TestCollapsible () {
        super("collapsible widget");

        columnDefaults(0).left();

        addCloseButton();
        closeOnEscape();

        VisCheckBox collapseCheckBox = new VisCheckBox("show advanced settings");
        collapseCheckBox.setChecked(true);

        VisTable table = new VisTable();
        final CollapsibleWidget collapsibleWidget = new CollapsibleWidget(table);

        VisTable numberTable = new VisTable(true);
        numberTable.add(new VisLabel("2 + 2 * 2 = "));
        numberTable.add(new VisTextField());

        table.defaults().left();
        table.defaults().padLeft(10);
        table.add(new VisCheckBox("advanced option #1")).row();
        table.add(new VisCheckBox("advanced option #2")).row();
        table.add(numberTable).padTop(3).row();

        collapseCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                collapsibleWidget.setCollapsed(!collapsibleWidget.isCollapsed());
            }
        });

        top();
        add(collapseCheckBox).row();
        add(collapsibleWidget).expandX().fillX().row();

        centerWindow();
        pack();
    }
}
"""
    )
  }
}
