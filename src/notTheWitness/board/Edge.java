package notTheWitness.board;

public class Edge {
  private Node nodeA, nodeB;
  private Edge linkedEdge = null;
  
  public Node getNodeA() { return nodeA; }
  public Node getNodeB() { return nodeB; }
  
  public Edge(Node a, Node b) {
    nodeA = a;
    nodeB = b;
  }
  
  public boolean connects(Node a, Node b) {
    return (a == nodeA && b == nodeB) ||
        (a == nodeB && b == nodeA);
  }
  
  public Node other(Node node) {
    if (node == nodeA) return nodeB;
    if (node == nodeB) return nodeA;
    return null;
  }
  
  public Edge getLinkedEdge() { return linkedEdge; }
  public void setLinkedEdge(Edge value) { linkedEdge = value; }
}
