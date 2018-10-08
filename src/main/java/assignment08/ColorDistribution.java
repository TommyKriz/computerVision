package assignment08;

import ij.process.FloatProcessor;
import imagingbook.lib.math.MahalanobisDistance;

public class ColorDistribution {

	private final double[][] sampleColors;

	private final double[] centroid;

	private final MahalanobisDistance mhd;

	public ColorDistribution(double[][] sampleColors) {
		this.sampleColors = sampleColors;
		centroid = initCentroid();
		mhd = new MahalanobisDistance(this.sampleColors);
	}

	private double[] initCentroid() {
		int numberOfSamples = sampleColors.length;
		double cx = 0;
		double cy = 0;
		for (int i = 0; i < numberOfSamples; i++) {
			cx += sampleColors[i][0];
			cy += sampleColors[i][1];
		}
		cx /= numberOfSamples;
		cy /= numberOfSamples;
		return new double[] { cx, cy };
	}

	private double distanceFromCentroid() {
		return mhd.distance(centroid);
	}

	private double distanceToPoint(double[] samplePoint) {
		return mhd.distance(centroid, samplePoint);
	}

	public FloatProcessor distribution() {
		FloatProcessor fp = new FloatProcessor(256, 256);

		return fp;
	}

}
