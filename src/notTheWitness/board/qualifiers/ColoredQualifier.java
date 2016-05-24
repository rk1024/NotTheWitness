package notTheWitness.board.qualifiers;

import java.awt.*;

import notTheWitness.board.*;

public class ColoredQualifier extends Qualifier<Node> {
	private Color color;
	
	public ColoredQualifier(Color color) {
		this.color = color;
	}
	
	public Color getColor() { return color; }

	@Override
	public void paint(Graphics2D g, int x, int y) {
		// TODO Auto-generated method stub
	}
}
