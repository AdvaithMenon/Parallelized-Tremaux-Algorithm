import edu.princeton.cs.introcs.StdDraw;

public class Traceback extends Thread {
	
	private Cell startCell;
	
	Traceback(Cell cell) {
		startCell = cell;
	}
	
	@Override
	public void run() {
		int maxTraceBacks = 1000;
		if (startCell == null)
    		return;
    	
    	StdDraw.setPenColor(StdDraw.BLACK);
    	StdDraw.filledCircle(startCell.coords[0] + 0.5, startCell.coords[1] + 0.5, 0.375 * 1.5);
    	if (Maze.tracedBack >= maxTraceBacks) { return; }
    	Maze.tracedBack ++;
    	
    	
    	while (startCell != null) {
    		StdDraw.setPenColor(StdDraw.GREEN);
    		StdDraw.filledCircle(startCell.coords[0] + 0.5, startCell.coords[1] + 0.5, 0.375/2);
    		startCell = startCell.cameFrom;
    		StdDraw.show();
    		StdDraw.pause(Globals.pauseTime);
    	}
    	
    	StdDraw.show();
    	StdDraw.pause(Globals.pauseTime);
	}
}
