package hardgame;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class MenuPrincipal extends JFrame {

    public static JFrame gameFrame;
    public static JFrame menuFrame;
    public static JPanel menuPanel;
    private Clip soundBoton;

    public MenuPrincipal() {
        try {
            AudioInputStream ballAudioInputStream = AudioSystem.getAudioInputStream(
                    GamePanel.class.getResourceAsStream("/hardgame/sounds/boton.wav"));
            soundBoton = AudioSystem.getClip();
            soundBoton.open(ballAudioInputStream);
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
        }
    }

    public static void menu() {
        SwingUtilities.invokeLater(() -> {
            menuFrame = new MenuPrincipal();
            configureMenu();
        });
    }

    private static void configureMenu() {
        setLookAndFeel();
        setupFrame();
        createMenuPanel();
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
        }
    }

    private static void setupFrame() {
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setResizable(false);
    }

    private static void createMenuPanel() {
        setMenuPanelBackground();

        menuPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Squid Game gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 25), 
                                                         0, getHeight(), new Color(45, 25, 45));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Geometric patterns (minimal triangles)
                g2d.setColor(new Color(255, 70, 100, 30)); // Light red with transparency
                for (int i = 0; i < 5; i++) {
                    int x = (int)(Math.random() * getWidth());
                    int y = (int)(Math.random() * getHeight());
                    int[] xPoints = {x, x + 20, x + 10};
                    int[] yPoints = {y, y, y - 20};
                    g2d.fillPolygon(xPoints, yPoints, 3);
                }
            }
        };
        menuPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 70, 100), 3),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        addTitleLabel();
        addStartButton();
        addLearnButton();
        addDifficultyButton();
        addExitButton();

        menuFrame.add(menuPanel);
        menuFrame.pack();
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    private static void setMenuPanelBackground() {
        // Squid Game inspired colors - Configurar UIManager para evitar herencia de colores
        UIManager.put("OptionPane.background", new Color(25, 25, 25)); // Dark background
        UIManager.put("Panel.background", new Color(25, 25, 25));
        UIManager.put("OptionPane.messageForeground", new Color(250, 250, 250));
        
        // Configurar botones específicamente
        UIManager.put("Button.background", new Color(50, 50, 50));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", new Color(255, 70, 100));
        UIManager.put("Button.focus", new Color(100, 255, 100));
        
        // Deshabilitar efectos del sistema
        UIManager.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
    }

    private static void addTitleLabel() {
        JLabel titleLabel = new JLabel("EL JUEGO MÁS DIFÍCIL") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setFont(getFont());
                g2d.drawString(getText(), 3, getHeight() - 7);
                
                // Main text
                g2d.setColor(new Color(255, 70, 100)); // Light red
                g2d.drawString(getText(), 0, getHeight() - 10);
                
                // Accent line
                g2d.setColor(new Color(100, 255, 100)); // Green
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(0, getHeight() - 3, getWidth(), getHeight() - 3);
            }
        };
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 70, 100));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(350, 50));
        
        GridBagConstraints gbcTitleLabel = new GridBagConstraints();
        gbcTitleLabel.gridx = 0;
        gbcTitleLabel.gridy = 0;
        gbcTitleLabel.gridwidth = 2;
        gbcTitleLabel.insets = new Insets(0, 0, 30, 0);
        menuPanel.add(titleLabel, gbcTitleLabel);
    }

    private static void addStartButton() {
        JButton startButton = createCustomButton("Iniciar juego", new Color(50, 50, 50), new Color(255, 70, 100));
        
        // Add hover effects
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(255, 70, 100));
                startButton.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(new Color(50, 50, 50));
                startButton.repaint();
            }
        });
        
        startButton.addActionListener((ActionEvent e) -> {

            playButtonSound();

            // Custom name input dialog with Squid Game style
            JDialog nameDialog = new JDialog();
            nameDialog.setTitle("REGISTRO DE JUGADOR");
            nameDialog.setModal(true);
            nameDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
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
            
            // Title
            JLabel titleLabel = new JLabel("<html><center><h2 style='color: #FF4664;'>👤 IDENTIFICA TU JUGADOR</h2></center></html>");
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(titleLabel, BorderLayout.NORTH);
            
            // Input panel
            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setOpaque(false);
            inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            
            JLabel nameLabel = new JLabel("NOMBRE:");
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setForeground(new Color(100, 255, 100));
            
            JTextField playerNameField = new JTextField(15) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fondo blanco sólido
                    g2d.setColor(new Color(245, 245, 245));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Borde con efecto de profundidad
                    g2d.setColor(new Color(255, 70, 100));
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
                    
                    // Sombra interna
                    g2d.setColor(new Color(200, 200, 200, 100));
                    g2d.drawRect(3, 3, getWidth() - 6, getHeight() - 6);
                    
                    // Llamar al método padre para dibujar el texto
                    super.paintComponent(g);
                }
            };
            playerNameField.setFont(new Font("Arial", Font.BOLD, 16));
            playerNameField.setBackground(new Color(245, 245, 245)); // Fondo muy claro
            playerNameField.setForeground(new Color(25, 25, 25)); // Texto negro
            playerNameField.setCaretColor(new Color(255, 70, 100)); // Cursor rojo visible
            playerNameField.setPreferredSize(new Dimension(200, 40)); // Tamaño más grande
            playerNameField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding interno
            playerNameField.setOpaque(false); // Para que funcione el paintComponent personalizado
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0, 0, 10, 10);
            inputPanel.add(nameLabel, gbc);
            
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 0, 10, 0);
            inputPanel.add(playerNameField, gbc);
            
            panel.add(inputPanel, BorderLayout.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.setOpaque(false);
            
            JButton startGameBtn = new JButton("COMENZAR") {
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
            startGameBtn.setFont(new Font("Arial", Font.BOLD, 14));
            startGameBtn.setForeground(Color.WHITE);
            startGameBtn.setBackground(new Color(100, 200, 100)); // Verde más visible
            startGameBtn.setFocusPainted(false);
            startGameBtn.setOpaque(false);
            startGameBtn.setContentAreaFilled(false);
            startGameBtn.setBorderPainted(false);
            
            JButton cancelBtn = new JButton("CANCELAR") {
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
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setBackground(new Color(200, 70, 70)); // Rojo más visible
            cancelBtn.setFocusPainted(false);
            cancelBtn.setOpaque(false);
            cancelBtn.setContentAreaFilled(false);
            cancelBtn.setBorderPainted(false);
            cancelBtn.addActionListener(ev -> nameDialog.dispose());
            
            startGameBtn.addActionListener(ev -> {
                String playerName = playerNameField.getText().trim();
                if (playerName.length() > 10) {
                    playerName = playerName.substring(0, 10);
                }

                if (playerName != null && !playerName.isEmpty()) {
                    gameFrame = new JFrame("El Juego Más Difícil del Mundo");
                    gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    gameFrame.setResizable(false);

                    GamePanel gamePanel = new GamePanel();
                    gamePanel.setPlayerName(playerName);
                    gameFrame.add(gamePanel, BorderLayout.CENTER);
                    gameFrame.pack();
                    gameFrame.setLocationRelativeTo(null);
                    gameFrame.setVisible(true);
                    menuFrame.setVisible(false);
                    nameDialog.dispose();
                } else {
                    // Error message with Squid Game style
                    JLabel errorLabel = new JLabel("<html><center><span style='color: #FF4664;'>⚠️ DEBES INGRESAR UN NOMBRE</span></center></html>");
                    JOptionPane.showMessageDialog(nameDialog, errorLabel, "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            buttonPanel.add(startGameBtn);
            buttonPanel.add(cancelBtn);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            nameDialog.setContentPane(panel);
            nameDialog.setPreferredSize(new Dimension(350, 250));
            nameDialog.pack();
            nameDialog.setLocationRelativeTo(null);
            nameDialog.setVisible(true);
        });
        GridBagConstraints gbcStartButton = new GridBagConstraints();
        gbcStartButton.gridx = 0;
        gbcStartButton.gridy = 1;
        gbcStartButton.fill = GridBagConstraints.HORIZONTAL;
        gbcStartButton.insets = new Insets(0, 0, 10, 10);
        menuPanel.add(startButton, gbcStartButton);
    }

    // Metodo publico para que el menuFrame se haga visible y el gameFrame se cierre
    public static void showMenu() {
        menuFrame.setVisible(true);
        gameFrame.dispose();
    }

    private static void addLearnButton() {
        JButton learnButton = createCustomButton("Instrucciones", new Color(50, 50, 50), new Color(255, 70, 100));
        
        // Add hover effects
        learnButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                learnButton.setBackground(new Color(255, 70, 100));
                learnButton.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                learnButton.setBackground(new Color(50, 50, 50));
                learnButton.repaint();
            }
        });
        
        GridBagConstraints gbcLearnButton = new GridBagConstraints();
        gbcLearnButton.gridx = 0;
        gbcLearnButton.gridy = 2;
        gbcLearnButton.fill = GridBagConstraints.HORIZONTAL;
        gbcLearnButton.insets = new Insets(0, 0, 10, 10);
        menuPanel.add(learnButton, gbcLearnButton);

        learnButton.addActionListener((ActionEvent e) -> {
            playButtonSound();

            JDialog dialog = new JDialog();
            dialog.setTitle("INSTRUCCIONES DEL JUEGO");
            dialog.setModal(true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    
                    // Squid Game background
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 25),
                                                             0, getHeight(), new Color(45, 25, 45));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Geometric accent
                    g2d.setColor(new Color(255, 70, 100, 50));
                    g2d.fillRect(0, 0, getWidth(), 5);
                    g2d.setColor(new Color(100, 255, 100, 50));
                    g2d.fillRect(0, getHeight() - 5, getWidth(), 5);
                }
            };
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 70, 100), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            JLabel label = new JLabel("<html><div style='color: white; text-align: center; font-family: Arial;'>"
                    + "<h2 style='color: #FF4664; margin-bottom: 20px;'>🔷 MISIÓN DEL CUADRADO 🔷</h2>"
                    + "<p style='color: #64FF64; font-weight: bold; margin-bottom: 15px;'>Un pequeño cuadrado debe superar todos los obstáculos</p>"
                    + "<div style='background: rgba(255,70,100,0.1); padding: 15px; border-radius: 10px; margin: 10px 0;'>"
                    + "<h3 style='color: #FF4664;'>⚡ CONTROLES:</h3>"
                    + "<p style='color: white; line-height: 1.6;'>"
                    + "🔸 <b>A</b> o <b>←</b> : Mover izquierda<br>"
                    + "🔸 <b>D</b> o <b>→</b> : Mover derecha<br>"
                    + "🔸 <b>W</b> o <b>↑</b> : Mover arriba<br>"
                    + "🔸 <b>S</b> o <b>↓</b> : Mover abajo</p>"
                    + "</div>"
                    + "<div style='background: rgba(100,255,100,0.1); padding: 15px; border-radius: 10px; margin: 10px 0;'>"
                    + "<h3 style='color: #64FF64;'>🎮 COMANDOS ESPECIALES:</h3>"
                    + "<p style='color: white; line-height: 1.6;'>"
                    + "⏸️ <b>P</b> : Pausar juego<br>"
                    + "🚪 <b>ESC</b> : Salir del juego</p>"
                    + "</div>"
                    + "<p style='color: #FF4664; font-weight: bold; margin-top: 20px;'>¡Solo los más hábiles sobreviven!</p>"
                    + "</div></html>");
            
            panel.add(label, BorderLayout.CENTER);
            
            // Close button with Squid Game style
            JButton closeButton = new JButton("CERRAR") {
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
            closeButton.setFont(new Font("Arial", Font.BOLD, 14));
            closeButton.setForeground(Color.WHITE);
            closeButton.setBackground(new Color(180, 70, 70)); // Rojo más oscuro y visible
            closeButton.setFocusPainted(false);
            closeButton.setOpaque(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setBorderPainted(false);
            closeButton.addActionListener(ev -> dialog.dispose());
            
            JPanel buttonPanel = new JPanel(new FlowLayout());
            buttonPanel.setOpaque(false);
            buttonPanel.add(closeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setContentPane(panel);
            dialog.setPreferredSize(new Dimension(450, 400));
            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }

    private static void addDifficultyButton() {
        JButton difficultyButton = createCustomButton("Dificultad", new Color(50, 50, 50), new Color(255, 70, 100));
        
        // Add hover effects
        difficultyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                difficultyButton.setBackground(new Color(255, 70, 100));
                difficultyButton.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                difficultyButton.setBackground(new Color(50, 50, 50));
                difficultyButton.repaint();
            }
        });
        
        difficultyButton.addActionListener((ActionEvent e) -> {
            playButtonSound();

            // Create custom dialog with Squid Game style
            JDialog difficultyDialog = new JDialog();
            difficultyDialog.setTitle("SELECCIONAR NIVEL");
            difficultyDialog.setModal(true);
            difficultyDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            JPanel mainPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    
                    // Squid Game background
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 25),
                                                             0, getHeight(), new Color(45, 25, 45));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 70, 100), 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            
            // Title
            JLabel titleLabel = new JLabel("🎯 ELIGE TU DESTINO 🎯");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(new Color(255, 70, 100));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            
            // Buttons panel
            JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
            buttonPanel.setOpaque(false);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            
            // Difficulty colors
            Color facil1 = new Color(178, 181, 252);
            Color facil2 = new Color(246, 246, 254);
            Color medio1 = new Color(229, 236, 233);
            Color medio2 = new Color(150, 154, 151);
            Color dificil1 = new Color(79, 0, 11);
            Color dificil2 = new Color(206, 66, 87);
            Color goal1 = new Color(83, 59, 77);
            Color goal2 = new Color(225, 251, 184);
            
            // Easy button
            JButton easyBtn = createDifficultyButton("🟢 FÁCIL", "Para principiantes", new Color(100, 255, 100));
            easyBtn.addActionListener(ev -> {
                Ball.setSpeed(2);
                GamePanel.setNuevoColor1(facil1);
                GamePanel.setNuevoColor2(facil2);
                showDifficultyConfirmation("FÁCIL", difficultyDialog);
            });
            
            // Medium button
            JButton mediumBtn = createDifficultyButton("🟡 MEDIO", "Desafío moderado", new Color(255, 255, 100));
            mediumBtn.addActionListener(ev -> {
                Ball.setSpeed(4);
                GamePanel.setNuevoColor1(medio1);
                GamePanel.setNuevoColor2(medio2);
                GamePanel.setColorGoal(goal1);
                showDifficultyConfirmation("MEDIO", difficultyDialog);
            });
            
            // Hard button
            JButton hardBtn = createDifficultyButton("🔴 DIFÍCIL", "Solo para expertos", new Color(255, 70, 100));
            hardBtn.addActionListener(ev -> {
                Ball.setSpeed(6);
                GamePanel.setNuevoColor1(dificil1);
                GamePanel.setNuevoColor2(dificil2);
                GamePanel.setColorGoal(goal2);
                showDifficultyConfirmation("DIFÍCIL", difficultyDialog);
            });
            
            buttonPanel.add(easyBtn);
            buttonPanel.add(mediumBtn);
            buttonPanel.add(hardBtn);
            
            mainPanel.add(buttonPanel, BorderLayout.CENTER);
            
            difficultyDialog.setContentPane(mainPanel);
            difficultyDialog.setPreferredSize(new Dimension(350, 300));
            difficultyDialog.pack();
            difficultyDialog.setLocationRelativeTo(null);
            difficultyDialog.setVisible(true);
        });
        GridBagConstraints gbcDifficultyButton = new GridBagConstraints();
        gbcDifficultyButton.gridx = 1;
        gbcDifficultyButton.gridy = 1;
        gbcDifficultyButton.fill = GridBagConstraints.HORIZONTAL;
        gbcDifficultyButton.insets = new Insets(0, 0, 10, 0);
        menuPanel.add(difficultyButton, gbcDifficultyButton);
    }
    
    private static JButton createDifficultyButton(String title, String description, Color accentColor) {
        JButton button = new JButton("<html><center><b style='font-size:16px;'>" + title + "</b><br><span style='font-size:12px;'>" + description + "</span></center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Force background color
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw border
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
                
                // Draw text - using HTML rendering for multi-line
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                
                // Manual text rendering for better control
                String[] lines = {title, description};
                FontMetrics fm = g2d.getFontMetrics();
                int totalHeight = lines.length * fm.getHeight();
                int startY = (getHeight() - totalHeight) / 2 + fm.getAscent();
                
                // Draw title
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics titleFm = g2d.getFontMetrics();
                int titleX = (getWidth() - titleFm.stringWidth(title)) / 2;
                g2d.drawString(title, titleX, startY);
                
                // Draw description
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                FontMetrics descFm = g2d.getFontMetrics();
                int descX = (getWidth() - descFm.stringWidth(description)) / 2;
                g2d.drawString(description, descX, startY + titleFm.getHeight() + 5);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60)); // Fondo gris oscuro para contraste
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(accentColor);
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(60, 60, 60));
                button.repaint();
            }
        });
        
        return button;
    }
    
    private static void showDifficultyConfirmation(String difficulty, JDialog parentDialog) {
        parentDialog.dispose();
        
        JDialog confirmDialog = new JDialog();
        confirmDialog.setTitle("CONFIRMACIÓN");
        confirmDialog.setModal(true);
        
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
            BorderFactory.createLineBorder(new Color(255, 70, 100), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel message = new JLabel("<html><center><h3 style='color: #FF4664;'>✅ DIFICULTAD SELECCIONADA</h3><p style='color: white;'>Nivel: <b style='color: #64FF64;'>" + difficulty + "</b></p></center></html>");
        message.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(message, BorderLayout.CENTER);
        
        JButton okButton = new JButton("ACEPTAR") {
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
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setForeground(Color.WHITE);
        okButton.setBackground(new Color(80, 180, 80)); // Verde más oscuro
        okButton.setFocusPainted(false);
        okButton.setOpaque(false);
        okButton.setContentAreaFilled(false);
        okButton.setBorderPainted(false);
        okButton.addActionListener(ev -> confirmDialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        confirmDialog.setContentPane(panel);
        confirmDialog.setPreferredSize(new Dimension(300, 150));
        confirmDialog.pack();
        confirmDialog.setLocationRelativeTo(null);
        confirmDialog.setVisible(true);
    }

    private static void addExitButton() {
        JButton exitButton = createCustomButton("Salir", new Color(50, 50, 50), new Color(255, 70, 100));
        
        // Add hover effects
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitButton.setBackground(new Color(255, 70, 100));
                exitButton.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setBackground(new Color(50, 50, 50));
                exitButton.repaint();
            }
        });
        
        exitButton.addActionListener((ActionEvent e) -> {
            playButtonSound();

            // Custom exit confirmation dialog with Squid Game style
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
            
            JLabel messageLabel = new JLabel("<html><center><h3 style='color: #FF4664;'>⚠️ CONFIRMACIÓN</h3><p style='color: white;'>¿Estás seguro de que deseas<br>abandonar el juego?</p></center></html>");
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
            yesButton.setBackground(new Color(180, 70, 70)); // Rojo más oscuro
            yesButton.setFocusPainted(false);
            yesButton.setOpaque(false);
            yesButton.setContentAreaFilled(false);
            yesButton.setBorderPainted(false);
            yesButton.addActionListener(ev -> System.exit(0));
            
            JButton noButton = new JButton("CANCELAR") {
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
            noButton.setBackground(new Color(80, 180, 80)); // Verde más oscuro
            noButton.setFocusPainted(false);
            noButton.setOpaque(false);
            noButton.setContentAreaFilled(false);
            noButton.setBorderPainted(false);
            noButton.addActionListener(ev -> exitDialog.dispose());
            
            buttonPanel.add(yesButton);
            buttonPanel.add(noButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            exitDialog.setContentPane(panel);
            exitDialog.setPreferredSize(new Dimension(300, 200));
            exitDialog.pack();
            exitDialog.setLocationRelativeTo(null);
            exitDialog.setVisible(true);
        });
        GridBagConstraints gbcExitButton = new GridBagConstraints();
        gbcExitButton.gridx = 1;
        gbcExitButton.gridy = 2;
        gbcExitButton.fill = GridBagConstraints.HORIZONTAL;
        gbcExitButton.insets = new Insets(0, 0, 10, 0);
        menuPanel.add(exitButton, gbcExitButton);
    }

    private static void playButtonSound() {
        try {
            AudioInputStream ballAudioInputStream = AudioSystem.getAudioInputStream(
                    GamePanel.class.getResourceAsStream("/hardgame/sounds/boton.wav"));
            Clip soundBoton = AudioSystem.getClip();
            soundBoton.open(ballAudioInputStream);
            soundBoton.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    // Helper method to create buttons with forced colors
    private static JButton createCustomButton(String text, Color backgroundColor, Color borderColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Force background color
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw border
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
                
                // Draw text
                g2d.setColor(Color.WHITE); // Siempre texto blanco
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 45));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        return button;
    }
}