package notTheWitness.board;

import java.util.*;

import notTheWitness.board.qualifiers.*;

public class BoardGridTemplate {
  public static final int END_SIDE_LEFT = 0,
      END_SIDE_TOP = 1,
      END_SIDE_RIGHT = 2,
      END_SIDE_BOTTOM = 3;
  
  private NodeGraph graph = new NodeGraph();
  private Board board;
  
  private Node[][] nodes;
  private Edge[][] hEdges, vEdges;
  private Cell[][] cells;
  
  private int rows, cols, antiRows, antiCols;
  
  public BoardGridTemplate(int rows, int cols, int posX, int posY, int width, int height) {
    this.rows = rows;
    this.cols = cols;
    antiRows = rows - 1;
    antiCols = cols - 1;
    
    generateGrid(posX, posY, width, height);
  }
  
  public Board getBoard() {
    board.init();
    return board;
  }
  
  private void generateGrid(int posX, int posY, int width, int height) {
    double scale = Math.min(width / (double)cols, height / (double)rows);
    
    nodes = new Node[rows][cols];
    hEdges = new Edge[rows][antiCols];
    vEdges = new Edge[antiRows][cols];
    cells = new Cell[antiRows][antiCols];
    
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        graph.add(nodes[r][c] = new Node(posX + (int)Math.round(scale * c), posY + (int)Math.round(scale * r)));
        
        if (r > 0) vEdges[r - 1][c] = graph.connect(nodes[r][c], nodes[r - 1][c]);
        if (c > 0) hEdges[r][c - 1] = graph.connect(nodes[r][c], nodes[r][c - 1]);
      }
    }
    
    board = new Board(graph);
    
    for (int r = 0; r < antiRows; r++) {
      for (int c = 0; c < antiCols; c++) {
        HashSet<Cell> query = board.getCellGraph().getCells(new Node[] {
          nodes[r][c],
          nodes[r + 1][c],
          nodes[r][c + 1],
          nodes[r + 1][c + 1],
        });
        
        if (query.size() == 1) cells[r][c] = query.iterator().next();
        else System.err.println("Warning: Found " + query.size() + " cells for node (" + r + ", " + c + ") - expected 1.");
      }
    }
  }
  
  public Qualifier<Node> getNodeQualifier(int r, int c) { return nodes[r][c].getQualifier(); }
  public Qualifier<Edge> getHEdgeQualifier(int r, int c) { return hEdges[r][c].getQualifier(); }
  public Qualifier<Edge> getVEdgeQualifier(int r, int c) { return vEdges[r][c].getQualifier(); }
  public Qualifier<Cell> getCellQualifier(int r, int c) { return cells[r][c].getQualifier(); }
  
  public void setNodeQualifier(int r, int c, Qualifier<Node> value) { nodes[r][c].setQualifier(value); }
  public void setHEdgeQualifier(int r, int c, Qualifier<Edge> value) { hEdges[r][c].setQualifier(value); }
  public void setVEdgeQualifier(int r, int c, Qualifier<Edge> value) { vEdges[r][c].setQualifier(value); }
  public void setCellQualifier(int r, int c, Qualifier<Cell> value) { cells[r][c].setQualifier(value); }
  
  public int getNodeType(int r, int c) { return nodes[r][c].getNodeType(); }
  public int getHEdgeType(int r, int c) { return hEdges[r][c].getEdgeType(); }
  public int getVEdgeType(int r, int c) { return vEdges[r][c].getEdgeType(); }
  
  public void setNodeType(int r, int c, int value) { nodes[r][c].setNodeType(value); }
  public void setHEdgeType(int r, int c, int value) { hEdges[r][c].setEdgeType(value); }
  public void setVEdgeType(int r, int c, int value) { vEdges[r][c].setEdgeType(value); }
  
  public Node addEndPoint(int side, int pos) {
    int endCrossOffs = 0, endXOffs = 0, endYOffs = 0;
    Node end, endLink = null;
    
    if (pos == 0) endCrossOffs = -25;
    else if (pos == (side % 2 > 0 ? rows : cols)) endCrossOffs = 25;
    
    switch (side) {
    case END_SIDE_LEFT:
      endXOffs = -25;
      endYOffs = endCrossOffs;
      endLink = nodes[pos][0];
      break;
    case END_SIDE_TOP:
      endXOffs = endCrossOffs;
      endYOffs = -25;
      endLink = nodes[0][pos];
      break;
    case END_SIDE_RIGHT:
      endXOffs = 25;
      endYOffs = endCrossOffs;
      endLink = nodes[pos][antiCols];
      break;
    case END_SIDE_BOTTOM:
      endXOffs = endCrossOffs;
      endYOffs = 25;
      endLink = nodes[antiRows][pos];
      break;
    }
      
    end = new Node(endLink.getX() + endXOffs, endLink.getY() + endYOffs, Node.TYPE_END);
    
    graph.add(end);
    graph.connect(end, endLink);
    
    return end;
  }
}
