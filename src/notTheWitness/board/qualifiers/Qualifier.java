package notTheWitness.board.qualifiers;

import java.awt.*;

public abstract class Qualifier<T> {
  private T link;
  
  public abstract void paint(Graphics2D g, int x, int y);
  
  public void attach(T link) { this.link = link; }
  public void detach() { this.link = null; }
  public boolean attached() { return link != null; }
  
  public T getLink() { return link; }
}
