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

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.element.*;
import net.mcreator.element.types.CustomElement;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.generator.GeneratorTemplatesList;
import net.mcreator.generator.ListTemplate;
import net.mcreator.io.FileIO;
import net.mcreator.java.JavaConventions;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.preferences.data.HiddenSection;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.UnregisteredAction;
import net.mcreator.ui.component.*;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.EventButtonGroup;
import net.mcreator.ui.component.util.ListUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.ModElementIDsDialog;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.dialogs.SearchUsagesDialog;
import net.mcreator.ui.ide.ProjectFileOpener;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.elementlist.*;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.modgui.ModTypeDropdown;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.workspace.breadcrumb.WorkspaceFolderBreadcrumb;
import net.mcreator.ui.workspace.resources.WorkspacePanelResources;
import net.mcreator.util.ColorUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.IconUtils;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.IElement;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;

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

	private final FilterModel dml = new FilterModel();
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

	private final JPanel rotatablePanel = new JPanel();
	private final Map<String, AbstractWorkspacePanel> sectionTabs = new HashMap<>();
	private final List<JButton> verticalTabs = new ArrayList<>();
	public final WorkspacePanelResources resourcesPan;

	private String currentTab = "mods";

	private final MCreator mcreator;

	private final JButton upFolder;
	private final JButton renameFolder;

	private final JLabel but2 = new JLabel(UIRES.get("wrk_edit"));
	private final JLabel but2a = new JLabel(UIRES.get("wrk_duplicate"));
	private final JLabel but3 = new JLabel(UIRES.get("wrk_delete"));
	private final JLabel but5 = new JLabel(UIRES.get("wrk_code"));
	private final JLabel but5a = new JLabel(UIRES.get("wrk_lock"));

	private final JMenuItem deleteElement = new JMenuItem(L10N.t("workspace.elements.list.edit.delete"));
	private final JMenuItem searchElement = new JMenuItem(L10N.t("common.search_usages"));
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

		this.elementsBreadcrumb = new WorkspaceFolderBreadcrumb(mcreator, 10, false);
		this.elementsBreadcrumb.setSelectionListener((element, component, event) -> {
			if (element instanceof ModElement me)
				editCurrentlySelectedModElement(me, component, event.getX(), event.getY());
			else if (element instanceof FolderElement fe)
				switchFolder(fe);
		});

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
				sectionTabs.get("mods").reloadElements();
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
						searchElement.setVisible(false);
						duplicateElement.setVisible(false);
						codeElement.setVisible(false);
						lockElement.setVisible(false);
						idElement.setVisible(false);
						renameElementFolder.setVisible(true);
					} else {
						searchElement.setVisible(true);
						duplicateElement.setVisible(true);
						codeElement.setVisible(true);
						lockElement.setVisible(true);
						idElement.setVisible(true);
						renameElementFolder.setVisible(false);
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

		list.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown() && e.isShiftDown()) {
					searchModElementsUsages();
				} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteCurrentlySelectedModElement();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					IElement selected = list.getSelectedValue();
					if (selected instanceof FolderElement) {
						switchFolder((FolderElement) selected);
					} else {
						editCurrentlySelectedModElement((ModElement) selected, list, 0, 0);
					}
				}
			}
		});

		JScrollPane sp = new JScrollPane(list);
		sp.setOpaque(false);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getViewport().setOpaque(false);
		sp.setBorder(null);

		JPanel modElementsPanel = new JPanel(new BorderLayout(0, 0));
		modElementsPanel.setOpaque(false);

		JPanel slo = new JPanel(new BorderLayout(0, 3));

		JPanel se = new JPanel(new BorderLayout());

		search = new JTextField(34) {
			@Override public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (getText().isEmpty()) {
					g.setFont(g.getFont().deriveFont(11f));
					g.setColor(new Color(120, 120, 120));
					if (!currentTab.equals("mods")) {
						g.drawString(L10N.t("workspace.elements.list.search_list"), 8, 19);
					} else {
						g.drawString(L10N.t("workspace.elements.list.search_folder"), 8, 19);
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

		ComponentUtils.deriveFont(search, 14);
		search.setOpaque(false);

		search.getDocument().addDocumentListener(new DocumentListener() {

			@Override public void removeUpdate(DocumentEvent arg0) {
				sectionTabs.values().forEach(IReloadableFilterable::refilterElements);
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				sectionTabs.values().forEach(IReloadableFilterable::refilterElements);
			}

			@Override public void changedUpdate(DocumentEvent arg0) {
				sectionTabs.values().forEach(IReloadableFilterable::refilterElements);
			}
		});

		modElementsBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton addFolder = new JButton(UIRES.get("laf.newFolder"));
		upFolder = new JButton(UIRES.get("laf.upFolder"));
		renameFolder = new JButton(UIRES.get("laf.renameFolder"));

		addFolder.setContentAreaFilled(false);
		addFolder.setBorderPainted(false);
		addFolder.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		addFolder.setCursor(new Cursor(Cursor.HAND_CURSOR));
		addFolder.setToolTipText(L10N.t("workspace.elements.folders.add_tooltip"));

		upFolder.setContentAreaFilled(false);
		upFolder.setBorderPainted(false);
		upFolder.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
		upFolder.setCursor(new Cursor(Cursor.HAND_CURSOR));
		upFolder.setToolTipText(L10N.t("workspace.elements.folders.up_tooltip"));
		upFolder.setEnabled(false);

		renameFolder.setContentAreaFilled(false);
		renameFolder.setBorderPainted(false);
		renameFolder.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
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
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.set(HiddenSection.IconSize.TILES);
				updateElementListRenderer();
			}
		});
		tilesIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.TILES);
		Arrays.stream(tilesIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(tilesIcons)));
		ComponentUtils.deriveFont(tilesIcons, 12);
		tilesIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		JRadioButtonMenuItem largeIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.large"));
		largeIcons.addActionListener(e -> {
			if (largeIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.set(HiddenSection.IconSize.LARGE);
				updateElementListRenderer();
			}
		});
		largeIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.LARGE);
		Arrays.stream(largeIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(largeIcons)));
		ComponentUtils.deriveFont(largeIcons, 12);
		largeIcons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JRadioButtonMenuItem mediumIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.medium"));
		mediumIcons.addActionListener(e -> {
			if (mediumIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.set(HiddenSection.IconSize.MEDIUM);
				updateElementListRenderer();
			}
		});
		mediumIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.MEDIUM);
		Arrays.stream(mediumIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(mediumIcons)));
		ComponentUtils.deriveFont(mediumIcons, 12);
		mediumIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		JRadioButtonMenuItem smallIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.small"));
		smallIcons.addActionListener(e -> {
			if (smallIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.set(HiddenSection.IconSize.SMALL);
				updateElementListRenderer();
			}
		});
		smallIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.SMALL);
		Arrays.stream(smallIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(smallIcons)));
		ComponentUtils.deriveFont(smallIcons, 12);
		smallIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		JRadioButtonMenuItem listIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.list"));
		listIcons.addActionListener(e -> {
			if (listIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.set(HiddenSection.IconSize.LIST);
				updateElementListRenderer();
			}
		});
		listIcons.setSelected(
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get() == HiddenSection.IconSize.LIST);
		Arrays.stream(listIcons.getChangeListeners()).forEach(e -> e.stateChanged(new ChangeEvent(listIcons)));
		ComponentUtils.deriveFont(listIcons, 12);
		listIcons.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		JRadioButtonMenuItem detailsIcons = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.details"));
		detailsIcons.addActionListener(e -> {
			if (detailsIcons.isSelected()) {
				PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.set(HiddenSection.IconSize.DETAILS);
				updateElementListRenderer();
			}
		});
		detailsIcons.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.DETAILS);
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

		JPanel filterSort = new JPanel(new GridLayout(1, 2, 0, 0));
		filterSort.setOpaque(false);

		search.setBackground(ColorUtils.applyAlpha(search.getBackground(), 150));
		search.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, filterSort);
		search.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

		JButton filter = L10N.button("workspace.elements.list.filter");
		JButton sort = L10N.button("workspace.elements.list.sort");

		filter.putClientProperty(FlatClientProperties.BUTTON_TYPE, "toolBarButton");
		sort.putClientProperty(FlatClientProperties.BUTTON_TYPE, "toolBarButton");

		filter.setPreferredSize(new Dimension(54, 0));
		sort.setPreferredSize(new Dimension(54, 0));

		filter.setCursor(new Cursor(Cursor.HAND_CURSOR));
		sort.setCursor(new Cursor(Cursor.HAND_CURSOR));

		filter.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIManager.getColor("Component.borderColor")));
		sort.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UIManager.getColor("Component.borderColor")));

		filterSort.add(filter);
		filterSort.add(sort);

		se.add("West", leftPan);

		JScrollablePopupMenu filterPopup = new JScrollablePopupMenu();
		filterPopup.add(new UnregisteredAction(L10N.t("workspace.elements.list.filter_all"), e -> search.setText("")));
		filterPopup.addSeparator();
		filterPopup.add(
				new UnregisteredAction(L10N.t("workspace.elements.list.filter_locked"), e -> toggleFilter("f:locked")));
		filterPopup.add(new UnregisteredAction(L10N.t("workspace.elements.list.filter_witherrors"),
				e -> toggleFilter("f:err")));
		filterPopup.addSeparator();
		for (ModElementType<?> type : ModElementTypeLoader.REGISTRY) {
			filterPopup.add(new UnregisteredAction(type.getReadableName(), e -> toggleFilter(
					"f:" + type.getReadableName().replace(" ", "").toLowerCase(Locale.ENGLISH))).setIcon(
					IconUtils.resize(type.getIcon(), 16, 16)));
		}

		filter.addActionListener(e -> filterPopup.show(filter, 0, 26));

		JPopupMenu sortPopup = new JPopupMenu();
		EventButtonGroup sortOne = new EventButtonGroup();
		sortOne.addActionListener(e -> resort());
		JRadioButtonMenuItem asc = new JRadioButtonMenuItem(L10N.t("workspace.elements.list.ascending"));
		asc.setSelected(PreferencesManager.PREFERENCES.hidden.workspaceSortAscending.get());
		desc.setSelected(!PreferencesManager.PREFERENCES.hidden.workspaceSortAscending.get());
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

		sort.addActionListener(e -> sortPopup.show(sort, 0, 26));

		JPopupMenu viewPopup = new JPopupMenu();
		viewPopup.add(tilesIcons);
		viewPopup.add(largeIcons);
		viewPopup.add(mediumIcons);
		viewPopup.add(smallIcons);
		viewPopup.add(listIcons);
		viewPopup.add(detailsIcons);

		tilesIcons.setIcon(UIRES.get("16px.tiles"));
		largeIcons.setIcon(UIRES.get("16px.large"));
		mediumIcons.setIcon(UIRES.get("16px.medium"));
		smallIcons.setIcon(UIRES.get("16px.small"));
		listIcons.setIcon(UIRES.get("16px.list"));
		detailsIcons.setIcon(UIRES.get("16px.details"));

		view.addActionListener(e -> viewPopup.show(view, 0, 23));

		if (PreferencesManager.PREFERENCES.hidden.workspaceSortOrder.get() == HiddenSection.SortType.NAME) {
			sortName.setSelected(true);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceSortOrder.get() == HiddenSection.SortType.TYPE) {
			sortType.setSelected(true);
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

		modElementsPanel.add("North", PanelUtils.northAndCenterElement(elementsBreadcrumb, detailsbar, 0, 0));
		modElementsPanel.add("Center", mainp);

		slo.add("Center", panels);

		slo.setBorder(null);

		rotatablePanel.setLayout(new BoxLayout(rotatablePanel, BoxLayout.PAGE_AXIS));
		rotatablePanel.setBackground(Theme.current().getBackgroundColor());
		slo.add("West", rotatablePanel);

		add("Center", slo);

		setOpaque(false);

		JPanel pne = new JPanel(new GridLayout(8, 1, 6, 6));
		pne.setOpaque(false);

		JLabel but1 = new JLabel(UIRES.get("wrk_add"));
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

		JPanel toolp = new JPanel(new BorderLayout(0, 0)) {
			@Override public void paintComponent(Graphics g) {
				g.setColor(ColorUtils.applyAlpha(Theme.current().getAltBackgroundColor(), 100));
				g.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		toolp.setOpaque(false);
		toolp.setBorder(BorderFactory.createEmptyBorder(3, 5, 0, 5));
		toolp.add("North", pne);

		JPanel emptct = new JPanel();
		emptct.setLayout(new BoxLayout(emptct, BoxLayout.LINE_AXIS));
		emptct.setOpaque(false);

		String[] workspaceEmptyTip = L10N.t("workspace.elements.empty.tip").split("%1");
		emptct.add(ComponentUtils.deriveFont(new JLabel(workspaceEmptyTip[0]), 24));
		emptct.add(new JLabel(IconUtils.resize(UIRES.get("wrk_add"), 32, 32)));
		emptct.add(ComponentUtils.deriveFont(new JLabel(workspaceEmptyTip[1]), 24));

		JPanel emptbtpd = new JPanel(new BorderLayout());
		emptbtpd.setOpaque(false);
		emptbtpd.add("Center", emptct);
		emptbtpd.add("South", new JEmptyBox(1, 40));

		mainp.add("ep", PanelUtils.totalCenterInPanel(emptbtpd));
		mainp.add("sp", sp);

		addVerticalTab("mods", L10N.t("workspace.category.mod_elements"),
				new WorkspacePanelMods(PanelUtils.westAndCenterElement(toolp, modElementsPanel)));
		addVerticalTab("resources", L10N.t("workspace.category.resources"), resourcesPan);
		addVerticalTab("tags", L10N.t("workspace.category.tags"), new WorkspacePanelTags(this));
		addVerticalTab("variables", L10N.t("workspace.category.variables"), new WorkspacePanelVariables(this));
		addVerticalTab("localization", L10N.t("workspace.category.localization"),
				new WorkspacePanelLocalizations(this));

		switchToVerticalTab("mods");

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

		searchElement.setIcon(UIRES.get("16px.search"));
		searchElement.addActionListener(e -> searchModElementsUsages());

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

		idElement.addActionListener(e -> editIDOfCurrentlySelectedModElement());

		JMenuItem addElementFolder = new JMenuItem(L10N.t("workspace.elements.list.edit.add.folder"));
		addElementFolder.setIcon(UIRES.get("laf.newFolder"));
		addElementFolder.addActionListener(e -> addNewFolder());

		renameElementFolder.setIcon(UIRES.get("laf.renameFolder"));
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
		contextMenu.add(searchElement);
		contextMenu.add(duplicateElement);
		contextMenu.add(lockElement);
		contextMenu.add(idElement);

		updateElementListRenderer();
	}

	/**
	 * Adds a new section to this workspace as well as a vertical tab button on the left that switches
	 * to the section panel when clicked.
	 *
	 * @param id      The unique identifier of the section used for reloading/filtering contained elements.
	 * @param name    The name of the section shown in the workspace.
	 * @param section The panel representing contents of the vertical tab being added.
	 */
	public void addVerticalTab(String id, String name, AbstractWorkspacePanel section) {
		if (getVerticalTab(id) != null)
			return;

		panels.add(section, id);
		sectionTabs.put(id, section);

		if (section.isSupportedInWorkspace()) {
			VerticalTabButton tab = new VerticalTabButton(name);
			tab.setName(id);
			tab.setContentAreaFilled(false);
			tab.setMargin(new Insets(7, 1, 7, 2));
			tab.setBorderPainted(false);
			tab.setFocusPainted(false);
			tab.setOpaque(true);
			tab.setBackground(Theme.current().getBackgroundColor());
			tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			tab.addActionListener(e -> switchToVerticalTab(id));
			verticalTabs.add(tab);
			rotatablePanel.add(tab);
		}
	}

	public AbstractWorkspacePanel getVerticalTab(String id) {
		return sectionTabs.get(id);
	}

	public void switchToVerticalTab(String id) {
		if (sectionTabs.get(id).canSwitchToSection()) {
			for (JButton btt : verticalTabs) {
				btt.setBackground(btt.getName().equals(id) ?
						Theme.current().getAltBackgroundColor() :
						Theme.current().getBackgroundColor());
			}
			cardLayout.show(panels, id);
			reloadElementsInCurrentTab();
			modElementsBar.setVisible(id.equals("mods"));
		}
	}

	public void switchFolder(FolderElement switchTo) {
		search.setText(null); // clear the search bar
		currentFolder = switchTo;

		sectionTabs.get("mods").reloadElements();

		// reload breadcrumb
		elementsBreadcrumb.reloadPath(currentFolder, ModElement.class);

		upFolder.setEnabled(!currentFolder.isRoot());
	}

	private void toggleFilter(String filter) {
		String currentSearchText = search.getText().trim();
		if (currentSearchText.contains(filter)) {
			search.setText(currentSearchText.replace(filter, "").replaceAll("\\s{2,}", " ").trim());
		} else {
			search.setText(filter + " " + currentSearchText);
		}
	}

	private void resort() {
		if (sortName.isSelected()) {
			PreferencesManager.PREFERENCES.hidden.workspaceSortOrder.set(HiddenSection.SortType.NAME);
		} else if (sortType.isSelected()) {
			PreferencesManager.PREFERENCES.hidden.workspaceSortOrder.set(HiddenSection.SortType.TYPE);
		} else {
			PreferencesManager.PREFERENCES.hidden.workspaceSortOrder.set(HiddenSection.SortType.CREATED);
		}

		PreferencesManager.PREFERENCES.hidden.workspaceSortAscending.set(!desc.isSelected());

		sectionTabs.values().forEach(IReloadableFilterable::refilterElements);
	}

	private void updateElementListRenderer() {
		if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get() == HiddenSection.IconSize.TILES) {
			list.setCellRenderer(new TilesModListRender());
			list.setFixedCellHeight(72);
			list.setFixedCellWidth(287);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			view.setIcon(UIRES.get("16px.tiles"));
			view.setText(L10N.t("workspace.elements.list.tiles"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.LARGE) {
			list.setCellRenderer(new LargeIconModListRender());
			list.setFixedCellHeight(97);
			list.setFixedCellWidth(90);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			view.setIcon(UIRES.get("16px.large"));
			view.setText(L10N.t("workspace.elements.list.large"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.MEDIUM) {
			list.setCellRenderer(new MediumIconModListRender());
			list.setFixedCellHeight(52);
			list.setFixedCellWidth(287);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			view.setIcon(UIRES.get("16px.medium"));
			view.setText(L10N.t("workspace.elements.list.medium"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.SMALL) {
			list.setCellRenderer(new SmallIconModListRender(true));
			list.setFixedCellHeight(32);
			list.setFixedCellWidth(200);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			view.setIcon(UIRES.get("16px.small"));
			view.setText(L10N.t("workspace.elements.list.small"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.LIST) {
			list.setCellRenderer(new ListIconModListRender());
			list.setFixedCellHeight(28);
			list.setFixedCellWidth(-1);
			list.setLayoutOrientation(JList.VERTICAL);
			view.setIcon(UIRES.get("16px.list"));
			view.setText(L10N.t("workspace.elements.list.list"));
			detailsbar.setVisible(false);
		} else if (PreferencesManager.PREFERENCES.hidden.workspaceModElementIconSize.get()
				== HiddenSection.IconSize.DETAILS) {
			list.setCellRenderer(new DetailsIconModListRender());
			list.setFixedCellHeight(24);
			list.setFixedCellWidth(-1);
			list.setLayoutOrientation(JList.VERTICAL);
			view.setIcon(UIRES.get("16px.details"));
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

	private void editIDOfCurrentlySelectedModElement() {
		IElement mu = list.getSelectedValue();
		if (mu instanceof ModElement modElement && !NamespacedGeneratableElement.class.isAssignableFrom(
				modElement.getType().getModElementStorageClass())) {
			ModElement modified = ModElementIDsDialog.openModElementIDDialog(mcreator, ((ModElement) mu));
			if (modified != null)
				mcreator.getWorkspace().markDirty();
		} else {
			JOptionPane.showMessageDialog(mcreator,
					L10N.t("workspace.elements.edit_registry_names.not_possible_message"),
					L10N.t("workspace.elements.edit_registry_names.not_possible_title"), JOptionPane.WARNING_MESSAGE);
		}
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
				dial.addProgressUnit(p0);

				List<ModElement> elementsThatGotUnlocked = new ArrayList<>();
				list.getSelectedValuesList().forEach(el -> {
					if (el instanceof ModElement mu) {
						if (mu.isCodeLocked()) {
							mu.setCodeLock(false);
							elementsThatGotUnlocked.add(mu); // code got unlocked, add to the list
						} else {
							mu.setCodeLock(true);
						}

						mcreator.getWorkspace().markDirty();
					}
				});
				reloadElementsInCurrentTab();

				p0.markStateOk();

				// if we have new unlocked elements, we recreate their code
				if (!elementsThatGotUnlocked.isEmpty()) {
					ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
							L10N.t("workspace.elements.lock_modelement_regeneration"));
					dial.addProgressUnit(p1);
					int i = 0;
					for (ModElement mod : elementsThatGotUnlocked) {
						GeneratableElement generatableElement = mod.getGeneratableElement();
						if (generatableElement != null) {
							// generate mod element
							mcreator.getGenerator().generateElement(generatableElement);
						}
						i++;
						p1.setPercent((int) (i / (float) elementsThatGotUnlocked.size() * 100));
					}
					p1.markStateOk();

					ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
							L10N.t("workspace.elements.lock_modelement_rebuilding_workspace"));
					dial.addProgressUnit(p2);
					mcreator.actionRegistry.buildWorkspace.doAction();
					p2.markStateOk();
				}
				dial.hideDialog();
			}, "CodeLock");
			t.start();
			dial.setVisible(true);
		}
	}

	private void searchModElementsUsages() {
		if (list.getSelectedValuesList().stream().anyMatch(i -> i instanceof ModElement)) {
			mcreator.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			Set<ModElement> references = new HashSet<>();
			for (IElement el : list.getSelectedValuesList()) {
				if (el instanceof ModElement mod) {
					references.addAll(ReferencesFinder.searchModElementUsages(mcreator.getWorkspace(), mod));
				}
			}

			mcreator.setCursor(Cursor.getDefaultCursor());
			SearchUsagesDialog.showUsagesDialog(mcreator, L10N.t("dialog.search_usages.type.mod_element"), references);
		}
	}

	private void duplicateCurrentlySelectedModElement() {
		if (list.getSelectedValue() instanceof ModElement mu) {
			GeneratableElement generatableElementOriginal = mu.getGeneratableElement();

			if (generatableElementOriginal != null && !(generatableElementOriginal instanceof CustomElement)) {
				WorkspaceFolderBreadcrumb.Small breadcrumb = new WorkspaceFolderBreadcrumb.Small(mcreator);

				String modName = VOptionPane.showInputDialog(mcreator,
						L10N.t("workspace.elements.duplicate_message", mu.getName()),
						L10N.t("workspace.elements.duplicate_element", mu.getName()), mu.getElementIcon(),
						new OptionPaneValidatior() {
							@Override public Validator.ValidationResult validate(JComponent component) {
								return new ModElementNameValidator(mcreator.getWorkspace(), (VTextField) component,
										L10N.t("common.mod_element_name")).validate();
							}
						}, L10N.t("workspace.elements.duplicate"), UIManager.getString("OptionPane.cancelButtonText"),
						null, breadcrumb.getInScrollPane(), null);
				if (modName != null && !modName.isEmpty()) {
					modName = JavaConventions.convertToValidClassName(modName);

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
								.getModElementGeneratorTemplatesList(generatableElementOriginal);
						List<GeneratorTemplate> duplicateFiles = mcreator.getGenerator()
								.getModElementGeneratorTemplatesList(generatableElementDuplicate);

						for (GeneratorTemplate originalTemplate : originalFiles) {
							File originalFile = originalTemplate.getFile();
							File duplicateFile = null;
							for (GeneratorTemplate newCandidate : duplicateFiles) {
								if (newCandidate.getTemplateIdentifier()
										.equals(originalTemplate.getTemplateIdentifier())) {
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

					// specify the folder of the mod element
					duplicateModElement.setParentFolder(
							Objects.requireNonNullElse(breadcrumb.getCurrentFolder(), currentFolder));

					mcreator.getWorkspace().addModElement(duplicateModElement);

					reloadElementsInCurrentTab();
				}
			}
		}
	}

	public void editCurrentlySelectedModElement(ModElement modElement, JComponent component, int x, int y) {
		if (modElement.getGeneratableElement() != null) {
			if (modElement.isCodeLocked()) {
				editCurrentlySelectedModElementAsCode(modElement, component, x, y);
			} else {
				ModElementGUI<?> modElementGUI = modElement.getType().getModElementGUI(mcreator, modElement, true);
				if (modElementGUI != null) {
					modElementGUI.showView();
				}
			}
		} else {
			if (modElement.isCodeLocked()) {
				editCurrentlySelectedModElementAsCode(modElement, component, x, y);
			} else {
				JOptionPane.showMessageDialog(null, L10N.t("workspace.elements.edit_modelement_nosavedinstance"),
						L10N.t("workspace.elements.edit_modelement_nosavedinstance.title"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void editCurrentlySelectedModElementAsCode(ModElement mu, JComponent component, int x, int y) {
		List<GeneratorTemplate> modElementFiles = mcreator.getGenerator()
				.getModElementGeneratorTemplatesList(mu.getGeneratableElement());
		List<GeneratorTemplate> modElementGlobalFiles = mcreator.getGenerator()
				.getGlobalTemplatesListForModElementType(mu.getType(), false, new AtomicInteger());
		List<GeneratorTemplatesList> modElementListFiles = mcreator.getGenerator()
				.getModElementListTemplates(mu.getGeneratableElement());

		for (BaseType baseType : mu.getBaseTypesProvided()) {
			modElementGlobalFiles.addAll(mcreator.getGenerator().getGlobalTemplatesListForDefinition(
					mcreator.getGenerator().getGeneratorConfiguration().getDefinitionsProvider()
							.getBaseTypeDefinition(baseType), false, new AtomicInteger()));
		}

		if (modElementFiles.size() + modElementGlobalFiles.size() > 1)
			new ModElementCodeDropdown(mcreator,
					modElementFiles.stream().filter(e -> !(e instanceof ListTemplate)).toList(), modElementGlobalFiles,
					modElementListFiles).show(component, x, y);
		else if (modElementFiles.size() == 1)
			ProjectFileOpener.openCodeFile(mcreator, modElementFiles.get(0).getFile());
		else if (modElementGlobalFiles.size() == 1)
			ProjectFileOpener.openCodeFile(mcreator, modElementGlobalFiles.get(0).getFile());
	}

	private void deleteCurrentlySelectedModElement() {
		if (but3.isEnabled()) {
			if (list.getSelectedValue() != null) {
				mcreator.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				Set<ModElement> references = new HashSet<>();
				for (IElement el : list.getSelectedValuesList()) {
					if (el instanceof ModElement mod)
						references.addAll(ReferencesFinder.searchModElementUsages(mcreator.getWorkspace(), mod));
				}
				list.getSelectedValuesList().stream() // exclude usages by other mod elements being removed
						.filter(e -> e instanceof ModElement).map(e -> (ModElement) e).forEach(references::remove);

				mcreator.setCursor(Cursor.getDefaultCursor());

				if (SearchUsagesDialog.showDeleteDialog(mcreator, L10N.t("dialog.search_usages.type.mod_element"),
						references, L10N.t("workspace.elements.confirm_delete_msg_suffix"))) {
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
					reloadElementsInCurrentTab();

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
			sectionTabs.get("mods").reloadElements();
		}
	}

	private void renameFolder(FolderElement selected) {
		String newName = VOptionPane.showInputDialog(mcreator, L10N.t("workspace.elements.folders.rename.message"),
				L10N.t("workspace.elements.folders.rename.title"), null, folderNameValidator);
		if (newName != null) {
			selected.setName(mcreator.getWorkspace(), newName);

			mcreator.getWorkspace().markDirty();
			sectionTabs.get("mods").reloadElements();
		}
	}

	public synchronized void reloadElementsInCurrentTab() {
		sectionTabs.get(currentTab).reloadElements();
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	private class FilterModel extends DefaultListModel<IElement> {
		ArrayList<IElement> items;
		ArrayList<IElement> filterItems;

		private final static Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

		FilterModel() {
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public void addAll(Collection<? extends IElement> collection) {
			items.addAll(collection);
			refilter();
		}

		@Override public IElement getElementAt(int index) {
			if (!filterItems.isEmpty() && index < filterItems.size())
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

						if (keyWords.isEmpty())
							return true;

						for (String key : keyWords)
							if (item.getName().toLowerCase(Locale.ENGLISH).contains(key.toLowerCase(Locale.ENGLISH)))
								return true;

						return false;
					}).toList());

			List<ModElement> modElements = items.stream().filter(e -> e instanceof ModElement).map(e -> (ModElement) e)
					.filter(item -> currentFolder.equals(item.getFolderPath()) || (flattenFolders
							&& currentFolder.getRecursiveFolderChildren().stream()
							.anyMatch(folder -> folder.equals(item.getFolderPath())))).filter(item -> {
						if (keyWords.isEmpty())
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
						if (filters.isEmpty())
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
						if (metfilters.isEmpty())
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

	private class WorkspacePanelMods extends AbstractWorkspacePanel {

		private WorkspacePanelMods(JComponent contents) {
			super(WorkspacePanel.this);
			add(contents);
		}

		@Override public void reloadElements() {
			if (mcreator.getWorkspaceSettings() != null) {
				// first we need to get current folder from the workspace
				// as current reference to the folder may be out of date (e.g. reload from disk)
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

					// add folders
					ArrayList<IElement> newDataModel = new ArrayList<>(currentFolder.getRecursiveFolderChildren());

					// add mod elements
					newDataModel.addAll(mcreator.getWorkspace().getModElements());

					List<IElement> selected = list.getSelectedValuesList();
					dml.removeAllElements();
					dml.addAll(newDataModel);
					ListUtil.setSelectedValues(list, selected);
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

		@Override public void refilterElements() {
			dml.refilter();
		}
	}

}
