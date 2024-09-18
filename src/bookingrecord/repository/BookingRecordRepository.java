package bookingrecord.repository;

import bookingrecord.model.BookingRecord;
import properties.service.PropertiesService;
import user.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class BookingRecordRepository {
    private final Connection connection;
    private final UserService userService;
    private final PropertiesService propertiesService;

    public BookingRecordRepository(Connection connection) throws SQLException {
        this.connection = connection;
        this.userService = new UserService();
        this.propertiesService = new PropertiesService();
    }

    public Optional<BookingRecord> add(BookingRecord bookingRecord) {
        String sql = "INSERT TO borrowing_record (user_id, properties_id, check_in_date, " +
                     "check_out_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, bookingRecord.getUser().getId());
            statement.setInt(2, bookingRecord.getProperties().getId());
            statement.setDate(3, bookingRecord.getCheckIn());
            statement.setDate(4, bookingRecord.getCheckOut());
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

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

    public int dataAvailabilityAdd(java.sql.Date checkIn, java.sql.Date checkOut) {
        String sql = "SELECT COUNT(*) FROM properties p" +
                " WHERE p.status = 'ACTIVE' AND NOT EXISTS (" +
                "    SELECT 1 FROM booking_record b" +
                "    WHERE p.id = b.properties_id" +
                "    AND (b.check_out_date >= ? AND b.check_in_date <= ?))";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, checkIn);
            statement.setDate(2, checkOut);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private BookingRecord mappingAllData(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int userId = resultSet.getInt("user_id");
        int propertiesId = resultSet.getInt("properties_id");
        Date checkIn = resultSet.getDate("check_in_date");
        Date checkOut = resultSet.getDate("check_out_date");
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");

        return new BookingRecord(id, this.userService.getById(userId), this.propertiesService.getById(propertiesId),
                checkIn, checkOut, createdAt, updatedAt);
    }

    public List<BookingRecord> findBookingListBy(int id) {
        List<BookingRecord> bookingRecords = new ArrayList<>();
        String sql = "SELECT * FROM booking_record WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookingRecords.add(mappingAllData(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookingRecords;
    }
}