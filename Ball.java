public class Ball {
    public int x, y;
    public int dx, dy;

    public Ball(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.dx = Config.BASE_SPEED;
        this.dy = Config.BASE_SPEED;
    }

    public void move() {
        x += dx;
        y += dy;
        if (y <= 0 || y >= Config.HEIGHT - Config.BALL_SIZE) {
            dy = -dy;
        }
    }

    public void increaseSpeed() {
        dx += dx>0?1:-1;
        dy += dy>0?1:-1;
    }
}
