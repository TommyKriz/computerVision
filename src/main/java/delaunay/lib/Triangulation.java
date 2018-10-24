package delaunay.lib;

//import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;


/**
 * A 2D Delaunay Triangulation (DT) with incremental site insertion.
 * This is not the fastest way to build a DT, but it's a reasonable way to build
 * a DT incrementally and it makes a nice interactive display. There are several
 * O(n log n) methods, but they require that the sites are all known initially.
 * A Triangulation is a Set of Triangles. A Triangulation is unmodifiable as a
 * set; the only way to change it is to add sites (via insert()).
 *
 * @author Paul Chew (2005-2007, see COPYRIGHT.TXT)      
 * @version 2016/03/08 W. Burger (WB): several additions/modifications, 
 * changed to implement no set any more.
 */
public class Triangulation {

	private Triangle mostRecent = null; 		// most recently "active" triangle
	private final Graph<Triangle> triangles; 	// holds triangles for navigation
	private final Triangle initialTriangle; 	// added by WB

	/**
	 * Creates an empty Delaunay triangulation that only
	 * contains the initial (surrounding) triangle.
	 * All sites must fall within the initial triangle.
	 * @param initialTriangle the initial triangle
	 */
	public Triangulation(Triangle initialTriangle) {
		this.initialTriangle = initialTriangle;
		this.triangles = new Graph<Triangle>();
		this.triangles.add(initialTriangle);
		this.mostRecent = initialTriangle;
	}
	
	
	/**
	 * Creates an empty Delaunay triangulation for the
	 * given working rectangle. All points inserted later
	 * must be inside the working rectangle.
	 * 
	 * @param width
	 * @param height
	 */
	public Triangulation(int width, int height) {
		this(makeInitialTriangle(width, height));
	}
	
	/**
	 * Creates a Delaunay triangulation from the supplied
	 * 2D points (sites of type {@link Vertex}).
	 * 
	 * @param sites array of 2D points
	 * @param width width of the working rectangle
	 * @param height height of the working rectangle
	 */
	public Triangulation(Vertex[] sites, int width, int height) {
		this(makeInitialTriangle(width, height));
		if (sites != null) {
			for (Vertex s : sites) {
				insert(s);
			}
		}
	}
	

	/**
	 * @author WB
	 * 
	 * @return an initial triangle enclosing the given rectangle.
	 */
	private static Triangle makeInitialTriangle(int width, int height) {
		Vertex A = new Vertex(-3 * width, -2 * height);
		Vertex B = new Vertex( 4 * width, -2 * height);
		Vertex C = new Vertex(width/2, 4 * height);
		return new Triangle(A, B, C);
	}

	// -----------------------------------------------------------
	
	/**
	 * @author WB
	 * @return the initial (surrounding) triangle of this triangulation.
	 */
	public Triangle getInitialTriangle() {
		return initialTriangle;
	}

	/**
	 * True iff triangle is a member of this triangulation. This method isn't
	 * required by AbstractSet, but it improves efficiency.
	 * @param triangle the object to check for membership
	 */
	private boolean contains(Triangle triangle) {
		return triangles.getNodeSet().contains(triangle);
	}

	/**
	 * Report neighbor opposite the given vertex of triangle.
	 * @param site a vertex of triangle
	 * @param triangle we want the neighbor of this triangle
	 * @return the neighbor opposite site in triangle; null if none
	 * @throws IllegalArgumentException if site is not in this triangle
	 */
	private Triangle neighborOpposite(Vertex site, Triangle triangle) {
		if (!triangle.contains(site))
			throw new IllegalArgumentException("Bad vertex; not in triangle");
		for (Triangle neighbor : triangles.getNeighbors(triangle)) {
			if (!neighbor.contains(site))
				return neighbor;
		}
		return null;
	}

	/**
	 * Return the set of triangles adjacent to triangle.
	 * @param triangle the triangle to check
	 * @return the neighbors of triangle
	 */
	private Set<Triangle> neighbors(Triangle triangle) {
		return triangles.getNeighbors(triangle);
	}

	/**
	 * Report triangles surrounding site in order (cw or ccw).
	 * @param site we want the surrounding triangles for this site
	 * @param triangle a "starting" triangle that has site as a vertex
	 * @return all triangles surrounding site in order (cw or ccw)
	 * @throws IllegalArgumentException if site is not in triangle
	 */
	public List<Triangle> getSurroundingTriangles(Vertex site, Triangle triangle) {
		if (!triangle.contains(site))
			throw new IllegalArgumentException("Site not in triangle");
		List<Triangle> list = new ArrayList<Triangle>();
		Triangle start = triangle;
		Vertex guide = triangle.getVertexButNot(site); // Affects cw or ccw
		while (true) {
			list.add(triangle);
			Triangle previous = triangle;
			triangle = this.neighborOpposite(guide, triangle); // Next triangle
			guide = previous.getVertexButNot(site, guide); // Update guide
			if (triangle == start)
				break;
		}
		return list;
	}

//	public List<Triangle> getSurroundingTriangles(Point2D pnt, Triangle triangle) {
//		return getSurroundingTriangles(new Pnt(pnt), triangle);
//	}

