package com.revature.daos;

import com.revature.models.Employee;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;


public class EmployeeDaoTest {
    private EmployeeDao testing;
    private static SessionFactory testSessionFactory;

    /**
     * setup for all the tests - configures hibernate
     */
    @BeforeClass
    public static void initClass(){
        Configuration configuration = new Configuration();
        configuration.configure("hibernateTest.cfg.xml");
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        testSessionFactory = configuration.buildSessionFactory(ssrb.build());

    }

    /**
     * setup for individual tests - recreates the dao object
     */
    @Before
    public void init(){
        testing = new EmployeeDao();
        testing.setSessionFactory(testSessionFactory);
    }

    /**
     * deletes all entries from the table that stores Employee in the testing database
     */
    @After
    public void teardown(){
        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        String clearEmployeeTable = "delete from Employee";
        Query query = session.createQuery(clearEmployeeTable);
        query.executeUpdate();

        tx.commit();
        session.close();
    }

    /**
     * tests that insertEmployee() inserts an employee if that employee has a unique email
     */
    @Test
    public void insertEmployeeInsertsNewEmpUniqueEmail(){
        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee = initializedEmployees.get(0);
        testing.insertEmployee(testEmployee);

        Session session = testSessionFactory.openSession();
        String hql = "from Employee as emp order by emp.lastName asc";
        Query query = session.createQuery(hql);

        List<Employee> allEmployees = query.list();
        session.close();

        Assert.assertFalse(allEmployees.isEmpty());
        Assert.assertEquals(testEmployee, allEmployees.get(0));
    }

    /**
     * tests that get all returns the full list of employees if there are employees in the database.
     */
    @Test
    public void getAllReturnsFullListPopulated(){


        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee1 = initializedEmployees.get(0);
        Employee testEmployee2 = initializedEmployees.get(1);
        Employee testEmployee3 = initializedEmployees.get(2);

        Session session = testSessionFactory.openSession();

        Transaction tx = session.beginTransaction();
        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        tx.commit();
        session.close();

        List<Employee> testListEmployees = testing.getAllEmployees();

        Assert.assertTrue(testEmployee1.equalsExceptId(testListEmployees.get(0)));
        Assert.assertTrue(testEmployee2.equalsExceptId(testListEmployees.get(1)));
        Assert.assertTrue(testEmployee3.equalsExceptId(testListEmployees.get(2)));
    }



    /**
     * tests that getAllEmployees returns an empty list if there are no employees in the database
     */
    @Test
    public void getAllEmployeesReturnsEmptyListWhenEmpty(){
        List<Employee> testListEmployees = testing.getAllEmployees();
        Assert.assertTrue(testListEmployees.isEmpty());
    }

    /**
     * tests that getEmployeeById returns the correct employee.
     */
    @Test
    public void getEmployeeByIdReturnsCorrectEmployee(){
        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee1 = initializedEmployees.get(0);
        Employee testEmployee2 = initializedEmployees.get(1);
        Employee testEmployee3 = initializedEmployees.get(2);

        Session session = testSessionFactory.openSession();

        Transaction tx = session.beginTransaction();
        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        tx.commit();
        session.close();

        Employee expected = testEmployee3;

        Employee result = testing.getEmployeeById(expected.getId());
        Assert.assertEquals(expected, result);
    }

    /**
     * tests that getEmployeeByEmail returns the correct employee.
     */
    @Test
    public void getEmployeeByEmailReturnsCorrectEmployee(){
        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee1 = initializedEmployees.get(0);
        Employee testEmployee2 = initializedEmployees.get(1);
        Employee testEmployee3 = initializedEmployees.get(2);

        Session session = testSessionFactory.openSession();

        Transaction tx = session.beginTransaction();
        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        tx.commit();
        session.close();

        Employee expected = testEmployee2;

        Employee result = testing.getEmployeeByEmail(expected.getEmail());
        Assert.assertEquals(expected, result);
    }

    /**
     * tests that getEmployeeByEmail returns null when no such employee exists.
     */
    @Test
    public void getEmployeeByEmailReturnsNullWhenNoEmployee(){
        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee1 = initializedEmployees.get(0);
        Employee testEmployee2 = initializedEmployees.get(1);
        Employee testEmployee3 = initializedEmployees.get(2);

        Session session = testSessionFactory.openSession();

        Transaction tx = session.beginTransaction();
        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        tx.commit();
        session.close();

        String invalidEmail = "thisIsNotAnEmailInTheDb@example.com";

        Employee result = testing.getEmployeeByEmail(invalidEmail);
        Assert.assertNull(result);
    }


    /**
     * tests that getEmployeeByEmailPassword returns a list with a single user (the correct one) when given a valid
     * username and password
     */
    @Test
    public void getEmployeeByEmailPasswordReturnsCorrectUser(){
        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee1 = initializedEmployees.get(0);
        Employee testEmployee2 = initializedEmployees.get(1);
        Employee testEmployee3 = initializedEmployees.get(2);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        tx.commit();
        session.close();

        List<Employee> selectedUserList = testing.getEmployeeByEmailPassword(testEmployee2.getEmail(),
                testEmployee2.getPassword());

        Assert.assertEquals(1, selectedUserList.size());
        Assert.assertTrue(testEmployee2.equalsExceptId(selectedUserList.get(0)));
    }

