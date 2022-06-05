
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
	// This is the Robot we will use to jump the hurdles
	private boolean running;
	private boolean pause;
	private String currDir = "R";
	private String currDir2 = "R";
	static final int width = 500;
	static final int height = 500;
	static final int unitSize = 25;
	static final int delay = 110;

	private int appleX;
	private int appleY;

	private ArrayList<Pair> snake = new ArrayList<Pair>();
	private HashMap<String, Integer> snakeCords = new HashMap<String, Integer>();
	private ArrayList<Pair> snake2 = new ArrayList<Pair>();
	private HashMap<String, Integer> snakeCords2 = new HashMap<String, Integer>();
	private HashMap<String, Integer> availableApples = new HashMap<String, Integer>();

	private Random random;
	private Timer timer;
	private Color bodyColor = new Color(0, 200, 200);
	private Color headColor = Color.cyan;
	private int cycle = 0;
	private int cycle2 = 1;

	private boolean snake1Dead = false;
	private boolean snake2Dead = false;

	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension (width, height));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();	
	}
	public void startGame() {
		for(int i = 0; i < width/unitSize; i++) {
			for(int j = 0; j < height/unitSize; j++) {
				String s = i * unitSize + "," + j * unitSize;
				availableApples.put(s, 1);
			}
		}
		setApple();
		running = false;
		pause = false;
		timer = new Timer(delay, this);
		timer.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);	
	}

	public void draw(Graphics g) {
		for(int i = 0; i < height/unitSize; i++) {
			g.drawLine(i * unitSize, 0, i * unitSize, height);
			g.drawLine(0, i * unitSize, width, i * unitSize);
		}
		g.setColor(Color.red);
		g.fillOval(appleX, appleY, unitSize, unitSize);
		int snakeHeight = (int) ((height/unitSize)/2) * unitSize;
		
		if(snake.size() == 0 && !snake1Dead) {
			for(int i = 1; i < 5; i++) {
				snake.add(new Pair(snakeHeight, i * unitSize));
			}
		}
		snakeHeight -= unitSize * 3;
		if(snake2.size() == 0 && !snake2Dead) {
			for(int i = 1; i < 5; i++) {
				snake2.add(new Pair(snakeHeight, i * unitSize));
			}
		}

		snakeCords.clear();
		snakeCords2.clear();

		cycleColor(cycle);
		g.setColor(bodyColor);
		for(Pair p : snake) {
			g.fillRect(p.getCol(), p.getRow(), unitSize, unitSize);
			String cord = p.getCol() + "," + p.getRow();
			snakeCords.put(cord, 1);
			if(availableApples.containsKey(cord)) {
				availableApples.remove(cord);
			}
		}
		Pair head = snake.get(snake.size() - 1);
		g.setColor(headColor);
		g.fillRect(head.getCol(), head.getRow(), unitSize, unitSize);

		cycleColor(cycle2);
		g.setColor(bodyColor);
		for(Pair p : snake2) {
			g.fillRect(p.getCol(), p.getRow(), unitSize, unitSize);
			String cord = p.getCol() + "," + p.getRow();
			snakeCords2.put(cord, 1);
			if(availableApples.containsKey(cord)) {
				availableApples.remove(cord);
			}
		}
		head = snake2.get(snake2.size() - 1);
		g.setColor(headColor);
		g.fillRect(head.getCol(), head.getRow(), unitSize, unitSize);
	}
	private void move() {
		String removed = snake.get(0).getCol() + "," + snake.get(0).getRow();
		availableApples.put(removed, 1);
		snake.remove(0);
		Pair p = snake.get(snake.size() - 1);
		switch(currDir) {
			case "R":
				snake.add(new Pair(p.getRow(), p.getCol() + unitSize));
				break;
			case "L":
				snake.add(new Pair(p.getRow(), p.getCol() - unitSize));
				break;
			case "U":
				snake.add(new Pair(p.getRow() - unitSize, p.getCol()));
				break;
			case "D":
				snake.add(new Pair(p.getRow() + unitSize, p.getCol()));
				break;
		}
	}
	private void move2() {
		String removed = snake2.get(0).getCol() + "," + snake2.get(0).getRow();
		availableApples.put(removed, 1);
		snake2.remove(0);
		Pair p = snake2.get(snake2.size() - 1);
		switch(currDir2) {
			case "R":
				snake2.add(new Pair(p.getRow(), p.getCol() + unitSize));
				break;
			case "L":
				snake2.add(new Pair(p.getRow(), p.getCol() - unitSize));
				break;
			case "U":
				snake2.add(new Pair(p.getRow() - unitSize, p.getCol()));
				break;
			case "D":
				snake2.add(new Pair(p.getRow() + unitSize, p.getCol()));
				break;
		}
	}

	private void checkCollision() {
		Pair p = snake.get(snake.size() - 1);
		if(p.getCol() >= height || p.getCol() < 0) {
			snake1Dead = true;
		}
		if(p.getRow() >= width || p.getRow() < 0) {
			snake1Dead = true;
		}

		Pair p2 = snake2.get(snake2.size() - 1);
		if(p2.getCol() >= height || p2.getCol() < 0) {
			snake2Dead = true;
		}
		if(p2.getRow() >= width || p2.getRow() < 0) {
			snake2Dead = true;
		}
		String snake1Head = p.getCol() + "," + p.getRow();
		String snake2Head = p2.getCol() + "," + p2.getRow();
		if(snakeCords.containsKey(snake2Head)) {

			snake2Dead = true;
		}
		if(snakeCords2.containsKey(snake1Head)) {
			snake1Dead = true;
		}
	}

	private void checkApple() {
		Pair head = snake.get(snake.size() - 1);
		if(head.getCol() == appleX && head.getRow() == appleY) {
			int xDiff = snake.get(1).getCol() - snake.get(0).getCol();
			int yDiff = snake.get(1).getRow() - snake.get(0).getRow();
			Pair tail = snake.get(0);
			if(xDiff == unitSize) {
				snake.add(0, new Pair(tail.getRow(), tail.getCol() - unitSize));
			} else if(xDiff == unitSize * -1) {
				snake.add(0, new Pair(tail.getRow(), tail.getCol() + unitSize));
			} else if(yDiff == unitSize) {
				snake.add(0, new Pair(tail.getRow() - unitSize, tail.getCol()));
			} else {
				snake.add(0, new Pair(tail.getRow() + unitSize, tail.getCol()));
			}
			setApple();
		}
		Pair head2 = snake2.get(snake2.size() - 1);
		if(head2.getCol() == appleX && head2.getRow() == appleY) {
			int xDiff = snake2.get(1).getCol() - snake2.get(0).getCol();
			int yDiff = snake2.get(1).getRow() - snake2.get(0).getRow();
			Pair tail = snake2.get(0);
			if(xDiff == unitSize) {
				snake2.add(0, new Pair(tail.getRow(), tail.getCol() - unitSize));
			} else if(xDiff == unitSize * -1) {
				snake2.add(0, new Pair(tail.getRow(), tail.getCol() + unitSize));
			} else if(yDiff == unitSize) {
				snake2.add(0, new Pair(tail.getRow() - unitSize, tail.getCol()));
			} else {
				snake2.add(0, new Pair(tail.getRow() + unitSize, tail.getCol()));
			}
			setApple();
		}
	}
	
	private void setApple() {
		ArrayList<String> keys = new ArrayList<String>(availableApples.keySet());
		String newApple = keys.get(random.nextInt(keys.size()));
		String[] split = newApple.split(",");

		appleX = Integer.parseInt(split[0]);
		appleY = Integer.parseInt(split[1]);
	}

	private void cycleColor(int cycle) {
		switch(cycle % 3) {
			case 0:
				bodyColor = new Color(0, 150, 150);
				headColor = Color.cyan;
				break;
			case 1:
				bodyColor = new Color(0, 150, 0);
				headColor = Color.green;
				break;
			case 2:
				bodyColor = new Color(150, 0, 150);
				headColor = new Color(255, 0, 255);
				break;
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(running && !pause) {
			checkCollision();
			checkApple();
			if(!snake1Dead) {
				move();
			}
			if(!snake2Dead) {
				move2();
			}
			repaint();
		}
	}
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch(keyCode) {
				case KeyEvent.VK_W:
					if(!currDir2.equals("D")) {
						currDir2 = "U";
					}
					break;
				case KeyEvent.VK_UP:
					if(!currDir.equals("D")) {
						currDir = "U";
					}
					break;
				case KeyEvent.VK_S:
					if(!currDir2.equals("U")) {
						currDir2 = "D";
					}
					break;
				case KeyEvent.VK_DOWN:
					if(!currDir.equals("U")) {
						currDir = "D";
					}
					break;
				case KeyEvent.VK_D:
					if(!currDir2.equals("L")) {
						if(!running) {
							running = true;
						}
						currDir2 = "R";
					}
					break;
				case KeyEvent.VK_RIGHT:
					if(!currDir.equals("L")) {
						if(!running) {
							running = true;
						}
						currDir = "R";
					}
					break;
				case KeyEvent.VK_A:
					if(!currDir2.equals("R")) {
						currDir2 = "L";
					}
					break;
				case KeyEvent.VK_LEFT:
					if(!currDir.equals("R")) {
						currDir = "L";
					}
					break;
				case KeyEvent.VK_P:
					pause = !pause;
					break;
				case KeyEvent.VK_V:
					cycle++;
					cycle2++;
					break;
			}
		}
	}
	static class Pair {
		private int row;
		private int col;
		Pair(int row, int col) {
			this.row = row;
			this.col = col;
		}
		public int getRow() {
			return row;
		}
		public int getCol() {
			return col;
		}
		public String toString() {
			return row + ":" + col;
		}
	}
}
