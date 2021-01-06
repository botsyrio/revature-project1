package com.revature.daos;

import com.revature.models.Employee;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ge;

/**
 * class used to perform interactions with the Employees table in the database
 */
public class EmployeeDao {
    /**
     * the session factory used by the class to do everything.
     */
    private SessionFactory sessionFactory;

    /**
     * setter for sessionFactory
     * @param sessionFactory the new SessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * constructor for EmployeeDao. initializes sessionFactory using the configuration info in hibernate.cfg.xml
     */
    public EmployeeDao(){
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        this.sessionFactory = configuration.buildSessionFactory(ssrb.build());
    }

    /**
     * inserts an Employee into the database
     * @param emp the Employee to be inserted
     */
    public void insertEmployee(Employee emp){
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(emp);
        tx.commit();
        session.close();
    }

    /**
     * getter for all employees in the employee table
     * @return a list of all employees in the table, or an empty list if the table is empty
     */
    public List<Employee> getAllEmployees(){
        Session session = this.sessionFactory.openSession();
        String hql = "from Employee as emp order by emp.lastName asc";
        Query query = session.createQuery(hql);

        List<Employee> allEmployees = query.list();
        session.close();
        return allEmployees;
    }

    /**
     * retrieves an employee from the database with the specified id
     * @param id the id of the employee we want
     * @return the employee with the given id
     */
    public Employee getEmployeeById(int id){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(Employee.class);
        crt
                .add(eq("id", id));

        List<Employee> employees = crt.list();
        session.close();
        return employees.get(0);
    }

    /**
     * returns the employee with the specified email
     * @param email the email to search by
     * @return the emplyoee with the given email
     */
    public Employee getEmployeeByEmail(String email){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(Employee.class);
        crt
                .add(eq("email", email));

        List<Employee> employees = crt.list();
        session.close();
        if(!employees.isEmpty())
            return employees.get(0);
        return null;
    }

    /**
     * gets a list of employees filtered by a specific email-password combination. should have either 1 or no elements
     *
     * @param email the email to filter by
     * @param password the paswsword to filter by
     * @return a list with the single employee with the correct email password combination OR an empty list if they don't
     * match
     */
    public List<Employee> getEmployeeByEmailPassword(String email, String password){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(Employee.class);
        crt
                .add(eq("email", email))
                .add(eq("password", password));

        List<Employee> employees = crt.list();
        session.close();
        return employees;
    }

    /**
     * updates the given employee, assuming an employee with the matching primary key exists in the database.
     * @param emp
     */
    public void updateEmployee(Employee emp){
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.update(emp);
        tx.commit();
        session.close();
    }
}
