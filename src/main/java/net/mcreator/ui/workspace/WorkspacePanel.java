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

package net.mcreator.ui.workspace;

import net.mcreator.element.*;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.io.FileIO;
import net.mcreator.java.JavaConventions;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.preferences.PreferencesData;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.UnregisteredAction;
import net.mcreator.ui.component.*;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.EventButtonGroup;
import net.mcreator.ui.component.util.ListUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.ModElementIDsDialog;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.ide.ProjectFileOpener;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.laf.renderer.elementlist.*;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.modgui.ModTypeDropdown;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.workspace.breadcrumb.WorkspaceFolderBreadcrumb;
import net.mcreator.ui.workspace.resources.WorkspacePanelResources;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("EqualsBetweenInconvertibleTypes") public class WorkspacePanel extends JPanel {

	private FilterModel dml = new FilterModel();
	public final JTextField search;

	public FolderElement currentFolder;

	public final JSelectableList<IElement> list;

	private final CardLayout cardLayout = new CardLayout() {
		@Override public void show(Container container, String s) {
			super.show(container, s);
			currentTab = s;
			search.repaint();
		}
	};
	private final JPanel panels = new JPanel(cardLayout);

	public WorkspacePanelResources resourcesPan;
	private final WorkspacePanelLocalizations localePan;
	private final WorkspacePanelVariables variablesPan;
	private final WorkspacePanelVCS vcsPan;

	private String currentTab = "mods";

	private final MCreator mcreator;

	private final JButton upFolder;
	private final JButton renameFolder;

	private final JLabel but2 = new JLabel(TiledImageCache.workspaceEdit);
	private final JLabel but2a = new JLabel(TiledImageCache.workspaceDuplicate);
	private final JLabel but3 = new JLabel(TiledImageCache.workspaceDelete);
	private final JLabel but5 = new JLabel(TiledImageCache.workspaceCode);
	private final JLabel but5a = new JLabel(TiledImageCache.workspaceToggle);
	private final JLabel but6 = new JLabel(TiledImageCache.workspaceModElementIDs);

	private final JMenuItem deleteElement = new JMenuItem(L10N.t("workspace.elements.list.edit.delete"));
	private final JMenuItem duplicateElement = new JMenuItem(L10N.t("workspace.elements.list.edit.duplicate"));
	private final JMenuItem codeElement = new JMenuItem(L10N.t("workspace.elements.list.edit.code"));
	private final JMenuItem lockElement = new JMenuItem(L10N.t("workspace.elements.list.edit.lock"));
	private final JMenuItem idElement = new JMenuItem(L10N.t("workspace.elements.list.edit.id"));
	private final JMenuItem renameElementFolder = new JMenuItem(L10N.t("workspace.elements.list.edit.rename.folder"));

	private final CardLayout mainpcl = new CardLayout();
	private final JPanel mainp = new JPanel(mainpcl);

	private final JPanel detailsbar = new JPanel(new GridLayout(1, 6));

	private final JButton view = L10N.button("workspace.elements.list.icon_size");

	private final TransparentToolBar modElementsBar = new TransparentToolBar();

	private final WorkspaceFolderBreadcrumb elementsBreadcrumb;

	private final JLabel elementsCount = new JLabel();

	public final JRadioButtonMenuItem desc = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.descending"));

	private final JRadioButtonMenuItem sortDateCreated = new JRadioButtonMenuItem(
			L10N.t("workspace.elements.list.sort_date"));
	public JRadioButtonMenuItem sortName = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.sort_name"));
	private final JRadioButtonMenuItem sortType = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.sort_type"));
	private final JRadioButtonMenuItem sortLoadingOrder = new JRadioButtonMenuItem(
			L10N.t("workspace.elements.list.sort_loading_order"));

	private final OptionPaneValidatior folderNameValidator = new OptionPaneValidatior() {
		@Override public ValidationResult validate(JComponent component) {
			String folderName = ((JTextField) component).getText();

			if (!folderName.matches("[A-Za-z0-9._ -]+")) {
				return new Validator.ValidationResult(ValidationResultType.ERROR,
						L10N.t("workspace.elements.folders.add.error_letters"));
			}

			List<FolderElement> folderElements = mcreator.getWorkspace().getFoldersRoot().getRecursiveFolderChildren();

			FolderElement tmpFolder = new FolderElement(folderName, currentFolder);

			for (FolderElement folderElement : folderElements) {
				if (folderElement.equals(tmpFolder)) {
					return new Validator.ValidationResult(ValidationResultType.ERROR,
							L10N.t("workspace.elements.folders.add.error_exists"));
				}
			}

			return Validator.ValidationResult.PASSED;
		}
	};

	public WorkspacePanel(final MCreator mcreator) {
		super(new BorderLayout(5, 5));
		this.mcreator = mcreator;

		this.currentFolder = mcreator.getWorkspace().getFoldersRoot();

		this.resourcesPan = new WorkspacePanelResources(this);
		this.localePan = new WorkspacePanelLocalizations(this);
		this.variablesPan = new WorkspacePanelVariables(this);
		this.vcsPan = new WorkspacePanelVCS(this);

		this.elementsBreadcrumb = new WorkspaceFolderBreadcrumb(mcreator);

		JPopupMenu contextMenu = new JPopupMenu();

		panels.setOpaque(false);

		list = new JSelectableList<>(dml);
		list.setOpaque(false);
		list.setBorder(BorderFactory.createEmptyBorder(2, 7, 0, 0));
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(-1);

		list.enableDNDCustom((target, sources) -> {
			if (target instanceof FolderElement) {
				for (IElement element : sources) {
					if (element instanceof ModElement) {
						((ModElement) element).setParentFolder((FolderElement) target);
					} else if (element instanceof FolderElement) {
						if (element.equals(target))
							continue;

						((FolderElement) element).moveTo(mcreator.getWorkspace(), (FolderElement) target);
					}
				}
				mcreator.getWorkspace().markDirty();
				reloadElements();
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		});
		list.setAdditionalDNDComponent(elementsBreadcrumb);

		list.addMouseMotionListener(new MouseAdapter() {
			@Override public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				int idx = list.locationToIndex(e.getPoint());
				IElement element = list.getModel().getElementAt(idx);
				if (element instanceof ModElement modElement) {
					mcreator.getStatusBar()
							.setMessage(modElement.getType().getReadableName() + ": " + modElement.getName());
				}
			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				IElement selected = list.getSelectedValue();

				if (e.isConsumed())
					return;

				if (e.getButton() == MouseEvent.BUTTON3) {
					list.setSelectedIndex(list.locationToIndex(e.getPoint()));
					selected = list.getSelectedValue();

					if (selected instanceof FolderElement) {
						duplicateElement.setEnabled(false);
						codeElement.setEnabled(false);
						lockElement.setEnabled(false);
						idElement.setEnabled(false);
						renameElementFolder.setEnabled(true);
					} else {
						duplicateElement.setEnabled(true);
						codeElement.setEnabled(true);
						lockElement.setEnabled(true);
						idElement.setEnabled(true);
						renameElementFolder.setEnabled(false);
					}

					contextMenu.show(list, e.getX(), e.getY());
				} else if (e.getClickCount() == 2) {
					list.cancelDND();

					if (selected instanceof FolderElement) {
						switchFolder((FolderElement) selected);
					} else {
						if (((e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK))
							editCurrentlySelectedModElementAsCode((ModElement) selected, list, e.getX(), e.getY());
						else
							editCurrentlySelectedModElement((ModElement) selected, list, e.getX(), e.getY());
					}
				}

				renameFolder.setEnabled(selected instanceof FolderElement);
			}
		});

		JScrollPane sp = new JScrollPane(list);
		sp.setOpaque(false);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getViewport().setOpaque(false);

		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		sp.setBorder(null);

		JPanel modElementsPanel = new JPanel(new BorderLayout(0, 0));
		modElementsPanel.setOpaque(false);

		resourcesPan.setBorder(
				BorderFactory.createMatteBorder(3, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));
		localePan.setBorder(
				BorderFactory.createMatteBorder(3, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));
		variablesPan.setBorder(
				BorderFactory.createMatteBorder(3, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));
		vcsPan.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));

		JPanel slo = new JPanel(new BorderLayout(0, 3));

		JPanel se = new JPanel(new BorderLayout());

		search = new JTextField(28) {
			@Override public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(new Color(0.3f, 0.3f, 0.3f, 0.4f));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
				g.setColor(new Color(0.4f, 0.4f, 0.4f, 0.3f));
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g.setColor(Color.white);
				if (getText().equals("")) {
					g.setFont(g.getFont().deriveFont(11f));
					g.setColor(new Color(120, 120, 120));
					if (!currentTab.equals("mods")) {
						g.drawString(L10N.t("workspace.elements.list.search_list"), 8, 18);
					} else {
						g.drawString(L10N.t("workspace.elements.list.search_folder"), 8, 18);
					}
				}
			}
		};
		search.addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent e) {
				super.focusGained(e);
				search.setText("");
			}
		});

		search.setToolTipText(L10N.t("workspace.elements.list.search.tooltip"));

		search.setForeground(new Color(230, 230, 230));
		ComponentUtils.deriveFont(search, 14);
		search.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
		search.setOpaque(false);
		search.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		search.getDocument().addDocumentListener(new DocumentListener() {

			@Override public void removeUpdate(DocumentEvent arg0) {
				refilterElements();
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				refilterElements();
			}

			@Override public void changedUpdate(DocumentEvent arg0) {
				refilterElements();
			}
		});

		modElementsBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton addFolder = new JButton(new ImageIcon(
				ImageUtils.crop(ImageUtils.toBufferedImage(UIRES.get("laf.newFolder.gif").getImage()),
						new Rectangle(1, 1, 16, 16))));
		upFolder = new JButton(new ImageIcon(
				ImageUtils.crop(ImageUtils.toBufferedImage(UIRES.get("laf.upFolder.gif").getImage()),
						new Rectangle(1, 1, 16, 16))));
		renameFolder = new JButton(new ImageIcon(
				ImageUtils.crop(ImageUtils.toBufferedImage(UIRES.get("laf.renameFolder.gif").getImage()),
						new Rectangle(1, 1, 16, 16))));

		addFolder.setContentAreaFilled(false);
		addFolder.setBorderPainted(false);
		addFolder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		addFolder.setCursor(new Cursor(Cursor.HAND_CURSOR));
		addFolder.setToolTipText(L10N.t("workspace.elements.folders.add_tooltip"));

		upFolder.setContentAreaFilled(false);
		upFolder.setBorderPainted(false);
		upFolder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		upFolder.setCursor(new Cursor(Cursor.HAND_CURSOR));
		upFolder.setToolTipText(L10N.t("workspace.elements.folders.up_tooltip"));
		upFolder.setEnabled(false);

		renameFolder.setContentAreaFilled(false);
		renameFolder.setBorderPainted(false);
		renameFolder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		renameFolder.setCursor(new Cursor(Cursor.HAND_CURSOR));
		renameFolder.setToolTipText(L10N.t("workspace.elements.folders.rename_tooltip"));
		renameFolder.setEnabled(false);

		addFolder.addActionListener(e -> addNewFolder());

		upFolder.addActionListener(e -> {
			if (!currentFolder.isRoot()) {
				switchFolder(currentFolder.getParent());
			}
		});

		renameFolder.addActionListener(e -> {
			if (list.getSelectedValue() instanceof FolderElement) {
				renameFolder((FolderElement) list.getSelectedValue());
			}
		});

		JComponent folderactions = ComponentUtils.deriveFont(L10N.label("workspace.elements.list.folder_actions"), 12);
		modElementsBar.add(folderactions);

		modElementsBar.add(addFolder);
		modElementsBar.add(upFolder);
		modElementsBar.add(renameFolder);

		modElementsBar.add(new JEmptyBox(7, 1));

		JComponent isize = ComponentUtils.deriveFont(L10N.label("workspace.elements.list.icon_size"), 12);
		isize.setToolTipText(L10N.t("workspace.elements.list.icon_size.tooltip"));
		modElementsBar.add(isize);
		view.setCursor(new Cursor(Cursor.HAND_CURSOR));
		view.setContentAreaFilled(false);
		view.setOpaque(false);
		view.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		ComponentUtils.deriveFont(view, 12);
		modElementsBar.add(view);

		JRadioButtonMenuItem tilesIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.tiles"));
		tilesIcons.addActionListener(e -> {
			if (tilesIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize = PreferencesData.WorkspaceIconSize.TILES;
				updateElementListRenderer();
			}
		});
		tilesIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.TILES);
		Arrays.stream(tilesIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(tilesIcons)));
		ComponentUtils.deriveFont(tilesIcons, 12);
		tilesIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		JRadioButtonMenuItem largeIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.large"));
		largeIcons.addActionListener(e -> {
			if (largeIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize = PreferencesData.WorkspaceIconSize.LARGE;
				updateElementListRenderer();
			}
		});
		largeIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.LARGE);
		Arrays.stream(largeIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(largeIcons)));
		ComponentUtils.deriveFont(largeIcons, 12);
		largeIcons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JRadioButtonMenuItem mediumIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.medium"));
		mediumIcons.addActionListener(e -> {
			if (mediumIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize = PreferencesData.WorkspaceIconSize.MEDIUM;
				updateElementListRenderer();
			}
		});
		mediumIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.MEDIUM);
		Arrays.stream(mediumIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(mediumIcons)));
		ComponentUtils.deriveFont(mediumIcons, 12);
		mediumIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		JRadioButtonMenuItem smallIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.small"));
		smallIcons.addActionListener(e -> {
			if (smallIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize = PreferencesData.WorkspaceIconSize.SMALL;
				updateElementListRenderer();
			}
		});
		smallIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.SMALL);
		Arrays.stream(smallIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(smallIcons)));
		ComponentUtils.deriveFont(smallIcons, 12);
		smallIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		JRadioButtonMenuItem listIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.list"));
		listIcons.addActionListener(e -> {
			if (listIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize = PreferencesData.WorkspaceIconSize.LIST;
				updateElementListRenderer();
			}
		});
		listIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.LIST);
		Arrays.stream(listIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(listIcons)));
		ComponentUtils.deriveFont(listIcons, 12);
		listIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		JRadioButtonMenuItem detailsIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.details"));
		detailsIcons.addActionListener(e -> {
			if (detailsIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize = PreferencesData.WorkspaceIconSize.DETAILS;
				updateElementListRenderer();
			}
		});
		detailsIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.DETAILS);
		Arrays.stream(detailsIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(detailsIcons)));
		ComponentUtils.deriveFont(detailsIcons, 12);
		detailsIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		sp.addMouseWheelListener(new MouseAdapter() {
			@Override public void mouseWheelMoved(MouseWheelEvent e) {
				super.mouseWheelMoved(e);
				if (e.isControlDown()) {
					if (e.getWheelRotation() < 0) {
						if (detailsIcons.isSelected()) {
							listIcons.doClick();
						} else if (listIcons.isSelected()) {
							smallIcons.doClick();
						} else if (smallIcons.isSelected()) {
							mediumIcons.doClick();
						} else if (mediumIcons.isSelected()) {
							largeIcons.doClick();
						} else if (largeIcons.isSelected()) {
							tilesIcons.doClick();
						}
					} else {
						if (tilesIcons.isSelected()) {
							largeIcons.doClick();
						} else if (largeIcons.isSelected()) {
							mediumIcons.doClick();
						} else if (mediumIcons.isSelected()) {
							smallIcons.doClick();
						} else if (smallIcons.isSelected()) {
							listIcons.doClick();
						} else if (listIcons.isSelected()) {
							detailsIcons.doClick();
						}
					}
				}
			}
		});

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(tilesIcons);
		buttonGroup.add(largeIcons);
		buttonGroup.add(mediumIcons);
		buttonGroup.add(smallIcons);
		buttonGroup.add(listIcons);
		buttonGroup.add(detailsIcons);

		elementsCount.setHorizontalTextPosition(SwingConstants.LEFT);

		modElementsBar.add(new JEmptyBox(7, 1));
		modElementsBar.add(ComponentUtils.deriveFont(elementsCount, 12));
		modElementsBar.add(new JEmptyBox(5, 1));

		se.add("East", modElementsBar);

		JPanel leftPan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		leftPan.setOpaque(false);
		leftPan.add(search);

		JButton filter = L10N.button("workspace.elements.list.filter");
		JButton sort = L10N.button("workspace.elements.list.sort");

		ComponentUtils.deriveFont(filter, 11);
		filter.setMargin(new Insets(1, 3, 1, 3));
		filter.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		filter.setBorderPainted(false);

		ComponentUtils.deriveFont(sort, 11);
		sort.setMargin(new Insets(1, 3, 1, 3));
		sort.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		sort.setBorderPainted(false);

		leftPan.add(new JEmptyBox(2, 2));
		leftPan.add(filter);
		leftPan.add(new JEmptyBox(2, 2));
		leftPan.add(sort);

		se.add("West", leftPan);

		JScrollablePopupMenu filterPopup = new JScrollablePopupMenu();
		filterPopup.add(new UnregisteredAction(L10N.t("workspace.elements.list.filter_all"), e -> search.setText("")));
		filterPopup.addSeparator();
		filterPopup.add(
				new UnregisteredAction(L10N.t("workspace.elements.list.filter_locked"), e -> togglefilter("f:locked")));
		filterPopup.add(new UnregisteredAction(L10N.t("workspace.elements.list.filter_witherrors"),
				e -> togglefilter("f:err")));
		filterPopup.addSeparator();
		for (ModElementType<?> type : ModElementTypeLoader.REGISTRY) {
			filterPopup.add(new UnregisteredAction(type.getReadableName(), e -> togglefilter(
					"f:" + type.getReadableName().replace(" ", "").toLowerCase(Locale.ENGLISH))).setIcon(
					new ImageIcon(ImageUtils.resizeAA(type.getIcon().getImage(), 16))));
		}
		filter.addActionListener(e -> filterPopup.show(filter, 0, 26));

		JPopupMenu sortPopup = new JPopupMenu();
		EventButtonGroup sortOne = new EventButtonGroup();
		sortOne.addActionListener(e -> resort());
		JRadioButtonMenuItem asc = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.ascending"));
		asc.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceSortAscending);
		desc.setSelected(!PreferencesManager.PREFERENCES.hidden.workspaceSortAscending);
		sortOne.add(asc);
		sortOne.add(desc);
		sortPopup.add(asc);
		sortPopup.add(desc);
		sortPopup.addSeparator();

		EventButtonGroup sortTwo = new EventButtonGroup();
		sortTwo.addActionListener(e -> resort());

		sortTwo.add(sortDateCreated);
		sortPopup.add(sortDateCreated);

		sortTwo.add(sortName);
		sortPopup.add(sortName);

		sortTwo.add(sortType);
		sortPopup.add(sortType);

		sortTwo.add(sortLoadingOrder);
		sortPopup.add(sortLoadingOrder);

		sort.addActionListener(e -> sortPopup.show(sort, 0, 26));

		JPopupMenu viewPopup = new JPopupMenu();
		viewPopup.add(tilesIcons);
		viewPopup.add(largeIcons);
		viewPopup.add(mediumIcons);
		viewPopup.add(smallIcons);
		viewPopup.add(listIcons);
		viewPopup.add(detailsIcons);

		tilesIcons.setIcon(UIRES.get("16px.tiles.gif"));
		largeIcons.setIcon(UIRES.get("16px.large.gif"));
		mediumIcons.setIcon(UIRES.get("16px.medium.gif"));
		smallIcons.setIcon(UIRES.get("16px.small.gif"));
		listIcons.setIcon(UIRES.get("16px.list.gif"));
		detailsIcons.setIcon(UIRES.get("16px.details.gif"));

		view.addActionListener(e -> viewPopup.show(view, 0, 23));

		if (PreferencesManager.PREFERENCES.hidden.workspaceSortType == PreferencesData.WorkspaceSortType.NAME) {
			sortName.setSelected(true);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceSortType == PreferencesData.WorkspaceSortType.TYPE) {
			sortType.setSelected(true);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceSortType
				== PreferencesData.WorkspaceSortType.LOADORDER) {
			sortLoadingOrder.setSelected(true);
		} else {
			sortDateCreated.setSelected(true);
		}

		slo.setOpaque(false);
		se.setOpaque(false);

		slo.add("North", se);

		mainp.setOpaque(false);

		detailsbar.add("Center", PanelUtils.gridElements(1, 6, L10N.label("workspace.elements.details.name"),
				L10N.label("workspace.elements.details.id"), L10N.label("workspace.elements.details.type"),
				L10N.label("workspace.elements.details.lock"), L10N.label("workspace.elements.details.compile")));
		detailsbar.setBorder(BorderFactory.createEmptyBorder(4, 47, 4, 8));
		detailsbar.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		modElementsPanel.add("Center",
				PanelUtils.northAndCenterElement(PanelUtils.northAndCenterElement(elementsBreadcrumb, detailsbar, 0, 0),
						mainp));

		panels.add(modElementsPanel, "mods");
		panels.add(resourcesPan, "res");
		panels.add(localePan, "locales");
		panels.add(variablesPan, "variables");
		panels.add(vcsPan, "vcs");

		cardLayout.show(panels, "mods");

		slo.add("Center", panels);

		slo.setBorder(null);

		JPanel rotatablePanel = new JPanel();
		rotatablePanel.setLayout(new BoxLayout(rotatablePanel, BoxLayout.PAGE_AXIS));

		VerticalTabButton btt1 = new VerticalTabButton(L10N.t("workspace.category.mod_elements"));
		VerticalTabButton btt2 = new VerticalTabButton(L10N.t("workspace.category.resources"));
		VerticalTabButton btt3 = new VerticalTabButton(L10N.t("workspace.category.variables"));
		VerticalTabButton btt6 = new VerticalTabButton(L10N.t("workspace.category.localization"));
		VerticalTabButton btt7 = new VerticalTabButton(L10N.t("workspace.category.remote_workspace"));

		btt1.setContentAreaFilled(false);
		btt1.setMargin(new Insets(7, 1, 7, 2));
		btt1.setBorderPainted(false);
		btt1.setFocusPainted(false);
		btt1.setOpaque(true);
		btt1.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		btt1.setCursor(new

				Cursor(Cursor.HAND_CURSOR));
		btt1.addActionListener(e -> {
			btt1.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			btt3.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt2.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt6.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt7.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			cardLayout.show(panels, "mods");
			updateMods();
			modElementsBar.setVisible(true);
		});
		rotatablePanel.add(btt1);

		btt2.setContentAreaFilled(false);
		btt2.setMargin(new Insets(7, 1, 7, 2));
		btt2.setBorderPainted(false);
		btt2.setFocusPainted(false);
		btt2.setOpaque(true);
		btt2.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		btt2.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btt2.addActionListener(e -> {
			btt1.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt3.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt2.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			btt6.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt7.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			cardLayout.show(panels, "res");
			updateMods();
			modElementsBar.setVisible(false);
		});

		if (resourcesPan.getTabCount() > 0)
			rotatablePanel.add(btt2);

		btt3.setContentAreaFilled(false);
		btt3.setMargin(new Insets(7, 1, 7, 2));
		btt3.setBorderPainted(false);
		btt3.setFocusPainted(false);
		btt3.setOpaque(true);
		btt3.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		btt3.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btt3.addActionListener(e -> {
			btt1.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt3.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			btt2.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt6.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt7.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			cardLayout.show(panels, "variables");
			updateMods();
			modElementsBar.setVisible(false);
		});

		if (mcreator.getGeneratorStats().getBaseCoverageInfo().get("variables") != GeneratorStats.CoverageStatus.NONE)
			rotatablePanel.add(btt3);

		btt6.setContentAreaFilled(false);
		btt6.setMargin(new Insets(7, 1, 7, 2));
		btt6.setBorderPainted(false);
		btt6.setFocusPainted(false);
		btt6.setOpaque(true);
		btt6.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		btt6.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btt6.addActionListener(e -> {
			btt1.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt3.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt2.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			btt6.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			btt7.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			cardLayout.show(panels, "locales");
			updateMods();
			modElementsBar.setVisible(false);
		});

		if (mcreator.getGeneratorStats().getBaseCoverageInfo().get("i18n") != GeneratorStats.CoverageStatus.NONE)
			rotatablePanel.add(btt6);

		btt7.setContentAreaFilled(false);
		btt7.setMargin(new Insets(7, 1, 7, 2));
		btt7.setBorderPainted(false);
		btt7.setFocusPainted(false);
		btt7.setOpaque(true);
		btt7.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		btt7.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btt7.addActionListener(e -> {
			if (vcsPan.panelShown()) {
				btt1.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				btt3.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				btt2.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				btt6.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				btt7.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				cardLayout.show(panels, "vcs");
				updateMods();
				modElementsBar.setVisible(false);
			}
		});
		rotatablePanel.add(btt7);

		rotatablePanel.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

		slo.add("West", rotatablePanel);

		add("Center", slo);

		setOpaque(false);

		JPanel pne = new JPanel(new GridLayout(8, 1, 6, 6));
		pne.setOpaque(false);

		JLabel but1 = new JLabel(TiledImageCache.workspaceAdd);
		but1.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (but1.isEnabled())
					new ModTypeDropdown(mcreator).show(e.getComponent(), e.getComponent().getWidth() + 5, -3);
			}
		});
		but1.setToolTipText(L10N.t("workspace.elements.add.tooltip"));
		but1.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pne.add(but1);

		but2.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (but2.isEnabled() && list.getSelectedValue() instanceof ModElement)
					editCurrentlySelectedModElement((ModElement) list.getSelectedValue(), but2,
							e.getComponent().getWidth() + 8, 0);
			}
		});
		but2.setToolTipText(L10N.t("workspace.elements.edit.tooltip"));
		but2.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pne.add(but2);

		but2a.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (but2a.isEnabled())
					duplicateCurrentlySelectedModElement();
			}
		});
		but2a.setToolTipText(L10N.t("workspace.elements.duplicate.tooltip"));
		but2a.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pne.add(but2a);

		but3.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				deleteCurrentlySelectedModElement();
			}
		});
		but3.setToolTipText(L10N.t("workspace.elements.delete.tooltip"));
		but3.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pne.add(but3);

		but5.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (but5.isEnabled() && list.getSelectedValue() instanceof ModElement) {
					editCurrentlySelectedModElementAsCode((ModElement) list.getSelectedValue(), but5,
							e.getComponent().getWidth() + 8, 0);
				}
			}
		});
		but5.setToolTipText(L10N.t("workspace.elements.edit_code.tooltip"));
		but5.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pne.add(but5);

		but5a.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (but5a.isEnabled()) {
					lockCode();
				}
			}
		});
		but5a.setToolTipText(L10N.t("workspace.elements.lock_code_tooltip"));
		but5a.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pne.add(but5a);

		but6.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (but6.isEnabled()) {
					IElement mu = list.getSelectedValue();
					if (mu instanceof ModElement && ((ModElement) mu).getType().getBaseType() != BaseType.DATAPACK) {
						ModElement modified = ModElementIDsDialog.openModElementIDDialog(mcreator, ((ModElement) mu));
						if (modified != null)
							mcreator.getWorkspace().updateModElement(modified);
					}
				}
			}
		});
		but6.setToolTipText(L10N.t("workspace.elements.edit_registry_names.tooltip"));
		but6.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pne.add(but6);

		JPanel toolp = new JPanel(new BorderLayout(0, 0)) {
			@Override public void paintComponent(Graphics g) {
				g.setColor(new Color(0.3f, 0.3f, 0.3f, 0.4f));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		toolp.setOpaque(false);
		toolp.setBorder(BorderFactory.createEmptyBorder(3, 5, 0, 5));

		toolp.add("North", pne);

		modElementsPanel.add("West", toolp);

		JPanel emptct = new JPanel();
		emptct.setLayout(new BoxLayout(emptct, BoxLayout.LINE_AXIS));
		emptct.setOpaque(false);

		String[] workspaceEmptyTip = L10N.t("workspace.elements.empty.tip").split("%1");
		emptct.add(ComponentUtils.deriveFont(new JLabel(workspaceEmptyTip[0]), 24));
		emptct.add(new JLabel(new ImageIcon(ImageUtils.resize(TiledImageCache.workspaceAdd.getImage(), 32))));
		emptct.add(ComponentUtils.deriveFont(new JLabel(workspaceEmptyTip[1]), 24));

		JPanel emptbtpd = new JPanel(new BorderLayout());
		emptbtpd.setOpaque(false);
		emptbtpd.add("Center", emptct);
		emptbtpd.add("South", new JEmptyBox(1, 40));

		mainp.add("ep", PanelUtils.totalCenterInPanel(emptbtpd));
		mainp.add("sp", sp);

		elementsBreadcrumb.reloadPath(currentFolder, ModElement.class);

		JMenuItem openElement = new JMenuItem(L10N.t("workspace.elements.list.edit.open"));
		openElement.setFont(openElement.getFont().deriveFont(Font.BOLD));
		openElement.addActionListener(e -> {
			IElement selected = list.getSelectedValue();
			if (selected instanceof FolderElement) {
				switchFolder((FolderElement) selected);
			} else
				editCurrentlySelectedModElement((ModElement) selected, list, 0, 0);
		});

		deleteElement.setIcon(UIRES.get("16px.clear"));
		deleteElement.addActionListener(e -> deleteCurrentlySelectedModElement());

		duplicateElement.addActionListener(e -> duplicateCurrentlySelectedModElement());

		codeElement.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
			}
		});
		codeElement.addActionListener(e -> {
			IElement selected = list.getSelectedValue();
			if (selected instanceof ModElement) {
				Point clickPos = list.getMousePosition();
				editCurrentlySelectedModElementAsCode((ModElement) selected, list,
						clickPos == null ? 0 : clickPos.x - 10, clickPos == null ? 0 : clickPos.y - 10);
			}
		});

		lockElement.addActionListener(e -> lockCode());

		idElement.addActionListener(e -> {
			IElement mu = list.getSelectedValue();
			if (mu instanceof ModElement && ((ModElement) mu).getType().getBaseType() != BaseType.DATAPACK) {
				ModElement modified = ModElementIDsDialog.openModElementIDDialog(mcreator, ((ModElement) mu));
				if (modified != null)
					mcreator.getWorkspace().updateModElement(modified);
			}
		});

		JMenuItem addElementFolder = new JMenuItem(L10N.t("workspace.elements.list.edit.add.folder"));
		addElementFolder.setIcon(UIRES.get("laf.newFolder.gif"));
		addElementFolder.addActionListener(e -> addNewFolder());

		renameElementFolder.setIcon(UIRES.get("laf.renameFolder.gif"));
		renameElementFolder.addActionListener(e -> {
			if (list.getSelectedValue() instanceof FolderElement) {
				renameFolder((FolderElement) list.getSelectedValue());
			}
		});

		contextMenu.add(openElement);
		contextMenu.add(codeElement);
		contextMenu.addSeparator();
		contextMenu.add(addElementFolder);
		contextMenu.add(renameElementFolder);
		contextMenu.addSeparator();
		contextMenu.add(deleteElement);
		contextMenu.addSeparator();
		contextMenu.add(duplicateElement);
		contextMenu.add(lockElement);
		contextMenu.add(idElement);

		updateElementListRenderer();
	}

	public void switchFolder(FolderElement switchTo) {
		search.setText(null); // clear the search bar
		currentFolder = switchTo;

		reloadElements();

		// reload breadcrumb
		elementsBreadcrumb.reloadPath(currentFolder, ModElement.class);

		upFolder.setEnabled(!currentFolder.isRoot());
	}

	private void togglefilter(String filter) {
		String currentSearchText = search.getText().trim();
		if (currentSearchText.contains(filter)) {
			search.setText(currentSearchText.replace(filter, "").replaceAll("\\s{2,}", " ").trim());
		} else {
			search.setText(filter + " " + currentSearchText);
		}
	}

	private void resort() {
		if (sortName.isSelected()) {
			PreferencesManager.PREFERENCES.hidden.workspaceSortType = PreferencesData.WorkspaceSortType.NAME;
		} else if (sortType.isSelected()) {
			PreferencesManager.PREFERENCES.hidden.workspaceSortType = PreferencesData.WorkspaceSortType.TYPE;
		} else if (sortLoadingOrder.isSelected()) {
			PreferencesManager.PREFERENCES.hidden.workspaceSortType = PreferencesData.WorkspaceSortType.LOADORDER;
		} else {
			PreferencesManager.PREFERENCES.hidden.workspaceSortType = PreferencesData.WorkspaceSortType.CREATED;
		}

		refilterElements();
	}

	private void updateElementListRenderer() {
		if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.TILES) {
			list.setCellRenderer(new TilesModListRender());
			list.setFixedCellHeight(72);
			list.setFixedCellWidth(287);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			view.setIcon(UIRES.get("16px.tiles.gif"));
			view.setText(L10N.t("workspace.elements.list.tiles"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.LARGE) {
			list.setCellRenderer(new LargeIconModListRender());
			list.setFixedCellHeight(97);
			list.setFixedCellWidth(90);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			view.setIcon(UIRES.get("16px.large.gif"));
			view.setText(L10N.t("workspace.elements.list.large"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.MEDIUM) {
			list.setCellRenderer(new MediumIconModListRender());
			list.setFixedCellHeight(52);
			list.setFixedCellWidth(287);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			view.setIcon(UIRES.get("16px.medium.gif"));
			view.setText(L10N.t("workspace.elements.list.medium"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.SMALL) {
			list.setCellRenderer(new SmallIconModListRender(true));
			list.setFixedCellHeight(32);
			list.setFixedCellWidth(200);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			view.setIcon(UIRES.get("16px.small.gif"));
			view.setText(L10N.t("workspace.elements.list.small"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.LIST) {
			list.setCellRenderer(new ListIconModListRender());
			list.setFixedCellHeight(28);
			list.setFixedCellWidth(-1);
			list.setLayoutOrientation(JList.VERTICAL);
			view.setIcon(UIRES.get("16px.list.gif"));
			view.setText(L10N.t("workspace.elements.list.list"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize
				== PreferencesData.WorkspaceIconSize.DETAILS) {
			list.setCellRenderer(new DetailsIconModListRender());
			list.setFixedCellHeight(24);
			list.setFixedCellWidth(-1);
			list.setLayoutOrientation(JList.VERTICAL);
			view.setIcon(UIRES.get("16px.details.gif"));
			view.setText(L10N.t("workspace.elements.list.details"));
			detailsbar.setVisible(true);
		}
	}

	public void disableRemoving() {
		but3.setEnabled(false);
		deleteElement.setEnabled(false);
		but3.setCursor(new Cursor(Cursor.WAIT_CURSOR));
	}

	public void enableRemoving() {
		but3.setEnabled(true);
		deleteElement.setEnabled(true);
		but3.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	private void lockCode() {
		Object[] options = { L10N.t("workspace.elements.lock_modelement_lock_unlock"),
				UIManager.getString("OptionPane.cancelButtonText") };
		int n = JOptionPane.showOptionDialog(mcreator, L10N.t("workspace.elements.lock_modelement_message"),
				L10N.t("workspace.elements.lock_modelement_confirm"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[1]);
		if (n == 0) {
			ProgressDialog dial = new ProgressDialog(mcreator, L10N.t("workspace.elements.lock_modelement_title"));
			Thread t = new Thread(() -> {
				ProgressDialog.ProgressUnit p0 = new ProgressDialog.ProgressUnit(
						L10N.t("workspace.elements.lock_modelement_locking_unlocking"));
				dial.addProgress(p0);

				List<ModElement> elementsThatGotUnlocked = new ArrayList<>();
				list.getSelectedValuesList().forEach(el -> {
					if (el instanceof ModElement mu) {
						if (mu.isCodeLocked()) {
							mu.setCodeLock(false);
							mcreator.getWorkspace().updateModElement(mu);
							elementsThatGotUnlocked.add(mu); // code got unlocked, add to the list
						} else {
							mu.setCodeLock(true);
							mcreator.getWorkspace().updateModElement(mu);
						}
					}
				});
				updateMods();

				p0.ok();
				dial.refreshDisplay();

				// if we have new unlocked elements, we recreate their code
				if (elementsThatGotUnlocked.size() > 0) {
					ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
							L10N.t("workspace.elements.lock_modelement_regeneration"));
					dial.addProgress(p1);
					int i = 0;
					for (ModElement mod : elementsThatGotUnlocked) {
						GeneratableElement generatableElement = mod.getGeneratableElement();
						if (generatableElement != null) {
							// generate mod element
							mcreator.getGenerator().generateElement(generatableElement);
						}
						i++;
						p1.setPercent((int) (((float) i / (float) elementsThatGotUnlocked.size()) * 100.0f));
						dial.refreshDisplay();
					}
					p1.ok();
					dial.refreshDisplay();

					ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
							L10N.t("workspace.elements.lock_modelement_rebuilding_workspace"));
					dial.addProgress(p2);
					mcreator.actionRegistry.buildWorkspace.doAction();
					p2.ok();
					dial.refreshDisplay();
				}
				dial.hideAll();
			});
			t.start();
			dial.setVisible(true);
		}
	}

	private void duplicateCurrentlySelectedModElement() {
		if (list.getSelectedValue() instanceof ModElement mu) {
			if (mcreator.getModElementManager().hasModElementGeneratableElement(mu)) {
				String modName = VOptionPane.showInputDialog(mcreator,
						L10N.t("workspace.elements.duplicate_message", mu.getName()),
						L10N.t("workspace.elements.duplicate_element", mu.getName()), mu.getElementIcon(),
						new OptionPaneValidatior() {
							@Override public Validator.ValidationResult validate(JComponent component) {
								return new ModElementNameValidator(mcreator.getWorkspace(),
										(VTextField) component).validate();
							}
						}, L10N.t("workspace.elements.duplicate"), UIManager.getString("OptionPane.cancelButtonText"));
				if (modName != null && !modName.equals("")) {
					modName = JavaConventions.convertToValidClassName(modName);

					GeneratableElement generatableElementOriginal = mu.getGeneratableElement();
					if (generatableElementOriginal != null) {
						ModElement duplicateModElement = new ModElement(mcreator.getWorkspace(), mu, modName);

						GeneratableElement generatableElementDuplicate = mcreator.getModElementManager()
								.fromJSONtoGeneratableElement(mcreator.getModElementManager()
										.generatableElementToJSON(generatableElementOriginal), duplicateModElement);

						if (generatableElementDuplicate instanceof NamespacedGeneratableElement) {
							((NamespacedGeneratableElement) generatableElementDuplicate).name = RegistryNameFixer.fromCamelCase(
									modName);
						}

						mcreator.getGenerator().generateElement(generatableElementDuplicate);
						mcreator.getModElementManager().storeModElementPicture(generatableElementDuplicate);
						mcreator.getModElementManager().storeModElement(generatableElementDuplicate);

						if (mu.getType() == ModElementType.CODE || mu.isCodeLocked()) {
							List<GeneratorTemplate> originalFiles = mcreator.getGenerator()
									.getModElementGeneratorTemplatesList(mu);
							List<GeneratorTemplate> duplicateFiles = mcreator.getGenerator()
									.getModElementGeneratorTemplatesList(duplicateModElement);

							for (GeneratorTemplate originalTemplate : originalFiles) {
								File originalFile = originalTemplate.getFile();
								File duplicateFile = null;
								for (GeneratorTemplate newCandidate : duplicateFiles) {
									if (newCandidate.getTemplateIdentificator()
											.equals(originalTemplate.getTemplateIdentificator())) {
										duplicateFile = newCandidate.getFile();
										break;
									}
								}
								if (duplicateFile != null) {
									FileIO.copyFile(originalFile, duplicateFile);
								}
							}

							duplicateModElement.setCodeLock(true);
						}

						// if we are not in the root folder, specify the folder of the mod element
						if (!currentFolder.equals(mcreator.getWorkspace().getFoldersRoot()))
							duplicateModElement.setParentFolder(currentFolder);

						mcreator.getWorkspace().addModElement(duplicateModElement);

						updateMods();
					}
				}
			}
		}
	}

	public void editCurrentlySelectedModElement(ModElement mu, JComponent component, int x, int y) {
		if (mcreator.getModElementManager().hasModElementGeneratableElement(mu)) {
			if (mu.isCodeLocked()) {
				editCurrentlySelectedModElementAsCode(mu, component, x, y);
			} else {
				ModElementGUI<?> modeditor = mu.getType().getModElementGUI(mcreator, mu, true);
				if (modeditor != null) {
					modeditor.showView();
				}
			}
		} else {
			if (mu.isCodeLocked()) {
				editCurrentlySelectedModElementAsCode(mu, component, x, y);
			} else {
				JOptionPane.showMessageDialog(null, L10N.t("workspace.elements.edit_modelement_nosavedinstance"));
			}
		}
	}

	private void editCurrentlySelectedModElementAsCode(ModElement mu, JComponent component, int x, int y) {
		GeneratableElement ge = mu.getGeneratableElement();

		List<GeneratorTemplate> modElementFiles = mcreator.getGenerator().getModElementGeneratorTemplatesList(mu, ge);

		modElementFiles.addAll(
				mcreator.getGenerator().getModElementGlobalTemplatesList(mu.getType(), false, new AtomicInteger()));

		if (ge instanceof ICommonType) {
			Collection<BaseType> baseTypes = ((ICommonType) ge).getBaseTypesProvided();
			for (BaseType baseType : baseTypes) {
				modElementFiles.addAll(mcreator.getGenerator().getGlobalTemplatesList(
						mcreator.getGenerator().getGeneratorConfiguration().getDefinitionsProvider()
								.getBaseTypeDefinition(baseType), false, new AtomicInteger()));
			}
		}

		if (modElementFiles.size() > 1) {
			JPopupMenu codeDropdown = new JPopupMenu();
			codeDropdown.setBorder(BorderFactory.createEmptyBorder());
			codeDropdown.setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());

			boolean global = false;
			for (GeneratorTemplate modElementFile : modElementFiles) {
				if (!global && modElementFile.isGlobal()) {
					codeDropdown.addSeparator();
					global = true;
				}

				JMenuItem item = new JMenuItem(
						"<html>" + modElementFile.getFile().getName() + "<br><small color=#666666>"
								+ mcreator.getWorkspace().getWorkspaceFolder().toPath()
								.relativize(modElementFile.getFile().toPath()));
				item.setIcon(FileIcons.getIconForFile(modElementFile.getFile()));
				item.setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());
				item.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
				item.setIconTextGap(8);
				item.setBorder(BorderFactory.createEmptyBorder(3, 0, 5, 3));
				item.addActionListener(e -> ProjectFileOpener.openCodeFile(mcreator, modElementFile.getFile()));
				codeDropdown.add(item);
			}
			codeDropdown.show(component, x, y);
		} else if (modElementFiles.size() == 1) {
			ProjectFileOpener.openCodeFile(mcreator, modElementFiles.get(0).getFile());
		}
	}

	private void deleteCurrentlySelectedModElement() {
		if (but3.isEnabled()) {
			if (list.getSelectedValue() != null) {
				int n = JOptionPane.showConfirmDialog(mcreator,
						L10N.t("workspace.elements.confirm_delete_message", list.getSelectedValuesList().size()),
						L10N.t("common.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);

				if (n == 0) {
					AtomicBoolean buildNeeded = new AtomicBoolean(false);
					list.getSelectedValuesList().forEach(re -> {
						if (re instanceof ModElement) {
							if (!buildNeeded.get()) {
								GeneratableElement ge = ((ModElement) re).getGeneratableElement();
								if (ge != null && mcreator.getModElementManager().requiresElementGradleBuild(ge))
									buildNeeded.set(true);
							}

							mcreator.getWorkspace().removeModElement(((ModElement) re));
						} else if (re instanceof FolderElement folder) {

							// re-assign mod-elements from deleted folder to parent folder
							for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
								if (folder.equals(modElement.getFolderPath())) {
									modElement.setParentFolder(folder.getParent());
								}
							}

							// re-assign deleted recursive children folder's elements to parent folder too
							for (FolderElement childFolder : folder.getRecursiveFolderChildren()) {
								for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
									if (childFolder.equals(modElement.getFolderPath())) {
										modElement.setParentFolder(folder.getParent());
									}
								}
							}

							// remove folder from the parent's child list
							// all folder's child folders will be orphaned at this point too
							// and thus removed
							folder.getParent().removeChild(folder);
						}
					});
					updateMods();

					if (buildNeeded.get())
						mcreator.actionRegistry.buildWorkspace.doAction();
				}
			}
		}
	}

	private void addNewFolder() {
		String name = VOptionPane.showInputDialog(mcreator, L10N.t("workspace.elements.folders.add.message"),
				L10N.t("workspace.elements.folders.add.title"), null, folderNameValidator);

		if (name != null) {
			currentFolder.addChild(new FolderElement(name, currentFolder));
			mcreator.getWorkspace().markDirty();
			reloadElements();
		}
	}

	private void renameFolder(FolderElement selected) {
		String newName = VOptionPane.showInputDialog(mcreator, L10N.t("workspace.elements.folders.rename.message"),
				L10N.t("workspace.elements.folders.rename.title"), null, folderNameValidator);
		if (newName != null) {
			selected.setName(mcreator.getWorkspace(), newName);

			mcreator.getWorkspace().markDirty();
			reloadElements();
		}
	}

	private boolean updateRunning = false;

	public synchronized void updateMods() {
		if (updateRunning)
			return;

		updateRunning = true;

		switch (currentTab) {
		case "mods":
			this.reloadElements();
			break;
		case "res":
			resourcesPan.reloadElements();
			break;
		case "locales":
			localePan.reloadElements();
			break;
		case "variables":
			variablesPan.reloadElements();
			break;
		case "vcs":
			vcsPan.reloadElements();
			break;
		}

		updateRunning = false;
	}

	public void reloadElements() {
		if (mcreator.getWorkspaceSettings() != null) {
			// first we need to get current folder from the workspace
			// as current reference to the folder may be out of date (eg. reload from disk)
			List<FolderElement> folders = mcreator.getWorkspace().getFoldersRoot().getRecursiveFolderChildren();
			int folderIdx = folders.indexOf(currentFolder);
			if (folderIdx == -1) {
				currentFolder = mcreator.getWorkspace().getFoldersRoot();
			} else {
				currentFolder = folders.get(folderIdx);
			}

			if (mcreator.getWorkspace().getModElements().stream()
					.anyMatch(el -> currentFolder.equals(el.getFolderPath()))
					|| !currentFolder.getDirectFolderChildren().isEmpty()) {
				mainpcl.show(mainp, "sp");

				// reload list model partially in the background
				new Thread(() -> {
					List<IElement> selected = list.getSelectedValuesList();

					FilterModel newModel = new FilterModel();

					// add folders
					currentFolder.getRecursiveFolderChildren().forEach(newModel::addElement);

					// add mod elements
					mcreator.getWorkspace().getModElements().forEach(newModel::addElement);

					SwingUtilities.invokeLater(() -> {
						list.setModel(dml = newModel);

						ListUtil.setSelectedValues(list, selected);

						this.refilterElements();
					});
				}).start();
			} else {
				mainpcl.show(mainp, "ep");
			}

			if (mcreator.getWorkspace().getModElements().isEmpty()) {
				elementsCount.setText(L10N.t("workspace.stats.empty", mcreator.getWorkspaceSettings().getModName(),
						mcreator.getGenerator().getGeneratorName()));
			} else {
				elementsCount.setText(
						L10N.t("workspace.stats.current_workspace", mcreator.getWorkspaceSettings().getModName(),
								mcreator.getGenerator().getGeneratorName(),
								mcreator.getWorkspace().getModElements().size()));
			}

			if (mcreator.getWorkspaceSettings().getMCreatorDependencies().contains("mcreator_link")) {
				elementsCount.setIcon(UIRES.get("16px.link"));
			} else {
				elementsCount.setIcon(new EmptyIcon(0, 0));
			}
		}
	}

	public void refilterElements() {
		dml.refilter();
		resourcesPan.refilter();
		localePan.refilterElements();
		variablesPan.refilterElements();
		vcsPan.refilterElements();
	}

	public MCreator getMcreator() {
		return mcreator;
	}

	private class FilterModel extends DefaultListModel<IElement> {
		ArrayList<IElement> items;
		ArrayList<IElement> filterItems;

		final Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public IElement getElementAt(int index) {
			if (filterItems.size() > 0 && index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
		}

		@Override public int indexOf(Object elem) {
			if (elem instanceof IElement)
				return filterItems.indexOf(elem);
			else
				return -1;
		}

		@Override public int getSize() {
			return filterItems.size();
		}

		@Override public void addElement(IElement o) {
			items.add(o);
			refilter();
		}

		@Override public void removeAllElements() {
			super.removeAllElements();
			items.clear();
			filterItems.clear();
		}

		@Override public boolean removeElement(Object a) {
			if (a instanceof IElement) {
				items.remove(a);
				filterItems.remove(a);
			}
			return super.removeElement(a);
		}

		private void refilter() {
			filterItems.clear();
			String searchInput = search.getText();

			List<ModElementType<?>> metfilters = new ArrayList<>();
			List<String> filters = new ArrayList<>();
			List<String> keyWords = new ArrayList<>();

			Matcher m = pattern.matcher(searchInput);
			while (m.find()) {
				String pat = m.group(1);
				if (pat.contains("f:")) {
					pat = pat.replaceFirst("f:", "");
					if (pat.equals("locked") || pat.equals("ok") || pat.equals("err"))
						filters.add(pat);
					for (ModElementType<?> type : ModElementTypeLoader.REGISTRY) {
						if (pat.equals(type.getReadableName().replace(" ", "").toLowerCase(Locale.ENGLISH))) {
							metfilters.add(type);
						}
					}
				} else
					keyWords.add(pat.replace("\"", ""));
			}

			boolean flattenFolders = !searchInput.isEmpty();

			filterItems.addAll(items.stream().filter(e -> e instanceof FolderElement)
					.filter(item -> currentFolder.getDirectFolderChildren().contains(item) || (flattenFolders
							&& currentFolder.getRecursiveFolderChildren().contains(item))).filter(item -> {
						if (!filters.isEmpty() || !metfilters.isEmpty())
							return false;

						if (keyWords.size() == 0)
							return true;

						for (String key : keyWords)
							if (item.getName().toLowerCase(Locale.ENGLISH).contains(key.toLowerCase(Locale.ENGLISH)))
								return true;

						return false;
					}).collect(Collectors.toList()));

			List<ModElement> modElements = items.stream().filter(e -> e instanceof ModElement).map(e -> (ModElement) e)
					.filter(item -> currentFolder.equals(item.getFolderPath()) || (flattenFolders
							&& currentFolder.getRecursiveFolderChildren().stream()
							.anyMatch(folder -> folder.equals(item.getFolderPath())))).filter(item -> {
						if (keyWords.size() == 0)
							return true;

						for (String key : keyWords) {
							boolean match = (item.getName().toLowerCase(Locale.ENGLISH)
									.contains(key.toLowerCase(Locale.ENGLISH))) || (item.getType().getReadableName()
									.toLowerCase(Locale.ENGLISH).contains(key.toLowerCase(Locale.ENGLISH)));
							if (match)
								return true;
						}

						return false;
					}).filter(item -> {
						if (filters.size() == 0)
							return true;

						for (String f : filters) {
							switch (f) {
							case "locked":
								return item.isCodeLocked();
							case "ok":
								return item.doesCompile();
							case "err":
								return !item.doesCompile();
							}
						}
						return false;
					}).filter(item -> {
						if (metfilters.size() == 0)
							return true;

						for (ModElementType<?> type : metfilters)
							if (item.getType() == type)
								return true;
						return false;
					}).collect(Collectors.toList());

			if (!sortDateCreated.isSelected()) {
				modElements.sort((a, b) -> {
					if (sortType.isSelected()) {
						return a.getType().getReadableName().compareTo(b.getType().getReadableName());
					} else if (sortLoadingOrder.isSelected()) {
						return a.getSortID() - b.getSortID();
					} else {
						return a.getName().compareTo(b.getName());
					}
				});
			}

			if (!sortDateCreated.isSelected()) {
				filterItems.sort(Comparator.comparing(IElement::getName));
			}

			if (desc.isSelected()) {
				Collections.reverse(modElements);
				Collections.reverse(filterItems);
			}

			filterItems.addAll(modElements);

			fireContentsChanged(this, 0, getSize());
		}
	}

}
