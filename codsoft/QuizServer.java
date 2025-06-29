import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class QuizServer {
    static class User {
        String password;
        boolean attended;
        int score;

        User(String password) {
            this.password = password;
            this.attended = false;
            this.score = 0;
        }
    }

    private static ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private static List<String[]> questions = new ArrayList<>();
    private static ConcurrentHashMap<String, Integer> leaderboard = new ConcurrentHashMap<>();
    private static Map<String, List<String>> userAnswers = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        loadQuestions();
        System.out.println("Server started...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void loadQuestions() {
        questions.add(new String[]{"Which company developed Java?", "Microsoft", "Sun Microsystems", "Oracle", "Google", "2"});
        questions.add(new String[]{"Which keyword is used to inherit a class in Java?", "this", "super", "extends", "implements", "3"});
        questions.add(new String[]{"Which method is the entry point of any Java program?", "start()", "main()", "run()", "init()", "2"});
        questions.add(new String[]{"Which package contains Scanner class?", "java.util", "java.io", "java.lang", "java.awt", "1"});
        questions.add(new String[]{"What is the size of int in Java?", "2 bytes", "4 bytes", "8 bytes", "Depends on system", "2"});
    }

    private static void handleClient(Socket socket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            while (true) {
                String command = (String) in.readObject();

                if ("register".equals(command)) {
                    String username = (String) in.readObject();
                    String password = (String) in.readObject();

                    if (users.containsKey(username)) {
                        out.writeObject("exists");
                    } else {
                        users.put(username, new User(password));
                        out.writeObject("registered");
                    }

                } else if ("login".equals(command)) {
                    String username = (String) in.readObject();
                    String password = (String) in.readObject();

                    User user = users.get(username);
                    if (user != null && user.password.equals(password)) {
                        synchronized (user) {
                            out.writeObject("success");
                            out.writeObject(user.attended);

                            if (!user.attended) {
                                out.writeObject(questions);
                                int score = (int) in.readObject();
                                @SuppressWarnings("unchecked")
                                List<String> answers = (List<String>) in.readObject();

                                user.score = score;
                                user.attended = true;
                                leaderboard.put(username, score);
                                userAnswers.put(username, answers);
                            }
                        }
                    } else {
                        out.writeObject("failed");
                    }

                } else if ("leaderboard".equals(command)) {
                    out.writeObject(new HashMap<>(leaderboard));

                } else if ("get_analysis".equals(command)) {
                    String username = (String) in.readObject();
                    List<String> answers = userAnswers.get(username);
                    out.writeObject(answers);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}