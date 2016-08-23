package org.openstreetmap.josm.plugins.shapetools;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;

/**
 * Event handler for all the plugin's opperations
 * 
 * @author Adrian_antochi
 *
 */
public class ShapeEvents {

	private EventListener rotate;
	private EventListener alignBuilding;
	private EventListener nearestSegment;
	private EventListener align;
	private EventListener alignAllBuildings;
	private EventListener cleanup;
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

	public EventListener getCleanup() {
		initCleanup();
		return cleanup;
	}

	public EventListener getAlign() {
		initAlign();
		return align;
	}

	public EventListener getRotateEvent(JComboBox<String> directionComboBox, JTextField angleInput) {
		this.directionComboBox = directionComboBox;
		this.angleInput = angleInput;
		initRotate();
		return rotate;
	}

	public EventListener getAlignBuildingEvent() {
		initAlignBuilding();
		return alignBuilding;
	}

	public EventListener getNearestSegmentEvent() {
		initNearestSegment();
		return nearestSegment;
	}

	public EventListener getAlignAllBuildings(JTextField epsilonInput) {
		this.epsilonInput = epsilonInput;
		initAlignAll();
		return alignAllBuildings;
	}

	private void initCleanup() {
		this.cleanup = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ShapeMode.cleanup();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
	}

	private void initAlign() {
		this.align = new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				OsmDataLayer currentLayer = Main.getLayerManager().getEditLayer();
				DataSet data = currentLayer.data;
				Collection<Way> selectedWays = data.getSelectedWays();
				List<Way> wayList = new ArrayList<Way>();
				for (Way way : selectedWays) {
					wayList.add(way);
				}
				ShapeMath.align(wayList.get(0), wayList.get(1));
				ShapeMode.cleanup();
			}
		};
	}

	private void initAlignAll() {
		alignAllBuildings = new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				ShapeMode mode = ShapeToolsPlugin.getMode();
				if (mode != null) {
					DrawableSegmentRoad road = ShapeMode.roadSegm;
					if (road != null) {
						System.out.println("AlignAllBuildings button pressed, non-null parameters found");
						double epsilon = Double.parseDouble(epsilonInput.getText());
						OsmDataLayer currentLayer = Main.getLayerManager().getEditLayer();
						DataSet data = currentLayer.data;
						Collection<Way> selectedWays = data.getSelectedWays();
						for (Way way : selectedWays) {
							ShapeMath.alignUsingEpsilon(road.segment, way, epsilon);
							ShapeMode.cleanup();
							Main.map.repaint();
						}
					} else {
						System.out.println("NULL PARAMETERS FOUND");
					}
				} else {
					System.out.println("NULL PARAMETERS FOUND");
				}
			}
		};
	}

	private void initNearestSegment() {
		nearestSegment = new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {

				ShapeMode mode = ShapeToolsPlugin.getMode();
				if (mode != null) {
					DrawableSegmentBuilding building = ShapeMode.buildingSegm;
					DrawableSegmentRoad road = ShapeMode.roadSegm;
					if (building != null && road != null) {
						System.out.println("NearestSegment button pressed, non-null parameters found");
						WaySegment segm = ShapeMath.getClosestSegment(building.getSegment().way, road.segment);
						DrawableSegment dSegm = new DrawableSegment(segm, Color.magenta);
						System.out.println("closest nodes" + segm.getFirstNode() + " " + segm.getSecondNode());
						Main.map.mapView.addTemporaryLayer(dSegm);
						Main.map.repaint();
					} else {
						System.out.println("NULL PARAMETERS FOUND");
					}
				} else {
					System.out.println("NULL PARAMETERS FOUND");
				}
			}
		};
	}

	private void initAlignBuilding() {
		alignBuilding = new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				ShapeMode mode = ShapeToolsPlugin.getMode();
				if (mode != null) {
					DrawableSegmentBuilding building = ShapeMode.buildingSegm;
					DrawableSegmentRoad road = ShapeMode.roadSegm;
					if (building != null && road != null) {
						System.out.println("AlignButtonPressed, non-null parameters found");
						ShapeMath.align(road.getSegment(), building.getSegment());
						ShapeMode.cleanup();
					} else {
						System.out.println("NULL PARAMETERS FOUND");
					}
				} else {
					System.out.println("NULL PARAMETERS FOUND");
				}
			}
		};
	}

	private void initRotate() {

		rotate = new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				double angle = 0;
				try {
					angle = Double.parseDouble(angleInput.getText());
					angle = Math.toRadians(angle);
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
				} catch (Throwable ex) {
					System.out.println("CANNOT CONVERT JTEXT INPUT TO ANGLE");
					ex.printStackTrace();
				}
				OsmDataLayer currentLayer = Main.getLayerManager().getEditLayer();
				DataSet data = currentLayer.data;
				Collection<Node> selectedNodes = data.getSelectedNodes();
				Collection<Way> selectedWays = data.getSelectedWays();
				if (directionComboBox.getSelectedIndex() == 0) {
					System.out.println("User requires clockwise rotation");
					System.out.println("Using angle: " + -angle);
					ShapeMath.doRotate(selectedWays, selectedNodes, -angle);
				} else if (directionComboBox.getSelectedIndex() == 1) {
					System.out.println("User requires antiClockwise rotation");
					System.out.println("Using angle: " + angle);
					ShapeMath.doRotate(selectedWays, selectedNodes, angle);
				}
			}
		};
	}

}
