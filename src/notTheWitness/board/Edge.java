package notTheWitness.board;

public class Edge {
  public static final int TYPE_NORMAL = 0,
      TYPE_NONE = 1,
      TYPE_BLOCKED = 2;
  
  private int edgeType;
  private Node nodeA, nodeB;
  
  public Node getNodeA() { return nodeA; }
  public Node getNodeB() { return nodeB; }
  public int getEdgeType() { return edgeType; }
  
  public Edge(Node a, Node b, int type) {
    nodeA = a;
    nodeB = b;
    edgeType = type;
  }
  
  public Edge(Node a, Node b) {
    this(a, b, TYPE_NORMAL);
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
  
  public boolean isDrawable() {
    return !(edgeType == TYPE_NONE || edgeType == TYPE_BLOCKED);
  }
}
