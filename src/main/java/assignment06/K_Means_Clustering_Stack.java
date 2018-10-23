package assignment06;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class K_Means_Clustering_Stack {

	List<Cluster> clusters = new ArrayList<>();

	private int k;

	private final static int MAX_ITERATIONS = 200;

	private static final int MAX_FITTING_ITERATIONS = 30;

	private float[][][] featureVector;

	public K_Means_Clustering_Stack(float[][][] featureVector, int k) {
		this.k = k;
		this.featureVector = featureVector;
		initClustersRandom(featureVector.length, featureVector[0].length);

	}

	private void initClustersRandom(int w, int h) {
		clusters.clear();
		int x;
		int y;
		for (int i = 0; i < k; i++) {
			x = ThreadLocalRandom.current().nextInt(0, w);
			y = ThreadLocalRandom.current().nextInt(0, h);
			clusters.add(new Cluster(featureVector[x][y]));
		}
	}

	public void start(ColorProcessor cp) {

		ImageStack stack = new ImageStack(cp.getWidth(), cp.getHeight());

		stack.addSlice(cp);

		ImagePlus animatedKMeansClustering = new ImagePlus(
				"K Means Clustering", stack);
		animatedKMeansClustering.show();

		double bestResult = Double.MAX_VALUE;
		double error = Double.MAX_VALUE;

		for (int i = 0; i < MAX_ITERATIONS; i++) {

			int fittingIteration = 0;
			while (error >= bestResult
					&& fittingIteration < MAX_FITTING_ITERATIONS) {
				assignFeaturePointsToClusterCenter();
				recalculateClusterCenters();
				error = calculateTotalIntraClusterScatter();
				fittingIteration++;
			}

			if (error < bestResult) {
				bestResult = error;
				stack.addSlice(drawClusters(cp.convertToColorProcessor()));
				IJ.log("Iteration #" + i + "   Intra-Cluster Scatter: " + error
						+ "  -fittingIterations: " + fittingIteration);
			} else {
				IJ.log("bad iteration");
			}

			initClustersRandom(featureVector.length, featureVector[0].length);
		}
	}

	private double calculateTotalIntraClusterScatter() {
		double sum = 0.0;
		for (int i = 0; i < clusters.size(); i++) {
			sum += clusters.get(i).calcIntraClusterScatter(featureVector);
		}
		return sum;
	}

	private void recalculateClusterCenters() {
		for (int i = 0; i < clusters.size(); i++) {
			clusters.get(i).recalculateCenterPoint(featureVector);
		}
	}

	private void assignFeaturePointsToClusterCenter() {
		for (int x = 0; x < featureVector.length; x++) {
			for (int y = 0; y < featureVector[0].length; y++) {
				float[] f = featureVector[x][y];
				int closestClusterCenterIdx = -1;
				double distance = Double.MAX_VALUE;
				for (int i = 0; i < clusters.size(); i++) {
					double d = clusters.get(i).distanceFromCenter(f);
					if (d < distance) {
						distance = d;
						closestClusterCenterIdx = i;
					}
				}
				clusters.get(closestClusterCenterIdx).addPoint(new Point(x, y));
			}
		}
	}

	private ColorProcessor drawClusters(ColorProcessor cp) {
		for (Cluster c : clusters) {
			cp.setColor(c.color);
			for (Point p : c.population) {
				cp.drawPixel(p.x, p.y);
			}
		}
		return cp;
	}

}
