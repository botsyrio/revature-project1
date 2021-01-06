package com.revature.daos;

import com.revature.models.Employee;
import com.revature.models.Manager;
import com.revature.models.ReimbursementReq;
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

public class ReimbursementReqDaoTest {
    private ReimbursementReqDao testing;
    private static SessionFactory testSessionFactory;
    private List<Employee> testEmployees;
    private List<Manager> testManagers;
    private List<List<ReimbursementReq>> testReimbursementReqsByEmployee;

    /**
     * setup for all the tests - configures hibernate
     */
    @BeforeClass
    public static void initClass(){
        Configuration configuration = new Configuration();
        configuration.configure("hibernateTest.cfg.xml");
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        testSessionFactory = configuration.buildSessionFactory(ssrb.build());

    }
    /**
     * setup for individual tests - recreates the dao object
     */
    @Before
    public void init(){
        testEmployees = new ArrayList<>();
        testManagers=new ArrayList<>();
        testReimbursementReqsByEmployee = new ArrayList<>();

        testing = new ReimbursementReqDao();
        testing.setSessionFactory(testSessionFactory);
    }

    /**
     * deletes all entries from the table that stores ReimbursementReq in the testing database
     */
    @After
    public void teardown(){
        testEmployees = null;
        testManagers = null;
        testReimbursementReqsByEmployee = null;

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        String clearReimbursementReqTable = "delete from ReimbursementReq";
        Query query = session.createQuery(clearReimbursementReqTable);
        query.executeUpdate();

        String clearManagerTable = "delete from Manager";
        query = session.createQuery(clearManagerTable);
        query.executeUpdate();

        String clearEmployeeTable = "delete from Employee";
        query = session.createQuery(clearEmployeeTable);
        query.executeUpdate();

        tx.commit();
        session.close();
    }

    /**
     * tests that insertReimbursementReq inserts a valid request
     */
    @Test
    public void insertReimbursementReqInsertsValidReq(){
        initInsertReimbursementReqInsertsValidReq();
        int testIndex = 0;
        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(testEmployees.get(testIndex));
        tx.commit();
        session.close();

        testing.insertReimbursementReq(testReimbursementReqsByEmployee.get(testIndex).get(0));

        List<ReimbursementReq> myRequests = testing.getAllPending();

        Assert.assertEquals(1, myRequests.size());
        Assert.assertEquals(testReimbursementReqsByEmployee.get(testIndex).get(0), myRequests.get(0));
        //Assert.assertTrue(testReimbursementReqsByEmployee.get(testIndex).get(0).equalsExceptId(myRequests.get(0)));
    }

    /**
     * tests that getAll returns a list of all the reimbursement requests in the database.
     */
    @Test
    public void getAllReturnsAll(){
        List<ReimbursementReq> expected = initGetAllReturnsAll();
        List<ReimbursementReq> actual= testing.getAll();
        Assert.assertEquals(expected, actual);
    }

    /**
     * tests that getAllPendingReturnsAllPending returns all pending requests
     */
    @Test
    public void getAllPendingReturnsAllPending(){
        List<ReimbursementReq> expected  = initGetAllPendingReturnsAllPending();
        List<ReimbursementReq> actual = testing.getAllPending();
        Assert.assertEquals(expected, actual);
    }

    /**
     * tests that getAllPendingReturnsAllResolved returns all resolved requests
     */
    @Test
    public void getAllResolvedReturnsAllResolved(){
        List<ReimbursementReq> expected  = initGetAllResolvedReturnsAllResolved();
        List<ReimbursementReq> actual = testing.getAllResolved();
        Assert.assertEquals(expected, actual);
    }

    /**
     * tests that getAllFromEmployee returns all of an employee's requests
     */
    @Test
    public void getAllFromEmployeeReturnsAllFromEmployee(){
        List<ReimbursementReq> expected = initGetAllFromEmployeeReturnsAllFromEmployee1();
        List<ReimbursementReq> actual = testing.getAllFromEmployee(testEmployees.get(0));
        Assert.assertEquals(expected, actual);
    }

