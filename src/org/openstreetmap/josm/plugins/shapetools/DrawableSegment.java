// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.layer.MapViewPaintable;

/**
 * Base class used for adding colored segments on map as temporary MapViewPaintables
 * @author Adrian_antochi
 */
public class DrawableSegment implements MapViewPaintable {

    protected final WaySegment segment;
    protected final Color color;
    protected final Stroke stroke;

    /**
     * Constructs a new {@code DrawableSegment}.
     * @param segment way segment
     */
    public DrawableSegment(WaySegment segment) {
        this(segment, Color.WHITE);
    }

    /**
     * Constructs a new {@code DrawableSegment}.
     * @param segment way segment
     * @param color color
     */
    public DrawableSegment(WaySegment segment, Color color) {
        int strokeThickness = 3;
        this.segment = segment;
        this.color = color;
        this.stroke = new BasicStroke(strokeThickness);
    }

    @Override
    public void paint(Graphics2D g, MapView mv, Bounds bbox) {
        if (segment != null && segment.way.getNodesCount() > 1) {
            g.setColor(color);
            g.setStroke(stroke);

            Point p1 = mv.getPoint(segment.getFirstNode());
            Point p2 = mv.getPoint(segment.getSecondNode());
            Line2D overlappingLine = new Line2D.Double(p1.getX(), p1.getY(),
                                                       p2.getX(), p2.getY());
            g.draw(overlappingLine);
        }
    }

    public Color getColor() {
        return color;
    }

    public WaySegment getSegment() {
        return segment;
    }
}
