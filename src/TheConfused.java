import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TheConfused extends Player{
	private Game myGame;
	private Point myNextMove;
	
	public TheConfused(Game theGame){
		myGame = theGame;
	}

	public void reset(Game theGame){
		myNextMove = null;
		myGame = theGame;
	}
	
	@Override
	public void run() {
		while(true){
			
			if(myNextMove != null){
				myGame.clickSpot(myNextMove.x, myNextMove.y);
			}
			
			int ranX = (int) (Math.random() * myGame.getWidth());
			int ranY = (int) (Math.random() * myGame.getHeight());
			
			myNextMove = new Point(ranX, ranY);
			
			Sweepin.delay(1000);
		}
	}

	@Override
	public ArrayList<Thought> generateThoughts(int size) {
		ArrayList<Thought> myThought = new ArrayList<Thought>();
		
		if(myNextMove != null){
			BufferedImage nova = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) nova.getGraphics();
			
			g.setColor(new Color(0, 255, 120));
			g.fillRect((int) (0.2 * size), (int) (0.2 * size), (int) (0.6 * size), (int) (0.6 * size));
			
			myThought.add(new Thought(myNextMove, nova));
		}
		return myThought;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}