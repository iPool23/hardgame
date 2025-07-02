package hardgame;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;

public final class GamePanel extends JPanel implements Runnable, KeyListener {

    // Nombre del Usuario
    private String playerName;

    // Sonidos
    private Clip ballSound;
    private Clip goalSound;
    private Clip pointSound;
    private Clip gameoverSound;
    private Clip loveSound;

    // Invulnerabilidad
    private boolean invulnerabilityActive = false;
    private long invulnerabilityStartTime = 0;
    private final int INVULNERABILITY_DURATION = 3000;

    // Logros
    private final ArrayList<Logro> logros;

    // Pausa
    private boolean paused = false;
    private boolean gameOver = false;
    private boolean winGame = false;

    // Fps
    private long lastTime = System.currentTimeMillis();
    private int fps;

    // Fondo
    private static final int BOARD_SIZE = 16;
    private final int squareSize;
    public static Color nuevoColor1 = new Color(178, 181, 252);
    public static Color nuevoColor2 = new Color(246, 246, 254);

    // Goal
    public static Color nuevoColor3 = new Color(186, 253, 174);

    // Level Maximo
    private static final int MAX_LEVEL = 20;

    // Ancho y altura del panel
    static final int PANEL_WIDTH = 400;
    static final int PANEL_HEIGHT = 400;

    // Tamaño de los elementos del juego
    private static final int SQUARE_SIZE = 20;
    private static final int GOAL_SIZE = 20;

    // Tiempo de espera para actualizar y pintar los elementos del juego
    private static final int DELAY = 10;

    // Declaración de las variables principales del juego
    private Square square; // El cuadrado controlado por el jugador
    private Goal goal; // La meta del juego
    private Ball[] balls; // Los obstáculos móviles que evita el jugador
    private Point[] points; // Los puntos que el jugador debe recolectar
    private Love[] loves; // El aumentador de vida
    private Magnet[] magnets; // El iman que atrae los puntos
    private Shield[] shields; // El escudo que protege al jugador
    private int level; // El nivel actual del jugador
    private int vidas; // Limite de Vidas
    private int maxLives; // Contador registro de vidas
    private int maxDeath; // Contador muertes en la partida
    private final boolean running; // Indica si el juego está corriendo o no
    private final Thread thread; // Hilo de ejecución del juego
    private int pointCount; // Contador de puntos

    public GamePanel() {

        try {
            AudioInputStream ballAudioInputStream = AudioSystem.getAudioInputStream(
                    GamePanel.class.getResourceAsStream("/hardgame/sounds/muerte.wav"));
            ballSound = AudioSystem.getClip();
            ballSound.open(ballAudioInputStream);

            AudioInputStream goalAudioInputStream = AudioSystem.getAudioInputStream(
                    GamePanel.class.getResourceAsStream("/hardgame/sounds/victoria.wav"));
            goalSound = AudioSystem.getClip();
            goalSound.open(goalAudioInputStream);

            AudioInputStream explosionAudioInputStream = AudioSystem.getAudioInputStream(
                    GamePanel.class.getResourceAsStream("/hardgame/sounds/coin.wav"));
            pointSound = AudioSystem.getClip();
            pointSound.open(explosionAudioInputStream);

            AudioInputStream overAudioInputStream = AudioSystem.getAudioInputStream(
                    GamePanel.class.getResourceAsStream("/hardgame/sounds/gameover.wav"));
            gameoverSound = AudioSystem.getClip();
            gameoverSound.open(overAudioInputStream);

            AudioInputStream loveAudioInputStream = AudioSystem.getAudioInputStream(
                    GamePanel.class.getResourceAsStream("/hardgame/sounds/love.wav"));
            loveSound = AudioSystem.getClip();
            loveSound.open(loveAudioInputStream);

        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
        }

        // Inicializa
        level = 1;
        vidas = 3;
        running = true;
        pointCount = 0;
        fps = 0;
        maxLives = 0;
        maxDeath = 0;

        // Crear los objetos Logro
        logros = new ArrayList<>();

        logros.add(new Logro("Primer puntos", 1));
        logros.add(new Logro("Diez puntos", 10));
        logros.add(new Logro("Cincuenta puntos", 50));
        logros.add(new Logro("Cien puntos", 100));
        logros.add(new Logro("Doscientos puntos", 200));
        logros.add(new Logro("Trescientos puntos", 300));
        logros.add(new Logro("Cuatrocientos puntos", 400));
        logros.add(new Logro("Quinientos puntos", 500));
        logros.add(new Logro("Pasarse el nivel sin perder vidas", 0));

        // Obtener el tamaño del panel y calcular el tamaño de cada celda
        Dimension size = getPreferredSize();
        squareSize = Math.min(size.width / BOARD_SIZE, size.height / BOARD_SIZE);

        // Configuración del panel
        setFocusable(true);
        requestFocus();

        resetObjects();

        // Inicializa y comienza el hilo de ejecución del juego
        thread = new Thread(this);
        thread.start();
    }

