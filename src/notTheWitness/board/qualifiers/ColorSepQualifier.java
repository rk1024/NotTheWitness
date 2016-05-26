package notTheWitness.board.qualifiers;

import java.awt.*;

import notTheWitness.board.*;

public class ColorSepQualifier extends ColoredQualifier<Cell> {
  public static final int RADIUS = 6,
      WIDTH = RADIUS + RADIUS,
      CORNER_RADIUS = 4;
  
  public ColorSepQualifier(Color color) {
    super(color);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void paint(Graphics2D g, int x, int y) {
    applyColor(g);
    g.fillRoundRect(x - RADIUS, y - RADIUS, WIDTH, WIDTH, CORNER_RADIUS, CORNER_RADIUS);
  }
}
