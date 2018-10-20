package assignment03;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import procrustes.ProcrustesFit;

public class ICP {

	private int[] pointAssociationTable;

	private List<Point2D> points1;
	private List<Point2D> points2;

	private int iteration = 0;
	private double minimalError = Double.POSITIVE_INFINITY;
	private double error = 0.0;
	private boolean converged = false;

	private RealMatrix T;

	public void iterativeClosestPointMatch(List<Point2D> referencePointSet1,
			List<Point2D> referencePointSet2,
			double minErrorImprovementBetweenSuccesiveIterations,
			int maxIterations) {

		points1 = referencePointSet1;
		points2 = referencePointSet2;

		T = initialTransformation(referencePointSet1, referencePointSet2);

		while (!converged && iteration < maxIterations) {
			associatePoints();
			fitPoints();

			double errorImprovement = (minimalError - error);
			// IJ.log("Error improvement " + errorImprovement);
			if (0 <= errorImprovement
					&& errorImprovement < minErrorImprovementBetweenSuccesiveIterations) {
				converged = true;
			}

			minimalError = error;
			iteration++;
		}

	}

	private void fitPoints() {
		List<Point2D> tmp = new ArrayList<>();
		for (int i = 0; i < pointAssociationTable.length; i++) {
			tmp.add(points2.get(pointAssociationTable[i]));
		}

		ProcrustesFit procrustes = new ProcrustesFit(true, true, false);
		procrustes.fitPoints(points1, tmp);

		T = procrustes.getTransformationMatrix();
		error = procrustes.getError();
	}

	private void associatePoints() {
		int numPoints = points1.size();
		pointAssociationTable = new int[numPoints];
		for (int i = 0; i < numPoints - 1; i++) {
			pointAssociationTable[i] = findNearestPointIndexAfterApplyingTransformation(points1
					.get(i));
		}
	}

	private int findNearestPointIndexAfterApplyingTransformation(Point2D p) {
		// apply T to the point
		double[] transformedPoint = T.operate(new double[] { p.getX(),
				p.getY(), 1.0 });

		int nearestPointIndex = 0;
		double d = Double.POSITIVE_INFINITY;
		for (int j = 0; j < points2.size(); j++) {
			double tmp = points2.get(j).distance(transformedPoint[0],
					transformedPoint[1]);
			if (tmp < d) {
				d = tmp;
				nearestPointIndex = j;
			}
		}
		return nearestPointIndex;
	}

	private RealMatrix initialTransformation(List<Point2D> referencePointSet1,
			List<Point2D> referencePointSet2) {
		double[] centroid1 = getCloudCentroid(referencePointSet1);
		double[] centroid2 = getCloudCentroid(referencePointSet2);
		// "fake" vector substraction
		return buildTranslationOnlyTransformationMatrix(centroid2[0]
				- centroid1[0], centroid2[1] - centroid1[1]);
	}

	private RealMatrix buildTranslationOnlyTransformationMatrix(double tx,
			double ty) {
		return MatrixUtils.createRealMatrix(new double[][] { { 1.0, 0.0, tx },
				{ 0.0, 1.0, ty }, { 0.0, 0.0, 1.0 } });
	}

	private double[] getCloudCentroid(List<Point2D> pointCloud) {
		int numPoints = pointCloud.size();
		float x = 0.0f;
		float y = 0.0f;
		for (int i = 0; i < numPoints; i++) {
			x += pointCloud.get(i).getX();
			y += pointCloud.get(i).getY();
		}
		return new double[] { x / numPoints, y / numPoints };
	}

	public int[] getPointAssociationTable() {
		return pointAssociationTable;
	}

	public RealMatrix getT() {
		return T;
	}

	public int getIteration() {
		return iteration;
	}

	public double getError() {
		return error;
	}

}
