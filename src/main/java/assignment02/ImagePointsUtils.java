package assignment02;

import ij.IJ;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ImagePointsUtils {

	public static ImageProcessor visualizePoints(final Point2D[] points,
			final int w, final int h) {
		ImageProcessor ip = new ByteProcessor(w, h);
		for (int u = 0; u < w; u++) {
			for (int v = 0; v < h; v++) {
				ip.putPixel(u, v, 255);
			}
		}
		for (Point2D p : points) {
			ip.putPixel((int) p.getX(), (int) p.getY(), 0);
		}
		ip.invert();
		return ip;
	}

	public static Point2D[] gatherPoints(final ImageProcessor ip) {
		List<Point2D> pointList = new ArrayList<>();
		for (int u = 0; u < ip.getWidth(); u++) {
			for (int v = 0; v < ip.getHeight(); v++) {
				if (ip.getPixel(u, v) > 0) {
					pointList.add(new Point2D.Double(u, v));
				}
			}
		}
		IJ.log("Found " + pointList.size() + " points.");
		return pointList.toArray(new Point2D[0]);
	}

}
