package hardgame;

import java.awt.*;

public class Love extends Rectangle {
    static final int SIZE = 10;
    private boolean collected;
    private long startTime;

    public Love() {
        super((int) (Math.random() * (GamePanel.PANEL_WIDTH - 5)),
                (int) (Math.random() * (GamePanel.PANEL_HEIGHT - 5) / 1.5),
                SIZE, SIZE);

        this.collected = false;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    public void draw(Graphics g) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (!collected && elapsedTime <= 5000) {
            g.setColor(Color.PINK);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(x, y, width, height);
        } else {
            collected = true;
        }
    }
}
