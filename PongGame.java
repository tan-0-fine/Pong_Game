import javax.swing.*;

public class PongGame {
    public static void main(String[] args) {
        String[] options = {"Chơi với máy", "Chơi mạng LAN"};
        int mode = JOptionPane.showOptionDialog(null, "Chọn chế độ chơi", "Pong Game",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        int maxRounds = Integer.parseInt(JOptionPane.showInputDialog("Nhập số vòng tối đa (ví dụ 13):"));

        if (mode == 0) {
            SwingUtilities.invokeLater(() -> new GameClient("localhost", true, maxRounds)); // AI
        } else {
            String host = JOptionPane.showInputDialog("Nhập IP server:");
            SwingUtilities.invokeLater(() -> new GameClient(host, false, maxRounds));
        }
    }
}
