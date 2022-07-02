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
import net.mcreator.ui.action.accelerators.AcceleratorsManager;
import net.mcreator.ui.action.accelerators.ActionAccelerator;
import net.mcreator.ui.action.impl.AboutAction;
import net.mcreator.ui.action.impl.CheckForUpdatesAction;
import net.mcreator.ui.action.impl.MinecraftFolderActions;
import net.mcreator.ui.action.impl.ShowDataListAction;
import net.mcreator.ui.action.impl.gradle.*;
import net.mcreator.ui.action.impl.vcs.*;
import net.mcreator.ui.action.impl.workspace.*;
import net.mcreator.ui.action.impl.workspace.resources.ImportSoundAction;
import net.mcreator.ui.action.impl.workspace.resources.ModelImportActions;
import net.mcreator.ui.action.impl.workspace.resources.StructureImportActions;
import net.mcreator.ui.action.impl.workspace.resources.TextureAction;
import net.mcreator.ui.browser.action.*;
import net.mcreator.ui.dialogs.AcceleratorDialog;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.dialogs.imageeditor.NewImageDialog;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.dialogs.tools.*;
import net.mcreator.ui.ide.action.*;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.AnimationMakerView;
import net.mcreator.ui.views.ArmorImageMakerView;
import net.mcreator.ui.views.editor.image.action.ImageEditorRedoAction;
import net.mcreator.ui.views.editor.image.action.ImageEditorSaveAction;
import net.mcreator.ui.views.editor.image.action.ImageEditorSaveAsAction;
import net.mcreator.ui.views.editor.image.action.ImageEditorUndoAction;
import net.mcreator.ui.views.editor.image.tool.action.*;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.DesktopUtils;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static net.mcreator.ui.action.accelerators.Accelerator.*;

public class ActionRegistry {

	private final List<Action> actionList = new ArrayList<>();

	private final MCreator mcreator;

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
	public final BasicAction showShortcuts;
	public final BasicAction help;
	public final BasicAction support;
	public final BasicAction knowledgeBase;

	// Gradle related actions
	public final BasicAction buildWorkspace;
	public final BasicAction runClient;
	public final BasicAction runServer;
	public final BasicAction runGradleTask;
	public final BasicAction buildClean;
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
	public final BasicAction injectDefaultTags;

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

	// VCS actions
	public final BasicAction setupVCS;
	public final BasicAction unlinkVCS;
	public final BasicAction setupVCSOrSettings;
	public final BasicAction syncToRemote;
	public final BasicAction syncFromRemote;
	public final BasicAction showUnsyncedChanges;
	public final BasicAction remoteWorkspaceSettings;

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
	public final BasicAction imageEditorResizeLayer;
	public final BasicAction imageEditorResizeCanvas;

