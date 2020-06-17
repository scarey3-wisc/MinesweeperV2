import java.awt.event.MouseListener;
import java.util.ArrayList;

public abstract class Player implements MouseListener, Runnable{
	public abstract ArrayList<Thought> generateThoughts(int size);
	public abstract void reset(Game theGame);
}