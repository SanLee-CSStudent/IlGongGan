package basicSystem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.*;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;
// import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import static java.awt.GraphicsDevice.WindowTranslucency.*;


@SuppressWarnings("unused")
//#main
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// checkTranslucency();
		CustomImage LoadImages = new CustomImage();
		GamePanel gamePanel = new GamePanel();// <-- GUI
		
		// initialize and start the main thread of the game
		Interface gameManager = new Interface(gamePanel);
		gameManager.start();
		
		content = new JPanel(new FlowLayout(FlowLayout.CENTER));
		content.add(gamePanel);
		createGUI(content);
	}
	
	@SuppressWarnings("unused")
	private static void checkTranslucency() {
		GraphicsEnvironment ge = 
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		
		boolean isUniformTranslucencySupported =
				gd.isWindowTranslucencySupported(TRANSLUCENT);
		boolean isPerPixelTranslucencySupported =
			    gd.isWindowTranslucencySupported(PERPIXEL_TRANSLUCENT);
		boolean isShapedWindowSupported =
			    gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT);
		
		if(!isUniformTranslucencySupported) {
			System.out.println("Uniform Translucency is not supported");
			System.exit(0);
		}
		else if(!isPerPixelTranslucencySupported) {
			System.out.println("Per-Pixel Translucency is not supported");
			System.exit(0);
		}
		else if(!isShapedWindowSupported){
			System.out.println("Shaped Window is not supported");
			System.exit(0);
		}
		else {
			System.out.println("All supported");
		}
	}
	
	private static void createGUI(JPanel gamePanel) {
		// JFrame.setDefaultLookAndFeelDecorated(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				@SuppressWarnings("unused")
				GameFrame gf = new GameFrame(content);
				
			}
		});
	}
	
	private static JPanel content;
	
	// static GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	// static GraphicsDevice device = ge.getScreenDevices()[0];
	// static DisplayMode defaultResolution = new DisplayMode(1920, 1080, device.getDisplayMode().getBitDepth(),device.getDisplayMode().getRefreshRate());
}
//#interface
class Interface extends Thread{
	// Create void run() to "start()"
	// Draw Tictoc in GamePanel or separate panel
	public Interface(GamePanel gp) {
		Interface.player = new Player();
		//since player is initialized in Interface class, classes initialized before Interface cannot access the player in static way
		
		isGameRunning = true;
		this.gamePanel = gp;
		
		kr = new KeyResponse();
		gamePanel.addPlayer(player);
		gamePanel.setFocusable(true);
		gamePanel.requestFocusInWindow();
		gamePanel.addKeyListener(kr);
	}
	
