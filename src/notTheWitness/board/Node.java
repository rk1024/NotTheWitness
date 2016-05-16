package notTheWitness.board;

import java.util.*;

import notTheWitness.board.qualifiers.*;

public class Node {
  public static final int TYPE_NONE = 0,
      TYPE_START = 1,
      TYPE_END = 2;
  
  private int x, y;
  private int nodeType;
  private NodeQualifier qual;
  
  public Node(int x, int y, int nodeType) {
    this.x = x;
    this.y = y;
    this.nodeType = nodeType;
  }
  
  public Node(int x, int y) {
    this(x, y, TYPE_NONE);
  }
  
  public int getX() { return x; }
  public int getY() { return y; }
  public int getNodeType() { return nodeType; }
  
  public NodeQualifier getQualifier() { return qual; }
  public void setQualifier(NodeQualifier value) {
  	if (hasQualifier()) qual.detach();
  	qual = value;
  	qual.attach(this);
	}
  
  public boolean hasQualifier() { return qual != null; }
}
