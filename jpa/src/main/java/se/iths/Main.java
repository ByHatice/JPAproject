package se.iths;

import jakarta.persistence.*;
import se.iths.entity.User;
import se.iths.main.QuizQueries;
import java.util.*;
import java.util.function.Consumer;

public class Main {
    public static final EntityManager em = JPAUtil.getEntityManager();
    public static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean programRunning = true;
        int choiceOfMenu;

        while (programRunning) {
            choiceOfMenu = menu();

            switch (choiceOfMenu) {
                case 1:
                    addUser();
                    break;
                case 2:
                    showAllUsers();
                    break;
                case 3:
                    updateUser();
                    break;
                case 4:
                    deleteUser();
                    break;
                case 5:
                    QuizQueries.quizeSwitch();
                    break;
                case 6:
                    scoreList();
                    break;
                case 7:
                    em.close();
                    System.out.println("Program avslutas...");
                    programRunning = false;
                    break;
                default:
                    System.out.println("Ogiltig inmatning. Var vänlig ange siffra mellan 1 till 7.\n");
                    break;
            }
        }
        scanner.close();
    }

    public static int menu() {
        System.out.println("""
                
                        Meny
                1. Lägg till ny spelare
                2. Visa alla spelare
                3. Uppdatera spelare
                4. Ta bort spelare
                5. Starta quiz
                6. Visa resultatlista
                7. Avsluta programmet
                
                      """);
        return input();
    }

    public static void addUser() {
        System.out.println("Lägg till ny spelare");
        System.out.println("Ange förnamn: ");
        String firstName = scanner.nextLine();
        System.out.println("Ange efternamn: ");
        String lastName = scanner.nextLine();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        inTransaction(em -> {
            em.persist(user);
            System.out.println("Ny spelare tillagd!\n");
        });
    }

    public static void showAllUsers() {
        System.out.println("Alla spelare i databasen:");
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        List<User> users = query.getResultList();
        users.forEach(System.out::println);
    }

    public static void deleteUser() {
        System.out.println("Ta bort spelare");
        System.out.println("Ange ID på spelare som ska tas bort: ");
        int id = input();

        User user = em.find(User.class, id);

        if (user != null) {
            String playerName = user.getFirstName() + " " + user.getLastName();
            em.getTransaction().begin();
            em.remove(user);
            em.getTransaction().commit();
            System.out.println(playerName + " är borttagen!\n");
        } else {
            System.out.println("Spelare med ID " + id + " finns inte i databasen.\n");
        }
    }

    public static void updateUser() {
        System.out.println("Uppdatera spelare");
        System.out.println("Ange ID på spelare som ska uppdateras: ");
        int id = input();
        User user = em.find(User.class, id);
        if (user != null) {
            System.out.println("Ange nytt förnamn: ");
            String firstName = scanner.nextLine();
            System.out.println("Ange nytt efternamn: ");
            String lastName = scanner.nextLine();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            System.out.println("Spelare uppdaterad!\n");
        } else {
            System.out.println("Spelare med ID " + id + " finns inte i databasen.\n");
        }
    }

    public static int input() {
        int choice = -1;

        try {
            if (scanner.hasNextLine()) {
                String inputString = scanner.nextLine();
                choice = Integer.parseInt(inputString);
            }
        } catch (InputMismatchException | NumberFormatException e) {
            System.out.println("\nOgiltig inmatning. Var vänlig ange giltig siffra.\n");
            scanner.nextLine();
        }
        return choice;
    }

    public static void scoreList() {
        String queryString = "SELECT u.id, u.firstName, u.lastName, SUM(r.score) as totalScore " +
                "FROM User u " +
                "JOIN u.results r " +
                "GROUP BY u.id " +
                "ORDER BY totalScore DESC";

        Query query = em.createQuery(queryString);

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();

        for (Object[] result : resultList) {
            Integer userId = (Integer) result[0];
            String firstName = (String) result[1];
            String lastName = (String) result[2];
            Long totalScore = (Long) result[3];

            System.out.println("UserID: " + userId + ", Name: " + firstName + " " + lastName + ", Total Score: " + totalScore);
        }
    }

    static void inTransaction(Consumer<EntityManager> work) {
        try (EntityManager entityManager = JPAUtil.getEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                work.accept(entityManager);
                transaction.commit();
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}
