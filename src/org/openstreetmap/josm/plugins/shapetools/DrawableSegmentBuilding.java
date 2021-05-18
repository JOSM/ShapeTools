// License: GPL. For details, see LICENSE file.
package org.openstreetmap.josm.plugins.shapetools;

import java.awt.Color;

import org.openstreetmap.josm.data.osm.IWaySegment;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;

/**
 * Adding this as a temporary layer on mapview will color the selected segment green
 * @author Adrian_antochi
 */
public class DrawableSegmentBuilding extends DrawableSegment {

    /**
     * Constructs a new {@code DrawableSegmentBuilding}.
     * @param segment way segment
     */
    public DrawableSegmentBuilding(IWaySegment<Node, Way> segment) {
        super(segment, Color.GREEN);
    }
}
