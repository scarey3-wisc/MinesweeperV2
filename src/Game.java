import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

public class Game{
	private Territory world;
	private Map model;
	private int height;
	private int width;
	private int numBombs;
	private int screenWidth;
	private int screenHeight;
	private int cellSize;
	private Point gridStart;
	private int gameValue;
	public Game(int width, int height, int numBombs){
		//world = new Territory(width, height, numBombs);
		model = new Map(width, height);
		this.height = height;
		this.width = width;
		this.numBombs = numBombs;
		gameValue = 0;
	}
	
	public BufferedImage getRendering(int sWidth, int sHeight){
		screenWidth = sWidth;
		screenHeight = sHeight;
		cellSize = screenHeight / height;
		if(screenWidth / width < cellSize){
			cellSize = screenWidth / width;
		}
		if(cellSize > 30){
			cellSize = 30;
		}
		gridStart = new Point();
		gridStart.x = (screenWidth - width * cellSize)/2;
		gridStart.y = (screenHeight - height * cellSize)/2;
		if(gridStart.y > 30){
			gridStart.y = 30;
		}
		BufferedImage canvas = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, screenWidth, screenHeight);
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				char c = model.getAt(i, j);
				
				g.setColor(new Color(220, 220, 220));
				g.fill3DRect(gridStart.x + i * cellSize, gridStart.y + j * cellSize
						, cellSize, cellSize, true);
				String paint = new String(new char[]{c});
				g.setFont(new Font(Font.MONOSPACED, Font.BOLD, (int) (0.8 * cellSize)));
				int sw = g.getFontMetrics().stringWidth(paint);
				int sh = g.getFontMetrics().getHeight();
								
				if(c == '1'){
					g.setColor(new Color(30, 30, 255));
					g.drawString(paint, 
							gridStart.x + i * cellSize + cellSize / 2 - sw/2, 
							gridStart.y + j * cellSize + cellSize / 2 + sh/4);
				}else if(c == '2'){
					g.setColor(new Color(30, 180, 70));
					g.drawString(paint, 
							gridStart.x + i * cellSize + cellSize / 2 - sw/2, 
							gridStart.y + j * cellSize + cellSize / 2 + sh/4);
				}else if(c == '3'){
					g.setColor(new Color(255, 0, 0));
					g.drawString(paint, 
							gridStart.x + i * cellSize + cellSize / 2 - sw/2, 
							gridStart.y + j * cellSize + cellSize / 2 + sh/4);
				}else if(c == '4'){
					g.setColor(new Color(60, 10, 140));
					g.drawString(paint, 
							gridStart.x + i * cellSize + cellSize / 2 - sw/2, 
							gridStart.y + j * cellSize + cellSize / 2 + sh/4);
				}else if(c == '5'){
					g.setColor(new Color(160, 40, 0));
					g.drawString(paint, 
							gridStart.x + i * cellSize + cellSize / 2 - sw/2, 
							gridStart.y + j * cellSize + cellSize / 2 + sh/4);
				}else if(c == '6'){
					g.setColor(new Color(100, 0, 100));
					g.drawString(paint, 
							gridStart.x + i * cellSize + cellSize / 2 - sw/2, 
							gridStart.y + j * cellSize + cellSize / 2 + sh/4);
				}else if(c == '7'){
					g.setColor(new Color(100, 100, 0));
					g.drawString(paint, 
							gridStart.x + i * cellSize + cellSize / 2 - sw/2, 
							gridStart.y + j * cellSize + cellSize / 2 + sh/4);
				}else if(c == '8'){
					g.setColor(new Color(0, 0, 0));
					g.drawString(paint, 
							gridStart.x + i * cellSize + cellSize / 2 - sw/2, 
							gridStart.y + j * cellSize + cellSize / 2 + sh/4);
				}else if(c == '?'){
					g.setColor(new Color(200, 200, 200));
					g.fill3DRect(gridStart.x + i * cellSize, gridStart.y + j * cellSize
							, cellSize, cellSize, true);
					
					g.setColor(new Color(240, 240, 240));
					int x = gridStart.x + i * cellSize;
					int y = gridStart.y + j * cellSize;
					int[] xs = new int[]{(int)(x),(int)(x+cellSize),(int)(x+0.85*cellSize),
							(int)(x+0.15 * cellSize),(int)(x+0.15 * cellSize), (int)(x)};
					int[] ys = new int[]{(int)(y),(int)(y),(int)(y+0.15*cellSize),
							(int)(y+0.15*cellSize),(int)(y+0.85*cellSize),(int)(y+cellSize)};
					int n = 6;
					g.fill(new Polygon(xs, ys, n));
					
					g.setColor(new Color(160, 160, 160));
					x = gridStart.x + (i+1) * cellSize;
					y = gridStart.y + (j+1) * cellSize;
					xs = new int[]{(int)(x),(int)(x-cellSize),(int)(x-0.9*cellSize),
							(int)(x-0.1 * cellSize),(int)(x-0.1 * cellSize), (int)(x)};
					ys = new int[]{(int)(y),(int)(y),(int)(y-0.1*cellSize),
							(int)(y-0.1*cellSize),(int)(y-0.9*cellSize),(int)(y-cellSize)};
					n = 6;
					g.fill(new Polygon(xs, ys, n));
				}
				
				
				
