package com.revature.daos;

import com.revature.models.Employee;
import com.revature.models.Manager;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ge;

/**
 * class used to perform interactions with the Managers table in the database
 */
public class ManagerDao {
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
     * constructor for ManagerDao. initializes sessionFactory using the configuration info in hibernate.cfg.xml
     */
    public ManagerDao(){
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        this.sessionFactory = configuration.buildSessionFactory(ssrb.build());
    }


    /**
     * selects a list of managers with the specified email and password. should have 1 element if there is, none if
     * there are none.
     * @param email the email of the desired manager
     * @param password the password of the desired manager
     * @return a list with one element if there exists a manager with the specified email and password, with no elements
     * if no such manager exists.
     */
    public List<Manager> getManagerByEmailPassword(String email, String password){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(Manager.class);
        crt
                .add(eq("email", email))
                .add(eq("password", password));

        List<Manager> managers = crt.list();
        session.close();
        return managers;

    }
}
