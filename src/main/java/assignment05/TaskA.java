package assignment05;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import imagingbook.pub.regions.RegionContourLabeling;
import imagingbook.pub.regions.RegionLabeling.BinaryRegion;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import assignment04.CPDH_Graphic;

/**
 * Only works for the supplied image shape-montage-2.png
 * 
 * @author Tommy
 *
 */
public class TaskA implements PlugInFilter {

  // TODO: 0.46 manhattan
  private static final double ERROR_THRESHOLD = 0.7;

  private final static Color SHARK_COLOR = Color.CYAN;
  private final static Color WEIRDO_COLOR = Color.GREEN;
  private final static Color GUPPY_COLOR = Color.PINK;
  private final static Color FISH_COLOR = Color.YELLOW;
  private final static Color WORM_COLOR = Color.BLUE;

  private ImagePlus im = null;

  private List<BinaryRegion> fiveReferenceRegions;
  private List<double[][]> referenceNormalizedHistograms;
  private List<Color> referenceColors;

  public int setup(String arg, ImagePlus im) {
    this.im = im;
    return DOES_8G + DOES_8C + NO_CHANGES;
  }

  public void run(ImageProcessor ip) {
    // create the region labeler / contour tracer:
    RegionContourLabeling segmenter = new RegionContourLabeling((ByteProcessor) ip);

    // Retrieve the list of detected regions, no sorting!
    List<BinaryRegion> regions = segmenter.getRegions(true);
    IJ.log("detected regions: " + regions.size());

    if (regions.isEmpty()) {
      IJ.error("no regions detected");
      return;
    }

    ColorProcessor cp = ip.convertToColorProcessor();

    initReferenceRegionsHistogramsAndColors(cp, regions);

    int counter = 0;

    for (BinaryRegion R : regions) {
      int outerContourLength = R.getOuterContour().getLength();

      if (outerContourLength < 50) {
        IJ.log("------Region irrelevant-------");
      } else {
        counter++;
        double[][] h =
            normalizeHistogram(new CPDH_Graphic(cp, R.getOuterContour()).getHistogram(),
                outerContourLength);

        // by default use Color.WHITE
        int colorIdx = 5;
        double errorTmp = ERROR_THRESHOLD;
        for (int i = 0; i < 5; i++) {
          double error = getEuclideanDistance(h, referenceNormalizedHistograms.get(i));
          if (error < errorTmp) {
            errorTmp = error;
            colorIdx = i;
          }
        }
        visualizeRegion(cp, R, referenceColors.get(colorIdx));
      }
    }

    IJ.log(" ----> # of Trials: " + counter);

    visualizeFiveReferenceRegions(cp, fiveReferenceRegions);

    new ImagePlus(im.getTitle() + "-associatedContourPointsWithinPolarCoordinates", cp).show();
  }

  private void initReferenceRegionsHistogramsAndColors(ColorProcessor cp, List<BinaryRegion> regions) {
    fiveReferenceRegions = collectFiveReferenceRegions(regions);
    referenceNormalizedHistograms = initReferenceHistograms(cp, fiveReferenceRegions);
    referenceColors = initReferenceColors();
  }

  private List<Color> initReferenceColors() {
    List<Color> colors = new ArrayList<>();
    colors.add(SHARK_COLOR);
    colors.add(WEIRDO_COLOR);
    colors.add(GUPPY_COLOR);
    colors.add(FISH_COLOR);
    colors.add(WORM_COLOR);
    colors.add(Color.WHITE);
    return colors;
  }

  private List<double[][]> initReferenceHistograms(ColorProcessor cp,
      List<BinaryRegion> alreadySortedFiveReferenceRegions) {
    List<double[][]> referenceHistograms = new ArrayList<>();
    for (BinaryRegion R : alreadySortedFiveReferenceRegions) {
      referenceHistograms
          .add(normalizeHistogram(new CPDH_Graphic(cp, R.getOuterContour()).getHistogram(), R
              .getOuterContour().getLength()));
    }
    return referenceHistograms;
  }

  private void visualizeFiveReferenceRegions(ColorProcessor cp,
      List<BinaryRegion> fiveReferenceRegions) {
    for (int i = 0; i < 5; i++) {
      visualizeRegion(cp, fiveReferenceRegions.get(i), referenceColors.get(i));
    }
  }

  private List<BinaryRegion> collectFiveReferenceRegions(List<BinaryRegion> regions) {
    List<BinaryRegion> fiveReferenceRegions = new ArrayList<>();
    for (BinaryRegion R : regions) {
      // discard irrelevant, too small regions
      if (R.getCenterPoint().getY() < 130 && R.getSize() > 25) {
        fiveReferenceRegions.add(R);
      }
    }
    IJ.log("fiveReferenceRegions.size() " + fiveReferenceRegions.size());
    fiveReferenceRegions.sort(new Comparator<BinaryRegion>() {
      @Override
      public int compare(BinaryRegion b1, BinaryRegion b2) {
        // sort from left to right
        return (int) (b1.getCenterPoint().getX() - b2.getCenterPoint().getX());
      }
    });
    return fiveReferenceRegions;
  }

  private void visualizeRegion(ColorProcessor cp, BinaryRegion R, Color color) {
    cp.setColor(color);
    for (Point p : R) {
      cp.drawPixel(p.x, p.y);
    }

  }

  double[][] normalizeHistogram(int[][] histogram, int totalNumberOfHistogramEntries) {
    int w = histogram.length;
    int h = histogram[0].length;
    double[][] normalizedHistogram = new double[w][h];
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        // normalization occurs here
        normalizedHistogram[x][y] = (histogram[x][y] / (double) totalNumberOfHistogramEntries);
      }
    }
    return normalizedHistogram;
  }

  // aka the L2 Norm
  private double getEuclideanDistance(double[][] h1, double[][] h2) {
    int w = h1.length;
    int h = h1[0].length;
    double sum = 0;
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        sum += Math.sqrt(Math.pow(h1[x][y] - h2[x][y], 2.0));
      }
    }
    return sum;
  }

}
