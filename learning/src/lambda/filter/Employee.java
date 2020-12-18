package lambda.filter;

import java.util.Objects;

public class Employee {

    private String name;
    private int age;
    private double slary;
    private Status status;

    public Employee() {
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getSlary() {
        return slary;
    }

    public void setSlary(double slary) {
        this.slary = slary;
    }

    public Employee(String name, int age, double slary) {
        this.name = name;
        this.age = age;
        this.slary = slary;
    }

    public Employee(int age){
        this.age = age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, slary);
    }

    public Employee(String name, int age, double slary, Status status) {
        this.name = name;
        this.age = age;
        this.slary = slary;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return age == employee.age &&
                Double.compare(employee.slary, slary) == 0 &&
                name.equals(employee.name) &&
                status == employee.status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status{
        FREE,
        BUSY,
        VOCATION;
    }

}
