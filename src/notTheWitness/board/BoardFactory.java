package notTheWitness.board;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import notTheWitness.*;
import notTheWitness.board.qualifiers.*;

public class BoardFactory {
	public static Board makeBoard(NodeQualifier qualifiers[][], int width, int height ) {
			
			NodeGraph path = new NodeGraph();
		    Node[][] grid = new Node[height][width];
		    		
		    
		    for (int r = 0; r < grid.length; r++) {
		      Node[] row = grid[r];
		      
		      for (int c = 0; c < row.length; c++) {
		        Node node = row[c] = new Node(c * 50 + 100, r * 50 + 100, r == 0 && c == 0 ? Node.TYPE_START : Node.TYPE_NONE);
		        
		        path.add(node);
		        
		        if (c > 0) path.connect(node, row[c - 1]);
		        if (r > 0) path.connect(node, grid[r - 1][c]);
		        
		       if(qualifiers[r][c]!=null) node.setQualifier(qualifiers[r][c]);
		      }
		    }
		    Node end = new Node(9 * 50 + 100 + 25, 5 * 50 + 100, Node.TYPE_END);
    	    
    	    path.add(end);
    	    path.connect(end, grid[5][9]);
		    Board gameLevel = new Board(path);
		    return gameLevel;
	}
}	