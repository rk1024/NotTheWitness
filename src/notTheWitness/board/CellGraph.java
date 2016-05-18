package notTheWitness.board;

import java.util.*;

public class CellGraph {
  private HashSet<Cell> cells = new HashSet<Cell>();
  private NodeGraph borderGraph;
  
  public CellGraph(NodeGraph borderGraph) {
    this.borderGraph = borderGraph;
    
    HashSet<Node> pool = new HashSet<Node>();
    for (Node node : borderGraph.getNodes()) pool.add(node);
    
    ArrayDeque<Node> queue = new ArrayDeque<Node>();
    queue.addLast(borderGraph.getNodes().iterator().next());
    
    Node node;
    while ((node = queue.pollFirst()) != null) {
      for (Edge edge : borderGraph.getEdges(node)) {
        Node other = edge.other(node);
        
        if (pool.contains(other))
          queue.addLast(other);
      }
    }
  }
  
  public Iterable<Cell> getCells() { return cells; }
  public Iterable<Node> getNodes() { return borderGraph.getNodes(); }
  public Iterable<Edge> getEdges() { return borderGraph.getEdges(); }
  public Iterable<Edge> getEdges(Node connectedTo) { return borderGraph.getEdges(connectedTo); }
  
  public HashSet<Cell> getCells(Node[] bordering) {
    HashSet<Cell> set = new HashSet<Cell>();
    
    cellLoop: for (Cell cell : cells) {
      for (Node node : bordering) {
        if (!cell.getNodes().contains(node))
          continue cellLoop;
      }
      
      set.add(cell);
    }
    
    return set;
  }
}
