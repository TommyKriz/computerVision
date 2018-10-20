package assignment03;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import assignment03.CornerVisualization;
import assignment03.StereoPictureCornerDetector;
/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital image processing
 * published by Springer-Verlag in various languages and editions. Permission to use and distribute
 * this software is granted under the BSD 2-Clause "Simplified" License (see
 * http://opensource.org/licenses/BSD-2-Clause). Copyright (c) 2006-2015 Wilhelm Burger, Mark J.
 * Burge. All rights reserved. Visit http://www.imagingbook.com for additional details.
 * 
 *******************************************************************************/
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.color.RandomColorGenerator;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.HarrisCornerDetector;

/**
 * This plugin implements the Harris corner detector. It calculates the corner
 * positions and shows the result in a new color image.
 * 
 * @version 2013/08/22
 */
public class Assignment03 implements PlugInFilter {

	private static final int MAX_ITERATIONS = 400;
	private static final double MINIMUM_ERROR_IMPROVEMENT_BETWEEN_ITERATIONS = 0.0000000001;
	private static final int MAXIMUM_NUMBER_OF_CORNERS = 70;

	ImagePlus im;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_ALL + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {

		StereoPictureCornerDetector cornerDetector = new StereoPictureCornerDetector(
				ip);

		List<Corner> leftCorners = cornerDetector.getLeftCorners();
		List<Corner> rightCorners = cornerDetector.getRightCorners();

		ColorProcessor R = ip.convertToColorProcessor();
		CornerVisualization.drawCorners(R, leftCorners, Color.yellow);
		CornerVisualization.drawCorners(R, rightCorners, Color.green);
		new ImagePlus("Corners from " + im.getShortTitle(), R).show();

		List<Point2D> points1 = convert(leftCorners);
		List<Point2D> points2 = convert(rightCorners);

		ICP icp = new ICP();
		icp.iterativeClosestPointMatch(points1, points2,
				MINIMUM_ERROR_IMPROVEMENT_BETWEEN_ITERATIONS, MAX_ITERATIONS);

		Overlay pointAssociationOverlay = createPointAssociationsOverlay(
				icp.getPointAssociationTable(), points1, points2);

		ImagePlus cornersAndOverlay = new ImagePlus(
				"Final Corner Association (with Corners)" + im.getShortTitle(),
				R.convertToColorProcessor());
		cornersAndOverlay.setOverlay(pointAssociationOverlay);
		cornersAndOverlay.show();

		ImagePlus overlayNoCorners = new ImagePlus("Final Corner Association "
				+ im.getShortTitle(), ip.convertToColorProcessor());
		overlayNoCorners.setOverlay(pointAssociationOverlay);
		overlayNoCorners.show();

		IJ.log("ICP Iterations: " + icp.getIteration());
		IJ.log("Average Final Procrustes Fit Square Error per Point Pair"
				+ icp.getError() / MAXIMUM_NUMBER_OF_CORNERS);

	}

	private List<Point2D> convert(List<Corner> corners) {
		List<Point2D> points = new ArrayList<>();
		for (Corner c : corners) {
			points.add(new Point2D.Double(c.getX(), c.getY()));
		}
		return points;
	}

	private Overlay createPointAssociationsOverlay(int[] pointAssociationTable,
			List<Point2D> points1, List<Point2D> points2) {
		RandomColorGenerator randColor = new RandomColorGenerator();

		// create an empty overlay
		Overlay oly = new Overlay();
		for (int i = 0; i < pointAssociationTable.length; i++) {

			// only draw every second point
			if (i % 2 == 0) {

				Point2D point1 = points1.get(i);
				Point2D point2 = points2.get(pointAssociationTable[i]);
				double x1 = point1.getX();
				double y1 = point1.getY();
				double x2 = point2.getX();
				double y2 = point2.getY();

				Shape curve = new QuadCurve2D.Double(x1, y1, (x1 + x2) / 2,
						(y1 + y2) / 2 + 50, x2, y2);
				ShapeRoi roi = new ShapeRoi(curve);
				roi.setStrokeWidth(0.8f);

				roi.setStrokeColor(randColor.nextColor());
				oly.add(roi);

			}

		}
		return oly;
	}

}
