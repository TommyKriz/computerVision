package assignment06;

import imagingbook.lib.color.RandomColorGenerator;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Cluster {

	private static RandomColorGenerator rcg = new RandomColorGenerator();

	Color color;

	float[] center;

	List<Point> population = new ArrayList<>();

	public Cluster(float[] center) {
		color = rcg.nextColor();
		this.center = center;
	}

	public void resetPopulation() {
		population.clear();
	}

	public void addPoint(Point p) {
		population.add(p);
	}

	public double calcIntraClusterScatter(float[][][] featureVector) {
		double sum = 0.0;
		for (Point p : population) {
			sum += distanceFromCenter(featureVector[p.x][p.y]);
		}
		return sum;
	}

	public void recalculateCenterPoint(float[][][] featureVector) {
		center = calcCenterPoint(featureVector);
	}

	private float[] calcCenterPoint(float[][][] featureVector) {
		float[] centerPoint = new float[9];
		for (Point p : population) {
			for (int i = 0; i < 9; i++) {
				centerPoint[i] += featureVector[p.x][p.y][i];
			}

		}
		for (int i = 0; i < 9; i++) {
			centerPoint[i] = centerPoint[i] / population.size();
		}
		return centerPoint;
	}

	public double distanceFromCenter(float[] f) {
		return squaredDistance(f, center);
	}

	private double squaredDistance(float[] p1, float[] p2) {
		double sum = 0.0;
		for (int m = 0; m < 9; m++) {
			sum += Math.pow(p1[m] - p2[m], 2.0);
		}
		return sum;
	}
}
