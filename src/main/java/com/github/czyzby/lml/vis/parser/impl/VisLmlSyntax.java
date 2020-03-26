package com.github.czyzby.lml.vis.parser.impl;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.github.czyzby.lml.parser.impl.DefaultLmlSyntax;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.ActorLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.ActorStorageLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.AnimatedImageLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.ButtonGroupLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.ButtonLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.ContainerLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.HorizontalGroupLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.StackLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.TooltipLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.TouchpadLmlTagProvider;
import com.github.czyzby.lml.parser.impl.tag.actor.provider.VerticalGroupLmlTagProvider;
import com.github.czyzby.lml.util.LmlUserObject.StandardTableTarget;
import com.github.czyzby.lml.util.LmlUserObject.TableExtractor;
import com.github.czyzby.lml.vis.parser.impl.attribute.ColorPickerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.FocusBorderEnabledLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.ResponsiveColorPickerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.VisTooltipLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.GroupTypeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.IntMaxLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.IntMinLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.IntStepLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.IntValueLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.ListAdapterLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.MenuItemImageLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.ShowWindowBorderLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.StringMaxLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.StringMinLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.StringStepLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.building.StringValueLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.button.ButtonImageLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.button.ImageButtonGenerateDisabledLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.button.TextButtonImageLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.collapsible.CollapsedLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.collapsible.HorizontalCollapsedLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.BlockInputLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.DeadzoneRadiusLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.DragListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.DraggedAlphaLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.DraggedFadingInterpolationLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.DraggedFadingTimeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.DraggedMovingInterpolationLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.DraggedMovingTimeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.InvisibleWhenDraggedLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.KeepDraggedWithinParentLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.pane.AcceptForeignLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.pane.DragPaneListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.pane.GroupIdLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.draggable.pane.MaxChildrenLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.floating.UseChildrenPreferredSizeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.flow.HorizontalSpacingLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.flow.VerticalSpacingLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.grid.GridSpacingLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.grid.ItemHeightLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.grid.ItemSizeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.grid.ItemWidthLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.grid.fixed.BlockIndexesLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.grid.fixed.ItemsAmountLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.HighlighterLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.BlinkTimeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.CursorLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.DigitsOnlyLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.EnterKeyFocusTraversalLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.IgnoreEqualsTextChangeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.InputAlignLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.MaxLengthLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.MessageLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.PasswordCharacterLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.PasswordModeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.PrefRowsLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.ReadOnlyLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.RestoreLastValidLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.SelectAllLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.TextFieldFilterLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.TextFieldListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.input.ValidationEnabledLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.linklabel.UrlLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.listview.FooterLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.listview.HeaderLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.listview.ItemClickListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.menu.MenuBarListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.menu.MenuItemGenerateDisabledImageLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.menu.MenuItemShortcutLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.menu.PopupMenuListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.picker.CloseAfterPickingLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.picker.ColorPickerListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.picker.ColorPickerResponsiveListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.picker.basic.AllowAlphaEditLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.picker.basic.BasicColorPickerListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.picker.basic.ShowHexFieldLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.spinner.*;
import com.github.czyzby.lml.vis.parser.impl.attribute.split.MaxSplitLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.split.MinSplitLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.split.SplitAmountLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.AttachDefaultTabListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.OnAllTabsRemovalLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.OnTabRemoveLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.OnTabSwitchLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.TabDeselectLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.TabHidingActionLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.TabListenerLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.TabSelectedLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.TabShowingActionLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.OnTabDisposeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.OnTabHideLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.OnTabSaveLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.OnTabShowLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.TabCloseableLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.TabDirtyLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.TabDisableLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.TabSavableLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tabbed.tab.TabTitleLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.table.PrefHeightLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.table.PrefSizeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.table.PrefWidthLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.table.UseCellDefaultsLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tooltip.DelayLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tooltip.MouseMoveFadeOutLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.tooltip.TooltipFadeTimeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.CustomValidatorLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.ErrorMessageLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.GreaterOrEqualLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.GreaterThanLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.HideOnEmptyInputLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.LesserOrEqualLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.LesserThanLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.form.DisableOnFormErrorLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.form.ErrorMessageLabelLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.form.FormSuccessMessageLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.form.RequireCheckedLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.form.RequireUncheckedLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.validator.form.TreatDisabledFieldsAsValidLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.window.AddCloseButtonLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.window.CloseOnEscapeLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.window.KeepWithinParentLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.attribute.window.OnResultLmlAttribute;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.*;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.spinner.ArraySpinnerLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.spinner.FloatSpinnerLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.spinner.IntSpinnerLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.spinner.SimpleFloatSpinnerLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.validator.CustomValidatorLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.validator.FloatValidatorLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.validator.GreaterThanValidatorLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.validator.IntegerValidatorLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.validator.LesserThanValidatorLmlTagProvider;
import com.github.czyzby.lml.vis.parser.impl.tag.provider.validator.NotEmptyValidatorLmlTagProvider;
import com.kotcrab.vis.ui.widget.VisDialog;

