package com.socialmedia.repository;

import com.socialmedia.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbc;

    public PostRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** converts DB row (with JOIN) into Post object */
    private final RowMapper<Post> postMapper = (rs, rowNum) -> new Post(
            rs.getInt("id"),
            rs.getInt("author_id"),
            rs.getString("author_name"),
            rs.getString("content"),
            rs.getInt("likes"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
    );

    private static final String SELECT_WITH_AUTHOR =
            "SELECT p.id, p.author_id, u.name AS author_name, p.content, p.likes, p.created_at " +
            "FROM posts p JOIN users u ON p.author_id = u.id";

    public List<Post> findAll() {
        return jdbc.query(SELECT_WITH_AUTHOR + " ORDER BY p.created_at DESC", postMapper);
    }

    public Optional<Post> findById(int id) {
        List<Post> results = jdbc.query(SELECT_WITH_AUTHOR + " WHERE p.id = ?", postMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public int save(int authorId, String content) {
        return jdbc.queryForObject(
                "INSERT INTO posts (author_id, content, likes) VALUES (?, ?, 0) RETURNING id",
                Integer.class, authorId, content
        );
    }

    public boolean update(int id, String content) {
        int rows = jdbc.update("UPDATE posts SET content = ? WHERE id = ?", content, id);
        return rows > 0;
    }

    public boolean like(int id) {
        int rows = jdbc.update("UPDATE posts SET likes = likes + 1 WHERE id = ?", id);
        return rows > 0;
    }

    public boolean delete(int id) {
        int rows = jdbc.update("DELETE FROM posts WHERE id = ?", id);
        return rows > 0;
    }

    /** search posts by keyword, sorted by likes (most popular first) */
    public List<Post> searchByKeyword(String keyword) {
        String pattern = "%" + keyword.toLowerCase() + "%";
        return jdbc.query(
                SELECT_WITH_AUTHOR + " WHERE LOWER(p.content) LIKE ? ORDER BY p.likes DESC",
                postMapper, pattern
        );
    }

    public int count() {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM posts", Integer.class);
        return c != null ? c : 0;
    }

    public int totalLikes() {
        Integer s = jdbc.queryForObject("SELECT COALESCE(SUM(likes), 0) FROM posts", Integer.class);
        return s != null ? s : 0;
    }
}
