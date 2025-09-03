package com.bigcompany.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An employee in the organizational hierarchy.
 * <p>
 * This class holds information about an employee, including their ID, name, salary, and manager.
 * It also maintains a list of direct subordinates and their depth in the hierarchy.
 * The class uses a Builder pattern for instantiation.
 * </p>
 */
public class Employee {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final BigDecimal salary;
    private final Long managerId;

    private Employee manager;
    private final List<Employee> subordinates = new ArrayList<>();
    private int depth = 0; // Default depth

    private Employee(Long id, String firstName, String lastName, BigDecimal salary, Long managerId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }

    /**
     * A builder class for creating {@link Employee} instances.
     */
    public static class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private BigDecimal salary;
        private Long managerId;

        /**
         * Sets the employee's ID.
         * @param id The unique identifier for the employee.
         * @return The builder instance.
         */
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the employee's first name.
         * @param firstName The first name of the employee.
         * @return The builder instance.
         */
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
         * Sets the employee's last name.
         * @param lastName The last name of the employee.
         * @return The builder instance.
         */
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
         * Sets the employee's salary.
         * @param salary The salary of the employee.
         * @return The builder instance.
         */
        public Builder salary(BigDecimal salary) {
            this.salary = salary;
            return this;
        }

        /**
         * Sets the ID of the employee's manager.
         * @param managerId The ID of the manager, or null if the employee has no manager.
         * @return The builder instance.
         */
        public Builder managerId(Long managerId) {
            this.managerId = managerId;
            return this;
        }

        /**
         * Builds and returns a new {@link Employee} instance.
         * @return A new {@link Employee}.
         */
        public Employee build() {
            return new Employee(id, firstName, lastName, salary, managerId);
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public Long getManagerId() {
        return managerId;
    }

    public Employee getManager() {
        return manager;
    }

    public List<Employee> getSubordinates() {
        return subordinates;
    }

    public int getDepth() {
        return depth;
    }

    // Setters for hierarchy
    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void addSubordinate(Employee subordinate) {
        this.subordinates.add(subordinate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salary=" + salary +
                ", managerId=" + managerId +
                ", depth=" + depth +
                '}';
    }
}