    // Principal del juego

    @Override
    public void run() {

        // Agrega un KeyListener para controlar al jugador
        addKeyListener(this);

        while (running) {
            update(); // Actualiza la posición de los elementos del juego
            repaint(); // Vuelve a pintar los elementos del juego

            // Calcula los FPS
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastTime;
            if (elapsedTime > 0) {
                fps = (int) (1000 / elapsedTime);
            }
            lastTime = currentTime;

            try {
                Thread.sleep(DELAY); // Espera un tiempo para actualizar y pintar de nuevo
            } catch (InterruptedException e) {
            }
        }
    }

    // Actualiza

    private void update() {
        if (!paused) {
            moveObjects();
            checkPoints();
            checkGoal();
            checkBalls();
            checkMagnet();
            checkLoves();
            checkShield();
        }
    }

    // Verifica los Square, Point, Goal & Balls

    private void moveObjects() {
        square.move();
        for (Ball ball : balls) {
            ball.move();
        }
        for (Magnet magnet : magnets) {
            magnet.move();
        }
        for (Shield shield : shields) {
            shield.move();
        }
    }

    private void checkPoints() {
        for (Point point : points) {
            if (square.intersects(point) && !point.isCollected()) {
                point.collect();
                pointCount++;
                pointSound.setFramePosition(0); // Reinicia el sonido
                pointSound.start(); // Reproduce el sonido
                mLogros();
            }
        }
    }

    private void checkGoal() {
        Logro logroDesbloqueado = null; // variable para guardar el logro desbloqueado
        if (square.intersects(goal) && allPointsCollected()) {
            advanceLevel();
            goalSound.setFramePosition(0); // Reinicia el sonido
            goalSound.start(); // Reproduce el sonido
            if (vidas >= 3) { // Solo si no se ha perdido ninguna vida durante la partida
                for (Logro logro : logros) {
                    if (!logro.isDesbloqueado() && logro.getNombre().equals("Pasarse el nivel sin perder vidas")) {
                        logro.aumentarProgreso(1);
                        logroDesbloqueado = logro; // guardamos el logro desbloqueado
                    }
                }
            }
        }
        if (logroDesbloqueado != null) { // si se desbloqueó algún logro
            logroDesbloqueado.setMostrarMensajeLogro(false);
            mostrarLogro(logroDesbloqueado.getMensajeLogro());
        }
    }

    private void checkBalls() {
        for (Ball ball : balls) {
            if (square.intersects(ball)) {
                if (!square.invincible) {
                    vidas--; // Restar una vida
                    maxDeath++; // Aumentar contador de muertes
                    if (vidas == 0) {
                        gameoverSound.setFramePosition(0); // Reinicia el sonido
                        gameoverSound.start(); // Reproduce el sonido
                        paused = true;
                        gameOver = true;
                    } else {
                        ballSound.setFramePosition(0); // Reinicia el sonido
                        ballSound.start(); // Reproduce el sonido
                        resetObjects(); // reanudar el nivel actual
                    }
                }
            }
        }
    }

