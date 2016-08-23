package net.frozenbit.plugin5zig.cubecraft.items;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;
import net.frozenbit.plugin5zig.cubecraft.Main;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SnakeItem extends AbstractModuleItem {
    private static final int TILE_SIZE = 4;
    private static final int HEIGHT = 25;
    private static final int WIDTH = 25;
    private static final int SNAKE_COLOR = 0xFF44FF44;
    private static final int FOOD_COLOR = 0xFF770000;
    private static final int BACKGROUND_COLOR = 0xFF999999;
    private static final int GAMEOVER_OVERLAY_COLOR = 0x44FF0000;

    private boolean gameOver;
    private long lastMoveTime;
    private Random random = new Random();
    private Point food = new Point();
    private List<Point> snake = new ArrayList<>();
    private int speedX = 0;
    private int speedY = 0;

    public SnakeItem() {
        super();
        spawnFood();
        snake.add(new Point(HEIGHT / 2, WIDTH / 2));
    }

    private static void dot(int x0, int y0, Point p, int color) {
        rect(x0, y0, p.x, p.y, 1, 1, color);
    }

    private static void rect(int x0, int y0, int x, int y, int w, int h, int color) {
        int left = x0 + x * TILE_SIZE;
        int top = y0 + y * TILE_SIZE;
        int right = left + w * TILE_SIZE;
        int bottom = top + h * TILE_SIZE;
        The5zigAPI.getAPI().getRenderHelper().drawRect(left, top, right, bottom, color);
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        return Main.getInstance().isSnake() && super.shouldRender(dummy);
    }

    @Override
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        long time = System.currentTimeMillis();
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            speedX = -1;
            speedY = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            speedX = 1;
            speedY = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            speedX = 0;
            speedY = -1;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            speedX = 0;
            speedY = 1;
        }
        if (!gameOver && time - lastMoveTime > 200) {
            lastMoveTime = time;
            move();
        }
        rect(x, y, 0, 0, WIDTH, HEIGHT, BACKGROUND_COLOR);
        dot(x, y, food, FOOD_COLOR);

        for (Point point : snake) {
            dot(x, y, point, SNAKE_COLOR);
        }

        if (gameOver) {
            rect(x, y, 0, 0, WIDTH, HEIGHT, GAMEOVER_OVERLAY_COLOR);
            int centerX = x + (WIDTH / 2) * TILE_SIZE;
            int centerY = y + (HEIGHT / 2) * TILE_SIZE;
            The5zigAPI.getAPI().getRenderHelper().drawCenteredString("GAME OVER", centerX, centerY);
        }
    }

    private void spawnFood() {
        do {
            food.setLocation(random.nextInt(WIDTH), random.nextInt(HEIGHT));
        } while (snake.contains(food));
    }

    private void move() {
        Point head = snake.get(snake.size() - 1);
        if (food.equals(head)) {
            snake.add(new Point(head));
            spawnFood();
        } else {
            Point lastPoint = snake.get(0);
            for (Point point : snake) {
                lastPoint.setLocation(point);
                lastPoint = point;
            }
        }
        moveHead(snake.get(snake.size() - 1));
    }

    private void moveHead(Point head) {
        head.translate(speedX, speedY);
        head.x = head.x < 0 ? WIDTH - 1 : head.x == WIDTH ? 0 : head.x;
        head.y = head.y < 0 ? HEIGHT - 1 : head.y == HEIGHT ? 0 : head.y;
        gameOver = snake.indexOf(head) != snake.size() - 1;
    }

    @Override
    public int getWidth(boolean dummy) {
        return WIDTH * TILE_SIZE;
    }

    @Override
    public int getHeight(boolean dummy) {
        return HEIGHT * TILE_SIZE;
    }
}
