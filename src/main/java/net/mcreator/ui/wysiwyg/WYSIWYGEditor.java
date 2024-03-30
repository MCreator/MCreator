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

package net.mcreator.ui.wysiwyg;

import net.mcreator.element.parts.gui.Button;
import net.mcreator.element.parts.gui.Checkbox;
import net.mcreator.element.parts.gui.Image;
import net.mcreator.element.parts.gui.Label;
import net.mcreator.element.parts.gui.TextField;
import net.mcreator.element.parts.gui.*;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.zoompane.JZoomPane;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.dialogs.wysiwyg.*;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ArrayListListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WYSIWYGEditor extends JPanel {

	//@formatter:off
	public static final List<WYSIWYGComponentRegistration<?>> COMPONENT_REGISTRY = new ArrayList<>() {{
		add(new WYSIWYGComponentRegistration<>("text_label", "addlabel", true, Label.class, LabelDialog.class));
		add(new WYSIWYGComponentRegistration<>("image", "addimage", true, Image.class, ImageDialog.class));
		add(new WYSIWYGComponentRegistration<>("button", "addbutton", false, Button.class, ButtonDialog.class));
		add(new WYSIWYGComponentRegistration<>("imagebutton", "addimagebutton", false, ImageButton.class, ImageButtonDialog.class));
		add(new WYSIWYGComponentRegistration<>("checkbox", "addcheckbox", false, Checkbox.class, CheckboxDialog.class));
		add(new WYSIWYGComponentRegistration<>("text_input", "addtextinput", false, TextField.class, TextFieldDialog.class));
		add(new WYSIWYGComponentRegistration<>("tooltip", "addtooltip", false, Tooltip.class, TooltipDialog.class));
		add(new WYSIWYGComponentRegistration<>("entity_model", "addmodel", true, EntityModel.class, EntityModelDialog.class));
		add(new WYSIWYGComponentRegistration<>("input_slot", "addinslot", false, InputSlot.class, InputSlotDialog.class));
		add(new WYSIWYGComponentRegistration<>("output_slot", "addoutslot", false, OutputSlot.class, OutputSlotDialog.class));
	}};
	//@formatter:on

	public WYSIWYG editor = new WYSIWYG(this);

	public ArrayListListModel<GUIComponent> components = new ArrayListListModel<>();
	public JList<GUIComponent> list = new JList<>(components);

	private final JButton moveComponent = new JButton(UIRES.get("18px.move"));
	private final JButton editComponent = new JButton(UIRES.get("18px.edit"));
	private final JButton removeComponent = new JButton(UIRES.get("18px.remove"));
	private final JButton moveComponentUp = new JButton(UIRES.get("18px.up"));
	private final JButton moveComponentDown = new JButton(UIRES.get("18px.down"));

	public JSpinner spa1 = new JSpinner(new SpinnerNumberModel(176, 0, 512, 1));
	public JSpinner spa2 = new JSpinner(new SpinnerNumberModel(166, 0, 512, 1));

	public JSpinner invOffX = new JSpinner(new SpinnerNumberModel(0, -4096, 4096, 1));
	public JSpinner invOffY = new JSpinner(new SpinnerNumberModel(0, -4096, 4096, 1));

	public JSpinner sx = new JSpinner(new SpinnerNumberModel(18, 1, 100, 1));
	public JSpinner sy = new JSpinner(new SpinnerNumberModel(18, 1, 100, 1));
	public JSpinner ox = new JSpinner(new SpinnerNumberModel(11, 1, 100, 1));
	public JSpinner oy = new JSpinner(new SpinnerNumberModel(15, 1, 100, 1));

	public JCheckBox snapOnGrid = L10N.checkbox("elementgui.gui.snap_components_on_grid");

	public JComboBox<String> guiType = new JComboBox<>(new String[] { "GUI without slots", "GUI with slots" });

	private boolean opening = false;

	public JCheckBox renderBgLayer = new JCheckBox((L10N.t("elementgui.gui.render_background_layer")));
	public JCheckBox doesPauseGame = new JCheckBox((L10N.t("elementgui.gui.pause_game")));
	public JComboBox<String> priority = new JComboBox<>(new String[] { "NORMAL", "HIGH", "HIGHEST", "LOW", "LOWEST" });

	public VComboBox<String> overlayBaseTexture = new SearchableComboBox<>();
	public VComboBox<String> overlayTarget = new SearchableComboBox<>(ElementUtil.getDataListAsStringArray("screens"));

	public MCreator mcreator;

	public JPanel ovst = new JPanel();

	public JPanel sidebar = new JPanel(new BorderLayout(0, 0));

	private final Map<WYSIWYGComponentRegistration<?>, JButton> addComponentButtonsMap = new HashMap<>();

	public final boolean isNotOverlayType;

	public WYSIWYGEditor(final MCreator mcreator, boolean isNotOverlayType) {
		super(new BorderLayout(5, 0));
		setOpaque(false);

		this.mcreator = mcreator;
		this.isNotOverlayType = isNotOverlayType;

		editor.isNotOverlayType = isNotOverlayType;

		spa1.setPreferredSize(new Dimension(60, 24));
		spa2.setPreferredSize(new Dimension(60, 24));
		invOffX.setPreferredSize(new Dimension(60, 24));
		invOffY.setPreferredSize(new Dimension(60, 24));

		renderBgLayer.setSelected(true);
		renderBgLayer.setOpaque(false);
		doesPauseGame.setOpaque(false);

		invOffX.setEnabled(false);
		invOffY.setEnabled(false);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addListSelectionListener(event -> {
			if (list.getSelectedValue() != null) {
				editor.setSelectedComponent(list.getSelectedValue());
				moveComponent.setEnabled(true);
				editComponent.setEnabled(true);
				removeComponent.setEnabled(true);
				moveComponentUp.setEnabled(true);
				moveComponentDown.setEnabled(true);
			} else {
				moveComponent.setEnabled(false);
				editComponent.setEnabled(false);
				removeComponent.setEnabled(false);
				moveComponentUp.setEnabled(false);
				moveComponentDown.setEnabled(false);
			}
		});

		list.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					editComponent.doClick();
				}
			}
		});

		moveComponent.addActionListener(event -> editor.moveMode());
		removeComponent.addActionListener(e -> editor.removeMode());

		moveComponentUp.addActionListener(e -> {
			boolean mu = components.moveUp(list.getSelectedIndex());
			if (mu)
				list.setSelectedIndex(list.getSelectedIndex() - 1);
		});
		moveComponentDown.addActionListener(e -> {
			boolean mu = components.moveDown(list.getSelectedIndex());
			if (mu)
				list.setSelectedIndex(list.getSelectedIndex() + 1);
		});

		editComponent.addActionListener(e -> editCurrentlySelectedComponent());

		list.setOpaque(false);
		list.setCellRenderer(new GUIComponentRenderer());

		JScrollPane span = new JScrollPane(list);
		span.setBorder(BorderFactory.createEmptyBorder());
		span.setOpaque(false);
		span.getViewport().setOpaque(false);

		JPanel adds = new JPanel();
		adds.setLayout(new BoxLayout(adds, BoxLayout.PAGE_AXIS));

		JPanel comppan = new JPanel(new BorderLayout());
		comppan.setOpaque(false);
		comppan.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getAltBackgroundColor(), 1),
				(L10N.t("elementgui.gui.component_list")), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JToolBar bar2 = new JToolBar();
		bar2.setOpaque(false);
		bar2.setFloatable(false);

		moveComponent.setToolTipText((L10N.t("elementgui.gui.move_component")));
		editComponent.setToolTipText((L10N.t("elementgui.gui.edit_component")));
		removeComponent.setToolTipText((L10N.t("elementgui.gui.remove_component")));
		moveComponentUp.setToolTipText((L10N.t("elementgui.gui.move_component_up")));
		moveComponentDown.setToolTipText((L10N.t("elementgui.gui.move_component_down")));

		moveComponent.setMargin(new Insets(1, 1, 1, 1));
		removeComponent.setMargin(new Insets(1, 1, 1, 1));
		editComponent.setMargin(new Insets(1, 1, 1, 1));
		moveComponentUp.setMargin(new Insets(1, 1, 1, 1));
		moveComponentDown.setMargin(new Insets(1, 1, 1, 1));

		bar2.add(moveComponent);
		bar2.add(moveComponentUp);
		bar2.add(moveComponentDown);
		bar2.add(editComponent);
		bar2.add(removeComponent);

		comppan.add("North", bar2);
		comppan.add("Center", span);

		JPanel add = new JPanel() {
			@Override public Component add(Component component) {
				Component c = super.add(component);
				super.add(new JEmptyBox(3, 3));
				return c;
			}
		};
		add.setOpaque(false);
		add.setLayout(new BoxLayout(add, BoxLayout.PAGE_AXIS));

		for (WYSIWYGComponentRegistration<?> componentRegistration : COMPONENT_REGISTRY) {
			if (isNotOverlayType || componentRegistration.worksInOverlay()) {
				JButton componentButton = new JButton(UIRES.get("wysiwyg_editor." + componentRegistration.icon()));
				componentButton.setToolTipText((L10N.t("elementgui.gui.add_" + componentRegistration.machineName())));
				componentButton.setMargin(new Insets(0, 0, 0, 0));
				componentButton.addActionListener(e -> {
					try {
						componentRegistration.editor()
								.getConstructor(WYSIWYGEditor.class, componentRegistration.component())
								.newInstance(this, null);
					} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
							 InvocationTargetException ex) {
						throw new RuntimeException(ex);
					}
				});
				add.add(componentButton);
				addComponentButtonsMap.put(componentRegistration, componentButton);
			}
		}

		snapOnGrid.setOpaque(false);
		snapOnGrid.addActionListener(event -> {
			editor.showGrid = snapOnGrid.isSelected();
			editor.repaint();
		});

		sx.addChangeListener(e -> {
			editor.grid_x_spacing = (int) sx.getValue();
			editor.repaint();
		});

		sy.addChangeListener(e -> {
			editor.grid_y_spacing = (int) sy.getValue();
			editor.repaint();
		});

		ox.addChangeListener(e -> {
			editor.grid_x_offset = (int) ox.getValue();
			editor.repaint();
		});

		oy.addChangeListener(e -> {
			editor.grid_y_offset = (int) oy.getValue();
			editor.repaint();
		});

		adds.add(PanelUtils.join(FlowLayout.LEFT, snapOnGrid));

		JPanel gx = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
		gx.setOpaque(false);
		gx.add(new JLabel((L10N.t("elementgui.gui.grid_x"))));
		gx.add(sx);
		gx.add(new JLabel((L10N.t("elementgui.gui.offset_x"))));
		gx.add(ox);
		adds.add(gx);

		JPanel gy = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		gy.setOpaque(false);
		gy.add(new JLabel((L10N.t("elementgui.gui.grid_y"))));
		gy.add(sy);
		gy.add(new JLabel((L10N.t("elementgui.gui.offset_y"))));
		gy.add(oy);
		adds.add(gy);

		adds.add(new JEmptyBox(1, 1));

		editComponent.setEnabled(false);
		moveComponent.setEnabled(false);
		removeComponent.setEnabled(false);
		moveComponentUp.setEnabled(false);
		moveComponentDown.setEnabled(false);

		adds.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getAltBackgroundColor(), 1),
				(L10N.t("elementgui.gui.editor_options")), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		adds.setOpaque(false);

		sidebar.add("Center", comppan);
		sidebar.add("South", adds);

		JPanel adds2 = new JPanel();
		adds2.setLayout(new BoxLayout(adds2, BoxLayout.PAGE_AXIS));
		adds2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getAltBackgroundColor(), 1),
				(L10N.t("elementgui.gui.gui_properties")), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JComponent pon = PanelUtils.westAndEastElement(new JLabel((L10N.t("elementgui.gui.gui_type"))), guiType);

		if (isNotOverlayType)
			guiType.addActionListener(event -> {
				invOffX.setEnabled(guiType.getSelectedIndex() == 1);
				invOffY.setEnabled(guiType.getSelectedIndex() == 1);
				if (guiType.getSelectedIndex() == 0 && !isOpening()) {
					int n = JOptionPane.showConfirmDialog(mcreator, (L10N.t("elementgui.gui.warning_switch_gui")),
							(L10N.t("common.warning")), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
					if (n == 0) {
						setSlotComponentsEnabled(false);

						List<GUIComponent> tmplist = new ArrayList<>(components);
						List<GUIComponent> deathNote = new ArrayList<>();
						for (GUIComponent component : components) {
							if (component instanceof Slot)
								deathNote.add(component);
						}
						tmplist.removeAll(deathNote);
						components.clear();
						components.addAll(tmplist);
					} else {
						guiType.setSelectedIndex(1);
					}
				} else if (guiType.getSelectedIndex() == 1) {
					setSlotComponentsEnabled(true);
				}
			});

		adds2.add(PanelUtils.join(FlowLayout.LEFT, pon));
		adds2.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.gui.width_height"), spa1, spa2));
		adds2.add(PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.gui.inventory_offset"), invOffX, invOffY));
		adds2.add(PanelUtils.join(FlowLayout.LEFT, renderBgLayer));
		adds2.add(PanelUtils.join(FlowLayout.LEFT, doesPauseGame));

		spa1.addChangeListener(event -> checkAndUpdateGUISize());
		spa2.addChangeListener(event -> checkAndUpdateGUISize());
		guiType.addActionListener(e -> checkAndUpdateGUISize());
		renderBgLayer.addActionListener(e -> checkAndUpdateGUISize());

		if (isNotOverlayType) {
			sidebar.add("North", adds2);
		} else {
			ovst.setOpaque(false);
			ovst.setLayout(new BoxLayout(ovst, BoxLayout.PAGE_AXIS));

			JPanel ovst2 = new JPanel(new GridLayout(3, 2, 2, 2));
			ovst2.setOpaque(false);

			ovst.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createLineBorder(Theme.current().getAltBackgroundColor(), 1),
					L10N.t("elementgui.gui.overlay_properties"), 0, 0, getFont().deriveFont(12.0f),
					Theme.current().getForegroundColor()));

			overlayBaseTexture.addActionListener(e -> editor.repaint());
			overlayBaseTexture.setPrototypeDisplayValue("XXXXXX");

			ovst2.add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("overlay/overlay_target"),
					L10N.label("elementgui.gui.overlay_target")));
			ovst2.add(overlayTarget);

			ovst2.add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("overlay/rendering_priority"),
					L10N.label("elementgui.gui.rendering_priority")));
			ovst2.add(priority);

			JButton importScreenTexture = new JButton(UIRES.get("18px.add"));
			importScreenTexture.setToolTipText(L10N.t("elementgui.gui.import_overlay_base_texture"));
			importScreenTexture.setOpaque(false);
			importScreenTexture.setMargin(new Insets(0, 0, 0, 0));
			importScreenTexture.addActionListener(e -> {
				TextureImportDialogs.importMultipleTextures(mcreator, TextureType.SCREEN);
				overlayBaseTexture.removeAllItems();
				overlayBaseTexture.addItem("");
				mcreator.getFolderManager().getTexturesList(TextureType.SCREEN)
						.forEach(el -> overlayBaseTexture.addItem(el.getName()));
			});

			ovst2.add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("overlay/base_texture"),
					L10N.label("elementgui.gui.overlay_base_texture")));
			ovst2.add(PanelUtils.centerAndEastElement(overlayBaseTexture, importScreenTexture));

			ovst.add(ovst2);
			ovst.add(new JEmptyBox(2, 2));

			sidebar.add("North", ovst);
		}

		adds2.setOpaque(false);

		editor.setOpaque(false);

		JPanel zoomHolder = new JPanel(new BorderLayout()) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Theme.current().getBackgroundColor());
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		zoomHolder.setOpaque(false);

		add.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));

		zoomHolder.add("Center", new JZoomPane(editor));
		zoomHolder.add("West", add);

		sidebar.setPreferredSize(new Dimension(250, 10));

		add("East", sidebar);
		add("Center", zoomHolder);
	}

	protected void editCurrentlySelectedComponent() {
		if (list.getSelectedValue() != null) {
			GUIComponent component = list.getSelectedValue();

			for (WYSIWYGComponentRegistration<?> componentRegistration : COMPONENT_REGISTRY) {
				if (componentRegistration.component() == component.getClass()
						&& componentRegistration.editor() != null) {
					try {
						component = componentRegistration.editor()
								.getConstructor(WYSIWYGEditor.class, componentRegistration.component())
								.newInstance(this, component).getEditingComponent();
					} catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
							 InvocationTargetException ex) {
						throw new RuntimeException(ex);
					}
					break;
				}
			}

			list.setSelectedValue(component, true);
		}
	}

	public void setSlotComponentsEnabled(boolean enable) {
		for (Map.Entry<WYSIWYGComponentRegistration<?>, JButton> entry : addComponentButtonsMap.entrySet()) {
			if (Slot.class.isAssignableFrom(entry.getKey().component())) {
				entry.getValue().setEnabled(enable);
			}
		}
	}

	private void checkAndUpdateGUISize() {
		editor.repaint();
	}

	public JComboBox<String> getGUITypeSelector() {
		return guiType;
	}

	private boolean isOpening() {
		return opening;
	}

	public void setOpening(boolean opening) {
		this.opening = opening;
	}

	public void setComponentList(List<GUIComponent> components) {
		this.components.clear();
		this.components.addAll(components);
	}

	public List<GUIComponent> getComponentList() {
		return components;
	}

	static class GUIComponentRenderer extends JLabel implements ListCellRenderer<Object> {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (isSelected) {
				setForeground(Theme.current().getBackgroundColor());
				setBackground(Theme.current().getForegroundColor());
				setOpaque(true);
			} else {
				setForeground(Theme.current().getForegroundColor());
				setOpaque(false);
			}

			setOpaque(isSelected);
			setBorder(null);
			setText(value.toString());
			return this;
		}
	}

}
