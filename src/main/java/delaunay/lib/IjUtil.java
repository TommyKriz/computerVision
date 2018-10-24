package delaunay.lib;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import ij.process.ImageProcessor;

/**
 * This class contains several static utility methods for
 * using the Delaunay triangulation with ImageJ.
 * 
 * @author WB
 * @version 2016/03/08
 */
public abstract class IjUtil {
	
	public static Vertex[] collectPoints(ImageProcessor ip) {
		List<Vertex> vertices = new ArrayList<Vertex>();
		int M = ip.getWidth();
		int N = ip.getHeight();
		for (int v = 0; v < N; v++) {
			for (int u = 0; u < M; u++) {
				int val = ip.getPixel(u, v);
				if (val > 0) {
					vertices.add(new Vertex(u, v));
				}
			}
		}
		return vertices.toArray(new Vertex[0]);
	}
	
	
	public static void draw(Triangle triangle, ImageProcessor ip) {
        draw(triangle.getVertices(), ip);
    }
    
    public static void draw(Vertex[] vertices, ImageProcessor ip) {
        int[] x = new int[vertices.length];
        int[] y = new int[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            x[i] = (int) Math.round(vertices[i].coord(0));
            y[i] = (int) Math.round(vertices[i].coord(1));
        }
        Polygon poly = new Polygon(x, y, vertices.length);
        ip.drawPolygon(poly);
    }
	
	public static void draw(Vertex p, ImageProcessor ip) {
		int x = (int) Math.round(p.coord(0));
		int y = (int) Math.round(p.coord(1));
		ip.drawRect(x - 1, y - 1, 3, 3);
	}
	

}
