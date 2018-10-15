package assignment06;

import java.util.HashMap;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.PlugInFilter;
import ij.process.Blitter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import imagingbook.lib.math.Matrix;

/**
 * This plugin calculates Law's Texture Energy Maps.
 * 
 * @author W. Burger
 *
 */
public class Laws_Texture_Energy_Util {

	private static final float[] HL = { 1, 4, 6, 4, 1 };
	private static final float[] HE = { -1, -2, 0, 2, 1 };
	private static final float[] HS = { -1, 0, 2, 0, -1 };
	private static final float[] HR = { 1, -4, 6, -4, 1 };

	private static double PREPROCESSING_BLUR_SIGMA = 4.0;
	private static double ENERGY_MAP_SMOOTH_SIGMA = 3.0;
	private static boolean NORMALIZE_ENERGY_MAPS = true;

	public ImageStack calc(ImageProcessor ip) {
		if (!getUserInput()) {
			return null;
		}

		FloatProcessor ipPrep = ip.convertToFloatProcessor();
		FloatProcessor ipAvg = ip.convertToFloatProcessor();

		// calculate the local average (ipAvg) using a Gaussian blur:
		GaussianBlur gb = new GaussianBlur();
		gb.blurGaussian(ipAvg, PREPROCESSING_BLUR_SIGMA);

		// subtract average from original image (ipPrep <- ipPrep - ipAvg)
		ipPrep.copyBits(ipAvg, 0, 0, Blitter.SUBTRACT);

		// create all 16 energy maps
		// FloatProcessor I_LL = makeTextureEnergy (ipPrep, HL, HL); // not used
		FloatProcessor I_LE = makeTextureEnergy(ipPrep, HL, HE);
		FloatProcessor I_LS = makeTextureEnergy(ipPrep, HL, HS);
		FloatProcessor I_LR = makeTextureEnergy(ipPrep, HL, HR);

		FloatProcessor I_EL = makeTextureEnergy(ipPrep, HE, HL);
		FloatProcessor I_EE = makeTextureEnergy(ipPrep, HE, HE);
		FloatProcessor I_ES = makeTextureEnergy(ipPrep, HE, HS);
		FloatProcessor I_ER = makeTextureEnergy(ipPrep, HE, HR);

		FloatProcessor I_SL = makeTextureEnergy(ipPrep, HS, HL);
		FloatProcessor I_SE = makeTextureEnergy(ipPrep, HS, HE);
		FloatProcessor I_SS = makeTextureEnergy(ipPrep, HS, HS);
		FloatProcessor I_SR = makeTextureEnergy(ipPrep, HS, HR);

		FloatProcessor I_RL = makeTextureEnergy(ipPrep, HE, HL);
		FloatProcessor I_RE = makeTextureEnergy(ipPrep, HE, HE);
		FloatProcessor I_RS = makeTextureEnergy(ipPrep, HE, HS);
		FloatProcessor I_RR = makeTextureEnergy(ipPrep, HE, HR);

		I_EL.copyBits(I_LE, 0, 0, Blitter.ADD); // I_EL <- I_EL + I_LE
		I_SL.copyBits(I_LS, 0, 0, Blitter.ADD);
		I_RL.copyBits(I_LR, 0, 0, Blitter.ADD);
		I_SE.copyBits(I_ES, 0, 0, Blitter.ADD);
		I_RE.copyBits(I_ER, 0, 0, Blitter.ADD);
		I_RS.copyBits(I_SR, 0, 0, Blitter.ADD);

		HashMap<FloatProcessor, String> eMaps = new HashMap<FloatProcessor, String>();
		eMaps.put(I_EE, "I_EE");
		eMaps.put(I_SS, "I_SS");
		eMaps.put(I_RR, "I_RR");
		eMaps.put(I_EL, "I_EL");
		eMaps.put(I_SL, "I_SL");
		eMaps.put(I_RL, "I_RL");
		eMaps.put(I_SE, "I_SE");
		eMaps.put(I_RE, "I_RE");
		eMaps.put(I_RS, "I_RS");

		if (NORMALIZE_ENERGY_MAPS) {
			for (FloatProcessor p : eMaps.keySet()) {
				normalize(p);
			}
		}

		ImageStack stack = new ImageStack(ip.getWidth(), ip.getHeight());
		for (FloatProcessor p : eMaps.keySet()) {
			stack.addSlice(eMaps.get(p), p);
		}
		return stack;
	}

	// ----------------------------------------------------------------------

	private FloatProcessor convolve1h(FloatProcessor p, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(p, h, 1, h.length);
		return p;
	}

	private FloatProcessor convolve1v(FloatProcessor p, float[] h) {
		Convolver conv = new Convolver();
		conv.setNormalize(false);
		conv.convolve(p, h, h.length, 1);
		return p;
	}

	private FloatProcessor makeTextureEnergy(FloatProcessor orig,
			float[] h_hor, float[] h_ver) {
		FloatProcessor p = (FloatProcessor) orig.duplicate();
		convolve1h(p, h_hor);
		convolve1v(p, h_ver);
		// take absolute value
		absoluteValue(p);
		GaussianBlur gb = new GaussianBlur();
		gb.blurGaussian(p, ENERGY_MAP_SMOOTH_SIGMA);
		return p;
	}

	private void absoluteValue(FloatProcessor p) {
		float[] pixels = (float[]) p.getPixels();
		for (int i = 0; i < pixels.length; i++) {
			float pix = pixels[i];
			if (pix < 0f)
				pixels[i] = -pix;
		}
	}

	private void normalize(FloatProcessor fp) {
		float[] pixels = (float[]) fp.getPixels();
		float max = Matrix.max(pixels);
		fp.multiply(1.0 / max);
	}

	// -------------------------------------------

	private boolean getUserInput() {
		GenericDialog gd = new GenericDialog("Texture Energy Parameters");

		gd.addNumericField("Preprocessing blur sigma",
				PREPROCESSING_BLUR_SIGMA, 2);
		gd.addNumericField("Energy map smooth sigma", ENERGY_MAP_SMOOTH_SIGMA,
				2);
		gd.addCheckbox("Normalize energy maps", NORMALIZE_ENERGY_MAPS);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		PREPROCESSING_BLUR_SIGMA = gd.getNextNumber();
		ENERGY_MAP_SMOOTH_SIGMA = gd.getNextNumber();
		NORMALIZE_ENERGY_MAPS = gd.getNextBoolean();
		return true;
	}

}
