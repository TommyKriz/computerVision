package assignment10;

/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital image processing
 * published by Springer-Verlag in various languages and editions. Permission to use and distribute
 * this software is granted under the BSD 2-Clause "Simplified" License (see
 * http://opensource.org/licenses/BSD-2-Clause). Copyright (c) 2006-2016 Wilhelm Burger, Mark J.
 * Burge. All rights reserved. Visit http://imagingbook.com for additional details.
 *******************************************************************************/

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;
import assignment09.ImageStitching;
import assignment09.SiftMatching;

public class Assignment10 implements PlugInFilter {
	ImagePlus imp = null;

	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + DOES_RGB + STACK_REQUIRED + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		if (imp.getStackSize() < 2) {
			IJ.error("Stack with at least 2 images required!");
			return;
		}

		ImageStack stack = imp.getImageStack();
		FloatProcessor image1 = stack.getProcessor(1).convertToFloatProcessor();
		FloatProcessor image2 = stack.getProcessor(2).convertToFloatProcessor();

		ProjectiveMapping mapping = SiftMatchingHomography
				.calcProjection(SiftMatching.calcMatches(image1,
						image2));

		new ImagePlus("stitched together from Sift Matching",
				new ImageStitching().stitchTogether(image1, image2, mapping))
				.show();
	}
}
