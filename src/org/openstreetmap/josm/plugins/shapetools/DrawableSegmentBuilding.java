package org.openstreetmap.josm.plugins.shapetools;

import java.awt.Color;

import org.openstreetmap.josm.data.osm.WaySegment;
/**
 * Adding this as a temporary layer on mapview will color the selected segment green
 * @author Adrian_antochi
 *
 */
public class DrawableSegmentBuilding extends DrawableSegment{
	
	public DrawableSegmentBuilding(WaySegment segment) {
		super(segment);
		this.color = Color.GREEN;
	}
}