	public ActionRegistry(MCreator mcreator) {
		this.mcreator = mcreator;

		// Gradle tasks are initialized here as they need mcreator reference
		this.buildWorkspace = new BuildWorkspaceAction(this).setIcon(UIRES.get("16px.build"));
		this.runClient = new RunClientAction(this).setIcon(UIRES.get("16px.runclient"));
		this.runServer = new RunServerAction(this).setIcon(UIRES.get("16px.runserver"));
		this.runGradleTask = new RunGradleTaskAction(this);
		this.buildClean = new GradleAction(this, L10N.t("action.gradle.clean_build"),
				e -> mcreator.getGradleConsole().exec("clean"), new ActionAccelerator("gradle.clean_build", KeyEvent.VK_B, CTRL));
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
				e -> new PreferencesDialog(mcreator, null), new ActionAccelerator("preferences", KeyEvent.VK_P, CTRL_SHIFT))
				.setIcon(UIRES.get("settings"));
		this.exitMCreator = new BasicAction(this, L10N.t("action.exit"),
				e -> mcreator.getApplication().closeApplication(), new ActionAccelerator("exit"));
		this.aboutMCreator = new AboutAction(this);
		this.checkForUpdates = new CheckForUpdatesAction(this);
		this.showShortcuts = new BasicAction(this, L10N.t("action.keyboard_shortcuts"),
				e -> new AcceleratorDialog(mcreator), new ActionAccelerator("keyboard_shortcuts", KeyEvent.VK_A, CTRL_ALT));
		this.help = new VisitURIAction(this, L10N.t("action.wiki"), MCreatorApplication.SERVER_DOMAIN + "/wiki");
		this.support = new VisitURIAction(this, L10N.t("action.support"),
				MCreatorApplication.SERVER_DOMAIN + "/support");
		this.openFile = new BasicAction(this, L10N.t("workspace_file_browser.open"),
				e -> mcreator.getProjectBrowser().openSelectedFile(true), new ActionAccelerator("workspace_file_browser.open"))
				.setIcon(UIRES.get("16px.edit.gif"));
		this.openFileInDesktop = new BasicAction(this, L10N.t("workspace_file_browser.open_desktop"),
				e -> mcreator.getProjectBrowser().openSelectedFileInDesktop(), new ActionAccelerator("workspace_file_browser.open_desktop"));
		this.showFileInExplorer = new BasicAction(this, L10N.t("workspace_file_browser.show_in_explorer"),
				e -> mcreator.getProjectBrowser().showSelectedFileInDesktop(), new ActionAccelerator("workspace_file_browser.show_in_explorer"))
				.setIcon(UIRES.get("16px.open.gif"));
		this.deleteFile = new BasicAction(this, L10N.t("workspace_file_browser.remove_file"),
				e -> mcreator.getProjectBrowser().deleteSelectedFile(), new ActionAccelerator("workspace_file_browser.remove_file"))
				.setIcon(UIRES.get("16px.delete.gif"));
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
		}, new ActionAccelerator("create_texture", KeyEvent.VK_9, CTRL_SHIFT)).setIcon(UIRES.get("16px.newtexture"));
		this.createArmorTexture = new TextureAction(this, L10N.t("action.create_armor_texture"),
				actionEvent -> new ArmorImageMakerView(mcreator).showView(), null);
		this.createAnimatedTexture = new TextureAction(this, L10N.t("action.create_animated_texture"),
				actionEvent -> new AnimationMakerView(mcreator).showView(), new ActionAccelerator("create_animated_texture",
				KeyEvent.VK_8, CTRL_SHIFT)).setIcon(UIRES.get("16px.newanimation"));
		this.importBlockTexture = new TextureAction(this, L10N.t("action.import_block_texture"),
				actionEvent -> TextureImportDialogs.importMultipleTextures(mcreator, TextureType.BLOCK), new ActionAccelerator("import_block_texture"))
				.setIcon(
				UIRES.get("16px.importblock"));
		this.importItemTexture = new TextureAction(this, L10N.t("action.import_item_texture"),
				actionEvent -> TextureImportDialogs.importMultipleTextures(mcreator, TextureType.ITEM), new ActionAccelerator("import_item_texture")).setIcon(
				UIRES.get("16px.importitem"));
		this.importEntityTexture = new TextureAction(this, L10N.t("action.import_entity_texture"),
				actionEvent -> TextureImportDialogs.importMultipleTextures(mcreator, TextureType.ENTITY));
		this.importEffectTexture = new TextureAction(this, L10N.t("action.import_effect_texture"),
				actionEvent -> TextureImportDialogs.importMultipleTextures(mcreator, TextureType.EFFECT));
		this.importParticleTexture = new TextureAction(this, L10N.t("action.import_particle_texture"),
				actionEvent -> TextureImportDialogs.importMultipleTextures(mcreator, TextureType.PARTICLE));
		this.importScreenTexture = new TextureAction(this, L10N.t("action.import_screen_texture"),
				actionEvent -> TextureImportDialogs.importMultipleTextures(mcreator, TextureType.SCREEN));
		this.importArmorTexture = new TextureAction(this, L10N.t("action.import_armor_texture"), actionEvent -> {
			TextureImportDialogs.importArmor(mcreator);
			mcreator.mv.resourcesPan.workspacePanelTextures.reloadElements();
		}, new ActionAccelerator("import_armor_texture"));
		this.importOtherTexture = new TextureAction(this, L10N.t("action.import_other_texture"),
				actionEvent -> TextureImportDialogs.importMultipleTextures(mcreator, TextureType.OTHER),
				new ActionAccelerator("import_other_texture", KeyEvent.VK_7, CTRL_SHIFT)).setIcon(
				UIRES.get("16px.importtexture"));
		this.importSound = new ImportSoundAction(this);
		this.importStructure = new StructureImportActions.ImportStructure(this).setIcon(
				UIRES.get("16px.importstructure"));
		this.importStructureFromMinecraft = new StructureImportActions.ImportStructureFromMinecraft(this);
		this.importJavaModel = new ModelImportActions.JAVA(this).setIcon(UIRES.get("16px.importjavamodel"));
		this.importJSONModel = new ModelImportActions.JSON(this).setIcon(UIRES.get("16px.importjsonmodel"));
		this.importOBJModel = new ModelImportActions.OBJ(this).setIcon(UIRES.get("16px.importobjmodel"));
		this.closeWorkspace = new BasicAction(this, L10N.t("action.workspace.close"),
				e -> mcreator.closeThisMCreator(mcreator.getApplication().getOpenMCreators().size() <= 1),
				new ActionAccelerator("workspace.close"));
		this.regenerateCode = new RegenerateCodeAction(this);
		this.exportWorkspaceToZIP = new ExportWorkspaceToZIPAction(this);
		this.exportWorkspaceToZIPWithRunDir = new ExportWorkspaceToZIPAction.WithRunDir(this);
		this.showConsoleTab = new BasicAction(this, L10N.t("action.show_console"),
				e -> mcreator.mcreatorTabs.showTab(mcreator.consoleTab), new ActionAccelerator("show_console", KeyEvent.VK_C, CTRL_SHIFT));
		this.showWorkspaceTab = new BasicAction(this, L10N.t("action.show_workspace"),
				e -> mcreator.mcreatorTabs.showTab(mcreator.workspaceTab), new ActionAccelerator("show_workspace", KeyEvent.VK_S, CTRL_SHIFT));
		this.closeAllTabs = new BasicAction(this, L10N.t("action.close_all_tabs"),
				e -> mcreator.mcreatorTabs.closeAllTabs(),
				new ActionAccelerator("close_all_tabs", KeyEvent.VK_F9, 0));
		this.closeCurrentTab = new BasicAction(this, L10N.t("action.close_tab"),
				e -> mcreator.mcreatorTabs.closeTab(mcreator.mcreatorTabs.getCurrentTab()),
				new ActionAccelerator("close_tab", KeyEvent.VK_F8, 0));
		this.showWorkspaceBrowser = new BasicAction(this, L10N.t("action.show_workspace_browser"),
				e -> mcreator.splitPane.setDividerLocation(280), new ActionAccelerator("show_workspace_browser", KeyEvent.VK_Y, CTRL_SHIFT));
		this.hideWorkspaceBrowser = new BasicAction(this, L10N.t("action.hide_workspace_browser"),
				e -> mcreator.splitPane.setDividerLocation(0), new ActionAccelerator("hide_workspace_browser", KeyEvent.VK_A, CTRL_SHIFT));
		this.openWorkspace = new OpenWorkspaceAction(this);
		this.newWorkspace = new NewWorkspaceAction(this);
		this.importWorkspace = new ImportWorkspaceAction(this);
		this.openWorkspaceFolder = new BasicAction(this, L10N.t("action.open_workspace_folder"),
				e -> DesktopUtils.openSafe(mcreator.getWorkspaceFolder()), new ActionAccelerator("open_workspace_folder"));
		this.setupVCS = new SetupVCSAction(this);
		this.unlinkVCS = new UnlinkVCSAction(this);
		this.setupVCSOrSettings = new SetupOrSettingsVCSAction(this);
		this.syncToRemote = new SyncLocalWithRemoteAction(this);
		this.syncFromRemote = new SyncRemoteToLocalAction(this);
		this.showUnsyncedChanges = new ShowLocalChangesAction(this);
		this.remoteWorkspaceSettings = new VCSInfoSettingsAction(this);
		this.openMaterialPackMaker = MaterialPackMakerTool.getAction(this);
		this.openOrePackMaker = OrePackMakerTool.getAction(this);
		this.openToolPackMaker = ToolPackMakerTool.getAction(this);
		this.openArmorPackMaker = ArmorPackMakerTool.getAction(this);
		this.openWoodPackMaker = WoodPackMakerTool.getAction(this);
		this.showEntityIDList = new ShowDataListAction.EntityIDs(this);
		this.showItemBlockList = new ShowDataListAction.ItemBlockList(this);
		this.showParticleIDList = new ShowDataListAction.ParticeIDList(this);
		this.showSoundsList = new ShowDataListAction.SoundsList(this);
		this.showFuelBurnTimes = new ShowDataListAction.FuelBurnTimes(this);
		this.showVanillaLootTables = new ShowDataListAction.VanillaLootTables(this);
		this.knowledgeBase = new VisitURIAction(this, L10N.t("action.knowledge_base"),
				MCreatorApplication.SERVER_DOMAIN + "/support/knowledgebase");
		this.setCreativeTabItemOrder = new EditTabOrderAction(this);
		this.injectDefaultTags = InjectTagsTool.getAction(this);
		this.donate = new VisitURIAction(this, L10N.t("action.donate"),
				MCreatorApplication.SERVER_DOMAIN + "/donate").setIcon(UIRES.get("donate"));
		this.openJavaEditionFolder = new MinecraftFolderActions.OpenJavaEditionFolder(this);
		this.openBedrockEditionFolder = new MinecraftFolderActions.OpenBedrockEditionFolder(this);

		//Image Editor actions
		this.imageEditorUndo = new ImageEditorUndoAction(this);
		this.imageEditorRedo = new ImageEditorRedoAction(this);
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
		this.imageEditorResizeLayer = new ResizeToolAction(this);
		this.imageEditorResizeCanvas = new ResizeCanvasToolAction(this);

		AcceleratorsManager.INSTANCE.loadAccelerators(this);
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
