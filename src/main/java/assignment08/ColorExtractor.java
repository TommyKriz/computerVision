package assignment08;

import ij.process.ImageProcessor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ColorExtractor {

	public static List<int[]> extractColors(ImageProcessor ip, Point[] points) {
		int[] rgb = new int[3];
		List<int[]> colors = new ArrayList<>();
		for (Point p : points) {
			ip.getPixel(p.x, p.y, rgb);
			colors.add(rgb.clone());
		}
		return colors;
	}

	public static List<int[]> extractColors(ImageProcessor ip) {
		int[] rgb = new int[3];
		List<int[]> colors = new ArrayList<>();

		for (int x = 0; x < ip.getWidth(); x++) {
			for (int y = 0; y < ip.getHeight(); y++) {
				ip.getPixel(x, y, rgb);
				colors.add(rgb.clone());
			}
		}

		return colors;
	}

	// public static List<int[]> extractColorsCheckIfAlreadyContained(
	// ImageProcessor ip, Point[] points) {
	// int[] rgb = new int[3];
	// List<int[]> colors = new ArrayList<>();
	// for (Point p : points) {
	// ip.getPixel(p.x, p.y, rgb);
	// if (!containsColor(colors, rgb)) {
	// colors.add(rgb.clone());
	// }
	// }
	// return colors;
	// }
	//
	// public static List<int[]> extractColorsCheckIfAlreadyContained(
	// ImageProcessor ip) {
	// int[] rgb = new int[3];
	// List<int[]> colors = new ArrayList<>();
	// for (int x = 0; x < ip.getWidth(); x++) {
	// for (int y = 0; y < ip.getHeight(); y++) {
	// ip.getPixel(x, y, rgb);
	// if (!containsColor(colors, rgb)) {
	// colors.add(rgb.clone());
	// }
	// }
	// }
	// return colors;
	// }
	//
	// private static boolean containsColor(List<int[]> colors, int[] color) {
	// for (int[] c : colors) {
	// if (c[0] == color[0] && c[1] == color[1] && c[2] == color[2]) {
	// return true;
	// }
	// }
	// return false;
	// }

}