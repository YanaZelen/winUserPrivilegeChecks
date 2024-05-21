package com.test_stm.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.test_stm.DatabaseUtil;
import com.test_stm.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDAO {

    public void createUser(User user) {
        try {
            if (isUsernameTaken(user.getUsername())) {
                throw new SQLException("Username already taken");
            }

            String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
            try (Connection conn = DatabaseUtil.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException ex) {
            log.error("Can't execute SQL: " + ex.getMessage(), ex);
        }
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            log.error("Can't execute SQL: " + ex.getMessage(), ex);
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }
        } catch (SQLException ex) {
            log.error("Can't execute SQL: " + ex.getMessage(), ex);
        }
        return users;
    }

    public User getUserById(Long id) {
        User user = null;
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                }
            }
        } catch (SQLException ex) {
            log.error("Can't execute SQL: " + ex.getMessage(), ex);
        }
        return user;
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setLong(3, user.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            log.error("Can't execute SQL: " + ex.getMessage(), ex);
        }
    }

    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            log.error("Can't execute SQL: " + ex.getMessage(), ex);
        }
    }
}
