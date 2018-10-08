package assignment07;

import ij.IJ;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class TemplateMatcher {

	private float[][] referenceImage;
	private float[][] searchImage;

	private int referenceImageWidth;
	private int referenceImageHeight;

	private int searchRangeX;
	private int searchRangeY;

	public TemplateMatcher(final FloatProcessor referenceImage,
			final FloatProcessor searchImage) {
		this.referenceImage = referenceImage.getFloatArray();
		this.searchImage = searchImage.getFloatArray();

		referenceImageWidth = referenceImage.getWidth();
		referenceImageHeight = referenceImage.getHeight();

		searchRangeX = searchImage.getWidth() - referenceImageWidth;
		searchRangeY = searchImage.getHeight() - referenceImageHeight;
	}

	public FloatProcessor calcMatchScore() {
		FloatProcessor matchScore = new FloatProcessor(searchRangeX,
				searchRangeY);
		for (int x = 0; x < searchRangeX; x++) {
			for (int y = 0; y < searchRangeY; y++) {
				matchScore.putPixelValue(x, y, sumOfSquaredDifferences(x, y));
			}
			IJ.showProgress(x, searchRangeX);
		}
		return matchScore;
	}

	private double sumOfSquaredDifferences(int r, int s) {
		double result = 0.0;
		for (int x = 0; x < referenceImageWidth; x++) {
			for (int y = 0; y < referenceImageHeight; y++) {
				result += Math.pow(searchImage[r + x][s + y]
						- referenceImage[x][y], 2.0);
			}
		}
		return Math.sqrt(result);
	}

}
