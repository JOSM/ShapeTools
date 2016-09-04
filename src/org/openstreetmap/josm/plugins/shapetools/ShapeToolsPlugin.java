// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class ShapeToolsPlugin extends Plugin {
    private static ShapeMode shMode;

    /**
     * Constructs a new {@code ShapeToolsPlugin}.
     * @param info plugin information
     */
    public ShapeToolsPlugin(PluginInformation info) { // NO_UCD (unused code)
        super(info);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (newFrame != null) {
            System.out.println("MAP FRAME INIT");
            newFrame.addToggleDialog(new ShapePanelDialog());
            shMode = new ShapeMode(Main.map);
            shMode.enterMode();
        } else {
            shMode = null;
        }
    }

    public static ShapeMode getMode() {
        return shMode;
    }
}
