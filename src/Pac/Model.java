package Pac;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel{
	
	//initializing the required variables
	
	private Dimension d; //height and width of the playing field
	private final Font smallFont=new Font("Arial",Font.BOLD,14); //setting font for the text to be displayed
	private boolean inGame= false; //to check if game is ongoing or not
	private boolean dying= false; //check to know if Pac is Alive
	private int bestScore=0;
	private int currentScore=0;
	
	private final int BLOCK_SIZE= 24; //describes how big blocks in the game
	private final int N_BLOCK = 15; //tells no of blocks
	private final int SCREEN_SIZE= N_BLOCK*BLOCK_SIZE;
	private final int MAX_ENEMIES=12;
	private final int PAC_SPEED=6;
	
	private int N_ENEMIES=6; //no. of enemies at beginning is set to six
	private int lives, score; //store the no. of lives left and current score respectively
	private int[] dx, dy; //tells the position of the enemies
	private int[] ghost_x ,ghost_y ,ghost_dx, ghost_dy , ghostSpeed; // to determine the no. and position of the ghost

	private Image heart, ghost;
	private Image up,down,left,right;
	
	private int pac_x,pac_y; //coordiantes of pac
	private int pac_dx,pac_dy; // delta changes in horizontal and vertical directions
	private int req_dx, req_dy; // are determined in TAdapter in our class(keyboard)
	
	private final int validSpeeds[]= {1,2,3,4,6,8}; //an array if valid speed
	private final int maxSpeed=6;
	
	private int currentSpeed=1;
	private short[] screenData;// will later take data from lvlData to redraw the game
	private Timer timer;
	
	
	// we have here 255 no. which tells the 255 possible positions in the Game 15 per row and column
	// 0- Obstacle
	// 1:left border 2:top border 4:right border 8:bottom border 
	// 16- white dots that pac connects
	// we add the numbers to tell the condition of the block
	//eg- 19(the first block) -> 1(left border)+2(top border)+16(white dot) = 19
	
	private final short levelData[] = {
	    	19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
	        17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
	        25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
	        0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
	        19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
	        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
	        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
	        17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
	        21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
	        17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
	        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
	        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
	        25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
	    };
	
	//constructor
	public Model() {
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }
	
	//loading all the required images
	private void loadImages(){
		down = new ImageIcon(Model.class.getResource("assets/down.gif")).getImage();
    	up = new ImageIcon(Model.class.getResource("assets/up.gif")).getImage();
    	left = new ImageIcon(Model.class.getResource("assets/left.gif")).getImage();
    	right = new ImageIcon(Model.class.getResource("assets/right.gif")).getImage();
        ghost = new ImageIcon(Model.class.getResource("assets/ghost.gif")).getImage();
        heart = new ImageIcon(Model.class.getResource("assets/heart.png")).getImage();
	}
	
	private void initVariables() {

        screenData = new short[N_BLOCK * N_BLOCK];
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_ENEMIES];
        ghost_dx = new int[MAX_ENEMIES];
        ghost_y = new int[MAX_ENEMIES];
        ghost_dy = new int[MAX_ENEMIES];
        ghostSpeed = new int[MAX_ENEMIES];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, (e)-> repaint());
        timer.start();
    }
	
	private void playGame(Graphics2D g2d) {

        if (dying) {

            death();

        } else {

            movePac();
            drawPac(g2d);
            moveEnemies(g2d);
            checkMaze();
        }
    }
	
	// display section
	
	private void showIntroScreen(Graphics2D g2d) {
		 
    	String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (SCREEN_SIZE)/4, 150);
    }
		
	private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " ;
        String b="Best Score: ";
        String c="CScore: ";
        g.drawString(b+ bestScore, SCREEN_SIZE / 2 - 60, SCREEN_SIZE + 16);
        g.drawString(s +score, SCREEN_SIZE / 2 + 110, SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }
	
	
	private void checkMaze() {
		int i = 0;
        boolean finished = true;

        if(currentScore==194) {
        	finished=true;
        }
        else
        {
        	finished=false;
        }

        if (finished) {

            score += 50;
            currentScore=0;

            if (N_ENEMIES < MAX_ENEMIES) {
            	N_ENEMIES++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            else if(N_ENEMIES < MAX_ENEMIES && currentSpeed < maxSpeed) {
            	inGame=false;
            	initGame();
            	
            }
            initLevel();
        }
    }
		
//		if(finished) {
//			String nextLevelMessage = "Moving to the next level";
//			String finalMessage = "Congratulation!! you have reached the end";
////			printCenter(g,nextLevelMessage);
//	        try {
//	            Thread.sleep(1000); // pause for 3 seconds
//	        } catch (InterruptedException e) {
//	            e.printStackTrace();
//	        }
//			score+=50;
//			currentScore=0; //resetting this score when moving to the next level
//			if(N_ENEMIES < MAX_ENEMIES && currentSpeed < maxSpeed) {
//				N_ENEMIES++;
//				currentSpeed++;
//			}
//			else
//			{
//				inGame=false;
////				printCenter(g,finalMessage);
//				initGame();
//				return;
//			}
//			initLevel();
//			
//		}
//		
//	}
	
	private void death() {
		lives--;
		if(lives==0) {
			if(score>bestScore) {
				
				bestScore=score;
			}
			inGame=false;
		}
		continueLevel();
	}
	
	//Enemies Section
	
	private void moveEnemies(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < N_ENEMIES; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCK * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawEnemies(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pac_x > (ghost_x[i] - 12) && pac_x < (ghost_x[i] + 12)
                    && pac_y > (ghost_y[i] - 12) && pac_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }
					
	private void drawEnemies(Graphics2D g2d,int x,int y) {
		g2d.drawImage(ghost, x, y, this);
	}
	
	private void movePac() {
		int pos;
		short ch; //value of the cell in the maze grid
		
		//pos of pac is determined
		if(pac_x%BLOCK_SIZE==0 && pac_y%BLOCK_SIZE==0) {
			pos=pac_x /BLOCK_SIZE + N_BLOCK* (int) (pac_y/BLOCK_SIZE);
			ch = screenData[pos];
			if((ch & 16) !=0) {
				screenData[pos]=(short) (ch & 15);
				currentScore++;
				score++;
			}
			//req_dx and rec_dy pac is controlled
			if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pac_dx = req_dx;
                    pac_dy = req_dy;
                }
            }
			
			//checks for stand still
			if((pac_dx==-1 && pac_dy==0 && (ch & 1) !=0) 
					|| (pac_dx==1 && pac_dy==0 && (ch & 4) !=0) 
					|| (pac_dx==0 && pac_dy==-1 && (ch & 2) !=0)
					|| (pac_dx==0 && pac_dy==1 && (ch & 8) !=0)) {
				 
					pac_dx= 0;
					pac_dy= 0;
				}
		}
		pac_x=pac_x + (PAC_SPEED * pac_dx);
		pac_y=pac_y + (PAC_SPEED * pac_dy);
		
	}
	
	
	//test  pac_x +1 and pac_y +1 
	private void drawPac(Graphics2D g2d) {
		if(req_dx ==-1) {
			g2d.drawImage(left,pac_x + 1,pac_y + 1,this);
		}
		else if(req_dx ==1) {
			g2d.drawImage(right,pac_x+1,pac_y+1,this);
		}
		else if(req_dy ==-1) {
			g2d.drawImage(up,pac_x+1,pac_y+1,this);
		}
		else{
			g2d.drawImage(down,pac_x+1,pac_y+1,this);
		}
	}
	
	private void  drawMaze(Graphics2D g2d){
		short i=0;
		int x,y;
		for(y=0;y<SCREEN_SIZE;y+=BLOCK_SIZE) {
			for(x=0;x<SCREEN_SIZE;x+=BLOCK_SIZE) {
				g2d.setColor(Color.BLUE);
				g2d.setStroke(new BasicStroke(5));
				if((levelData[i]==0)) {
					g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
				}
				if((screenData[i] & 1)!=0) {
					g2d.drawLine(x, y, x, y+BLOCK_SIZE-1);
				}
				if((screenData[i] & 2) !=0) {
					g2d.drawLine(x, y, x+BLOCK_SIZE-1, y);
				}
				if((screenData[i] & 4) !=0) {
					g2d.drawLine(x+BLOCK_SIZE-1, y, x+BLOCK_SIZE-1, y+BLOCK_SIZE-1);
				}
				if((screenData[i] & 8) !=0) {
					g2d.drawLine(x, y+BLOCK_SIZE-1, x+BLOCK_SIZE-1, y+BLOCK_SIZE-1);
				}
				if((screenData[i] & 16) !=0) {
					g2d.setColor(Color.white);
					g2d.fillOval(x+10, y+10, 6, 6);
				}
				i++; //what does this do
			}
			
		}	
	}
	
	private void initGame(){
		lives=3; //starting value of life
		score=0; //initial score
		initLevel();
		N_ENEMIES=6;
		currentSpeed=3;
	}

	//to initialize level copy the whole field from array levelData to screenData
	private void initLevel() {
		for(int i=0;i<N_BLOCK*N_BLOCK;i++) {
			screenData[i]=levelData[i];
		}
		continueLevel();
	}
	
	//defines the position of the enemies and create random speed for the enemies
		private void continueLevel() {
			 int dx=1; // dx is an integer variable that is initialized to 1. It is used to alternate the direction of the ghosts' movement at the start of a level.
			 int random;
			 
			 for(int i=0;i<MAX_ENEMIES;i++) {
				 ghost_y[i]=4*BLOCK_SIZE;
				 ghost_x[i]=4*BLOCK_SIZE;
				 ghost_dy[i]=0;
				 ghost_dx[i]= dx;
				 dx=-dx;
				 random=(int) (Math.random()*(currentSpeed+1));
				 if(random>currentSpeed) {
					 random=currentSpeed;
				 }
				 ghostSpeed[i]=validSpeeds[random];
			 }
			 
			 //start position of pac is defined
			 pac_x= 7*BLOCK_SIZE;
			 pac_y= 11*BLOCK_SIZE;
			 pac_dx=0;
			 pac_dy=0;
			 
			 //these variables are controlled by the cursor keys
			 req_dx=0;
			 req_dy=0;
			 dying=false;
		}
		
	
	//this function is use to calls other function and display the graphics
	public void paintComponent(Graphics g) {
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
		g2d.dispose();
	}
	
	
	//the keyboard working to move pac is told here
	//controls
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();
            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } 
            } else {
                if (key == KeyEvent.VK_SPACE && !inGame) {
                    inGame = true;
                    initGame();
                }
            }
            
        }
}
		
	}