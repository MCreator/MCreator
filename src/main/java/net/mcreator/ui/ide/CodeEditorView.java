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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.mcreator.io.FileIO;
import net.mcreator.io.writer.JSONWriter;
import net.mcreator.java.CodeCleanup;
import net.mcreator.java.DeclarationFinder;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.JFileBreadCrumb;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.KeyStrokes;
import net.mcreator.ui.ide.autocomplete.CustomJSCCache;
import net.mcreator.ui.ide.autocomplete.StringCompletitionProvider;
import net.mcreator.ui.ide.json.JsonTree;
import net.mcreator.ui.ide.mcfunction.MinecraftCommandsTokenMaker;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.laf.SlickTreeUI;
import net.mcreator.ui.laf.renderer.AstTreeCellRendererCustom;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaCompletionProvider;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.tree.JavaOutlineTree;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.tree.JavaScriptOutlineTree;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rsyntaxtextarea.focusabletip.FocusableTip;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CodeEditorView extends ViewBase {

	private static final Logger LOG = LogManager.getLogger("Code Editor");

	public SearchBar sed;
	public ReplaceBar rep;

	private final JSplitPane spne = new JSplitPane();

	private final JScrollPane treeSP = new JScrollPane();
	private AbstractSourceTree tree;
	public ChangeListener cl;

	public RSyntaxTextArea te = new RSyntaxTextArea() {
		@Override public void setCursor(Cursor c) {
			if (jumpToMode)
				return;
			if (c != null && !c.equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)))
				super.setCursor(c);
		}
	};

	public File fileWorkingOn;

	public boolean changed = false;

	private AutoCompletion ac = null;

	private boolean jumpToMode = false;
	public MouseEvent mouseEvent;

	private final JLabel ro = new JLabel();

	public final boolean readOnly;

	private final CodeCleanup codeCleanup;

	private final JFileBreadCrumb fileBreadCrumb;

	@Nullable private ModElement fileOwner = null;

	private final JPanel rightDummy = new JPanel();

	public CodeEditorView(MCreator fa, File fs) {
		this(fa, FileIO.readFileToString(fs), fs.getName(), fs, false);
	}

	CodeEditorView(MCreator fa, String code, String fileName, File fileWorkingOn, boolean readOnly) {
		super(fa);

		this.fileWorkingOn = fileWorkingOn;
		if (this.fileWorkingOn == null)
			this.fileWorkingOn = new File(fileName);

		this.readOnly = readOnly;

		this.codeCleanup = new CodeCleanup();

		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

		this.fileBreadCrumb = new JFileBreadCrumb(mcreator, fileWorkingOn, fa.getWorkspaceFolder());

		te.addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent focusEvent) {
				super.focusGained(focusEvent);
				fileBreadCrumb.reloadPath(fileWorkingOn);
				te.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			}
		});

		LanguageSupportFactory.get().register(te);

		sed = new SearchBar(te);
		rep = new ReplaceBar(te);
		te.setText(code);

		if (readOnly) {
			te.setEditable(false);
		}

		te.requestFocusInWindow();
		te.setMarkOccurrences(true);
		te.setCodeFoldingEnabled(true);
		te.setClearWhitespaceLinesEnabled(true);
		te.setAutoIndentEnabled(true);

		te.setTabSize(4);
		te.setTabsEmulated(false);

		ToolTipManager.sharedInstance().registerComponent(te);

		RTextScrollPane sp = new RTextScrollPane(te, PreferencesManager.PREFERENCES.ide.lineNumbers);

		RSyntaxTextAreaStyler.style(te, sp, PreferencesManager.PREFERENCES.ide.fontSize);

		sp.setFoldIndicatorEnabled(true);

		sp.getGutter().setFoldBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		sp.getGutter().setBorderColor((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		sp.getGutter().setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

		sp.getGutter().setBookmarkingEnabled(true);
		sp.setIconRowHeaderEnabled(false);

		sp.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		sp.setBorder(null);

		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getHorizontalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getHorizontalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(7, 0));
		sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 7));

		JPanel cornerDummy1 = new JPanel();
		cornerDummy1.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, cornerDummy1);

		JPanel cornerDummy2 = new JPanel();
		cornerDummy2.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		sp.setCorner(JScrollPane.LOWER_LEFT_CORNER, cornerDummy2);

		JPanel cornerDummy12 = new JPanel();
		cornerDummy12.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		treeSP.setCorner(JScrollPane.LOWER_RIGHT_CORNER, cornerDummy12);

		JPanel cornerDummy22 = new JPanel();
		cornerDummy22.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		treeSP.setCorner(JScrollPane.LOWER_LEFT_CORNER, cornerDummy22);

		treeSP.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), treeSP.getVerticalScrollBar()));
		treeSP.getHorizontalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), treeSP.getHorizontalScrollBar()));
		treeSP.getVerticalScrollBar().setPreferredSize(new Dimension(7, 0));
		treeSP.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 7));

		treeSP.setBorder(null);

		te.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent documentEvent) {
				if (!changed && !readOnly) {
					changed = true;
					if (cl != null)
						cl.stateChanged(new ChangeEvent(this));
				}
			}

			@Override public void removeUpdate(DocumentEvent documentEvent) {
				if (!changed && !readOnly) {
					changed = true;
					if (cl != null)
						cl.stateChanged(new ChangeEvent(this));
				}

			}

			@Override public void changedUpdate(DocumentEvent documentEvent) {
			}
		});

		sp.setOpaque(false);

		spne.setRightComponent(rightDummy);

		JPanel cp = new JPanel(new BorderLayout());
		cp.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		cp.add(sp);

		if (PreferencesManager.PREFERENCES.ide.errorInfoEnable) {
			ErrorStrip errorStrip = new ErrorStrip(te);
			errorStrip.setFollowCaret(false);
			errorStrip.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			cp.add(errorStrip, BorderLayout.LINE_END);
		}

		spne.setLeftComponent(cp);
		spne.setContinuousLayout(true);

		spne.setUI(new BasicSplitPaneUI() {
			@Override public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					@Override public void setBorder(Border b) {
					}

					@Override public void paint(Graphics g) {
						g.setColor((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		});

		spne.setBorder(null);

		JPanel bars = new JPanel(new BorderLayout());

		ro.setBackground(new Color(0x3C3939));
		ComponentUtils.deriveFont(ro, 13);
		ro.setOpaque(true);
		ro.setForeground(new Color(0xE0E0E0));
		Border margin = new EmptyBorder(3, 3, 3, 3);
		ro.setBorder(new CompoundBorder(ro.getBorder(), margin));
		ro.setVisible(false);

		ro.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ro.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				super.mouseClicked(mouseEvent);
				ro.setVisible(false);
			}
		});

		if (!readOnly) {
			bars.add("North", ro);
			bars.add("Center", sed);
			bars.add("South", rep);
		} else {
			ro.setText(
					"This is (decompiled/provided) source code and is read-only and intended for internal reference and educational use only");
			bars.add("North", ro);
			bars.add("South", sed);
			ro.setVisible(true);
		}

		sed.setVisible(false);
		rep.setVisible(false);

		JPanel topPan = new JPanel(new BorderLayout());
		topPan.setOpaque(false);
		topPan.add("Center", bars);
		topPan.add("North", fileBreadCrumb);

		add("North", topPan);
		add("Center", spne);
		setBorder(null);

		if (!readOnly)
			KeyStrokes.registerKeyStroke(
					KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), te,
					new AbstractAction() {
						@Override public void actionPerformed(ActionEvent actionEvent) {
							disableJumpToMode();
							saveCode();
							fa.actionRegistry.buildWorkspace.doAction();
							if (CodeEditorView.this.mouseEvent != null)
								new FocusableTip(te, null).toolTipRequested(CodeEditorView.this.mouseEvent,
										"Code saved and build started");
						}
					});

		if (!readOnly)
			KeyStrokes.registerKeyStroke(
					KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), te,
					new AbstractAction() {
						@Override public void actionPerformed(ActionEvent actionEvent) {
							disableJumpToMode();
							reformatTheCodeOrganiseAndFixImports();
							if (CodeEditorView.this.mouseEvent != null)
								new FocusableTip(te, null).toolTipRequested(CodeEditorView.this.mouseEvent,
										"Reformatted and organized code and imports");
						}
					});

		if (!readOnly)
			KeyStrokes.registerKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_M,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_DOWN_MASK, false), te,
					new AbstractAction() {
						@Override public void actionPerformed(ActionEvent actionEvent) {
							disableJumpToMode();
							saveCode();
							fa.actionRegistry.runClient.doAction();
							if (CodeEditorView.this.mouseEvent != null)
								new FocusableTip(te, null).toolTipRequested(CodeEditorView.this.mouseEvent,
										"Code saved and Minecraft launched");
						}
					});

		spne.setResizeWeight(1);

		int posit = te.getText().indexOf("public class");
		if (posit < 0)
			posit = 0;

		te.setHyperlinksEnabled(false);
		te.setCaretPosition(posit);

		te.discardAllEdits();

		new Thread(() -> setupCodeSupport(fileName)).start();
	}

	private void setupCodeSupport(String fileName) {
		if (fileName.endsWith(".java")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA));

			JavaLanguageSupport jls = new JavaLanguageSupport();
			jls.setAutoCompleteEnabled(PreferencesManager.PREFERENCES.ide.autocomplete);
			jls.setAutoActivationEnabled(!PreferencesManager.PREFERENCES.ide.autocompleteMode.equals("Manual"));
			jls.setParameterAssistanceEnabled(true);
			jls.setShowDescWindow(PreferencesManager.PREFERENCES.ide.autocompleteDocWindow);

			try {
				Field field = jls.getClass().getDeclaredField("jarManager");
				field.setAccessible(true);
				field.set(jls, mcreator.getGenerator().getProjectJarManager());
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e1) {
				LOG.error(e1.getMessage(), e1);
			}

			jls.install(te);

			try {
				Class<?> treeNodeClass = Class.forName("org.fife.rsta.ac.AbstractLanguageSupport");
				Method method = treeNodeClass.getDeclaredMethod("getAutoCompletionFor", RSyntaxTextArea.class);
				method.setAccessible(true);
				ac = (AutoCompletion) method.invoke(jls, te);
			} catch (ClassNotFoundException | SecurityException | InvocationTargetException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException e1) {
				LOG.error(e1.getMessage(), e1);
			}

			JavaCompletionProvider jcp = jls.getCompletionProvider(te);

			try {
				Field field = jcp.getClass().getDeclaredField("sourceProvider");
				field.setAccessible(true);
				DefaultCompletionProvider sourceCompletionProvider = (DefaultCompletionProvider) field.get(jcp);
				jcp.setShorthandCompletionCache(
						new CustomJSCCache(sourceCompletionProvider, new DefaultCompletionProvider()));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e1) {
				LOG.error(e1.getMessage(), e1);
			}

			jcp.setStringCompletionProvider(new StringCompletitionProvider(mcreator.getWorkspace()));

			if (ac != null)
				AutocompleteStyle.installStyle(ac, te);

			JavaParser parser = jls.getParser(te);

			te.addKeyListener(new KeyAdapter() {
				final boolean smartAutocomplete = PreferencesManager.PREFERENCES.ide.autocompleteMode.equals("Smart");

				boolean completitionInAction = false;

				@Override public void keyPressed(KeyEvent keyEvent) {
					super.keyPressed(keyEvent);
					if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
						te.setCursor(new Cursor(Cursor.HAND_CURSOR));
						jumpToMode = true;
					} else if (smartAutocomplete && !completitionInAction && jls.isAutoActivationEnabled() && Character
							.isLetterOrDigit(keyEvent.getKeyChar()) && jcp.getAlreadyEnteredText(te).length() > 1) {
						if (!completitionInAction) {
							new Thread(() -> {
								if (ac != null) {
									completitionInAction = true;
									try {
										SwingUtilities.invokeAndWait(() -> ac.doCompletion());
									} catch (InterruptedException | InvocationTargetException ignored) {
									}
									completitionInAction = false;
								}
							}).start();
						}
					}
				}

				@Override public void keyReleased(KeyEvent keyEvent) {
					super.keyReleased(keyEvent);
					if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
						disableJumpToMode();
					}
				}
			});

			te.addMouseListener(new MouseAdapter() {
				@Override public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					disableJumpToMode();
				}

				@Override public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					if ((e.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK) {
						te.setCursor(new Cursor(Cursor.HAND_CURSOR));
						jumpToMode = true;
					}
				}
			});

			te.addMouseListener(new MouseAdapter() {

				@Override public void mouseClicked(MouseEvent mouseEvent) {
					CodeEditorView.this.mouseEvent = mouseEvent;
					if (jumpToMode && ac != null) {
						DeclarationFinder.InClassPosition position = DeclarationFinder
								.getDeclarationOnPos(mcreator.getWorkspace(), parser, te, jls.getJarManager());
						if (position != null) {
							if (position.classFileNode == null) {
								te.setCaretPosition(position.carret);
								SwingUtilities.invokeLater(() -> centerLineInScrollPane());
							} else {
								ProjectFileOpener
										.openFileSpecific(mcreator, position.classFileNode, position.openInReadOnly,
												position.carret, position.virtualFile);
							}
							disableJumpToMode();
						} else {
							new FocusableTip(te, null).toolTipRequested(mouseEvent, "Failed to find declaration!");
						}
					}
					jumpToMode = false;
				}
			});
		} else if (fileName.endsWith(".mcfunction")) {
			SwingUtilities.invokeLater(() -> {
				AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
				atmf.putMapping("text/mcfunction", MinecraftCommandsTokenMaker.class.getName());
				te.setSyntaxEditingStyle("text/mcfunction");
			});
		} else if (fileName.endsWith(".info") || fileName.endsWith(".json") || fileName.endsWith(".mcmeta")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON));
		} else if (fileName.endsWith(".xml")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML));
		} else if (fileName.endsWith(".lang")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE));
		} else if (fileName.endsWith(".gradle")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY));
		} else if (fileName.endsWith(".js")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT));

			JavaScriptLanguageSupport javaScriptLanguageSupport = new JavaScriptLanguageSupport();

			javaScriptLanguageSupport.setAutoCompleteEnabled(PreferencesManager.PREFERENCES.ide.autocomplete);
			javaScriptLanguageSupport
					.setAutoActivationEnabled(!PreferencesManager.PREFERENCES.ide.autocompleteMode.equals("Manual"));
			javaScriptLanguageSupport.setParameterAssistanceEnabled(true);
			javaScriptLanguageSupport.setShowDescWindow(PreferencesManager.PREFERENCES.ide.autocompleteDocWindow);

			javaScriptLanguageSupport.install(te);

			if (ac != null)
				AutocompleteStyle.installStyle(ac, te);
		} else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_YAML));
		}

		SwingUtilities.invokeLater(this::loadSourceTree);
	}

	private void setCustomNotice(String notice, Color color) {
		ro.setText(notice);
		ro.setBackground(color);
		ro.setVisible(true);
	}

	public void disableJumpToMode() {
		jumpToMode = false;
		te.setCursor(new Cursor(Cursor.TEXT_CURSOR));
	}

	private void loadSourceTree() {
		String language = te.getSyntaxEditingStyle();

		if (SyntaxConstants.SYNTAX_STYLE_JAVA.equals(language)) {
			tree = new JavaOutlineTree();
		} else if (SyntaxConstants.SYNTAX_STYLE_JSON.equals(language)) {
			tree = new JsonTree();
		} else if (SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT.equals(language)) {
			tree = new JavaScriptOutlineTree();
		}

		if (tree != null) {
			tree.setCellRenderer(new AstTreeCellRendererCustom());
			tree.listenTo(te);
			tree.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			tree.setForeground(Color.white);
			tree.setRowHeight(18);
			tree.setUI(new SlickTreeUI());
			treeSP.setViewportView(tree);
			treeSP.addComponentListener(new ComponentAdapter() {
				@Override public void componentResized(ComponentEvent componentEvent) {
					super.componentResized(componentEvent);
					tree.updateUI();
					tree.setCellRenderer(new AstTreeCellRendererCustom());
					tree.setUI(new SlickTreeUI());
				}
			});
			treeSP.revalidate();
			spne.setRightComponent(treeSP);
			spne.setDividerLocation(0.8);
		} else {
			spne.setRightComponent(rightDummy);
		}
	}

	public void setChangeListener(ChangeListener changeListener) {
		this.cl = changeListener;
	}

	public void reformatTheCodeOnly() {
		String language = te.getSyntaxEditingStyle();
		if (SyntaxConstants.SYNTAX_STYLE_JAVA.equals(language)) {
			int pos = te.getCaretPosition();
			String ncode = codeCleanup.reformatTheCodeOnly(te.getText());
			te.setText(ncode);
			te.setCaretPosition(pos);
		} else if (SyntaxConstants.SYNTAX_STYLE_JSON.equals(language)) {
			int pos = te.getCaretPosition();
			JsonElement json = JsonParser.parseString(te.getText());
			te.setText(JSONWriter.gson.toJson(json));
			te.setCaretPosition(pos);
		}
	}

	public void reformatTheCodeOrganiseAndFixImports() {
		String language = te.getSyntaxEditingStyle();
		if (SyntaxConstants.SYNTAX_STYLE_JAVA.equals(language)) {
			int pos = te.getCaretPosition();
			String ncode = codeCleanup.reformatTheCodeAndOrganiseImports(mcreator.getWorkspace(), te.getText());
			te.setText(ncode);
			te.setCaretPosition(pos);
		} else if (SyntaxConstants.SYNTAX_STYLE_JSON.equals(language)) {
			int pos = te.getCaretPosition();
			JsonElement json = JsonParser.parseString(te.getText());
			te.setText(JSONWriter.gson.toJson(json));
			te.setCaretPosition(pos);
		}
	}

	public void saveCode() {
		savingMCreatorModElementWarning();
		FileIO.writeStringToFile(te.getText(), fileWorkingOn);
		changed = false;
		if (cl != null)
			cl.stateChanged(new ChangeEvent(this));
	}

	public void centerLineInScrollPane() {
		Container container = SwingUtilities.getAncestorOfClass(JViewport.class, te);

		if (container == null)
			return;

		try {
			Rectangle r = te.modelToView(te.getCaretPosition());
			if (r == null)
				return;
			JViewport viewport = (JViewport) container;
			int extentHeight = viewport.getExtentSize().height;
			int viewHeight = viewport.getViewSize().height;

			int y = Math.max(0, r.y - ((extentHeight - r.height) / 2));
			y = Math.min(y, viewHeight - extentHeight);

			viewport.setViewPosition(new Point(0, y));
		} catch (BadLocationException ignored) {
		}
	}

	void setFileOwnerModElement(ModElement fileOwner) {
		this.fileOwner = fileOwner;
		boolean codeLocked = this.fileOwner.isCodeLocked();
		if (!codeLocked) {
			setCustomNotice(this.fileOwner.getName()
							+ " was created from MCreator's interface. You need to lock its code to prevent the code from being overwritten!",
					new Color(0x31332F));
		}
	}

	private void savingMCreatorModElementWarning() {
		if (this.fileOwner != null) {
			boolean codeLocked = this.fileOwner.isCodeLocked();
			if (!codeLocked) {
				Object[] options = { "Lock the code for MCreator and save", "Save without locking" };
				int n = JOptionPane.showOptionDialog(mcreator,
						"<html><b>You are trying to save unlocked mod file!</b><br>"
								+ "<br>This means that MCreator might overwrite your changes in some cases.<br>"
								+ "To prevent this, you can lock the code.<br><br>"
								+ "If the code is locked, MCreator won't change the source code, but this means that when<br>"
								+ "updating MCreator or changing Minecraft version, changes and fixes won't be applied<br>"
								+ "to the elements that are locked, but will need to be done manually.<br>"
								+ "<br><small>Please read the wiki page on MCreator's website about locking code before using this action.",
						"Overwriting MCreator generated file", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

				if (n == 0) {
					this.fileOwner.setCodeLock(true);
					mcreator.getWorkspace().updateModElement(this.fileOwner);
					ro.setVisible(false);
				} else {
					setCustomNotice(this.fileOwner.getName()
									+ " was created from MCreator's interface. You need to lock its code to prevent the code from being overwritten!",
							new Color(0x31332F));
				}
			}
		}
	}

	@Override public ViewBase showView() {
		MCreatorTabs.Tab fileTab = new MCreatorTabs.Tab(this, fileWorkingOn, false);
		fileTab.setTabClosingListener(tab -> {
			if (((CodeEditorView) tab.getContent()).changed) {
				Object[] options = { "Close and save", "Close", "Cancel" };
				int res = JOptionPane.showOptionDialog(mcreator,
						"<html><b>The file " + ((CodeEditorView) tab.getContent()).fileWorkingOn.getName()
								+ " has not been saved.</b><br>Please select desired action to solve this conflict:",
						"Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
						options[0]);
				if (res == 0) {
					((CodeEditorView) tab.getContent()).saveCode();
					return true;
				} else
					return res == 1;
			}
			return true;
		});
		if (readOnly)
			fileTab.setActiveColor((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		setChangeListener(changeEvent -> {
			if (!readOnly) {
				if (changed) {
					fileTab.setActiveColor((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
					fileTab.setInactiveColor((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
				} else {
					fileTab.setActiveColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
					fileTab.setInactiveColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				}
			}
		});

		MCreatorTabs.Tab existing = mcreator.mcreatorTabs.showTabOrGetExisting(fileWorkingOn);
		if (existing == null) {
			mcreator.mcreatorTabs.addTab(fileTab);
			return this;
		}
		return (ViewBase) existing.getContent();
	}

	@Override public String getViewName() {
		return fileWorkingOn.getName();
	}

	@Override public ImageIcon getViewIcon() {
		return FileIcons.getIconForFile(fileWorkingOn);
	}

	public static boolean isFileSupported(String fileName) {
		return Arrays
				.asList("java", "info", "txt", "json", "mcmeta", "lang", "gradle", "ini", "conf", "xml", "properties",
						"mcfunction", "toml", "js", "yaml", "yml").contains(FilenameUtils.getExtension(fileName));
	}

	public void jumpToLine(int linenum) {
		new Thread(() -> {
			SwingUtilities.invokeLater(() -> te.requestFocus());
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
			SwingUtilities.invokeLater(() -> {
				try {
					te.setCaretPosition(te.getLineStartOffset(linenum));
					centerLineInScrollPane();
				} catch (BadLocationException ignored) {
				}
			});
		}).start();
	}

}
