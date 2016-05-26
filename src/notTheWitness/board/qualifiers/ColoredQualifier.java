package notTheWitness.board.qualifiers;

import java.awt.*;

public abstract class ColoredQualifier<T> extends Qualifier<T> {
	private Color color;
	
	public ColoredQualifier(Color color) {
		this.color = color;
	}
	
	public Color getColor() { return color; }
	
	protected void applyColor(Graphics2D g) {
	  g.setColor(color);
	}
}
