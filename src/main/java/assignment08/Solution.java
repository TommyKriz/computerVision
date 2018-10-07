package assignment08;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.color.image.HsvConverter;

public class Solution implements PlugInFilter {

	private ImagePlus im = null;

	private int[] rgb = new int[3];

	private HsvConverter hsvConverter = new HsvConverter();

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		// return DOES_RGB + ROI_REQUIRED;
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) {

		FloatProcessor rChromaPixelValues = getRChromaticity(ip);
		FloatProcessor gChromaPixelValues = getGChromaticity(ip);
	
		new ImagePlus("r(u,v)", rChromaPixelValues).show();
		new ImagePlus("g(u,v)", gChromaPixelValues).show();

	}

	private FloatProcessor getRChromaticity(ImageProcessor ip) {
		FloatProcessor rChroma = ip.convertToFloatProcessor();
		for (int x = 0; x < rChroma.getWidth(); x++) {
			for (int y = 0; y < rChroma.getHeight(); y++) {
				rChroma.putPixelValue(x, y, calcRChroma(ip.getPixel(x, y, rgb)));
			}
		}
		return rChroma;
	}

	private FloatProcessor getGChromaticity(ImageProcessor ip) {
		FloatProcessor gChroma = ip.convertToFloatProcessor();
		for (int x = 0; x < gChroma.getWidth(); x++) {
			for (int y = 0; y < gChroma.getHeight(); y++) {
				gChroma.putPixelValue(x, y, calcGChroma(ip.getPixel(x, y, rgb)));
			}
		}
		return gChroma;
	}

	private float calcRChroma(int[] rgb) {
		float sum = rgb[0] + rgb[1] + rgb[2];
		if (sum == 0) {
			return 0;
		} else {
			return rgb[0] / sum;
		}
	}

	private float calcGChroma(int[] rgb) {
		float sum = rgb[0] + rgb[1] + rgb[2];
		if (sum == 0) {
			return 0;
		} else {
			return rgb[1] / sum;
		}
	}

}