    private void checkLoves() {
        for (Love vida : loves) {
            if (square.intersects(vida) && !vida.isCollected()) {
                vida.collect();
                loveSound.setFramePosition(0); // Reinicia el sonido
                loveSound.start(); // Reproduce el sonido
                vidas++; // Suma una vida
                if (vidas > maxLives) {
                    maxLives = vidas;
                }
            }
        }
    }

    private void checkMagnet() {
        for (Magnet magnet : magnets) {
            if (square.intersects(magnet) && !magnet.isCollected()) {
                magnet.collect();
                // magnetSound.setFramePosition(0); // Reinicia el sonido
                // magnetSound.start(); // Reproduce el sonido
                for (Point point : points) {
                    if (!point.isCollected()) {
                        magnet.setDirection(point);
                        point.collect();
                        pointCount++;
                    }
                    mLogros();
                }
            }
        }
    }

    private void checkShield() {
        long currentTime = System.currentTimeMillis();

        for (Shield shield : shields) {
            if (square.intersects(shield) && !shield.isCollected()) {
                shield.collect();
                // shieldSound.setFramePosition(0); // Reinicia el sonido
                // shieldSound.start(); // Reproduce el sonido
                square.invincible = true; // Activar invulnerabilidad

                invulnerabilityActive = true;
                invulnerabilityStartTime = currentTime;
            }
        }

        if (invulnerabilityActive && currentTime - invulnerabilityStartTime >= INVULNERABILITY_DURATION) {
            square.invincible = false; // Desactivar invulnerabilidad después de 3 segundos
            invulnerabilityActive = false;
        }
    }

    // Actualizar progreso de logros
    private void mLogros(){
        for (Logro logro : logros) {
            if (!logro.isDesbloqueado() && !logro.getNombre().equals("Pasarse el nivel sin perder vidas")) {
                logro.aumentarProgreso(1);
            }
            if (logro.isDesbloqueado() && logro.isMostrarMensajeLogro()) {
                logro.setMostrarMensajeLogro(false);
                mostrarLogro(logro.getMensajeLogro());
            }
        }
    }


    // Muestra los logros en pantalla

