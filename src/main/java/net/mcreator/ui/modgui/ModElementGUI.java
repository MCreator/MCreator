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

import net.mcreator.element.GeneratableElement;
import net.mcreator.minecraft.MCItem;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JModElementProgressPanel;
import net.mcreator.ui.component.UnsupportedComponent;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;

public abstract class ModElementGUI<GE extends GeneratableElement> extends ViewBase implements IHelpContext {

	private static final Logger LOG = LogManager.getLogger(ModElementGUI.class);

	private final boolean editingMode;
	private MCreatorTabs.Tab tabIn;

	@Nonnull protected ModElement modElement;

	private ModElementCreatedListener<GE> modElementCreatedListener;

	private final Map<String, JComponent> pages = new LinkedHashMap<>();

	public ModElementGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator);
		this.editingMode = editingMode;
		this.modElement = modElement;
	}

	public final void addPage(JComponent component) {
		pages.put(modElement.getType().getReadableName(), component);
	}

	public final void addPage(String name, JComponent component) {
		pages.put(name, component);
	}

	public void setModElementCreatedListener(ModElementCreatedListener<GE> modElementCreatedListener) {
		this.modElementCreatedListener = modElementCreatedListener;
	}

	@Override public String getViewName() {
		return modElement.getName();
	}

	@Override public ImageIcon getViewIcon() {
		ImageIcon modIcon = modElement.getElementIcon();
		if (modIcon != null && modIcon.getImage() != null && modIcon.getIconWidth() > 0 && modIcon.getIconHeight() > 0
				&& modIcon != MCItem.DEFAULT_ICON)
			return modIcon;
		return TiledImageCache.getModTypeIcon(modElement.getType());
	}

	@Override public ViewBase showView() {
		this.tabIn = new MCreatorTabs.Tab(this, modElement);

		// reload data lists in a background thread
		this.tabIn.setTabShownListener(tab -> {
			if (PreferencesManager.PREFERENCES.ui.autoreloadTabs)
				reloadDataLists();
		});

		MCreatorTabs.Tab existing = mcreator.mcreatorTabs.showTabOrGetExisting(this.tabIn);
		if (existing == null) {
			mcreator.mcreatorTabs.addTab(this.tabIn);
			return this;
		}
		return (ViewBase) existing.getContent();
	}

	protected final void finalizeGUI() {
		finalizeGUI(true);
	}

	protected final void finalizeGUI(boolean wrapInScrollpane) {
		if (pages.size() > 1) {
			JModElementProgressPanel split = new JModElementProgressPanel(pages.values().toArray(new Component[0]));

			Map<Integer, AbstractButton> pagers = new HashMap<>();
			ButtonGroup buttonGroup = new ButtonGroup();

			JButton back = new JButton(UIRES.get("previous"));
			JButton forward = new JButton(UIRES.get("next"));

			back.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
			back.setFocusPainted(false);
			back.setContentAreaFilled(false);
			back.setCursor(new Cursor(Cursor.HAND_CURSOR));
			back.addActionListener(event -> {
				AggregatedValidationResult validationResult = validatePage(split.getPage());
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
				AggregatedValidationResult validationResult = validatePage(split.getPage());
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
			pager.add(back);
			pager.add(new JLabel(UIRES.get("separator")));

			int idx = 0;
			for (Map.Entry<String, JComponent> entry : pages.entrySet()) {
				JToggleButton page = new JToggleButton(entry.getKey());
				page.setBorder(null);
				page.setContentAreaFilled(false);
				page.setCursor(new Cursor(Cursor.HAND_CURSOR));
				ComponentUtils.deriveFont(page, 13f);

				page.addChangeListener(e -> page.setForeground(page.isSelected() ?
						((Color) UIManager.get("MCreatorLAF.MAIN_TINT")) :
						((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"))));
				pager.add(page);
				buttonGroup.add(page);

				int finalIdx = idx;
				page.addActionListener(e -> split.setPage(finalIdx));

				if (idx == 0)
					page.setSelected(true);

				pager.add(new JLabel(UIRES.get("separator")));

				pagers.put(idx, page);
				idx++;
			}

			pager.add(forward);

			JButton save = L10N.button("elementgui.save_mod_element");
			save.setMargin(new Insets(1, 40, 1, 40));
			save.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
			save.setForeground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			save.setFocusPainted(false);
			save.addActionListener(event -> {
				List<ValidationGroup> errors = new ArrayList<>();

				int i = 0;
				for (Map.Entry<String, JComponent> ignored : pages.entrySet()) {
					AggregatedValidationResult validationResult = validatePage(i);
					if (!validationResult.validateIsErrorFree()) {
						pagers.get(i).setIcon(UIRES.get("16px.clear"));
						errors.add(validationResult);
					} else {
						pagers.get(i).setIcon(null);
					}
					i++;
				}

				AggregatedValidationResult validationResult = new AggregatedValidationResult(errors);
				if (validationResult.validateIsErrorFree())
					finishModCreation(true);
				else
					showErrorsMessage(validationResult);
			});

			JButton saveOnly = L10N.button("elementgui.save_keep_open");
			saveOnly.setMargin(new Insets(1, 40, 1, 40));
			saveOnly.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			saveOnly.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			saveOnly.setFocusPainted(false);
			saveOnly.addActionListener(event -> {
				List<ValidationGroup> errors = new ArrayList<>();

				int i = 0;
				for (Map.Entry<String, JComponent> ignored : pages.entrySet()) {
					AggregatedValidationResult validationResult = validatePage(i);
					if (!validationResult.validateIsErrorFree()) {
						pagers.get(i).setIcon(UIRES.get("16px.clear"));
						errors.add(validationResult);
					} else {
						pagers.get(i).setIcon(null);
					}
					i++;
				}

				AggregatedValidationResult validationResult = new AggregatedValidationResult(errors);
				if (validationResult.validateIsErrorFree())
					finishModCreation(false);
				else
					showErrorsMessage(validationResult);
			});

			add("South", pager);

			JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
			toolBar.setOpaque(false);
			toolBar.add(saveOnly);
			toolBar.add(save);
			add("North", PanelUtils
					.maxMargin(PanelUtils.westAndEastElement(new JEmptyBox(0, 0), toolBar), 5, true, true, false,
							false));

			if (wrapInScrollpane) {
				JScrollPane splitScroll = new JScrollPane(split);
				splitScroll.setOpaque(false);
				splitScroll.getViewport().setOpaque(false);
				splitScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				splitScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				splitScroll.getVerticalScrollBar().setUnitIncrement(15);
				splitScroll.getHorizontalScrollBar().setUnitIncrement(15);
				add("Center", splitScroll);
			} else {
				add("Center", split);
			}
		} else {
			JButton saveOnly = L10N.button("elementgui.save_keep_open");
			saveOnly.setMargin(new Insets(1, 40, 1, 40));
			saveOnly.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			saveOnly.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			saveOnly.setFocusPainted(false);
			saveOnly.addActionListener(event -> {
				AggregatedValidationResult validationResult = validatePage(0);
				if (validationResult.validateIsErrorFree())
					finishModCreation(false);
				else
					showErrorsMessage(validationResult);
			});

			JButton save = L10N.button("elementgui.save_mod_element");
			save.setMargin(new Insets(1, 40, 1, 40));
			save.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
			save.setForeground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			save.setFocusPainted(false);
			save.addActionListener(event -> {
				AggregatedValidationResult validationResult = validatePage(0);
				if (validationResult.validateIsErrorFree())
					finishModCreation(true);
				else
					showErrorsMessage(validationResult);
			});

			JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
			toolBar.setOpaque(false);
			toolBar.add(saveOnly);
			toolBar.add(save);

			add("North", PanelUtils
					.maxMargin(PanelUtils.westAndEastElement(new JEmptyBox(0, 0), toolBar), 5, true, true, false,
							false));

			if (wrapInScrollpane) {
				JScrollPane splitScroll = new JScrollPane(new ArrayList<>(pages.values()).get(0));
				splitScroll.setOpaque(false);
				splitScroll.getViewport().setOpaque(false);
				splitScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				splitScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				splitScroll.getVerticalScrollBar().setUnitIncrement(15);
				splitScroll.getHorizontalScrollBar().setUnitIncrement(15);
				add("Center", splitScroll);
			} else {
				add("Center", new ArrayList<>(pages.values()).get(0));
			}
		}

		reloadDataLists();

		if (editingMode) {
			@SuppressWarnings("unchecked") GE generatableElement = (GE) modElement.getGeneratableElement();
			openInEditingMode(generatableElement);
		}

		disableUnsupportedFields();
	}

	private void disableUnsupportedFields() {
		List<String> inclusions = mcreator.getGeneratorConfiguration()
				.getSupportedDefinitionFields(modElement.getType());

		List<String> exclusions = mcreator.getGeneratorConfiguration()
				.getUnsupportedDefinitionFields(modElement.getType());

		if (inclusions != null && exclusions != null) {
			LOG.warn("Field inclusions and exclusions can not be used at the same time. Skipping them.");
			return;
		}

		if (inclusions != null) {
			Field[] fields = getClass().getDeclaredFields();
			for (Field field : fields) {
				if (Component.class.isAssignableFrom(field.getType())) {
					if (!inclusions.contains(field.getName())) {
						try {
							field.setAccessible(true);
							Component obj = (Component) field.get(this);

							Container parent = obj.getParent();
							int index = Arrays.asList(parent.getComponents()).indexOf(obj);
							parent.remove(index);
							parent.add(new UnsupportedComponent(obj), index);
						} catch (IllegalAccessException e) {
							LOG.warn("Failed to access field", e);
						}
					}
				}
			}
		}

		if (exclusions != null) {
			Field[] fields = getClass().getDeclaredFields();
			for (Field field : fields) {
				if (Component.class.isAssignableFrom(field.getType())) {
					if (!exclusions.contains(field.getName())) {
						if (exclusions.contains(field.getName())) {
							try {
								field.setAccessible(true);
								Component obj = (Component) field.get(this);

								Container parent = obj.getParent();
								int index = Arrays.asList(parent.getComponents()).indexOf(obj);
								parent.remove(index);
								parent.add(new UnsupportedComponent(obj), index);
							} catch (IllegalAccessException e) {
								LOG.warn("Failed to access field", e);
							}
						}
					} else {
						LOG.warn(field.getName()
								+ " can not be used in both inclusions and exclusions fields at the same time. The field will be enabled.");
					}
				}
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
		GE element = getElementFromGUI();

		// add mod element to the list, it will be only added for the first time, otherwise refreshed
		// add it before generating so all references are loaded
		mcreator.getWorkspace().addModElement(modElement);

		// we perform any custom defined before the generatable element is generated
		beforeGeneratableElementGenerated();

		// generate mod element code
		mcreator.getGenerator().generateElement(element);

		// save custom mod element (preview) picture if it has one
		mcreator.getModElementManager().storeModElementPicture(element);
		modElement.reinit(); // re-init mod element to pick up the new mod element picture

		// save the GeneratableElement definition
		mcreator.getModElementManager().storeModElement(element);

		// we perform any custom defined after all other operations are complete
		afterGeneratableElementStored();

		// build if selected and needed
		if (PreferencesManager.PREFERENCES.gradle.compileOnSave && mcreator.getModElementManager()
				.usesGeneratableElementJava(element))
			mcreator.actionRegistry.buildWorkspace.doAction();

		if (this.tabIn != null && closeTab)
			mcreator.mcreatorTabs.closeTab(tabIn);

		if (!editingMode && modElementCreatedListener
				!= null) // only call this event if listener registered and we are not in editing mode
			modElementCreatedListener.modElementCreated(element);
	}

	public @Nonnull ModElement getModElement() {
		return modElement;
	}

	protected abstract void initGUI();

	protected abstract AggregatedValidationResult validatePage(int page);

	protected void beforeGeneratableElementGenerated() {
	}

	protected void afterGeneratableElementStored() {
	}

	public void reloadDataLists() {
	}

	/**
	 * This method is called to open a mod element in the GUI
	 */
	protected abstract void openInEditingMode(GE generatableElement);

	public abstract GE getElementFromGUI();

	public final boolean isEditingMode() {
		return editingMode;
	}

	public interface ModElementCreatedListener<GE extends GeneratableElement> {
		void modElementCreated(GE generatableElement);
	}

	@Override @Nullable public String getContextName() {
		return modElement.getType().getReadableName();
	}

}
