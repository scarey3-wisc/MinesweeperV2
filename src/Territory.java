import java.awt.Point;
import java.util.LinkedList;

public class Territory{
	private char[][] world;
	private int numBombs;
	public Territory(int width, int height, int numBombs){
		this.numBombs = numBombs;
		this.world = new char[width][height];
		LinkedList<Point> allPoints = new LinkedList<Point>();
		for(int i = 0; i < world.length; i++){
			for(int j = 0; j < world[i].length; j++){
				allPoints.add(new Point(i, j));
			}
		}
		
		for(int i = 0; i < numBombs; i++){
			setAt(allPoints.remove((int) (Math.random() * allPoints.size())), 'B');
		}
		
		while(!allPoints.isEmpty()){
			calculateAt(allPoints.removeFirst());
		}
	}
	
	public Territory(int width, int height, int numBombs, int x, int y){
		this.numBombs = numBombs;
		this.world = new char[width][height];
		LinkedList<Point> allPoints = new LinkedList<Point>();
		for(int i = 0; i < world.length; i++){
			for(int j = 0; j < world[i].length; j++){
				if(x != i || y != j){
					allPoints.add(new Point(i, j));
				}
			}
		}
		
		for(int i = 0; i < numBombs; i++){
			setAt(allPoints.remove((int) (Math.random() * allPoints.size())), 'B');
		}
		
		while(!allPoints.isEmpty()){
			calculateAt(allPoints.removeFirst());
		}
		calculateAt(new Point(x, y));
	}
	
	private void calculateAt(Point p){
		int totalBombs = 0;
		for(int i = p.x - 1; i <= p.x + 1; i++){
			for(int j = p.y - 1; j <= p.y + 1; j++){
				if(i >= 0 && i < world.length && j >= 0 && j < world[i].length){
					if((i != p.x || j != p.y) && world[i][j] == 'B'){
						totalBombs++;
					}
				}
			}
		}
		if(world[p.x][p.y] != -1){
			world[p.x][p.y] = (char) ('0' + totalBombs);
		}
	}
	private void setAt(Point p, char value){
		world[p.x][p.y] = value;
	}
	
	public char getAt(int x, int y){
		return world[x][y];
	}
	public int getNumBombs(){
		return numBombs;
	}
}