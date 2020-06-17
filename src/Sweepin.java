import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Sweepin{
	private static boolean inUse;
	private static int numRequests = 0;
	private static RenderLoop rendererThing;
	public static Scanner std;
	private static Game myGame;
	private static Player p1;
	public static void main(String[] args){
		std = new Scanner(System.in);
		inUse = false;
		JFrame jf = new JFrame();
		jf.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		jf.setSize(jf.getWidth(), 4 * jf.getHeight() / 5);
		jf.setLocation(0, 0);
		//jf.setUndecorated(true);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel jp = new JPanel();
		jf.add(jp);
		jp.setBackground(Color.white);
		jp.updateUI();
		myGame = new Game(30, 16, 99);
		p1 = new ArtificialIntelligence(myGame);
		jp.addMouseListener(p1);
		delay(2000);
		Thread analysis = new Thread(p1);
		analysis.start();
		rendererThing = new RenderLoop(jp, p1);
		Thread renderer = new Thread(rendererThing);
		renderer.start();
	}
	public static void reset(){
		myGame = new Game(30, 16, 99);
		p1.reset(myGame);
	}
	
	public static void delay(long amount){
		try {
			Thread.sleep(amount);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static BigInteger nCk(int n, int k){
		if(k > n - k){
			k = n - k;
		}
		BigInteger result = new BigInteger("1");
		for(int i = 1; i <= k; i++){
			result = result.multiply(new BigInteger(Integer.toString(n - k + i)));
			result = result.divide(new BigInteger(Integer.toString(i)));
		}
		return result;
	}
	
	public static void lockProcess(){
		numRequests--;
		inUse = true;
	}
	public static void releaseProcess(){
		inUse = false;
		if(numRequests > 0){
			delay(11);
		}
	}
	public static void delay(){
		numRequests++;
		while(inUse){
			delay(10);
		}
	}
	public static void updateImage(){
		rendererThing.renderStep();
	}
	public static class RenderLoop implements Runnable{
		
		private JPanel thePanel;
		private Player thePlayer;
		
		public RenderLoop(JPanel j, Player p){
			thePanel = j;
			thePlayer = p;
		}

		@Override
		public void run() {
			while(true){
				delay();
				lockProcess();
				renderStep();
				releaseProcess();
				delay(20);
			}
		}
		public void renderStep(){
			Graphics g = thePanel.getGraphics();
			int width = thePanel.getWidth();
			int height = thePanel.getHeight();
			BufferedImage render = myGame.getRendering(width, height);
			Graphics gg = render.getGraphics();
			for(Thought t: thePlayer.generateThoughts(myGame.getCellSize())){
				t.paint(gg, myGame.convertGridCoordinates(t.getCoordinates()));
			}
			g.drawImage(render, 0, 0, null);
		}
		
	}
}