package assignment09;

import ij.IJ;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.mappings.linear.LinearMapping;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;
import imagingbook.pub.geometry.mappings.linear.Translation;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ImageStitching {

	@Deprecated
	public ImageProcessor stitchTogetherCopyBits(FloatProcessor image1,
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

	public ByteProcessor stitchTogether(FloatProcessor image1,
			FloatProcessor image2, ProjectiveMapping mapping) {
		List<Point2D> points = new ArrayList<Point2D>();

		int w = image1.getWidth();
		int h = image1.getHeight();

		points.add(new Point2D.Double(0, 0));
		points.add(new Point2D.Double(0, h));
		points.add(new Point2D.Double(w, 0));
		points.add(new Point2D.Double(w, h));

		points.add(mapping.applyTo(new Point2D.Double(0, 0)));
		points.add(mapping.applyTo(new Point2D.Double(0, h)));
		points.add(mapping.applyTo(new Point2D.Double(w, 0)));
		points.add(mapping.applyTo(new Point2D.Double(w, h)));

		double smallestX = Double.POSITIVE_INFINITY;
		double largestX = Double.NEGATIVE_INFINITY;
		double smallestY = Double.POSITIVE_INFINITY;
		double largestY = Double.NEGATIVE_INFINITY;

		for (Point2D p : points) {
			if (p.getX() < smallestX) {
				smallestX = p.getX();
			}
			if (p.getY() < smallestY) {
				smallestY = p.getY();
			}
			if (p.getX() > largestX) {
				largestX = p.getX();
			}
			if (p.getY() > largestY) {
				largestY = p.getY();
			}
		}

		int widthBoth = (int) Math.round(largestX - smallestX);
		int heightBoth = (int) Math.round(largestY - smallestY);

		ByteProcessor imA = new ByteProcessor(widthBoth, heightBoth);
		ByteProcessor imB = new ByteProcessor(widthBoth, heightBoth);
		ByteProcessor imBoth = new ByteProcessor(widthBoth, heightBoth);

		int spawnX = 0;
		int spawnY = 0;

		if (smallestX < 0) {
			spawnX = imBoth.getWidth() - w;
		} else if (largestX > imBoth.getWidth()) {
			spawnX = -(imBoth.getWidth() - w);
		}

		if (smallestY < 0) {
			spawnY = imBoth.getHeight() - h;
		} else if (largestY > imBoth.getHeight()) {
			spawnY = -(imBoth.getHeight() - h);
		}

		LinearMapping t = new Translation(spawnX, spawnY); // translation only
		LinearMapping m2 = mapping.concat(t); // creating a new transformation
												// that is the original

		imA.insert(image1, 0, 0);
		imB.insert(image2, 0, 0);

		m2.applyTo(imA, InterpolationMethod.Bicubic);
		t.applyTo(imB, InterpolationMethod.Bicubic);

		for (int i = 0; i < imBoth.getWidth(); i++) {
			for (int j = 0; j < imBoth.getHeight(); j++) {
				float a = imA.getPixel(i, j);
				float b = imB.getPixel(i, j);
				imBoth.putPixelValue(i, j, (double) ((a + b) / 2));
			}
		}
		return imBoth;
	}

}
