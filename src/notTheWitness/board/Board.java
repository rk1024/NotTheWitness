package notTheWitness.board;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import notTheWitness.*;
import notTheWitness.board.qualifiers.*;

public class Board implements Paintable {
  public class BoardFragment {
    private HashSet<Cell> cells = new HashSet<Cell>();
    private HashSet<Qualifier<Node>> nodeQuals = new HashSet<Qualifier<Node>>();
    private HashSet<Qualifier<Edge>> edgeQuals = new HashSet<Qualifier<Edge>>();
    private HashSet<Qualifier<Cell>> cellQuals = new HashSet<Qualifier<Cell>>();
    
    public BoardFragment() { }
    
    public Iterable<Cell> getCells() { return cells; }
    public Iterable<Qualifier<Node>> getNodeQuals() { return nodeQuals; }
    public Iterable<Qualifier<Edge>> getEdgeQuals() { return edgeQuals; }
    public Iterable<Qualifier<Cell>> getCellQuals() { return cellQuals; }
    
    public boolean addCell(Cell cell) { return cells.add(cell); }
    public boolean addNodeQual(Qualifier<Node> qual) { return nodeQuals.add(qual); }
    public boolean addEdgeQual(Qualifier<Edge> qual) { return edgeQuals.add(qual); }
    public boolean addCellQual(Qualifier<Cell> qual) { return cellQuals.add(qual); }
  }
  
  public static final int PATH_RADIUS = 8,
      PATH_WIDTH = PATH_RADIUS + PATH_RADIUS,
      SP_RADIUS = PATH_RADIUS + 10,
      SP_WIDTH = SP_RADIUS + SP_RADIUS,
      GAP_RADIUS = 4,
      GAP_RADIUS_SQ = GAP_RADIUS * GAP_RADIUS,
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
//  private ArrayList<BoardFragment> frags;
  private Stack<Node> drawnNodes = new Stack<Node>();
  private NodeGraph path, drawn = new NodeGraph();
  private CellGraph cells;
  private boolean canHandleDrag = false, isInvalid = false;
  
  public Board(NodeGraph path) {
    this.path = path;
    cells = new CellGraph(path);
    
    updateOpenNodes();
    updateHighlights();
  }
  
  public CellGraph getCellGraph() { return cells; }
  
  private void openNode(Node node) { openNodes.add(node); }
//  private void closeNode(Node node) { openNodes.remove(node); }
//  private boolean isNodeOpen(Node node) { return openNodes.contains(node); }
  private void closeAllNodes() { openNodes.clear(); }
  
  private void highlight(Node node) { hlNodes.add(node); }
//  private void unHighlight(Node node) { hlNodes.remove(node); }
//  private boolean highlighted(Node node) { return hlNodes.contains(node); }
  private void clearHighlights() { hlNodes.clear(); }
  
  private Polygon line(double x1, double y1, double x2, double y2, double radius) {
    Polygon p = new Polygon();
    
    double angle = Math.atan2(y2 - y1, x2 - x1);
    
    p.addPoint((int)Math.round(x1 + Math.sin(angle) * radius), (int)Math.round(y1 - Math.cos(angle) * radius));
    p.addPoint((int)Math.round(x2 + Math.sin(angle) * radius), (int)Math.round(y2 - Math.cos(angle) * radius));
    p.addPoint((int)Math.round(x2 - Math.sin(angle) * radius), (int)Math.round(y2 + Math.cos(angle) * radius));
    p.addPoint((int)Math.round(x1 - Math.sin(angle) * radius), (int)Math.round(y1 + Math.cos(angle) * radius));
    
    return p;
  }
  
