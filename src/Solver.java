import edu.princeton.cs.introcs.StdDraw;

public class Solver extends Thread {
	
	private Cell[][] maze;
	private int n;
	private int[] start;
	private int[] end;
	private java.awt.Color dotColour;
	
	public Cell endCell = null;
	public static Cell[] cellsToTraceback = new Cell[2];

	
	Solver(Cell[][] m, int n, int[] start, int[] end, java.awt.Color c) {
		this.maze = m;
		this.n = n;		
		this.start = start;
		this.end = end;
		dotColour = c;
	}
	

	
	@Override
	public void run() {		
		Solve(end, start[0], start[1], null);
	}
	
	private void Solve(int[] end, int x, int y, Cell cameFrom) {
		
		if (maze[x][y].visited && !Thread.currentThread().getName().equals(maze[x][y].visitedBy)) {
			Maze.done = true;
			endCell = cameFrom;
			cellsToTraceback[0] = endCell;
			cellsToTraceback[1] = maze[x][y];

			return;
		}
		
		String threadName = Thread.currentThread().getName();
		
        if (Maze.done || maze[x][y].visited) {
        	endCell = cameFrom;
        	return; 
        }
        
        maze[x][y].visited = true;
        maze[x][y].visitedBy = threadName;
        maze[x][y].cameFrom = cameFrom;
        
        StdDraw.setPenColor(dotColour);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(Globals.pauseTime);
        
        if (x == end[0] && y == end[1])
        	Maze.done = true;
        
        /*
        if (!maze[x][y].north && maze[x][y+1].visited && maze[x][y+1].visitedBy != threadName)
        	Solve(end, x, y + 1, maze[x][y]);
        
        else if (!maze[x][y].east && maze[x+1][y].visited && maze[x+1][y].visitedBy != threadName)
        	Solve(end, x + 1, y, maze[x][y]);
        
        else if (!maze[x][y].south && maze[x][y-1].visited && maze[x][y-1].visitedBy != threadName)
        	Solve(end, x, y - 1, maze[x][y]);
        
        else if (!maze[x][y].west && maze[x-1][y].visited && maze[x-1][y].visitedBy != threadName)
        	Solve(end, x - 1, y, maze[x][y]);
        
        else {
       */
	        if (!maze[x][y].north) Solve(end, x, y + 1, maze[x][y]);
	        if (!maze[x][y].east)  Solve(end, x + 1, y, maze[x][y]);
	        if (!maze[x][y].south) Solve(end, x, y - 1, maze[x][y]);
	        if (!maze[x][y].west)  Solve(end, x - 1, y, maze[x][y]);
       
        //}
        

        if (Maze.done) { 
        	endCell = cameFrom;
        	return;
        	
        }

        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
	}
	
	public static void TRACE() {
		Traceback T1 = new Traceback(cellsToTraceback[0]);
		Traceback T2 = new Traceback(cellsToTraceback[1]);
		
		T1.start();
		T2.start();
		
		try {
			T1.join();
			T2.join();
		} catch (InterruptedException ex) { System.out.println("Interrupted - " + ex.getMessage()); }
		
	}
	
}
