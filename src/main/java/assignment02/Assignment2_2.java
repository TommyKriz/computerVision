package assignment02;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * This ImageJ plugin collects the foreground points in
 * a binary image, converts to a new color image and
 * draws half of the dots in color.
 * 
 * @author WB
 * @version 2018-08-27
 */
public class Assignment2_2 implements PlugInFilter {
	
	public int setup(String arg, ImagePlus im) {
		return PlugInFilter.DOES_8G;
	}

	public void run(ImageProcessor ip) {
		
		
		
		RealMatrix M = MatrixUtils.createRealMatrix(new double[][] 
				{{ 0.013, 	1.088, 	18.688}, 
				 {-1.000, 	-0.050, 127.500}});
		
		ImageProcessor ip2 = transformImage(ip, M);
		
		
		// Display the newly created image:
		showImage(ip2, "colored dots");
	}
	
	void showImage(ImageProcessor ip, String title) {
		(new ImagePlus(title, ip)).show();
	}

	
	
	ImageProcessor transformImage(ImageProcessor ip, RealMatrix M) {
		
		ImageProcessor ipTrans = new ByteProcessor(ip.getWidth(), ip.getHeight());
		Point[] points = getPointArray(ip);
		
		double error = 0; 
		
		for (Point p : points) {
			RealVector a = MatrixUtils.createRealVector(new double[] {p.x, p.y, 1});
			RealVector b = M.operate(a);
			
			double bX = b.getEntry(0);
			double bY = b.getEntry(1);
			
			double distance = Math.sqrt(Math.pow(bX - Math.round(bX), 2) + Math.pow(bY - Math.round(bY), 2));
			error += distance; 
			
			ipTrans.putPixel((int)Math.round(bX), (int)Math.round(bY), 255);
		}
		
		IJ.log("Residual Error: " + error);
		
		ipTrans.invert();
		return ipTrans;
	}
	
	Point[] getPointArray(ImageProcessor ip) {
		int w = ip.getWidth();
		int h = ip.getHeight();
		
		// Collect all image points with pixel values greater than zero:
		List<Point> pntlist = new ArrayList<Point>();
		for (int v = 0; v < h; v++) {
			for (int u = 0; u < w; u++) {
				int p = ip.getPixel(u, v);
				if (p > 0) {
					pntlist.add(new Point(u, v));
				}
			}
		}
		
		Point[] pntarr = pntlist.toArray(new Point[0]);
		IJ.log("Found " + pntarr.length + " foreground points.");
		return pntarr; 
	}
}
