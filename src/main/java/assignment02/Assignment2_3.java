package assignment02;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import delaunay.lib.IjUtil;
import delaunay.lib.Triangle;
import delaunay.lib.Triangulation;
import delaunay.lib.Vertex;

/**
 * This IJ plugin opens an image stack (TIF) that is assumed to contain 
 * at least 2 images of identical size.
 * 
 * @author WB
 *
 */
public class Assignment2_3 implements PlugInFilter {
	
	ImagePlus im = null;
	static String title = "Delaunay triangulation";
	private static Color DelaunayColor = Color.green;
	private static Color PointColor = Color.magenta;
	
	public int setup(String arg, ImagePlus im) {
		
		this.im = im;	// keep a reference to im
		return DOES_8G + STACK_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		int k = im.getStackSize();
		if (k < 2) {
			IJ.error("stack with 2 images required");
			return;
		}
		
		ImageStack stack = im.getStack();
		ImageProcessor ip1 = stack.getProcessor(1);
		ImageProcessor ip2 = stack.getProcessor(2);
	
		Triangle[] tris1 = getTriangles(ip1);
		Triangle[] tris2 = getTriangles(ip2);	 
		
		int numOfChecks = 100; 
		Random rand = new Random(); 
		
		double smallestDiff = -1; 
		RealMatrix bestMatrix = null;
		
		for (int j = 0; j < numOfChecks; j++) {
			for (int i = 0; i < 3; i++) {
				Vertex[] v1 = tris1[rand.nextInt(tris1.length)].getVertices();
				Vertex[] v2 = tris2[rand.nextInt(tris2.length)].getVertices();
					
				RealMatrix M = createSolverMatrix(v1[(0+i) % 3], v1[(1+i) % 3], v1[(2+i) % 3]);
				RealVector b = createSolverVector(v2[0], v2[1], v2[2]); 
				RealMatrix transM = calcTransformationMatrix(M, b);
					
				double diff = calcTotalPointDifference(ip1 ,ip2, transM);
				if (diff < smallestDiff || smallestDiff < 0) {
					smallestDiff = diff; 
					bestMatrix = transM; 
				}
			}	
		}
		
		IJ.log("Affine transformation matrix: " + bestMatrix);
		IJ.log("Distance error: " + smallestDiff);
		
		applyTransformationAndShow(ip1, bestMatrix);
	}

	void applyTransformationAndShow(ImageProcessor ip, RealMatrix M) {
		Point[] points = getPointArray(ip);
		ImageProcessor ipNew = new ByteProcessor(ip.getWidth(), ip.getHeight());
		
		for (Point p : points) {
			RealVector v =  MatrixUtils.createRealVector(new double[] {p.x, p.y, 1});
			RealVector newPoint = M.operate(v);
			ipNew.putPixel((int)Math.round(newPoint.getEntry(0)), (int)Math.round(newPoint.getEntry(1)), 255);
		}
		ipNew.invert(); 
		showImage(ipNew, "trans");
	}
	
	double calcTotalPointDifference(ImageProcessor ipSource, ImageProcessor ipTarget, RealMatrix M) {
		Point[] sourcePnts = getPointArray(ipSource); 
		Point[] targetPnts = getPointArray(ipTarget);
		double distanceAll = 0; 
		
		for (Point sourcePnt : sourcePnts) {
			double closestDistance = -1;
			RealVector a = MatrixUtils.createRealVector(new double[] {sourcePnt.x, sourcePnt.y, 1});
			RealVector b = M.operate(a);
			
			for (Point targetPnt : targetPnts) {				
				double distance = calcDistanceSquared(targetPnt.x, targetPnt.y, b.getEntry(0), b.getEntry(1));
				if (distance < closestDistance || closestDistance < 0) {
					closestDistance = distance; 
				}
			}
			distanceAll += closestDistance; 
		}
		return distanceAll; 
	}
	
	double calcDistanceSquared(double x1, double y1, double x2, double y2) {
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
	}
	
	RealMatrix calcTransformationMatrix(RealMatrix M, RealVector b) {
		DecompositionSolver solver = new SingularValueDecomposition(M).getSolver();
		RealVector a = solver.solve(b);
		
		return MatrixUtils.createRealMatrix(new double[][] 
			   {	{ a.getEntry(0), a.getEntry(1), a.getEntry(2)}, 
				{ a.getEntry(3), a.getEntry(4), a.getEntry(5)}, 
				{ 0, 0, 1}});	
	}
	
	RealVector createSolverVector(Vertex v1, Vertex v2, Vertex v3) {
		return MatrixUtils.createRealVector(new double[] 
				   {	v1.coord(0), v1.coord(1),
					v2.coord(0), v2.coord(1),
					v3.coord(0), v3.coord(1)}); 
	}
	
	RealMatrix createSolverMatrix(Vertex v1, Vertex v2, Vertex v3) {
		return MatrixUtils.createRealMatrix(new double[][] 
			   {	{ v1.coord(0),  v1.coord(1), 1, 0, 0, 0}, 
				{ 0, 0, 0, v1.coord(0),  v1.coord(1), 1}, 
				{ v2.coord(0),  v2.coord(1), 1, 0, 0, 0}, 
				{ 0, 0, 0, v2.coord(0),  v2.coord(1), 1}, 
				{ v3.coord(0),  v3.coord(1), 1, 0, 0, 0}, 
				{ 0, 0, 0, v3.coord(0),  v3.coord(1), 1}});
	}
	
	Triangle[] getTriangles(ImageProcessor ip) {
		Vertex[] pointsA = IjUtil.collectPoints(ip);
		Triangulation dt = new Triangulation(pointsA, ip.getWidth(), ip.getHeight());
		return dt.getDelaunayTriangles();
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
		return pntarr; 
	}
	
	void showImage(ImageProcessor ip, String title) {
		(new ImagePlus(title, ip)).show();
	}
}
