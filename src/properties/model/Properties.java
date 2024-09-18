package properties.model;

import user.model.ActivationStatus;

import java.util.Objects;

public class Properties {
    private int id;
    private ActivationStatus propertiesStatus;
    private final String name;
    private final String location;
    private final String description;

    public Properties(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }

    public Properties(int id, String name, String location, String description, ActivationStatus propertiesStatus) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.propertiesStatus = propertiesStatus;
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

    public String getLocation() {
        return this.location;
    }

    public String getDescription() {
        return this.description;
    }

    public String toSimpleString() {
        return "Properties{ id=" + id + ", name=" + name + ", location=" + location + " }";
    }

    @Override
    public String toString() {
        return "Properties{ id=" + id + ", name=" + name + ", location=" + location +
                ", status=" + this.propertiesStatus + ", description=" + description + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Properties that)) return false;
        return Objects.equals(location, that.location) || Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, description);
    }
}