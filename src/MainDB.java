import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainDB {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Social Media Database CLI ===");
        System.out.println("Make sure PostgreSQL is running and the 'socialmedia' database exists.");
        System.out.println();

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. User Operations");
            System.out.println("2. Post Operations");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput(scanner);

            switch (choice) {
                case 1:
                    userMenu(scanner);
                    break;
                case 2:
                    postMenu(scanner);
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }

    private static void userMenu(Scanner scanner) {
        System.out.println("\n--- User Operations ---");
        System.out.println("1. Create User");
        System.out.println("2. Read All Users");
        System.out.println("3. Read User by ID");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.print("Choose an option: ");

        int choice = getIntInput(scanner);

        try {
            switch (choice) {
                case 1:
                    createUser(scanner);
                    break;
                case 2:
                    readAllUsers();
                    break;
                case 3:
                    readUserById(scanner);
                    break;
                case 4:
                    updateUser(scanner);
                    break;
                case 5:
                    deleteUser(scanner);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void postMenu(Scanner scanner) {
        System.out.println("\n--- Post Operations ---");
        System.out.println("1. Create Post");
        System.out.println("2. Read All Posts");
        System.out.println("3. Update Post");
        System.out.println("4. Like Post");
        System.out.println("5. Delete Post");
        System.out.print("Choose an option: ");

        int choice = getIntInput(scanner);

        try {
            switch (choice) {
                case 1:
                    createPost(scanner);
                    break;
                case 2:
                    readAllPosts();
                    break;
                case 3:
                    updatePost(scanner);
                    break;
                case 4:
                    likePost(scanner);
                    break;
                case 5:
                    deletePost(scanner);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // User CRUD operations
    private static void createUser(Scanner scanner) throws SQLException {
        scanner.nextLine(); // consume newline
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        int id = DatabaseManager.createUser(name, email);
        System.out.println("User created with ID: " + id);
    }

    private static void readAllUsers() throws SQLException {
        List<User> users = DatabaseManager.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            System.out.println("\n--- All Users ---");
            users.forEach(System.out::println);
        }
    }

    private static void readUserById(Scanner scanner) throws SQLException {
        System.out.print("Enter user ID: ");
        int id = getIntInput(scanner);

        User user = DatabaseManager.getUserById(id);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("User not found.");
        }
    }

    private static void updateUser(Scanner scanner) throws SQLException {
        System.out.print("Enter user ID to update: ");
        int id = getIntInput(scanner);
        scanner.nextLine(); // consume newline

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();

        if (DatabaseManager.updateUser(id, name, email)) {
            System.out.println("User updated successfully.");
        } else {
            System.out.println("User not found or update failed.");
        }
    }

    private static void deleteUser(Scanner scanner) throws SQLException {
        System.out.print("Enter user ID to delete: ");
        int id = getIntInput(scanner);

        if (DatabaseManager.deleteUser(id)) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("User not found or delete failed.");
        }
    }

    // Post CRUD operations
    private static void createPost(Scanner scanner) throws SQLException {
        System.out.print("Enter author ID: ");
        int authorId = getIntInput(scanner);
        scanner.nextLine(); // consume newline

        System.out.print("Enter post content: ");
        String content = scanner.nextLine();

        int id = DatabaseManager.createPost(authorId, content, 0);
        System.out.println("Post created with ID: " + id);
    }

    private static void readAllPosts() throws SQLException {
        List<Post> posts = DatabaseManager.getAllPosts();
        if (posts.isEmpty()) {
            System.out.println("No posts found.");
        } else {
            System.out.println("\n--- All Posts ---");
            posts.forEach(System.out::println);
        }
    }

    private static void updatePost(Scanner scanner) throws SQLException {
        System.out.print("Enter post ID to update: ");
        int id = getIntInput(scanner);
        scanner.nextLine(); // consume newline

        System.out.print("Enter new content: ");
        String content = scanner.nextLine();

        if (DatabaseManager.updatePost(id, content)) {
            System.out.println("Post updated successfully.");
        } else {
            System.out.println("Post not found or update failed.");
        }
    }

    private static void likePost(Scanner scanner) throws SQLException {
        System.out.print("Enter post ID to like: ");
        int id = getIntInput(scanner);

        if (DatabaseManager.likePost(id)) {
            System.out.println("Post liked successfully.");
        } else {
            System.out.println("Post not found.");
        }
    }

    private static void deletePost(Scanner scanner) throws SQLException {
        System.out.print("Enter post ID to delete: ");
        int id = getIntInput(scanner);

        if (DatabaseManager.deletePost(id)) {
            System.out.println("Post deleted successfully.");
        } else {
            System.out.println("Post not found or delete failed.");
        }
    }

    private static int getIntInput(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }
}
