package hardgame;

import java.awt.*;

public class Ball extends Rectangle {
    public static int SPEED = 2;
    static final int SIZE = 10;
    private int dx;
    private int dy;

    public Ball() {
        super((int) (Math.random() * (GamePanel.PANEL_WIDTH / 1.1)) + 10,
                (int) (Math.random() * (GamePanel.PANEL_HEIGHT / 1.5)) + 10,
                SIZE, SIZE);

        do {
            dx = (int) (Math.random() * SPEED * 2 - SPEED);
            dy = (int) (Math.random() * SPEED * 2 - SPEED);
        } while (dx == 0 && dy == 0);

    }

    public void move() {
        x += dx;
        y += dy;

        if (x - 5 < 0 || x > GamePanel.PANEL_WIDTH - width - 5) {
            dx = -dx;
        }

        if (y - 5 < 0 || y > GamePanel.PANEL_HEIGHT - height - 5) {
            dy = -dy;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, width, height);
        g.setColor(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(x, y, width, height);
    }

    public static void setSpeed(int speed) {
        SPEED = speed;
    }

}
