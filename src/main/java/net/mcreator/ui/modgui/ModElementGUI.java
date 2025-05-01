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

package net.mcreator.ui.modgui;

import net.mcreator.Launcher;
import net.mcreator.element.GeneratableElement;
import net.mcreator.io.net.analytics.AnalyticsConstants;
import net.mcreator.minecraft.MCItem;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.ui.ModElementGUIEvent;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.JModElementProgressPanel;
import net.mcreator.ui.component.UnsupportedComponent;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.help.ModElementHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.codeviewer.ModElementCodeViewer;
import net.mcreator.ui.search.ITextFieldSearchable;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

public abstract class ModElementGUI<GE extends GeneratableElement> extends ViewBase implements IHelpContext,
		ITextFieldSearchable {

	private static final Logger LOG = LogManager.getLogger(ModElementGUI.class);

	private boolean editingMode;
	private MCreatorTabs.Tab tabIn;

	private boolean changed, listeningEnabled = false;
	private final ModElementChangedListener elementUpdateListener;

	@Nonnull protected final ModElement modElement;
	@Nullable private FolderElement targetFolder;

	private ModElementCreatedListener<GE> modElementCreatedListener;

	private final List<ModElementGUIPage> pages = new ArrayList<>();

	private ModElementCodeViewer<GE> modElementCodeViewer = null;
	private JSplitPane splitPane;

	private final ModElementGUISearch search = new ModElementGUISearch(this);

	public ModElementGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator);
		this.editingMode = editingMode;
		this.modElement = modElement;

		this.changed = !editingMode; // new mod elements should always warn about unsaved changes unless they are saved
		this.elementUpdateListener = () -> {
			if (listeningEnabled)
				changed = true;
		};
	}

	public final ModElementGUIPage addPage(JComponent component) {
		return addPage(component, true);
	}

	public final ModElementGUIPage addPage(JComponent component, boolean scroll) {
		return addPage(modElement.getType().getReadableName(), component, scroll);
	}

	public final ModElementGUIPage addPage(String name, JComponent component) {
		return addPage(name, component, true);
	}

	public final ModElementGUIPage addPage(String name, JComponent component, boolean scroll) {
		ModElementGUIPage page = new ModElementGUIPage(name, component, scroll);
		pages.add(page);
		return page;
	}

	public void setTargetFolder(@Nullable FolderElement targetFolder) {
		this.targetFolder = targetFolder;
	}

	public void setModElementCreatedListener(ModElementCreatedListener<GE> modElementCreatedListener) {
		this.modElementCreatedListener = modElementCreatedListener;
	}

	@Override public String getViewName() {
		return modElement.getName();
	}

	@Override public ImageIcon getViewIcon() {
		if (!editingMode)
			return modElement.getType().getIcon();

		ImageIcon modIcon = modElement.getElementIcon();
		if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0 && modIcon.getIconHeight() > 0
				&& modIcon != MCItem.DEFAULT_ICON)
			return modIcon;
		return modElement.getType().getIcon();
	}

	@Override public ViewBase showView() {
		MCREvent.event(new ModElementGUIEvent.BeforeLoading(mcreator, this.tabIn, this));

		this.tabIn = new MCreatorTabs.Tab(this, modElement);

		ViewBase retval;
		MCreatorTabs.Tab existing = mcreator.getTabs().showTabOrGetExisting(this.tabIn);
		if (existing == null) {
			mcreator.getTabs().addTab(this.tabIn);

			this.tabIn.setTabShownListener(tab -> {
				if (PreferencesManager.PREFERENCES.ui.autoReloadTabs.get()) {
					listeningEnabled = false;
					reloadDataLists();
					listeningEnabled = true;
				}
			});

			this.tabIn.setTabClosingListener(tab -> {
				if (changed && PreferencesManager.PREFERENCES.ui.remindOfUnsavedChanges.get())
					return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(mcreator,
							L10N.label("dialog.unsaved_changes.message"), L10N.t("dialog.unsaved_changes.title"),
							JOptionPane.YES_NO_OPTION);
				return true;
			});

			this.tabIn.setTabClosedListener(tab -> onViewClosed());

			retval = this;
		} else {
			retval = (ViewBase) existing.getContent();
		}

		MCREvent.event(new ModElementGUIEvent.AfterLoading(mcreator, existing, this));

		return retval;
	}

	protected final void finalizeGUI() {
		JComponent centerComponent, parameters = new JPanel();

		if (allowCodePreview())
			this.modElementCodeViewer = new ModElementCodeViewer<>(this);

		if (pages.size() > 1) {
			JModElementProgressPanel split = new JModElementProgressPanel(
					pages.stream().map(ModElementGUIPage::getComponent).toArray(Component[]::new));

			Map<Integer, AbstractButton> pagers = new HashMap<>();
			ButtonGroup buttonGroup = new ButtonGroup();

			JButton back = new JButton(UIRES.get("previous"));
			JButton forward = new JButton(UIRES.get("next"));

			back.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
			back.setFocusPainted(false);
			back.setContentAreaFilled(false);
			back.setCursor(new Cursor(Cursor.HAND_CURSOR));
			back.addActionListener(event -> {
				AggregatedValidationResult validationResult = pages.get(split.getPage()).getValidationResult();
				if (validationResult.validateIsErrorFree()) {
					pagers.get(split.getPage()).setIcon(null);
				} else {
					pagers.get(split.getPage()).setIcon(UIRES.get("16px.clear"));
				}

				split.back();
				pagers.get(split.getPage()).setSelected(true);
			});

			forward.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
			forward.setFocusPainted(false);
			forward.setContentAreaFilled(false);
			forward.setCursor(new Cursor(Cursor.HAND_CURSOR));
			forward.addActionListener(event -> {
				AggregatedValidationResult validationResult = pages.get(split.getPage()).getValidationResult();
				if (validationResult.validateIsErrorFree()) {
					pagers.get(split.getPage()).setIcon(null);
				} else {
					pagers.get(split.getPage()).setIcon(UIRES.get("16px.clear"));
				}

				split.next();
				pagers.get(split.getPage()).setSelected(true);
			});

			JPanel pager = new JPanel();
			pager.setOpaque(false);
			pager.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			pager.addMouseWheelListener(e -> {
				if (e.getWheelRotation() > 0)
					split.next();
				else
					split.back();
				pagers.get(split.getPage()).setSelected(true);
			});

			pager.add(back);
			pager.add(new JLabel(UIRES.get("separator")));

			int idx = 0;
			for (ModElementGUIPage pageEntry : pages) {
				JToggleButton page = new JToggleButton(pageEntry.getID());
				page.setBorder(null);
				page.setContentAreaFilled(false);
				page.setCursor(new Cursor(Cursor.HAND_CURSOR));
				ComponentUtils.deriveFont(page, 13);

				page.addChangeListener(e -> page.setForeground(page.isSelected() ?
						(Theme.current().getInterfaceAccentColor()) :
						(Theme.current().getForegroundColor())));
				pager.add(page);
				buttonGroup.add(page);

				int finalIdx = idx;
				page.addActionListener(e -> split.setPage(finalIdx));

				pageEntry.setShowThisPageAction(page::doClick);

				if (idx == 0)
					page.setSelected(true);

				pager.add(new JLabel(UIRES.get("separator")));

				pagers.put(idx, page);
				idx++;
			}

			pager.add(forward);

			JButton save = L10N.button("elementgui.save_mod_element");
			save.setMargin(new Insets(1, 40, 1, 40));
			save.setBackground(Theme.current().getInterfaceAccentColor());
			save.setForeground(Theme.current().getSecondAltBackgroundColor());
			save.addActionListener(event -> {
				List<ValidationGroup> errors = new ArrayList<>();
				for (int i = 0; i < pages.size(); i++) {
					AggregatedValidationResult validationResult = pages.get(i).getValidationResult();
					if (!validationResult.validateIsErrorFree()) {
						pagers.get(i).setIcon(UIRES.get("16px.clear"));
						errors.add(validationResult);
					} else {
						pagers.get(i).setIcon(null);
					}
				}

				AggregatedValidationResult validationResult = new AggregatedValidationResult(errors);
				if (validationResult.validateIsErrorFree())
					finishModCreation(true);
				else
					showErrorsMessage(validationResult);
			});

			JButton saveOnly = L10N.button("elementgui.save_keep_open");
			saveOnly.setMargin(new Insets(1, 40, 1, 40));
			saveOnly.setBackground(Theme.current().getAltBackgroundColor());
			saveOnly.setForeground(Theme.current().getForegroundColor());
			saveOnly.addActionListener(event -> {
				List<ValidationGroup> errors = new ArrayList<>();
				for (int i = 0; i < pages.size(); i++) {
					AggregatedValidationResult validationResult = pages.get(i).getValidationResult();
					if (!validationResult.validateIsErrorFree()) {
						pagers.get(i).setIcon(UIRES.get("16px.clear"));
						errors.add(validationResult);
					} else {
						pagers.get(i).setIcon(null);
					}
				}

				AggregatedValidationResult validationResult = new AggregatedValidationResult(errors);
				if (validationResult.validateIsErrorFree())
					finishModCreation(false);
				else
					showErrorsMessage(validationResult);
			});

			JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
			toolBar.setOpaque(false);
			toolBar.add(saveOnly);
			toolBar.add(save);

			JPanel toolBarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
			toolBarLeft.setOpaque(false);

			if (modElementCodeViewer != null) {
				JToggleButton codeViewer = L10N.togglebutton("elementgui.code_viewer");
				codeViewer.setMargin(new Insets(1, 40, 1, 40));
				codeViewer.addActionListener(e -> {
					if (codeViewer.isSelected()) {
						modElementCodeViewer.setVisible(true);
						splitPane.setDividerSize(10);
						splitPane.setDividerLocation(0.6);
						splitPane.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
					} else {
						modElementCodeViewer.setVisible(false);
						splitPane.setDividerSize(0);
						splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
					}
				});

				toolBarLeft.add(codeViewer);
			}

			try {
				URI helpURI = this.contextURL();
				if (helpURI != null) {
					JButton help = L10N.button("common.help");
					help.setMargin(new Insets(1, 40, 1, 40));
					toolBarLeft.add(help);
					help.addActionListener(e -> DesktopUtils.browse(helpURI));
				}
			} catch (URISyntaxException e) {
				LOG.warn("Failed to create help context", e);
			}

			toolBarLeft.add(search);

			add("North",
					ComponentUtils.applyPadding(PanelUtils.westAndEastElement(toolBarLeft, toolBar), 5, true, false,
							true, false));

			centerComponent = PanelUtils.centerAndSouthElement(parameters = split, pager);
		} else {
			JButton saveOnly = L10N.button("elementgui.save_keep_open");
			saveOnly.setMargin(new Insets(1, 40, 1, 40));
			saveOnly.setBackground(Theme.current().getAltBackgroundColor());
			saveOnly.setForeground(Theme.current().getForegroundColor());
			saveOnly.addActionListener(event -> {
				AggregatedValidationResult validationResult = pages.getFirst().getValidationResult();
				if (validationResult.validateIsErrorFree())
					finishModCreation(false);
				else
					showErrorsMessage(validationResult);
			});

			JButton save = L10N.button("elementgui.save_mod_element");
			save.setMargin(new Insets(1, 40, 1, 40));
			save.setBackground(Theme.current().getInterfaceAccentColor());
			save.setForeground(Theme.current().getSecondAltBackgroundColor());
			save.addActionListener(event -> {
				AggregatedValidationResult validationResult = pages.getFirst().getValidationResult();
				if (validationResult.validateIsErrorFree())
					finishModCreation(true);
				else
					showErrorsMessage(validationResult);
			});

			JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
			toolBar.setOpaque(false);
			toolBar.add(saveOnly);
			toolBar.add(save);

			JPanel toolBarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
			toolBarLeft.setOpaque(false);

			if (modElementCodeViewer != null) {
				JToggleButton codeViewer = L10N.togglebutton("elementgui.code_viewer");
				codeViewer.setMargin(new Insets(1, 40, 1, 40));
				codeViewer.addActionListener(e -> {
					if (codeViewer.isSelected()) {
						modElementCodeViewer.setVisible(true);
						splitPane.setDividerSize(10);
						splitPane.setDividerLocation(0.6);
						splitPane.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
					} else {
						modElementCodeViewer.setVisible(false);
						splitPane.setDividerSize(0);
						splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
					}
				});

				toolBarLeft.add(codeViewer);
			}

			try {
				URI helpURI = this.contextURL();
				if (helpURI != null) {
					JButton help = L10N.button("common.help");
					help.setMargin(new Insets(1, 40, 1, 40));
					toolBarLeft.add(help);
					help.addActionListener(e -> DesktopUtils.browse(helpURI));
				}
			} catch (URISyntaxException e) {
				LOG.warn("Failed to create help context", e);
			}

			add("North",
					ComponentUtils.applyPadding(PanelUtils.westAndEastElement(toolBarLeft, toolBar), 5, true, false,
							true, false));

			centerComponent = pages.getFirst().getComponent();
		}

		if (modElementCodeViewer != null) {
			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, centerComponent, modElementCodeViewer);
			splitPane.setOpaque(false);
			splitPane.setOneTouchExpandable(true);
			modElementCodeViewer.setVisible(false);
			splitPane.setDividerSize(0);
			add("Center", splitPane);
			modElementCodeViewer.registerUI(centerComponent);
		} else {
			add("Center", centerComponent);
		}

		reloadDataLists();

		if (editingMode) {
			@SuppressWarnings("unchecked") GE generatableElement = (GE) modElement.getGeneratableElement();
			openInEditingMode(generatableElement);
		}

		elementUpdateListener.registerUI(pages.size() > 1 ? parameters : centerComponent);
		listeningEnabled = true;

		disableUnsupportedFields();
	}

	private void disableUnsupportedFields() {
		List<String> exclusions = mcreator.getGeneratorConfiguration()
				.getUnsupportedDefinitionFields(modElement.getType());

		List<String> inclusions = mcreator.getGeneratorConfiguration()
				.getSupportedDefinitionFields(modElement.getType());

		if (exclusions != null && inclusions != null) { // can't exclude and include together
			LOG.warn("Field exclusions and inclusions can not be used at the same time. Skipping them.");
			TestUtil.failIfTestingEnvironment();
		} else if ((exclusions != null && !exclusions.isEmpty()) || (inclusions != null && !inclusions.isEmpty())) {
			Map<Container, List<Component>> includedComponents = new HashMap<>();
			for (String entry : Objects.requireNonNullElse(exclusions, inclusions)) {
				try {
					Stack<Component> hierarchy = new Stack<>();
					hierarchy.push(this);
					for (String next : entry.split("\\.")) {
						Field field = hierarchy.peek().getClass().getDeclaredField(next);
						if (!Component.class.isAssignableFrom(field.getType())) {
							hierarchy.clear(); // clear hierarchy cache to skip current entry
							break;
						}

						field.setAccessible(true);
						Component obj = (Component) field.get(hierarchy.peek());
						if (obj == null) {
							hierarchy.clear(); // clear hierarchy cache to skip current entry
							break;
						}

						hierarchy.push(obj);
					}

					// only process current entry if its target component is found
					if (hierarchy.size() < 2)
						continue;

					Component c = hierarchy.pop();
					if (inclusions != null) // register component to exclude its "neighbors" later
						includedComponents.computeIfAbsent((Container) hierarchy.peek(), e -> new ArrayList<>()).add(c);
					else // exclude the component itself
						UnsupportedComponent.markUnsupported(c);
				} catch (IllegalAccessException | NoSuchFieldException | NullPointerException e) {
					LOG.warn("Failed to access component: {}", entry, e);
					TestUtil.failIfTestingEnvironment();
				}
			}

			if (inclusions != null) { // "include" components registered before
				includedComponents.forEach((k, v) -> {
					for (Field field : k.getClass().getDeclaredFields()) {
						if (!Component.class.isAssignableFrom(field.getType()))
							continue;

						try {
							field.setAccessible(true);
							Component obj = (Component) field.get(k);
							if (!v.contains(obj)) // exclude child component if it was not registered as included
								UnsupportedComponent.markUnsupported(obj);
						} catch (IllegalAccessException e) {
							LOG.warn("Failed to access component", e);
							TestUtil.failIfTestingEnvironment();
						}
					}
				});
			}
		}
	}

	private void showErrorsMessage(AggregatedValidationResult validationResult) {
		StringBuilder stringBuilder = new StringBuilder(L10N.t("elementgui.errors.heading"));
		stringBuilder.append("<ul>");
		int count = 0;
		for (String error : validationResult.getValidationProblemMessages()) {
			stringBuilder.append("<li>").append(error).append("</li>");
			count++;
			if (count > 5) {
				stringBuilder.append("<li>").append("+ ")
						.append(validationResult.getValidationProblemMessages().size() - count).append(" more")
						.append("</li>");
				break;
			}

		}
		stringBuilder.append("</ul>");
		stringBuilder.append(L10N.t("elementgui.errors.note"));
		JOptionPane.showMessageDialog(mcreator, stringBuilder.toString(), L10N.t("elementgui.errors.title"),
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * This method implements the mod element saving and generation
	 */
	private void finishModCreation(boolean closeTab) {
		MCREvent.event(new ModElementGUIEvent.WhenSaving(mcreator, tabIn, this, !closeTab));
		GE element = getElementFromGUI();

		// if new element, specify the folder of the mod element
		if (!editingMode && mcreator instanceof ModMaker modMaker)
			modElement.setParentFolder(
					Objects.requireNonNullElse(targetFolder, modMaker.getWorkspacePanel().currentFolder));

		// add mod element to the list, it will be only added for the first time, otherwise refreshed
		// add it before generating so all references are loaded
		if (!editingMode) {
			mcreator.getWorkspace().addModElement(modElement);
		} else {
			modElement.reloadElementIcon();
			modElement.getMCItems().forEach(mcItem -> mcItem.icon.getImage().flush()); // update MCItem icons
		}

		// make sure workspace will also be saved
		mcreator.getWorkspace().markDirty();

		// save the GeneratableElement definition
		mcreator.getModElementManager().storeModElement(element);

		// we perform any custom defined after all other operations are complete
		afterGeneratableElementStored();

		// generate mod base (this is needed so imports tree generator can see base
		// files while generating imports for the mod element Java files)
		mcreator.getGenerator().generateBase();

		// generate mod element code
		mcreator.getGenerator().generateElement(element);

		// save custom mod element (preview) picture if it has one
		mcreator.getModElementManager().storeModElementPicture(element);
		modElement.reloadElementIcon(); // force another reload here in case the image changed

		// re-init mod element to pick up the new mod element picture and reload mcitems cache
		modElement.reinit(mcreator.getWorkspace());

		afterGeneratableElementGenerated();

		// build if selected and needed
		if ((Launcher.version.isDevelopment() || PreferencesManager.PREFERENCES.gradle.buildOnSave.get())
				&& mcreator.getModElementManager().requiresElementGradleBuild(element)) {
			mcreator.getActionRegistry().buildWorkspace.doAction();
		}

		mcreator.getApplication().getAnalytics().trackEvent(
				editingMode ? AnalyticsConstants.EVENT_EDIT_MOD_ELEMENT : AnalyticsConstants.EVENT_NEW_MOD_ELEMENT,
				modElement.getType().getRegistryName());

		changed = false;

		if (!editingMode && modElementCreatedListener
				!= null) // only call this event if listener is registered and we are not in editing mode
			modElementCreatedListener.modElementCreated(element);

		// at this point, ME is stored so if session was not marked as editingMode before, now it is
		editingMode = true;

		// handle tab changes
		if (this.tabIn != null && closeTab)
			mcreator.getTabs().closeTab(tabIn);
		else
			mcreator.getTabs().getTabs().stream().filter(e -> e.getContent() == this)
					.forEach(e -> e.setIcon(((ModElementGUI<?>) e.getContent()).getViewIcon()));
	}

	public @Nonnull ModElement getModElement() {
		return modElement;
	}

	protected abstract void initGUI();

	protected void afterGeneratableElementStored() {
	}

	protected void afterGeneratableElementGenerated() {
	}

	protected boolean allowCodePreview() {
		return true;
	}

	public void onViewClosed() {
		if (this instanceof IBlocklyPanelHolder holder) {
			holder.closeBlocklyPanels();
		}
	}

	public void reloadDataLists() {
	}

	@Override public JTextComponent getSearchTextField() {
		return search;
	}

	/**
	 * This method is called to open a mod element in the GUI
	 */
	protected abstract void openInEditingMode(GE generatableElement);

	public abstract GE getElementFromGUI();

	public final boolean isEditingMode() {
		return editingMode;
	}

	final List<ModElementGUIPage> getPages() {
		return pages;
	}

	public final AggregatedValidationResult validateAllPages() {
		List<ValidationGroup> errors = new ArrayList<>();
		for (ModElementGUIPage page : pages) {
			AggregatedValidationResult validationResult = page.getValidationResult();
			if (!validationResult.validateIsErrorFree())
				errors.add(validationResult);
		}
		return new AggregatedValidationResult(errors);
	}

	public interface ModElementCreatedListener<GE extends GeneratableElement> {
		void modElementCreated(GE generatableElement);
	}

	@Override @Nullable public String contextName() {
		return modElement.getType().getReadableName();
	}

	@Override @Nullable public IHelpContext withEntry(String entry) {
		try {
			return new ModElementHelpContext<>(this.contextName(), this.contextURL(), entry, this);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override @Nullable public abstract URI contextURL() throws URISyntaxException;

}
