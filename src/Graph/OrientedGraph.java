package src.Graph;
import java.util.ArrayList;
import java.util.Set;

public class OrientedGraph<T> extends Graph<T> {
    
    /** 
     * Ajout d'un arc
     * @param u sommet
     * @param v sommet
     */
    public void addEdge(T u, T v) {
        this.addVertex(u);
        this.addVertex(v);
        this.adjList.get(u).add(v);
    }

    /**
     * Getter des voisins sortant d'un sommet
     * @param u sommet
     * @return les voisins sortant de u
     */
    public ArrayList<T> getOutNeighbors(T u) {
        if (!this.adjList.containsKey(u))
            return null;
        return this.adjList.get(u);
    }

    /**
     * Getter des voisins entrant d'un sommet
     * @param u sommet
     * @return les voisins entrant de u
     */
    public ArrayList<T> getInNeighbors(T u) {
        if (!this.adjList.containsKey(u))
            return null;
        ArrayList<T> result = new ArrayList<T>();
        for (T v : this.vertices) {
            if (this.hasEdge(v, u)) result.add(v);
        }
        return result;
    }

    /**
     * Getter des sommets du graphe
     * @return Set<T> ensemble des sommets
     */
    public Set<T> getVertices() {
        return this.adjList.keySet();
    }
}
