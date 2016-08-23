// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.tools.Shortcut;

/**
 * @author ignacio_palermo
 *
 */
public class ShapeToolsAction extends JosmAction {

    public ShapeToolsAction() {
        super(tr("GetNodes"), null,
                tr("Displays a dialog containing loaded nodes"), Shortcut
                .registerShortcut("menu:shapeTool",
                        tr("Menu: {0}", tr("ShapeTool one")),
                        KeyEvent.VK_NUMPAD3, Shortcut.CTRL), false);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        ShapeToolsDialog dialog = new ShapeToolsDialog();
        JOptionPane pane = new JOptionPane(dialog, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        JDialog dlg = pane.createDialog(Main.parent, tr("LoadNodes"));
        dlg.setVisible(true);
        dialog.setVisible(true);
    }

}
