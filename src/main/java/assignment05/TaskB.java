package assignment05;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.util.DirectoryWalker;
import imagingbook.lib.util.SerializationHelper;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * ImageJ plugin demonstrating the recursive traversal of an image directory,
 * starting from a user-selected root directory. It collects a list of all image
 * paths found, reads every image on the list and accumulates the number of
 * pixels (for whatever purpose). The images are supposed to be readable as
 * ByteProcessor objects, which includes 8-bit grayscale and indexed color
 * images (such as those in the Kimia database, which are only 1 bit deep).
 * 
 * @author WB
 * @version 2018/04/02
 */
public class TaskB implements PlugIn {

	static final String DataBaseFileName = "target/db";

	public void run(String arg0) {
		String dir = IJ.getDirectory("Select start directory...");
		if (dir == null)
			return;

		IJ.log("Collecting files in: " + dir);
		DirectoryWalker dw = new DirectoryWalker(".gif");
		Collection<String> paths = dw.collectFiles(dir);
		IJ.log("Found files: " + paths.size());

		long starttime = System.currentTimeMillis();
		int fileCount = 0;

		HashMap<String, double[][]> histograms = new HashMap<>();

		for (String path : paths) {
			fileCount++;
			IJ.log(fileCount + " Opening " + path);
			ImagePlus img = IJ.openImage(path);
			if (img == null) {
				IJ.log("Could not open " + path);
				continue;
			}
			ImageProcessor ip = img.getProcessor();
			if (!(ip instanceof ByteProcessor)) {
				IJ.log("Wrong image type: " + path);
				continue;
			}
			ByteProcessor bp = (ByteProcessor) ip;

			// create the region labeler / contour tracer:
			RegionContourLabeling segmenter = new RegionContourLabeling(
					(ByteProcessor) ip);

			// Retrieve the list of detected regions, no sorting!
			List<BinaryRegion> regions = segmenter.getRegions(true);

			for (BinaryRegion R : regions) {

				try {

					double[][] h = new CPDH(R.getOuterContour())
							.getNormalizedHistogram();
					histograms.put(path, h);
				} catch (Exception e) {
					IJ.log("Error calculating histogram for image " + path);
				}

			}

		}
		double timeUsed = (System.currentTimeMillis() - starttime) / 1000.0;
		IJ.log("Processed " + fileCount + " images in " + timeUsed + " sec.");

		String serializedHistogramsPath = SerializationHelper.writeObject(
				histograms, DataBaseFileName);
		if (serializedHistogramsPath == null)
			IJ.log("Could not write data to " + DataBaseFileName);
		else
			IJ.log("Data written to " + serializedHistogramsPath);

	}
}
