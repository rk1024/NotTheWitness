package notTheWitness.board;

import java.util.*;

import notTheWitness.board.qualifiers.*;

public class Node {
  public static final int TYPE_NONE = 0,
      TYPE_START = 1,
      TYPE_END = 2;
  
  private int x, y, nodeType;
  private Qualifier<Node> qual = null;
  
  public Node(int x, int y, int type) {
    this.x = x;
    this.y = y;
    nodeType = type;
  }
  
  public Node(int x, int y) {
    this(x, y, TYPE_NONE);
  }
  
  public int getX() { return x; }
  public int getY() { return y; }
  public int getNodeType() { return nodeType; }
  
  public Qualifier<Node> getQualifier() { return qual; }
  public void setQualifier(Qualifier<Node> value) {
  	if (hasQualifier()) qual.detach();
  	qual = value;
  	if (hasQualifier()) qual.attach(this);
	}
  
  public boolean hasQualifier() { return qual != null; }
}
