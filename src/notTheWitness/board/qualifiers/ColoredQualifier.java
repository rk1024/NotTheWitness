package notTheWitness.board.qualifiers;

import java.awt.*;

public class ColoredQualifier extends NodeQualifier {
	private Color color;
	
	public ColoredQualifier(Color color) {
		this.color = color;
	}
	
	public Color getColor() { return color; }

	@Override
	public void paint(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
}
