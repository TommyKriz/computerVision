package assignment02;

import java.awt.Color;

import delaunay.lib.IjUtil;
import delaunay.lib.Triangle;
import delaunay.lib.Triangulation;
import delaunay.lib.Vertex;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


/**
 * This ImageJ plugin demonstrates the calculation of of the 2D Delaunay triangulation for
 * a given set of points.
 * 
 * Requires a binary (grayscale) image with points valued > 0. 
 * 
 * 
 * This plugin is based on applet code by Paul Chew (http://www.cs.cornell.edu/home/chew/Delaunay.html)
 * that is contained in the associated 'delaunay' package.
 * Usage: Create or open an grayscale image containing a point set (any pixel with value > 0 
 * is considered a foreground point, zero pixels are considered background). The plugin 
 * triangulates this point set and displays the triangulation as a new color image.
 * 
 * @author W. Burger
 * @version 2016/03/08
 */
public class Delaunay_Demo implements PlugInFilter {
	
	static String title = "Delaunay triangulation";
	private static Color DelaunayColor = Color.green;
	private static Color PointColor = Color.magenta;
	
	public int setup(String arg, ImagePlus im) {
		return DOES_8G + NO_CHANGES;
	}
	
	public void run(ImageProcessor ip) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		Vertex[] points = IjUtil.collectPoints(ip);
		IJ.log("Found " + points.length + " image points.");
		
		Triangulation dt = new Triangulation(points, width, height);
				
		ImageProcessor cp = ip.convertToColorProcessor();
		
		// draw the Delaunay triangulation
		cp.setColor(DelaunayColor);
		Triangle[] triangles = dt.getDelaunayTriangles();
        for (Triangle trgl : triangles) {
        	IjUtil.draw(trgl, cp);
        }
        
        // draw the original point set on top
		cp.setColor(PointColor);
		for (Vertex pnt : points) {
			IjUtil.draw(pnt, cp);
		}
		
		(new ImagePlus(title, cp)).show();
	}
	
}
