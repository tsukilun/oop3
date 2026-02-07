import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SocialMedia {
    private final String name;
    private final List<User> users;
    private final List<Post> posts;

    public SocialMedia(String name) {
        this.name = name;
        this.users = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Post> getPosts() {
        return new ArrayList<>(posts);
    }

    public boolean addUser(User user) {
        if (user == null || users.contains(user)) {
            return false;
        }
        users.add(user);
        return true;
    }

    public boolean addPost(Post post) {
        if (post == null || posts.contains(post)) {
            return false;
        }
        addUser(post.getAuthor());
        posts.add(post);
        return true;
    }

    public List<Post> searchPostsByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        String normalized = keyword.toLowerCase(Locale.ROOT);
        return posts.stream()
                .filter(p -> p.getContent().toLowerCase(Locale.ROOT).contains(normalized))
                .sorted(Comparator.comparing(Post::getLikes).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "SocialMedia{" + "name='" + name + "', users=" + users.size() + ", posts=" + posts.size()
                + "}";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        SocialMedia that = (SocialMedia) other;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
