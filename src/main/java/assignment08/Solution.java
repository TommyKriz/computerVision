package assignment08;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.util.List;

public class Solution implements PlugInFilter {

	private ImagePlus im = null;

	private RgChromaticity rgChromaticity = new RgChromaticity();

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {

		FloatProcessor rChromaPixelValuesImage = rgChromaticity
				.getRChromaticityValues(ip);
		FloatProcessor gChromaPixelValuesImage = rgChromaticity
				.getGChromaticityValues(ip);

		new ImagePlus("r(u,v)", rChromaPixelValuesImage).show();
		new ImagePlus("g(u,v)", gChromaPixelValuesImage).show();

		List<int[]> referenceColors = ColorExtractor.extractColors(ip, im
				.getRoi().getContainedPoints());
		List<int[]> allColors = ColorExtractor.extractColors(ip);

		new ImagePlus("Color Distribution",
				ColorDistribution.distribution(allColors)).show();
		new ImagePlus("Color Distribution",
				ColorDistribution.distribution(referenceColors)).show();

		ColorProbability colorProbability = new ColorProbability(
				referenceColors);
		new ImagePlus("Reference Color Probability", colorProbability.calc(ip))
				.show();

	}

}
