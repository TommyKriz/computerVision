package assignment09;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.ShapeRoi;
import ij.gui.TextRoi;
import ij.process.Blitter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.interpolation.InterpolationMethod;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;
import imagingbook.pub.sift.SiftDescriptor;
import imagingbook.pub.sift.SiftMatch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

public class SiftMatchingVisualization {

	static int NumberOfMatchesToShow = 25;
	static double MatchLineCurvature = 0.25;
	static double FeatureScale = 1.0;
	static double FeatureStrokewidth = 1.0;

	static boolean ShowFeatureLabels = true;

	static Color SeparatorColor = Color.black;
	static Color DescriptorColor1 = Color.green;
	static Color DescriptorColor2 = Color.green;
	static Color MatchLineColor = Color.magenta;
	static Color LabelColor = Color.yellow;
	static Font LabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

	public void showMontage(ImageProcessor image1, ImageProcessor image2,
			List<SiftMatch> matches) {

		int w = image1.getWidth();
		int h = image1.getHeight();
		ImageProcessor montage = new ByteProcessor(2 * w, h);

		montage.insert(image1, 0, 0);

		montage.copyBits(image2, w, 0, Blitter.ADD);

		ImagePlus montageIm = new ImagePlus("-matches", montage);

		Overlay oly = new Overlay();
		oly.add(makeStraightLine(w, 0, w, h, Color.YELLOW));

		int xoffset = w;

		drawSiftMarkers(matches, oly, xoffset);

		if (oly != null) {
			montageIm.setOverlay(oly);
		}
		montageIm.show();
	}

	private void drawSiftMarkers(List<SiftMatch> matches, Overlay oly,
			int xoffset) {
		// draw the matched SIFT markers:
		int count = 1;
		for (SiftMatch m : matches) {
			SiftDescriptor dA = m.getDescriptor1();
			SiftDescriptor dB = m.getDescriptor2();
			oly.add(makeSiftMarker(dA, 0, 0, DescriptorColor1));
			oly.add(makeSiftMarker(dB, xoffset, 0, DescriptorColor2));
			count++;
			if (count > NumberOfMatchesToShow)
				break;
		}

		// draw the connecting lines:
		count = 1;
		for (SiftMatch m : matches) {
			SiftDescriptor dA = m.getDescriptor1();
			SiftDescriptor dB = m.getDescriptor2();
			oly.add(makeConnectingLine(dA, dB, xoffset, 0, MatchLineColor));
			count++;
			if (count > NumberOfMatchesToShow)
				break;
		}

		// draw the labels:
		if (ShowFeatureLabels) {
			count = 1;
			for (SiftMatch m : matches) {
				SiftDescriptor dA = m.getDescriptor1();
				SiftDescriptor dB = m.getDescriptor2();
				String label = Integer.valueOf(count).toString();
				oly.add(makeSiftLabel(dA, 0, 0, label));
				oly.add(makeSiftLabel(dB, xoffset, 0, label));
				count++;
				if (count > NumberOfMatchesToShow)
					break;
			}
		}
	}

	private ShapeRoi makeStraightLine(double x1, double y1, double x2,
			double y2, Color col) {
		Path2D poly = new Path2D.Double();
		poly.moveTo(x1, y1);
		poly.lineTo(x2, y2);
		ShapeRoi roi = new ShapeRoi(poly);
		roi.setStrokeWidth((float) FeatureStrokewidth);
		roi.setStrokeColor(col);
		return roi;
	}

	private ShapeRoi makeSiftMarker(SiftDescriptor d, double xo, double yo,
			Color col) {
		double x = d.getX() + xo;
		double y = d.getY() + yo;
		double scale = FeatureScale * d.getScale();
		double orient = d.getOrientation();
		double sin = Math.sin(orient);
		double cos = Math.cos(orient);
		Path2D poly = new Path2D.Double();
		poly.moveTo(x + (sin - cos) * scale, y - (sin + cos) * scale);
		// poly.lineTo(x, y);
		poly.lineTo(x + (sin + cos) * scale, y + (sin - cos) * scale);
		poly.lineTo(x, y);
		poly.lineTo(x - (sin - cos) * scale, y + (sin + cos) * scale);
		poly.lineTo(x - (sin + cos) * scale, y - (sin - cos) * scale);
		poly.closePath();
		ShapeRoi roi = new ShapeRoi(poly);
		roi.setStrokeWidth((float) FeatureStrokewidth);
		roi.setStrokeColor(col);
		return roi;
	}

	private ShapeRoi makeConnectingLine(SiftDescriptor f1, SiftDescriptor f2,
			double xo, double yo, Color col) {
		double x1 = f1.getX();
		double y1 = f1.getY();
		double x2 = f2.getX() + xo;
		double y2 = f2.getY() + yo;
		double dx = x2 - x1;
		double dy = y2 - y1;
		double ctrlx = (x1 + x2) / 2 - MatchLineCurvature * dy;
		double ctrly = (y1 + y2) / 2 + MatchLineCurvature * dx;
		Shape curve = new QuadCurve2D.Double(x1, y1, ctrlx, ctrly, x2, y2);
		ShapeRoi roi = new ShapeRoi(curve);
		roi.setStrokeWidth((float) FeatureStrokewidth);
		roi.setStrokeColor(col);
		return roi;
	}

	private TextRoi makeSiftLabel(SiftDescriptor d, double xo, double yo,
			String text) {
		double x = d.getX() + xo;
		double y = d.getY() + yo;
		TextRoi roi = new TextRoi((int) Math.rint(x), (int) Math.rint(y), text,
				LabelFont);
		roi.setStrokeColor(LabelColor);
		return roi;
	}
}
