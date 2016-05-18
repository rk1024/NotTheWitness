package notTheWitness.board;

import java.util.*;

public class NodeGraph {
  private HashSet<Node> nodes = new HashSet<Node>();
  private HashSet<Edge> edges = new HashSet<Edge>();
  private HashMap<Node, HashMap<Node, Edge>> edgeMap = new HashMap<Node, HashMap<Node, Edge>>();
  
  public NodeGraph() { }
  
  public NodeGraph(Collection<Node> nodes) {
    this();
    this.nodes.addAll(nodes);
  }
  
  public Iterable<Node> getNodes() { return nodes; }
  public Iterable<Edge> getEdges() { return edges; }
  public Iterable<Edge> getEdges(Node connectedTo) { return edgeMap.get(connectedTo).values(); }
  
  public boolean contains(Node node) { return nodes.contains(node); }
  
  public boolean add(Node node) {
    return nodes.add(node);
  }
  
  public boolean remove(Node node) {
    if (nodes.remove(node)) {
      for (Edge edge : edgeMap.remove(node).values()) {
        edges.remove(edge);
        edgeMap.get(edge.other(node)).remove(node);
      }
      
      return true;
    }
    
    return false;
  }
  
  public void clear() {
    nodes.clear();
    edges.clear();
    edgeMap.clear();
  }
  
  public Edge getEdge(Node nodeA, Node nodeB) {
    if (!edgeMap.containsKey(nodeA)) return null;
    
    return edgeMap.get(nodeA).get(nodeB);
  }
  
  private void putEdge(Node primary, Node secondary, Edge edge) {
    if (!edgeMap.containsKey(primary)) edgeMap.put(primary, new HashMap<Node, Edge>());
    
    edgeMap.get(primary).put(secondary, edge);
  }
  
  public boolean connected(Node nodeA, Node nodeB) {
    return getEdge(nodeA, nodeB) != null;
  }
  
  public Edge connect(Node nodeA, Node nodeB) {
    if (!(nodes.contains(nodeA) && nodes.contains(nodeB))) return null;

    if (!connected(nodeA, nodeB)) {
      Edge edge = new Edge(nodeA, nodeB);
      
      edges.add(edge);
      putEdge(nodeA, nodeB, edge);
      putEdge(nodeB, nodeA, edge);
      
      return edge;
    }
    
    return null;
  }
}
