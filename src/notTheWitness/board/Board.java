package notTheWitness.board;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import notTheWitness.*;

public class Board implements Paintable {
  public static final int PATH_RADIUS = 10,
      PATH_WIDTH = PATH_RADIUS + PATH_RADIUS,
      SP_RADIUS = 20,
      SP_WIDTH = SP_RADIUS + SP_RADIUS,
      HL_BORDER = 5,
      HL1_RADIUS = PATH_RADIUS + HL_BORDER,
      HL1_WIDTH = HL1_RADIUS + HL1_RADIUS,
      HL2_RADIUS = PATH_RADIUS + HL_BORDER + HL_BORDER,
      HL2_WIDTH = HL2_RADIUS + HL2_RADIUS,
      HIT_RADIUS = HL2_RADIUS + Game.CUR_RADIUS,
      HIT_RADIUS_SQ = HIT_RADIUS * HIT_RADIUS;
  
  private HashSet<Node> openNodes = new HashSet<Node>(),
      hlNodes = new HashSet<Node>(),
      drawnSet = new HashSet<Node>();
  private Stack<Node> drawnNodes = new Stack<Node>();
  private NodeGraph path = new NodeGraph(), drawn = new NodeGraph();
  private boolean canHandleDrag = false;
  
  public Board() {
    Node[][] grid = new Node[10][10];
    
    for (int r = 0; r < grid.length; r++) {
      Node[] row = grid[r];
      
      for (int c = 0; c < row.length; c++) {
        Node node = row[c] = new Node(c * 50 + 100, r * 50 + 100, r == 0 && c == 0 ? Node.TYPE_START : Node.TYPE_NONE);
        
        path.add(node);
        
        if (c > 0 && Math.random() < .5) path.connect(node, row[c - 1]);
        if (r > 0 && Math.random() < .5) path.connect(node, grid[r - 1][c]);
      }
    }
    
    Node end = new Node(9 * 50 + 100 + 25, 5 * 50 + 100, Node.TYPE_END);
    
    path.add(end);
    path.connect(end, grid[5][9]);
    
    updateOpenNodes();
    updateHighlights();
  }
  
  private void openNode(Node node) { openNodes.add(node); }
  private void closeNode(Node node) { openNodes.remove(node); }
  private boolean isNodeOpen(Node node) { return openNodes.contains(node); }
  private void closeAllNodes() { openNodes.clear(); }
  
  private void highlight(Node node) { hlNodes.add(node); }
  private void unHighlight(Node node) { hlNodes.remove(node); }
  private boolean highlighted(Node node) { return hlNodes.contains(node); }
  private void clearHighlights() { hlNodes.clear(); }
  
  private void paintNodeGraph(NodeGraph graph, Graphics2D g) {
    for (Edge edge : graph.getEdges()) {
      Node a = edge.getNodeA(),
          b = edge.getNodeB();
      
      
      
      double angle = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX());
      
      g.fillPolygon(new int[] {
          (int)Math.round(a.getX() + Math.sin(angle) * PATH_RADIUS),
          (int)Math.round(b.getX() + Math.sin(angle) * PATH_RADIUS),
          (int)Math.round(b.getX() - Math.sin(angle) * PATH_RADIUS),
          (int)Math.round(a.getX() - Math.sin(angle) * PATH_RADIUS),
      }, new int[] {
          (int)Math.round(a.getY() - Math.cos(angle) * PATH_RADIUS),
          (int)Math.round(b.getY() - Math.cos(angle) * PATH_RADIUS),
          (int)Math.round(b.getY() + Math.cos(angle) * PATH_RADIUS),
          (int)Math.round(a.getY() + Math.cos(angle) * PATH_RADIUS),
      }, 4);
    }
    
