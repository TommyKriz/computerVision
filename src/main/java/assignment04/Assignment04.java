package assignment04;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.Contour;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

import java.awt.geom.Point2D;
import java.util.List;

public class Assignment04 implements PlugInFilter {

	private ImagePlus im = null;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + DOES_8C + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {

		List<BinaryRegion> regions = getRegions(ip);

		if (regions.isEmpty()) {
			IJ.error("no regions detected");
			return;
		}

		for (BinaryRegion R : regions) {

			Contour outerContour = R.getOuterContour();
			Point2D centerPoint = R.getCenterPoint();

			// float[][] normalizedHistogram = ColorPointDistributionUtil
			// .getNormalizedHistogram(outerContour, centerPoint);

			int[][] histogram = new CPDH_Graphic(ip.convertToColorProcessor(),
					outerContour).getHistogram();

			visualizeHistogram(ColorPointDistributionUtil
					.normalizeHistogram(histogram));

		}

	}

	private void visualizeHistogram(float[][] histogram) {
		int w = histogram.length;
		int h = histogram[0].length;
		FloatProcessor fp = new FloatProcessor(w, h);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				// normalization occurs here
				fp.putPixelValue(x, y, histogram[x][y]);
			}
		}
		new ImagePlus("Normalized Histogram", fp).show();
	}

	private List<BinaryRegion> getRegions(ImageProcessor ip) {
		// create the region labeler / contour tracer:
		RegionContourLabeling segmenter = new RegionContourLabeling(
				(ByteProcessor) ip);

		// Retrieve the list of detected regions, no sorting!
		List<BinaryRegion> regions = segmenter.getRegions(true);
		IJ.log("detected regions: " + regions.size());

		return regions;
	}

}
