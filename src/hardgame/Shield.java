package hardgame;

import java.awt.*;

public class Shield extends Rectangle {
    private static final int SIZE = 10;
    private static final int SPEED = 2;
    private int dx;
    private int dy;
    private boolean collected;

    public Shield() {
        super((int) (Math.random() * (GamePanel.PANEL_WIDTH / 1.1)) + 10,
                (int) (Math.random() * (GamePanel.PANEL_HEIGHT / 1.1)) + 10, SIZE, SIZE);
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
        g.setColor(Color.DARK_GRAY);
        g.fillOval(x, y, width, height);
        g.setColor(Color.YELLOW);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(x, y, width, height);
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

}
