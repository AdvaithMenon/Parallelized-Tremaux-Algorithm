
public class Cell {
	public boolean north;
	public boolean south;
	public boolean east;
	public boolean west;
	public boolean visited;
	public boolean isExit;
	public String visitedBy;
	public int visitedNumber;
	public Cell cameFrom = null;
	public int[] coords;
	
	Cell(boolean entry, boolean exit, int[] coords) {
		north = true;
		south = true;
		east = true;
		west = true;
		visited = false;
		visitedBy = null;
		isExit = exit;
		this.coords = new int[] { coords[0], coords[1] };
	}
	
	Cell(int[] coords) {
		north = true;
		south = true;
		east = true;
		west = true;
		visited = false;
		isExit = false;
		this.coords = new int[] { coords[0], coords[1] };
	}
	
	@Override
	public String toString() {
		return "(" + this.coords[1] + ", " + this.coords[0] + ")\nVisited By: " + visitedNumber + "\n\n";
	}
	
	
}