  private void paintNodeGraph(NodeGraph graph, Graphics2D g) {
    for (Edge edge : graph.getEdges()) {
      Node a = edge.getNodeA(),
          b = edge.getNodeB();
      
      switch (edge.getEdgeType()) {
      case Edge.TYPE_NONE: break;
      case Edge.TYPE_NORMAL:
        g.fill(line(a.getX(), a.getY(), b.getX(), b.getY(), PATH_RADIUS));
        break;
        
      case Edge.TYPE_BLOCKED:
        if (hitRadius(a, b.getX(), b.getY()) <= GAP_RADIUS_SQ) break;
        
        double angle = Math.atan2(b.getY() - a.getY(), b.getX() - a.getX()),
          mx = (a.getX() + b.getX()) / 2d, my = (a.getY() + b.getY()) / 2d;

        g.fill(line(a.getX(), a.getY(), mx - GAP_RADIUS * Math.cos(angle), my - GAP_RADIUS * Math.sin(angle), PATH_RADIUS));
        g.fill(line(b.getX(), b.getY(), mx + GAP_RADIUS * Math.cos(angle), my + GAP_RADIUS * Math.sin(angle), PATH_RADIUS));
        
        break;
      }
    }
    
    for (Node node : graph.getNodes()) {
      if (graph.edgeCount(node) > 0) {
        boolean allHidden = true;
        
        for (Edge edge : graph.getEdges(node)) {
          if (edge.getEdgeType() != Edge.TYPE_NONE) {
            allHidden = false;
            break;
          }
        }
        
        if (allHidden) continue;
      }
      
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
  
  private ArrayList<BoardFragment> getFragments() {
    HashSet<Cell> checked = new HashSet<Cell>();
    ArrayDeque<Cell> queue = new ArrayDeque<Cell>();
    ArrayList<BoardFragment> frags = new ArrayList<BoardFragment>();
    
    for (Cell start : cells.getCells()) {
      if (checked.contains(start)) continue;
      
      BoardFragment frag = new BoardFragment();
      frags.add(frag);
      
      queue.addLast(start);
      
      while (queue.peekFirst() != null) {
        Cell cell = queue.pollFirst();
        if (!checked.add(cell)) continue;
        
        frag.addCell(cell);
        frag.addCellQual(cell.getQualifier());
        
        for (Edge edge : cell.getEdges()) {
          if (drawn.connected(edge.getNodeA(), edge.getNodeB())) continue;
          
          if (edge.getQualifier() != null) frag.addEdgeQual(edge.getQualifier());
          
          if (!(drawn.contains(edge.getNodeA()) || edge.getNodeA().getQualifier() == null)) frag.addNodeQual(edge.getNodeA().getQualifier());
          if (!(drawn.contains(edge.getNodeB()) || edge.getNodeB().getQualifier() == null)) frag.addNodeQual(edge.getNodeB().getQualifier());
          
          for (Cell bordering : cells.getCells(new Node[] {
            edge.getNodeA(),
            edge.getNodeB(),
          })) {
            if (bordering == cell) continue;
            
            queue.addLast(bordering);
          }
        }
      }
    }
    
    return frags;
  }
  
  public void paint(Graphics2D g) {
    g.setColor(new Color(.5f, .5f, .5f));
    paintNodeGraph(path, g);
    
    for (Node node : path.getNodes()) {
    	if (node.hasQualifier())
    		node.getQualifier().paint(g, node.getX(), node.getY());
    }
    
    for (Edge edge : path.getEdges()) {
      if (edge.hasQualifier()) {
        edge.getQualifier().paint(g, edge.getX(), edge.getY());
      }
    }
    
    cells.paint(g);
    
    g.setColor(Color.WHITE);
    
//    if (frags != null) {
//      for (int i = 0; i < frags.size(); i++) {
//        for (Cell cell : frags.get(i).getCells()) {
//          g.drawString(i + "", cell.getX(), cell.getY());
//        }
//      }
//    }
    
    g.setColor(isInvalid ? new Color(.85f, .05f, .05f) : new Color(0f, .5f, 1f));
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
  
//  private boolean hitTestNode(Node node, int x, int y) {
//    return hitRadius(node, x, y) <= HIT_RADIUS_SQ;
//  }
  
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
    if (idx == 0) { clearDrawn(); return; }
    
    while (drawnNodes.size() > idx) {
      Node rem = drawnNodes.pop();
      drawn.remove(rem);
      drawnSet.remove(rem);
    }

    updateOpenNodes();
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
    updateHighlights();
    updateOpenNodes();
  }
  
  private void mouseDraw(int x, int y) {
    BoardHitResult hit;
    
    if (drawnNodes.size() > 0) {
      hit = hitTestNodes(drawnNodes, x, y, false);
    
      if (hit != null) {
        backtrack(hit.getNode());
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
        return;
      }
    }
  }
  
  private boolean validate() {
  	for(Node node : path.getNodes()) {
  		if(node.hasQualifier()) {
  		  if (node.getQualifier() instanceof NodeDetourQualifier) {
  			  if(!drawn.contains(node)) {
  				  return false;
  			  }
  		  }
  		}
  	}
  	  
  	for(Edge edge : path.getEdges()) {
  	  if(edge.hasQualifier()) {
  	    if(edge.getQualifier() instanceof EdgeDetourQualifier) {
  	      if(!drawn.connected(edge.getNodeA(), edge.getNodeB())) {
  	        return false;
  	      }
  	    }
  	  }
  	}
  	
  	ArrayList<BoardFragment> frags = getFragments();
  	
  	for (BoardFragment frag : frags) {
  	  Color firstSepColor = null;
  	  
  	  for (Qualifier<Cell> qual : frag.getCellQuals()) {
  	    if (qual instanceof ColorSepQualifier) {
  	      ColorSepQualifier castQual = (ColorSepQualifier)qual;
  	      if (firstSepColor == null) firstSepColor = castQual.getColor();
  	      else if (castQual.getColor().getRGB() != firstSepColor.getRGB()) return false;
  	    }
  	  }
  	}
  	
    return true;
  }
  
  public void handlePress(MouseEvent e) {
    int x = e.getX(), y = e.getY();
    
    BoardHitResult hitResult = hitTestNodes(path.getNodes(), x, y, false);
    if (hitResult != null && hitResult.getNode().getNodeType() == Node.TYPE_START)
      clearDrawn();
    
    canHandleDrag = drawnNodes.size() == 0 ? hitTestNodes(openNodes, x, y, false) != null : hitTestNodes(drawnNodes, x, y, false) != null;
    
    if (canHandleDrag) {
      mouseDraw(x, y);
      isInvalid = false;
    }
  }
  
  public void handleDrag(MouseEvent e) {
    if (!canHandleDrag) return;
    mouseDraw(e.getX(), e.getY());
  }
  
  public void handleRelease(MouseEvent e) {
    if (drawnNodes.size() < 2) {
      clearDrawn();
      updateOpenNodes();
      updateHighlights();
    }
    
    isInvalid = drawnNodes.size() > 0 && drawnNodes.peek().getNodeType() == Node.TYPE_END && !validate();
  }
  
  private void updateOpenNodes() {
    closeAllNodes();
    if (drawnNodes.size() == 0) {
      for (Node node : path.getNodes()) {
        if (node.getNodeType() == Node.TYPE_START) openNode(node);
      }
      
      return;
    }
    
    Node peek = drawnNodes.peek();
    for (Edge edge : path.getEdges(peek)) {
      if (!edge.isDrawable()) continue;
      
      Node other = edge.other(peek);
      
      if (!drawnSet.contains(other)) openNode(other);
    }
    
//    frags = getFragments();
  }
  
  private void updateHighlights() {
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
  
  public void init() {
    updateOpenNodes();
    updateHighlights();
  }
}
