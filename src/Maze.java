import edu.princeton.cs.introcs.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

class Globals {
	public static int pauseTime = 0;
}

public class Maze {
	private int n; // dimension of maze
	public static boolean done = false;
	private static Cell[][] cells;
	public static int tracedBack = 0;
	private int[] end;
	private int[] start;

	public Maze(int n, int[] end, int[] start, int randFactor, boolean display) {
		this.n = n;
		this.end = end;
		this.start = start;
		StdDraw.setXscale(0, n + 2);
		StdDraw.setYscale(0, n + 2);
		init();

		if (randFactor <= 0) {
			randFactor = 0;
		} else if (randFactor > 10) {
			randFactor = 10;
		}

		
		randFactor = (randFactor * n * n) / 14;
		if (display)
			System.out.println("Deleting " + randFactor + " walls.");
		generate(randFactor);
	}

	private void init() {
		// initialize border cells as already visited
		// visited = new boolean[n+2][n+2];

		cells = new Cell[n + 2][n + 2];
		for (int i = 0; i < n + 2; i++) {
			for (int j = 0; j < n + 2; j++) {
				cells[i][j] = new Cell((i == end[0] && j == end[1]), (i == 1 && j == 1), new int[] { i, j });
			}
		}

		for (int x = 0; x < n + 2; x++) {
			cells[x][0].visited = true;
			cells[x][n + 1].visited = true;
		}
		for (int y = 0; y < n + 2; y++) {
			cells[0][y].visited = true;
			cells[n + 1][y].visited = true;
		}
	}

	// generate the maze
	private void generate(int x, int y) {
		cells[x][y].visited = true;

		// while there is an unvisited neighbor
		while (!cells[x][y + 1].visited || !cells[x + 1][y].visited || !cells[x][y - 1].visited
				|| !cells[x - 1][y].visited) {

			while (true) {
				double r = StdRandom.uniform(4);
				if (r == 0 && !cells[x][y + 1].visited) {
					cells[x][y].north = false;
					cells[x][y + 1].south = false;
					generate(x, y + 1);
					break;
				} else if (r == 1 && !cells[x + 1][y].visited) {
					cells[x][y].east = false;
					cells[x + 1][y].west = false;
					generate(x + 1, y);
					break;
				} else if (r == 2 && !cells[x][y - 1].visited) {
					cells[x][y].south = false;
					cells[x][y - 1].north = false;
					generate(x, y - 1);
					break;
				} else if (r == 3 && !cells[x - 1][y].visited) {
					cells[x][y].west = false;
					cells[x - 1][y].east = false;
					generate(x - 1, y);
					break;
				}
			}
		}

	}

	// generate the maze starting from lower left
	private void generate(int randFactor) {
		generate(1, 1);

		Random rand = new Random(System.nanoTime());

		int counter = 0;

		while (counter < randFactor) {
			int x = 2 + rand.nextInt(n - 4);
			int y = 2 + rand.nextInt(n - 4);
			int choice = rand.nextInt(4);

			// System.out.println("Deleted for: " + x + ", " + y + ". Choice = " + choice);
			boolean original = true;

			switch (choice) {
			case 0:
				original = cells[x][y].north;
				cells[x][y].north = false;
				if (original)
					break;
			case 1:
				original = cells[x][y].east;
				cells[x][y].east = false;
				if (original)
					break;
			case 2:
				original = cells[x][y].west;
				cells[x][y].west = false;
				if (original)
					break;
			case 3:
				original = cells[x][y].south;
				cells[x][y].south = false;
				if (original)
					break;
				
			default:
				counter --;
			}

			counter++;

			// System.out.println();
		}

	}

	private void solve(int x, int y, Cell prevCell) {
		if (x == 0 || y == 0 || x == n + 1 || y == n + 1)
			return;
		if (done || cells[x][y].visited)
			return;
		cells[x][y].visited = true;
		cells[x][y].cameFrom = prevCell;

		StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
		StdDraw.show();
		StdDraw.pause(Globals.pauseTime);

		// reached middle
		if (x == end[0] && y == end[1])
			done = true;

		if (!cells[x][y].north)
			solve(x, y + 1, cells[x][y]);
		if (!cells[x][y].east)
			solve(x + 1, y, cells[x][y]);
		if (!cells[x][y].south)
			solve(x, y - 1, cells[x][y]);
		if (!cells[x][y].west)
			solve(x - 1, y, cells[x][y]);

		if (done) {
			return;
		}

		StdDraw.setPenColor(StdDraw.GRAY);
		StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
		StdDraw.show();
	}

	public void ResetMaze() {
		for (int x = 1; x <= n; x++)
			for (int y = 1; y <= n; y++) {
				cells[x][y].visited = false;
				cells[x][y].cameFrom = null;
				cells[x][y].visitedBy = null;

			}
		StdDraw.clear(StdDraw.WHITE);
		this.draw();
		done = false;
		Maze.tracedBack = 0;
	}

	// solve the maze starting from the start state
	public void solve() {
		for (int x = 1; x <= n; x++)
			for (int y = 1; y <= n; y++)
				cells[x][y].visited = false;
		done = false;
		solve(start[0], start[1], null);
	}

