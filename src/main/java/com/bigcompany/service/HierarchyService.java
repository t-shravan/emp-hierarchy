package com.bigcompany.service;

import com.bigcompany.model.Employee;
import com.bigcompany.model.Hierarchy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for building the employee hierarchy from a flat list of employees.
 * <p>
 * This service is responsible for constructing the tree structure of the organization
 * and calculating the reporting depth for each employee.
 * </p>
 */
public class HierarchyService {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyService.class);

    /**
     * Builds the organizational hierarchy from a list of employees.
     * <p>
     * This method links employees to their managers and identifies the CEO (the root of the hierarchy).
     * It assumes that the CEO is the only employee without a manager. After building the tree,
     * it calculates the reporting depth for every employee.
     * </p>
     *
     * @param employees A flat list of all {@link Employee} objects.
     * @return The constructed {@link Hierarchy}.
     * @throws IllegalStateException if a CEO cannot be determined (e.g., no employee without a manager,
     *         or multiple such employees if not handled).
     */
    public Hierarchy buildHierarchy(List<Employee> employees) {
        Map<Long, Employee> employeeMap = employees.stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity()));

        Employee ceo = null;
        for (Employee employee : employees) {
            if (employee.getManagerId() != null) {
                Employee manager = employeeMap.get(employee.getManagerId());
                if (manager != null) {
                    manager.addSubordinate(employee);
                    employee.setManager(manager);
                } else {
                    logger.warn("Manager with ID {} not found for employee {}",
                            employee.getManagerId(), employee.getId());
                }
            } else {
                ceo = employee; // Assume employee with no manager is the CEO
            }
        }

        if (ceo == null) {
            throw new IllegalStateException("Could not determine the CEO. No employee without a manager found.");
        }

        calculateReportingDepths(ceo);
        logger.info("Successfully built employee hierarchy and calculated reporting depths.");
        return new Hierarchy(employeeMap, ceo);
    }

    /**
     * Calculates the reporting depth for each employee in the hierarchy using a Breadth-First Search (BFS) algorithm.
     * The depth of the CEO is 0.
     *
     * @param ceo The root of the employee hierarchy.
     */
    private void calculateReportingDepths(Employee ceo) {
        Queue<Employee> queue = new LinkedList<>();
        ceo.setDepth(0);
        queue.add(ceo);

        while (!queue.isEmpty()) {
            Employee manager = queue.poll();
            for (Employee subordinate : manager.getSubordinates()) {
                subordinate.setDepth(manager.getDepth() + 1);
                queue.add(subordinate);
            }
        }
    }
}