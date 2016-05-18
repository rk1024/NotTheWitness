package notTheWitness.board;

import java.util.*;

public class CellGraph {
  private HashSet<Cell> cells = new HashSet<Cell>();
  private NodeGraph borderGraph;
  
  public CellGraph(NodeGraph borderGraph) {
    this.borderGraph = borderGraph;
  }
  
  public Iterable<Cell> getCells() { return cells; }
  public Iterable<Node> getNodes() { return borderGraph.getNodes(); }
  public Iterable<Edge> getEdges() { return borderGraph.getEdges(); }
  public Iterable<Edge> getEdges(Node connectedTo) { return borderGraph.getEdges(connectedTo); }
}
