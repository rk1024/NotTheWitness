package notTheWitness.board;

import notTheWitness.board.qualifiers.*;
import notTheWitness.util.*;

public class BoardFactory {
  public static final int END_SIDE_LEFT = 0,
      END_SIDE_TOP = 1,
      END_SIDE_RIGHT = 2,
      END_SIDE_BOTTOM = 3;
  
	public static Board makeBoard(HashMap2D<Integer, Integer, Qualifier<Node>> nodeQualifiers, int[][] hEdgeType, int[][] vEdgeType, int width, int height, int endSide, int endPos) {
			NodeGraph path = new NodeGraph();
		  Node[][] grid = new Node[height][width];
		    		
		    
		    for (int r = 0; r < grid.length; r++) {
		      Node[] row = grid[r];
		      
		      for (int c = 0; c < row.length; c++) {
		        Node node = row[c] = new Node(c * 50 + 100, r * 50 + 100, r == 0 && c == 0 ? Node.TYPE_START : Node.TYPE_NONE);
		        
		        path.add(node);
		        
		        if (c > 0) path.connect(node, row[c - 1], hEdgeType[r][c - 1]);
		        if (r > 0) path.connect(node, grid[r - 1][c], vEdgeType[r - 1][c]);
		        
		       if(nodeQualifiers.get(r, c) != null)
		         node.setQualifier(nodeQualifiers.get(r, c));
		      }
		    }
		    
		    int endCrossOffs = 0, endXOffs = 0, endYOffs = 0;
		    Node end, endLink = null;
		    
		    if (endPos == 0) endCrossOffs = -25;
		    else if (endPos == (endSide % 2 > 0 ? height : width)) endCrossOffs = 25;
		    
		    switch (endSide) {
        case END_SIDE_LEFT:
          endXOffs = -25;
          endYOffs = endCrossOffs;
          endLink = grid[endPos][0];
          break;
        case END_SIDE_TOP:
          endXOffs = endCrossOffs;
          endYOffs = -25;
          endLink = grid[0][endPos];
          break;
        case END_SIDE_RIGHT:
          endXOffs = 25;
          endYOffs = endCrossOffs;
          endLink = grid[endPos][width - 1];
          break;
        case END_SIDE_BOTTOM:
          endXOffs = endCrossOffs;
          endYOffs = 25;
          endLink = grid[height - 1][endPos];
          break;
        }
    	    
		    end = new Node(endLink.getX() + endXOffs, endLink.getY() + endYOffs, Node.TYPE_END);
		    
    	  path.add(end);
    	  path.connect(end, endLink);
    	  
		    Board gameLevel = new Board(path);
		    return gameLevel;
	}
}	