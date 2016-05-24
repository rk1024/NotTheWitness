package notTheWitness.board.qualifiers;

import java.awt.*;

public abstract class DetourQualifier<T> extends Qualifier<T> {
	public static final int RADIUS = 4,
			WIDTH = 8;
	
	@Override
	public void paint(Graphics2D g, int x, int y) {
		g.setColor(new Color(.1f, .1f, .1f));
		g.fillOval(x - RADIUS, y - RADIUS, WIDTH, WIDTH);
	}
}