    private void mostrarLogro(String mensajeLogro) {
        JLabel logroLabel = new JLabel(mensajeLogro);
        logroLabel.setForeground(Color.WHITE);
        logroLabel.setBackground(Color.BLACK);
        logroLabel.setOpaque(true);
        logroLabel.setFont(new Font("Courier New", Font.BOLD, 10));
        logroLabel.setSize(logroLabel.getPreferredSize());
        logroLabel.setLocation(10, getHeight() - logroLabel.getHeight() - 10);
        add(logroLabel);

        // Crea un temporizador para ocultar el JLabel después de 3 segundos
        Timer timer = new Timer(3000, (ActionEvent e) -> {
            remove(logroLabel);
            revalidate();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Siguietne nivel

    private void advanceLevel() {
        if (level < MAX_LEVEL) {
            level++;
            resetObjects();
        } else {
            paused = true;
            winGame = true;
        }
    }

    // Resetea estadisticas tras perder
    private void resetScore() {
        level = 1;
        pointCount = 0;
        maxLives = 0;
    }

    // Resetea el nivel
    private void resetLevel() {
        resetScore();
        resetObjects();
    }

    // Resetea los valores por predeterminado

    void resetObjects() {
        square = new Square(PANEL_WIDTH / 2 - SQUARE_SIZE / 2, PANEL_HEIGHT - SQUARE_SIZE - 10, SQUARE_SIZE);
        balls = new Ball[level + 4];
        points = new Point[level + 4];
        loves = new Love[1];
        magnets = new Magnet[1];
        shields = new Shield[1];

        for (int i = 0; i < balls.length; i++) {
            balls[i] = new Ball();
        }
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point();
        }
        for (int i = 0; i < loves.length; i++) {
            loves[i] = new Love();
        }
        for (int i = 0; i < magnets.length; i++) {
            magnets[i] = new Magnet();
        }
        for (int i = 0; i < shields.length; i++) {
            shields[i] = new Shield();
        }
        goal = new Goal(PANEL_WIDTH / 2 - GOAL_SIZE / 2, 10, GOAL_SIZE);
    }

    // Pintar Todo

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (gameOver) {
            drawGameOver(g2d);
        } else if (winGame) {
            drawWinGame(g2d);
        } else if (paused) {
            drawPaused(g);
        } else {
            drawPantalla(g);
        }
    }

    private void drawPantalla(Graphics g) {
        drawFondoPantalla(g);
        drawBorde(g);
        drawGreenBox(g);
        goal.draw(g);
        drawLove(g);
        drawBalls(g);
        drawPoints(g);
        drawMagnet(g);
        drawShield(g);
        square.draw(g);
        drawBoxLevelInfo(g);
    }

    // Pintar Pantalla

    private void drawFondoPantalla(Graphics g) {
        boolean isWhite = true;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (isWhite) {
                    g.setColor(nuevoColor1);
                } else {
                    g.setColor(nuevoColor2);
                }
                g.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }

    private void drawBorde(Graphics g) {
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        g.setColor(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(10));
        g2d.drawRect(0, 0, screenWidth, screenHeight);
    }

    private void drawBalls(Graphics g) {
        for (Ball ball : balls) {
            ball.draw(g);
        }
    }

    private void drawPoints(Graphics g) {
        for (Point point : points) {
            if (point.isVisible()) {
                point.draw(g);
            }
        }
    }

    private void drawLove(Graphics g) {
        for (Love love : loves) {
            if (!love.isCollected()) {
                love.draw(g);
            }
        }
    }

    private void drawMagnet(Graphics g) {
        for (Magnet magnet : magnets) {
            if (!magnet.isCollected()) {
                magnet.draw(g);
            }
        }
    }

    private void drawShield(Graphics g) {
        for (Shield shield : shields) {
            if (!shield.isCollected()) {
                shield.draw(g);
            }
        }
    }

    private void drawGreenBox(Graphics g) {
        // Dibujamos el cuadrado verde en el punto de inicio del jugador
        g.setColor(nuevoColor3);
        g.fillRect(185, 365, SQUARE_SIZE, SQUARE_SIZE);
    }

    // Pintar Cuadro de Estadisticas

    private void drawBoxLevelInfo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Squid Game inspired statistics box
        Color boxColor = new Color(25, 25, 25, 220); // Dark background with transparency
        Color borderColor = new Color(255, 70, 100); // Light red border
        Color accentColor = new Color(100, 255, 100); // Green accent
        int borderWidth = 2;

        // Obtenemos el tamaño del texto
        Font font = new Font("Arial", Font.BOLD, 12);
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();

        // Definimos el tamaño de las cadenas de texto
        String[] textStrings = { 
            "👤 " + playerName, 
            "🎯 Nivel: " + level, 
            "⚡ FPS: " + fps, 
            "💎 Puntos: " + pointCount,
            "❤️ Vidas: " + vidas 
        };
        int[] textWidths = new int[textStrings.length];
        int totalHeight = 0;

        for (int i = 0; i < textStrings.length; i++) {
            textWidths[i] = metrics.stringWidth(textStrings[i]);
            totalHeight += metrics.getHeight();
        }

        // Definimos el tamaño y posición del cuadro
        int boxWidth = Arrays.stream(textWidths).max().getAsInt() + 25;
        int boxHeight = totalHeight + 50;
        int boxX = 10;
        int boxY = 10;

        // Fondo con gradiente
        GradientPaint gradient = new GradientPaint(boxX, boxY, boxColor, 
                                                  boxX, boxY + boxHeight, new Color(45, 25, 45, 220));
        g2d.setPaint(gradient);
        g2d.fillRect(boxX, boxY, boxWidth, boxHeight);

        // Borde principal
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(borderWidth));
        g2d.drawRect(boxX, boxY, boxWidth, boxHeight);

