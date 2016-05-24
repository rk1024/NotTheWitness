package notTheWitness.board;

import java.awt.*;
import java.util.*;

public class CellGraph implements Paintable {
  private HashSet<Cell> cells = new HashSet<Cell>();
  private NodeGraph graph;
  
  public CellGraph(NodeGraph graph) {
    this.graph = graph;
    
    this.cells = findCells();
  }
  
  private HashSet<Cell> findCells() {
    HashSet<Cell> cells = new HashSet<Cell>();
    HashSet<Node> checked = new HashSet<Node>();
    ArrayList<Node> pool = new ArrayList<Node>();
    
    for (Node node : graph.getNodes()) pool.add(node);
    
    for (int i = 0; i < pool.size() - 2; i++) {
      checked.add(pool.get(i));
      for (int j = i + 1; j < pool.size() - 1; j++) {
        if (!graph.connected(pool.get(i), pool.get(j))) continue;
        
        ArrayList<ArrayList<Node>> paths = new ArrayList<ArrayList<Node>>();
        
        for (int k = j + 1; k < pool.size(); k++) {
          if (!graph.connected(pool.get(i), pool.get(k))) continue;
          
          if (graph.connected(pool.get(j), pool.get(k))) {
            cells.add(new Cell(graph, new Node[] {
              pool.get(i),
              pool.get(j),
              pool.get(k),
            }));
          }
          
          ArrayList<Node> path = new ArrayList<Node>(3);
          path.add(pool.get(j));
          path.add(pool.get(i));
          path.add(pool.get(k));
          paths.add(path);
        }
        
        while (paths.size() > 0) {
          ArrayList<Node> path = paths.remove(0);
          Node k = path.get(path.size() - 1);
          
          chkLoop: for (int m = i + 1; m < pool.size(); m++) {
            if (path.contains(pool.get(m)) || !graph.connected(pool.get(m), k)) continue chkLoop;
            
            boolean chord = false;
            for (int n = 1; n < path.size() - 1; n++) {
              if (graph.connected(pool.get(m), path.get(n))) {
                chord = true;
                break;
              }
            }
            
            if (chord) continue chkLoop;
            
            ArrayList<Node> newPath = new ArrayList<Node>(path.size() + 1);
            newPath.addAll(path);
            newPath.add(pool.get(m));
            
            Polygon x = new Polygon();

            for (Node node : newPath)
              x.addPoint(node.getX(), node.getY());
            
            for (Node node : graph.getNodes()) {
              if (newPath.contains(node)) continue;
              
              if (x.contains(node.getX(), node.getY())) {
                continue chkLoop;
              }
            }

            if (graph.connected(pool.get(m), pool.get(j))) {
              cells.add(new Cell(graph, newPath));
              continue chkLoop;
            }
            
            int hash = 0;
            
            for (Node node : newPath)
              hash += node.hashCode();
            
            paths.add(newPath);
          }
        }
      }
    }
    
    return cells;
  }
  
  
  public void paint(Graphics2D g) {
    for (Cell cell : cells){
      //Polygon p = new Polygon();
      
      //for (Node node : cell.getNodes())
        //p.addPoint(node.getX(), node.getY());
      
      //g.setColor(new Color(1f, 0f, 0f, .5f));
      //g.fill(p);
      
      g.setStroke(new BasicStroke(2));
      g.setColor(new Color(0f, 0.5f, 1f));
      g.drawOval(cell.getX() - 5, cell.getY() - 5, 10, 10);
    }
  }
  
  public Iterable<Cell> getCells() { return cells; }
  
  public HashSet<Cell> getCells(Node[] bordering) {
    HashSet<Cell> set = new HashSet<Cell>();
    
    cellLoop: for (Cell cell : cells) {
      for (Node node : bordering) {
        if (!cell.contains(node))
          continue cellLoop;
      }
      
      set.add(cell);
    }
    
    return set;
  }
}
