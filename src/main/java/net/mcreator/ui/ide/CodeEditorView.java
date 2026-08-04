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

package net.mcreator.ui.ide;

import net.mcreator.generator.io.GradleTrackingFileIO;
import net.mcreator.io.FileIO;
import net.mcreator.java.CodeCleanup;
import net.mcreator.java.DeclarationChecker;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.JFileBreadCrumb;
import net.mcreator.ui.component.MonacoEditorPanel;
import net.mcreator.ui.component.MonacoEditorPool;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.ide.debug.BreakpointHandler;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.search.ISearchable;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.Iterator;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class CodeEditorView extends ViewBase implements ISearchable {

	private static final Logger LOG = LogManager.getLogger("Code Editor");

	private static final List<String> SUPPORTED_FILE_EXTENSIONS = List.of("java", "info", "txt", "json", "mcmeta",
			"lang", "gradle", "ini", "conf", "xml", "properties", "mcfunction", "toml", "js", "yaml", "yml", "md",
			"cfg", "fsh", "vsh", "csv",
			"classtweaker"); // classtweaker is Fabric's access transformer format (formerly known as accesswidener)

	public ChangeListener changeListener;

	private final MonacoEditorPanel te;

	public MonacoEditorPanel getEditor() { return te; }

	public File fileWorkingOn;

	public boolean changed = false;

	private final JLabel ro = new JLabel();

	public final boolean readOnly;

	private final CodeCleanup codeCleanup;

	private final JFileBreadCrumb fileBreadCrumb;

	@Nullable private BreakpointHandler breakpointHandler;

	@Nullable private ModElement fileOwner = null;

	public CodeEditorView(MCreator fa, File fs) {
		this(fa, FileIO.readFileToString(fs), fs.getName(), fs, false);
	}

	public CodeEditorView(MCreator fa, String code, String fileName, @Nullable File fileWorkingOn, boolean readOnly) {
		super(fa);

		this.fileWorkingOn = fileWorkingOn;
		if (this.fileWorkingOn == null)
			this.fileWorkingOn = new File(fileName);

		this.readOnly = readOnly;

		this.codeCleanup = new CodeCleanup();

		setBackground(Theme.current().getBackgroundColor());

		this.fileBreadCrumb = new JFileBreadCrumb(mcreator, this.fileWorkingOn, fa.getWorkspaceFolder());

		String ext = FilenameUtilsPatched.getExtension(this.fileWorkingOn.getName());
		te = MonacoEditorPool.getOrCreate(code, ext, readOnly);
		te.setWorkspace(mcreator.getWorkspace());

		te.addChangeListener(e -> {
			if (!changed && !readOnly) {
				changed = true;
				if (changeListener != null)
					changeListener.stateChanged(new ChangeEvent(this));
			}
		});

		this.breakpointHandler = new BreakpointHandler(this);

		te.setEditorEventListener(new MonacoEditorPanel.EditorEventListener() {
			@Override public void onSaveRequested() { saveCode(); }
			@Override public void onSaveAndBuildRequested() { saveAndBuildCode(); }
			@Override public void onBreakpointToggled(int line) { if (breakpointHandler != null) breakpointHandler.toggleBreakpoint(line); }
			@Override public void onOpenDeclaration(String word) { handleOpenDeclaration(word); }
		});

		JPanel bars = new JPanel(new BorderLayout(2, 2));
		ComponentUtils.deriveFont(ro, 12);
		ro.setOpaque(true);
		Border margin = new EmptyBorder(3, 5, 3, 3);
		ro.setBorder(new CompoundBorder(ro.getBorder(), margin));
		ro.setVisible(false);

		ro.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ro.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				super.mouseClicked(mouseEvent);
				ro.setVisible(false);
			}
		});

		if (readOnly) {
			ro.setText(L10N.t("ide.warnings.read_only"));
			bars.add("North", ro);
			ro.setVisible(true);
		}

		JPanel topPan = new JPanel(new BorderLayout());
		topPan.setOpaque(false);
		topPan.add("Center", bars);

		if (fileWorkingOn != null)
			topPan.add("North", fileBreadCrumb);

		add("North", topPan);

		add("Center", te);
		setBorder(null);
	}

	private void handleOpenDeclaration(String word) {
		if (word == null || word.isBlank() || mcreator == null || mcreator.getWorkspace() == null) return;

		try {
			String code = te.getText();
			CompilationUnit cu = new ASTFactory().getCompilationUnit("File.java", new Scanner(new java.io.StringReader(code)));
			JarManager jarManager = mcreator.getGenerator().getProjectJarManager();
			if (cu != null && jarManager != null) {
				DeclarationChecker.InClassPosition pos = null;
				TypeDeclaration typeDecl = null;
				Iterator<TypeDeclaration> typeDeclIter = cu.getTypeDeclarationIterator();
				if (typeDeclIter != null && typeDeclIter.hasNext()) {
					typeDecl = typeDeclIter.next();
				}
				
				if ("this".equals(word) && typeDecl != null) {
					pos = DeclarationChecker.checkForThisDeclaration(code, word, typeDecl);
				} else if ("super".equals(word) && typeDecl != null) {
					pos = DeclarationChecker.checkForSuperDeclaration(mcreator.getWorkspace(), word, typeDecl, cu, jarManager);
				} else {
					pos = DeclarationChecker.checkForClassDeclaration(mcreator.getWorkspace(), word, cu, jarManager);
				}
				
				if (pos != null) {
					if (pos.classFileNode != null || pos.virtualFile != null) {
						ProjectFileOpener.openFileSpecific(mcreator, pos.classFileNode, pos.openInReadOnly, pos.caret, pos.virtualFile);
					} else {
						te.setCaretPosition(pos.caret);
						te.requestFocus();
					}
					return;
				}
			}
		} catch (Exception e) {
			LOG.error("Failed to open declaration for {}", word, e);
		}

		File srcRoot = mcreator.getWorkspace().getGenerator().getSourceRoot();
		File targetFile = findJavaFileInDir(srcRoot, word);
		if (targetFile != null && targetFile.exists()) {
			ProjectFileOpener.openCodeFile(mcreator, targetFile);
		}
	}

	private File findJavaFileInDir(File dir, String className) {
		if (dir == null || !dir.isDirectory()) return null;
		File[] files = dir.listFiles();
		if (files == null) return null;
		for (File f : files) {
			if (f.isDirectory()) {
				File res = findJavaFileInDir(f, className);
				if (res != null) return res;
			} else if (f.getName().equalsIgnoreCase(className + ".java")) {
				return f;
			}
		}
		return null;
	}

	private void setCustomNotice(String notice) {
		ro.setText(notice);
		ro.setVisible(true);
	}

	public void hideNotice() {
		ro.setVisible(false);
	}

	public void setChangeListener(ChangeListener changeListener) {
		this.changeListener = changeListener;
	}
	
	public void reformatTheCodeOnly() {
		if (readOnly) return;
		te.formatCode();
	}

	public void reformatTheCodeOrganiseAndFixImports() {
		if (readOnly) return;
		String ext = FilenameUtilsPatched.getExtension(fileWorkingOn.getName());
		if ("java".equalsIgnoreCase(ext)) {
			String ncode = codeCleanup.reformatTheCodeAndOrganiseImports(mcreator.getWorkspace(), te.getText());
			te.setText(ncode);
		} else {
			te.formatCode();
		}
	}

	public void saveCode() {
		if (readOnly) return;
		savingMCreatorModElementWarning();
		GradleTrackingFileIO.writeFile(mcreator.getWorkspace(), te.getText(), fileWorkingOn);
		changed = false;
		if (changeListener != null)
			changeListener.stateChanged(new ChangeEvent(this));
	}

	public void saveAndBuildCode() {
		if (readOnly) return;
		te.showNotification(L10N.t("ide.tips.save_and_build"));
	}

	void setFileOwnerModElement(ModElement fileOwner) {
		this.fileOwner = fileOwner;
		boolean codeLocked = this.fileOwner.isCodeLocked();
		if (!codeLocked) {
			setCustomNotice(L10N.t("ide.warnings.created_from_ui", this.fileOwner.getName()));
		}
	}

	private void savingMCreatorModElementWarning() {
		if (this.fileOwner != null) {
			boolean codeLocked = this.fileOwner.isCodeLocked();
			if (!codeLocked) {
				Object[] options = { L10N.t("ide.actions.lock_and_save"), L10N.t("ide.actions.save_without_locking") };
				int n = JOptionPane.showOptionDialog(mcreator, L10N.t("ide.warnings.save_unlocked_element"),
						L10N.t("ide.warnings.save_unlocked_element.title"), JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

				if (n == 0) {
					this.fileOwner.setCodeLock(true);
					mcreator.getWorkspace().markDirty();
					ro.setVisible(false);
				} else {
					setCustomNotice(L10N.t("ide.warnings.created_from_ui", this.fileOwner.getName()));
				}
			}
		}
	}

	@Override public ViewBase showView() {
		MCreatorTabs.Tab fileTab = new MCreatorTabs.Tab(this, fileWorkingOn);
		fileTab.setTabClosingListener(tab -> {
			if (((CodeEditorView) tab.getContent()).changed) {
				Object[] options = { L10N.t("ide.action.close_and_save"), L10N.t("common.close"),
						UIManager.getString("OptionPane.cancelButtonText") };
				int res = JOptionPane.showOptionDialog(mcreator, L10N.t("ide.warnings.file_not_saved",
								((CodeEditorView) tab.getContent()).fileWorkingOn.getName()), L10N.t("common.warning"),
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				boolean shouldClose = res == 1;
				if (res == 0) {
					((CodeEditorView) tab.getContent()).saveCode();
					shouldClose = true;
				}
				if (shouldClose) {
					MonacoEditorPool.recycle(((CodeEditorView) tab.getContent()).getEditor());
				}
				return shouldClose;
			}
			MonacoEditorPool.recycle(((CodeEditorView) tab.getContent()).getEditor());
			return true;
		});

		MCreatorTabs.Tab existing = mcreator.getTabs().showTabOrGetExisting(fileWorkingOn);
		if (existing == null) {
			mcreator.getTabs().addTab(fileTab);
			return this;
		}
		return (ViewBase) existing.getContent();
	}

	public CodeCleanup getCodeCleanup() {
		return codeCleanup;
	}

	@Override public String getViewName() {
		return fileWorkingOn.getName();
	}

	@Override public ImageIcon getViewIcon() {
		return FileIcons.getIconForFile(fileWorkingOn);
	}

	public static boolean isFileSupported(String fileName) {
		return SUPPORTED_FILE_EXTENSIONS.contains(FilenameUtilsPatched.getExtension(fileName).toLowerCase());
	}

	public void jumpToLine(int linenum) {
		te.jumpToLine(linenum);
	}

	@Override public void search(@Nullable String searchTerm) {
		te.triggerFind();
	}

	@Nullable public BreakpointHandler getBreakpointHandler() {
		return breakpointHandler;
	}

}