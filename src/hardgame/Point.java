package hardgame;

import java.awt.*;

public class Point extends Rectangle {

    private boolean collected;
    private static final long serialVersionUID = 1L;
    private static final int SIZE = 10;
    private boolean visible;

    public Point() {
        super((int) (Math.random() * (GamePanel.PANEL_WIDTH / 1.1)) + 10,
                (int) (Math.random() * (GamePanel.PANEL_HEIGHT / 1.1)) + 10, SIZE, SIZE);
        visible = true;
        collected = false;
        visible = true;
        this.collected = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void draw(Graphics g) {
        if (!collected) {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, width, height);
            g.setColor(Color.BLACK);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(x, y, width, height);
        }
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

}
