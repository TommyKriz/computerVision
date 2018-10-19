package assignment09;

import ij.IJ;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;

public class ImageStitching {

	public ImageProcessor stitchTogether(FloatProcessor image1,
			FloatProcessor image2, ProjectiveMapping mapping) {

		double[] outerMostPoint = mapping.applyTo(new double[] {
				image2.getWidth(), image2.getHeight() });

		IJ.log("Outermost Point: " + outerMostPoint[0] + "-"
				+ outerMostPoint[1]);

		ImageProcessor ip = new ByteProcessor(
				(int) Math.ceil(outerMostPoint[0]),
				(int) Math.ceil(outerMostPoint[1]));

		int cy = ip.getHeight() - image1.getHeight();

		ip.insert(image1, 0, cy);

		mapping.applyTo(image2, InterpolationMethod.Bicubic);

		ip.copyBits(image2, 0, cy, Blitter.AVERAGE);

		return ip;
	}

}
