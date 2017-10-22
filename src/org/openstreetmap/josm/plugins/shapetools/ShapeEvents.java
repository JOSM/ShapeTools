// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.tools.Logging;

/**
 * Event handler for all the plugin's operations
 *
 * @author Adrian_antochi
 *
 */
public final class ShapeEvents {

    private MouseListener rotate;
    private MouseListener alignBuilding;
    private MouseListener nearestSegment;
    private MouseListener align;
    private MouseListener alignAllBuildings;
    private MouseListener cleanup;
    private static ShapeEvents instance;
    private JComboBox<String> directionComboBox;
    private JTextField angleInput;
    private JTextField epsilonInput;

    private ShapeEvents() {
    }

    public static ShapeEvents getInstance() {
        if (instance == null) {
            instance = new ShapeEvents();
        }
        return instance;
    }

    public MouseListener getCleanup() {
        initCleanup();
        return cleanup;
    }

    public MouseListener getAlign() {
        initAlign();
        return align;
    }

    public MouseListener getRotateEvent(JComboBox<String> directionComboBox, JTextField angleInput) {
        this.directionComboBox = directionComboBox;
        this.angleInput = angleInput;
        initRotate();
        return rotate;
    }

    public MouseListener getAlignBuildingEvent() {
        initAlignBuilding();
        return alignBuilding;
    }

    public MouseListener getNearestSegmentEvent() {
        initNearestSegment();
        return nearestSegment;
    }

    public MouseListener getAlignAllBuildings(JTextField epsilonInput) {
        this.epsilonInput = epsilonInput;
        initAlignAll();
        return alignAllBuildings;
    }

    private void initCleanup() {
        this.cleanup = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ShapeMode.cleanup();
            }
        };
    }

    private void initAlign() {
        this.align = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DataSet data = MainApplication.getLayerManager().getEditDataSet();
                Collection<Way> selectedWays = data.getSelectedWays();
                List<Way> wayList = new ArrayList<>();
                for (Way way : selectedWays) {
                    wayList.add(way);
                }
                ShapeMath.align(wayList.get(0), wayList.get(1));
                ShapeMode.cleanup();
            }
        };
    }

    private void initAlignAll() {
        alignAllBuildings = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ShapeMode mode = ShapeToolsPlugin.getMode();
                if (mode != null) {
                    DrawableSegmentRoad road = ShapeMode.roadSegm;
                    if (road != null) {
                        try {
                            Logging.debug("AlignAllBuildings button pressed, non-null parameters found");
                            double epsilon = Double.parseDouble(epsilonInput.getText());
                            OsmDataLayer currentLayer = MainApplication.getLayerManager().getEditLayer();
                            DataSet data = currentLayer.data;
                            Collection<Way> selectedWays = data.getSelectedWays();
                            for (Way way : selectedWays) {
                                ShapeMath.alignUsingEpsilon(road.segment, way, epsilon);
                                ShapeMode.cleanup();
                                MainApplication.getMap().repaint();
                            }
                        } catch (NumberFormatException ex) {
                            new Notification(tr("Please enter minimal distance in metres")).show();
                        }
                    }
                }
            }
        };
    }

    private void initNearestSegment() {
        nearestSegment = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ShapeMode mode = ShapeToolsPlugin.getMode();
                if (mode != null) {
                    DrawableSegmentBuilding building = ShapeMode.buildingSegm;
                    DrawableSegmentRoad road = ShapeMode.roadSegm;
                    if (building != null && road != null) {
                        Logging.debug("NearestSegment button pressed, non-null parameters found");
                        WaySegment segm = ShapeMath.getClosestSegment(building.getSegment().way, road.segment);
                        DrawableSegment dSegm = new DrawableSegment(segm, Color.magenta);
                        Logging.debug("closest nodes" + segm.getFirstNode() + " " + segm.getSecondNode());
                        MainApplication.getMap().mapView.addTemporaryLayer(dSegm);
                        MainApplication.getMap().repaint();
                    }
                }
            }
        };
    }

    private void initAlignBuilding() {
        alignBuilding = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ShapeMode mode = ShapeToolsPlugin.getMode();
                if (mode != null) {
                    DrawableSegmentBuilding building = ShapeMode.buildingSegm;
                    DrawableSegmentRoad road = ShapeMode.roadSegm;
                    if (building != null && road != null) {
                        Logging.debug("AlignButtonPressed, non-null parameters found");
                        ShapeMath.align(road.getSegment(), building.getSegment());
                        ShapeMode.cleanup();
                    }
                }
            }
        };
    }

    private void initRotate() {
        rotate = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                double angle = 0;
                try {
                    angle = Math.toRadians(Double.parseDouble(angleInput.getText()));
                } catch (NumberFormatException ex) {
                    new Notification(tr("Please enter numeric angle in degrees")).show();
                } catch (Throwable ex) {
                    Logging.error(ex);
                }
                OsmDataLayer currentLayer = MainApplication.getLayerManager().getEditLayer();
                DataSet data = currentLayer.data;
                Collection<Node> selectedNodes = data.getSelectedNodes();
                Collection<Way> selectedWays = data.getSelectedWays();
                if (directionComboBox.getSelectedIndex() == 0) {
                    Logging.debug("User requires clockwise rotation");
                    Logging.debug("Using angle: " + -angle);
                    ShapeMath.doRotate(selectedWays, selectedNodes, -angle);
                } else if (directionComboBox.getSelectedIndex() == 1) {
                    Logging.debug("User requires antiClockwise rotation");
                    Logging.debug("Using angle: " + angle);
                    ShapeMath.doRotate(selectedWays, selectedNodes, angle);
                }
            }
        };
    }
}
