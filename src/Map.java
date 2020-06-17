public class Map{
	private char[][] world;
	public Map(int width, int height){
		this.world = new char[width][height];
		for(int i = 0; i < world.length; i++){
			for(int j = 0; j < world[i].length; j++){
				setAt(i, j, '?');
			}
		}
	}
	
	public void setAt(int x, int y, char value){
		world[x][y] = value;
	}
	
	public char getAt(int x, int y){
		return world[x][y];
	}
	
	public boolean plausibleNumBombs(int x, int y){
		int numBombs = getAt(x, y) - '0';
		if(numBombs < 0 || numBombs > 8){
			System.out.println("SERIOUS TROUBLE - THERE'S AN ERROR HERE");
		}
		int minBombs = 0;
		int maxBombs = 0;
		for(int i = x - 1; i <= x + 1; i++){
			for(int j = y - 1; j <= y + 1; j++){
				if(i == x && j == y){
					continue;
				}
				if(i < 0 || i >= world.length || j < 0 || j >= world[i].length){
					continue;
				}
				char c = getAt(i, j);
				if(c == 'B'){
					minBombs++;
					maxBombs++;
				}
				if(c == '?'){
					maxBombs++;
				}
			}
		}
		return minBombs <= numBombs && numBombs <= maxBombs;
	}
}