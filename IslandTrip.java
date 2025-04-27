import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

public class IslandTrip {
	
	
	/**
	 * Read lines one at a time from r.  Discover "islands" of 1s in the input grid.
	 * Read the specification to see where "bridges" (i.e., edges) exist between islands.
	 * Find the length of the shortest path from the smallest island to the biggest 
	 * island.
	 * 
	 * Ouput should be the size of each island, in sorted order, separated by newline characters.
	 * The last line should be the length of the shortest path from the smallest island to the largest.
	 * 
	 * @param r the reader to read from
	 * @param w the writer to write to
	 * @throws IOException
	 */
	public static void doIt(BufferedReader r, PrintWriter w) throws IOException {
	

//		List<Integer> islands = new ArrayList<>();
		TreeSet<Integer> islands = new TreeSet<>();
		List<String> inputGrid = new ArrayList<>();
//		Map<String, String> bridges = new HashMap<>();
		Map<Integer, List<Integer>> bridges = new HashMap<>();
		boolean[][] visited;

		int pathLength = -1;
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			inputGrid.add((line));
		}
		int inputGridRows= inputGrid.size();
		int inputGridCols= inputGrid.get(0).length();


		//Boolean Array of Visited Vertices
		visited = new boolean[inputGridRows][inputGridCols];

		sizeOfAllIslands(islands, inputGrid, visited);

		constructGraph(islands, bridges);
		graphBridges(islands, bridges);

		pathLength = shortestPathLength(bridges, islands);

		if (islands.size() == 1) {
			w.println(islands.first());
			w.println(0);
			return;
		}

		if (islands.isEmpty()) {
//			throw new IOException();
			return;
		}

	

		 for (int i: islands){
			w.println(i);
		}
		
		w.println("Shortest Path Length: " + pathLength);


		
	}

	public static int dfsSizeOfOneIsland(List<String> inputGrid,boolean[][] visited,int x, int y){
		int rows= inputGrid.size();
		int cols= inputGrid.get(0).length();
		int size = 0;

//		Stack<int[]> s = new Stack<>(); //lifo
//		s.push(new int[]{x, y});
		Queue<int[]> s = new LinkedList<>(); //lifo
		s.add(new int[]{x, y});
		visited[x][y] = true;
		while (!s.isEmpty()) {
//			int[] index = s.pop();
			int[] index = s.remove();
			int currX = index[0];
			int currY = index[1];
			size++;

			//Similar logic as comp 2801 with robot search algorithm
			//assuming turn direction ur forward, backwards, left right, and the upper and lower diagonals
			//+ and - following robot controls for motor power for turn directions
			for (int turnDirection = 0; turnDirection < 8; turnDirection++) {
				int newX = currX;
				int newY = currY;


				switch (turnDirection) {
					case 0: //FORWARD
						newX = currX - 1;
						newY = currY;
						break;
					case 1: //BACKWARD
						newX = currX + 1;
						newY = currY;
						break;
					case 2: //LEFT
						newX = currX;
						newY = currY - 1;
						break;
					case 3: //RIGHT
						newX = currX;
						newY = currY + 1;
						break;
					case 4: //BACKWARD-RIGHT
						newX = currX + 1;
						newY = currY + 1;
						break;
					case 5: //BACKWARD-LEFT
						newX = currX + 1;
						newY = currY - 1;
						break;
					case 6: //FORWARD-RIGHT
						newX = currX - 1;
						newY = currY + 1;
						break;
					case 7: //FORWARD-LEFT
						newX = currX - 1;
						newY = currY - 1;
						break;

				}
				if (newX >= 0 && newX < rows && newY >= 0 && newY < cols){ //
					//row number
					String row = inputGrid.get(newX);
					if (!visited[newX][newY] && row.charAt(newY) ==  '1'){
//						s.push(new int[]{newX, newY});
						s.add(new int[]{newX, newY});
						visited[newX][newY] = true;
					}
				}
			}
		}
		return size;
	}

	public static void sizeOfAllIslands(TreeSet<Integer> islands, List<String> inputGrid, boolean[][] visited){
		int rows= inputGrid.size();
		int cols= inputGrid.get(0).length();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String row = inputGrid.get(i);
				if (!visited[i][j] && row.charAt(j) ==  '1'){
					int islandSize = dfsSizeOfOneIsland(inputGrid, visited, i, j);
					islands.add(islandSize);
				}
			}
		}
	}

	//minimum number of bridges that must be crossed in order to go from the smallest island to the largest
	public static int shortestPathLength(Map<Integer, List<Integer>> bridges,  TreeSet<Integer> islands){
		//similar to ods bfs
		int smallest = islands.first();
		int largest = islands.last();

		if (smallest == largest){
			return 0;
		}

		Map<Integer, Integer> pathLengths = new HashMap<>();
		Queue<Integer> q = new LinkedList<Integer>();

		q.add(smallest);
		pathLengths.put(smallest, 0);

		while (!q.isEmpty()) {
			int i = q.remove();
			int currPath = pathLengths.get(i);

			for(int j : bridges.get(i)){
				if (!pathLengths.containsKey(j)) { //check whether a neighbor of i is NOT being mapped into the HashMap of all the nistances
					pathLengths.put(j, currPath + 1);
					q.add(j);

					if (j == largest){ //last node
						return currPath + 1;
					}
				}

			}

		}

		return -1;
	}

	public static void constructGraph(TreeSet<Integer> islands, Map<Integer, List<Integer>> bridges){
		for (int size : islands) {
			bridges.put(size, new ArrayList<>());
		}
	}

	public static void graphBridges(TreeSet<Integer> islandsSet, Map<Integer, List<Integer>> bridges){
		List<Integer> islands = new ArrayList<>(islandsSet);
		for (int i = 0; i < islands.size(); i++) {
			int currIsland = islands.get(i);

			if (i < islands.size() - 1) { //if the last island
				bridges.get(currIsland).add(islands.get(i + 1));//add bridge to the next (i+1)
			}
			if (i > 0) { //if not the first
				bridges.get(currIsland).add(islands.get(i - 1));//add bridge to the prev (i-1)
			}

			for (int j = i+1; j < islands.size(); j++) {
				int notI = islands.get(j);
				if (digitSum(notI) == digitSum(currIsland)) {
					bridges.get(currIsland).add(notI);
				}
			}
		}
	}

	public static int digitSum(int size){
		int sum=0;
		while (size > 0) {
			sum = sum + size % 10;
			size = size / 10;
		}
		return sum;
	}





	/**
	 * The driver.  Open a BufferedReader and a PrintWriter, either from System.in
	 * and System.out or from filenames specified on the command line, then call doIt.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedReader r;
			PrintWriter w;
			if (args.length == 0) {
				r = new BufferedReader(new InputStreamReader(System.in));
				w = new PrintWriter(System.out);
			} else if (args.length == 1) {
				r = new BufferedReader(new FileReader(args[0]));
				w = new PrintWriter(System.out);				
			} else {
				r = new BufferedReader(new FileReader(args[0]));
				w = new PrintWriter(new FileWriter(args[1]));
			}
			long start = System.nanoTime();
			doIt(r, w);
			w.flush();
			long stop = System.nanoTime();
			System.out.println("Execution time: " + 1e-9 * (stop-start));
		} catch (IOException e) {
			System.err.println(e);
			System.exit(-1);
		}
	}
}
