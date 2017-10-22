// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;

import org.openstreetmap.josm.actions.mapmode.MapMode;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.tools.Logging;

public class ShapeMode extends MapMode {
    static ButtonGroup group;
    static DrawableSegmentBuilding buildingSegm;
    static DrawableSegmentRoad roadSegm;

    /**
     * Constructs a new {@code ShapeMode}.
     */
    public ShapeMode() {
        super("ShapeMode", "mode.png", "shapeModeTooltip", Cursor.getDefaultCursor());
    }

    @Override
    public void enterMode() {
        super.enterMode();
        MainApplication.getMap().mapView.addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            boolean ctrlPressed = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0;
            boolean altPressed = (e.getModifiers() & (ActionEvent.ALT_MASK | InputEvent.ALT_GRAPH_MASK)) != 0;
            MapView mapView = MainApplication.getMap().mapView;

            WaySegment selectedSegment = mapView.getNearestWaySegment(new java.awt.Point(e.getX(), e.getY()), OsmPrimitive::isUsable);
            if (group != null) {
                switch (group.getSelection().getMnemonic()) {
                case 0:
                    if (selectedSegment != null) {
                        if (buildingSegm != null) {
                            mapView.removeTemporaryLayer(buildingSegm);
                        }
                        buildingSegm = new DrawableSegmentBuilding(selectedSegment);
                        Logging.info("SELECTED BUILDING SEGMENT " + buildingSegm);
                        mapView.addTemporaryLayer(buildingSegm);
                    }
                    break;
                case 1:
                    if (selectedSegment != null) {
                        if (roadSegm != null) {
                            mapView.removeTemporaryLayer(roadSegm);
                        }
                        roadSegm = new DrawableSegmentRoad(selectedSegment);
                        Logging.info("SELECTED ROAD SEGMENT " + roadSegm);
                        mapView.addTemporaryLayer(roadSegm);
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
        MainApplication.getMap().repaint();
    }

    /**
     * Removes all temporary layers added to the current mapView
     */
    public static void cleanup() {
        MapView mapView = MainApplication.getMap().mapView;
        mapView.removeTemporaryLayer(roadSegm);
        mapView.removeTemporaryLayer(buildingSegm);
        roadSegm = null;
        buildingSegm = null;
        MainApplication.getMap().repaint();
    }

    public static void setRadioGroup(ButtonGroup group) {
        ShapeMode.group = group;
    }
}
