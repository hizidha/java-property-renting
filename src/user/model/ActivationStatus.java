package user.model;

import user.model.exception.StatusDoNotMatchException;

public enum ActivationStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String status;

    ActivationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return status;
    }

    public static ActivationStatus fromString(String status) {
        for (ActivationStatus s : ActivationStatus.values()) {
            if (s.getStatus().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new StatusDoNotMatchException();
    }
}