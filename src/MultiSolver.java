import edu.princeton.cs.introcs.StdDraw;
import java.util.Random;
import java.util.Stack;
import java.util.LinkedList;
import java.util.Queue;

public class MultiSolver extends Thread {
	
	private Cell[][] maze;
	private int n;
	private java.awt.Color dotColour;
	
	public Cell endCell = null;
	public int magicNumber;
	public static Cell[][] contactPoints;
	
	private int[] end;
	private int[] start;
	private static int numThreads;
	
	
	MultiSolver(Cell[][] m, int n, int[] start, int[] end, int threadNumber, java.awt.Color c) {
		this.maze = m;
		this.n = n;		
		dotColour = c;
		
		magicNumber = threadNumber;
		
		if (start == null)
			start = GetRandomStartingPoint();
		
		this.start = start;
		this.end = end;
	}
	

	
	@Override
	public void run() {
		Solve(end, start[0], start[1], null);

	}
	
	private void Solve(int[] end, int x, int y, Cell cameFrom) {
		if (Maze.done)  {
			return;
		}
		
		if (maze[x][y].visited) {
			int vn = maze[x][y].visitedNumber;
			
			if (magicNumber == vn) {
				return;
			}
			
			if (magicNumber < vn) {
				contactPoints[magicNumber][vn] = cameFrom;
				contactPoints[vn][magicNumber] = maze[x][y];
			}
			else if (magicNumber > vn) {
				contactPoints[vn][magicNumber] = maze[x][y];
				contactPoints[magicNumber][vn] = cameFrom;
			}
			
			//System.out.println("Solver: " + magicNumber + " just met Solver: " + vn);
			
			if (!Maze.done && CheckIfCompleted()) {
				Maze.done = true;
				return;
			}
					
			return;
			
			/*
			if (magicNumber > 0) {
				if (maze[x][y].visitedNumber == 0 && connections.contactPoints[0][0] == null) {
					System.out.println("Solver " + magicNumber + " just met Solver " + maze[x][y].visitedNumber);
					return;
				}
				else if (maze[x][y].visitedNumber == -1 && contactPoints[1][0] == null) {
					contactPoints[1] = new Cell[2];
					contactPoints[1][0] = cameFrom;
					contactPoints[1][1] = maze[x][y];
					System.out.println("Solver " + magicNumber + " just met Solver " + maze[x][y].visitedNumber);
					return;
				}
			
			}
			
			else if ((magicNumber == 0 && maze[x][y].visitedNumber == -1) || (magicNumber == -1 && maze[x][y].visitedNumber == 0)) {
				Maze.done = true;
				endCell = cameFrom;
				contactPoints[0] = new Cell[2];
				contactPoints[0][0] = endCell;
				contactPoints[1] = new Cell[2];
				contactPoints[1][0] = maze[x][y];
				return;
			}
			else if (magicNumber <= 0 && maze[x][y].visitedNumber > 0) {
				int index = Math.abs(magicNumber);
				if (contactPoints[index][0] == null) {
					contactPoints[index] = new Cell[2];
					contactPoints[index][0] = maze[x][y];
					contactPoints[index][1] = cameFrom;
				}
				
				System.out.println("Solver " + magicNumber + " just met Solver " + maze[x][y].visitedNumber);					
				
				return;
			}
			*/

		}
		
        
        maze[x][y].visited = true;
        maze[x][y].visitedNumber = magicNumber;
        maze[x][y].cameFrom = cameFrom;
        
        StdDraw.setPenColor(dotColour);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(Globals.pauseTime);
        
        if (end != null && x == end[0] && y == end[1])
        	Maze.done = true;
        
        
        if (!maze[x][y].north && maze[x][y+1].visited && maze[x][y+1].visitedNumber != magicNumber)
        	Solve(end, x, y + 1, maze[x][y]);
        
        else if (!maze[x][y].east && maze[x+1][y].visited && maze[x+1][y].visitedNumber != magicNumber)
        	Solve(end, x + 1, y, maze[x][y]);
        
        else if (!maze[x][y].south && maze[x][y-1].visited && maze[x][y-1].visitedNumber != magicNumber)
        	Solve(end, x, y - 1, maze[x][y]);
        
        else if (!maze[x][y].west && maze[x-1][y].visited && maze[x-1][y].visitedNumber != magicNumber)
        	Solve(end, x - 1, y, maze[x][y]);
        
        else {
       
	        if (!maze[x][y].north) Solve(end, x, y + 1, maze[x][y]);
	        if (!maze[x][y].east)  Solve(end, x + 1, y, maze[x][y]);
	        if (!maze[x][y].south) Solve(end, x, y - 1, maze[x][y]);
	        if (!maze[x][y].west)  Solve(end, x - 1, y, maze[x][y]);
       
        }
        

        if (Maze.done) { 
        	endCell = cameFrom;
        	return;
        	
        }

        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
	}
	
	private int[] GetRandomStartingPoint() {
		Random rand = new Random(System.nanoTime());
		return new int[] { rand.nextInt(n-2), rand.nextInt(n-2) };
	}
	
	public static void InitializeBooleans(int num) {
		numThreads = num;
		contactPoints = new Cell[numThreads][numThreads];
		
	}
	
	private static boolean CheckIfCompleted() {
		boolean result = CheckForFinishedCells() != null;
		//System.out.println("Is Completed = " + result);
		return result;
	}
	

	private static Stack <Cell> CheckForFinishedCells() {

		Stack <Cell> cells = new Stack <Cell> ();
		int currentThread = 0;
		int endThread = numThreads - 1;
	
		
		Queue <Integer> queue = new LinkedList <Integer> ();
		LinkedList <Integer> checkedThreads = new LinkedList <Integer> ();
		
		
		
		while (currentThread != endThread) {
			boolean foundConnection = false;
			
			for (int i=endThread; i>=0; i--) {
				if (!checkedThreads.contains(i) && contactPoints[currentThread][i] != null) {
					cells.add(contactPoints[currentThread][i]);
					cells.add(contactPoints[i][currentThread]);
					foundConnection = true;
					queue.add(i); 
					if (i == endThread)
						break;
				}
			}
			
			checkedThreads.add(currentThread);
			
			if (!foundConnection) {
				if (cells.size() < 2) {
					return null;
				}
				cells.pop();
				cells.pop();
			}
			
			if (queue.size() == 0) {
				return null;
			}
			
			currentThread = queue.poll();
			
		}
		
		if (currentThread == endThread) {
			for (int i=0; i<queue.size()-1; i++) {
				cells.pop();
				cells.pop();
			}
		}
		
		
		return cells;
	}
	
	
	public static void TRACE() {
		System.out.println("TRACING");
		Stack <Cell> cells = CheckForFinishedCells();
		Traceback[] traces = new Traceback[cells.size()];
		
		for (int i=0; i<cells.size(); i++) {
			traces[i] = new Traceback(cells.get(i));
			traces[i].start();
		}
		
		
		try {
			for (int i=0; i<cells.size(); i++) {
				traces[i].join();
			}
		}
		catch (InterruptedException ex) {
			System.out.println("Interrupted! - " + ex.getMessage());
		}
	}
	

	   
}








