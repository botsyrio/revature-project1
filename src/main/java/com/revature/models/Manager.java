package com.revature.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

/**
 * model class for a manager
 */

@Entity
@Table(name = "MANAGERS")
public @Data class Manager {

    /**
     * the manager's arbitrarily-assigned unique id -- this is a surrogate key and is not expected to change
     */
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    /**
     * the manager's unique email address
     */
    @Column(name="EMAIL")
    private String email;

    /**
     * the manager's password (>= 8 characters, <=50)
     */
    @Column(name="PASSWORD")
    private String password;

    /**
     * the manager's first name (<= 50 characters)
     */
    @Column(name="FIRST_NAME")
    private String firstName;

    /**
     * the manager's last name (<= 50 characters)
     */
    @Column(name="LAST_NAME")
    private String lastName;

    /**
     * tests equality on everything except the id
     * @param o the object to be compared
     * @return whether the objects are equal
     */
    public boolean equalsExceptId(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manager manager = (Manager) o;
        return Objects.equals(getEmail(), manager.getEmail()) &&
                Objects.equals(getPassword(), manager.getPassword()) &&
                Objects.equals(getFirstName(), manager.getFirstName()) &&
                Objects.equals(getLastName(), manager.getLastName());
    }

}
