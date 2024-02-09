/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.action;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.action.impl.*;
import net.mcreator.ui.action.impl.gradle.*;
import net.mcreator.ui.action.impl.workspace.*;
import net.mcreator.ui.action.impl.workspace.resources.*;
import net.mcreator.ui.browser.action.*;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.dialogs.imageeditor.NewImageDialog;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.dialogs.tools.*;
import net.mcreator.ui.ide.action.*;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.AnimationMakerView;
import net.mcreator.ui.views.ArmorImageMakerView;
import net.mcreator.ui.views.editor.image.action.*;
import net.mcreator.ui.views.editor.image.tool.action.*;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ActionRegistry {

	private final List<Action> actionList = new ArrayList<>();

	private final MCreator mcreator;
	private AcceleratorMap acceleratorMap = null;

	// MCreator website and community related actions
	public final BasicAction mcreatorWebsite;
	public final BasicAction mcreatorCommunity;
	public final BasicAction mcreatorPublish;
	public final BasicAction donate;

	// General actions
	public final BasicAction preferences;
	public final BasicAction closeWorkspace;
	public final BasicAction exitMCreator;

	// File menu
	public final BasicAction openWorkspace;
	public final BasicAction newWorkspace;
	public final BasicAction importWorkspace;

	// Help actions
	public final BasicAction aboutMCreator;
	public final BasicAction checkForUpdates;
	public final BasicAction checkForPluginUpdates;
	public final BasicAction showShortcuts;
	public final BasicAction help;
	public final BasicAction support;
	public final BasicAction knowledgeBase;

	// Gradle related actions
	public final BasicAction buildWorkspace;
	public final BasicAction runClient;
	public final BasicAction debugClient;
	public final BasicAction runServer;
	public final BasicAction runGradleTask;
	public final BasicAction buildGradleOnly;
	public final BasicAction reloadGradleProject;
	public final BasicAction clearAllGradleCaches;
	public final BasicAction cancelGradleTaskAction;
	public final BasicAction regenerateCode;

	// Workspace related actions
	public final BasicAction exportToJAR;
	public final BasicAction exportToDeobfJAR;
	public final BasicAction workspaceSettings;
	public final BasicAction exportWorkspaceToZIP;
	public final BasicAction exportWorkspaceToZIPWithRunDir;
	public final BasicAction openWorkspaceFolder;
	public final BasicAction setCreativeTabItemOrder;

	// IDE actions
	public final BasicAction openFile;
	public final BasicAction openFileInDesktop;
	public final BasicAction showFileInExplorer;
	public final BasicAction deleteFile;
	public final BasicAction newClass;
	public final BasicAction newJson;
	public final BasicAction newImage;
	public final BasicAction newPackage;
	public final BasicAction newFolder;
	public final BasicAction showFindBar;
	public final BasicAction showReplaceBar;
	public final BasicAction reformatCodeAndImports;
	public final BasicAction reformatCodeOnly;
	public final BasicAction saveCode;
	public final BasicAction reloadCode;

	// Resource actions
	public final BasicAction createMCItemTexture;
	public final BasicAction createArmorTexture;
	public final BasicAction createAnimatedTexture;
	public final BasicAction importBlockTexture;
	public final BasicAction importItemTexture;
	public final BasicAction importEntityTexture;
	public final BasicAction importEffectTexture;
	public final BasicAction importParticleTexture;
	public final BasicAction importScreenTexture;
	public final BasicAction importArmorTexture;
	public final BasicAction importOtherTexture;
	public final BasicAction importSound;
	public final BasicAction importStructure;
	public final BasicAction importStructureFromMinecraft;
	public final BasicAction importJavaModel;
	public final BasicAction importJSONModel;
	public final BasicAction importOBJModel;

	// Window actions
	public final BasicAction showWorkspaceBrowser;
	public final BasicAction hideWorkspaceBrowser;
	public final BasicAction showConsoleTab;
	public final BasicAction showWorkspaceTab;
	public final BasicAction closeAllTabs;
	public final BasicAction closeCurrentTab;

	// Tools actions
	public final BasicAction openMaterialPackMaker;
	public final BasicAction openOrePackMaker;
	public final BasicAction openToolPackMaker;
	public final BasicAction openArmorPackMaker;
	public final BasicAction openWoodPackMaker;
	public final BasicAction showEntityIDList;
	public final BasicAction showItemBlockList;
	public final BasicAction showParticleIDList;
	public final BasicAction showSoundsList;
	public final BasicAction showFuelBurnTimes;
	public final BasicAction showVanillaLootTables;
	public final BasicAction openJavaEditionFolder;
	public final BasicAction openBedrockEditionFolder;

	//Image Editor actions
	public final BasicAction imageEditorUndo;
	public final BasicAction imageEditorRedo;
	public final BasicAction imageEditorSave;
	public final BasicAction imageEditorSaveAs;
	public final ImageEditorCopyAction imageEditorCopy;
	public final ImageEditorCopyAllAction imageEditorCopyAll;
	public final ImageEditorCutAction imageEditorCut;
	public final ImageEditorPasteAction imageEditorPaste;
	public final ImageEditorDeleteAction imageEditorDelete;
	public final BasicAction imageEditorPencil;
	public final BasicAction imageEditorLine;
	public final BasicAction imageEditorShape;
	public final BasicAction imageEditorEraser;
	public final BasicAction imageEditorStamp;
	public final BasicAction imageEditorFloodFill;
	public final BasicAction imageEditorColorPicker;
	public final BasicAction imageEditorColorize;
	public final BasicAction imageEditorDesaturate;
	public final BasicAction imageEditorHSVNoise;
	public final BasicAction imageEditorMoveLayer;
	public final BasicAction imageEditorSelectLayer;
	public final ImageEditorClearSelectionAction imageEditorClearSelection;
	public final BasicAction imageEditorResizeLayer;
	public final BasicAction imageEditorResizeCanvas;

	public ActionRegistry(MCreator mcreator) {
		this.mcreator = mcreator;

		// Gradle tasks are initialized here as they need mcreator reference
		this.buildWorkspace = new BuildWorkspaceAction(this).setIcon(UIRES.get("16px.build"));
		this.runClient = new RunClientAction(this).setIcon(UIRES.get("16px.runclient"));
		this.debugClient = new DebugClientAction(this).setIcon(UIRES.get("16px.debugclient"));
		this.runServer = new RunServerAction(this).setIcon(UIRES.get("16px.runserver"));
		this.runGradleTask = new RunGradleTaskAction(this);
		this.exportToJAR = new ExportWorkspaceForDistAction(this).setIcon(UIRES.get("16px.exporttojar"));
		this.exportToDeobfJAR = new ExportWorkspaceForDistAction.Deobf(this);
		this.workspaceSettings = new WorkspaceSettingsAction(this).setIcon(UIRES.get("16px.wrksett"));
		this.mcreatorWebsite = new VisitURIAction(this, L10N.t("action.mcreator_website"),
				MCreatorApplication.SERVER_DOMAIN);
		this.mcreatorCommunity = new VisitURIAction(this, L10N.t("action.mcreator_community"),
				MCreatorApplication.SERVER_DOMAIN + "/community");
		this.mcreatorPublish = new VisitURIAction(this, L10N.t("action.publish_modification"),
				MCreatorApplication.SERVER_DOMAIN + "/node/add/modification/");
		this.preferences = new BasicAction(this, L10N.t("action.preferences"),
				e -> new PreferencesDialog(mcreator, null)).setIcon(UIRES.get("settings"));
		this.exitMCreator = new BasicAction(this, L10N.t("action.exit"),
				e -> mcreator.getApplication().closeApplication());
		this.aboutMCreator = new AboutAction(this);
		this.checkForUpdates = new CheckForUpdatesAction(this);
		this.checkForPluginUpdates = new CheckForPluginUpdatesAction(this);
		this.help = new VisitURIAction(this, L10N.t("action.wiki"), MCreatorApplication.SERVER_DOMAIN + "/wiki");
		this.support = new VisitURIAction(this, L10N.t("action.support"),
				MCreatorApplication.SERVER_DOMAIN + "/support");
		this.openFile = new BasicAction(this, L10N.t("workspace_file_browser.open"),
				e -> mcreator.getProjectBrowser().openSelectedFile(true)).setIcon(UIRES.get("16px.edit"));
		this.openFileInDesktop = new BasicAction(this, L10N.t("workspace_file_browser.open_desktop"),
				e -> mcreator.getProjectBrowser().openSelectedFileInDesktop());
		this.showFileInExplorer = new BasicAction(this, L10N.t("workspace_file_browser.show_in_explorer"),
				e -> mcreator.getProjectBrowser().showSelectedFileInDesktop()).setIcon(UIRES.get("16px.open"));
		this.deleteFile = new BasicAction(this, L10N.t("workspace_file_browser.remove_file"),
				e -> mcreator.getProjectBrowser().deleteSelectedFile()).setIcon(UIRES.get("16px.delete"));
		this.newClass = new NewClassAction(this);
		this.newJson = new NewJsonFileAction(this);
		this.newImage = new NewImageFileAction(this);
		this.newPackage = new NewPackageAction(this);
		this.newFolder = new NewFolderAction(this);
		this.showFindBar = new ShowFindAction(this);
		this.showReplaceBar = new ShowReplaceAction(this);
		this.reformatCodeAndImports = new ReformatCodeAndImportsAction(this).setIcon(UIRES.get("16px.reformatcode"));
		this.reformatCodeOnly = new ReformatCodeAction(this);
		this.saveCode = new SaveCodeAction(this);
		this.reloadCode = new ReloadCodeAction(this);
		this.buildGradleOnly = new BuildGradleOnlyAction(this);
		this.reloadGradleProject = new ReloadGradleProjectAction(this);
		this.clearAllGradleCaches = new ClearAllGradleCachesAction(this);
		this.cancelGradleTaskAction = new CancelGradleTaskAction(this);
		this.createMCItemTexture = new TextureAction(this, L10N.t("action.create_texture"), actionEvent -> {
			NewImageDialog newImageDialog = new NewImageDialog(mcreator);
			newImageDialog.setVisible(true);
		}).setIcon(UIRES.get("16px.newtexture"));
		this.createArmorTexture = new TextureAction(this, L10N.t("action.create_armor_texture"),
				actionEvent -> new ArmorImageMakerView(mcreator).showView(), TextureType.ARMOR).setIcon(
				UIRES.get("16px.newarmor"));
		this.createAnimatedTexture = new TextureAction(this, L10N.t("action.create_animated_texture"),
				actionEvent -> new AnimationMakerView(mcreator).showView()).setIcon(UIRES.get("16px.newanimation"));
		this.importBlockTexture = new TextureImportAction(this, L10N.t("action.import_block_texture"),
				TextureType.BLOCK).setIcon(UIRES.get("16px.importblock"));
		this.importItemTexture = new TextureImportAction(this, L10N.t("action.import_item_texture"),
				TextureType.ITEM).setIcon(UIRES.get("16px.importitem"));
		this.importEntityTexture = new TextureImportAction(this, L10N.t("action.import_entity_texture"),
				TextureType.ENTITY).setIcon(UIRES.get("16px.importentity"));
		this.importEffectTexture = new TextureImportAction(this, L10N.t("action.import_effect_texture"),
				TextureType.EFFECT).setIcon(UIRES.get("16px.importeffect"));
		this.importParticleTexture = new TextureImportAction(this, L10N.t("action.import_particle_texture"),
				TextureType.PARTICLE).setIcon(UIRES.get("16px.importparticle"));
		this.importScreenTexture = new TextureImportAction(this, L10N.t("action.import_screen_texture"),
				TextureType.SCREEN).setIcon(UIRES.get("16px.importgui"));
		this.importArmorTexture = new TextureAction(this, L10N.t("action.import_armor_texture"), actionEvent -> {
			TextureImportDialogs.importArmor(mcreator);
			mcreator.mv.resourcesPan.workspacePanelTextures.reloadElements();
		}, TextureType.ARMOR).setIcon(UIRES.get("16px.importarmor"));
		this.importOtherTexture = new TextureImportAction(this, L10N.t("action.import_other_texture"),
				TextureType.OTHER).setIcon(UIRES.get("16px.importtexture"));
		this.importSound = new SoundImportAction(this);
		this.importStructure = new StructureImportActions.ImportStructure(this).setIcon(
				UIRES.get("16px.importstructure"));
		this.importStructureFromMinecraft = new StructureImportActions.ImportStructureFromMinecraft(this);
		this.importJavaModel = new ModelImportActions.JAVA(this).setIcon(UIRES.get("16px.importjavamodel"));
		this.importJSONModel = new ModelImportActions.JSON(this).setIcon(UIRES.get("16px.importjsonmodel"));
		this.importOBJModel = new ModelImportActions.OBJ(this).setIcon(UIRES.get("16px.importobjmodel"));
		this.closeWorkspace = new BasicAction(this, L10N.t("action.workspace.close"),
				e -> mcreator.closeThisMCreator(mcreator.getApplication().getOpenMCreators().size() <= 1));
		this.regenerateCode = new RegenerateCodeAction(this);
		this.exportWorkspaceToZIP = new ExportWorkspaceToZIPAction(this);
		this.exportWorkspaceToZIPWithRunDir = new ExportWorkspaceToZIPAction.WithRunDir(this);
		this.showConsoleTab = new BasicAction(this, L10N.t("action.show_console"),
				e -> mcreator.mcreatorTabs.showTab(mcreator.consoleTab));
		this.showWorkspaceTab = new BasicAction(this, L10N.t("action.show_workspace"),
				e -> mcreator.mcreatorTabs.showTab(mcreator.workspaceTab));
		this.closeAllTabs = new BasicAction(this, L10N.t("action.close_all_tabs"),
				e -> mcreator.mcreatorTabs.closeAllTabs());
		this.closeCurrentTab = new BasicAction(this, L10N.t("action.close_tab"),
				e -> mcreator.mcreatorTabs.closeTab(mcreator.mcreatorTabs.getCurrentTab()));
		this.showWorkspaceBrowser = new BasicAction(this, L10N.t("action.show_workspace_browser"),
				e -> mcreator.splitPane.setDividerLocation(280));
		this.hideWorkspaceBrowser = new BasicAction(this, L10N.t("action.hide_workspace_browser"),
				e -> mcreator.splitPane.setDividerLocation(0));
		this.openWorkspace = new OpenWorkspaceAction(this);
		this.newWorkspace = new NewWorkspaceAction(this);
		this.importWorkspace = new ImportWorkspaceAction(this);
		this.openWorkspaceFolder = new BasicAction(this, L10N.t("action.open_workspace_folder"),
				e -> DesktopUtils.openSafe(mcreator.getWorkspaceFolder()));
		this.openMaterialPackMaker = MaterialPackMakerTool.getAction(this);
		this.openOrePackMaker = OrePackMakerTool.getAction(this);
		this.openToolPackMaker = ToolPackMakerTool.getAction(this);
		this.openArmorPackMaker = ArmorPackMakerTool.getAction(this);
		this.openWoodPackMaker = WoodPackMakerTool.getAction(this);
		this.showShortcuts = new BasicAction(this, L10N.t("action.keyboard_shortcuts"),
				e -> AcceleratorDialog.showAcceleratorMapDialog(mcreator, this.acceleratorMap));
		this.showEntityIDList = new ShowDataListAction.EntityIDs(this);
		this.showItemBlockList = new ShowDataListAction.ItemBlockList(this);
		this.showParticleIDList = new ShowDataListAction.ParticeIDList(this);
		this.showSoundsList = new ShowDataListAction.SoundsList(this);
		this.showFuelBurnTimes = new ShowDataListAction.FuelBurnTimes(this);
		this.showVanillaLootTables = new ShowDataListAction.VanillaLootTables(this);
		this.knowledgeBase = new VisitURIAction(this, L10N.t("action.knowledge_base"),
				MCreatorApplication.SERVER_DOMAIN + "/support/knowledgebase");
		this.setCreativeTabItemOrder = new EditTabOrderAction(this);
		this.donate = new VisitURIAction(this, L10N.t("action.donate"),
				MCreatorApplication.SERVER_DOMAIN + "/donate").setIcon(UIRES.get("donate"));
		this.openJavaEditionFolder = new MinecraftFolderActions.OpenJavaEditionFolder(this);
		this.openBedrockEditionFolder = new MinecraftFolderActions.OpenBedrockEditionFolder(this);

		//Image Editor actions
		this.imageEditorUndo = new ImageEditorUndoAction(this);
		this.imageEditorRedo = new ImageEditorRedoAction(this);
		this.imageEditorCopy = new ImageEditorCopyAction(this);
		this.imageEditorCopyAll = new ImageEditorCopyAllAction(this);
		this.imageEditorCut = new ImageEditorCutAction(this);
		this.imageEditorPaste = new ImageEditorPasteAction(this);
		this.imageEditorDelete = new ImageEditorDeleteAction(this);
		this.imageEditorSave = new ImageEditorSaveAction(this);
		this.imageEditorSaveAs = new ImageEditorSaveAsAction(this);
		this.imageEditorPencil = new PencilToolAction(this);
		this.imageEditorLine = new LineToolAction(this);
		this.imageEditorShape = new ShapeToolAction(this);
		this.imageEditorEraser = new EraserToolAction(this);
		this.imageEditorStamp = new StampToolAction(this);
		this.imageEditorFloodFill = new FloodFillToolAction(this);
		this.imageEditorColorPicker = new ColorPickerToolAction(this);
		this.imageEditorColorize = new ColorizeToolAction(this);
		this.imageEditorDesaturate = new DesaturateToolAction(this);
		this.imageEditorHSVNoise = new HSVNoiseToolAction(this);
		this.imageEditorMoveLayer = new MoveToolAction(this);
		this.imageEditorSelectLayer = new SelectionToolAction(this);
		this.imageEditorClearSelection = new ImageEditorClearSelectionAction(this);
		this.imageEditorResizeLayer = new ResizeToolAction(this);
		this.imageEditorResizeCanvas = new ResizeCanvasToolAction(this);

		this.acceleratorMap = new AcceleratorMap(this);
		this.acceleratorMap.registerAll();
	}

	void addAction(Action action) {
		actionList.add(action);
	}

	public List<Action> getActions() {
		return actionList;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

}