    /**
     * tests that getEmployeeByEmailPassword returns empty list when given bad email
     */
    @Test
    public void getEmployeeByEmailPasswordReturnsEmptyBadEmail(){
        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee1 = initializedEmployees.get(0);
        Employee testEmployee2 = initializedEmployees.get(1);
        Employee testEmployee3 = initializedEmployees.get(2);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        tx.commit();
        session.close();

        String testEmail = "thisEmail@notIn.db";

        List<Employee> selectedUserList = testing.getEmployeeByEmailPassword(testEmail,
                testEmployee2.getPassword());

        Assert.assertTrue(selectedUserList.isEmpty());

    }

    /**
     * tests that getEmployeeByEmailPassword returns empty list when given bad password
     */
    @Test
    public void getEmployeeByEmailPasswordReturnsEmptyBadPassword(){
        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee1 = initializedEmployees.get(0);
        Employee testEmployee2 = initializedEmployees.get(1);
        Employee testEmployee3 = initializedEmployees.get(2);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        tx.commit();
        session.close();

        String testPassword = "thisPasswordNotAssoc";

        List<Employee> selectedUserList = testing.getEmployeeByEmailPassword(testEmployee2.getEmail(),
                testPassword);

        Assert.assertTrue(selectedUserList.isEmpty());

    }

    /**
     * tests that updateEmployee properly updates an employee that's in the database
     */
    @Test
    public void updateEmployeeUpdatesEmployee(){
        List<Employee> initializedEmployees = makeThreeEmployees();
        Employee testEmployee1 = initializedEmployees.get(0);
        Employee testEmployee2 = initializedEmployees.get(1);
        Employee testEmployee3 = initializedEmployees.get(2);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        tx.commit();
        session.close();

        Employee expected = testEmployee1;
        expected.setPassword("hooplaboberino");
        expected.setBirthday(new Date(400000));
        expected.setAboutMe("I'm the changed about me");

        testing.updateEmployee(expected);

        session = testSessionFactory.openSession();
        Criteria crt = session.createCriteria(Employee.class);
        crt
                .add(eq("id", expected.getId()));

        Employee actual = (Employee) crt.list().get(0);
        session.close();

        Assert.assertEquals(expected, actual);
    }

    /**
     * utility method to generate a list of 3 employees
     */
    public List<Employee> makeThreeEmployees(){


        String testEmployee1Email = "example1@gmail.com";
        String testEmployee1Password = "xDDDD1234";
        String testEmployee1FirstName = "country";
        String testEmployee1LastName = "bumpkin";
        String testEmployee1AboutMe = "bonjour I'm from the country";
        String testEmployee1JobTitle = "country bounjour giver";
        Date testEmployee1Birthday = new Date(0);

        Employee testEmployee1 = new Employee();

        testEmployee1.setEmail(testEmployee1Email);
        testEmployee1.setPassword(testEmployee1Password);
        testEmployee1.setFirstName(testEmployee1FirstName);
        testEmployee1.setLastName(testEmployee1LastName);
        testEmployee1.setAboutMe(testEmployee1AboutMe);
        testEmployee1.setJobTitle(testEmployee1JobTitle);
        testEmployee1.setBirthday(testEmployee1Birthday);

        String testEmployee2Email = "example2@gmail.com";
        String testEmployee2Password = "xDDDD1234";
        String testEmployee2FirstName = "sea";
        String testEmployee2LastName = "chumpkin";
        Date testEmployee2Birthday = new Date(10000);

        Employee testEmployee2 = new Employee();
        testEmployee2.setEmail(testEmployee2Email);
        testEmployee2.setPassword(testEmployee2Password);
        testEmployee2.setFirstName(testEmployee2FirstName);
        testEmployee2.setLastName(testEmployee2LastName);
        testEmployee2.setBirthday(testEmployee2Birthday);

        String testEmployee3Email = "example3@gmail.com";
        String testEmployee3Password = "OWORDBRUH?";
        String testEmployee3FirstName = "dirty";
        String testEmployee3LastName = "harry";
        String testEmployee3AboutMe = "hello I am dirty harold";
        String testEmployee3JobTitle = "being dirty";

        Employee testEmployee3 = new Employee();
        testEmployee3.setEmail(testEmployee3Email);
        testEmployee3.setPassword(testEmployee3Password);
        testEmployee3.setFirstName(testEmployee3FirstName);
        testEmployee3.setLastName(testEmployee3LastName);
        testEmployee3.setAboutMe(testEmployee3AboutMe);
        testEmployee3.setJobTitle(testEmployee3JobTitle);

        List<Employee> testListEmployees = new ArrayList<>();
        testListEmployees.add(testEmployee1);
        testListEmployees.add(testEmployee2);
        testListEmployees.add(testEmployee3);
        return testListEmployees;
    }

}
