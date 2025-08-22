public class AIPlayer {
    private Paddle paddle;
    private Ball ball;
    private int speed; // tốc độ AI theo độ khó

    public AIPlayer(Paddle paddle, Ball ball, int difficulty){
        this.paddle = paddle;
        this.ball = ball;
        this.speed = difficulty * 3; // ví dụ: dễ=3, trung bình=6, khó=9
    }

    public void move(){
        if(ball.y + Config.BALL_SIZE/2 > paddle.y + Config.PADDLE_HEIGHT/2)
            paddle.y += speed;
        else if(ball.y + Config.BALL_SIZE/2 < paddle.y + Config.PADDLE_HEIGHT/2)
            paddle.y -= speed;

        // giữ paddle trong màn hình
        paddle.y = Math.max(0, Math.min(Config.HEIGHT - Config.PADDLE_HEIGHT, paddle.y));
    }
}
