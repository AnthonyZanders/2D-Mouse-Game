import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.Random;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements ActionListener {
    //ADDED SPRITES
	private Image mouseSprite1;
    private Image mouseSprite2;
    private Image mouseSpriteRight1;
    private Image mouseSpriteRight2;
    private Image mouseSpriteLeft1;
    private Image mouseSpriteLeft2;
    private Image mouseSpriteUp1;
    private Image mouseSpriteUp2;
    private Image backgroundImage;
    private Image lastImage;
    
	private long lastAnimationTime = 0;
	private long animationDelay = 100; // 100ms delay (10 frames per second)
    
    private Image cheese;
    
    private Image cheeseTrap;
    int numCheeseTraps = 3; // Adjust this to the number of cheese traps you want
    int[] cheeseTrapX = new int[numCheeseTraps];
    int[] cheeseTrapY = new int[numCheeseTraps];
    boolean[] cheeseTrapMoved = new boolean[numCheeseTraps]; // Track if each trap has moved

    
    private Image catSprite;
    
    private boolean frameToggle;
    
    //still need to make sprite size bigger as constant variable
    static final int screenWidth = 600;
    static final int screenHeight = 600;
    static final int SpriteSize = 25;// Size of the player and obstacles
    static final int FPS = 50;
    static final int NUM_OBSTACLES = 5;
    
    int PlayerX = 200;
    int PlayerY = 200;
    int fruitX = 100;
    int fruitY = 100;
    
    int PlayerSpeed = 7;
    int Score = 0;
    Random random;
    boolean GameRunning = true; // Start the game in running state
    Timer time;
    KeyHandler keyH = new KeyHandler();

    int[] obstacleX = new int[NUM_OBSTACLES];
    int[] obstacleY = new int[NUM_OBSTACLES];
    int[] obstacleDX = new int[NUM_OBSTACLES];// X velocity of each obstacle
    int[] obstacleDY = new int[NUM_OBSTACLES]; // Y velocity of each obstacle
    

    
    String[] catColors = {"orange", "black", "gray"};
    
    private Rectangle playerRect;

    public GamePanel() {
      //ADDED FINDS IMAGES FROM SOURCE PACK
    	try {
            mouseSprite1 = ImageIO.read(getClass().getResource("mouseSprite1.png"));
            mouseSprite2 = ImageIO.read(getClass().getResource("mouseSprite2.png"));
            mouseSpriteRight1 = ImageIO.read(getClass().getResource("mouseSpriteRight1.png"));
            mouseSpriteRight2 = ImageIO.read(getClass().getResource("mouseSpriteRight2.png"));
            mouseSpriteUp1 = ImageIO.read(getClass().getResource("mouseSpriteUp1.png"));
            mouseSpriteUp2 = ImageIO.read(getClass().getResource("mouseSpriteUp2.png"));
            backgroundImage = ImageIO.read(getClass().getResource("backgroundImage.jpg"));
            mouseSpriteLeft1 = ImageIO.read(getClass().getResource("mouseSpriteLeft1.png"));
            mouseSpriteLeft2 = ImageIO.read(getClass().getResource("mouseSpriteLeft2.png"));
            cheeseTrap = ImageIO.read(getClass().getResource("cheese_trap.png"));
            random = new Random();
            String randomCat = catColors[random.nextInt(catColors.length)];
            catSprite = ImageIO.read(getClass().getResource(randomCat + ".png"));
            



        
            cheese = ImageIO.read(getClass().getResource("cheese.png"));
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        frameToggle = true;
        random = new Random();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        Start();
        
    }

    public void Start() {
        GameRunning = true;
        time = new Timer(1000 / FPS, this);
        time.start();
        Obstacles();
    }

    private void Obstacles() {
        for (int i = 0; i < NUM_OBSTACLES; i++) {
            obstacleX[i] = random.nextInt(screenWidth - SpriteSize);
            obstacleY[i] = random.nextInt(screenHeight - SpriteSize);
            obstacleDX[i] = random.nextInt(5) + 1; // Random velocity between 1 and 5
            obstacleDY[i] = random.nextInt(5) + 1; // Random velocity between 1 and 5
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //background immage try and catch
        if (backgroundImage == null) {
            System.err.println("Background image is null");
        } else {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        if (GameRunning) {
            Sprites(g);
            gameScore(g);
        } else {
            GameOver(g); // Display "Game Over" if the game is not running
        }
    }

    public void Sprites(Graphics g) {
    	//ADDED THIS ALL (sprite size is constnatly changing and messes up with hit box need to make constant)
        //fruit / cheese creation sorry still need to change variable name
    	g.drawImage(cheese, fruitX, fruitY, SpriteSize + 30, SpriteSize + 30, this);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAnimationTime >= animationDelay) {
             frameToggle = !frameToggle;
             lastAnimationTime = currentTime;
         }
        //personal framed animation for arrow keys mouse
        if (keyH.upPressed) {
            if (frameToggle) {
                g.drawImage(mouseSpriteUp1, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);
                lastImage = mouseSpriteUp1;
            } else {
                g.drawImage(mouseSpriteUp2, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);
                lastImage = mouseSpriteUp2;
            }
        } else if (keyH.rightPressed) {
            if (frameToggle) {
                g.drawImage(mouseSpriteRight1, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);
                lastImage = mouseSpriteRight1;
            } else {
                g.drawImage(mouseSpriteRight2, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);
                lastImage = mouseSpriteRight2;
            }
        } else if (keyH.leftPressed) {
            if (frameToggle) {
                g.drawImage(mouseSpriteLeft1, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);
                lastImage = mouseSpriteLeft1;
            } else {
                g.drawImage(mouseSpriteLeft2, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);
                lastImage = mouseSpriteLeft2;
            }
        } else if (keyH.downPressed) {
            if (frameToggle) {
                g.drawImage(mouseSprite1, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);
                lastImage = mouseSprite1;
            } else {
                g.drawImage(mouseSprite2, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);
                lastImage = mouseSprite2;
            }
        
            // Use lastImage if no key is pressed
        }else {
            g.drawImage(lastImage, PlayerX, PlayerY, SpriteSize + 30, SpriteSize + 30, this);

        }

        frameToggle = !frameToggle;

        //cat sprite
        for (int i = 0; i < NUM_OBSTACLES; i++) {
        	g.drawImage(catSprite, obstacleX[i], obstacleY[i], SpriteSize+60, SpriteSize+60, this);
        }
        //cheese trap sprite
        for (int i = 0; i < numCheeseTraps; i++) {
            g.drawImage(cheeseTrap, cheeseTrapX[i], cheeseTrapY[i], SpriteSize + 30, SpriteSize + 30, this);
        }
    }

    public void gameScore(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + Score, 10, 20);
    }

    private boolean checkObstacleCollision() {
        Rectangle playerRect = new Rectangle(PlayerX, PlayerY, SpriteSize, SpriteSize);
        for (int i = 0; i < numCheeseTraps; i++) {
            Rectangle cheeseTrapRect = new Rectangle(cheeseTrapX[i], cheeseTrapY[i], SpriteSize, SpriteSize);
            if (playerRect.intersects(cheeseTrapRect)) {
                return true; // Collision detected with any cheese trap
            }
        }
        for (int i = 0; i < NUM_OBSTACLES; i++) {
            Rectangle obstacleRect = new Rectangle(obstacleX[i], obstacleY[i], SpriteSize, SpriteSize);
            if (playerRect.intersects(obstacleRect)) {
                return true;
            }
        }        
        return false;
    }
    //ADDED THIS - still a bit messy because of the sprite creation up above having numbers being added need to make constant
    private boolean spawnProtection(int x, int y, int radius) {
        int playerCenterX = PlayerX + SpriteSize / 2;
        int playerCenterY = PlayerY + SpriteSize / 2;
        int dx = x - playerCenterX;
        int dy = y - playerCenterY;
        return dx * dx + dy * dy <= radius * radius;
    }

    public void KeyInputs() {
        if (keyH.upPressed) {
            PlayerY = Math.max(0, PlayerY - PlayerSpeed);
        }
        if (keyH.downPressed) {
            PlayerY = Math.min(screenHeight - SpriteSize, PlayerY + PlayerSpeed);
        }
        if (keyH.leftPressed) {
            PlayerX = Math.max(0, PlayerX - PlayerSpeed);
        }
        if (keyH.rightPressed) {
            PlayerX = Math.min(screenWidth - SpriteSize, PlayerX + PlayerSpeed);
        }
    }

    public boolean checkCollision() {
        int circleCenterX = fruitX + SpriteSize / 2;
        int circleCenterY = fruitY + SpriteSize / 2;
        int circleRadius = SpriteSize / 2;

        playerRect = new Rectangle(PlayerX, PlayerY, SpriteSize, SpriteSize);
        for (int x = PlayerX; x < PlayerX + SpriteSize; x++) {
            for (int y = PlayerY; y < PlayerY + SpriteSize; y++) {
                int dx = x - circleCenterX;
                int dy = y - circleCenterY;
                if (dx * dx + dy * dy <= circleRadius * circleRadius) {
                    return true;
                }
            }
        }
        return false;
    }

    private void moveObstacles() {
        for (int i = 0; i < NUM_OBSTACLES; i++) {
            
            obstacleX[i] += obstacleDX[i];
            obstacleY[i] += obstacleDY[i];

            // Bounce off the left or right edge
            if (obstacleX[i] <= 0 || obstacleX[i] >= screenWidth - SpriteSize) {
                obstacleDX[i] = -obstacleDX[i];
            }

            // Bounce off the top or bottom edge
            if (obstacleY[i] <= 0 || obstacleY[i] >= screenHeight - SpriteSize) {
                obstacleDY[i] = -obstacleDY[i];
            }

        }
    }

    
    public void moveFruit() {
        int newX, newY;
        do {
            newX = random.nextInt(screenWidth - SpriteSize);
            newY = random.nextInt(screenHeight - SpriteSize);
            //added for spawn protection
        } while (spawnProtection(newX, newY, SpriteSize / 2));
        fruitX = newX;
        fruitY = newY;
        Score++;
    }
    public void moveCheeseTrap() {
    	for (int i = 0; i < numCheeseTraps; i++) {
        if(cheeseTrapMoved[i]) {
        	int newX, newY;
    	do {
            newX = random.nextInt(screenWidth - SpriteSize);
            newY = random.nextInt(screenHeight - SpriteSize);
            //added for spawn proterction
        } while (spawnProtection(newX, newY, SpriteSize / 2));
        cheeseTrapX[i] = newX;
        cheeseTrapY[i] = newY;
    }
    	}
    }
//WORK IN PROGRESS ADDED (VELOCITY DOESNT RESET)
    private void resetObstacles() {
        for (int i = 0; i < NUM_OBSTACLES; i++) {
            int newX, newY;
            do {
                newX = random.nextInt(screenWidth - SpriteSize);
                newY = random.nextInt(screenHeight - SpriteSize);
            } while (spawnProtection(newX, newY, SpriteSize / 2));
            obstacleX[i] = newX;
            obstacleY[i] = newY;
        }
    }
    
    public void GameOver(Graphics g) {
    	//this.setBackground
        g.setColor(Color.GREEN);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics Over = getFontMetrics(g.getFont());
        g.drawString("Game Over", (screenWidth - Over.stringWidth("Game Over")) / 2, screenHeight / 2);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("            Final Score: " + Score, (screenWidth - Over.stringWidth("Final Score:" + Score)) / 2, screenHeight / 2 + 60);
        //WORK IN PROGRESS GAME RESET
        if (keyH.reset) {
            resetGameState();
        	Start();
        	
        }
    }
  //WORK IN PROGRESS ADDED (VELOCITY DOESNT RESET)
    private void resetGameState() {
        PlayerX = 200;
        PlayerY = 200;
        PlayerSpeed = 7;
        fruitX = 100;
        fruitY = 100;
        Score = 0;
        keyH.reset = false;
        keyH.upPressed = false;
        keyH.downPressed = false;
        keyH.leftPressed = false;
        keyH.rightPressed = false;
        resetObstacles();
	}

  

    public void actionPerformed(ActionEvent e) {
        if (GameRunning) {
            KeyInputs();
            moveObstacles();

            if (checkCollision()) {
                moveFruit();
                moveCheeseTrap(); // Move cheese traps when fruit is collected
                for (int i = 0; i < numCheeseTraps; i++) {
                    cheeseTrapMoved[i] = true; // Reset cheese trap movement 
                }
            }

            if (checkObstacleCollision()) {
                GameRunning = false;
            }

            repaint();
        } else {
            repaint();
        }
    }

    // New method to check if any cheese trap needs to be moved
    public boolean checkCheeseTrapMovement() {
        for (int i = 0; i < numCheeseTraps; i++) {
            if (cheeseTrapMoved[i]) {
                return true; // At least one cheese trap needs to be moved
            }
        }
        return false; // No cheese trap needs to be moved
    }

    class KeyHandler extends KeyAdapter {
        boolean upPressed, downPressed, leftPressed, rightPressed, reset;

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    upPressed = true;
                    break;
                case KeyEvent.VK_S:
                    downPressed = true;
                    break;
                case KeyEvent.VK_A:
                    leftPressed = true;
                    break;
                case KeyEvent.VK_D:
                    rightPressed = true;
                    break;
                    //WORK IN PROGRESS : added r to reset when game over but isnt working
                case KeyEvent.VK_R:
                	reset = true;
                	break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    upPressed = false;
                    break;
                case KeyEvent.VK_S:
                    downPressed = false;
                    break;
                case KeyEvent.VK_A:
                    leftPressed = false;
                    break;
                case KeyEvent.VK_D:
                    rightPressed = false;
                    break;
                    //WORK IN PROGRESS : added r to reset when game over but isnt working
                case KeyEvent.VK_R:
                	reset = false;
                	break;
            }
        }
    }



}

