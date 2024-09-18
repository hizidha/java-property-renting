package user.repository;

import common.CRUDRepository;
import user.model.ActivationStatus;
import user.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements CRUDRepository<User> {
    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(User t) {
        String sql = "INSERT INTO users (name, email, phone, status) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, t.getName());
            statement.setString(2, t.getEmail());
            statement.setString(3, t.getPhone());
            statement.setString(4, ActivationStatus.ACTIVE.toString().toUpperCase());

            int insertedRow = statement.executeUpdate();
            if (insertedRow > 0) {
                ResultSet resultSet = statement.getGeneratedKeys();

                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int update(User t) {
        String sql = "UPDATE users SET name = ?, phone = ? WHERE id = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, t.getName());
            statement.setString(2, t.getPhone());
            statement.setInt(3, t.getId());

            int isSuccess = statement.executeUpdate();
            if (isSuccess == 1) {
                return t.getId();
            }
            return isSuccess;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int delete(int id) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setInt(2, id);
            statement.setString(1, ActivationStatus.INACTIVE.toString().toUpperCase());

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id ASC";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                users.add(mappingAllData(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public int dataAvailabilityAdd(User t) {
        String sql = "SELECT COUNT(*) FROM users WHERE UPPER(email) = ? OR (UPPER(name) = ? AND status = 'ACTIVE')";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, t.getEmail().toUpperCase());
            statement.setString(2, t.getName().toUpperCase());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int dataAvailabilityDelete(User t) {
        String sql = "SELECT COUNT(*) FROM users WHERE status = 'ACTIVE' AND id = ? " +
                     "AND NOT EXISTS (SELECT 1 FROM booking_record WHERE user_id = ? AND check_out_date < ?)";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setInt(1, t.getId());
            statement.setInt(2, t.getId());
            statement.setDate(3, Date.valueOf(LocalDate.now()));

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Optional<User> findBy(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mappingAllData(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findBy(String name) {
        String sql = "SELECT * FROM users WHERE UPPER(name) = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name.toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mappingAllData(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findListBy(String name) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE UPPER(name) LIKE ? ORDER BY id ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name.toUpperCase() + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mappingAllData(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private User mappingAllData(ResultSet resultSet) throws SQLException {
        int idUser = resultSet.getInt("id");
        String nameUser = resultSet.getString("name");
        String emailUser = resultSet.getString("email");
        String phoneUser = resultSet.getString("phone");
        ActivationStatus userStatus = ActivationStatus.fromString(resultSet.getString("status"));

        return new User(idUser, nameUser, emailUser, phoneUser, userStatus);
    }
}