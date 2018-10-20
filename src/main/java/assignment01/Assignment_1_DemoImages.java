package assignment01;

import java.awt.Color;
import java.awt.Point;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Assignment_1_DemoImages implements PlugInFilter {

	private static final int NUMBER_OF_TRIALS = 6;

	private static final double ASSOCIATION_RADIAL_OFFSET = 24.0001;

	/**
	 * expects a image created by the {@link Make_Circle_TestImage} plugin.
	 */
	public int setup(String arg, ImagePlus im) {
		return PlugInFilter.DOES_8G;
	}

	public void run(ImageProcessor ip) {

		CirclePointUtils pointStore = new CirclePointUtils(ip);

		// verify point collection
		ImageProcessor cp = ip.convertToColorProcessor();
		cp.setColor(Color.red);
		for (Point p : pointStore.points) {
			cp.drawDot(p.x, p.y);
		}
		showImage(cp, "colored dots");

		// verify random point selection
		int rOval;
		for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
			cp = ip.convertToColorProcessor();
			cp.setColor(Color.green);

			Point[] threePoints = pointStore.getThreeRandomPoints();
			rOval = 10;
			for (Point p : threePoints) {
				cp.drawDot(p.x, p.y);
				cp.drawOval(p.x - rOval, p.y - rOval, rOval * 2, rOval * 2);
			}
			try {
				// verify circle construction from 3 points
				Circle c = CircleMath.constructCircle(threePoints);
				cp.setColor(Color.PINK);
				int rc = (int) c.getRadius();
				cp.drawOval(c.getCenter().x - rc, c.getCenter().y - rc, rc * 2, rc * 2);

				// for every circle verify point association

				cp.setColor(Color.YELLOW);
				rOval = 4;
				for (Point p : pointStore.associatePointsWithCircle(c, ASSOCIATION_RADIAL_OFFSET)) {
					cp.drawDot(p.x, p.y);
					cp.drawOval(p.x - rOval, p.y - rOval, rOval * 2, rOval * 2);
				}

			} catch (CircleConstructionException e) {
				IJ.log("Circle could not be constructed");
			}

			showImage(cp, "randomly chosen dots #" + i);
		}

	}

	void showImage(ImageProcessor ip, String title) {
		(new ImagePlus(title, ip)).show();
	}

}
