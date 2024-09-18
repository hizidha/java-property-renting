package properties.repository;

import common.CRUDRepository;
import properties.model.Properties;
import user.model.ActivationStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

public class PropertiesRepository implements CRUDRepository<Properties> {
    private final Connection connection;

    public PropertiesRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int add(Properties t) {
        String sql = "INSERT INTO properties (name, location, description, status) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, t.getName());
            statement.setString(2, t.getLocation());
            statement.setString(3, t.getDescription());
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
    public int update(Properties t) {
        String sql = "UPDATE properties SET location = ?, description = ? WHERE id = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, t.getLocation());
            statement.setString(2, t.getDescription());
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
        String sql = "UPDATE properties SET status = ? WHERE id = ? ";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setInt(2, id);
            statement.setString(1, ActivationStatus.INACTIVE.toString());

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Properties> getAll() {
        List<Properties> properties = new ArrayList<>();
        String sql = "SELECT * FROM properties ORDER BY id ASC";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                properties.add(mappingAllData(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    @Override
    public int dataAvailabilityAdd(Properties t) {
        String sql = "SELECT COUNT(*) FROM properties WHERE UPPER(name) = ? AND UPPER(location) = ? AND status = 'ACTIVE'";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, t.getName().toUpperCase());
            statement.setString(2, t.getLocation().toUpperCase());


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
    public int dataAvailabilityDelete(Properties t) {
        String sql = "SELECT COUNT(*) FROM properties WHERE status = 'ACTIVE' AND id = ? " +
                     "AND NOT EXISTS (SELECT 1 FROM booking_record WHERE properties_id = ? AND check_out_date < ?)";

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
    public Optional<Properties> findBy(int id) {
        String sql = "SELECT * FROM properties WHERE id = ?";

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
    public Optional<Properties> findBy(String name) {
        String sql = "SELECT * FROM properties WHERE UPPER(name) = ?";

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
    public List<Properties> findListBy(String name) {
        List<Properties> properties = new ArrayList<>();
        String sql = "SELECT * FROM properties WHERE UPPER(name) LIKE ? ORDER BY id ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name.toUpperCase() + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    properties.add(mappingAllData(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public List<Properties> findListByLocation(String name) {
        List<Properties> properties = new ArrayList<>();
        String sql = "SELECT * FROM properties WHERE status='ACTIVE' AND UPPER(location) = ? ORDER BY id ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    properties.add(mappingAllData(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public Optional<Properties> findDetailBy(int id) {
        String sql = "SELECT * FROM properties WHERE status = 'ACTIVE' AND id = ?";

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

    private Properties mappingAllData(ResultSet resultSet) throws SQLException {
        int idProperties = resultSet.getInt("id");
        String nameProperties = resultSet.getString("name");
        String locationProperties = resultSet.getString("location");
        String descriptionProperties = resultSet.getString("description");
        ActivationStatus propertiesStatus = ActivationStatus.fromString(resultSet.getString("status"));

        return new Properties(idProperties, nameProperties, locationProperties, descriptionProperties, propertiesStatus);
    }

    public List<Properties> getAllAvailableProperties(java.util.Date checkIn, java.util.Date checkOut) {
        List<Properties> propertiesList = new ArrayList<>();
        String sql = "SELECT p.* FROM properties p" +
                " WHERE p.status = 'ACTIVE' AND NOT EXISTS (" +
                "    SELECT 1 FROM booking_record b\n" +
                "    WHERE p.id = b.properties_id\n" +
                "    AND (b.check_out_date >= ? AND b.check_in_date <= ?" +
                "    )) ORDER BY p.id ASC;";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(checkIn.getTime()));
            statement.setDate(2, new Date(checkOut.getTime()));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                propertiesList.add(mappingAllData(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return propertiesList;
    }
}