package hardgame;

import java.awt.*;

public class Goal extends Rectangle {
    private static final long serialVersionUID = 1L;
    private static final int BORDER_SIZE = 5;

    public Goal(int x, int y, int size) {
        super(x, y, size, size);
    }

    public void draw(Graphics g) {
        g.fillRect(x, y, width, height);
        g.fillRect(x + BORDER_SIZE, y + BORDER_SIZE, width - BORDER_SIZE * 2, height - BORDER_SIZE * 2);
    }
}
