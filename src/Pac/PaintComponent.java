package Pac;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class PaintComponent extends JFrame {
	PaintComponent(Graphics g,Dimension d, boolean inGame){
		super.paintComponents(g);
		Graphics2D g2d= (Graphics2D) g;
		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, d.width, d.height);
		drawMaze(g2d);
		drawScore(g2d);
		
		if(inGame) {
			playGame(g2d);
		}
		else {
			showIntroScreen(g2d);
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
	private void drawMaze(Graphics2D g2d){
		
	}
	
	private void drawScore(Graphics2D g2d){
		
	}
	
	private void playGame(Graphics2D g2d){
		if(dying) {
			death();
		}
		else {
			movePac();
			drawPac(g2d);
			moveEnemies(g2d);
			checkMaze();
		}
	}
	
	private void showIntroScreen(Graphics2D g2d){
		
	}
	
}
