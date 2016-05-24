package notTheWitness.board;

import java.util.*;

public class Cell {
  private ArrayList<Node> nodeList;
  private HashSet<Node> nodes = new HashSet<Node>();
  private HashSet<Edge> edges = new HashSet<Edge>();
  private int x, y;
  
  public Cell(NodeGraph sourceGraph, Node[] nodes) {
    nodeList = new ArrayList<Node>();
    for (Node node : nodes) nodeList.add(node);
    
    init(sourceGraph);
  }
  
  public Cell(NodeGraph sourceGraph, ArrayList<Node> nodes) {
    nodeList = nodes;
    
    init(sourceGraph);
  }
  
  private void init(NodeGraph sourceGraph) {
    for (int i = 0; i < nodeList.size(); i++) {
      Node node = nodeList.get(i);
      nodes.add(node);
      edges.add(sourceGraph.getEdge(node, nodeList.get((i + 1) % nodeList.size())));
    }
    
    if (nodeList.size() > 0) {
      double area = 0, cx = 0, cy = 0;
      
      for (int i = 0; i < nodeCount(); i++) {
        int iNext = (i + 1) % nodeCount();
        
        double cross = nodeList.get(i).getX() * nodeList.get(iNext).getY()
            - nodeList.get(iNext).getX() * nodeList.get(i).getY();
        
        cx += cross * (nodeList.get(i).getX() + nodeList.get(iNext).getX());
        cy += cross * (nodeList.get(i).getY() + nodeList.get(iNext).getY());
        area += cross;
      }
      
      if (area == 0) {
        HashSet<Long> posSet = new HashSet<Long>();
        cx = cy = area = 0;
        
        for (Node node : nodeList) {
          if (posSet.add((long)node.getX() << 32 + (long)node.getY())) {
            cx += node.getX();
            cy += node.getY();
            ++area;
          }
        }
      }
      else {
        area *= 3;
      }
      
      cx /= area;
      cy /= area;
      
      x = (int)Math.round(cx);
      y = (int)Math.round(cy);
    }
  }
  
  public int getX() { return x; }
  public int getY() { return y; }
  
  public Iterable<Node> getNodes() { return nodeList; }
  public Iterable<Edge> getEdges() { return edges; }
  
  public int nodeCount() { return nodes.size(); }
  public int edgeCount() { return edges.size(); } // Should be equal to nodeCount, but semantics...
  
  public boolean contains(Node node) { return nodes.contains(node); }
  public boolean contains(Edge edge) { return edges.contains(edge); }
  
  public int indexOf(Node node) { return nodeList.indexOf(node); }
  
  public boolean equals(Cell other) {
    if (this.nodeList.size() != other.nodeList.size()) return false;
    
    for (int i = 0; i < this.nodeList.size(); i++) {
      if (this.nodeList.get(i) != other.nodeList.get(i)) return false;
    }
    
    return true;
  }
}
