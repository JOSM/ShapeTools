// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.IconToggleButton;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

public class ShapeToolsPlugin extends Plugin {
    private static ShapeMode shMode;
    private static ShapePanelDialog sDialog;
    @SuppressWarnings("unused")
    private static IconToggleButton optButton;

    /**
     * Constructs a new {@code ShapeToolsPlugin}.
     * @param info plugin information
     */
    public ShapeToolsPlugin(PluginInformation info) { // NO_UCD (unused code)
        super(info);
        sDialog = new ShapePanelDialog();
    }

    @SuppressWarnings("unused")
    private void enableLogFile() {
        String logFile = "D:\\shapeToolsLogFile.txt";
        try {
            if (Files.exists(Paths.get(logFile))) {
                Files.delete(Paths.get(logFile));
            }
            Files.createFile(Paths.get(logFile));
            PrintStream stream = new PrintStream(new File(logFile));
            System.setOut(stream);
            System.setErr(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (newFrame != null) {
            System.out.println("MAP FRAME INIT");
            optButton = newFrame.addToggleDialog(sDialog);
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