/** Replaces regular Scene2D actor tags with VisUI widgets. Supports the same core syntax - most tags from original LML
 * are either pointing to the same widgets or to VisUI equivalents, and all the attributes and macros you know from LML
 * are also supported. This syntax, however, adds extra tags and attributes of unique VisUI actors, that are simply
 * absent in regular Scene2D. See {@link #registerActorTags()} method source for all registered actor tags. Macro tags
 * are unchanged.
 *
 * @author MJ
 * @author Kotcrab */
public class VisLmlSyntax extends DefaultLmlSyntax {
    public VisLmlSyntax() {
        overrideTableExtractors();
    }

    /** Since some multi-table Vis widgets do not extend standard Scene2D widgets, table extractors from multi-table
     * actors need to be changed. */
    protected void overrideTableExtractors() {
        StandardTableTarget.MAIN.setTableExtractor(new TableExtractor() {
            @Override
            public Table extract(final Table table) {
                if (table instanceof Dialog) {
                    return ((Dialog) table).getContentTable();
                } else if (table instanceof VisDialog) {
                    return ((VisDialog) table).getContentTable();
                }
                return table;
            }
        });
        StandardTableTarget.BUTTON.setTableExtractor(new TableExtractor() {
            @Override
            public Table extract(final Table table) {
                if (table instanceof Dialog) {
                    return ((Dialog) table).getButtonTable();
                } else if (table instanceof VisDialog) {
                    return ((VisDialog) table).getButtonsTable();
                }
                return table;
            }
        });
    }

