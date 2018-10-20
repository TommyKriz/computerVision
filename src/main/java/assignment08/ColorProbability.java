package assignment08;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.math.MahalanobisDistance;

import java.util.List;

public class ColorProbability {

	private final double[][] sampleColors;

	private final double[] centroid;

	private final MahalanobisDistance mhd;

	public ColorProbability(List<int[]> sampleColors) {
		this.sampleColors = convert(sampleColors);
		centroid = initCentroid();
		mhd = new MahalanobisDistance(this.sampleColors);
	}

	public FloatProcessor calc(ImageProcessor ip) {
		int[] rgb = new int[3];
		FloatProcessor result = ip.convertToFloatProcessor();
		for (int x = 0; x < ip.getWidth(); x++) {
			for (int y = 0; y < ip.getHeight(); y++) {
				// for every image pixel
				ip.getPixel(x, y, rgb);
				double d = distanceToPoint(new double[] {
						RgChromaticity.calcRChroma(rgb),
						RgChromaticity.calcGChroma(rgb) });

				result.putPixelValue(x, y, Math.exp(-Math.pow(d, 2) / 2));
			}
		}
		return result;
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

	private double distanceToPoint(double[] samplePoint) {
		return mhd.distance(samplePoint, centroid);
	}

	private double[][] convert(List<int[]> colors) {
		double[][] rgColors = new double[colors.size()][2];
		for (int i = 0; i < colors.size(); i++) {
			rgColors[i][0] = RgChromaticity.calcRChroma(colors.get(i));
			rgColors[i][1] = RgChromaticity.calcGChroma(colors.get(i));
		}
		return rgColors;
	}

}
