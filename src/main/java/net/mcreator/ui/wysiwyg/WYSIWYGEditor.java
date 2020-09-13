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
import net.mcreator.element.parts.gui.Image;
import net.mcreator.element.parts.gui.Label;
import net.mcreator.element.parts.gui.TextField;
import net.mcreator.element.parts.gui.*;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.zoompane.JZoomPane;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.dialogs.wysiwyg.*;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.util.ArrayListListModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class WYSIWYGEditor extends JPanel {

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

	public JSpinner invOffX = new JSpinner(new SpinnerNumberModel(0, -256, 256, 1));
	public JSpinner invOffY = new JSpinner(new SpinnerNumberModel(0, -256, 256, 1));

	public JButton button = new JButton(UIRES.get("32px.addbutton"));
	public JButton text = new JButton(UIRES.get("32px.addtextinput"));
	public JButton slot1 = new JButton(UIRES.get("32px.addinslot"));
	public JButton slot2 = new JButton(UIRES.get("32px.addoutslot"));

	public JComboBox<String> lol = new JComboBox<>(new String[] { "GUI without slots", "GUI with slots" });

	private boolean opening = false;

	public JCheckBox renderBgLayer = new JCheckBox((L10N.t("elementgui.gui.render_background_layer")));
	public JCheckBox doesPauseGame = new JCheckBox((L10N.t("elementgui.gui.pause_game")));
	public JComboBox<String> priority = new JComboBox<>(new String[] { "NORMAL", "HIGH", "HIGHEST", "LOW", "LOWEST" });

	public VComboBox<String> overlayBaseTexture = new SearchableComboBox<>();

	public MCreator mcreator;

	public JPanel ovst = new JPanel();

	public JPanel sidebar = new JPanel(new BorderLayout(0, 0));

	public WYSIWYGEditor(final MCreator mcreator, boolean isNotOverlayType) {
		super(new BorderLayout(5, 0));
		setOpaque(false);

		this.mcreator = mcreator;

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
			public void mouseClicked(MouseEvent evt) {
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
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), 1), (L10N.t("elementgui.gui.component_list")),
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		TransparentToolBar bar2 = new TransparentToolBar();
		bar2.setBorder(null);
		bar2.setFloatable(false);

		moveComponent.setToolTipText((L10N.t("elementgui.gui.move_component")));
		editComponent.setToolTipText((L10N.t("elementgui.gui.edit_component")));
		removeComponent.setToolTipText((L10N.t("elementgui.gui.remove_component")));
		moveComponentUp.setToolTipText((L10N.t("elementgui.gui.move_component_up")));
		moveComponentDown.setToolTipText((L10N.t("elementgui.gui.move_component_down")));

		moveComponent.setMargin(new Insets(0, 0, 0, 0));
		removeComponent.setMargin(new Insets(0, 0, 0, 0));
		editComponent.setMargin(new Insets(0, 0, 0, 0));
		moveComponentUp.setMargin(new Insets(0, 0, 0, 0));
		moveComponentDown.setMargin(new Insets(0, 0, 0, 0));

		moveComponent.setOpaque(false);
		removeComponent.setOpaque(false);
		editComponent.setOpaque(false);
		moveComponentUp.setOpaque(false);
		moveComponentDown.setOpaque(false);

		moveComponent.setBorder(BorderFactory.createEmptyBorder());
		removeComponent.setBorder(BorderFactory.createEmptyBorder());
		editComponent.setBorder(BorderFactory.createEmptyBorder());
		moveComponentUp.setBorder(BorderFactory.createEmptyBorder());
		moveComponentDown.setBorder(BorderFactory.createEmptyBorder());

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

		button.addActionListener(event -> new ButtonDialog(this, null));

		JButton image = new JButton(UIRES.get("32px.addimage"));
		JButton label = new JButton(UIRES.get("32px.addlabel"));

		label.addActionListener(event -> new LabelDialog(this, null));
		image.addActionListener(event -> new ImageDialog(this, null));

		button.setMargin(new Insets(0, 0, 0, 0));
		button.setToolTipText((L10N.t("elementgui.gui.add_button")));

		label.setMargin(new Insets(0, 0, 0, 0));
		label.setToolTipText((L10N.t("elementgui.gui.add_text_label")));

		image.setMargin(new Insets(0, 0, 0, 0));
		image.setToolTipText((L10N.t("elementgui.gui.add_image")));

		text.setMargin(new Insets(0, 0, 0, 0));
		text.setToolTipText((L10N.t("elementgui.gui.add_text_input")));

		slot1.setMargin(new Insets(0, 0, 0, 0));
		slot1.setToolTipText((L10N.t("elementgui.gui.add_input_slot")));

		slot2.setMargin(new Insets(0, 0, 0, 0));
		slot2.setToolTipText((L10N.t("elementgui.gui.add_output_slot")));

		add.add(label);
		add.add(button);
		add.add(image);
		add.add(text);
		add.add(slot1);
		add.add(slot2);

		text.addActionListener(event -> new TextFieldDialog(this, null));
		slot1.addActionListener(e -> new InputSlotDialog(this, null));
		slot2.addActionListener(e -> new OutputSlotDialog(this, null));

		JCheckBox snapOnGrid = new JCheckBox((L10N.t("elementgui.gui.snap_components_on_grid")));
		snapOnGrid.setOpaque(false);
		snapOnGrid.addActionListener(event -> {
			editor.showGrid = snapOnGrid.isSelected();
			editor.repaint();
		});

		JSpinner sx = new JSpinner(new SpinnerNumberModel(18, 1, 100, 1));
		JSpinner sy = new JSpinner(new SpinnerNumberModel(18, 1, 100, 1));
		JSpinner ox = new JSpinner(new SpinnerNumberModel(11, 1, 100, 1));
		JSpinner oy = new JSpinner(new SpinnerNumberModel(15, 1, 100, 1));

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

		adds.add(new JEmptyBox(1,1));

		editComponent.setEnabled(false);
		moveComponent.setEnabled(false);
		removeComponent.setEnabled(false);
		moveComponentUp.setEnabled(false);
		moveComponentDown.setEnabled(false);

		adds.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), 1), (L10N.t("elementgui.gui.editor_options")),
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		adds.setOpaque(false);

		sidebar.add("Center", comppan);
		sidebar.add("South", adds);

		JPanel adds2 = new JPanel();
		adds2.setLayout(new BoxLayout(adds2, BoxLayout.PAGE_AXIS));
		adds2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), 1), (L10N.t("elementgui.gui.gui_properties")),
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JComponent pon = PanelUtils.westAndEastElement(new JLabel((L10N.t("elementgui.gui.gui_type"))), lol);

		if (isNotOverlayType)
			lol.addActionListener(event -> {
				invOffX.setEnabled(lol.getSelectedIndex() == 1);
				invOffY.setEnabled(lol.getSelectedIndex() == 1);
				if (lol.getSelectedIndex() == 0 && !isOpening()) {
					Object[] options = { "Yes", "No" };
					int n = JOptionPane.showOptionDialog(mcreator,
							(L10N.t("elementgui.gui.warning_switch_gui")),
							(L10N.t("elementgui.gui.warning")), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
							options[0]);
					if (n == 0) {
						slot1.setEnabled(false);
						slot2.setEnabled(false);

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
						lol.setSelectedIndex(1);
					}
				} else if (lol.getSelectedIndex() == 1) {
					if ((int) spa1.getValue() < 176)
						spa1.setValue(176);
					if ((int) spa2.getValue() < 166)
						spa2.setValue(166);

					slot1.setEnabled(true);
					slot2.setEnabled(true);
				}
			});

		adds2.add(PanelUtils.join(FlowLayout.LEFT, pon));
		adds2.add(PanelUtils.join(FlowLayout.LEFT, new JLabel("GUI WxH: "), spa1, new JLabel("x"), spa2));
		adds2.add(PanelUtils.join(FlowLayout.LEFT, new JLabel(L10N.t("elementgui.gui.inventory_ofsset")), invOffX, invOffY));
		adds2.add(PanelUtils.join(FlowLayout.LEFT, renderBgLayer));
		adds2.add(PanelUtils.join(FlowLayout.LEFT, doesPauseGame));

		spa1.addChangeListener(event -> checkAndUpdateGUISize());
		spa2.addChangeListener(event -> checkAndUpdateGUISize());
		lol.addActionListener(e -> checkAndUpdateGUISize());
		renderBgLayer.addActionListener(e -> checkAndUpdateGUISize());

		if (isNotOverlayType) {
			sidebar.add("North", adds2);
		} else {
			ovst.setOpaque(false);
			ovst.setLayout(new BoxLayout(ovst, BoxLayout.PAGE_AXIS));
			ovst.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), 1),
					"Overlay properties", 0, 0, getFont().deriveFont(12.0f),
					(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

			overlayBaseTexture.addActionListener(e -> editor.repaint());
			overlayBaseTexture.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");
			ComponentUtils.deriveFont(overlayBaseTexture, 16);

			ovst.add(PanelUtils.join(FlowLayout.LEFT, HelpUtils
					.wrapWithHelpButton(IHelpContext.NONE.withEntry("overlay/rendering_priority"),
							new JLabel("Rendering priority:")), priority));

			JButton importmobtexture = new JButton(UIRES.get("18px.add"));
			importmobtexture.setToolTipText("Click this to import overlay base texture");
			importmobtexture.setOpaque(false);
			importmobtexture.setMargin(new Insets(0, 0, 0, 0));
			importmobtexture.addActionListener(e -> {
				TextureImportDialogs.importOtherTextures(mcreator);
				overlayBaseTexture.removeAllItems();
				overlayBaseTexture.addItem("");
				mcreator.getWorkspace().getFolderManager().getOtherTexturesList()
						.forEach(el -> overlayBaseTexture.addItem(el.getName()));
			});

			ovst.add(PanelUtils.northAndCenterElement(HelpUtils
							.wrapWithHelpButton(IHelpContext.NONE.withEntry("overlay/base_texture"),
									new JLabel("Overlay base texture:")),
					PanelUtils.join(FlowLayout.LEFT, overlayBaseTexture, importmobtexture)));

			sidebar.add("North", ovst);
		}

		adds2.setOpaque(false);

		editor.setOpaque(false);

		JPanel zoomHolder = new JPanel(new BorderLayout()) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
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
		sidebar.setOpaque(false);

		add("East", sidebar);
		add("Center", zoomHolder);
	}

	protected void editCurrentlySelectedComponent() {
		if (list.getSelectedValue() != null) {
			GUIComponent component = list.getSelectedValue();
			if (component instanceof Label) {
				new LabelDialog(this, (Label) component);
			} else if (component instanceof Button) {
				new ButtonDialog(this, (Button) component);
			} else if (component instanceof TextField) {
				new TextFieldDialog(this, (TextField) component);
			} else if (component instanceof InputSlot) {
				new InputSlotDialog(this, (InputSlot) component);
			} else if (component instanceof OutputSlot) {
				new OutputSlotDialog(this, (OutputSlot) component);
			} else if (component instanceof Image) {
				new ImageDialog(this, (Image) component);
			} else {
				JOptionPane.showMessageDialog(mcreator, "This component can only be repositioned or removed!",
						"Edit component", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private void checkAndUpdateGUISize() {
		editor.repaint();
	}

	public JComboBox<String> getGUITypeSelector() {
		return lol;
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
				setForeground(Color.black);
				setBackground(Color.white);
				setOpaque(true);
			} else {
				setForeground(Color.white);
				setOpaque(false);
			}

			setOpaque(isSelected);
			setBorder(null);
			setText(value.toString());
			return this;
		}
	}

}
