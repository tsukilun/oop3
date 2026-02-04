import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:8888/oop";
    private static final String USER = "postgres";
    private static final String PASSWORD = "981891";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // CREATE - Insert new user
    public static int createUser(String name, String email) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Failed to create user");
        }
    }

    // READ - Get all users
    public static List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }
        }
        return users;
    }

    // READ - Get user by ID
    public static User getUserById(int id) throws SQLException {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
            }
        }
        return null;
    }

    // UPDATE - Update user
    public static boolean updateUser(int id, String name, String email) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // DELETE - Delete user
    public static boolean deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // CREATE - Insert new post
    public static int createPost(int authorId, String content, int likes) throws SQLException {
        String sql = "INSERT INTO posts (author_id, content, likes) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            pstmt.setString(2, content);
            pstmt.setInt(3, likes);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Failed to create post");
        }
    }

    // READ - Get all posts
    public static List<Post> getAllPosts() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.id, p.author_id, p.content, p.likes, u.name, u.email " +
                     "FROM posts p JOIN users u ON p.author_id = u.id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User author = new User(
                    rs.getInt("author_id"),
                    rs.getString("name"),
                    rs.getString("email")
                );
                Post post = new Post(
                    rs.getInt("id"),
                    author,
                    rs.getString("content")
                );
                // Set likes manually
                int likes = rs.getInt("likes");
                for (int i = 0; i < likes; i++) {
                    post.like();
                }
                posts.add(post);
            }
        }
        return posts;
    }

    // UPDATE - Update post
    public static boolean updatePost(int id, String content) throws SQLException {
        String sql = "UPDATE posts SET content = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // UPDATE - Increment post likes
    public static boolean likePost(int id) throws SQLException {
        String sql = "UPDATE posts SET likes = likes + 1 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    // DELETE - Delete post
    public static boolean deletePost(int id) throws SQLException {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}