        // Línea de acento superior
        g2d.setColor(accentColor);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(boxX, boxY, boxX + boxWidth, boxY);

        // Escribimos las cadenas de texto
        int y = boxY + metrics.getHeight() + 5;
        for (String textString : textStrings) {
            if (textString.contains("Vidas:")) {
                // Color coding for lives
                if (vidas >= 3) {
                    g2d.setColor(accentColor); // Green for high lives
                } else if (vidas == 2) {
                    g2d.setColor(new Color(255, 255, 100)); // Yellow for medium
                } else if (vidas == 1) {
                    g2d.setColor(new Color(255, 70, 100)); // Red for low
                } else {
                    vidas = 3;
                    g2d.setColor(accentColor);
                }
            } else if (textString.contains("Nivel:")) {
                g2d.setColor(new Color(255, 70, 100)); // Red for level
            } else if (textString.contains("Puntos:")) {
                g2d.setColor(new Color(100, 255, 100)); // Green for points
            } else {
                g2d.setColor(Color.WHITE); // White for other text
            }
            
            g2d.drawString(textString, boxX + 12, y);
            y += metrics.getHeight() + 8;
        }

        // Decorative elements
        g2d.setColor(new Color(255, 70, 100, 100));
        g2d.fillRect(boxX + boxWidth - 8, boxY + 5, 3, boxHeight - 10);
        
