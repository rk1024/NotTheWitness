package notTheWitness.board;

import java.util.*;

public class Cell {
  private ArrayList<Node> nodeList;
  private HashSet<Node> nodes = new HashSet<Node>();
  private HashSet<Edge> edges = new HashSet<Edge>();
  private int x, y;
  
  public Cell(NodeGraph sourceGraph, ArrayList<Node> nodeList) {
    this.nodeList = nodeList;
    for (int i = 0; i < nodeList.size(); i++) {
      Node node = nodeList.get(i);
      nodes.add(node);
      edges.add(sourceGraph.getEdge(node, nodeList.get((i + 1) % nodeList.size())));
    }
    
    {
      double area = 0, cx = 0, cy = 0;
      
      for (int i = 0; i < nodeCount(); i++) {
        int iNext = (i + 1) % nodeCount();
        
        double cross = nodeList.get(i).getX() * nodeList.get(iNext).getY()
            - nodeList.get(iNext).getX() * nodeList.get(i).getY();
        
        cx += cross * (nodeList.get(i).getX() + nodeList.get(iNext).getX());
        cy += cross * (nodeList.get(i).getY() + nodeList.get(iNext).getY());
        area += cross;
      }
      
      area *= 3;
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
}
