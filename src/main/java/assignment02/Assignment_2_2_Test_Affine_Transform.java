package assignment02;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.awt.geom.Point2D;

public class Assignment_2_2_Test_Affine_Transform implements PlugInFilter {

	ImagePlus im = null;

	public int setup(String arg, ImagePlus im) {
		this.im = im; // keep a reference to im
		return DOES_8G + STACK_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		int k = im.getStackSize();
		if (k < 2) {
			IJ.error("stack with 2 images required");
			return;
		}
		ImageStack stack = im.getStack();
		ImageProcessor ip1 = stack.getProcessor(1);
		ImageProcessor ip2 = stack.getProcessor(2);

		Point2D[] original = ImagePointsUtils.gatherPoints(ip1);
		Point2D[] transformed = ImagePointsUtils.gatherPoints(ip2);

		AffineTransform affineTransform = new AffineTransform(0.013, 1.088,
				18.688, -1.0, -0.05, 127.5);

		Point2D[] appliedAffineTransform = affineTransform.applyTo(original);

		IJ.log("\n Residual Error: "
				+ PointSetMath.residualError(transformed,
						appliedAffineTransform));

		IJ.log("\n Residual Error Cast To Int: "
				+ PointSetMath.residualError(transformed,
						floor(appliedAffineTransform)));

		stack.addSlice(ImagePointsUtils.visualizePoints(appliedAffineTransform,
				ip.getWidth(), ip.getHeight()));

		stack.addSlice(ImagePointsUtils.visualizePoints(
				floor(appliedAffineTransform), ip.getWidth(), ip.getHeight()));

	}

	private Point2D[] floor(Point2D[] doublePoints) {
		Point2D[] intPoints = new Point2D[doublePoints.length];
		Point2D p;
		for (int i = 0; i < doublePoints.length; i++) {
			p = doublePoints[i];
			intPoints[i] = new Point2D.Double(Math.floor(p.getX()),
					Math.floor(p.getY()));
		}
		return intPoints;
	}
}