    for (Node node : graph.getNodes()) {
      int radius, width;
      if (node.getNodeType() == Node.TYPE_START) {
        radius = SP_RADIUS;
        width = SP_WIDTH;
      }
      else {
        radius = PATH_RADIUS;
        width = PATH_WIDTH;
      }
      
      g.fillOval(node.getX() - radius, node.getY() - radius, width, width);
    }
  }
  
  public void paint(Graphics2D g) {
    g.setColor(new Color(.5f, .5f, .5f));
    paintNodeGraph(path, g);
    
    g.setColor(new Color(0f, .5f, 1f));
    paintNodeGraph(drawn, g);
    
    g.setColor(new Color(1f, 1f, 1f, .25f));
    
    for (Node node : hlNodes) {
      g.fillOval(node.getX() - PATH_RADIUS, node.getY() - PATH_RADIUS, PATH_WIDTH, PATH_WIDTH);
      g.fillOval(node.getX() - HL1_RADIUS, node.getY() - HL1_RADIUS, HL1_WIDTH, HL1_WIDTH);
      g.fillOval(node.getX() - HL2_RADIUS, node.getY() - HL2_RADIUS, HL2_WIDTH, HL2_WIDTH);
    }
  }
  
  private int hitRadius(Node node, int x, int y) {
    int dx = node.getX() - x,
        dy = node.getY() - y,
        radius = dx * dx + dy * dy;
    
    return radius;
  }
  
  private boolean hitTestNode(Node node, int x, int y) {
    return hitRadius(node, x, y) <= HIT_RADIUS_SQ;
  }
  
  private BoardHitResult hitTestNodes(Iterable<Node> nodes, int x, int y, boolean ignoreRadius) {
    Node hit = null;
    int hitRadius = -1;
    for (Node node : nodes) {
      int radius = hitRadius(node, x, y);
      
      if ((ignoreRadius || radius <= HIT_RADIUS_SQ) && (hit == null || hitRadius > radius)) {
        hit = node;
        hitRadius = radius;
      }
    }
    
    if (hit == null) return null;
    return new BoardHitResult(hitRadius, hit);
  }
  
  private void drawNode(Node node) {
    if (drawnSet.contains(node)) return;
    
    drawn.add(node);
    drawnSet.add(node);
    if (drawnNodes.size() > 0) drawn.connect(node, drawnNodes.get(drawnNodes.size() - 1));
    
    drawnNodes.push(node);
  }
  
  private void backtrack(int idx) {
    ++idx;
    
    if (idx == drawnNodes.size()) return;
    
    while (drawnNodes.size() > idx) {
      Node rem = drawnNodes.pop();
      drawn.remove(rem);
      drawnSet.remove(rem);
    }
    
    updateHighlights();
  }
  
  private void backtrack(Node to) {
    if (to == drawnNodes.peek()) return;
    backtrack(drawnNodes.indexOf(to));
  }
  
  private void clearDrawn() {
    drawn.clear();
    drawnSet.clear();
    drawnNodes.clear();
  }
  
  private void mouseDraw(int x, int y) {
    BoardHitResult hit;
    
    if (drawnNodes.size() > 0) {
      hit = hitTestNodes(drawnNodes, x, y, false);
    
      if (hit != null) {
        backtrack(hit.getNode());
        updateOpenNodes();
        
        return;
      }
    }
    
    boolean drewAny = false;
    do {
      hit = hitTestNodes(openNodes, x, y, true);
      BoardHitResult drawnHit = hitTestNodes(drawnNodes, x, y, true);
      
      if (hit == null || drawnSet.contains(hit.getNode()) || (drawnHit != null && hit.getRadius() >= drawnHit.getRadius())) break;
      
      if (!drewAny) drewAny = true;
      drawNode(hit.getNode());
      updateOpenNodes();
    } while (true);
    
    if (drewAny) {
      updateHighlights();
      return;
    }
    
    if (drawnNodes.size() > 0) {
      hit = hitTestNodes(drawnNodes, x, y, true); 
      
      if (hit != null) {
        backtrack(hit.getNode());
        updateOpenNodes();
        
        return;
      }
    }
  }
  
  public void handlePress(MouseEvent e) {
    int x = e.getX(), y = e.getY();
    
    canHandleDrag = drawnNodes.size() == 0 ? hitTestNodes(openNodes, x, y, false) != null : hitTestNodes(drawnNodes, x, y, false) != null;
    
    if (canHandleDrag) {
      mouseDraw(x, y);
    }
  }
  
  public void handleDrag(MouseEvent e) {
    if (!canHandleDrag) return;
    mouseDraw(e.getX(), e.getY());
  }
  
  public void handleRelease(MouseEvent e) {
    if (drawnNodes.size() == 1) {
      clearDrawn();
      updateOpenNodes();
      updateHighlights();
    }
  }
  
  public void updateOpenNodes() {
    closeAllNodes();
    if (drawnNodes.size() == 0) {
      for (Node node : path.getNodes()) {
        if (node.getNodeType() == Node.TYPE_START) openNode(node);
      }
      
      return;
    }
    
    Node peek = drawnNodes.peek();
    for (Edge edge : path.getEdges(peek)) {
      Node other = edge.other(peek);
      
      if (!drawnSet.contains(other)) openNode(other);
    }
  }
  
  public void updateHighlights() {
    clearHighlights();
    if (drawnNodes.size() < 2) {
      for (Node node : path.getNodes()) {
        if (node.getNodeType() == Node.TYPE_START) highlight(node);
      }
      
      return;
    }
    
    if (drawnNodes.peek().getNodeType() != Node.TYPE_END) {
      for (Node node : path.getNodes()) {
        if (node.getNodeType() == Node.TYPE_END) highlight(node);
      }
    }
  }
}