				g.setColor(new Color(130, 130, 130));
				g.drawRect(gridStart.x + i * cellSize, gridStart.y + j * cellSize
						, cellSize, cellSize);
			}
		}
		
		String paint = "";
		if(gameValue == 1){
			paint = "YOU FOUND ALL THE BOMBS! YAY!!!";
		}else if(gameValue == -1){
			paint = "YOU DETONATED A BOMB: GGEZ NOOB";
		}
		
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, screenHeight/15));
		g.setColor(Color.black);
		int sw = g.getFontMetrics().stringWidth(paint);
		int sh = g.getFontMetrics().getHeight();
		
		g.drawString(paint, screenWidth/2 - sw/2, cellSize * height + (screenHeight - cellSize * height)/2 + sh/4);
		
		return canvas;
	}
	
	private void revealFromZeroes(int x, int y){
		LinkedList<Point> popList = new LinkedList<Point>();
		for(int i = x - 1; i <= x + 1; i++){
			for(int j = y - 1; j <= y + 1; j++){
				if(i >= 0 && i < width && j >= 0 && j < height){
					if(i != x || j != y){
						if(model.getAt(i, j) == '?'){
							model.setAt(i, j, world.getAt(i, j));
							if(world.getAt(i, j) == '0'){
								popList.addFirst(new Point(i, j));
							}
						}
					}
				}
			}
		}
		while(!popList.isEmpty()){
			Point p = popList.removeFirst();
			revealFromZeroes(p.x, p.y);
		}
	}
	
	public boolean hasInfo(Point p){
		for(int i = p.x - 1; i <= p.x + 1; i++){
			for(int j = p.y - 1; j <= p.y + 1; j++){
				if(i == p.x && j == p.y){
					continue;
				}
				if(i < 0 || i >= width || j < 0 || j >= height){
					continue;
				}
				if(model.getAt(i, j) != '?'){
					return true;
				}
			}
		}
		return false;
	}
	public boolean isNumber(Point p){
		return 0 <=  model.getAt(p.x, p.y) - '0' && model.getAt(p.x, p.y) - '0' <= 8;
	}
	public boolean isUnknown(Point p){
		return model.getAt(p.x, p.y) == '?';
	}
	
	public ArrayList<Point> getAllUnknown(){
		ArrayList<Point> alp = new ArrayList<Point>();
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(model.getAt(i, j) == '?'){
					alp.add(new Point(i, j));
				}
			}
		}
		return alp;
	}
	
	public Point convertScreenCoordinates(int x, int y){
		int newX = (x - gridStart.x) / cellSize;
		int newY = (y - gridStart.y) / cellSize;
		return new Point(newX, newY);
	}
	public Point convertGridCoordinates(Point p){
		int newX = gridStart.x + p.x * cellSize;
		int newY = gridStart.y + p.y * cellSize;
		return new Point(newX, newY);
	}
	
	public char getKnowledge(int x, int y){
		return model.getAt(x, y);
	}
	
	public int clickSpot(int x, int y){
		
		if(world == null){
			world = new Territory(width, height, numBombs, x, y);
		}
		
		if(gameValue != 0){
			return -1;
		}
		
		if(x >= 0 && x < width && y >= 0 && y < height){
			char c = model.getAt(x, y);
			if(c == '?'){
				char f = world.getAt(x, y);
				if(f == 'B'){
					System.out.println("YOU HAVE DETONATED A BOMB");
					gameValue = -1;
					Sweepin.updateImage();
					Sweepin.delay(1000);
					Sweepin.reset();
					return -1;
				}
				
				model.setAt(x, y, f);
				
				if(f == '0'){
					revealFromZeroes(x, y);
				}
				Sweepin.updateImage();
				checkForVictory();
				return 1;
			}
		}
		return 0;
	}
	public void checkForVictory(){
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(model.getAt(i, j) == '?' && world.getAt(i, j) != 'B'){
					return;
				}
			}
		}
		System.out.println("YOU WON!!!! CONGRATULATIONS!!!!");
		gameValue = 1;
		Sweepin.updateImage();
		Sweepin.delay(1000);
		Sweepin.reset();
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public int getCellSize(){
		return cellSize;
	}
}