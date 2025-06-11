package models;

public class Driver {
    private final String firstName;
    private final String lastName;
    private final String phone;

    public Driver(String firstName, String lastName, String phone) {
        if (firstName == null || lastName == null || phone == null) {
            throw new IllegalArgumentException("Driver fields cannot be null");
        }
        if (!phone.matches("\\d+")) {
            throw new IllegalArgumentException("Phone must contain only digits");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + phone + ")";
    }
}
