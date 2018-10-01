package main;

import ij.ImageJ;

/**
 * Appropriated from <a
 * href="https://github.com/imagej/example-legacy-plugin">https
 * ://github.com/imagej/example-legacy-plugin</a>.
 * 
 * @author Tommy
 *
 */
public class StartImageJ {

	public static void main(String[] args) {

		/**
		 * Set the plugins.dir property to make the plugin appear in the Plugins
		 * dropdown menu.
		 */
		Class<?> clazz = StartImageJ.class;
		String url = clazz.getResource(
				"/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring("file:".length(), url.length()
				- clazz.getName().length() - ".class".length());

		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

	}
}
