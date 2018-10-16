package assignment08;

import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class RgChromaticity {

	private int[] rgb = new int[3];

	public FloatProcessor getRChromaticityValues(ImageProcessor ip) {
		return getChromaticity(ip, 0);
	}

	public FloatProcessor getGChromaticityValues(ImageProcessor ip) {
		return getChromaticity(ip, 1);
	}

	private FloatProcessor getChromaticity(ImageProcessor ip, int chroma) {
		FloatProcessor rChroma = ip.convertToFloatProcessor();
		for (int x = 0; x < rChroma.getWidth(); x++) {
			for (int y = 0; y < rChroma.getHeight(); y++) {
				rChroma.putPixelValue(x, y,
						calcChroma(ip.getPixel(x, y, rgb), chroma));
			}
		}
		return rChroma;
	}

	/**
	 * 
	 * @param rgb
	 *            int[]
	 * @param chroma
	 *            0 for r, 1 for g
	 * @return r || g
	 */
	private float calcChroma(int[] rgb, int chroma) {
		float sum = rgb[0] + rgb[1] + rgb[2];
		if (sum == 0) {
			return 0;
		} else {
			return rgb[chroma] / sum;
		}
	}
}