	public void parallelSolve() {
		for (int x = 1; x <= n; x++)
			for (int y = 1; y <= n; y++)
				cells[x][y].visited = false;
		done = false;
		Solver S1 = new Solver(cells, n, start, end, StdDraw.BOOK_RED);
		Solver S2 = new Solver(cells, n, end, start, StdDraw.BLUE);

		S1.start();
		S2.start();

		try {
			S1.join();
			S2.join();
		} catch (InterruptedException ex) {
			System.out.println("Interrupted! - " + ex.getMessage());
		}
	}

	public void MultiParallelSolve(int numThreads) {
		for (int x = 1; x <= n; x++)
			for (int y = 1; y <= n; y++)
				cells[x][y].visited = false;

		done = false;

		MultiSolver[] solvers = new MultiSolver[numThreads];
		Color colors[] = new Color[] { StdDraw.BOOK_RED, StdDraw.YELLOW, StdDraw.ORANGE, StdDraw.BLUE,
				StdDraw.BOOK_LIGHT_BLUE, StdDraw.CYAN, StdDraw.BLACK, StdDraw.DARK_GRAY, StdDraw.PINK };

		for (int i = 0; i < numThreads; i++) {
			int[] s = null, e = null;
			if (i == 0) {
				s = start;
				e = end;
			} else if (i == numThreads - 1) {
				s = end;
				e = start;
			}

			solvers[i] = new MultiSolver(cells, n, s, e, i, colors[i % colors.length]);
		}

		MultiSolver.InitializeBooleans(numThreads);

		for (int i = 0; i < numThreads; i++) {
			solvers[i].start();
		}

		try {
			for (int i = 0; i < numThreads; i++) {
				solvers[i].join();
			}
		} catch (InterruptedException ex) {
			System.out.println("Interrupted! - " + ex.getMessage());
		}
	}

	public void draw() {

		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.filledCircle(end[0] + 0.5, end[1] + 0.5, 0.375 * 2);
		StdDraw.filledCircle(start[0] + 0.5, start[1] + 0.5, 0.375 * 2);

		StdDraw.setPenColor(StdDraw.BLACK);
		for (int x = 1; x <= n; x++) {
			for (int y = 1; y <= n; y++) {
				if (cells[x][y].south)
					StdDraw.line(x, y, x + 1, y);
				if (cells[x][y].north)
					StdDraw.line(x, y + 1, x + 1, y + 1);
				if (cells[x][y].west)
					StdDraw.line(x, y, x, y + 1);
				if (cells[x][y].east)
					StdDraw.line(x + 1, y, x + 1, y + 1);

			}

		}
		StdDraw.show();
		// StdDraw.pause(1000);

	}

