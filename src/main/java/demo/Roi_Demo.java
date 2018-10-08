package demo;

import java.awt.Point;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


/**
 * This plugin demonstrates how to iterate over all points contained in a
 * user-selected ROI (possibly composed of multiple ROIs).
 * 
 * @author WB
 * @version 2018/05/08
 */
public class Roi_Demo implements PlugInFilter {

	private ImagePlus im = null;

	public int setup(String arg, ImagePlus im) {
		this.im = im;
		return DOES_RGB + ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		Roi roi = im.getRoi();
		if (roi == null)  // no ROI selected
			return;
		
		final int[] rgb = new int[3];
		for (Point p : roi) {
			ip.getPixel(p.x, p.y, rgb);
			rgb[1] = 0; rgb[2] = 255;	// modify green/blue channels
			ip.putPixel(p.x, p.y, rgb);
		}
	}

}