	public void run() {
		while(isGameRunning) {
			player.live();
			
			manageKeys();
			
			gamePanel.repaint();
			
			try {
				Thread.sleep(MAIN_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void manageKeys() {
		HashSet<Integer> currentKeys = KeyResponse.activeKeys;

		if (currentKeys.contains(KeyEvent.VK_F9)) {
			// System.out.println("KEY_PRESSED");
			if(UserInteract.isOpen) {
				gamePanel.UI.setVisible(false);
				UserInteract.isOpen = false;
			}
			else {
				gamePanel.UI.setVisible(true);
				UserInteract.isOpen = true;
			}
			KeyResponse.activeKeys.clear();
		}
	}
	
	public static Player player;
	private boolean isGameRunning;
	
	private GamePanel gamePanel;
	private KeyResponse kr;
	
	private static final int MAIN_SLEEP_TIME = 18;
}
//#gameframe
class GameFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameFrame(JPanel gp) {// GamePanel gp) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setAutoRequestFocus(true);
		this.requestFocusInWindow(true);
		this.setUndecorated(true);

		try {
			icon = new ImageIcon(ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\icons\\idle_L00.png")));
			cursor = new ImageIcon(CustomImage.Cursor[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setIconImage(icon.getImage());
		this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor.getImage(), new Point(0,0), "Main Cursor"));
		
		cs = new MouseResponse(cursor, this);
		this.addMouseListener(cs);
		this.addMouseMotionListener(cs);
		
		GameFrame.gamePanel = (GamePanel) gp.getComponent(0);
		this.add(gamePanel);
		GameFrame.gamePanel.setFrame(this);
		
		setSize((int)WIDTH, (int)HEIGHT);

		setBackground(new Color(0,255,0,0));
		
		setContentPane(gamePanel);
		pack();
		
		this.setAlwaysOnTop(true);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}
	
	public void setCS(ImageIcon cs) {
		this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cs.getImage(), new Point(0,0), "Main Cursor"));
	}
	
	public boolean isStaying() {
		return cs.isStaying();
	}
	
	public void setHeartDelay() {
		// System.out.println(heartDelay);
		
		heartDelay++;
		gamePanel.setHeartDelay(heartDelay);
		
		if(heartDelay > MAXIMUM_HEART_DELAY) {
			heartDelay = 0;
		}
	}
	
	public static final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	public static final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static final int RESOLUTION = Toolkit.getDefaultToolkit().getScreenResolution();
	
	public static GamePanel gamePanel;
	
	public static ImageIcon cursor;
	private ImageIcon icon;
	public CustomImage Images;
	private int heartDelay = 0;
	
	private MouseResponse cs;
	
	public static final int MAXIMUM_HEART_DELAY = 20;
}
//#keyresponse
class KeyResponse implements KeyListener{
	
	public KeyResponse(){
		activeKeys = new HashSet<Integer>();
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		activeKeys.add(e.getKeyCode());
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	
	public static HashSet<Integer> activeKeys;
}
//#mouseresponse
class MouseResponse extends MouseInputAdapter 
	implements MouseMotionListener{
	
	public MouseResponse(ImageIcon cs, GameFrame gf) {
		cursor = cs;
		gameFrame = gf;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if(Player.hitbox.contains(new Point(e.getX(), e.getY()))) {
			petPlayer();
		}
	}
	
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		cursor = new ImageIcon(CustomImage.Cursor[1]);
		gameFrame.setCS(cursor);
		
	}
	
	public void mouseClicked(MouseEvent e) {
		if(!SleepButton.isSleep) {
			if(Player.hitbox.contains(new Point(e.getX(), e.getY()))) {
				if(!isStaying && wasRunning) {// if TICTOC is staying, TICTOC is sitting
					isStaying = true;
					wasRunning = false;
					Interface.player.setAction(1);// Sit
				}
				else {
					isStaying = false;
					wasRunning = true;
					Interface.player.setAction(0); // Idle
				}
			}
		}
		else {
			if(Player.hitbox.contains(new Point(e.getX(), e.getY()))) {
				isStaying = false;
				SleepButton.isSleep = false;
				SleepButton.pause();
				if(wasRunning) {
					Interface.player.setAction(0);
				}
				else {
					Interface.player.setAction(1);
				}
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		cursor = new ImageIcon(CustomImage.Cursor[0]);
		gameFrame.setCS(cursor);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean isStaying() {
		return isStaying;
	}
	
	public void petPlayer() {
		// change the cursor image in gamePanel level not gameFrame---SOLVED
		cursor = new ImageIcon(CustomImage.Cursor[1]);
		if(wasRunning) {
			Interface.player.setAction(0);
		}
		else {
			Interface.player.setAction(1);
		}
		
		gameFrame.setHeartDelay();
		gameFrame.setCS(cursor);
		isPetting = true;
		
		if(GameFrame.gamePanel.getHearts() < 7) {
			GameFrame.gamePanel.addHeart(GameFrame.gamePanel.getHearts(), isPetting);
		}
	}
	
	public static ImageIcon cursor = new ImageIcon();
	private GameFrame gameFrame;
	// private UserInteract UI;
	
	private boolean isPetting = false;
	public static boolean isStaying = false;
	public static boolean wasRunning = true;
}
//#gamepanel
class GamePanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GamePanel(){
		setBackground(new Color(255, 255, 255, 0));
		heartDelay = 0;
		
		this.setPreferredSize(new Dimension((int)(1920), (int)(1080)));
		this.setLocation(new Point(0,0));
		
		this.setLayout(null);
		UI = new UserInteract(this);
		this.add(UI);

		this.setDoubleBuffered(true);
		this.setOpaque(false);
		
		Hearts = new ArrayList<Heart>();
		Foods = new ArrayList<Food>();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(player.getCurrentFrame(), (int)player.getCurrentX(), player.getCurrentY(), 
				player.PLAYER_WIDTH, player.PLAYER_HEIGHT, new Color(0,0,0,0), null);
		// g2.setColor(new Color(100, 100, 100));
		// g2.drawRect(Player.hitbox.x, Player.hitbox.y, Player.hitbox.width, Player.hitbox.height);
		// g2.setColor(new Color(200, 200, 200));
		// g2.drawRect(player.getCurrentX(), player.getCurrentY(), player.PLAYER_WIDTH, player.PLAYER_WIDTH);
		
		if(this.isPetting) {
			for(int i = 0; i < Hearts.size(); i++) {
				Hearts.get(i).setMovement();
				
				g2.drawImage(Hearts.get(i).getCurrentFrame(), (int)(Hearts.get(i).heartX), (int)(Hearts.get(i).heartY), null);
				if(Hearts.get(i).isLastFrame) {
					GamePanel.Hearts.remove(Hearts.get(i));
				}
			}
		}
		
		if(FeedButton.isFoodHover || Foods.size() > 0) {
			g2.drawImage(food.getCurrentFrame(), food.currentX, food.currentY, null);

		}
		/*if(player.isSleeping) {
			for(int i = 0; i < player.Zs.size(); i++) {
				g2.drawImage(CustomImage.Z, player.Zs.get(i).Zx, player.Zs.get(i).Zy, null);

			}
		}*/
		
		g2.setBackground(new Color(255, 255, 255, 0));
	}
	
	public void setHeartDelay(int delay) {
		heartDelay = delay;
	}
	
	public void addHeart(int location, boolean isPetting) {
		this.isPetting = isPetting;
		if(this.isPetting) {
			
			// System.out.println("HeartNum: " + heartNum);
			if(heartDelay > GameFrame.MAXIMUM_HEART_DELAY) {
				heart = new Heart(player);
				
				Hearts.add(heart);
			
				heartDelay = 0;
			}
		}
	}
	
	public void addFood(Food fd) {
		food = fd;
		this.add(food);
		Foods.add(food);
		// GamePanel gp = this;
		food.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// System.out.println("Mouse is at " + e.getXOnScreen() + ", " + e.getYOnScreen());
				gameFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[2], new Point(0,0), "Main Cursor"));

				food.currentX = e.getXOnScreen();
				food.currentY = e.getYOnScreen();
			}
			
			public void mouseReleased(MouseEvent e) {
				gameFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[0], new Point(0,0), "Main Cursor"));
				
				if(Player.hitbox.contains(food.hitbox.x, food.hitbox.y)) {
					Player.HUNGER += 25;
					
					if(Player.HUNGER > 100) {
						Player.HUNGER = 100;
					}
					// getSubimage() helps crop not extend the image
					// UserInteract.hungerBar.hunger = UserInteract.hungerBar.hunger
					// 		.getSubimage(0, 0, currentHunger, UserInteract.hungerBar.BAR_HEIGHT);
					UserInteract.hungerBar.hunger = CustomImage.Bar[1].getSubimage(0, 0, 
							(int)(((double)Player.HUNGER / UserInteract.hungerBar.BAR_WIDTH) * 81), UserInteract.hungerBar.BAR_HEIGHT);
					removeFood();
					FeedButton.isFoodHover = false;
				}
			}
		});
		food.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				gameFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[2], new Point(0,0), "Main Cursor"));

				food.currentX = e.getXOnScreen();
				food.currentY = e.getYOnScreen();
				food.setLocation(food.currentX, food.currentY);
				food.hitbox.setLocation(new Point(food.currentX, food.currentY));
				
			}
		});	
	}
	
	private void removeFood() {
		
		Foods.remove(food);
	}
	
	public void setFrame(GameFrame gf) {
		gameFrame = gf;
	}
	
	public int getHearts() {
		return Hearts.size();
	}
	
	public void addPlayer(Player pl) {
		this.player = pl;
		UI.addPlayer(player, this);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setIsPetting() {
		isPetting = false;
	}
	
	private Player player;
	public static ArrayList<Heart> Hearts;
	private ArrayList<Food> Foods;
	private Heart heart;
	private Food food;
	
	private int heartDelay;

	private boolean isPetting = false;
	public UserInteract UI;

	public static GameFrame gameFrame = null;
}
//#userinteract
class UserInteract extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// custom frame for UI

	public UserInteract(GamePanel gamePanel) {
		this.setSize(new Dimension(UI_WIDTH, UI_HEIGHT));
		this.setLocation(new Point(UI_startX, UI_startY));
		// this.setVisible(false);
		p = this;
		UserInteract.gamePanel = gamePanel;
		hungerBar = new HungerBar();
		sanityBar = new SanityBar();
		// System.out.println(hungerBar.BAR_WIDTH + ", " + hungerBar.BAR_HEIGHT + ", " + Player.HUNGER);

		try {
			background = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\UI\\UI_Background.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}

		/*display = new JTextArea();
		display.setEditable(false);
		log = new JScrollPane(display);
		log.setPreferredSize(new Dimension(UI_WIDTH - 10, UI_HEIGHT/4));
		// display.setPreferredSize(new Dimension(log.WIDTH, log.HEIGHT));
		log.setLocation(10, 15);
		
		display.setFont(new Font("Consolas", Font.PLAIN, 18));
		display.append("Screen Size:\n" + GameFrame.WIDTH + "x" + GameFrame.HEIGHT);
		p.add(log);*/
		
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// MouseResponse.cursor = new ImageIcon(ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\icons\\Cursor\\masterHand1.png")));
				p.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[2], new Point(0,0), "Main Cursor"));
					
				UI_currentX = e.getX();
				UI_currentY = e.getY();
			}
			
			public void mouseReleased(MouseEvent e) {
				p.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[0], new Point(0,0), "Main Cursor"));

			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				p.setLocation(p.getX() + e.getX() - UI_currentX, p.getY() + e.getY() - UI_currentY);
			}
		});
		
		this.setLayout(new GridLayout(2, 2, 15, 15));
		this.setBorder(BorderFactory.createEmptyBorder(60, 20, 15, 15));

	}
	
	public void addPlayer(Player pl, GamePanel gp) {
		// TODO Auto-generated method stub
		UserInteract.player = pl;
		
		sleep = new SleepButton(p);
		feed = new FeedButton(p);
		weather = new WeatherButton(p);
		exit = new ExitButton(p);
		
		// p.add(hungerBar);
		// p.add(sanityBar);
		p.add(sleep);
		p.add(feed);
		p.add(weather);
		p.add(exit);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.drawImage(background, 0, 0, null);
		
		g2.drawImage(hungerBar.BarFrame, hungerBar.currentX, hungerBar.offsetY, null);
		g2.drawImage(hungerBar.hunger, hungerBar.currentX, hungerBar.offsetY, null);
		
		g2.drawImage(sanityBar.BarFrame, sanityBar.currentX, sanityBar.offsetY, null);
		g2.drawImage(sanityBar.sanity, sanityBar.currentX, sanityBar.offsetY, null);

		// g2.drawString("", 0, 0);
		// System.out.println("paintComponent() in UI is working");
	}
	
	public static final int UI_WIDTH = 240;// ((int)GameFrame.WIDTH) / 8;// 240
	public static final int UI_HEIGHT = 180;// ((int)GameFrame.HEIGHT) / 6;// 180
	private static final int UI_offset = 38;
	
	public static int UI_startX = (int)GameFrame.WIDTH - UI_WIDTH - UI_offset;
	public static int UI_startY = UI_offset;
	
	private int UI_currentX, UI_currentY;
	
	public static Player player;
	private JPanel p;
	public static GamePanel gamePanel;
	// private JTextArea display;
	// private JScrollPane log;
	public static boolean isOpen = true;
	
	private BufferedImage background;
	
	private SleepButton sleep;
	private FeedButton feed;
	private WeatherButton weather;
	private ExitButton exit;
	
	public static HungerBar hungerBar;
	public static SanityBar sanityBar;
}
//#custombutton
class CustomButton extends JLabel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	CustomButton(){

	}
	
	CustomButton(JPanel p, ImageIcon onButton, ImageIcon offButton){
		button = this;
		this.setIcon(onButton);
		this.setBounds(new Rectangle(this.getPreferredSize()));
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				button.setIcon(offButton);
				p.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[4], new Point(0,0), "Main Cursor"));
				
			}
			
			public void mouseEntered(MouseEvent e) {
				p.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[3], new Point(0,0), "Main Cursor"));
			}
			
			public void mouseExited(MouseEvent e) {
				p.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[0], new Point(0,0), "Main Cursor"));
			}
			
			public void mouseReleased(MouseEvent e) {
				button.setIcon(onButton);
				p.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(CustomImage.Cursor[3], new Point(0,0), "Main Cursor"));
			}
		});
	}
	
	public JLabel getButton() {
		return this;
	}
	
	CustomButton button;
	JPanel UI;
	
	protected Player player;

}
//#sleepbutton
class SleepButton extends CustomButton{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	SleepButton(JPanel p) {
		super(p, new ImageIcon(CustomImage.Button[2]), new ImageIcon(CustomImage.Button[3]));
		// TODO Auto-generated constructor stub
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// System.out.println("SANITY: " + Player.SANITY);
				if(isSleep) {
					isSleep = false;
					MouseResponse.isStaying = false;
					MouseResponse.wasRunning = true;
					UserInteract.player.setAction(0);
					pause();
				}
				else {
					isSleep = true;
					MouseResponse.isStaying = true;
					MouseResponse.wasRunning = false;
					UserInteract.player.setAction(2);
					resume();
					// add sleep icon
				}
			}
		});
	}
	
	public static void pause() {
		sanityIncrement.cancel();
	}
	
	private void resume() {
		sanityIncrement = new Timer();
		sanityIncrement.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(Player.SANITY < 100) {
					System.out.println("Sanity: " + Player.SANITY);
					Player.SANITY += 2;
					
					UserInteract.sanityBar.sanity = CustomImage.Bar[2]
							.getSubimage(0, 0, (int)(((double)Player.SANITY / UserInteract.sanityBar.BAR_WIDTH) * 81), UserInteract.sanityBar.BAR_HEIGHT);
				}
			}
			
		}, INCREMENT_DELAY, INCREMENT_DELAY);
	}
	
	static Timer sanityIncrement;
	public static boolean isSleep = false;
	private int INCREMENT_DELAY = 7000;
}
//#foodbutton
class FeedButton extends CustomButton{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	FeedButton(JPanel p){
		super(p, new ImageIcon(CustomImage.Button[0]), new ImageIcon(CustomImage.Button[1]));
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(!isFoodHover) {
					isFoodHover = true;
					food = new Food(p.getX() + p.getWidth() - Food.FOOD_WIDTH, p.getY() + p.getHeight());
					
					UserInteract.gamePanel.addFood(food);
				}
			}
		});
	}
	
	public static boolean isFoodHover = false;
	public static Food food;
}
//#weatherbutton
class WeatherButton extends CustomButton{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	WeatherButton(JPanel p){
		super(p, new ImageIcon(CustomImage.Button[4]), new ImageIcon(CustomImage.Button[5]));
	}
}
//#exitbutton
class ExitButton extends CustomButton{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ExitButton(JPanel p){
		super(p, new ImageIcon(CustomImage.Button[6]), new ImageIcon(CustomImage.Button[7]));
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.exit(1);
			}
		});
	}
}
enum DIR {
	LEFT, RIGHT;
}
//#customimage
class CustomImage{
	