	public static void main(String[] args) {
		Scanner Sc = new Scanner(System.in);

		long startTime, endTime, runTime;
		Random rand = new Random(System.nanoTime());
		int numRuns = 1;
		StdDraw.enableDoubleBuffering();


		System.out.println(String.format("%-15s\t|\t%-15s\t|\t%-15s\t|\t%-15s", "Sparsity", "Maze Size", "Num Threads", "Time"));
		System.out.println(new String(new char[15 * 4 + 9 * 3]).replace("\0", "-"));

		for (int randFactor = 10; randFactor >= 0; randFactor -= 10) {
			for (int mazeDimension = 50; mazeDimension < 55; mazeDimension+=5) {

				for (int numThreads = 6; numThreads < 7; numThreads++) {
					
					System.out.println(randFactor + "\t" + mazeDimension + "\t" + numThreads);
					
					if (numThreads == 1) {
						for (int run=0; run<numRuns; run++) {
							
							int[] endCell = new int[] { rand.nextInt((mazeDimension / 2 - 3) + 1) + mazeDimension / 2,
									rand.nextInt((mazeDimension / 2 - 3) + 1) + mazeDimension / 2 };
							int[] startCell = new int[] { 1, 1 };
							Maze maze = new Maze(mazeDimension, endCell, startCell, randFactor, run==0);
							StdDraw.clear();
							maze.draw();
							
							startTime = System.nanoTime();
							maze.solve();
							Maze.TraceBack(cells[endCell[0]][endCell[1]], 1000);
							endTime = System.nanoTime();
	
							runTime = (endTime - startTime) / 1000000;
							
							DataEntry entry = new DataEntry(randFactor, mazeDimension, numThreads, runTime);
							DataEntry.SaveToFile(entry);
							
							maze.ResetMaze();
						}

					} else {

						for (int run = 0; run < numRuns; run++) {
							
							
							int[] endCell = new int[] { rand.nextInt((mazeDimension / 2 - 3) + 1) + mazeDimension / 2,
									rand.nextInt((mazeDimension / 2 - 3) + 1) + mazeDimension / 2 };
							int[] startCell = new int[] { 1, 1 };
							Maze maze = new Maze(mazeDimension, endCell, startCell, randFactor, run==0);
							StdDraw.clear();
							maze.draw();
							
							StdDraw.pause(5000);
							
							startTime = System.nanoTime();
							maze.MultiParallelSolve(numThreads);
							try {
								MultiSolver.TRACE();
							} catch (NullPointerException ex) {
								System.out.println("NULL EXCEPTION DISCARDING");
								run--;
								continue;
							}
							endTime = System.nanoTime();
							
							runTime = ((endTime - startTime) / (1000000));
							
							DataEntry entry = new DataEntry(randFactor, mazeDimension, numThreads, runTime);
							DataEntry.SaveToFile(entry);
							
							StdDraw.pause(5000);

							maze.ResetMaze();
						}

					}
					
					

					
				}
				
			}
		}

		System.out.println(new String(new char[15 * 4 + 9 * 3]).replace("\0", "-"));

		/*
		 * 
		 * 
		 * for (DataEntry D : DATA) { if (D.numThreads < prevNumThreads)
		 * System.out.println();
		 * 
		 * System.out.println(D); prevNumThreads = D.numThreads;
		 * 
		 * }
		 * 
		 */

		/*
		 * 
		 * System.out.print("Enter size of maze: "); int n = Sc.nextInt();
		 * 
		 * System.out.print("Enter sparsity factor: "); int sparsityFactor =
		 * Sc.nextInt();
		 * 
		 * Random rand = new Random(System.nanoTime()); int[] endCell = new int[] {
		 * rand.nextInt((n - n/2 - 3) + 1) + n/2, rand.nextInt((n - n/2 - 3) + 1) + n/2
		 * }; int[] startCell = new int[] { 1, 1 }; long startTime, endTime;
		 * 
		 * System.out.println("\nRandomly generated end cell at coordinates - (" +
		 * endCell[0] + ", " + endCell[1] + ")");
		 * 
		 * 
		 * 
		 * 
		 * Maze maze = new Maze(n, endCell, startCell, sparsityFactor);
		 * StdDraw.enableDoubleBuffering(); maze.draw();
		 * 
		 * StdDraw.pause(500);
		 * 
		 * startTime = System.nanoTime(); maze.MultiParallelSolve(4);
		 * MultiSolver.TRACE(); endTime = System.nanoTime();
		 * 
		 * System.out.println("Multi Parallel execution took " +
		 * (endTime-startTime)/1000000 + " milliseconds.");
		 * 
		 * 
		 * StdDraw.show();
		 * 
		 * StdDraw.pause(10000);
		 * 
		 * 
		 * maze.ResetMaze();
		 * 
		 * 
		 * 
		 * startTime = System.nanoTime(); maze.MultiParallelSolve(2);
		 * MultiSolver.TRACE(); endTime = System.nanoTime();
		 * System.out.println("Double Parallel execution took " +
		 * (endTime-startTime)/1000000 + " milliseconds.");
		 * 
		 * 
		 * 
		 * StdDraw.show(); StdDraw.pause(1000);
		 * 
		 * 
		 * 
		 * maze.ResetMaze();
		 * 
		 * 
		 * startTime = System.nanoTime(); maze.solve();
		 * Maze.TraceBack(cells[endCell[0]][endCell[1]], 1); endTime =
		 * System.nanoTime();
		 * 
		 * System.out.println("Serial execution took " + (endTime-startTime)/1000000 +
		 * " milliseconds.");
		 * 
		 * StdDraw.show(); Sc.close();
		 */
		Sc.close();
		System.out.println("Execution Completed!");

	}

	public static void TraceBack(Cell cell, int maxTraceBacks) {
		if (cell == null)
			return;

		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.filledCircle(cell.coords[0] + 0.5, cell.coords[1] + 0.5, 0.375);
		if (Maze.tracedBack >= maxTraceBacks) {
			return;
		}
		Maze.tracedBack++;

		while (cell != null) {
			StdDraw.setPenColor(StdDraw.GREEN);
			StdDraw.filledCircle(cell.coords[0] + 0.5, cell.coords[1] + 0.5, 0.375 / 2);
			cell = cell.cameFrom;
			StdDraw.show();
			StdDraw.pause(Globals.pauseTime);
		}

		StdDraw.show();
		StdDraw.pause(Globals.pauseTime);

	}

}

class DataEntry {
	int mazeDimension;
	int numThreads;
	int sparsity;
	long timeToComplete;

	DataEntry(int r, int m, int t, long l) {
		sparsity = r;
		mazeDimension = m;
		numThreads = t;
		timeToComplete = l;
	}

	@Override
	public String toString() {
		return String.format("%-15d\t|\t%-15d\t|\t%-15d\t|\t%-15s", sparsity, mazeDimension, numThreads,
				timeToComplete);
	}

	public static void SaveToFile(DataEntry entry) {
		
		String sb = new String();

		sb += entry.sparsity + ",";
		sb += entry.mazeDimension + ",";
		sb += entry.numThreads + ",";
		sb += entry.timeToComplete + "\n";

		try (FileOutputStream fos = new FileOutputStream("Results.txt", true)) {
			OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			BufferedWriter writer = new BufferedWriter(osw);
			writer.write(sb);
			
			writer.close();
			osw.close();
			
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException ex) {
			System.out.println("IOException: " + ex.getMessage());
		}
		

	}
}
