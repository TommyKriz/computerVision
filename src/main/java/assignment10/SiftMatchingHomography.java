package assignment10;

import homography.HomographyEstimator;
import imagingbook.pub.geometry.mappings.linear.ProjectiveMapping;
import imagingbook.pub.sift.SiftMatch;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SiftMatchingHomography {

	public static ProjectiveMapping calcProjection(List<SiftMatch> matches) {
		ArrayList<Point2D> featureCoordinatesInImage1 = new ArrayList<>();
		ArrayList<Point2D> featureCoordinatesInImage2 = new ArrayList<>();

		for (SiftMatch m : matches) {
			featureCoordinatesInImage1.add(new Point2D.Double(m
					.getDescriptor1().getX(), m.getDescriptor1().getY()));
			featureCoordinatesInImage2.add(new Point2D.Double(m
					.getDescriptor2().getX(), m.getDescriptor2().getY()));
		}

		return HomographyEstimator.getHomographyMapping(
				featureCoordinatesInImage1.toArray(new Point2D[0]),
				featureCoordinatesInImage2.toArray(new Point2D[0]));
	}
}
