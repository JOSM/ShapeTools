// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.mapmode.MapMode;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.MapFrame;

public class ShapeMode extends MapMode {
    static ButtonGroup group;
    static DrawableSegmentBuilding buildingSegm;
    static DrawableSegmentRoad roadSegm;

    /**
     * Constructs a new {@code ShapeMode}.
     * @param mapFrame map frame
     */
    public ShapeMode(MapFrame mapFrame) {
        super("ShapeMode", "mode.png", "shapeModeTooltip", mapFrame, Cursor.getDefaultCursor());
    }

    @Override
    public void enterMode() {
        super.enterMode();
        Main.map.mapView.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            boolean ctrlPressed = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0;
            boolean altPressed = (e.getModifiers() & (ActionEvent.ALT_MASK | InputEvent.ALT_GRAPH_MASK)) != 0;

            WaySegment selectedSegment = Main.map.mapView.getNearestWaySegment(new java.awt.Point(e.getX(), e.getY()), OsmPrimitive::isUsable);
            if (group != null) {
                switch (group.getSelection().getMnemonic()) {
                case 0:
                    if (selectedSegment != null) {
                        if (buildingSegm != null) {
                            Main.map.mapView.removeTemporaryLayer(buildingSegm);
                        }
                        buildingSegm = new DrawableSegmentBuilding(selectedSegment);
                        System.out.println("SELECTED BUILDING SEGMENT " + buildingSegm);
                        Main.map.mapView.addTemporaryLayer(buildingSegm);
                    }
                    break;
                case 1:
                    if (selectedSegment != null) {
                        if (roadSegm != null) {
                            Main.map.mapView.removeTemporaryLayer(roadSegm);
                        }
                        roadSegm = new DrawableSegmentRoad(selectedSegment);
                        System.out.println("SELECTED ROAD SEGMENT " + roadSegm);
                        Main.map.mapView.addTemporaryLayer(roadSegm);
                    }
                    break;
                case 2:
                    break;
                default:
                    break;
                }
            }
            if (ctrlPressed && altPressed) {
                cleanup();
            }
        }
        Main.map.repaint();
    }

    /**
     * Removes all temporary layers added to the current mapView
     */
    public static void cleanup() {
        Main.map.mapView.removeTemporaryLayer(roadSegm);
        Main.map.mapView.removeTemporaryLayer(buildingSegm);
        roadSegm = null;
        buildingSegm = null;
        Main.map.repaint();
    }

    public static void setRadioGroup(ButtonGroup group) {
        ShapeMode.group = group;
    }
}
