package se.iths.main;
import java.util.List;
import se.iths.Main;
import se.iths.entity.Quiz;
import se.iths.entity.Result;
import se.iths.entity.User;
import static se.iths.Main.em;
import static se.iths.Main.scanner;


public class QuizQueries {

    public static int menu() {
        System.out.println("""
                Testa dina kunskaper om Sveriges län!
                        
                        Svenska Residensstäder
                        1. Lätt
                        2. Medel
                        3. Svår
                
                        Sveriges läns högsta berg
                        4. Lätt
                        5. Medel
                        6. Svår        
                        
                        Exit
                7. Tillbaka till huvudmenyn
                """);
        return Main.input();
    }

    public static void quizeSwitch() {
        System.out.println("Välkommen till Quiz!\nAnge ID: ");
        int id = Main.input();
        User user = em.find(User.class, id);

        if (user != null) {
            System.out.println("Välkommen " + user.getFirstName() + " " + user.getLastName() + " till Quiz!");
            while (true) {
                switch (menu ()) {
                    case 1:
                        difficultyLevelCity(user.getId(), 1, 1);
                        break;
                    case 2:
                        difficultyLevelCity(user.getId(), 2,1);
                        break;
                    case 3:
                        difficultyLevelCity(user.getId(), 3 ,1);
                        break;
                    case 4:
                        difficultyLevelMountain(user.getId(), 1,2);
                        break;
                    case 5:
                        difficultyLevelMountain(user.getId(),2, 2);
                        break;
                    case 6:
                        difficultyLevelMountain(user.getId(), 3, 2);
                        break;
                    case 7:
                        System.out.println("Återgår till huvudmenyn...");
                        return;
                    default:
                        System.out.println("Ogiltig inmatning. Var vänlig ange siffra mellan 1 till 7.\n");
                        break;
                }
            }
        } else {
            System.out.println("Spelare med ID " + id + " finns inte i databasen.\n");
        }
    }

    public static void difficultyLevelCity (int userID, int difficultyID, int categoryID) {
        User user = em.find(User.class, userID);
        List<Quiz> quizList = getQuizByDifficultyAndCategory(difficultyID, categoryID);

        int score = 0;

        for (Quiz quiz : quizList) {
            System.out.println("Vad är residentstaden i " + quiz.getQuestionID() + "?");
            String userAnswer = scanner.nextLine().trim(); // tar bort extra whitespace

            if (userAnswer.equalsIgnoreCase(quiz.getCorrectAnswer())) {
                ++score;
                System.out.println("Rätt svar!\n");

            } else {
                System.out.println("Fel svar. Det korrekta svaret är: " + quiz.getCorrectAnswer() + "\n");
            }
        }
        System.out.println("Du fick " + score + " poäng av " + quizList.size() + " möjliga.\n");
        saveResult(userID, score);
    }

    public static void difficultyLevelMountain (int userID, int difficultyID, int categoryID) {
        User user = em.find(User.class, userID);
        List<Quiz> quizList = getQuizByDifficultyAndCategory(difficultyID, categoryID);

        int score = 0;

        for (Quiz quiz : quizList) {
            System.out.println("Vad är högsta toppen i  " + quiz.getQuestionID() + "?");
            String userAnswer = scanner.nextLine().trim(); // tar bort extra whitespace

            if (userAnswer.equalsIgnoreCase(quiz.getCorrectAnswer())) {
                ++score;
                System.out.println("Rätt svar!\n");

            } else {
                System.out.println("Fel svar. Det korrekta svaret är: " + quiz.getCorrectAnswer() + "\n");
            }
        }
        System.out.println("Du fick " + score + " poäng av " + quizList.size() + " möjliga.\n");
        saveResult(userID, score);
    }

    public static List<Quiz> getQuizByDifficultyAndCategory(int difficultyID, int categoryID) {
    List<Quiz> quizzes = em.createQuery(
            "SELECT q FROM Quiz q WHERE q.difficultyID.id = :difficultyID AND q.categoryID.id = :categoryID",
                    Quiz.class
            )
            .setParameter("difficultyID", difficultyID)
            .setParameter("categoryID", categoryID)
            .getResultList();
    return quizzes;
    }

    public static void saveResult(int userID, int score) {
        User user = em.find(User.class, userID);
        Result result = new Result();

        result.setScore(score);
        result.setUserID(user);

        em.getTransaction().begin();
        em.persist(result);
        em.getTransaction().commit();
    }
}
