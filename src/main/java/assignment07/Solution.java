package assignment07;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.util.List;

public class Solution implements PlugInFilter {

	private ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {

		FloatProcessor target = ip.convertToFloatProcessor();
		FloatProcessor reference = ip.crop().convertToFloatProcessor();

		new ImagePlus("target", target).show();
		new ImagePlus("reference", reference).show();

		TemplateMatcher tm = new TemplateMatcher(reference, target);

		FloatProcessor matchScore = tm.calcMatchScore();

		new ImagePlus("matchScore", matchScore).show();

		LocalMinMaxDetector ll = new LocalMinMaxDetector(matchScore);

//		List<Pixel> localMinima = ll.localMins(4);
//
//		for (Pixel p : localMinima) {
//			IJ.log(p.toString());
//		}

	}
}