	CustomImage(){
		Cursor = new Image[CURSOR_NUMBER];
		Button = new Image[BUTTON_NUMBER];
		Heart = new BufferedImage[HEART_NUMBER];
		Food = new BufferedImage[FOOD_NUMBER];
		Player = new BufferedImage[IMAGE_TYPE][12];
		Bar = new BufferedImage[BAR_NUMBER];
		FoodInPixel = new int[FOODINPIXEL_WIDTH][FOODINPIXEL_HEIGHT];
		loadIcon();
		loadButton();
		loadPlayer();
		loadHeart();
		loadFood();
		loadPixels();
	}
	
	private void loadPixels() {
		FoodInByte = ((DataBufferByte)CustomImage.Food[0].getRaster().getDataBuffer()).getData();
		final int pixelSize = 3;
		for(int pixel = 0, row = 0, col = 0; pixel + 2 < pixelSize; pixel += pixelSize) {
			int pixelComponent = 0;
			pixelComponent += -16777216;// alpha 255
			pixelComponent += ((int) FoodInByte[pixel] & 0xff);// blue
			pixelComponent += (((int) FoodInByte[pixel + 1] & 0xff) << 8);// green
			pixelComponent += (((int) FoodInByte[pixel + 2] & 0xff) << 16);// red
			FoodInPixel[col][row] = pixelComponent;
			col++;
			if(col == FOODINPIXEL_WIDTH) {
				col = 0;
				row++;
			}
		}
	}
	
