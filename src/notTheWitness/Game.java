package notTheWitness;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;

import notTheWitness.board.*;
import notTheWitness.board.qualifiers.*;
import notTheWitness.util.HashMap2D;

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
    
    {
      
      HashMap2D<Integer, Integer, Qualifier<Node>> nodeQuals = new HashMap2D<Integer, Integer, Qualifier<Node>>();
      nodeQuals.put(0, 1, new NodeDetourQualifier());
      nodeQuals.put(1, 0, new NodeDetourQualifier());

      int[][] hEdgeType = new int[10][9],
          vEdgeType = new int[9][10];
      
      for (int i = 1; i <= 8; i++)
        vEdgeType[0][i] = Edge.TYPE_NONE;
      
      hEdgeType[1][2] = Edge.TYPE_BLOCKED;
      
      this.currentBoard = BoardFactory.makeBoard(nodeQuals, hEdgeType, vEdgeType, 10, 10, BoardFactory.END_SIDE_RIGHT, 2);
    }
    
    win.setVisible(true);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;
    
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
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
