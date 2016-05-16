package notTheWitness.board.qualifiers;

import java.awt.*;

import notTheWitness.board.*;

public abstract class Qualifier implements Paintable {
	private Node node;
	
	public abstract void paint(Graphics2D g);
	
	public void attach(Node node) { this.node = node; }
	public void detach() { node = null; }
	public boolean attached() { return node != null; }
	
	public Node getNode() { return node; }
}
