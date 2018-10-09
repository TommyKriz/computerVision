package assignment07;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Path2D;
import java.util.List;

public class Assignment07_Pattern_Recognition implements PlugInFilter {

	private ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G + NO_CHANGES + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		int numberOfMatches = 10;

		GenericDialog gd = new GenericDialog("Create Circle Test Image");
		gd.addNumericField("Number of Best Matching Patterns To Show",
				numberOfMatches, 0);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return;
		}
		// + 1 because we show the reference image as match #0
		numberOfMatches = (int) gd.getNextNumber() + 1;

		FloatProcessor target = ip.convertToFloatProcessor();
		FloatProcessor reference = ip.crop().convertToFloatProcessor();

		new ImagePlus("target", target).show();
		new ImagePlus("reference", reference).show();

		ScoreFunctionMatcher tm = new ScoreFunctionMatcher(reference, target);

		FloatProcessor matchScore = tm.calcMatchScore();

		new ImagePlus("matchScore", matchScore).show();

		List<Pixel> bestLocalMinima = new LocalMinMaxDetector()
				.findLocalMinima(matchScore, numberOfMatches);

		visualizeResult(bestLocalMinima, target.convertToColorProcessor(),
				ip.getRoi().width, ip.getRoi().height, Color.YELLOW);

	}

	private Font LabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	private void visualizeResult(List<Pixel> localMinima, ColorProcessor cp,
			int referenceWidth, int referenceHeight, Color color) {

		final int[] pink = new int[] { 255, 105, 180 };

		ImagePlus detected = new ImagePlus("detected", cp);

		Overlay oly = new Overlay();

		final int xo = 6, yo = 2;

		int matchNumber = 0;

		for (Pixel p : localMinima) {

			IJ.log(" -> " + p.toString());

			TextRoi text = new TextRoi(p.x + xo, p.y + yo, "#" + matchNumber,
					LabelFont);
			text.setStrokeColor(color);
			oly.add(text);

			oly.add(makeStraightLine(p.x, p.y, p.x + referenceWidth, p.y, color));
			oly.add(makeStraightLine(p.x, p.y, p.x, p.y + referenceHeight,
					color));
			oly.add(makeStraightLine(p.x, p.y + referenceHeight, p.x
					+ referenceWidth, p.y + referenceHeight, color));
			oly.add(makeStraightLine(p.x + referenceWidth, p.y, p.x
					+ referenceWidth, p.y + referenceHeight, color));

			matchNumber++;

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
