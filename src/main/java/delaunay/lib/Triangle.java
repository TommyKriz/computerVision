package delaunay.lib;

//import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A Triangle is an immutable Set of exactly three Pnts.
 * All Set operations are available. Individual vertices can be accessed via
 * iterator() and also via triangle.get(index).
 * Note that, even if two triangles have the same vertex set, they are
 * *different* triangles. Methods equals() and hashCode() are consistent with
 * this rule.
 *
 * @author Paul Chew (2007, see COPYRIGHT.TXT)
 * @version 2015/03/07 W. Burger (WB): 
 * Replaced general simplices with geometric triangle. 
 * Changed to extend ArrayList (instead of proprietary ArraySet)
 */

public class Triangle extends ArrayList<Vertex> {
	
	private static final long serialVersionUID = 1L;
	private static int idGenerator = 0;     // Used to create id numbers
    private final int idNumber;             // The id number
    private Vertex circumcenter = null;        // The triangle's circumcenter


    /**
     * Constructor.
     * WB: reduced to 3 points
     * @param A
     * @param B
     * @param C
     */
    public Triangle (Vertex A, Vertex B, Vertex C) {
        this(Arrays.asList(new Vertex[] {A, B, C}));
    }
    
    /**
     * @param vertices a Collection holding the Simplex vertices
     * @throws IllegalArgumentException if there are not three distinct vertices
     */
    protected Triangle (Collection<? extends Vertex> vertices) {
        super(vertices);
        if (this.size() != 3)
            throw new IllegalArgumentException("Triangle must have 3 vertices");
        idNumber = idGenerator++;
    }

    @Override
    public String toString () {
        return "Triangle" + idNumber + super.toString();
    }

    /**
     * Get arbitrary vertex of this triangle, but not any of the bad vertices.
     * @param badVertices one or more bad vertices
     * @return a vertex of this triangle, but not one of the bad vertices
     * @throws NoSuchElementException if no vertex found
     */
    public Vertex getVertexButNot (Vertex... badVertices) {
        Collection<Vertex> bad = Arrays.asList(badVertices);
        for (Vertex v: this) {
        	if (!bad.contains(v)) return v;
        }
        throw new NoSuchElementException("No vertex found");
    }
    
    /**
     * @author WB
     * @return the vertices of this triangle.
     */
    public Vertex[] getVertices() {
    	return this.toArray(new Vertex[0]);

    }

    /**
     * True iff triangles are neighbors. Two triangles are neighbors if they
     * share a facet.
     * @param triangle the other Triangle
     * @return true iff this Triangle is a neighbor of triangle
     */
    public boolean isNeighbor (Triangle triangle) {
        int count = 0;
        for (Vertex vertex : this) {
            if (!triangle.contains(vertex)) count++;
        }
        return count == 1;
    }
    
    /**
     * Added to determine triangles that connect to the initial (outer) triangle.
     * @author WB
     * @param triangle2
     * @return true iff this shares any vertex with the other triangle.
     */
    public boolean sharesVertexWith(Triangle triangle2) {
        int count = 0;
        for (Vertex vertex : this) {
            if (triangle2.contains(vertex)) count++;
        }
        return count > 0;
    }

    /**
     * Report the facet opposite vertex.
     * @param vertex a vertex of this Triangle
     * @return the facet opposite vertex
     * @throws IllegalArgumentException if the vertex is not in triangle
     */
//    public ArraySet<Pnt> facetOpposite (Pnt vertex) {
//        ArraySet<Pnt> facet = new ArraySet<Pnt>(this);
//        if (!facet.remove(vertex))
//            throw new IllegalArgumentException("Vertex not in triangle");
//        return facet;
//    }
    
    /**
     * @author WB
     * @param vertex
     * @return
     */
    public List<Vertex> facetOpposite (Vertex vertex) {
        List<Vertex> facet = new ArrayList<Vertex>(this);
        if (!facet.remove(vertex))
            throw new IllegalArgumentException("Vertex not in triangle");
        return facet;
    }

    /**
     * @return the triangle's circumcenter
     */
    public Vertex getCircumcenter () {
        if (circumcenter == null) {
            circumcenter = Vertex.circumcenter(this.toArray(new Vertex[0]));
        }
        return circumcenter;
    }
    

    /**
     * Ensure that a Triangle is immutable 
     */
    @Override
    public boolean add (Vertex vertex) {
        throw new UnsupportedOperationException();
    }

    // wilbur: removed, not needed
//    @Override
//    public Iterator<Pnt> iterator () {
//        return new Iterator<Pnt>() {
//            private Iterator<Pnt> it = Triangle.super.iterator();
//            public boolean hasNext() {return it.hasNext();}
//            public Pnt next() {return it.next();}
//            public void remove() {throw new UnsupportedOperationException();}
//        };
//    }

    
    /* The following two methods ensure that all triangles are different. */

    @Override
    public int hashCode () {
        return (int)(idNumber^(idNumber>>>32));
    }

    @Override
    public boolean equals (Object o) {
        return (this == o);
    }
    

}