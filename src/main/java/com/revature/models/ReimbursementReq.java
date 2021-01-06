package com.revature.models;

import lombok.Data;

import javax.persistence.*;

import java.sql.Date;
import java.util.Objects;

/**
 * model for a reimbursement request
 */
@Entity
@Table(name = "REIMBURSEMENT_REQS")
public @Data class ReimbursementReq {
    /**
     * the reimbursement req's arbitrarily-assigned unique id -- this is a surrogate key and is not expected to change
     */
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    /**
     * the id of the employee who made the reimbursement request
     */
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="EMPLOYEE_CREATOR_ID", referencedColumnName="ID", columnDefinition="INT")
    private Employee employeeCreator;

    /**
     * the name of the manager who resolved the reimbursement request
     */
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="MANAGER_RESOLVER_ID", referencedColumnName="ID", columnDefinition="INT")
    private Manager managerResolver;

    /**
     * the amount of the reimbursement
     */
    @Column(name="AMOUNT")
    private double amount;

    /**
     * the date the expense occurred
     */
    @Column(name="DATEOF")
    private Date dateOf;

    /**
     * the status of the request (pending, approved or denied)
     */
    @Column(name="STATUS")
    private String status;

    /**
     * a short blurb explaining what the expense was for
     */
    @Column(name="DETAILS")
    private String details;

    /**
     * overrided definition of equals
     * @param o the object compared to
     * @return whether the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReimbursementReq that = (ReimbursementReq) o;
        return getId() == that.getId() &&
                Double.compare(that.getAmount(), getAmount()) == 0 &&
                Objects.equals(getEmployeeCreator(), that.getEmployeeCreator()) &&
                Objects.equals(getManagerResolver(), that.getManagerResolver()) &&
                getDateOf().toString().equals(that.getDateOf().toString()) &&
                getStatus().equals(that.getStatus()) &&
                Objects.equals(getDetails(), that.getDetails());
    }
    /**
     * overrided definition of hashcode
     * @return int representation of the state of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmployeeCreator(), getManagerResolver(), getAmount(), getDateOf(), getStatus(), getDetails());
    }

    /**
     * tests equality on everything except the id
     * @param o the object to be compared
     * @return whether the objects are equal
     */
    public boolean equalsExceptId(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReimbursementReq that = (ReimbursementReq) o;
        return Double.compare(that.getAmount(), getAmount()) == 0 &&
                getEmployeeCreator().equals(that.getEmployeeCreator()) &&
                Objects.equals(getManagerResolver(), that.getManagerResolver()) &&
                getDateOf().toString().equals(that.getDateOf().toString()) &&
                getStatus().equals(that.getStatus()) &&
                Objects.equals(getDetails(), that.getDetails());
    }



}