        g2d.setColor(new Color(100, 255, 100, 100));
        g2d.fillRect(boxX + 5, boxY + boxHeight - 8, boxWidth - 10, 3);
    }

    // Pintar Pausa

    private void drawPaused(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        drawPantalla(g);
        drawPausedText(g2d);
    }

    private void drawPausedText(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Semi-transparent overlay
        g2d.setColor(new Color(25, 25, 25, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Main pause text with Squid Game style
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        String pauseText = "⏸️ JUEGO PAUSADO";
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(pauseText);
        int x = (getWidth() - textWidth) / 2;
        int y = getHeight() / 2 - 20;
        
        // Shadow effect
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(pauseText, x + 3, y + 3);
        
        // Main text
        g2d.setColor(new Color(255, 70, 100));
        g2d.drawString(pauseText, x, y);
        
        // Instruction text
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String instructionText = "Presiona 'P' para continuar";
        FontMetrics smallMetrics = g2d.getFontMetrics();
        int instructionWidth = smallMetrics.stringWidth(instructionText);
        int instructionX = (getWidth() - instructionWidth) / 2;
        
        g2d.setColor(new Color(100, 255, 100));
        g2d.drawString(instructionText, instructionX, y + 40);
        
        // Decorative border
        g2d.setColor(new Color(255, 70, 100));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x - 20, y - 40, textWidth + 40, 100);
        
        g2d.setColor(new Color(100, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x - 25, y - 45, textWidth + 50, 110);
    }

    // Pintar Game Over
    private void drawGameOver(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Squid Game inspired Game Over screen
        GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 25), 
                                                  0, getHeight(), new Color(45, 25, 45));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Geometric patterns
        g2d.setColor(new Color(255, 70, 100, 30));
        for (int i = 0; i < 8; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            int[] xPoints = {x, x + 15, x + 7};
            int[] yPoints = {y, y, y - 15};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
        
        // Main title with shadow
        Font titleFont = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(titleFont);
        String gameOverText = "💀 ELIMINADO";
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int titleX = (getWidth() - titleMetrics.stringWidth(gameOverText)) / 2;
        int titleY = 80;
        
        // Shadow effect
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(gameOverText, titleX + 3, titleY + 3);
        
        // Main title
        g2d.setColor(new Color(255, 70, 100));
        g2d.drawString(gameOverText, titleX, titleY);
        
        // Decorative line under title
        g2d.setColor(new Color(100, 255, 100));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(titleX - 20, titleY + 10, titleX + titleMetrics.stringWidth(gameOverText) + 20, titleY + 10);
        
        // Statistics box
        int boxX = 50;
        int boxY = 130;
        int boxWidth = getWidth() - 100;
        int boxHeight = 160;
        
        // Box background
        g2d.setColor(new Color(25, 25, 25, 200));
        g2d.fillRect(boxX, boxY, boxWidth, boxHeight);
        
        // Box border
        g2d.setColor(new Color(255, 70, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(boxX, boxY, boxWidth, boxHeight);
        
        // Accent lines
        g2d.setColor(new Color(100, 255, 100));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(boxX, boxY, boxX + boxWidth, boxY);
        
        // Player information
        Font infoFont = new Font("Arial", Font.BOLD, 16);
        g2d.setFont(infoFont);
        
        String[] infoLines = {
            "👤 Jugador: " + this.playerName,
            "🎯 Nivel alcanzado: " + level,
            "💎 Puntos totales: " + pointCount,
            "❤️ Vidas máximas: " + maxLives,
            "💀 Muertes: " + maxDeath
        };
        
        Color[] infoColors = {
            Color.WHITE,
            new Color(255, 70, 100),
            new Color(100, 255, 100),
            new Color(255, 255, 100),
            new Color(255, 70, 100)
        };
        
        int infoStartY = boxY + 30;
        for (int i = 0; i < infoLines.length; i++) {
            g2d.setColor(infoColors[i]);
            g2d.drawString(infoLines[i], boxX + 20, infoStartY + (i * 25));
        }
        
        // Instructions
        Font instructionFont = new Font("Arial", Font.PLAIN, 14);
        g2d.setFont(instructionFont);
        
        String[] instructions = {
            "🔄 Presiona 'R' para intentar de nuevo",
            "🚪 Presiona 'ESC' para volver al menú"
        };
        
        int instructionY = boxY + boxHeight + 40;
        for (int i = 0; i < instructions.length; i++) {
            g2d.setColor(i == 0 ? new Color(100, 255, 100) : new Color(255, 255, 100));
            FontMetrics instrMetrics = g2d.getFontMetrics();
            int instrX = (getWidth() - instrMetrics.stringWidth(instructions[i])) / 2;
            g2d.drawString(instructions[i], instrX, instructionY + (i * 25));
        }
        
        // Decorative border around the whole screen
        g2d.setColor(new Color(255, 70, 100));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(5, 5, getWidth() - 10, getHeight() - 10);
    }

    // Preferencia de tamaño panel

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400); // Tamaño predeterminado del panel
    }

    // Controles del jugador

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT
                || e.getKeyCode() == KeyEvent.VK_A) {
            square.setDx(-1);
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT
                || e.getKeyCode() == KeyEvent.VK_D) {
            square.setDx(1);
        }

        if (e.getKeyCode() == KeyEvent.VK_UP
                || e.getKeyCode() == KeyEvent.VK_W) {
            square.setDy(-1);
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN
                || e.getKeyCode() == KeyEvent.VK_S) {
            square.setDy(1);
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            paused = true;
            
            // Create custom exit confirmation dialog with Squid Game style
            JDialog exitDialog = new JDialog();
            exitDialog.setTitle("CONFIRMACIÓN DE SALIDA");
            exitDialog.setModal(true);
            exitDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            JPanel panel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 25),
                                                             0, getHeight(), new Color(45, 25, 45));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 70, 100), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            
            JLabel messageLabel = new JLabel("<html><center><h3 style='color: #FF4664;'>⚠️ CONFIRMACIÓN</h3><p style='color: white;'>¿Estás seguro de que deseas<br>abandonar la partida?</p></center></html>");
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(messageLabel, BorderLayout.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            buttonPanel.setOpaque(false);
            
            JButton yesButton = new JButton("SÍ, SALIR") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Force background color
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Draw border
                    g2d.setColor(new Color(100, 255, 100));
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
                    
                    // Draw text
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(getText(), x, y);
                }
            };
            yesButton.setFont(new Font("Arial", Font.BOLD, 14));
            yesButton.setForeground(Color.WHITE);
            yesButton.setBackground(new Color(180, 70, 70));
            yesButton.setFocusPainted(false);
            yesButton.setOpaque(false);
            yesButton.setContentAreaFilled(false);
            yesButton.setBorderPainted(false);
            yesButton.addActionListener(ev -> {
                exitDialog.dispose();
                MenuPrincipal.showMenu();
            });
            
            JButton noButton = new JButton("CONTINUAR") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Force background color
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Draw border
                    g2d.setColor(new Color(255, 70, 100));
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
                    
                    // Draw text
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(getFont());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(getText(), x, y);
                }
            };
            noButton.setFont(new Font("Arial", Font.BOLD, 14));
            noButton.setForeground(Color.WHITE);
            noButton.setBackground(new Color(80, 180, 80));
            noButton.setFocusPainted(false);
            noButton.setOpaque(false);
            noButton.setContentAreaFilled(false);
            noButton.setBorderPainted(false);
            noButton.addActionListener(ev -> {
                exitDialog.dispose();
                paused = false;
            });
            
            buttonPanel.add(yesButton);
            buttonPanel.add(noButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            exitDialog.setContentPane(panel);
            exitDialog.setPreferredSize(new Dimension(300, 200));
            exitDialog.pack();
            exitDialog.setLocationRelativeTo(null);
            exitDialog.setVisible(true);
        }

        if (e.getKeyCode() == KeyEvent.VK_R) {
            paused = false;
            resetLevel();
            gameOver = false;
            winGame = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT
                || e.getKeyCode() == KeyEvent.VK_RIGHT
                || e.getKeyCode() == KeyEvent.VK_A
                || e.getKeyCode() == KeyEvent.VK_D) {
            square.setDx(0);
        }

        if (e.getKeyCode() == KeyEvent.VK_UP
                || e.getKeyCode() == KeyEvent.VK_DOWN
                || e.getKeyCode() == KeyEvent.VK_W
                || e.getKeyCode() == KeyEvent.VK_S) {
            square.setDy(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Recollectar todos los puntos

    private boolean allPointsCollected() {
        for (Point p : points) {
            if (!p.isCollected()) {
                return false;
            }
        }
        return true;
    }

    // Nombre del Jugador
    public void setPlayerName(String name) {
        playerName = name;
    }

    public static void setNuevoColor1(Color color) {
        nuevoColor1 = color;
    }

    public static void setNuevoColor2(Color color) {
        nuevoColor2 = color;
    }

    public static void setColorGoal(Color color) {
        nuevoColor3 = color;
    }

    private void drawWinGame(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Squid Game inspired Win screen
        GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 25), 
                                                  0, getHeight(), new Color(25, 45, 25));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Celebratory geometric patterns
        g2d.setColor(new Color(100, 255, 100, 40));
        for (int i = 0; i < 12; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            int[] xPoints = {x, x + 20, x + 10};
            int[] yPoints = {y, y, y - 20};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
        
        // Victory circles
        g2d.setColor(new Color(255, 255, 100, 30));
        for (int i = 0; i < 6; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            g2d.fillOval(x, y, 25, 25);
        }
        
        // Main title with shadow
        Font titleFont = new Font("Arial", Font.BOLD, 32);
        g2d.setFont(titleFont);
        String winText = "🏆 ¡SUPERVIVIENTE!";
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int titleX = (getWidth() - titleMetrics.stringWidth(winText)) / 2;
        int titleY = 80;
        
        // Shadow effect
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(winText, titleX + 3, titleY + 3);
        
        // Main title
        g2d.setColor(new Color(100, 255, 100));
        g2d.drawString(winText, titleX, titleY);
        
        // Decorative line under title
        g2d.setColor(new Color(255, 255, 100));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(titleX - 20, titleY + 10, titleX + titleMetrics.stringWidth(winText) + 20, titleY + 10);
        
        // Congratulations message
        Font subFont = new Font("Arial", Font.BOLD, 18);
        g2d.setFont(subFont);
        String congrats = "¡Has completado todos los niveles!";
        FontMetrics subMetrics = g2d.getFontMetrics();
        int congratsX = (getWidth() - subMetrics.stringWidth(congrats)) / 2;
        g2d.setColor(new Color(255, 255, 100));
        g2d.drawString(congrats, congratsX, titleY + 40);
        
        // Statistics box
        int boxX = 50;
        int boxY = 140;
        int boxWidth = getWidth() - 100;
        int boxHeight = 160;
        
        // Box background with victory gradient
        GradientPaint boxGradient = new GradientPaint(boxX, boxY, new Color(25, 45, 25, 200),
                                                     boxX, boxY + boxHeight, new Color(45, 65, 45, 200));
        g2d.setPaint(boxGradient);
        g2d.fillRect(boxX, boxY, boxWidth, boxHeight);
        
        // Box border
        g2d.setColor(new Color(100, 255, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(boxX, boxY, boxWidth, boxHeight);
        
        // Accent lines
        g2d.setColor(new Color(255, 255, 100));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(boxX, boxY, boxX + boxWidth, boxY);
        
        // Player information
        Font infoFont = new Font("Arial", Font.BOLD, 16);
        g2d.setFont(infoFont);
        
        String[] infoLines = {
            "🎉 Campeón: " + this.playerName,
            "🎯 Niveles completados: " + level,
            "💎 Puntos finales: " + pointCount,
            "❤️ Vidas máximas: " + maxLives,
            "💀 Caídas totales: " + maxDeath
        };
        
        Color[] infoColors = {
            new Color(255, 255, 100),
            new Color(100, 255, 100),
            new Color(100, 255, 100),
            new Color(255, 255, 100),
            new Color(255, 150, 150)
        };
        
        int infoStartY = boxY + 30;
        for (int i = 0; i < infoLines.length; i++) {
            g2d.setColor(infoColors[i]);
            g2d.drawString(infoLines[i], boxX + 20, infoStartY + (i * 25));
        }
        
        // Instructions
        Font instructionFont = new Font("Arial", Font.PLAIN, 14);
        g2d.setFont(instructionFont);
        
        String[] instructions = {
            "🔄 Presiona 'R' para jugar de nuevo",
            "🚪 Presiona 'ESC' para volver al menú"
        };
        
        int instructionY = boxY + boxHeight + 40;
        for (int i = 0; i < instructions.length; i++) {
            g2d.setColor(i == 0 ? new Color(255, 255, 100) : new Color(100, 255, 100));
            FontMetrics instrMetrics = g2d.getFontMetrics();
            int instrX = (getWidth() - instrMetrics.stringWidth(instructions[i])) / 2;
            g2d.drawString(instructions[i], instrX, instructionY + (i * 25));
        }
        
        // Decorative border around the whole screen
        g2d.setColor(new Color(100, 255, 100));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRect(5, 5, getWidth() - 10, getHeight() - 10);
        
        // Victory sparkles effect
        g2d.setColor(new Color(255, 255, 100));
        for (int i = 0; i < 15; i++) {
            int x = (int)(Math.random() * getWidth());
            int y = (int)(Math.random() * getHeight());
            g2d.fillOval(x, y, 3, 3);
        }
    }

}