    @Override
    protected void registerActorTags() {
        // Standard Scene2D tags - abstract bases for Vis widgets or actors with no VisUI equivalents:
        addTagProvider(new ActorLmlTagProvider(), "actor");
        addTagProvider(new ActorStorageLmlTagProvider(), "actorStorage", "isolate");
        addTagProvider(new ButtonGroupLmlTagProvider(), "buttonGroup", "buttonTable");
        addTagProvider(new ButtonLmlTagProvider(), "button");
        addTagProvider(new ContainerLmlTagProvider(), "container");
        addTagProvider(new HorizontalGroupLmlTagProvider(), "horizontalGroup");
        addTagProvider(new StackLmlTagProvider(), "stack");
        addTagProvider(new TooltipLmlTagProvider(), "tooltip"); // VisTooltipLmlTagProvider supports Vis tooltips.
        addTagProvider(new TouchpadLmlTagProvider(), "touchpad", "touch");
        addTagProvider(new VerticalGroupLmlTagProvider(), "verticalGroup");

        // LML unique actors:
        addTagProvider(new AnimatedImageLmlTagProvider(), "animatedImage");

        // Vis actor equivalents:
        addTagProvider(new VisCheckBoxLmlTagProvider(), "checkBox", "visCheckBox");
        addTagProvider(new VisDialogLmlTagProvider(), "dialog", "visDialog");
        addTagProvider(new VisImageButtonLmlTagProvider(), "imageButton", "visImageButton");
        addTagProvider(new VisImageLmlTagProvider(), "image", "visImage");
        addTagProvider(new VisImageTextButtonLmlTagProvider(), "imageTextButton", "visImageTextButton");
        addTagProvider(new VisLabelLmlTagProvider(), "label", "visLabel");
        addTagProvider(new VisListLmlTagProvider(), "list", "visList");
        addTagProvider(new VisProgressBarLmlTagProvider(), "progressBar", "visProgressBar");
        addTagProvider(new VisRadioButtonLmlTagProvider(), "radioButton", "visRadioButton");
        addTagProvider(new VisScrollPaneLmlTagProvider(), "scrollPane", "visScrollPane");
        addTagProvider(new VisSelectBoxLmlTagProvider(), "selectBox", "visSelectBox");
        addTagProvider(new VisSliderLmlTagProvider(), "slider", "visSlider");
        addTagProvider(new VisSplitPaneLmlTagProvider(), "splitPane", "visSplitPane");
        addTagProvider(new VisTableLmlTagProvider(), "table", "visTable");
        addTagProvider(new VisTextAreaLmlTagProvider(), "textArea", "visTextArea");
        addTagProvider(new VisTextButtonLmlTagProvider(), "textButton", "visTextButton");
        addTagProvider(new VisTextFieldLmlTagProvider(), "textField", "visTextField");
        addTagProvider(new VisTreeLmlTagProvider(), "tree", "visTree");
        addTagProvider(new VisWindowLmlTagProvider(), "window", "visWindow");

        // Vis unique actors:
        addTagProvider(new BasicColorPickerLmlTagProvider(), "basicColorPicker", "basicPicker");
        addTagProvider(new BusyBarLmlTagProvider(), "busyBar");
        addTagProvider(new CollapsibleWidgetLmlTagProvider(), "collapsible", "verticalCollapsible",
                "collapsibleWidget");
        addTagProvider(new ColorPickerLmlTagProvider(), "colorPicker");
        addTagProvider(new DraggableLmlTagProvider(), "drag", "draggable");
        addTagProvider(new DragPaneLmlTagProvider(), "dragPane");
        addTagProvider(new ExtendedColorPickerLmlTagProvider(), "extendedColorPicker", "extendedPicker");
        addTagProvider(new FloatingGroupLmlTagProvider(), "floatingGroup");
        addTagProvider(new FormValidatorLmlTagProvider(), "form", "formValidator", "formTable");
        addTagProvider(new GridGroupLmlTagProvider(), "gridGroup", "grid");
        addTagProvider(new HighlightTextAreaLmlTagProvider(), "highlightTextArea");
        addTagProvider(new HorizontalCollapsibleWidgetLmlTagProvider(), "horizontalCollapsible",
                "horizontalCollapsibleWidget");
        addTagProvider(new HorizontalFlowGroupLmlTagProvider(), "horizontalFlow", "horizontalFlowGroup");
        addTagProvider(new LinkLabelLmlTagProvider(), "linkLabel", "link");
        addTagProvider(new ListViewLmlTagProvider(), "listView");
        addTagProvider(new MenuBarLmlTagProvider(), "menuBar");
        addTagProvider(new MenuItemLmlTagProvider(), "menuItem");
        addTagProvider(new MenuLmlTagProvider(), "menu");
        addTagProvider(new MenuPopupLmlTagProvider(), "popupMenu", "subMenu");
        addTagProvider(new MenuSeparatorLmlTagProvider(), "menuSeparator");
        addTagProvider(new MultiSplitPaneLmlTagProvider(), "multiSplitPane");
        addTagProvider(new ScrollableTextAreaLmlTagProvider(), "scrollableTextArea");
        addTagProvider(new SeparatorLmlTagProvider(), "separator");
        addTagProvider(new TabbedPaneLmlTagProvider(), "tabbedPane");
        addTagProvider(new TabLmlTagProvider(), "tab");
        addTagProvider(new ToastLmlTagProvider(), "toast");
        addTagProvider(new VerticalFlowGroupLmlTagProvider(), "verticalFlow", "verticalFlowGroup");
        addTagProvider(new VisTooltipLmlTagProvider(), "visTooltip");
        addTagProvider(new VisValidatableTextFieldLmlTagProvider(), "validatable", "validatableTextField",
                "visValidatableTextField");

        // Vis spinners:
        addTagProvider(new ArraySpinnerLmlTagProvider(), "arraySpinner");
        addTagProvider(new FloatSpinnerLmlTagProvider(), "floatSpinner", "spinner");
        addTagProvider(new IntSpinnerLmlTagProvider(), "intSpinner");
        addTagProvider(new SimpleFloatSpinnerLmlTagProvider(), "simpleFloatSpinner");

        // Vis validators:
        addTagProvider(new CustomValidatorLmlTagProvider(), "validator", "customValidator");
        addTagProvider(new FloatValidatorLmlTagProvider(), "floatValidator", "isFloat");
        addTagProvider(new GreaterThanValidatorLmlTagProvider(), "greaterThan", "greaterThanValidator");
        addTagProvider(new IntegerValidatorLmlTagProvider(), "integerValidator", "isInt", "isInteger");
        addTagProvider(new LesserThanValidatorLmlTagProvider(), "lesserThan", "lesserThanValidator");
        addTagProvider(new NotEmptyValidatorLmlTagProvider(), "notEmptyValidator", "isNotEmpty");

        // Note: other, GWT incompatible VisUI utilities can be registered with ExtendedVisLml.
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        registerVisAttributes();
    }

