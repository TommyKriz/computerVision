package assignment06;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class Assignment06 implements PlugInFilter {

	@Override
	public int setup(String arg, ImagePlus imp) {
		// TODO Auto-generated method stub
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {

		getUserInput();

		ImageStack stack = new Laws_Texture_Energy_Util().calc(ip);

		int w = stack.getWidth(); // all stack images have the same size
		int h = stack.getHeight();

		float[][][] featureVector = new float[w][h][9];

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				for (int i = 0; i < stack.size(); i++) {
					FloatProcessor fp = (FloatProcessor) stack
							.getProcessor(i + 1);
					featureVector[x][y][i] = fp.getPixelValue(x, y);
				}
			}
		}

		for (int n = 0; n < 9; n++) {
			IJ.log("featureVector [" + n + "]: " + featureVector[2][1][n]
					+ "\n");
		}

		K_Means_Clustering_Stack kMeansClustering = new K_Means_Clustering_Stack(
				featureVector, 16);
		kMeansClustering.start(ip.convertToColorProcessor());

	}

	private static int K = 2; // width of the new image

	private void getUserInput() {
		GenericDialog gd = new GenericDialog(
				"How many different textures are used in the image?");
		gd.addNumericField("K: ", K, 2);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return;
		}
		K = (int) gd.getNextNumber();
		return;
	}

}
