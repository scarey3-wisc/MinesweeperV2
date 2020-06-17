import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;

public class Human extends Player{
	private LinkedList<Point> taggedThings;
	private Game myGame;
	public Human(Game theGame){
		myGame = theGame;
		taggedThings = new LinkedList<Point>();
	}
	public void reset(Game theGame){
		myGame = theGame;
		taggedThings = new LinkedList<Point>();
	}
	public ArrayList<Thought> generateThoughts(int size){
		ArrayList<Thought> myThoughts = new ArrayList<Thought>();
		for(Point p: taggedThings){
			BufferedImage nova = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) nova.getGraphics();
			
			g.translate(size/2, size/2);
			g.rotate(Math.PI/4);
			g.setColor(Color.red);
			g.fillRect((int) (-0.1 * size), (int) (-0.4 * size), (int) (0.2 * size), (int) (0.8 * size));
			g.fillRect((int) (-0.4 * size), (int) (-0.1 * size), (int) (0.8 * size), (int) (0.2 * size));
			
			myThoughts.add(new Thought(p, nova));
		}
		return myThoughts;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Sweepin.delay();
		Sweepin.lockProcess();
		int x = e.getX();
		int y = e.getY();
		Point p = myGame.convertScreenCoordinates(x, y);
		if(e.getButton() == 1){
			myGame.clickSpot(p.x, p.y);
		}else if(e.getButton() == 3){
			if(p.x >= 0 && p.x < myGame.getWidth() && p.y >= 0 && p.y < myGame.getHeight()){
				Point found = null;
				for(Point t: taggedThings){
					if(t.x == p.x && t.y == p.y){
						found = t;
						break;
					}
				}
				if(found != null){
					taggedThings.remove(found);
				}else if(myGame.isUnknown(p)){
					taggedThings.add(p);
				}
			}
		}
		for(int i = 0; i < taggedThings.size(); i++){
			if(!myGame.isUnknown(taggedThings.get(i))){
				taggedThings.remove(i);
				i--;
			}
		}
		Sweepin.releaseProcess();
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void run() {}
}