    /** Registers attributes of VisUI-specific actors. */
    protected void registerVisAttributes() {
        registerCollapsibleWidgetAttributes();
        registerColorPickerAttributes();
        registerDraggableAttributes();
        registerDragPaneAttributes();
        registerFloatingGroupAttributes();
        registerFlowGroupsAttributes();
        registerGridGroupAttributes();
        registerMenuAttributes();
        registerLinkLabelAttributes();
        registerListViewAttributes();
        registerSpinnerAttributes();
        registerTabbedPaneAttributes();
        registerValidatableTextFieldAttributes();
        registerValidatorAttributes();
    }

    // Extra common attributes:

    @Override
    protected void registerBuildingAttributes() {
        super.registerBuildingAttributes();
        // DragPaneLmlActorBuilder:
        addBuildingAttributeProcessor(new GroupTypeLmlAttribute(), "type");
        // VisWindowLmlActorBuilder:
        addBuildingAttributeProcessor(new ShowWindowBorderLmlAttribute(), "showBorder", "showWindowBorder");
        // MenuItemLmlActorBuilder:
        addBuildingAttributeProcessor(new MenuItemImageLmlAttribute(), "icon", "image", "drawable");
        // ListViewLmlActorBuilder:
        addBuildingAttributeProcessor(new ListAdapterLmlAttribute(), "adapter", "listAdapter");
        // IntRangeLmlActorBuilder:
        addBuildingAttributeProcessor(new IntMaxLmlAttribute(), "max");
        addBuildingAttributeProcessor(new IntMinLmlAttribute(), "min");
        addBuildingAttributeProcessor(new IntStepLmlAttribute(), "step");
        addBuildingAttributeProcessor(new IntValueLmlAttribute(), "value");
        // StringRangeLmlActorBuilder:
        addBuildingAttributeProcessor(new StringMaxLmlAttribute(), "max");
        addBuildingAttributeProcessor(new StringMinLmlAttribute(), "min");
        addBuildingAttributeProcessor(new StringStepLmlAttribute(), "step");
        addBuildingAttributeProcessor(new StringValueLmlAttribute(), "value");
    }

