package assignment08;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Solution implements PlugInFilter {

	private ImagePlus im = null;

	private RgChromaticity rgChromaticity = new RgChromaticity();

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		// return DOES_RGB + ROI_REQUIRED;
		return DOES_RGB;
	}

	public void run(ImageProcessor ip) {

		FloatProcessor rChromaPixelValues = rgChromaticity
				.getRChromaticityValues(ip);
		FloatProcessor gChromaPixelValues = rgChromaticity
				.getGChromaticityValues(ip);

		new ImagePlus("r(u,v)", rChromaPixelValues).show();
		new ImagePlus("g(u,v)", gChromaPixelValues).show();

	}

}