	private void loadCursor(){
		try {
			for(int i = 0; i < CURSOR_NUMBER; i++) {
				Cursor[i] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\icons\\Cursor\\Cursor" + i + ".png"));
				// System.out.println(Cursor[i].getWidth(null) + "x" + Cursor[i].getHeight(null));
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadButton() {
		try {
			for(int i = 0; i < BUTTON_NUMBER; i++) {
				Button[i] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\UI\\UI_Button" + i + ".png"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadHeart() {
		try {
			for(int i = 1; i <= HEART_NUMBER; i++) {
				Heart[i-1] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\icons\\Heart\\heart" + (i) + ".png"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadFood() {
		try {
			for(int i = 1; i <= FOOD_NUMBER; i++) {
				Food[i-1] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\icons\\Food\\Food" + (i) + ".png"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadIcon() {
		loadCursor();
		try {
			Z = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\icons\\Sleep_Icon.png"));
			for(int i = 1; i <= BAR_NUMBER; i++) {
				Bar[i-1] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\icons\\Bar\\Bar" + (i) + ".png"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadPlayer() {
		try {
			for(int i = 0; i < IMAGE_TYPE; i++) {
				for(int j = 0; j < TICTOC_NUMBER; j++) {
					switch(i) {
						case 0:
							if(j < 10) {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\IdleImage_Tictoc\\idle_L0" + j +".png"));
							}
							else {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\IdleImage_Tictoc\\idle_L" + j +".png"));
							}
							break;
							
						case 1:
							if(j < 10) {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\IdleImage_Tictoc\\idle_R0" + j +".png"));
							}
							else {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\IdleImage_Tictoc\\idle_R" + j +".png"));
							}
							break;
						case 2:
							if(j < 10) {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SitImage_Tictoc\\sit_L0" + j +".png"));
							}
							else {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SitImage_Tictoc\\sit_L" + j +".png"));
							}
							break;
						case 3:
							if(j < 10) {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SitImage_Tictoc\\sit_R0" + j +".png"));
							}
							else {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SitImage_Tictoc\\sit_R" + j +".png"));
							}
							break;

					}
				}
			}
			
			for(int i = 4; i < 6; i++) {
				for(int j = 0; j < RUN_FRAME_NUMBER; j++) {
					switch(i) {
						case 4:
							Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\RunImage_Tictoc\\run_L" + j +".png"));
							break;
						case 5:
							Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\RunImage_Tictoc\\run_R" + j +".png"));
							break;
					}
				}
			}
			
			for(int i = 6; i < 8; i++) {
				for(int j = 0; j < SLEEP_FRAME_NUMBER; j++) {
					switch(i) {
						case 6:
							Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SleepImage_Tictoc\\sleep_L0" + j +".png"));
							break;
						case 7:
							Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SleepImage_Tictoc\\sleep_R0" + j +".png"));
							break;
					}
				}
			}
			
			for(int i = 8; i < 10; i++) {
				for(int j = 0; j < SLEEPING_FRAME_NUMBER; j++) {
					switch(i) {
						case 8:
							if(j < 10) {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SleepImage_Tictoc\\sleeping_L0" + j +".png"));
							}
							else {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SleepImage_Tictoc\\sleeping_L" + j +".png"));
							}
							break;
						case 9:
							if(j < 10) {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SleepImage_Tictoc\\sleeping_R0" + j +".png"));
							}
							else {
								Player[i][j] = ImageIO.read(new java.io.FileInputStream("basicSystem\\src\\images\\SleepImage_Tictoc\\sleeping_R" + j +".png"));
							}
							break;
					}
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Image[] Cursor;
	public static Image[] Button;
	public static BufferedImage[] Bar;
	public static BufferedImage[] Heart;
	public static BufferedImage[] Food;
	public static BufferedImage[][] Player;//(0-1: idle) --> (2-3: sit) --> (4-5: run) --> 6-7: sleep --> 8-9: sleeping
	public static BufferedImage Z;
	private byte[] FoodInByte;
	public static int[][] FoodInPixel;
	
	private final int BAR_NUMBER = 3;//0: frame --> 1: hunger --> 2: sanity 
	private final int CURSOR_NUMBER = 5;
	private final int BUTTON_NUMBER = 8;
	public static final int FOODINPIXEL_WIDTH = 39;
	public static final int FOODINPIXEL_HEIGHT = 36;
	public final static int HEART_NUMBER = 19;
	public final static int FOOD_NUMBER = 8;
	private final int IMAGE_TYPE = 10;
	private final int RUN_FRAME_NUMBER = 6;
	private final int SLEEP_FRAME_NUMBER = 5;
	public static final int SLEEPING_FRAME_NUMBER = 12;
	public static final int TICTOC_NUMBER = 12;
}
//#player
class Player {
	
	public Player() {
		d = DIR.LEFT;
		hitbox = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT);
		hitbox.setLocation(currentX, this.currentY);
		
		Zs = new ArrayList<Z>();
		hungerTimer = new Timer();
		sanityTimer = new Timer();
		
		currentFrame = CustomImage.Player[0][0];
		hungerTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(HUNGER <= 1) {
					HUNGER = 0;
					
					System.out.println("TICTOC Has Starved to Death");
					hungerTimer.cancel();
					hungerTimer.purge();
				}
				else {
					UserInteract.hungerBar.hunger = UserInteract.hungerBar.hunger
							.getSubimage(0, 0, (int)(((double)HUNGER / UserInteract.hungerBar.BAR_WIDTH) * 81), UserInteract.hungerBar.BAR_HEIGHT);
					// UserInteract.hungerBar.hunger = UserInteract.hungerBar.
					// 		hunger.getScaledInstance((int)(((double)HUNGER / UserInteract.hungerBar.BAR_WIDTH) * 81), UserInteract.hungerBar.BAR_HEIGHT, Image.SCALE_FAST);
					HUNGER--;
				}
			}
			
		}, HUNGER_DELAY, HUNGER_DELAY);
		sanityTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(SANITY <= 1) {
					SANITY = 0;
					
					sanityTimer.cancel();
					sanityTimer.purge();
				}
				else {
					UserInteract.sanityBar.sanity = UserInteract.sanityBar.sanity
							.getSubimage(0, 0, (int)(((double)SANITY / UserInteract.sanityBar.BAR_WIDTH) * 81), UserInteract.sanityBar.BAR_HEIGHT);
					SANITY--;
				}
			}
			
		}, SANITY_DELAY, SANITY_DELAY);
	}
	
	public int getCurrentX() {
		return (int)currentX;
	}
	
	public int getCurrentY() {
		return currentY;
	}
	
	public BufferedImage getCurrentFrame() {

		return currentFrame;
	}
	
	public void live() {
		if(action == 1) {//Sit
			changeImages(2);
			stop();
		}
		else if(action == 2) {//Sleep
			if(!isSleeping) {
				changeImages(3);
			}
			else {
				changeImages(4);
				sleep();
			}
		}
		else if(action == 3) {
			
		}
		else {//Idle
			changeImages(0);
		}
		if(ACTIVITY_COUNTER >= MAX_ACTIVITY) {
			randNum = rand.nextInt(3);

			ACTIVITY_COUNTER = 0;
		}
		
		if(ACTIVITY_COUNTER >= 100) {
			if(randNum < 2) {
				move(randNum);
			}
		}
		
		ACTIVITY_COUNTER++;
	}
	
	public void move(int direction) {
		changeImages(1);
		if(direction == 0) {
			if(currentX  <= 0) {
				randNum += 1;
			}
			else {
				d = DIR.LEFT;
				currentX -= 4;
			}
		}
		else {
			if(currentX >= GameFrame.WIDTH - PLAYER_WIDTH) {
				d = DIR.LEFT;
				randNum -= 1;	
			}
			else {
				d = DIR.RIGHT;
				currentX += 4;
			}		
		}
		hitbox.setLocation(currentX, currentY);
	}
	
	public void stop() {
		randNum = 2;
	}

	public void changeImages(int act) {
		if(act == 0) {
			// Idle
			setFrameNumber(12, idleCounter, "IDLE");
			if(d == DIR.LEFT) {
				currentFrame = CustomImage.Player[0][currentFrameNumber];
			}
			else {
				currentFrame = CustomImage.Player[1][currentFrameNumber];
			}
			idleCounter++;
		}
		else if(act == 1){
			// Run
			setFrameNumber(6, moveCounter, "MOVE");
			if(d == DIR.LEFT) {
				currentFrame = CustomImage.Player[4][currentFrameNumber];
			}
			else {
				currentFrame = CustomImage.Player[5][currentFrameNumber];
			}
			
			moveCounter++;
		}
		else if(act == 2){
			// Sit
			setFrameNumber(12, idleCounter, "SIT");
			if(d == DIR.LEFT) {
				currentFrame = CustomImage.Player[2][currentFrameNumber];
			}
			else {
				currentFrame = CustomImage.Player[3][currentFrameNumber];
			}
			idleCounter++;
		}
		else if(act == 3){
			// Sleep
			if(sleepCounter < FRAME_COUNTER_THRESH * 5) {
				setFrameNumber(5, sleepCounter, "SLEEP");
				sleepCounter++;
			}
			else {
				sleepCounter = 0;
				isSleeping = true;
			}
			
			// System.out.println(sleepCounter);
			if(d == DIR.LEFT) {
				currentFrame = CustomImage.Player[6][currentFrameNumber];
			}
			else {
				currentFrame = CustomImage.Player[7][currentFrameNumber];
			}
		}
		else {
			// sleeping
			setFrameNumber(CustomImage.SLEEPING_FRAME_NUMBER + 3, sleepingCounter, "SLEEPING");
			
			if(d == DIR.LEFT) {
				if(currentFrameNumber >= 10) {
					currentFrame = CustomImage.Player[8][9];

				}
				else {
					currentFrame = CustomImage.Player[8][currentFrameNumber];
				}
			}
			else {
				if(currentFrameNumber >= 10) {
					currentFrame = CustomImage.Player[9][9];
				}
				else {
					currentFrame = CustomImage.Player[9][currentFrameNumber];
				}
			}
			sleepingCounter++;
		}
	}

	private void setFrameNumber(int Frame, int Counter, String Type) {
		currentFrameNumber = Counter / FRAME_COUNTER_THRESH;
		currentFrameNumber %= Frame;
		
		if (Counter > FRAME_COUNTER_THRESH * Frame) {
			if(Type.equals("MOVE")) {
				moveCounter = 0;
			}
			else if(Type.equals("SIT") || Type.equals("IDLE")) {
				idleCounter = 0;
			}
			else if(Type.equals("SLEEPING")) {
				sleepingCounter = 0;
			}
			else {
				sleepCounter = 0;
			}
		}
	}
	
	public DIR getDirection() {
		return d;
	}
	
	public void setAction(int command) {
		action = command;
	}
	
	public void sleep() {
		stop();
		Zs.add(Z);
	}
	
	private int ACTIVITY_COUNTER = 0;
	private int MAX_ACTIVITY = 135;
	
	private BufferedImage currentFrame;
	private Random rand = new Random();
	public static final int PLAYER_START_X = 7 * (int)GameFrame.WIDTH / 8;
	public final int PLAYER_WIDTH = 134;
	public final int PLAYER_HEIGHT = 134;
	private int currentX = PLAYER_START_X - PLAYER_WIDTH;
	private int currentY = (int) (GameFrame.HEIGHT - PLAYER_HEIGHT - 40 + 2);
	
	public ArrayList<Z> Zs;
	private Z Z;
	
	private DIR d;
	private int randNum;
	public static Rectangle hitbox;
	
	private int currentFrameNumber = 0;
	
	private int idleCounter = 0;
	private int moveCounter = 0;
	private int sleepCounter = 0;
	private int sleepingCounter = 0;
	private static final int FRAME_COUNTER_THRESH = 6;
	
	public static int HUNGER = 100;
	public static int SANITY = 100;
	public static int LOVE = 0;
	
	private Timer hungerTimer;
	private final int HUNGER_DELAY = 10000;//10,000 ms or 10 seconds
	private Timer sanityTimer;
	private final int SANITY_DELAY = 60000;// 1 minutes
	
	private int action = 0;
	public boolean isSleeping = false;

}
//#Bar
class Bar{

	public Bar() {
		BarFrame = CustomImage.Bar[0];
		
	}
	
	protected Image BarFrame;
	protected final int BAR_WIDTH = 90;
	protected final int BAR_HEIGHT = 20;
	protected final int offsetX = 20;
	protected final int offsetY = 15;
}
class HungerBar extends Bar implements animationFrame{
	
	public HungerBar() {
		hunger = CustomImage.Bar[1];
		currentFrame = CustomImage.Food[0];
		currentX = offsetX;
	}
	
	@Override
	public void setFrameNumber() {
		// TODO Auto-generated method stub
		currentFrameNumber = counter / FRAME_COUNTER_THRESH;
		currentFrameNumber %= CustomImage.FOOD_NUMBER;
	}
	@Override
	public BufferedImage getCurrentFrame() {
		// TODO Auto-generated method stub
		setFrameNumber();
		currentFrame = CustomImage.Food[currentFrameNumber];
		counter++;
		
		return currentFrame;
	}
	
	public BufferedImage hunger;

	private BufferedImage currentFrame;
	private final int FRAME_COUNTER_THRESH = 6;
	private int counter = 0;
	private int currentFrameNumber = 0;
	
	public int currentX;
}
class SanityBar extends Bar{

	public SanityBar() {
		sanity = CustomImage.Bar[2];
		currentX = UserInteract.UI_WIDTH - offsetX - BAR_WIDTH;
	}
	public BufferedImage sanity;
	public int currentX;
}
//#z
class Z{
	public Z(Player pl){
		this.player = pl;
	}
	
	private Player player;
	public int Zx = player.getCurrentX() + (player.PLAYER_WIDTH / 2);
	public int Zy = player.getCurrentY() + (player.PLAYER_HEIGHT / 2);
}
//#heart
class Heart implements animationFrame{
	public Heart(Player pl){
		this.player = pl;
		rand = new Random();
		if(player.getDirection() == DIR.LEFT) {
			// System.out.println("Tictoc is facing left");
			startX = ((rand.nextInt() % player.PLAYER_WIDTH/2) / 2) 
				+ (player.getCurrentX() + player.PLAYER_WIDTH/4);
		}
		else {
			// System.out.println("Tictoc is facing right");
			startX = ((rand.nextInt() % player.PLAYER_WIDTH/2) / 2) 
					+ (player.getCurrentX() + player.PLAYER_WIDTH / 2);
		}
		startY = ((rand.nextInt() % player.PLAYER_HEIGHT/2) / 3) 
				+ (player.getCurrentY() + player.PLAYER_HEIGHT/2);
		heartX = startX;
		heartY = startY;
		
		currentFrame = CustomImage.Heart[0];
		
		removeHeart = new Timer();
		removeHeart.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				isHeartRemoved = true;
				
				removeHeart.cancel();
				removeHeart.purge();
		
			}	
		}, (long) (1000 * 1.5));
	}
	
	public void setMovement() {
		if(!isHeartRemoved) {
			heartY--;
			heartX += ((heartY % 10) * Math.sin(heartY / 3));
		}
		return;
	}
	
	public BufferedImage getCurrentFrame() {
		if(isHeartRemoved) {
			heartKilled();
		}
		
		return currentFrame;
	}
	
	public void setFrameNumber() {
		currentFrameNumber = heartCounter / FRAME_COUNTER_THRESH;
		currentFrameNumber %= CustomImage.HEART_NUMBER;

	}
	
	public void heartKilled() {
		setFrameNumber();
		currentFrame = CustomImage.Heart[currentFrameNumber];
		heartCounter++;
		if(currentFrameNumber == CustomImage.HEART_NUMBER - 1) {
			isLastFrame = true;
		}
	}
	
	private BufferedImage currentFrame;
	private Player player;
	private Timer removeHeart;
	
	public double heartX;
	public double heartY;
	
	private Random rand;
	private int startX;
	private int startY;
	
	public final int FRAME_COUNTER_THRESH = 5;
	public int heartCounter = 0;
	private int currentFrameNumber = 0;
	public boolean isHeartRemoved = false;
	public boolean isLastFrame = false;
}
//#food
class Food extends JLabel implements animationFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Food(int X, int Y) {
		currentX = X;
		currentY = Y;
		this.setBounds(currentX, currentY, FOOD_WIDTH, FOOD_HEIGHT);
		this.setLocation(new Point(currentX, currentY));
		currentFrame = CustomImage.Food[currentFrameNumber];
		
	}
	
	public BufferedImage getCurrentFrame() {
		setFrameNumber();
		currentFrame = CustomImage.Food[currentFrameNumber];
		foodCounter++;
		return currentFrame;
	}
	
	public void setFrameNumber() {
		currentFrameNumber = foodCounter / FRAME_COUNTER_THRESH;
		currentFrameNumber %= CustomImage.FOOD_NUMBER;

	}
	
	private BufferedImage currentFrame;
	private final int FRAME_COUNTER_THRESH = 6;
	private int foodCounter = 0;
	private int currentFrameNumber = 0;
	
	public static final int FOOD_WIDTH = 39;
	public static final int FOOD_HEIGHT = 36;
	public final Rectangle hitbox = new Rectangle(FOOD_WIDTH, FOOD_HEIGHT);
	
	public int currentX;
	public int currentY;
}
//#animationframe
interface animationFrame{
	abstract void setFrameNumber();
	abstract BufferedImage getCurrentFrame();
}