package assignment03;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.process.ImageProcessor;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.HarrisCornerDetector;

import java.util.Collections;
import java.util.List;

public class CornerDetector {

	public List<Corner> findCorners(ImageProcessor ip) {
		HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
		if (!showDialog(params)) {
			return null;
		}
		HarrisCornerDetector cd = new HarrisCornerDetector(ip, params);
		return cd.findCorners();
	}

	public List<Corner> strongestCorners(List<Corner> unsorted, int m) {
		List<Corner> sorted = unsorted;
		Collections.sort(sorted);
		if (m > sorted.size()) {
			m = sorted.size();
		}
		return sorted.subList(0, m);
	}

	private boolean showDialog(HarrisCornerDetector.Parameters params) {
		// display dialog , return false if canceled or on error.
		GenericDialog dlg = new GenericDialog("Harris Corner Detector");
		dlg.addNumericField("Alpha", params.alpha, 3);
		dlg.addNumericField("Threshold", params.tH, 0);
		dlg.addCheckbox("Clean up corners", params.doCleanUp);
		dlg.showDialog();
		if (dlg.wasCanceled())
			return false;
		params.alpha = dlg.getNextNumber();
		params.tH = (int) dlg.getNextNumber();
		params.doCleanUp = dlg.getNextBoolean();
		if (dlg.invalidNumber()) {
			IJ.error("Input Error", "Invalid input number");
			return false;
		}
		return true;
	}
}
