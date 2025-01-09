package com.mycompany.hospital;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Quiz111 {

    public static void main(String[] args) {
        // Create JFrame
        JFrame frame = new JFrame("Login Page");
        frame.setSize(1200, 600);
        frame.setLocation(200, 170);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create JPanel
        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 216, 230));
        frame.add(panel);
        placeComponents(panel, frame);

        // Set JFrame visibility
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel, JFrame frame) {
        panel.setLayout(null);

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        // Username Text Field
        JTextField userText = new JTextField(20);
        userText.setBounds(150, 20, 200, 25);
        panel.add(userText);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        // Password Field
        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(150, 50, 200, 25);
        panel.add(passwordText);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 90, 80, 25);
        panel.add(loginButton);

        // Login Button Action
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(panel, "Login successful!");
                frame.dispose();
                showQuiz(username);
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static boolean authenticate(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/quiz_login";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void showQuiz(String username) {
        JFrame quizFrame = new JFrame("Quiz");
        quizFrame.setSize(1200, 600);
        quizFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        quizFrame.setLocation(200, 170);

        JPanel quizPanel = new JPanel();
        quizPanel.setLayout(new GridLayout(12, 1));

        // Questions and answers using hash map
        Map<String, String> questions = new HashMap<>();
        questions.put("What is 2 + 2?", "4");
        questions.put("What is the capital of France?", "Paris");
        questions.put("What is 64 + 8?", "76");
        questions.put("What is the capital of INDIA?", "new delhi");
        questions.put("What is the chemical formula of water", "H2O");
        questions.put("what is number of seats in the legislative assembly", "256");
        questions.put("what programing paradigm does java follow", "oops");
        questions.put("true or false: Parameters of weight and height involve hieght and weight", "true");

        Map<JTextField, String> answers = new HashMap<>();

        // Display questions
        for (Map.Entry<String, String> entry : questions.entrySet()) {
            JLabel questionLabel = new JLabel(entry.getKey());
            JTextField answerField = new JTextField();
            quizPanel.add(questionLabel);
            quizPanel.add(answerField);
            answers.put(answerField, entry.getValue());
        }

        JButton submitButton = new JButton("Submit");
        quizPanel.add(submitButton);

        quizFrame.add(quizPanel);
        quizFrame.setVisible(true);

        submitButton.addActionListener(e -> {
            int score = 0;
            StringBuilder incorrectAnswers = new StringBuilder("Incorrect Answers:\n");

            for (Map.Entry<JTextField, String> entry : answers.entrySet()) {
                String userAnswer = entry.getKey().getText();
                String correctAnswer = entry.getValue();

                if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                    score++;
                } else {
                    incorrectAnswers.append("Question: ").append(((JLabel) entry.getKey().getParent().getComponent(0)).getText())
                            .append("\nYour Answer: ").append(userAnswer)
                            .append("\nCorrect Answer: ").append(correctAnswer)
                            .append("\n\n");
                }
            }

            saveScore(username, score);

            JOptionPane.showMessageDialog(quizFrame, "Your score: " + score);
            quizFrame.dispose();

            // Show incorrect answers
            JFrame incorrectAnswersFrame = new JFrame("Incorrect Answers");
            incorrectAnswersFrame.setSize(1200, 600);
            incorrectAnswersFrame.setLocation(200, 170);

            JTextArea incorrectAnswersArea = new JTextArea(incorrectAnswers.toString());
            incorrectAnswersArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(incorrectAnswersArea);

            incorrectAnswersFrame.add(scrollPane);
            incorrectAnswersFrame.setVisible(true);

            showLeaderboard();
        });
    }

    private static void saveScore(String username, int score) {
        String url = "jdbc:mysql://localhost:3306/login_app";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String sql = "INSERT INTO leaderboard (username, score) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setInt(2, score);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showLeaderboard() {
        JFrame leaderboardFrame = new JFrame("Leaderboard with scores");
        leaderboardFrame.setSize(400, 300);
        leaderboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        leaderboardFrame.setLocation(400, 200);

        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new GridLayout(11, 1));

        leaderboardPanel.add(new JLabel("Top 10 Scores:"));

        String url = "jdbc:mysql://localhost:3306/login_app";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String sql = "SELECT username, score FROM leaderboard ORDER BY score DESC LIMIT 10";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String row = resultSet.getString("username") + " - " + resultSet.getInt("score");
                leaderboardPanel.add(new JLabel(row));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        leaderboardFrame.add(leaderboardPanel);
        leaderboardFrame.setVisible(true);
    }
}
