package com.revature.daos;

import com.revature.models.Manager;
import com.revature.models.Manager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ManagerDaoTest {
    private ManagerDao testing;
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
        testing = new ManagerDao();
        testing.setSessionFactory(testSessionFactory);
    }

    /**
     * deletes all entries from the table that stores Manager in the testing database
     */
    @After
    public void teardown(){
        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        String clearManagerTable = "delete from Manager";
        Query query = session.createQuery(clearManagerTable);
        query.executeUpdate();

        tx.commit();
        session.close();
    }


    /**
     * tests that getManagerByEmailPassword returns a list with a single user (the correct one) when given a valid
     * username and password
     */
    @Test
    public void getManagerByEmailPasswordReturnsCorrectUser(){
        List<Manager> initializedManagers = makeThreeManagers();
        Manager testManager1 = initializedManagers.get(0);
        Manager testManager2 = initializedManagers.get(1);
        Manager testManager3 = initializedManagers.get(2);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testManager2);
        session.save(testManager3);
        session.save(testManager1);

        tx.commit();
        session.close();

        List<Manager> selectedUserList = testing.getManagerByEmailPassword(testManager2.getEmail(),
                testManager2.getPassword());

        Assert.assertEquals(1, selectedUserList.size());
        Assert.assertTrue(testManager2.equalsExceptId(selectedUserList.get(0)));
    }

    /**
     * tests that getManagerByEmailPassword returns empty list when given bad email
     */
    @Test
    public void getManagerByEmailPasswordReturnsEmptyBadEmail(){
        List<Manager> initializedManagers = makeThreeManagers();
        Manager testManager1 = initializedManagers.get(0);
        Manager testManager2 = initializedManagers.get(1);
        Manager testManager3 = initializedManagers.get(2);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testManager2);
        session.save(testManager3);
        session.save(testManager1);

        tx.commit();
        session.close();

        String testEmail = "thisEmail@notIn.db";

        List<Manager> selectedUserList = testing.getManagerByEmailPassword(testEmail,
                testManager2.getPassword());

        Assert.assertTrue(selectedUserList.isEmpty());

    }

    /**
     * tests that getManagerByEmailPassword returns empty list when given bad password
     */
    @Test
    public void getManagerByEmailPasswordReturnsEmptyBadPassword(){
        List<Manager> initializedManagers = makeThreeManagers();
        Manager testManager1 = initializedManagers.get(0);
        Manager testManager2 = initializedManagers.get(1);
        Manager testManager3 = initializedManagers.get(2);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testManager2);
        session.save(testManager3);
        session.save(testManager1);

        tx.commit();
        session.close();

        String testPassword = "thisPasswordNotAssoc";

        List<Manager> selectedUserList = testing.getManagerByEmailPassword(testManager2.getEmail(),
                testPassword);

        Assert.assertTrue(selectedUserList.isEmpty());

    }

    /**
     * utility method to generate a list of 3 employees
     */
    public List<Manager> makeThreeManagers(){


        String testManager1Email = "jhblair@harlancounty.gov";
        String testManager1Password = "thuggery";
        String testManager1FirstName = "JH";
        String testManager1LastName = "Blair";

        Manager testManager1 = new Manager();

        testManager1.setEmail(testManager1Email);
        testManager1.setPassword(testManager1Password);
        testManager1.setFirstName(testManager1FirstName);
        testManager1.setLastName(testManager1LastName);

        String testManager2Email = "tonyboyle@umwa.org";
        String testManager2Password = "yablonski";
        String testManager2FirstName = "Tony";
        String testManager2LastName = "Boyle";

        Manager testManager2 = new Manager();
        testManager2.setEmail(testManager2Email);
        testManager2.setPassword(testManager2Password);
        testManager2.setFirstName(testManager2FirstName);
        testManager2.setLastName(testManager2LastName);

        String testManager3Email = "rogersmith@gm.com";
        String testManager3Password = "merryChrimbas";
        String testManager3FirstName = "Roger";
        String testManager3LastName = "Smith";

        Manager testManager3 = new Manager();
        testManager3.setEmail(testManager3Email);
        testManager3.setPassword(testManager3Password);
        testManager3.setFirstName(testManager3FirstName);
        testManager3.setLastName(testManager3LastName);

        List<Manager> testListManagers = new ArrayList<>();
        testListManagers.add(testManager1);
        testListManagers.add(testManager2);
        testListManagers.add(testManager3);
        return testListManagers;
    }
}
