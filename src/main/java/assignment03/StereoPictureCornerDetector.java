package assignment03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.process.ImageProcessor;
import imagingbook.pub.corners.Corner;
import imagingbook.pub.corners.HarrisCornerDetector;

public class StereoPictureCornerDetector {

  private static final double M_PERCENTAGE = 0.05;

  /**
   * The percentage of the left and right border averaged over the 5 sample images.
   */
  private static final double borderPercentage = 7.2 / 100.0;

  /**
   * The percentage of the bottom border averaged over the 5 sample images.
   */
  private static final double bottomBorderPercentage = 9.32 / 100.0;

  private List<Corner> leftCorners;
  private List<Corner> rightCorners;

  private int width;
  private int height;

  public StereoPictureCornerDetector(ImageProcessor ip) {
    List<Corner> allCornersInPicture = findAllCornersInPicture(ip);
    width = ip.getWidth();
    height = ip.getHeight();
    separateAndSortCorners(allCornersInPicture);
  }


  private void separateAndSortCorners(List<Corner> allCornersInPicture) {
    List<Corner> sortedLeftCorners = separateLeftCorners(allCornersInPicture);
    List<Corner> sortedRightCorners = separateRightCorners(allCornersInPicture);

    Collections.sort(sortedLeftCorners);
    Collections.sort(sortedRightCorners);

    // correct invalid m values
    int maxAllowedM = Math.min(sortedLeftCorners.size(), sortedRightCorners.size());
    IJ.log("maxAllowedM: " + maxAllowedM);

    int M = (int) (maxAllowedM - (maxAllowedM * M_PERCENTAGE));
    IJ.log("M: " + M);

    List<Corner> sortedMLeftCorners = new ArrayList<>();
    List<Corner> sortedMRightCorners = new ArrayList<>();

    for (int i = 0; i < M; i++) {
      sortedMLeftCorners.add(sortedLeftCorners.get(i));
      sortedMRightCorners.add(sortedRightCorners.get(i));
    }

    leftCorners = sortedMLeftCorners;
    rightCorners = sortedMRightCorners;
  }

  private List<Corner> findAllCornersInPicture(ImageProcessor ip) {
    HarrisCornerDetector.Parameters params = new HarrisCornerDetector.Parameters();
    if (!showDialog(params)) {
      return null;
    }
    HarrisCornerDetector cd = new HarrisCornerDetector(ip, params);
    return cd.findCorners();
  }

  private boolean showDialog(HarrisCornerDetector.Parameters params) {
    // display dialog , return false if canceled or on error.
    GenericDialog dlg = new GenericDialog("Harris Corner Detector");
    dlg.addNumericField("Alpha", params.alpha, 3);
    dlg.addNumericField("Threshold", params.tH, 0);
    dlg.addCheckbox("Clean up corners", params.doCleanUp);
    dlg.showDialog();
    if (dlg.wasCanceled())
      return false;
    params.alpha = dlg.getNextNumber();
    params.tH = (int) dlg.getNextNumber();
    params.doCleanUp = dlg.getNextBoolean();
    if (dlg.invalidNumber()) {
      IJ.error("Input Error", "Invalid input number");
      return false;
    }
    return true;
  }

  private List<Corner> separateRightCorners(List<Corner> corners) {
    List<Corner> rightCorners = new ArrayList<>();

    int middle = (width / 2);
    double rightBorderX = width - (width * borderPercentage);
    double bottomBorderY = height - (height * bottomBorderPercentage);

    for (Corner c : corners) {
      if (c.getX() > middle && c.getX() < rightBorderX && c.getY() < bottomBorderY) {
        rightCorners.add(c);
      }
    }
    IJ.log("Num of right corners: " + rightCorners.size());
    return rightCorners;
  }

  private List<Corner> separateLeftCorners(List<Corner> corners) {
    List<Corner> leftCorners = new ArrayList<>();

    int middle = (width / 2);
    double leftBorderX = width * borderPercentage;
    double bottomBorderY = height - (height * bottomBorderPercentage);

    for (Corner c : corners) {
      if (c.getX() < middle && c.getX() > leftBorderX && c.getY() < bottomBorderY) {
        leftCorners.add(c);
      }
    }
    IJ.log("Num of left corners: " + leftCorners.size());
    return leftCorners;
  }

  public List<Corner> getLeftCorners() {
    return leftCorners;
  }

  public List<Corner> getRightCorners() {
    return rightCorners;
  }

}
