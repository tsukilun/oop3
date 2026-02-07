package com.socialmedia.repository;

import com.socialmedia.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    // Constructor injection (best practice, no @Autowired needed)
    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** converts each DB row into a User object */
    private final RowMapper<User> userMapper = (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email")
    );

    public List<User> findAll() {
        return jdbc.query("SELECT id, name, email FROM users", userMapper);
    }

    public Optional<User> findById(int id) {
        List<User> results = jdbc.query("SELECT id, name, email FROM users WHERE id = ?", userMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public int save(String name, String email) {
        return jdbc.queryForObject(
                "INSERT INTO users (name, email) VALUES (?, ?) RETURNING id",
                Integer.class, name, email
        );
    }

    public boolean update(int id, String name, String email) {
        int rows = jdbc.update("UPDATE users SET name = ?, email = ? WHERE id = ?", name, email, id);
        return rows > 0;
    }

    public boolean delete(int id) {
        int rows = jdbc.update("DELETE FROM users WHERE id = ?", id);
        return rows > 0;
    }

    public int count() {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        return c != null ? c : 0;
    }
}
