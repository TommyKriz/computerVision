package assignment07;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.geom.Path2D;
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

		ScoreFunctionMatcher tm = new ScoreFunctionMatcher(reference, target);

		FloatProcessor matchScore = tm.calcMatchScore();

		new ImagePlus("matchScore", matchScore).show();

		List<Pixel> bestLocalMinima = new LocalMinMaxDetector()
				.findLocalMinima(matchScore, 20);

		visualizeResult(bestLocalMinima, target.convertToColorProcessor(),
				reference.getWidth(), reference.getHeight());

	}

	private void visualizeResult(List<Pixel> localMinima, ColorProcessor cp,
			int referenceWidth, int referenceHeight) {

		final int[] pink = new int[] { 255, 105, 180 };

		ImagePlus detected = new ImagePlus("detected", cp);

		Overlay oly = new Overlay();

		for (Pixel p : localMinima) {

			IJ.log(" -> " + p.toString());

			oly.add(makeStraightLine(p.x, p.y, p.x + referenceWidth, p.y,
					Color.YELLOW));
			oly.add(makeStraightLine(p.x, p.y, p.x, p.y + referenceHeight,
					Color.YELLOW));
			oly.add(makeStraightLine(p.x, p.y + referenceHeight, p.x
					+ referenceWidth, p.y + referenceHeight, Color.YELLOW));
			oly.add(makeStraightLine(p.x + referenceWidth, p.y, p.x
					+ referenceWidth, p.y + referenceHeight, Color.YELLOW));

		}

		if (oly != null) {
			detected.setOverlay(oly);
		}
		detected.show();
	}

	private ShapeRoi makeStraightLine(double x1, double y1, double x2,
			double y2, Color col) {
		Path2D poly = new Path2D.Double();
		poly.moveTo(x1, y1);
		poly.lineTo(x2, y2);
		ShapeRoi roi = new ShapeRoi(poly);
		roi.setStrokeWidth(1);
		roi.setStrokeColor(col);
		return roi;
	}

}
