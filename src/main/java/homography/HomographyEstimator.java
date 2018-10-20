package homography;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresFactory;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;

/**
 * Implements the Direct Linear Method for homography estimation
 * (part of the imagingbook calibration toolbox).
 * 
 * @author W. Burger
 * @version 2018/05/30
 */
public class HomographyEstimator {
	
	static int maxLmEvaluations = 1000;
	static int maxLmIterations = 1000;
	static boolean normalize = true;
	
	public static ProjectiveMapping getHomographyMapping(Point2D[] ptsA, Point2D[] ptsB) {
		RealMatrix H = getHomography(ptsA, ptsB);
		return new ProjectiveMapping(
				H.getEntry(0, 0),
				H.getEntry(0, 1),
				H.getEntry(0, 2),
				H.getEntry(1, 0),
				H.getEntry(1, 1),
				H.getEntry(1, 2),
				H.getEntry(2, 0),
				H.getEntry(2, 1),
				false
				);
	}
	
	public static RealMatrix getHomography(Point2D[] ptsA, Point2D[] ptsB) {
		HomographyEstimator he = new HomographyEstimator();
		RealMatrix Hinit = he.estimateHomography(ptsA, ptsB);
		RealMatrix H = he.refineHomography(Hinit, ptsA, ptsB);
		return H;
	}
	
	public HomographyEstimator() {
	}
		
	public RealMatrix[] estimateHomographies(Point2D[] modelPts, Point2D[][] obsPoints) {
		final int M = obsPoints.length;
		RealMatrix[] homographies = new RealMatrix[M];
		for (int i = 0; i < M; i++) {
			RealMatrix Hinit = estimateHomography(modelPts, obsPoints[i]);
			RealMatrix H = refineHomography(Hinit, modelPts, obsPoints[i]);
			homographies[i] = H;
		}
		return homographies;
	}
	
	public RealMatrix estimateHomography(Point2D[] ptsA, Point2D[] ptsB) {
		System.out.println("estimating homography");
		int n = ptsA.length;
		
		RealMatrix Na = (normalize) ? getNormalisationMatrix(ptsA) : MatrixUtils.createRealIdentityMatrix(3);
		RealMatrix Nb = (normalize) ? getNormalisationMatrix(ptsB) : MatrixUtils.createRealIdentityMatrix(3);
		
		RealMatrix M = MatrixUtils.createRealMatrix(n * 2, 9);

		for (int j = 0, k = 0; j < ptsA.length; j++, k += 2) {
			final double[] pA = transform(MathUtil.toArray(ptsA[j]), Na);
			final double[] pB = transform(MathUtil.toArray(ptsB[j]), Nb);
			final double xA = pA[0];
			final double yA = pA[1];
			final double xB = pB[0];
			final double yB = pB[1];			
			M.setRow(k + 0, new double[] {xA, yA, 1, 0, 0, 0, -(xA * xB), -(yA * xB), -(xB)});
			M.setRow(k + 1, new double[] {0, 0, 0, xA, yA, 1, -(xA * yB), -(yA * yB), -(yB)});
		}

		// find h, such that M . h = 0:
		double[] h = MathUtil.solveHomogeneousSystem(M).toArray();
		
		// assemble homography matrix H from h:
		RealMatrix H = MatrixUtils.createRealMatrix(new double[][] 
				{{h[0], h[1], h[2]},
				 {h[3], h[4], h[5]},
				 {h[6], h[7], h[8]}} );

		// de-normalize the homography
		H = MatrixUtils.inverse(Nb).multiply(H).multiply(Na);
		
		// rescale M such that H[2][2] = 1 (unless H[2][2] close to 0)
		if (Math.abs(H.getEntry(2, 2)) > 10e-8) {
			H = H.scalarMultiply(1.0 / H.getEntry(2, 2));
		}
		return H;
	}
	

	/**
	 * Refines the initial homography by Levenberg-Marquart nonlinear optimization.
	 * @param Hinit initial homography matrix
	 * @param modelPts model points (2D)
	 * @param obsPts observed points (2D)
	 * @return the refined homography matrix
	 */
	public RealMatrix refineHomography(RealMatrix Hinit, Point2D[] modelPts, Point2D[] obsPts) {
		final int M = modelPts.length;		
		double[] observed = new double[2 * M];
		for (int i = 0; i < M; i++) {
			observed[i * 2 + 0] = obsPts[i].getX();
			observed[i * 2 + 1] = obsPts[i].getY();
		}			
		MultivariateVectorFunction value = getValueFunction(modelPts);
		MultivariateMatrixFunction jacobian = getJacobianFunction(modelPts);
		
		LeastSquaresProblem problem = LeastSquaresFactory.create(
				LeastSquaresFactory.model(value, jacobian),
				MatrixUtils.createRealVector(observed), 
				MathUtil.getRowPackedVector(Hinit), 
				null,  // ConvergenceChecker
				maxLmEvaluations, 
				maxLmIterations);
		
		LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
		Optimum result = lm.optimize(problem);
		
//		Optimum result = lm.optimize(LeastSquaresFactory.create(
//				LeastSquaresFactory.model(value, jacobian),
//				MatrixUtils.createRealVector(observed), 
//				MathUtil.getRowPackedVector(Hinit), 
//				null,  // ConvergenceChecker
//				maxLmEvaluations, 
//				maxLmIterations));
		
		RealVector optimum = result.getPoint();
		RealMatrix Hopt = MathUtil.fromRowPackedVector(optimum, 3, 3);
//		System.out.println("LM optimizer iterations " + result.getIterations());
		return Hopt.scalarMultiply(1.0 / Hopt.getEntry(2, 2));
	}
	
	
	private MultivariateVectorFunction getValueFunction(final Point2D[] X) {
		System.out.println("MultivariateVectorFunction getValueFunction");
		return new MultivariateVectorFunction() {
			@Override
			public double[] value(double[] h) { // throws IllegalArgumentException {
				final double[] Y = new double[X.length * 2];
				for (int j = 0; j < X.length; j++) {
					final double x = X[j].getX();
					final double y = X[j].getY();
					final double w = h[6] * x + h[7] * y + h[8];
					Y[j * 2 + 0] = (h[0] * x + h[1] * y + h[2]) / w;
					Y[j * 2 + 1] = (h[3] * x + h[4] * y + h[5]) / w;
				}
				return Y;
			}
		};
	}
	
