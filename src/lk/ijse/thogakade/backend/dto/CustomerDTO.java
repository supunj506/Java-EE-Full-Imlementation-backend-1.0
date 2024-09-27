/*
 * @author : xCODE
 * Project : GDSE_61
 * Date    : 7/17/2024 (Wednesday)
 * Time    : 3:06 PM
 * For GDSE course of IJSE institute.
 */

package lk.ijse.thogakade.backend.dto;

import java.io.Serializable;

public class CustomerDTO implements Serializable {
    private String id;
    private String name;
    private String address;
    private double salary;

    public CustomerDTO(String id, String name, String address, double salary) {
        this.setId(id);
        this.setName(name);
        this.setAddress(address);
        this.setSalary(salary);
    }

    public CustomerDTO() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
