package com.xx.rpc.api;

/**
 * @Description: TODO
 * Author: XX
 * Date: 2022/4/11  14:47
 **/
public class Person {

    private String firstName;
    private String lastName;

    public Person(){}

    public Person(String firstName,String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