	protected MultivariateMatrixFunction getJacobianFunction(final Point2D[] X) {
		return new MultivariateMatrixFunction() {
			@Override
			public double[][] value(double[] h) {
				final double[][] J = new double[2 * X.length][];
				for (int i = 0; i < X.length; i++) {
					final double x = X[i].getX();
					final double y = X[i].getY();
					
					final double w  = h[6] * x + h[7] * y + h[8];
					final double w2 = w * w;
					
					final double sx = h[0] * x + h[1] * y + h[2];		
					J[2 * i + 0] = new double[] {x/w, y/w, 1/w, 0, 0, 0, -sx*x/w2, -sx*y/w2, -sx/w2};
					
					final double sy = h[3] * x + h[4] * y + h[5];
					J[2 * i + 1] = new double[] {0, 0, 0, x/w, y/w, 1/w, -sy*x/w2, -sy*y/w2, -sy/w2};
				}
				return J;
			}
		};
	}	
	
	private double[] transform(double[] p, RealMatrix M3x3) {
		double[] pA = MathUtil.toHomogeneous(p);
		double[] pAt = M3x3.operate(pA);
		return MathUtil.toCartesian(pAt); // need to de-homogenize, since pAt[2] == 1?
	}
	
	private RealMatrix getNormalisationMatrix(Point2D[] pnts) {
		final int N = pnts.length;
		double[] x = new double[N];
		double[] y = new double[N];
		
		for (int i = 0; i < N; i++) {
			x[i] = pnts[i].getX();
			y[i] = pnts[i].getY();
		}
		
		// calculate the means in x/y
		double meanx = MathUtil.mean(x);
		double meany = MathUtil.mean(y);

		// calculate the variances in x/y
		double varx = MathUtil.variance(x);
		double vary = MathUtil.variance(y);
		
		double sx = Math.sqrt(2 / varx);
		double sy = Math.sqrt(2 / vary);

		RealMatrix matrixA = MatrixUtils.createRealMatrix(new double[][] {
				{ sx,  0, -sx * meanx},
				{  0, sy, -sy * meany},
				{  0,  0,           1 }});
		
		return matrixA;
	}
	

	// TESTING --------------------------------------------------------
	
	static Random rand = new Random();
	private static Point2D apply(RealMatrix H, Point2D X, double noise) {
		
		double[] Xa = {X.getX(), X.getY(), 1};
		double[] Xb = H.operate(Xa);
		double xn = noise * rand.nextGaussian();
		double yn = noise * rand.nextGaussian();
		return new Point2D.Double(xn + Xb[0]/Xb[2], yn + Xb[1]/Xb[2]);
	}
	
	public static void main(String[] args) {
		RealMatrix Hreal = MatrixUtils.createRealMatrix(new double[][]
				{{3, 2, -1},
				{5, 0, 2},
				{4, 4, 9}});
		
		List<Point2D> pntlistA = new ArrayList<Point2D>();
		pntlistA.add(new Point2D.Double(10, 7));
		pntlistA.add(new Point2D.Double(3, -1));
		pntlistA.add(new Point2D.Double(5, 5));
		pntlistA.add(new Point2D.Double(-6, 13));
		pntlistA.add(new Point2D.Double(0, 1));
		pntlistA.add(new Point2D.Double(2, 3));
		
		List<Point2D> pntlistB = new ArrayList<Point2D>();
		for (Point2D a : pntlistA) {
			pntlistB.add(apply(Hreal, a, 0.1));
		}
		
		Point2D[] pntsA = pntlistA.toArray(new Point2D[0]);
		Point2D[] pntsB = pntlistB.toArray(new Point2D[0]);
		
		for (int i = 0; i < pntsA.length; i++) {
			Point2D a = pntsA[i];
			Point2D b = pntsB[i];
			System.out.format("(%.3f, %.3f) -> (%.3f, %.3f)\n", a.getX(), a.getY(), b.getX(), b.getY());
		}
		System.out.println();
		
		HomographyEstimator hest = new HomographyEstimator();
		RealMatrix H = hest.estimateHomography(pntsA, pntsB);
		
		MathUtil.print("H = ", H); System.out.println();
		
		for (Point2D a : pntlistA) {
			Point2D b = apply(H, a, 0);
			System.out.format("(%.3f, %.3f) -> (%.3f, %.3f)\n", a.getX(), a.getY(), b.getX(), b.getY());
		}
		
	}

}
