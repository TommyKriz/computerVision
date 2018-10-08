package assignment09;

import ij.IJ;
import ij.process.FloatProcessor;
import imagingbook.pub.sift.SiftDescriptor;
import imagingbook.pub.sift.SiftDetector;
import imagingbook.pub.sift.SiftMatch;
import imagingbook.pub.sift.SiftMatcher;

import java.util.List;

public class SiftMatching {

	private static final int NUMBER_OF_SIFT_MATCHES = 20;

	private static final float RHO_MAX = 0.3f;

	public static List<SiftMatch> calcMatches(FloatProcessor image1,
			FloatProcessor image2) {
		SiftDetector.Parameters params = new SiftDetector.Parameters();
		params.rho_Max = RHO_MAX;

		SiftDetector sdA = new SiftDetector(image1, params);
		SiftDetector sdB = new SiftDetector(image2, params);

		List<SiftDescriptor> fsA = sdA.getSiftFeatures();
		List<SiftDescriptor> fsB = sdB.getSiftFeatures();

		IJ.log("SIFT features found in image 1: " + fsA.size());
		IJ.log("SIFT features found in image 2: " + fsB.size());

		IJ.log("matching ...");
		// create a matcher on the first set of features:
		SiftMatcher sm = new SiftMatcher(fsA);
		// match the second set of features:
		List<SiftMatch> matches = sm.matchDescriptors(fsB);

		return matches.subList(0, NUMBER_OF_SIFT_MATCHES);
	}

}
