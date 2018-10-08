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

}

public class LocalMinMaxDetector {

	private FloatProcessor ip;

	private List<Pixel> localAverages;

	public LocalMinMaxDetector(FloatProcessor ip) {
		this.ip = ip;
		localAverages = calcLocalAverages();
	}

	public List<Pixel> localMins(int n) {
		Collections.sort(localAverages, new Comparator<Pixel>() {
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

		return localAverages.subList(0, n);
	}

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
	private List<Pixel> calcLocalAverages() {
		List<Pixel> localAverages = new ArrayList<>();

		for (int x = 1; x < ip.getWidth() - 1; x++) {
			for (int y = 1; y < ip.getHeight() - 1; y++) {
				localAverages.add(new Pixel(x, y, calcLocalAverage(x, y)));
			}
		}

		return localAverages;
	}

	private float calcLocalAverage(int x, int y) {
		float avg = 0f;
		for (int xKernel = -1; xKernel < 2; xKernel++) {
			for (int yKernel = -1; yKernel < 2; yKernel++) {
				avg += ip.getPixelValue(x + xKernel, x + yKernel);
			}
		}
		return avg / 9f;
	}
}
