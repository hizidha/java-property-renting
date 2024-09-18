import bookingrecord.model.BookingRecord;
import bookingrecord.service.BookingRecordService;
import properties.model.Properties;
import properties.service.PropertiesService;
import user.model.User;
import user.service.UserService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.util.Locale;
import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateFormatString = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.US);

    private final UserService userService;
    private final PropertiesService propertiesService;
    private final BookingRecordService bookingRecordService;

    public InputHandler() throws SQLException {
        this.userService = new UserService();
        this.propertiesService = new PropertiesService();
        this.bookingRecordService = new BookingRecordService(userService, propertiesService);
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            mainMenu();
            int choice = this.getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    searchPropertiesByLocation();
                    break;
                case 2:
                    detailProperties();
                    break;
                case 3:
                    bookingProperties();
                    break;
                case 4:
                    viewBookingListByUser();
                    break;
                case 5:
                    subMenuUser();
                    break;
                case 6:
                    subMenuProperties();
                    break;
                case 7:
                    System.out.println("\nExiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void mainMenu() {
        System.out.println("\n-------------------------------------");
        System.out.println("---*   Property Renting System   *---");
        System.out.println("-------------------------------------");
        System.out.println("1. Search Properties by Location");
        System.out.println("2. Detail Properties");
        System.out.println("3. Book a Properties");
        System.out.println("4. View My Booking List");
        System.out.println("5. Manage User Data");
        System.out.println("6. Manage Properties Data");
        System.out.println("7. Exit");
        System.out.println("-------------------------------------");
    }

    private void searchPropertiesByLocation() {
        String locationName = getStringInput("Enter location name: ");
        List<Properties> existingProperties = this.propertiesService.getListByLocation(locationName);
        if (existingProperties.isEmpty()) {
            System.out.println("\nProperty at location '" + locationName + "' was not found.");
            return;
        }
        System.out.println("\nProperty at location " + locationName);
        existingProperties.forEach(property -> System.out.println(property.toSimpleString()));
    }

    private void detailProperties() {
        int propertiesId = getIntInput("Enter properties id: ");
        Properties existingProperties = this.propertiesService.getDetailBy(propertiesId);
        System.out.println(existingProperties.toString());
    }

    private void bookingProperties() {
        String userName = getStringInput("Enter user full name: ");
        User existingUser = this.userService.getByName(userName);
        System.out.println(existingUser);

        String option = getStringInput("Is the above data correct? (Y/N): ");
        if (option.equalsIgnoreCase("Y")) {
            Date checkInUser = getDateInput("Enter check-in date (yyyy-MM-dd): ");
            Date checkOutUser = getDateInput("Enter check-out date (yyyy-MM-dd): ");
            displayAvailableProperties(checkInUser, checkOutUser);

            String propertiesName = getStringInput("\nEnter properties name: ");
            Properties existingProperties = this.propertiesService.getByName(propertiesName);
            System.out.println(existingProperties);

            String option2 = getStringInput("Is the above data correct? (Y/N): ");
            if (option2.equalsIgnoreCase("Y")) {
                displayResultBook(new BookingRecord(existingUser, existingProperties, checkInUser, checkOutUser));
            }
        }
    }

    private void displayResultBook(BookingRecord bookingRecord) {
        BookingRecord newBookingRecord = this.bookingRecordService.create(bookingRecord);
        System.out.println("\n------------------------------------");
        System.out.println("-----***   CONGRATULATION   ***-----");
        System.out.println("------------------------------------");
        System.out.println("* Your Order Process is Successful *");
        System.out.println(newBookingRecord);
        System.out.println("------------------------------------\n");
    }

    private void displayAvailableProperties(java.util.Date checkIn, java.util.Date checkOut) {
        System.out.println("\nList of Available properties from " + dateFormatString.format(checkIn) +
                            " to " + dateFormatString.format(checkOut) + " :");

        List<Properties> properties = this.propertiesService.getAllAvailableProperties(checkIn, checkOut);

        if (properties.isEmpty()) {
            System.out.println("Properties not found, try another date.");
            return;
        }
        properties.forEach(property -> System.out.println(property.toSimpleString()));
    }

    private void viewBookingListByUser() {
        String userName = getStringInput("Enter user full name: ");
        List<BookingRecord> existingBookingRecord = this.bookingRecordService.getBookingListBy(this.userService.getByName(userName));
        if (existingBookingRecord.isEmpty()) {
            System.out.println("\nBooking List by '" + userName + "' was not found.");
            return;
        }
        System.out.println("\nBooking List by '" + userName + "'");
        existingBookingRecord.forEach(bookingRecord -> System.out.println(bookingRecord.toSimpleString()));
    }

    public void subMenuUser() {
        while (true) {
            System.out.println("\n-------------------------------------");
            System.out.println("-----*    PRS / Manage User    *-----");
            System.out.println("-------------------------------------");
            System.out.println("1. Add User");
            System.out.println("2. Update User");
            System.out.println("3. Delete User");
            System.out.println("4. View User List");
            System.out.println("5. View User List by Name");
            System.out.println("6. View User by Id");
            System.out.println("7. View User by Full Name");
            System.out.println("8. Main Menu");
            System.out.println("9. Exit");
            System.out.println("-------------------------------------");

            int choice2 = getIntInput("Choose an option: ");
            switch (choice2) {
                case 1:
                    addUser();
                    break;
                case 2:
                    updateUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    viewUserList();
                    break;
                case 5:
                    viewUserListByName();
                    break;
                case 6:
                    viewUserById();
                    break;
                case 7:
                    viewUserByName();
                    break;
                case 8:
                    return;
                case 9:
                    System.out.println("\nExiting...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addUser() {
        String userName = getStringInput("Enter your full name: ");
        String userEmail = getStringInput("Enter your email: ");
        String userPhone = getStringInput("Enter your phone number: ");

        int userId = this.userService.create(new User(userName, userEmail, userPhone));
        System.out.println("\nUser added: " + this.userService.getById(userId));
    }

    private void updateUser() {
        int userId = getIntInput("Enter user id: ");
        User existingUser = this.userService.getById(userId);
        System.out.println(existingUser);

        String option = getStringInput("Are you sure to edit this User? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            String userName = getStringInput("Enter new name: ");
            String userPhone = getStringInput("Enter new phone: ");

            int updatedUserId = this.userService.update(existingUser, new User(userName, existingUser.getEmail(), userPhone));
            System.out.println("\nUser updated: " + this.userService.getById(updatedUserId));
        }
    }

    private void deleteUser() {
        viewUserList();
        int userId = getIntInput("Enter user id: ");
        System.out.println(this.userService.getById(userId));

        String option = getStringInput("Are you sure to delete this User? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            this.userService.deleteBy(userId);
            System.out.println("\nUser deleted.");
        }
    }

    private void viewUserList() {
        List<User> users = this.userService.getAll();
        if (users.isEmpty()) {
            System.out.println("\nList of User not found.");
            return;
        }
        System.out.println("\nUser List");
        users.forEach(System.out::println);
    }

    private void viewUserListByName() {
        String userName = getStringInput("Enter user name: ");
        List<User> users = this.userService.getListBy(userName);
        if (users.isEmpty()) {
            System.out.println("List of User not found.");
            return;
        }
        System.out.println("\nUser List by Name: " + userName);
        users.forEach(System.out::println);
    }

    private void viewUserById() {
        int userId = getIntInput("Enter user id: ");
        System.out.println("\nUser by Id " + userId);
        System.out.println(this.userService.getById(userId));
    }

    private void viewUserByName() {
        String userName = getStringInput("Enter user full name: ");
        System.out.println("\nUser by Name " + userName);
        System.out.println(this.userService.getByName(userName));
    }

    public void subMenuProperties() {
        while (true) {
            System.out.println("\n-------------------------------------");
            System.out.println("---*   PRS / Manage Properties   *---");
            System.out.println("-------------------------------------");
            System.out.println("1. Add Properties");
            System.out.println("2. Update Properties");
            System.out.println("3. Delete Properties");
            System.out.println("4. View Properties List");
            System.out.println("5. View Properties List by Name");
            System.out.println("6. View Properties by Id");
            System.out.println("7. View Properties by Full Name");
            System.out.println("8. Main Menu");
            System.out.println("9. Exit");
            System.out.println("-------------------------------------");

            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    addProperties();
                    break;
                case 2:
                    updateProperties();
                    break;
                case 3:
                    deleteProperties();
                    break;
                case 4:
                    viewPropertiesList();
                    break;
                case 5:
                    viewPropertiesListByName();
                    break;
                case 6:
                    viewPropertiesById();
                    break;
                case 7:
                    viewPropertiesByName();
                    break;
                case 8:
                    return;
                case 9:
                    System.out.println("\nExiting...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addProperties() {
        String propertiesName = getStringInput("Enter properties name: ");
        String propertiesLocation = getStringInput("Enter properties location: ");
        String propertiesDescription = getStringInput("Enter properties description: ");

        int propertiesId = this.propertiesService.create(new Properties(propertiesName, propertiesLocation, propertiesDescription));
        System.out.println("\nProperties added: " + this.propertiesService.getById(propertiesId).toString());
    }

    private void updateProperties() {
        int propertiesId = getIntInput("Enter properties id: ");
        Properties existingProperties = this.propertiesService.getById(propertiesId);
        System.out.println(existingProperties);

        String option = getStringInput("Are you sure to edit this Properties? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            String propertiesLocation = getStringInput("Enter new properties location: ");
            String propertiesDescription = getStringInput("Enter new properties description: ");

            int updatedPropertiesId = this.propertiesService.update(existingProperties, new Properties(existingProperties.getName(), propertiesLocation, propertiesDescription));
            System.out.println("\nProperties updated: " + this.propertiesService.getById(updatedPropertiesId).toString());
        }
    }

    private void deleteProperties() {
        viewPropertiesList();
        int propertiesId = getIntInput("Enter properties id: ");
        System.out.println(this.propertiesService.getById(propertiesId));

        String option = getStringInput("Are you sure to delete this User? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            this.propertiesService.deleteBy(propertiesId);
            System.out.println("Properties deleted.");
        }
    }

    private void viewPropertiesList() {
        List<Properties> properties = this.propertiesService.getAll();
        if (properties.isEmpty()) {
            System.out.println("\nList of Properties not found.");
            return;
        }
        System.out.println("\nProperties List");
        properties.forEach(property -> System.out.println(property.toString()));
    }

    private void viewPropertiesListByName() {
        String propertiesName = getStringInput("Enter properties name: ");
        List<Properties> properties = this.propertiesService.getListBy(propertiesName);
        if (properties.isEmpty()) {
            System.out.println("List of Properties not found.");
            return;
        }
        System.out.println("\nProperties List by Name: " + propertiesName);
        properties.forEach(property -> System.out.println(property.toString()));
    }

    private void viewPropertiesById() {
        int propertiesId = getIntInput("Enter properties id: ");
        System.out.println("\nProperties by Id " + propertiesId);
        System.out.println(this.propertiesService.getById(propertiesId).toString());
    }
        
    private void viewPropertiesByName() {
        String propertiesName = getStringInput("Enter properties name: ");
        System.out.println("\nProperties by Name " + propertiesName);
        System.out.println(this.propertiesService.getByName(propertiesName).toString());
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private Date getDateInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            String input = scanner.nextLine();
            try {
                return new Date(dateFormat.parse(input).getTime());
            } catch (ParseException e) {
                System.out.print("Invalid date format. Please enter the date in yyyy-MM-dd format: ");
            }
        }
    }
}