package assignment04;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import imagingbook.pub.regions.Contour;

import java.awt.geom.Point2D;

public class ColorPointDistributionUtil {

	public static float[][] getNormalizedHistogram(Contour outerContour,
			Point2D centerPoint) {
		return normalizeHistogram(getColorPointDistributionHistogram(
				outerContour, centerPoint));
	}

	public static float[][] normalizeHistogram(int[][] histogram) {
		int w = histogram.length;
		int h = histogram[0].length;
		float totalNumberOfHistogramEntries = w * h;
		float[][] normalizedHistogram = new float[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				// normalization occurs here
				normalizedHistogram[x][y] = histogram[x][y]
						/ totalNumberOfHistogramEntries;
			}
		}
		return normalizedHistogram;
	}

	private static int[][] getColorPointDistributionHistogram(
			Contour outerContour, Point2D centerPoint) {
		return new CPDH_().getHistogram(outerContour, centerPoint);
	}

}
