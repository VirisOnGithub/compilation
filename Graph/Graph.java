package Graph;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Graph<T> {
    protected ArrayList<T> vertices;
    protected HashMap<T,ArrayList<T>> adjList;
   
    /**
     * Constructeur
     */
    public Graph() {
        this.vertices = new ArrayList<T>();
        this.adjList = new HashMap<T,ArrayList<T>>();
    }

    /**
     * Teste si u est un sommet
     * @param u un sommet
     * @return boolean
     */
    public boolean hasVertex(T u) {
        return (this.adjList.containsKey(u));
    }

    /**
     * Teste si uv est une arête/un arc
     * @param u sommet
     * @param v sommet 
     * @return boolean uv est une arête/arc
     */
    public boolean hasEdge(T u, T v) {
        return (this.adjList.containsKey(u) && this.adjList.get(u).contains(v));
    }
    
    /**
     * Ajout d'un sommet 
     * @param u sommet
     */
    public void addVertex(T u) {
        if (this.vertices.contains(u)) return;
        this.vertices.add(u);
        this.adjList.put(u, new ArrayList<T>());
    }   
    
    /**
     * Ajout d'une arête/arc
     * @param u sommet
     * @param v sommet
     */
    public abstract void addEdge(T u, T v);


    
    /** 
     * Conversion pour l'affichage
     * @return String
     */
    public String toString() {
        String s = "";
        for (T u : this.vertices) {
            s += u.toString()+": ";
            for (T v : this.adjList.get(u)) {
                s += v.toString()+", ";
            }
            s += "\n";
        }
        return s;
    }

}
