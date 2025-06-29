// Keep all the imports same
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.Timer;

public class QuizClient {

    private static JFrame frame;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static String username;
    private static Timer timer;
    private static JLabel timerLabel;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        frame = new JFrame("Quiz Login/Register");
        frame.setSize(350, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(128, 0, 128)); // purple background
        frame.add(panel);
        placeLoginComponents(panel);
        frame.setVisible(true);
    }

    private static void styleComponent(JComponent comp) {
        comp.setForeground(Color.WHITE);
        comp.setFont(new Font("Arial", Font.PLAIN, 14));
        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            btn.setBackground(new Color(255, 105, 180)); // pink
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.PINK));
        }
    }

    private static void placeLoginComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 80, 25);
        styleComponent(userLabel);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(120, 20, 180, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 60, 80, 25);
        styleComponent(passwordLabel);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(120, 60, 180, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(40, 110, 100, 30);
        styleComponent(loginButton);
        panel.add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(180, 110, 100, 30);
        styleComponent(registerButton);
        panel.add(registerButton);

        loginButton.addActionListener(e -> {
            try {
                out.writeObject("login");
                out.writeObject(userText.getText());
                out.writeObject(new String(passwordText.getPassword()));

                String response = (String) in.readObject();
                if ("success".equals(response)) {
                    username = userText.getText();
                    boolean attended = (boolean) in.readObject();

                    JOptionPane.showMessageDialog(frame,
                            "Login successful! You will now start the quiz.\nEach question has 30 seconds.");

                    if (!attended) {
                        @SuppressWarnings("unchecked")
                        List<String[]> questions = (List<String[]>) in.readObject();
                        takeTest(questions);
                    } else {
                        JOptionPane.showMessageDialog(frame, "You already attended the test.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Login Failed");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        registerButton.addActionListener(e -> {
            try {
                out.writeObject("register");
                out.writeObject(userText.getText());
                out.writeObject(new String(passwordText.getPassword()));

                String response = (String) in.readObject();
                if ("registered".equals(response)) {
                    JOptionPane.showMessageDialog(frame, "Registration Successful");
                } else {
                    JOptionPane.showMessageDialog(frame, "User already exists");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void takeTest(List<String[]> questions) {
        JFrame testFrame = new JFrame("Quiz");
        testFrame.setSize(650, 500);
        testFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(128, 0, 128)); // purple background
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        timerLabel = new JLabel();
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.WHITE);
        panel.add(timerLabel);

        JRadioButton[][] options = new JRadioButton[questions.size()][4];
        ButtonGroup[] groups = new ButtonGroup[questions.size()];

        for (int i = 0; i < questions.size(); i++) {
            String[] q = questions.get(i);
            JPanel qPanel = new JPanel();
            qPanel.setLayout(new GridLayout(5, 1));
            qPanel.setBorder(BorderFactory.createTitledBorder("Q" + (i + 1) + ": " + q[0]));
            qPanel.setBackground(new Color(128, 0, 128)); // purple

            groups[i] = new ButtonGroup();
            for (int j = 0; j < 4; j++) {
                options[i][j] = new JRadioButton(q[j + 1]);
                options[i][j].setBackground(new Color(128, 0, 128));
                options[i][j].setForeground(Color.WHITE);
                groups[i].add(options[i][j]);
                qPanel.add(options[i][j]);
            }
            panel.add(qPanel);
        }

        JButton submitButton = new JButton("Submit");
        styleComponent(submitButton);
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> submitTest(testFrame, questions, options));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(submitButton);

        JScrollPane scrollPane = new JScrollPane(panel);
        testFrame.add(scrollPane);
        testFrame.setVisible(true);

        int timeLimit = questions.size() * 30;
        timer = new Timer(1000, new ActionListener() {
            int remaining = timeLimit;

            public void actionPerformed(ActionEvent e) {
                if (remaining == 30) {
                    JOptionPane.showMessageDialog(testFrame, "⚠ Only 30 seconds remaining!");
                }
                if (remaining == 10) {
                    JOptionPane.showMessageDialog(testFrame, "⏳ 10 seconds left! Hurry up!");
                }

                timerLabel.setText("Time left: " + remaining + " seconds");
                remaining--;
                if (remaining < 0) {
                    timer.stop();
                    submitTest(testFrame, questions, options);
                }
            }
        });
        timer.start();
    }

    private static void submitTest(JFrame frame, List<String[]> questions, JRadioButton[][] options) {
        int score = 0;
        List<String> userAnswers = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            int correct = Integer.parseInt(questions.get(i)[5]) - 1;
            String selectedAnswer = "Not Answered";

            for (int j = 0; j < 4; j++) {
                if (options[i][j].isSelected()) {
                    selectedAnswer = options[i][j].getText();
                    break;
                }
            }

            userAnswers.add("Q: " + questions.get(i)[0] + "\nYour Answer: " + selectedAnswer +
                    "\nCorrect Answer: " + questions.get(i)[correct + 1] + "\n");

            if (selectedAnswer.equals(questions.get(i)[correct + 1])) {
                score++;
            }
        }

        try {
            out.writeObject(score);
            out.writeObject(userAnswers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer.stop();
        JOptionPane.showMessageDialog(frame, "Test submitted! Your score: " + score);
        frame.dispose();
        showResultAnalysis();
    }

    private static void showResultAnalysis() {
        try {
            out.writeObject("get_analysis");
            out.writeObject(username);
            @SuppressWarnings("unchecked")
            List<String> analysis = (List<String>) in.readObject();

            StringBuilder result = new StringBuilder("Result Analysis:\n\n");
            for (String entry : analysis) {
                result.append(entry).append("\n");
            }

            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 300));

            JOptionPane.showMessageDialog(frame, scrollPane, "Result Analysis", JOptionPane.INFORMATION_MESSAGE);
            showLeaderboard();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showLeaderboard() {
        try {
            out.writeObject("leaderboard");
            @SuppressWarnings("unchecked")
            Map<String, Integer> board = (Map<String, Integer>) in.readObject();

            StringBuilder sb = new StringBuilder("🏆 Leaderboard:\n\n");
            board.entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .forEach(e -> sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n"));

            JOptionPane.showMessageDialog(frame, sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}