package assignment05;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.SerializationHelper;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import assignment04.CPDH_Graphic;

public class TaskC implements PlugInFilter {

	private static final int TOP_MATCHES_TO_SHOW = 10;
	static final String DataBaseFileName = "target/db";

	public void run(ImageProcessor ip) {

		HashMap<String, double[][]> referenceHistograms = (HashMap<String, double[][]>) SerializationHelper
				.readObject(DataBaseFileName);

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

		TreeMap<Double, String> foundMatches = new TreeMap<>();

		for (BinaryRegion R : regions) {

			double[][] h = new CPDH_Graphic(cp, R.getOuterContour())
					.getNormalizedHistogram();

			double bestError = Double.MAX_VALUE;

			for (String path : referenceHistograms.keySet()) {

				double error = getManhattanDistance(h,
						referenceHistograms.get(path));
				if (error < bestError) {
					bestError = error;
					foundMatches.put(error, path);
				}

			}

			int i = 0;
			for (Double d : foundMatches.keySet()) {
				if (i > TOP_MATCHES_TO_SHOW) {
					return;
				}
				// IJ.log(" match #" + i + ":  " + foundMatches.get(d));
				String path = foundMatches.get(d);
				ImagePlus match = IJ.openImage(path);
				match.setTitle("match #" + (i + 1) + " - path: " + path
						+ "  - error: " + d);
				match.show();
				i++;
			}

		}

	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		// TODO Auto-generated method stub
		return DOES_ALL;
	}

	// aka the L1 Norm
	private double getManhattanDistance(double[][] h1,
			double[][] referenceHistogram) {
		int w = h1.length;
		int h = h1[0].length;
		double sum = 0;
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				sum += Math.abs(h1[x][y] - referenceHistogram[x][y]);
			}
		}

		double sumMirrored = 0;
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				sumMirrored += Math.abs(h1[x][y]
						- referenceHistogram[x][h - y % h - 1]);
			}
		}

		if (sumMirrored < sum) {
			return sumMirrored;
		} else {
			return sum;
		}
	}

}