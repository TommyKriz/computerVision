package assignment02;

import imagingbook.pub.geometry.mappings.linear.LinearMapping;

public class AffineTransform extends LinearMapping {

	/**
	 * <pre>
	 * { a00, a01, a02 }, 
	 * { a10, a11, a12 }, 
	 * { 0.0, 0.0, 1.0 }
	 * </pre>
	 */
	public AffineTransform(double a00, double a01, double a02, double a10,
			double a11, double a12) {
		this.a00 = a00;
		this.a01 = a01;
		this.a02 = a02;
		this.a10 = a10;
		this.a11 = a11;
		this.a12 = a12;
		this.a20 = 0;
		this.a21 = 0;
		this.a22 = 1;
		isInverseFlag = false;
	}
}
