package notTheWitness.board.qualifiers;

import java.awt.*;

public class DetourQualifier extends Qualifier {
	public static final int RADIUS = 4,
			WIDTH = 8;
	
	public void paint(Graphics2D g) {
		g.setColor(new Color(.1f, .1f, .1f));
		g.fillOval(getNode().getX() - RADIUS, getNode().getY() - RADIUS, WIDTH, WIDTH);
	}
}
