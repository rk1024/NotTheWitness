package notTheWitness.board;

import java.awt.*;
import java.util.*;

public class CellGraph implements Paintable {
  private HashSet<Cell> cells = new HashSet<Cell>();
  private NodeGraph graph;
  
  public CellGraph(NodeGraph graph) {
    this.graph = graph;
    
    {
      for (Node node : graph.getNodes()) {
        startLoop: for (ArrayList<Node> list : findCycle(node)) {
          if (list == null)
            continue;
            
          for (Cell cell : cells) {
            if (cell.nodeCount() != list.size()) continue;
              
            {
              boolean allContained = true;
              
              for (Node item : list) {
                if (!cell.contains(item)) {
                  allContained = false;
                  break;
                }
              }
              
              if (allContained) continue startLoop;
            }
          }
          
          cells.add(new Cell(graph, list));
        }
      }
    }
  }
  
  private ArrayList<ArrayList<Node>> findCycle(Node start) {
    for (Edge heading : graph.getEdges(start)) {
      if (!graph.contains(start)) return null;
      
      ArrayList<ArrayList<Node>> ret = new ArrayList<ArrayList<Node>>();
      
      HashSet<Node> pool = new HashSet<Node>();
      HashMap<Node, Double> distMap = new HashMap<Node, Double>();
      HashMap<Node, Node> prevMap = new HashMap<Node, Node>();
      
      for (Node node : graph.getNodes()) {
        pool.add(node);
        distMap.put(node, Double.POSITIVE_INFINITY);
      }
      
      distMap.put(start, 0d);
      
      while (pool.size() > 0) {
        Node min = null;
        {
          double dist = 0;
          
          for (Node node : pool) {
            if (min == null || distMap.get(node) < dist) {
              min = node;
              dist = distMap.get(node);
            }
          }
        }
        
        pool.remove(min);
        
        for (Edge edge : graph.getEdges(min)) {
          if (min == start && edge != heading) continue;
          //if (!edgePool.contains(edge)) continue;
          
          Node other = edge.other(min);
          
          if (pool.contains(other)) {
            double alt = distMap.get(min) + 1;
            
            if (alt < distMap.get(other)) {
              distMap.put(other, alt);
              prevMap.put(other, min);
            }
          }
          
          if (min == start) break;
        }
        {
          boolean allClosed = true;
          for (Edge edge : graph.getEdges(start)) {
            if (prevMap.get(edge.other(start)) != null) {
              allClosed = false;
              break;
            }
          }
          
          if (allClosed) break;
        }
      }
      
      {
        Node min = null;
        double dist = 0;
        
        for (Edge edge : graph.getEdges(start)) {
          //if (!edgePool.contains(edge)) continue;
          
          Node other = edge.other(start);
          
          if (prevMap.get(other) != start /*&& nodePool.contains(other)*/ && (min == null || distMap.get(other) < dist)) {
            min = other;
            dist = distMap.get(other);
          }
        }
        
        if (min != null) {
          distMap.put(start, dist + 1);
          prevMap.put(start, min);
        }
      }
      
      if (prevMap.get(start) == null) return null;
      
      ArrayList<Node> list = new ArrayList<Node>();
      HashSet<Node> set = new HashSet<Node>();
      {
        Node node = start;
        
        do {
          if (!set.add(node)) break;
          list.add(node);
          node = prevMap.get(node);
        } while (node != null);
      }
      
      ret.add(list);
    } 
  }

    return ret;
  }
  
  public void paint(Graphics2D g) {
    for (Cell cell : cells){
      Polygon p = new Polygon();
      
      for (Node node : cell.getNodes()) {
        p.addPoint(node.getX(), node.getY());
      }
      
      g.setColor(new Color(1f, 0f, 0f, .5f));
      g.fill(p);
      
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
