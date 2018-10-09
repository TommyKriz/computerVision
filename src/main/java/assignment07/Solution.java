package assignment07;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.util.List;

public class Solution implements PlugInFilter {

	private ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + NO_CHANGES + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {

		FloatProcessor target = ip.convertToFloatProcessor();
		FloatProcessor reference = ip.crop().convertToFloatProcessor();

		new ImagePlus("target", target).show();
		new ImagePlus("reference", reference).show();

		TemplateMatcher tm = new TemplateMatcher(reference, target);

		FloatProcessor matchScore = tm.calcMatchScore();

		new ImagePlus("matchScore", matchScore).show();

		LocalMinMaxDetector ll = new LocalMinMaxDetector();
		List<Pixel> allLocalMinima = ll.findLocalMinima(matchScore);

		// optional auch drawen

		int[] pink = new int[] { 255, 105, 180 };

		ColorProcessor cp = target.convertToColorProcessor();
		for (Pixel p : ll.bestMatches(20, allLocalMinima)) {
			IJ.log(" -> " + p.toString());
			cp.putPixel(p.x, p.y, pink);
		}

		new ImagePlus("detected", cp).show();

	}
}
