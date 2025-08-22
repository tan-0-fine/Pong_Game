import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GameServer {
    private Ball ball = new Ball(Config.WIDTH / 2, Config.HEIGHT / 2);
    private Paddle p1 = new Paddle(50, Config.HEIGHT / 2 - Config.PADDLE_HEIGHT / 2);
    private Paddle p2 = new Paddle(Config.WIDTH - 50 - Config.PADDLE_WIDTH, Config.HEIGHT / 2 - Config.PADDLE_HEIGHT / 2);
    private int score1 = 0, score2 = 0;
    private int rounds = 0;
    private int maxRounds = 13;
    private CopyOnWriteArrayList<PrintWriter> clients = new CopyOnWriteArrayList<>();

    private int port = 5000; // port mặc định

    public static void main(String[] args) throws Exception {
        new GameServer().start();
    }

    public void start() throws Exception {
        ServerSocket serverSocket = null;
        while (serverSocket == null) {
            try {
                serverSocket = new ServerSocket(port);
            } catch (BindException e) {
                System.out.println("Cổng " + port + " đang bị chiếm, thử cổng tiếp theo...");
                port++; // tự tăng port nếu bị chiếm
            }
        }

        System.out.println("Server đã khởi động rồi đó. Bạn vui lòng chờ chút nha! (port " + port + ")");

        // Thread di chuyển bóng và gửi trạng thái cho client
        new Thread(this::gameLoop).start();

        // Chấp nhận client
        while (true) {
            Socket socket = serverSocket.accept();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            clients.add(out);
            System.out.println("Client đã kết nối: " + socket.getInetAddress());

            new Thread(() -> handleClient(socket)).start();
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("UP")) p1.moveUp();
                else if (line.equalsIgnoreCase("DOWN")) p1.moveDown();
            }
        } catch (Exception e) {
            System.out.println("Client đã rời: " + socket.getInetAddress());
        }
    }

    private void gameLoop() {
        while (true) {
            ball.move();
            checkCollision();
            broadcastState();
            try { Thread.sleep(30); } catch (Exception e) {}
        }
    }

    private void checkCollision() {
        if (ball.x <= p1.x + Config.PADDLE_WIDTH &&
                ball.y + Config.BALL_SIZE >= p1.y && ball.y <= p1.y + Config.PADDLE_HEIGHT)
            ball.dx = -ball.dx;
        if (ball.x + Config.BALL_SIZE >= p2.x &&
                ball.y + Config.BALL_SIZE >= p2.y && ball.y <= p2.y + Config.PADDLE_HEIGHT)
            ball.dx = -ball.dx;

        if (ball.x <= 0) {
            score2++; newRound();
        }
        if (ball.x >= Config.WIDTH) {
            score1++; newRound();
        }
    }

    private void newRound() {
        rounds++;
        ball.x = Config.WIDTH / 2;
        ball.y = Config.HEIGHT / 2;
        ball.dx = Config.BASE_SPEED * (Math.random() > 0.5 ? 1 : -1);
        ball.dy = Config.BASE_SPEED * (Math.random() > 0.5 ? 1 : -1);

        if (rounds % Config.LEVEL_UP_INTERVAL == 0) ball.increaseSpeed();

        if (score1 >= maxRounds / 2 + 1) endGame("Player 1 thắng!");
        if (score2 >= maxRounds / 2 + 1) endGame("Player 2 thắng!");
    }

    private void endGame(String msg) {
        broadcastMessage("END," + msg);
        System.out.println(msg);
        System.exit(0);
    }

    private void broadcastState() {
        String state = ball.x + "," + ball.y + "," + p1.y + "," + p2.y + "," + score1 + "," + score2 + "," + rounds;
        broadcastMessage("STATE," + state);
    }

    private void broadcastMessage(String msg) {
        for (PrintWriter out : clients) {
            out.println(msg);
        }
    }
}
