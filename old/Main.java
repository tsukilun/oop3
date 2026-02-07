import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SocialMedia platform = new SocialMedia("CampusChat");

        User alice = new User(1, "Alice", "alice@example.com");
        User bob = new User(2, "Bob", "bob@example.com");
        platform.addUser(alice);
        platform.addUser(bob);

        Post helloPost = new Post(1, alice, "Hello from Alice.");
        helloPost.like();
        Post studyGroup = new Post(2, bob, "Study group this weekend?");
        studyGroup.like();
        studyGroup.like();
        platform.addPost(helloPost);
        platform.addPost(studyGroup);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to " + platform.getName() + "!");

            System.out.print("Your name: ");
            String name = scanner.nextLine();
            System.out.print("Your email: ");
            String email = scanner.nextLine();
            User current = new User(3, name, email);
            platform.addUser(current);

            System.out.print("Your first post: ");
            String content = scanner.nextLine();
            Post userPost = new Post(3, current, content);
            platform.addPost(userPost);

            System.out.print("Keyword to search: ");
            String keyword = scanner.nextLine();
            List<Post> matches = platform.searchPostsByKeyword(keyword);

            System.out.println("\nSearch results (sorted by likes):");
            matches.forEach(System.out::println);

            System.out.println("\nAll entities (polymorphic toString):");
            List<PlatformEntity> entities = new ArrayList<>();
            entities.addAll(platform.getUsers());
            entities.addAll(platform.getPosts());
            entities.forEach(System.out::println);
        }
    }
}