    @Override
    protected void registerCommonAttributes() {
        super.registerCommonAttributes();
        addAttributeProcessor(new VisTooltipLmlAttribute(), "visTooltip");
        // BorderOwner:
        addAttributeProcessor(new FocusBorderEnabledLmlAttribute(), "focusBorder", "focusBorderEnabled");
        // Actor (ColorPicker attachment):
        addAttributeProcessor(new ColorPickerLmlAttribute(), "colorPicker");
        addAttributeProcessor(new ResponsiveColorPickerLmlAttribute(), "responsiveColorPicker");
    }

    @Override
    protected void registerTooltipAttributes() {
        super.registerTooltipAttributes();
        // Tooltip (VisUI pre-LibGDX 1.6.5 implementation):
        addAttributeProcessor(new DelayLmlAttribute(), "delay", "appearDelay");
        addAttributeProcessor(new MouseMoveFadeOutLmlAttribute(), "mouseMoveFadeOut");
        addAttributeProcessor(new TooltipFadeTimeLmlAttribute(), "fadeTime", "fadingTime");
    }

    // Scene2D equivalents extra attributes:

    @Override
    protected void registerButtonAttributes() {
        super.registerButtonAttributes();
        // VisImageButton:
        addAttributeProcessor(new ButtonImageLmlAttribute(), "image", "icon");
        addAttributeProcessor(new ImageButtonGenerateDisabledLmlAttribute(), "generateDisabled",
                "generateDisabledImage");
        // VisImageTextButton:
        addAttributeProcessor(new TextButtonImageLmlAttribute(), "image", "icon");
    }

    @Override
    protected void registerDialogAttributes() {
        super.registerDialogAttributes();
        // VisDialog children:
        addAttributeProcessor(new OnResultLmlAttribute(), "result", "onResult", "onDialogResult");
    }

    @Override
    protected void registerSplitPaneAttributes() {
        // VisSplitPane:
        addAttributeProcessor(new MaxSplitLmlAttribute(), "max", "maxSplit", "maxSplitAmount");
        addAttributeProcessor(new MinSplitLmlAttribute(), "min", "minSplit", "minSplitAmount");
        addAttributeProcessor(new SplitAmountLmlAttribute(), "split", "splitAmount", "value");
    }

    @Override
    protected void registerTableAttributes() {
        super.registerTableAttributes();
        // Table:
        addAttributeProcessor(new UseCellDefaultsLmlAttribute(), "useCellDefaults", "useVisDefaults");
    }

    @Override
    protected void registerCellAttributes() {
        super.registerCellAttributes();
        // Updated pref size attributes:
        addAttributeProcessor(new PrefHeightLmlAttribute(), "prefHeight");
        addAttributeProcessor(new PrefSizeLmlAttribute(), "prefSize");
        addAttributeProcessor(new PrefWidthLmlAttribute(), "prefWidth");
    }

    @Override
    protected void registerTextFieldAttributes() {
        // VisTextField:
        addAttributeProcessor(new BlinkTimeLmlAttribute(), "blink", "blinkTime");
        addAttributeProcessor(new CursorLmlAttribute(), "cursor", "cursorPos", "cursorPosition");
        addAttributeProcessor(new DigitsOnlyLmlAttribute(), "digitsOnly", "numeric");
        addAttributeProcessor(new EnterKeyFocusTraversalLmlAttribute(), "enterKeyFocusTraversal");
        addAttributeProcessor(new IgnoreEqualsTextChangeLmlAttribute(), "ignoreEqualsTextChange");
        addAttributeProcessor(new InputAlignLmlAttribute(), "textAlign", "inputAlign", "textAlignment");
        addAttributeProcessor(new MaxLengthLmlAttribute(), "max", "maxLength");
        addAttributeProcessor(new MessageLmlAttribute(), "message", "messageText");
        addAttributeProcessor(new PasswordCharacterLmlAttribute(), "passwordCharacter", "passCharacter");
        addAttributeProcessor(new PasswordModeLmlAttribute(), "passwordMode", "password");
        addAttributeProcessor(new ReadOnlyLmlAttribute(), "readOnly");
        addAttributeProcessor(new SelectAllLmlAttribute(), "selectAll");
        addAttributeProcessor(new TextFieldFilterLmlAttribute(), "filter", "textFilter", "textFieldFilter");
        addAttributeProcessor(new TextFieldListenerLmlAttribute(), "listener", "textListener", "textFieldListener");
        // VisTextArea:
        addAttributeProcessor(new PrefRowsLmlAttribute(), "prefRows", "prefRowsAmount");
        // HighlightTextArea:
        addAttributeProcessor(new HighlighterLmlAttribute(), "highlighter");
    }