	/**
	 * Locate the triangle with point inside it or on its boundary.
	 * @param site the point to locate
	 * @return the triangle that holds point; null if no such triangle
	 */
	private Triangle locate(Vertex site) {
		Triangle triangle = mostRecent;
		if (!this.contains(triangle))
			triangle = null;

		// Try a directed walk (this works fine in 2D, but can fail in 3D)
		Set<Triangle> visited = new HashSet<Triangle>();
		while (triangle != null) {
			if (visited.contains(triangle)) { // This should never happen
				System.out.println("Warning: Caught in a locate loop");
				break;
			}
			visited.add(triangle);
			// Corner opposite point
			Vertex corner = site.isOutside(triangle.toArray(new Vertex[0]));
			if (corner == null)
				return triangle;
			triangle = this.neighborOpposite(corner, triangle);
		}
		// No luck; try brute force
		System.out.println("Warning: Checking all triangles for " + site);
		for (Triangle tri : triangles.getNodeSet()) {
			if (site.isOutside(tri.toArray(new Vertex[0])) == null)
				return tri;
		}
		// No such triangle
		System.out.println("Warning: No triangle holds " + site);
		return null;
	}

	/**
	 * Add a new site into the triangulation.
	 * Nothing happens if the site matches an
	 * existing DT vertex. 
	 * Uses straightforward scheme rather than best asymptotic time.
	 * @param site the new 2D point
	 * @throws IllegalArgumentException if site does not lie in any triangle
	 */
	public void insert(Vertex site) {
		// Locate containing triangle
		Triangle triangle = locate(site);
		// Give up if no containing triangle or if site is already in DT
		if (triangle == null)
			throw new IllegalArgumentException("No containing triangle");
		if (triangle.contains(site))
			return;
		// Determine the cavity and update the triangulation
		Set<Triangle> cavity = getCavity(site, triangle);
		mostRecent = update(site, cavity);
	}
	

	/**
	 * Determine the cavity caused by site.
	 * @param site the site causing the cavity
	 * @param triangle the triangle containing site
	 * @return set of all triangles that have site in their circumcircle
	 */
	private Set<Triangle> getCavity(Vertex site, Triangle triangle) {
		Set<Triangle> encroached = new HashSet<Triangle>();
		Queue<Triangle> toBeChecked = new ArrayDeque<Triangle>(10);
		Set<Triangle> marked = new HashSet<Triangle>();
		toBeChecked.add(triangle);
		marked.add(triangle);
		while (!toBeChecked.isEmpty()) {
			triangle = toBeChecked.remove();
			if (site.vsCircumcircle(triangle.toArray(new Vertex[0])) == 1)
				continue; // Site outside triangle => triangle not in cavity
			encroached.add(triangle);
			// Check the neighbors
			for (Triangle neighbor : triangles.getNeighbors(triangle)) {
				if (marked.contains(neighbor))
					continue;
				marked.add(neighbor);
				toBeChecked.add(neighbor);
			}
		}
		return encroached;
	}

	/**
	 * Update the triangulation by removing the cavity triangles and then
	 * filling the cavity with new triangles.
	 * @param site the site that created the cavity
	 * @param cavity the triangles with site in their circumcircle
	 * @return one of the new triangles
	 */
	private Triangle update(Vertex site, Set<Triangle> cavity) {
		Set<List<Vertex>> boundary = new HashSet<List<Vertex>>();
		Set<Triangle> theTriangles = new HashSet<Triangle>();

		// Find boundary facets and adjacent triangles
		for (Triangle triangle : cavity) {
			theTriangles.addAll(neighbors(triangle));
			for (Vertex vertex : triangle) {
				List<Vertex> facet = triangle.facetOpposite(vertex);
				if (boundary.contains(facet))
					boundary.remove(facet);
				else
					boundary.add(facet);
			}
		}
		theTriangles.removeAll(cavity); // Adj triangles only

		// Remove the cavity triangles from the triangulation
		for (Triangle triangle : cavity)
			triangles.remove(triangle);

		// Build each new triangle and add it to the triangulation
		Set<Triangle> newTriangles = new HashSet<Triangle>();
		for (List<Vertex> vertices : boundary) {
			vertices.add(site);
			Triangle tri = new Triangle(vertices);
			triangles.add(tri);
			newTriangles.add(tri);
		}

		// Update the graph links for each new triangle
		theTriangles.addAll(newTriangles); // Adj triangle + new triangles
		for (Triangle triangle : newTriangles)
			for (Triangle other : theTriangles)
				if (triangle.isNeighbor(other))
					triangles.add(triangle, other);

		// Return one of the new triangles
		return newTriangles.iterator().next();
	}
	
	/**
	 * WB: added
	 * @return an array of the triangles in this triangulation, not including 
	 * the initial (surrounding) triangle.
	 */
	public Triangle[] getDelaunayTriangles() {
		ArrayList<Triangle> delTriangles = new ArrayList<Triangle>();
		for (Triangle trg : triangles.getNodeSet()) {
			if (!trg.sharesVertexWith(initialTriangle)) {
				delTriangles.add(trg);
			}
		}
		return delTriangles.toArray(new Triangle[0]);
	}
	

	/**
	 * Main program; used for testing.
	 */
//	public static void main(String[] args) {
//		Triangle tri = new Triangle(new Pnt(-10, 10), new Pnt(10, 10), new Pnt(
//				0, -10));
//		System.out.println("Triangle created: " + tri);
//		Triangulation dt = new Triangulation(tri);
//		System.out.println("DelaunayTriangulation created: " + dt);
//		dt.insert(new Pnt(0, 0));
//		dt.insert(new Pnt(1, 0));
//		dt.insert(new Pnt(0, 1));
//		System.out.println("After adding 3 points, we have a " + dt);
//		Triangle.moreInfo = true;
//		System.out.println("Triangles: " + dt.triangles.getNodeSet());
//	}

}