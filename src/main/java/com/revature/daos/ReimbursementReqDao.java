package com.revature.daos;

import com.revature.models.Employee;
import com.revature.models.Manager;
import com.revature.models.ReimbursementReq;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.io.Serializable;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;

/**
 * class used to make queries between the java code and the reimbursement_reqs table in the database
 */
public class ReimbursementReqDao {

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
    public ReimbursementReqDao(){
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        this.sessionFactory = configuration.buildSessionFactory(ssrb.build());
    }

    /**
     * inserts a reimbursement request into the database
     * @param req the request to be added to the database
     * @throws HibernateException should be thrown if the request violates some constraint of the database (i.e. has an
     * amount <=0)
     */
    public void insertReimbursementReq(ReimbursementReq req) throws HibernateException {
        Session session = sessionFactory.openSession();

        Transaction tx = session.beginTransaction();
        session.save(req);
        tx.commit();

        session.close();
    }

    /**
     * returns a list of all the reimbursement requests in the database
     * @return the list of all the reimbursement requests in the database
     */
    public List<ReimbursementReq> getAll(){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(ReimbursementReq.class);
        crt
                .addOrder(Order.asc("dateOf"));

        List<ReimbursementReq> allReqs = crt.list();
        session.close();
        return allReqs;
    }

    /**
     * gets a list of all pending requests
     * @return a list of all Reimbursement Reqs with status="pending", or an empty list if none exist
     */
    public List<ReimbursementReq> getAllPending(){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(ReimbursementReq.class);
        crt
            .add(eq("status","pending" ))
            .addOrder(Order.asc("dateOf"));

        List<ReimbursementReq> allPendingReqs = crt.list();
        session.close();
        return allPendingReqs;
    }

    /**
     * gets a list of all resolved requests
     * @return a list of all Reimbursement Reqs with status = "approved" or status = "denied"
     */
    public List<ReimbursementReq> getAllResolved(){
        Session session = this.sessionFactory.openSession();

        Criteria crt = session.createCriteria(ReimbursementReq.class);
        Criterion isApproved = Restrictions.eq("status", "approved");
        Criterion isDenied = Restrictions.eq("status", "denied");
        LogicalExpression orExp = Restrictions.or(isApproved, isDenied);
        crt.add(orExp);
        crt
                .addOrder(Order.asc("dateOf"));

        List<ReimbursementReq> allResolvedReqs = crt.list();
        session.close();
        return allResolvedReqs;

    }

    /**
     * returns all requests from a specified employee
     * @param employee the employee whose requests we're fetching
     * @return a list of all the reimbursement requests from the employee
     */
    public List<ReimbursementReq> getAllFromEmployee(Employee employee){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(ReimbursementReq.class);
        crt
                .add(eq("employeeCreator", employee))
                .addOrder(Order.asc("dateOf"));

        List<ReimbursementReq> allReqsFromEmployee = crt.list();
        session.close();
        return allReqsFromEmployee;
    }
    /**
     * get all pending requests made by the specified employee
     * @param employee the employee whose requests we're trying to look up
     * @return the list of all pending ReimbursementReqs created by the specified employee or an empty list if none such
     * Reimbursement reqs exist
     */
    public List<ReimbursementReq> getPendingFromEmployee(Employee employee){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(ReimbursementReq.class);
        crt
            .add(eq("status","pending" ))
            .add(eq("employeeCreator", employee))
                .addOrder(Order.asc("dateOf"));

        List<ReimbursementReq> allPendingReqsFromEmployee = crt.list();
        session.close();
        return allPendingReqsFromEmployee;
    }

    /**
     * get a list of all resolved requests made by a specific employee
     * @param employee the employee whose requests we're trying to lookup
     * @return the list of all approved or denied ReimbursementReqs created by the specified employee
     */
    public List<ReimbursementReq> getResolvedFromEmployee(Employee employee){
        Session session = this.sessionFactory.openSession();
        Criteria crt = session.createCriteria(ReimbursementReq.class);
        Criterion isApproved = Restrictions.eq("status", "approved");
        Criterion isDenied = Restrictions.eq("status", "denied");
        LogicalExpression orExp = Restrictions.or(isApproved, isDenied);

        crt
            .add(orExp)
            .add(eq("employeeCreator", employee))
                .addOrder(Order.asc("dateOf"));

        List<ReimbursementReq> allResolvedReqsFromEmployee = crt.list();
        session.close();
        return allResolvedReqsFromEmployee;

    }

    /**
     * takes the specified request, sets its status to approved or denied, sets the manager who resolved it, and updates
     * it in the database
     * @param req the ReimbursementReq to be resolved
     * @param updatedStatus the new status of the req (either approved or denied)
     * @param resolver the manager who resolved the request
     */
    public void resolveReimbursementReq(ReimbursementReq req, String updatedStatus, Manager resolver){
        if(updatedStatus.equals("approved") || updatedStatus.equals("denied")) {
            req.setStatus(updatedStatus);
            req.setManagerResolver(resolver);
        }

        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.update(req);
        tx.commit();
        session.close();
    }

    /**
     * returns the individual ReimbursementReq with the given id
     * @param id the id of the request to be retrieved
     * @return the desired request
     */
    public ReimbursementReq getReqById(Serializable id) {
        Session session = this.sessionFactory.openSession();
        ReimbursementReq req = (ReimbursementReq)session.get(ReimbursementReq.class, id);
        session.close();

        return req;
    }

}
