package org.openstreetmap.josm.plugins.shapetools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.MapViewPaintable;

/**
 * Base class used for adding colored segments on map as temporary MapViewPaintables
 * @author Adrian_antochi
 *
 */
public class DrawableSegment implements MapViewPaintable {

	protected WaySegment segment;
	protected Color color;
	protected Stroke stroke;

	public DrawableSegment(WaySegment segment) {
		int strokeThickness = 3;
		this.segment = segment;
		this.color = Color.WHITE;
		this.stroke = new BasicStroke(strokeThickness);
	}

	public DrawableSegment(WaySegment segment, Color color) {
		int strokeThickness = 3;
		this.segment = segment;
		this.color = color;
		this.stroke = new BasicStroke(strokeThickness);
	}

	@Override
	public void paint(Graphics2D g, MapView mv, Bounds bbox) {
		if (segment != null) {
			g.setColor(this.color);
			g.setStroke(this.stroke);
			Node firstNode = segment.getFirstNode();
			Node secondNode = segment.getSecondNode();

			Line2D overlappingLine = new Line2D.Double(mv.getPoint(firstNode).getX(), mv.getPoint(firstNode).getY(), mv.getPoint(secondNode).getX(), mv
					.getPoint(secondNode).getY());
			g.draw(overlappingLine);
		}
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setSegment(WaySegment segment) {
		this.segment = segment;
	}

	public WaySegment getSegment() {
		return this.segment;
	}

}
