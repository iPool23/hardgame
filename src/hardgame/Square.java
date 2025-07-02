package hardgame;

import java.awt.*;

public class Square extends Rectangle {
    private static final long serialVersionUID = 1L;
    private static final int SPEED = 3;
    protected boolean invincible;
    private int dx;
    private int dy;
    private static final int SIZE = 10;

    public Square(int x, int y, int size) {
        super(x, y, size, size);
        dx = 0;
        dy = 0;
    }

    public void setDx(int dx) {
        this.dx = dx * SPEED;
    }

    public void setDy(int dy) {
        this.dy = dy * SPEED;
    }

    public void move() {
        int newX = x + dx;
        int newY = y + dy;

        if (newX - 4 >= 0 && newX + width <= GamePanel.PANEL_WIDTH + 5) {
            x = newX;
        }

        if (newY - 4 >= 0 && newY + height <= GamePanel.PANEL_HEIGHT + 5) {
            y = newY;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, SIZE, SIZE);
        g.setColor(Color.BLACK);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(x, y, SIZE, SIZE);
    }
}

