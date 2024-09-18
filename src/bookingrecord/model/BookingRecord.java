package bookingrecord.model;

import user.model.User;
import properties.model.Properties;

import java.sql.Timestamp;
import java.util.Date;

public class BookingRecord {
    private int id;
    private final User user;
    private final Properties properties;
    private final Date checkIn;
    private final Date checkOut;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public BookingRecord(User user, Properties properties, Date checkIn, Date checkOut) {
        this.user = user;
        this.properties = properties;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public BookingRecord(int id, User user, Properties properties, Date checkIn, Date checkOut, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.user = user;
        this.properties = properties;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return this.user;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public java.sql.Date getCheckIn() {
        return (java.sql.Date) checkIn;
    }

    public java.sql.Date getCheckOut() {
        return (java.sql.Date) checkOut;
    }

    public String toSimpleString() {
        return "BookingRecord{ id=" + this.id +
                ", properties= " + properties +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt + '}';
    }

    @Override
    public String toString() {
        return "User with name:" + this.user.getName() + ", email:" + this.user.getEmail() + ", phone:" + this.user.getPhone() +
                "\nProperties with name:" + this.properties.getName() + ", in:" + this.properties.getLocation() +
                "\nCheck-In at:" + this.checkIn + "\nCheck-Out at:" + this.checkOut;
    }
}