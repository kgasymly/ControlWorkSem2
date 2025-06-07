import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

public class QuizGame {
    private static final String QUESTIONS_FILE = "questions.dat";
    private static final String RAITING_FILE = "raiting.dat";
    private static final Scanner scanner = new Scanner(System.in);
    private static Questions questions;
    private static Raiting raiting;

    public static void main(String[] args) {
        loadData();

        System.out.println("Добро пожаловать в викторину!");
        System.out.print("Введите ваше имя: ");
        String playerName = scanner.nextLine();

        int score = playQuiz();

        saveGameResult(playerName, score);
        showRaiting();
    }

    private static void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(QUESTIONS_FILE))) {
            questions = (Questions) ois.readObject();
        } catch (Exception e) {
            System.out.println("Ошибка загрузки вопросов. Созданы тестовые вопросы.");
            createTestQuestions();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RAITING_FILE))) {
            raiting = (Raiting) ois.readObject();
        } catch (Exception e) {
            System.out.println("Рейтинг не найден. Создан новый.");
            raiting = new Raiting();
        }
    }

    private static void createTestQuestions() {
        questions = new Questions();

        questions.question1 = "Столица Франции?";
        questions.response1 = new String[]{"Лондон", "Париж", "Берлин"};
        questions.goodResponseIndex1 = 1;

        questions.question2 = "Сколько планет в Солнечной системе?";
        questions.response2 = new String[]{"8", "9", "10"};
        questions.goodResponseIndex2 = 0;

        questions.question3 = "Автор 'Войны и мира'?";
        questions.response3 = new String[]{"Достоевский", "Толстой", "Чехов"};
        questions.goodResponseIndex3 = 1;
    }

    private static int playQuiz() {
        int score = 0;

        System.out.println("\nНачинаем викторину!");
        score += askQuestion(questions.question1, questions.response1, questions.goodResponseIndex1);
        score += askQuestion(questions.question2, questions.response2, questions.goodResponseIndex2);
        score += askQuestion(questions.question3, questions.response3, questions.goodResponseIndex3);

        System.out.printf("\nВаш результат: %d из 3\n", score);
        return score;
    }

    private static int askQuestion(String question, String[] responses, int correctIndex) {
        System.out.println("\n" + question);
        for (int i = 0; i < responses.length; i++) {
            System.out.printf("%d. %s\n", i+1, responses[i]);
        }

        System.out.print("Ваш ответ (номер): ");
        int answer = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера

        if (answer == correctIndex + 1) {
            System.out.println("Правильно!");
            return 1;
        } else {
            System.out.println("Неверно! Правильный ответ: " + responses[correctIndex]);
            return 0;
        }
    }

    private static void saveGameResult(String playerName, int score) {
        Game game = new Game();
        game.gamer = playerName;
        game.raiting = score;
        game.gameDate = new Date();

        raiting.games.add(game);
        Collections.sort(raiting.games);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RAITING_FILE))) {
            oos.writeObject(raiting);
        } catch (Exception e) {
            System.out.println("Ошибка сохранения рейтинга: " + e.getMessage());
        }
    }

    private static void showRaiting() {
        System.out.println("\nРейтинг игроков:");
        System.out.println("--------------------------------------------------");
        System.out.println("Имя\t\tОчки\tДата игры");
        System.out.println("--------------------------------------------------");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        for (Game game : raiting.games) {
            System.out.printf("%s\t\t%d\t%s\n",
                    game.gamer,
                    game.raiting,
                    dateFormat.format(game.gameDate));
        }
    }
}