// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.widgets.JosmTextField;

/**
 * Main dialog for the plugin, toggles when the plugin is loaded
 * @author Adrian_antochi
 *
 */
public class ShapePanelDialog extends ToggleDialog {

    /**
     * Constructs a new {@code ShapePanelDialog}.
     */
    public ShapePanelDialog() {
        super(tr("Shape actions panel"), "shapePanelButton.png", tr("Shape mode control panel"),
                null, 70);
        JPanel dialogPane = new JPanel();
        GridBagLayout gbl_dialogPane = new GridBagLayout();
        gbl_dialogPane.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 122, 0, 0, 0, 0, 50, 0};
        gbl_dialogPane.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gbl_dialogPane.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0,
                Double.MIN_VALUE};
        gbl_dialogPane.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        dialogPane.setLayout(gbl_dialogPane);

        JLabel lblRotateMode = new JLabel(tr("Rotate Mode:"));
        GridBagConstraints gbc_lblRotateMode = new GridBagConstraints();
        gbc_lblRotateMode.gridwidth = 4;
        gbc_lblRotateMode.insets = new Insets(0, 0, 5, 5);
        gbc_lblRotateMode.gridx = 2;
        gbc_lblRotateMode.gridy = 2;
        dialogPane.add(lblRotateMode, gbc_lblRotateMode);

        JComboBox<String> directionComboBox = new JComboBox<>(new String[] {tr("Clockwise"), tr("Counter Clockwise")});

        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.gridwidth = 6;
        gbc_comboBox.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.gridx = 6;
        gbc_comboBox.gridy = 2;
        dialogPane.add(directionComboBox, gbc_comboBox);

        JosmTextField angleInput = new JosmTextField();
        angleInput.setHint(tr("Input angle here"));
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 12;
        gbc_textField.gridy = 2;
        dialogPane.add(angleInput, gbc_textField);
        angleInput.setColumns(10);

        JButton rotButton = new JButton(tr("Rotate"));
        GridBagConstraints gbc_btnDoRotate = new GridBagConstraints();
        gbc_btnDoRotate.anchor = GridBagConstraints.WEST;
        gbc_btnDoRotate.insets = new Insets(0, 0, 5, 5);
        gbc_btnDoRotate.gridx = 15;
        gbc_btnDoRotate.gridy = 2;
        rotButton.addMouseListener(ShapeEvents.getInstance().getRotateEvent(directionComboBox, angleInput));
        dialogPane.add(rotButton, gbc_btnDoRotate);

        JButton alignButton = new JButton(tr("Align building "));
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.anchor = GridBagConstraints.WEST;
        gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
        gbc_btnNewButton.gridx = 15;
        gbc_btnNewButton.gridy = 3;
        alignButton.addMouseListener(ShapeEvents.getInstance().getAlignBuildingEvent());
        dialogPane.add(alignButton, gbc_btnNewButton);

        JRadioButton radioBtnBuilding = new JRadioButton(tr("Select building"));
        GridBagConstraints gbc_radioButton = new GridBagConstraints();
        gbc_radioButton.anchor = GridBagConstraints.NORTHWEST;
        gbc_radioButton.gridwidth = 4;
        gbc_radioButton.insets = new Insets(0, 0, 5, 5);
        gbc_radioButton.gridx = 6;
        gbc_radioButton.gridy = 4;
        radioBtnBuilding.setMnemonic(0);
        dialogPane.add(radioBtnBuilding, gbc_radioButton);

        JRadioButton radioBtnRoad = new JRadioButton(tr("Select road"));
        GridBagConstraints gbc_radioBtnRoad = new GridBagConstraints();
        gbc_radioBtnRoad.anchor = GridBagConstraints.NORTHWEST;
        gbc_radioBtnRoad.gridwidth = 4;
        gbc_radioBtnRoad.insets = new Insets(0, 0, 5, 5);
        gbc_radioBtnRoad.gridx = 6;
        gbc_radioBtnRoad.gridy = 5;
        radioBtnRoad.setMnemonic(1);
        dialogPane.add(radioBtnRoad, gbc_radioBtnRoad);

        JRadioButton rdbtnNothing = new JRadioButton(tr("Select none"));
        GridBagConstraints gbc_rdbtnNothing = new GridBagConstraints();
        gbc_rdbtnNothing.anchor = GridBagConstraints.NORTHWEST;
        gbc_rdbtnNothing.gridwidth = 4;
        gbc_rdbtnNothing.insets = new Insets(0, 0, 5, 5);
        gbc_rdbtnNothing.gridx = 6;
        gbc_rdbtnNothing.gridy = 6;
        rdbtnNothing.setMnemonic(2);
        rdbtnNothing.setSelected(true);
        dialogPane.add(rdbtnNothing, gbc_rdbtnNothing);

        ButtonGroup group = new ButtonGroup();
        group.add(radioBtnRoad);
        group.add(radioBtnBuilding);
        group.add(rdbtnNothing);
        ShapeMode.setRadioGroup(group);

        JLabel labelEpsilon = new JLabel(tr("Min Distance:"));
        GridBagConstraints gbc_lblEpsilon = new GridBagConstraints();
        gbc_lblEpsilon.insets = new Insets(0, 0, 5, 5);
        gbc_lblEpsilon.gridx = 4;
        gbc_lblEpsilon.gridy = 7;
        dialogPane.add(labelEpsilon, gbc_lblEpsilon);

        JosmTextField epsilonInput = new JosmTextField();
        epsilonInput.setHint(tr("Input min distance"));
        GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.gridwidth = 5;
        gbc_textField_1.insets = new Insets(0, 0, 5, 5);
        gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_1.gridx = 6;
        gbc_textField_1.gridy = 7;
        dialogPane.add(epsilonInput, gbc_textField_1);
        epsilonInput.setColumns(10);

        JButton btnAlignAll = new JButton(tr("Align all buildings"));
        GridBagConstraints gbc_btnAlignBuilding = new GridBagConstraints();
        gbc_btnAlignBuilding.anchor = GridBagConstraints.WEST;
        gbc_btnAlignBuilding.insets = new Insets(0, 0, 5, 5);
        gbc_btnAlignBuilding.gridx = 12;
        gbc_btnAlignBuilding.gridy = 7;
        btnAlignAll.addMouseListener(ShapeEvents.getInstance().getAlignAllBuildings(epsilonInput));
        dialogPane.add(btnAlignAll, gbc_btnAlignBuilding);

        JButton btnDeleteOverlay = new JButton(tr("Delete overlay"));
        GridBagConstraints gbc_btnDeleteOverlay = new GridBagConstraints();
        gbc_btnDeleteOverlay.insets = new Insets(0, 0, 5, 5);
        gbc_btnDeleteOverlay.gridx = 13;
        gbc_btnDeleteOverlay.gridy = 7;
        btnDeleteOverlay.addMouseListener(ShapeEvents.getInstance().getCleanup());
        dialogPane.add(btnDeleteOverlay, gbc_btnDeleteOverlay);

        JLabel lblError = new JLabel("");
        GridBagConstraints gbc_lblNimicBine = new GridBagConstraints();
        gbc_lblNimicBine.insets = new Insets(0, 0, 0, 5);
        gbc_lblNimicBine.gridx = 12;
        gbc_lblNimicBine.gridy = 8;
        dialogPane.add(lblError, gbc_lblNimicBine);

        createLayout(dialogPane, false, null);

    }
}
