package assignment04;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

import java.util.List;

/**
 * Only works for the supplied image shape-montage-2.png
 * 
 * @author Tommy
 *
 */
public class Assignment04 implements PlugInFilter {

	private ImagePlus im = null;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_8G + DOES_8C + NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		// create the region labeler / contour tracer:
		RegionContourLabeling segmenter = new RegionContourLabeling(
				(ByteProcessor) ip);

		// Retrieve the list of detected regions, no sorting!
		List<BinaryRegion> regions = segmenter.getRegions(true);
		IJ.log("detected regions: " + regions.size());

		if (regions.isEmpty()) {
			IJ.error("no regions detected");
			return;
		}

		ColorProcessor cp = ip.convertToColorProcessor();

		for (BinaryRegion R : regions) {

			double[][] h = new CPDH_Graphic(cp, R.getOuterContour())
					.getNormalizedHistogram();

			visualizeHistogram(h);

		}

		new ImagePlus(im.getTitle()
				+ "-associatedContourPointsWithinPolarCoordinates", cp).show();
	}

	private void visualizeHistogram(double[][] histogram) {
		int w = histogram.length;
		int h = histogram[0].length;
		FloatProcessor fp = new FloatProcessor(h, w);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				fp.putPixelValue(y, x, histogram[x][y]);
			}
		}
		new ImagePlus("Normalized Histogram", fp).show();
	}

}
