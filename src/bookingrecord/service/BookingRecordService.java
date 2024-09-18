package bookingrecord.service;

import bookingrecord.repository.BookingRecordRepository;
import bookingrecord.service.exception.DateIsFullException;
import bookingrecord.service.exception.FailedToCreateBookingRecordException;
import db.DB;
import bookingrecord.model.BookingRecord;
import properties.service.PropertiesService;
import user.model.User;
import user.service.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookingRecordService {
    UserService userService;
    PropertiesService propertiesService;
    BookingRecordRepository bookingRecordRepository;

    public BookingRecordService(UserService userService, PropertiesService propertiesService) throws SQLException {
        Connection connection = DB.connect();
        this.userService = new UserService();
        this.propertiesService = new PropertiesService();
        this.bookingRecordRepository = new BookingRecordRepository(connection);
    }

    public List<BookingRecord> getBookingListBy(User user) {
        return this.bookingRecordRepository.findBookingListBy(user.getId());
    }

    public BookingRecord create(BookingRecord bookingRecord) {
        int checkDate = this.bookingRecordRepository.dataAvailabilityAdd(bookingRecord.getCheckIn(), bookingRecord.getCheckOut());
        if (checkDate > 0) {
            throw new DateIsFullException();
        }
        return this.bookingRecordRepository.add(bookingRecord)
                .orElseThrow(FailedToCreateBookingRecordException::new);
    }
}