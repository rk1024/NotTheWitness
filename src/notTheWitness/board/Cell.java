package notTheWitness.board;

import java.util.*;

public class Cell extends Node {
  private HashSet<Node> nodes = new HashSet<Node>();
  private HashSet<Edge> edges = new HashSet<Edge>();
  
  public Cell(int x, int y) {
    super(x, y);
    // TODO Auto-generated constructor stub
  }
  
  public HashSet<Node> getNodes() { return nodes; }
  public HashSet<Edge> getEdges() { return edges; }
}
