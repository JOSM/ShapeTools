/**
 * 
 */
package org.openstreetmap.josm.plugins.shapetools;

import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openstreetmap.josm.data.osm.Node;

public class ShapeToolsDialog extends JPanel { // Temporarily not used

	static JList<Node> currentNodeList;
	static JTextField angleInput;
	static JComboBox<String> directionComboBox;
	static JButton rotButton;
	static JButton centrButton;
	static JButton alignButton;
	static JButton alignBuildingButton;
	static JButton getNearestSegment;
	static JButton alignAllBuildings;
	static JTextField epsilonInput;

	public ShapeToolsDialog() {
		currentNodeList = null;
		angleInput = new JTextField();
		directionComboBox = new JComboBox<>(new String[] { "Clockwise", "AntiClockwise" });
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		rotButton = new JButton("Rotate");
		centrButton = new JButton("DrawCenter");
		alignButton = new JButton("Align");
		alignBuildingButton = new JButton("AlignBuilding");
		getNearestSegment = new JButton("NearestSegment");
		alignAllBuildings = new JButton("AlignBuildings");
		epsilonInput = new JTextField();

		rotButton.addMouseListener((MouseListener) ShapeEvents.getInstance().getRotateEvent(directionComboBox, angleInput));
		alignButton.addMouseListener((MouseListener) ShapeEvents.getInstance().getAlign());
		alignBuildingButton.addMouseListener((MouseListener) ShapeEvents.getInstance().getAlignBuildingEvent());
		getNearestSegment.addMouseListener((MouseListener) ShapeEvents.getInstance().getAlignBuildingEvent());
		alignAllBuildings.addMouseListener((MouseListener) ShapeEvents.getInstance().getAlignAllBuildings(epsilonInput));

		add(rotButton);
		add(centrButton);
		add(alignButton);
		add(alignBuildingButton);
		add(getNearestSegment);
		add(angleInput);
		add(directionComboBox);
		add(epsilonInput);
		add(alignAllBuildings);
	}
}
