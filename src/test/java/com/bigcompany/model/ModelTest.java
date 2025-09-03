package com.bigcompany.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testEmployeeModel() {
        Employee e1 = new Employee.Builder().id(1L).firstName("John").lastName("Doe").salary(new BigDecimal("1000")).managerId(null).build();
        Employee e2 = new Employee.Builder().id(1L).firstName("Jane").lastName("Doe").salary(new BigDecimal("2000")).managerId(null).build();
        Employee e3 = new Employee.Builder().id(2L).firstName("Peter").lastName("Jones").salary(new BigDecimal("3000")).managerId(1L).build();

        // Test equals and hashCode
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertEquals(e1.hashCode(), e2.hashCode());

        // Test getters
        assertEquals("John", e1.getFirstName());
        assertEquals("Doe", e1.getLastName());
        assertEquals(0, new BigDecimal("1000").compareTo(e1.getSalary()));
        assertNull(e1.getManagerId());

        // Test toString
        assertTrue(e1.toString().contains("John"));
        
        // Test setters
        e3.setManager(e1);
        assertEquals(e1, e3.getManager());
        
        e1.setDepth(1);
        assertEquals(1, e1.getDepth());
    }

    @Test
    void testHierarchyModel() {
        Employee ceo = new Employee.Builder().id(1L).build();
        Hierarchy hierarchy = new Hierarchy(java.util.Map.of(1L, ceo), ceo);
        assertEquals(ceo, hierarchy.getCeo());
        assertEquals(1, hierarchy.getEmployeeMap().size());
    }
}