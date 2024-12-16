package src.Graph;
import java.util.ArrayList;
import java.util.HashMap;

public class UnorientedGraph<T> extends Graph<T> {
    private HashMap<T,Integer> colors = new HashMap<T,Integer>(); 
    
    public UnorientedGraph() {
    	super();
    	this.colors = new HashMap<T,Integer>();
    }

    /** 
     * Ajout d'une arête
     * @param u sommet 
     * @param v sommet
     */
    public void addEdge(T u, T v) {
        if (!this.hasEdge(u, v)) {
            this.addVertex(u);
            this.addVertex(v);
            this.adjList.get(u).add(v);
            this.adjList.get(v).add(u);
        }
    }
    /**
     * Getter des voisins d'un sommet
     * @param u sommet
     * @return les voisins de u
     */
    public ArrayList<T> getNeighbors(T u) {
        if (!this.adjList.containsKey(u))
            return null;
        return this.adjList.get(u);
    }

    /**
     * Getter de la couleur d'un sommet
     * @param u sommet
     * @return int couleur
     */
    public int getColor(T u) {
        if (!this.colors.containsKey(u)) return -1;
        return this.colors.get(u).intValue();
    }

    /**
     * Algorithme glouton de coloration
     * @return int nombre de couleurs utilisées
     */
    public int color() {
        int maxCol = 0;
        for (T u : this.vertices) {
            boolean[] usedColors = new boolean[maxCol+1]; 
            for (int i = 0; i <= maxCol; i++) {
                usedColors[i] = false;
            }
            for (T v : getNeighbors(u)) {
                if (getColor(v) != -1)
                    usedColors[getColor(v)] = true;
            }
            for (int i = 0; i <= maxCol; i++) {
                if (!usedColors[i]) {
                    this.colors.put(u,i);
                    break;
                }
            }
            if (!this.colors.containsKey(u)) {
                maxCol++;
                this.colors.put(u,maxCol);
            }
        }
        return maxCol+1;
    }
}