    @Override
    protected void registerWindowAttributes() {
        super.registerWindowAttributes();
        // VisWindow:
        addAttributeProcessor(new AddCloseButtonLmlAttribute(), "closeButton", "addCloseButton");
        addAttributeProcessor(new CloseOnEscapeLmlAttribute(), "closeOnEscape");
        addAttributeProcessor(new KeepWithinParentLmlAttribute(), "keepWithinParent");
    }

    // Unique Vis actors attributes:

    /** CollapsibleWidget attributes. */
    protected void registerCollapsibleWidgetAttributes() {
        addAttributeProcessor(new CollapsedLmlAttribute(), "collapse", "collapsed");
        addAttributeProcessor(new HorizontalCollapsedLmlAttribute(), "collapse", "collapsed");
    }

    /** ColorPicker attributes. */
    protected void registerColorPickerAttributes() {
        addAttributeProcessor(new CloseAfterPickingLmlAttribute(), "closeAfterPickingFinished", "closeAfter");
        addAttributeProcessor(new ColorPickerListenerLmlAttribute(), "listener");
        addAttributeProcessor(new ColorPickerResponsiveListenerLmlAttribute(), "responsiveListener");
        // BasicColorPicker:
        addAttributeProcessor(new AllowAlphaEditLmlAttribute(), "allowAlphaEdit", "allowAlpha");
        addAttributeProcessor(new BasicColorPickerListenerLmlAttribute(), "listener");
        addAttributeProcessor(new ShowHexFieldLmlAttribute(), "showHex", "showHexField");
    }

    /** Draggable listener attributes. */
    protected void registerDraggableAttributes() {
        addAttributeProcessor(new BlockInputLmlAttribute(), "blockInput");
        addAttributeProcessor(new DeadzoneRadiusLmlAttribute(), "deadzone", "deadzoneRadius");
        addAttributeProcessor(new DraggedAlphaLmlAttribute(), "alpha");
        addAttributeProcessor(new DraggedFadingInterpolationLmlAttribute(), "fadingInterpolation");
        addAttributeProcessor(new DraggedFadingTimeLmlAttribute(), "fadingTime");
        addAttributeProcessor(new DraggedMovingInterpolationLmlAttribute(), "movingInterpolation");
        addAttributeProcessor(new DraggedMovingTimeLmlAttribute(), "movingTime");
        addAttributeProcessor(new DragListenerLmlAttribute(), "listener");
        addAttributeProcessor(new InvisibleWhenDraggedLmlAttribute(), "invisible", "invisibleWhenDragged");
        addAttributeProcessor(new KeepDraggedWithinParentLmlAttribute(), "keepWithinParent");
    }

    /** DragPane attributes. */
    protected void registerDragPaneAttributes() {
        addAttributeProcessor(new AcceptForeignLmlAttribute(), "foreign", "acceptForeign");
        addAttributeProcessor(new DragPaneListenerLmlAttribute(), "listener");
        addAttributeProcessor(new GroupIdLmlAttribute(), "groupId");
        addAttributeProcessor(new MaxChildrenLmlAttribute(), "maxChildren");
    }

