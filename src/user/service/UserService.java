package user.service;

import db.DB;
import user.model.User;
import user.repository.UserRepository;
import user.service.exception.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    UserRepository userRepository;

    public UserService() throws SQLException {
        Connection connection = DB.connect();
        this.userRepository = new UserRepository(connection);
    }

    public List<User> getAll() {
        return this.userRepository.getAll();
    }

    public List<User> getListBy(String name) {
        return this.userRepository.findListBy(name);
    }

    public User getById(int id) {
        return this.userRepository.findBy(id).orElseThrow(UserNotFoundException::new);
    }

    public User getByName(String name) {
        return this.userRepository.findBy(name).orElseThrow(UserNotFoundException::new);
    }

    public int create(User user) {
        int existingUser = this.userRepository.dataAvailabilityAdd(user);
        if (existingUser > 0) {
            throw new UserIsStillActiveOrEmailHasRegisteredException();
        }

        int newUser = this.userRepository.add(user);
        if (newUser <= 0) {
            throw new FailedToAddUserException();
        }
        return newUser;
    }

    public int update(User user, User newUser) {
        User existingUser = this.getById(user.getId());
        if (existingUser == null) {
            throw new UserNotFoundException();
        }

        User existingUserCompare = new User(existingUser.getName(), existingUser.getEmail(), existingUser.getPhone());
        boolean isChange = !newUser.getName().equals(existingUserCompare.getName()) ||
                           !newUser.getPhone().equals(existingUserCompare.getPhone());

        if (!isChange) {
            throw new UserDataHasNotChangedException();
        }

        newUser.setId(user.getId());
        int result = this.userRepository.update(newUser);

        if (result <= 0) {
            throw new FailedToUpdateUserException();
        }
        return result;
    }

    public void deleteBy(int id) {
        int existingUser = this.userRepository.dataAvailabilityDelete(this.getById(id));
        if (existingUser > 0) {
            throw new UserStillHaveListBookingException();
        }

        int result = this.userRepository.delete(id);
        if (result <= 0) {
            throw new FailedToRemoveUserException();
        }
    }
}