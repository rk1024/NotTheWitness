package notTheWitness;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

import notTheWitness.board.*;

public class Game extends JPanel {
  public static final int CUR_RADIUS = 8,
      CUR_WIDTH = CUR_RADIUS + CUR_RADIUS;
  
  private Component frame;
  private Board currentBoard;
  private JFrame win = new JFrame("Not the Witness");
  private Toolkit tk;
  private Cursor hiddenCursor;
  
  private int mouseX = 0, mouseY = 0;
  
  public Game() {
    win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    win.setSize(1280, 720);
    win.setResizable(false);
    
    this.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) { }

      @Override
      public void mouseExited(MouseEvent e) {
        mouseX = mouseY = -1;
        repaint();
      }

      @Override
      public void mousePressed(MouseEvent e) {
        if (currentBoard != null) currentBoard.handlePress(e);
        repaint();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (currentBoard != null) currentBoard.handleRelease(e);
        repaint();
      }
    });
    
    this.addMouseMotionListener(new MouseMotionListener() {
      @Override
      public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        
        if (currentBoard != null) currentBoard.handleDrag(e);
        
        repaint();
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        
        repaint();
      }
    });
    
    win.setContentPane(this);
    
    setDoubleBuffered(true);
    setFocusable(true);
    requestFocus();
    
    tk = getToolkit();
    hiddenCursor = tk.createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), "hidden");
    setCursor(hiddenCursor);
    
    this.currentBoard = new Board();
    
    win.setVisible(true);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    //super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    //g2.clearRect(0, 0, getWidth(), getHeight());
    
    g2.setColor(Color.DARK_GRAY);
    g2.fillRect(0, 0, getWidth(), getHeight());
    
    if (currentBoard != null) currentBoard.paint(g2);
    
    if (mouseX > -1 && mouseY > -1) {
      g2.setColor(Color.LIGHT_GRAY);
      g2.fillOval(mouseX - CUR_RADIUS, mouseY - CUR_RADIUS, CUR_WIDTH, CUR_WIDTH);
    }
  }
  
  public static void main(String[] args) {
    Game game = new Game();
  }
}