    /** Floating group attributes. */
    protected void registerFloatingGroupAttributes() {
        addAttributeProcessor(new UseChildrenPreferredSizeLmlAttribute(), "useChildrenPreferredSize", "usePref");
    }

    /** Flow groups attributes. */
    protected void registerFlowGroupsAttributes() {
        // HorizontalFlowGroup:
        addAttributeProcessor(new HorizontalSpacingLmlAttribute(), "spacing");
        // VerticalFlowGroup:
        addAttributeProcessor(new VerticalSpacingLmlAttribute(), "spacing");
    }

    /** GridGroup attributes. */
    protected void registerGridGroupAttributes() {
        addAttributeProcessor(new GridSpacingLmlAttribute(), "spacing");
        addAttributeProcessor(new ItemHeightLmlAttribute(), "itemHeight");
        addAttributeProcessor(new ItemSizeLmlAttribute(), "itemSize");
        addAttributeProcessor(new ItemWidthLmlAttribute(), "itemWidth");
        // FixedSizeGridGroup:
        addAttributeProcessor(new BlockIndexesLmlAttribute(), "blockIndexes");
        addAttributeProcessor(new ItemsAmountLmlAttribute(), "itemsAmount");
    }

    /** Menu-related attributes. */
    protected void registerMenuAttributes() {
        // PopupMenu:
        addAttributeProcessor(new PopupMenuListenerLmlAttribute(), "menuListener");
        // MenuBar:
        addAttributeProcessor(new MenuBarListenerLmlAttribute(), "menuListener");
        // MenuItem:
        addAttributeProcessor(new MenuItemGenerateDisabledImageLmlAttribute(), "generateDisabled");
        addAttributeProcessor(new MenuItemShortcutLmlAttribute(), "shortcut");
    }

    /** LinkLabel attributes. */
    protected void registerLinkLabelAttributes() {
        addAttributeProcessor(new UrlLmlAttribute(), "url", "href");
    }

    /** ListView attributes. */
    protected void registerListViewAttributes() {
        // ListView children:
        addAttributeProcessor(new FooterLmlAttribute(), "footer");
        addAttributeProcessor(new HeaderLmlAttribute(), "header");
        // ListView attributes:
        addAttributeProcessor(new ItemClickListenerLmlAttribute(), "itemListener", "itemClickListener");
    }

    /** Spinner attributes. */
    protected void registerSpinnerAttributes() {
        addAttributeProcessor(new SpinnerArrayLmlAttribute(), "items");
        addAttributeProcessor(new SpinnerDisabledLmlAttribute(), "disabled", "inputDisabled");
        addAttributeProcessor(new SpinnerNameLmlAttribute(), "selectorName", "text");
        addAttributeProcessor(new SpinnerPrecisionLmlAttribute(), "precision", "scale");
        addAttributeProcessor(new SpinnerProgrammaticChangeEventsLmlAttribute(), "programmaticChangeEvents");
        addAttributeProcessor(new SpinnerSelectedLmlAttribute(), "selected");
        addAttributeProcessor(new SpinnerTextFieldEventPolicyLmlAttribute(), "textFieldEventPolicy");
        addAttributeProcessor(new SpinnerWrapLmlAttribute(), "wrap");
    }

