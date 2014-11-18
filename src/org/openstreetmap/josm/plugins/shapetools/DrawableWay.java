package org.openstreetmap.josm.plugins.shapetools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.List;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.MapViewPaintable;

/**
 * Adding this as a temporary layer on mapview will color the selected way white
 * 
 * @author Adrian_antochi
 *
 */
public class DrawableWay implements MapViewPaintable {

	private Color color;
	private Stroke stroke;
	private Way way;

	public DrawableWay() {
		int strokeThicknes = 5;
		this.color = Color.WHITE;
		this.stroke = new BasicStroke(strokeThicknes);
	}

	public DrawableWay(Way way) {
		this();
		this.way = way;
	}

	public DrawableWay(Way way, Color color) {
		this();
		this.color = color;
		this.way = way;
	}

	@Override
	public void paint(Graphics2D g, MapView mv, Bounds bbox) {

		if (way != null) {
			g.setStroke(stroke);
			g.setColor(color);
			List<Node> wayNodes = way.getNodes();
			for (int i = 0; i < wayNodes.size() - 1; i++) {
				Line2D overlappingLine = new Line2D.Double(mv.getPoint(wayNodes.get(i)).getX(), mv.getPoint(wayNodes.get(i)).getY(), mv.getPoint(
						wayNodes.get(i + 1)).getX(), mv.getPoint(wayNodes.get(i + 1)).getY());
				g.draw(overlappingLine);
			}
		}
	}

	public Color getWayColor() {
		return color;
	}

	public void setWayColor(Color wayColor) {
		this.color = wayColor;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	public Way getWay() {
		return way;
	}

	public void setWay(Way way) {
		this.way = way;
	}
}
