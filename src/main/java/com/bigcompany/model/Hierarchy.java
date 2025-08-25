package com.bigcompany.model;

import java.util.List;
import java.util.Map;

/**
 * Represents the entire organizational hierarchy.
 *
 * This class acts as a container for the complete map of employees for quick lookups
 * and holds a reference to the root(s) of the hierarchy (e.g., the CEO).
 */
public class Hierarchy {

    private final Map<Long, Employee> employeeMap;
    private final Employee ceo;

    public Hierarchy(Map<Long, Employee> employeeMap, Employee ceo) {
        this.employeeMap = employeeMap;
        this.ceo = ceo;
    }

    public Map<Long, Employee> getEmployeeMap() {
        return employeeMap;
    }

    public Employee getCeo() {
        return ceo;
    }
}