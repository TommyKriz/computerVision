package assignment08;

import ij.process.FloatProcessor;

import java.util.List;

public class ColorDistribution {

	public static FloatProcessor distribution(List<int[]> colors) {
		int[][] distributionCount = new int[256][256];

		for (int[] rgb : colors) {
			float r = RgChromaticity.calcRChroma(rgb);
			float g = RgChromaticity.calcGChroma(rgb);
			distributionCount[map0To1Value(r, 255)][map0To1Value(g, 255)]++;
		}

		FloatProcessor fp = new FloatProcessor(distributionCount);
		// because certain colors are counted more than 255 times
		fp.setMinAndMax(0, 255);
		return fp;
	}

	private static int map0To1Value(float value, int toRange) {
		return Math.round(value * toRange);
	}

}