    /**
     * tests that getPendingFromEmployee returns all of an employee's pending requests
     */
    @Test
    public void getPendingFromEmployeeReturnsPendingFromEmployee(){
        List<ReimbursementReq> expected = initGetPendingFromEmployeeReturnsPendingFromEmployee1();
        List<ReimbursementReq> actual = testing.getPendingFromEmployee(testEmployees.get(0));
        Assert.assertEquals(expected, actual);
    }

    /**
     * tests that getResolvedFromEmployee returns all of an employee's resolved requests
     */
    @Test
    public void getResolvedFromEmployeeReturnsResolvedFromEmployee(){
        List<ReimbursementReq> expected = initGetResolvedFromEmployeeReturnsResolvedFromEmployee1();
        List<ReimbursementReq> actual = testing.getResolvedFromEmployee(testEmployees.get(0));
        Assert.assertEquals(expected, actual);
    }

    /**
     * tests that resolveReimbursementReqWorks when passed valid inpuit
     */
    @Test
    public void resolveReimbursementReqWorks(){
        List<ReimbursementReq> reqList = initResolveReimbursementReqWorks();
        ReimbursementReq testReq = reqList.get(0);
        ReimbursementReq expectedReq = testReq;
        String testUpdatedStatus = "approved";
        Manager testManager = testManagers.get(0);

        expectedReq.setManagerResolver(testManager);
        expectedReq.setStatus(testUpdatedStatus);

        testing.resolveReimbursementReq(testReq, testUpdatedStatus, testManager);
        ReimbursementReq actual = testing.getReqById(testReq.getId());
        Assert.assertEquals(expectedReq, actual);
    }

    /**
     * tests that getReqById returns the right request for the given Id
     */
    @Test
    public void getReqByIdReturnsRightRequest(){
        List<ReimbursementReq> expectedList = initGetRequestByIdReturnsRequest();
        ReimbursementReq expected = expectedList.get(0);
        ReimbursementReq actual = testing.getReqById(expected.getId());
        Assert.assertEquals(expected, actual);
    }
    /**
     * adds an employee to testEmployees and an associated Reimbursement to testReimbursementReqs to perform test
     * insertReimbursementReqInsertsValidReq
     */
    public void initInsertReimbursementReqInsertsValidReq(){

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

        double testReqAmount = 100.0;
        Date testReqDate = new Date(10000);
        String testReqStatus = "pending";
        String testReqDetails = "coffee";

        ReimbursementReq testReq = new ReimbursementReq();
        testReq.setEmployeeCreator(testEmployee3);
        testReq.setAmount(testReqAmount);
        testReq.setDateOf(testReqDate);
        testReq.setStatus(testReqStatus);
        testReq.setDetails(testReqDetails);

        testEmployees.add(testEmployee3);
        testReimbursementReqsByEmployee.add(new ArrayList<>());

        testReimbursementReqsByEmployee.get(0).add(testReq);
    }

    /**
     * initialization for getAllReturnsAll
     * @return list of all the requests in the system
     */
    public List<ReimbursementReq> initGetAllReturnsAll(){

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


        ReimbursementReq request1 = new ReimbursementReq();
        request1.setEmployeeCreator(testEmployee1);
        request1.setStatus("approved");
        request1.setAmount(4000.0);
        request1.setDateOf(new Date(1000000000));


        ReimbursementReq request2 = new ReimbursementReq();
        request2.setEmployeeCreator(testEmployee2);
        request2.setStatus("pending");
        request2.setAmount(1000.0);
        request2.setDateOf(new Date(1500000000));


        ReimbursementReq request3 = new ReimbursementReq();
        request3.setEmployeeCreator(testEmployee3);
        request3.setStatus("denied");
        request3.setAmount(4000.0);
        request3.setDateOf(new Date(2000000000));


        ReimbursementReq request4 = new ReimbursementReq();
        request4.setEmployeeCreator(testEmployee2);
        request4.setStatus("approved");
        request4.setAmount(4000.0);
        request4.setDateOf(new Date(2100000000));


        List<ReimbursementReq> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);
        requests.add(request3);
        requests.add(request4);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        session.save(request1);
        session.save(request2);
        session.save(request3);
        session.save(request4);

