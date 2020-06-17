import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class Thought{
	private BufferedImage result;
	private Point coordinate;
	public Thought(Point c, BufferedImage r){
		coordinate = c;
		result = r;
	}
	public Point getCoordinates(){
		return coordinate;
	}
	public void paint(Graphics g, Point p){
		g.drawImage(result, p.x, p.y, null);
	}
}