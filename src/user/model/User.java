package user.model;

import java.util.Objects;

public class User {
    private int id;
    private ActivationStatus userStatus;
    private final String name;
    private final String email;
    private final String phone;

    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User(int id, String name, String email, String phone, ActivationStatus userStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userStatus = userStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    @Override
    public String toString() {
        return "User{ id=" + this.id + ", name=" + this.name +
                ", email=" + this.email + ", phone=" + this.phone +
                ", status=" + this.userStatus + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(name, user.name) || Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone);
    }
}