    /** TabbedPane (and its tab children) attributes. */
    protected void registerTabbedPaneAttributes() {
        // TabbedPane (pane's main table with TabbedPane attached):
        addAttributeProcessor(new AttachDefaultTabListenerLmlAttribute(), "defaultListener", "attachDefaultListener");
        addAttributeProcessor(new OnAllTabsRemovalLmlAttribute(), "onAllRemoved", "onAllTabsRemoved", "onClear",
                "onTabsClear");
        addAttributeProcessor(new OnTabRemoveLmlAttribute(), "onRemove", "onTabRemove");
        addAttributeProcessor(new OnTabSwitchLmlAttribute(), "onSwitch", "onTabSwitch");
        addAttributeProcessor(new TabDeselectLmlAttribute(), "allowTabDeselect", "tabDeselect");
        addAttributeProcessor(new TabHidingActionLmlAttribute(), "tabHideAction");
        addAttributeProcessor(new TabListenerLmlAttribute(), "tabListener", "tabbedPaneListener");
        addAttributeProcessor(new TabSelectedLmlAttribute(), "selected", "selectedTab");
        addAttributeProcessor(new TabShowingActionLmlAttribute(), "tabShowAction");
        // Tab (VisTabTable):
        addAttributeProcessor(new OnTabDisposeLmlAttribute(), "onDispose", "onTabDispose", "onRemove", "onTabRemove");
        addAttributeProcessor(new OnTabHideLmlAttribute(), "onTabHide");
        addAttributeProcessor(new OnTabSaveLmlAttribute(), "onSave", "onTabSave");
        addAttributeProcessor(new OnTabShowLmlAttribute(), "onTabShow");
        addAttributeProcessor(new TabCloseableLmlAttribute(), "closeable", "closeableByUser");
        addAttributeProcessor(new TabDirtyLmlAttribute(), "dirty");
        addAttributeProcessor(new TabDisableLmlAttribute(), "disable", "disabled");
        addAttributeProcessor(new TabSavableLmlAttribute(), "savable");
        addAttributeProcessor(new TabTitleLmlAttribute(), "title", "name", "tabTitle", "tabName");
    }

    /** VisValidatableTextField attributes. */
    protected void registerValidatableTextFieldAttributes() {
        addAttributeProcessor(new RestoreLastValidLmlAttribute(), "restore", "restoreLastValid");
        addAttributeProcessor(new ValidationEnabledLmlAttribute(), "enabled", "validate", "validationEnabled");
    }

    /** InputValidator implementations' attributes. */
    protected void registerValidatorAttributes() {
        // CustomValidator:
        addAttributeProcessor(new CustomValidatorLmlAttribute(), "validator", "validate", "method", "action", "check");
        // FormInputValidator:
        addAttributeProcessor(new ErrorMessageLmlAttribute(), "error", "errorMsg", "errorMessage", "formError");
        addAttributeProcessor(new HideOnEmptyInputLmlAttribute(), "hideOnEmpty", "hideErrorOnEmpty");
        // GreaterThanValidator:
        addAttributeProcessor(new GreaterOrEqualLmlAttribute(), "orEqual", "allowEqual", "greaterOrEqual");
        addAttributeProcessor(new GreaterThanLmlAttribute(), "value", "min", "greaterThan");
        // LesserThanValidator:
        addAttributeProcessor(new LesserOrEqualLmlAttribute(), "orEqual", "allowEqual", "lesserOrEqual");
        addAttributeProcessor(new LesserThanLmlAttribute(), "value", "max", "lesserThan");
        // FormValidator (VisFormTable):
        addAttributeProcessor(new FormSuccessMessageLmlAttribute(), "success", "successMsg", "successMessage");
        addAttributeProcessor(new TreatDisabledFieldsAsValidLmlAttribute(), "treatDisabledFieldsAsValid",
                "disabledValid");
        // FormValidator children:
        addAttributeProcessor(new DisableOnFormErrorLmlAttribute(), "disableOnError", "disableOnFormError",
                "formDisable");
        addAttributeProcessor(new ErrorMessageLabelLmlAttribute(), "errorMessage", "errorLabel", "errorMsgLabel",
                "errorMessageLabel");
        addAttributeProcessor(new RequireCheckedLmlAttribute(), "requireChecked", "formChecked", "notCheckedError",
                "uncheckedError");
        addAttributeProcessor(new RequireUncheckedLmlAttribute(), "requireUnchecked", "requireNotChecked",
                "formUnchecked", "checkedError");
    }
}
