package Pac;
import javax.swing.JFrame;


public class PacPac extends JFrame {
	
	public PacPac() {
		add(new Model());
	}
	
	
	public static void main(String[] args) {
		PacPac pac = new PacPac();
		pac.setVisible(true);
		pac.setTitle("Pacman");
		pac.setSize(380,420);
		pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
		pac.setLocationRelativeTo(null);
		
	}


}
