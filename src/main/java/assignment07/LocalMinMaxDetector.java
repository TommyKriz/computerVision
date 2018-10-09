package assignment07;

import ij.process.FloatProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Pixel {
	int x;
	int y;
	float data;

	public Pixel(int x, int y, float data) {
		this.x = x;
		this.y = y;
		this.data = data;
	}

	@Override
	public String toString() {
		return " x: " + x + " y: " + y + " data: " + data;
	}

}

public class LocalMinMaxDetector {

	private static final float NOT_A_LOCAL_EXTREME = -13;

	/**
	 * get the average for a 3x3 kernel:
	 * 
	 * <pre>
	 * xxx
	 * xix
	 * xxx
	 * </pre>
	 * 
	 * ignores the border pixels
	 */
	public List<Pixel> findLocalMinima(FloatProcessor matchScore) {
		List<Pixel> localAverages = new ArrayList<>();
		for (int x = 1; x < matchScore.getWidth() - 1; x++) {
			for (int y = 1; y < matchScore.getHeight() - 1; y++) {
				float pixelValue = checkForLocalMinimum(x, y, matchScore);
				if (pixelValue != NOT_A_LOCAL_EXTREME) {
					localAverages.add(new Pixel(x, y, pixelValue));
				}
			}
		}
		return localAverages;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param matchScore
	 * @return -1 if [x,y] is NOT a local average
	 */
	private float checkForLocalMinimum(int x, int y, FloatProcessor matchScore) {
		float value = matchScore.getPixelValue(x, y);
		for (int xKernel = -1; xKernel < 2; xKernel++) {
			for (int yKernel = -1; yKernel < 2; yKernel++) {
				if (!(xKernel == 0 && yKernel == 0)) {
					if (matchScore.getPixelValue(x + xKernel, y + yKernel) <= value) {
						return NOT_A_LOCAL_EXTREME;
					}
				}
			}
		}
		return value;
	}

	public List<Pixel> findLocalMinima(FloatProcessor matchScore, int n) {
		return bestMatches(n, findLocalMinima(matchScore));
	}

	private List<Pixel> bestMatches(int n, List<Pixel> localExtremes) {

		if (n >= localExtremes.size()) {
			return localExtremes;
		}

		Collections.sort(localExtremes, new Comparator<Pixel>() {
			@Override
			public int compare(Pixel p1, Pixel p2) {
				if (p1.data < p2.data) {
					return -1;
				} else if (p1.data > p2.data) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		// TODO: reverse list when looking for maxima !!
		return localExtremes.subList(0, n);
	}
}
