package com.revature.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

/**
 * model class for an employee
 */
@Entity
@Table(name = "EMPLOYEES")
public class Employee {
    /**
     * the employee's arbitrarily-assigned unique id -- this is a surrogate key and is not expected to change
     */
    @Id
    @Column(name="id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private @Getter @Setter int id;

    /**
     * the employee's unique email address
     */
    @Column(name="email")
    private @Getter @Setter String email;

    /**
     * the employee's password (>= 8 characters, <=50)
     */
    @Column(name="password")
    private @Getter @Setter String password;

    /**
     * the employee's first name (<= 50 characters)
     */
    @Column(name="first_name")
    private @Getter @Setter String firstName;

    /**
     * the employee's last name (<= 50 characters)
     */
    @Column(name="last_name")
    private @Getter @Setter String lastName;

    /**
     * a blurb the employee can write about themself. must be <1000 characters, may be none
     */
    @Column(name="about_me")
    private @Getter @Setter String aboutMe;

    /**
     * the employee's job title (0<=number of characters<=50)
     */
    @Column(name="job_title")
    private @Getter @Setter String jobTitle;

    /**
     * the employee's birthday
     */
    @Column(name="birthday")
    private @Getter @Setter Date birthday;

    /**
     * overrided definition of equals
     * @param o the object compared to
     * @return whether the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return getId() == employee.getId() &&
                getEmail().equals(employee.getEmail()) &&
                getPassword().equals(employee.getPassword()) &&
                getFirstName().equals(employee.getFirstName()) &&
                getLastName().equals(employee.getLastName()) &&
                Objects.equals(getAboutMe(), employee.getAboutMe()) &&
                Objects.equals(getJobTitle(), employee.getJobTitle()) &&
                ((getBirthday()== null && employee.getBirthday() == null) ||
                        Objects.equals(getBirthday().toString(), employee.getBirthday().toString()));
    }

    /**
     * overrided definition of hashcode
     * @return int representation of the state of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getPassword(), getFirstName(), getLastName(), getAboutMe(), getJobTitle(), getBirthday());
    }

    /**
     * tests equality on everything except the id
     * @param o the object to be compared
     * @return whether the objects are equal
     */
    public boolean equalsExceptId(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getEmail(), employee.getEmail()) &&
                Objects.equals(getPassword(), employee.getPassword()) &&
                Objects.equals(getFirstName(), employee.getFirstName()) &&
                Objects.equals(getLastName(), employee.getLastName()) &&
                Objects.equals(getAboutMe(), employee.getAboutMe()) &&
                Objects.equals(getJobTitle(), employee.getJobTitle()) &&
                ((getBirthday()== null && employee.getBirthday() == null) ||
                        Objects.equals(getBirthday().toString(), employee.getBirthday().toString()));
    }

    /**
     * overrided to string
     * @return string representation of the employee
     */
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
