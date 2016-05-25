package notTheWitness.board.qualifiers;

import java.awt.*;

import notTheWitness.board.*;

public class ColorSepQualifier extends ColoredQualifier<Cell> {

	public ColorSepQualifier(Color color) {
		super(color);
	}

	@Override
	public void paint(Graphics2D g, int x, int y) {
		applyColor(g);
		g.fillRoundRect(x, y, 5, 5, 2, 2);
		
	}

}
