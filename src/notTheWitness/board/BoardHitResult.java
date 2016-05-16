package notTheWitness.board;

public class BoardHitResult {
  private int radius;
  private Node node;
  
  public BoardHitResult(int radius, Node node) {
    this.radius = radius;
    this.node = node;
  }
  
  public int getRadius() { return radius; }
  public Node getNode() { return node; }
}