        tx.commit();
        session.close();
        return requests;
    }

    /**
     * initialization for getAllPendingReturnsAllPending
     * @return the list of all the pending requests
     */
    public List<ReimbursementReq> initGetAllPendingReturnsAllPending(){

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


        ReimbursementReq request1 = new ReimbursementReq();
        request1.setEmployeeCreator(testEmployee1);
        request1.setStatus("approved");
        request1.setAmount(4000.0);
        request1.setDateOf(new Date(1000000000));


        ReimbursementReq request2 = new ReimbursementReq();
        request2.setEmployeeCreator(testEmployee2);
        request2.setStatus("pending");
        request2.setAmount(1000.0);
        request2.setDateOf(new Date(1500000000));


        ReimbursementReq request3 = new ReimbursementReq();
        request3.setEmployeeCreator(testEmployee3);
        request3.setStatus("denied");
        request3.setAmount(4000.0);
        request3.setDateOf(new Date(2000000000));


        ReimbursementReq request4 = new ReimbursementReq();
        request4.setEmployeeCreator(testEmployee2);
        request4.setStatus("approved");
        request4.setAmount(4000.0);
        request4.setDateOf(new Date(2100000000));

        ReimbursementReq request5 = new ReimbursementReq();
        request5.setEmployeeCreator(testEmployee1);
        request5.setStatus("pending");
        request5.setAmount(4000.0);
        request5.setDateOf(new Date(2110000000));


        List<ReimbursementReq> pendingRequests = new ArrayList<>();
        pendingRequests.add(request2);
        pendingRequests.add(request5);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        session.save(request1);
        session.save(request2);
        session.save(request3);
        session.save(request4);
        session.save(request5);

        tx.commit();
        session.close();
        return pendingRequests;
    }

    /**
     * initialization for getAllResolvedReturnsAllResolved
     * @return the list of all the resolved requests
     */
    public List<ReimbursementReq> initGetAllResolvedReturnsAllResolved(){

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


        ReimbursementReq request1 = new ReimbursementReq();
        request1.setEmployeeCreator(testEmployee1);
        request1.setStatus("approved");
        request1.setAmount(4000.0);
        request1.setDateOf(new Date(1000000000));


        ReimbursementReq request2 = new ReimbursementReq();
        request2.setEmployeeCreator(testEmployee2);
        request2.setStatus("pending");
        request2.setAmount(1000.0);
        request2.setDateOf(new Date(1500000000));


        ReimbursementReq request3 = new ReimbursementReq();
        request3.setEmployeeCreator(testEmployee3);
        request3.setStatus("denied");
        request3.setAmount(4000.0);
        request3.setDateOf(new Date(2000000000));


        ReimbursementReq request4 = new ReimbursementReq();
        request4.setEmployeeCreator(testEmployee2);
        request4.setStatus("approved");
        request4.setAmount(4000.0);
        request4.setDateOf(new Date(2100000000));

        ReimbursementReq request5 = new ReimbursementReq();
        request5.setEmployeeCreator(testEmployee1);
        request5.setStatus("pending");
        request5.setAmount(4000.0);
        request5.setDateOf(new Date(2110000000));


        List<ReimbursementReq> resolvedRequests = new ArrayList<>();
        resolvedRequests.add(request1);
        resolvedRequests.add(request3);
        resolvedRequests.add(request4);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        session.save(request1);
        session.save(request2);
        session.save(request3);
        session.save(request4);
        session.save(request5);

        tx.commit();
        session.close();
        return resolvedRequests;
    }

    /**
     * initialization for GetAllFromEmployeeReturnsAllFromEmployee
     * @return the list of all the employee's requests
     */
    public List<ReimbursementReq> initGetAllFromEmployeeReturnsAllFromEmployee1(){

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


        ReimbursementReq request1 = new ReimbursementReq();
        request1.setEmployeeCreator(testEmployee1);
        request1.setStatus("approved");
        request1.setAmount(4000.0);
        request1.setDateOf(new Date(1000000000));


        ReimbursementReq request2 = new ReimbursementReq();
        request2.setEmployeeCreator(testEmployee2);
        request2.setStatus("pending");
        request2.setAmount(1000.0);
        request2.setDateOf(new Date(1500000000));


        ReimbursementReq request3 = new ReimbursementReq();
        request3.setEmployeeCreator(testEmployee3);
        request3.setStatus("denied");
        request3.setAmount(4000.0);
        request3.setDateOf(new Date(2000000000));


        ReimbursementReq request4 = new ReimbursementReq();
        request4.setEmployeeCreator(testEmployee2);
        request4.setStatus("approved");
        request4.setAmount(4000.0);
        request4.setDateOf(new Date(2100000000));

        ReimbursementReq request5 = new ReimbursementReq();
        request5.setEmployeeCreator(testEmployee1);
        request5.setStatus("pending");
        request5.setAmount(4000.0);
        request5.setDateOf(new Date(2110000000));


        List<ReimbursementReq> employee1Reqs = new ArrayList<>();
        employee1Reqs.add(request1);
        employee1Reqs.add(request5);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        session.save(request1);
        session.save(request2);
        session.save(request3);
        session.save(request4);
        session.save(request5);

        tx.commit();

        testEmployees.add(testEmployee1);
        session.close();
        return employee1Reqs;
    }

    /**
     * initialization for GetPendingFromEmployeeReturnsPendingFromEmployee
     * @return the list of all the employee's resolved requests
     */
    public List<ReimbursementReq> initGetPendingFromEmployeeReturnsPendingFromEmployee1(){

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


        ReimbursementReq request1 = new ReimbursementReq();
        request1.setEmployeeCreator(testEmployee1);
        request1.setStatus("approved");
        request1.setAmount(4000.0);
        request1.setDateOf(new Date(1000000000));


        ReimbursementReq request2 = new ReimbursementReq();
        request2.setEmployeeCreator(testEmployee2);
        request2.setStatus("pending");
        request2.setAmount(1000.0);
        request2.setDateOf(new Date(1500000000));


        ReimbursementReq request3 = new ReimbursementReq();
        request3.setEmployeeCreator(testEmployee3);
        request3.setStatus("denied");
        request3.setAmount(4000.0);
        request3.setDateOf(new Date(2000000000));


        ReimbursementReq request4 = new ReimbursementReq();
        request4.setEmployeeCreator(testEmployee2);
        request4.setStatus("approved");
        request4.setAmount(4000.0);
        request4.setDateOf(new Date(2100000000));

        ReimbursementReq request5 = new ReimbursementReq();
        request5.setEmployeeCreator(testEmployee1);
        request5.setStatus("pending");
        request5.setAmount(4000.0);
        request5.setDateOf(new Date(2110000000));


        List<ReimbursementReq> employee1PendingReqs = new ArrayList<>();
        employee1PendingReqs.add(request5);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        session.save(request1);
        session.save(request2);
        session.save(request3);
        session.save(request4);
        session.save(request5);

        tx.commit();

        testEmployees.add(testEmployee1);
        session.close();
        return employee1PendingReqs;
    }

    /**
     * initialization for GetResolvedFromEmployeeReturnsResolvedFromEmployee
     * @return the list of all the employee's resolved requests
     */
    public List<ReimbursementReq> initGetResolvedFromEmployeeReturnsResolvedFromEmployee1(){

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


        ReimbursementReq request1 = new ReimbursementReq();
        request1.setEmployeeCreator(testEmployee1);
        request1.setStatus("approved");
        request1.setAmount(4000.0);
        request1.setDateOf(new Date(1000000000));


        ReimbursementReq request2 = new ReimbursementReq();
        request2.setEmployeeCreator(testEmployee2);
        request2.setStatus("pending");
        request2.setAmount(1000.0);
        request2.setDateOf(new Date(1500000000));


        ReimbursementReq request3 = new ReimbursementReq();
        request3.setEmployeeCreator(testEmployee3);
        request3.setStatus("denied");
        request3.setAmount(4000.0);
        request3.setDateOf(new Date(2000000000));


        ReimbursementReq request4 = new ReimbursementReq();
        request4.setEmployeeCreator(testEmployee2);
        request4.setStatus("approved");
        request4.setAmount(4000.0);
        request4.setDateOf(new Date(2100000000));

        ReimbursementReq request5 = new ReimbursementReq();
        request5.setEmployeeCreator(testEmployee1);
        request5.setStatus("pending");
        request5.setAmount(4000.0);
        request5.setDateOf(new Date(2110000000));


        List<ReimbursementReq> employee1PendingReqs = new ArrayList<>();
        employee1PendingReqs.add(request1);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        session.save(request1);
        session.save(request2);
        session.save(request3);
        session.save(request4);
        session.save(request5);

        tx.commit();

        testEmployees.add(testEmployee1);
        session.close();
        return employee1PendingReqs;
    }
    /**
     * initialization for ResolveReimbursementReqWorks
     * @return the list of all the employee's resolved requests
     */
    public List<ReimbursementReq> initResolveReimbursementReqWorks(){

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

        String testManager1Email = "jhblair@harlancounty.gov";
        String testManager1Password = "thuggery";
        String testManager1FirstName = "JH";
        String testManager1LastName = "Blair";

        Manager testManager1 = new Manager();

        testManager1.setEmail(testManager1Email);
        testManager1.setPassword(testManager1Password);
        testManager1.setFirstName(testManager1FirstName);
        testManager1.setLastName(testManager1LastName);
        testManagers.add(testManager1);


        ReimbursementReq request1 = new ReimbursementReq();
        request1.setEmployeeCreator(testEmployee1);
        request1.setStatus("approved");
        request1.setAmount(4000.0);
        request1.setDateOf(new Date(1000000000));


        ReimbursementReq request2 = new ReimbursementReq();
        request2.setEmployeeCreator(testEmployee2);
        request2.setStatus("pending");
        request2.setAmount(1000.0);
        request2.setDateOf(new Date(1500000000));


        ReimbursementReq request3 = new ReimbursementReq();
        request3.setEmployeeCreator(testEmployee3);
        request3.setStatus("denied");
        request3.setAmount(4000.0);
        request3.setDateOf(new Date(2000000000));


        ReimbursementReq request4 = new ReimbursementReq();
        request4.setEmployeeCreator(testEmployee2);
        request4.setStatus("approved");
        request4.setAmount(4000.0);
        request4.setDateOf(new Date(2100000000));

        ReimbursementReq request5 = new ReimbursementReq();
        request5.setEmployeeCreator(testEmployee1);
        request5.setStatus("pending");
        request5.setAmount(4000.0);
        request5.setDateOf(new Date(2110000000));


        List<ReimbursementReq> employee1PendingReqs = new ArrayList<>();
        employee1PendingReqs.add(request5);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        session.save(request1);
        session.save(request2);
        session.save(request3);
        session.save(request4);
        session.save(request5);

        tx.commit();

        testEmployees.add(testEmployee1);
        session.close();
        return employee1PendingReqs;
    }

    /**
     * initialization for GetResolvedFromEmployeeReturnsResolvedFromEmployee
     * @return the list of all the employee's resolved requests
     */
    public List<ReimbursementReq> initGetRequestByIdReturnsRequest(){

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


        ReimbursementReq request1 = new ReimbursementReq();
        request1.setEmployeeCreator(testEmployee1);
        request1.setStatus("approved");
        request1.setAmount(4000.0);
        request1.setDateOf(new Date(1000000000));


        ReimbursementReq request2 = new ReimbursementReq();
        request2.setEmployeeCreator(testEmployee2);
        request2.setStatus("pending");
        request2.setAmount(1000.0);
        request2.setDateOf(new Date(1500000000));


        ReimbursementReq request3 = new ReimbursementReq();
        request3.setEmployeeCreator(testEmployee3);
        request3.setStatus("denied");
        request3.setAmount(4000.0);
        request3.setDateOf(new Date(2000000000));


        ReimbursementReq request4 = new ReimbursementReq();
        request4.setEmployeeCreator(testEmployee2);
        request4.setStatus("approved");
        request4.setAmount(4000.0);
        request4.setDateOf(new Date(2100000000));

        ReimbursementReq request5 = new ReimbursementReq();
        request5.setEmployeeCreator(testEmployee1);
        request5.setStatus("pending");
        request5.setAmount(4000.0);
        request5.setDateOf(new Date(2110000000));


        List<ReimbursementReq> employee1PendingReqs = new ArrayList<>();
        employee1PendingReqs.add(request1);

        Session session = testSessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        session.save(testEmployee2);
        session.save(testEmployee3);
        session.save(testEmployee1);

        session.save(request1);
        session.save(request2);
        session.save(request3);
        session.save(request4);
        session.save(request5);

        tx.commit();

        testEmployees.add(testEmployee1);
        session.close();
        return employee1PendingReqs;
    }
}
