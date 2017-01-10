package com.kedevelopers.supermarketprices.Model;

/**
 * Created by BEN on 12/22/2016.
 */

public class User {

    public String getName() {
        return name;
    }

    private String id;

    private String name;
    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String password;

    public String getSupermarket() {
        return supermarket;
    }

    private String supermarket;

    public String getFname() {
        return fname;
    }

    public String getId() {
        return id;
    }

    public String getLname() {
        return lname;
    }

    public String getMname() {
        return mname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }


}
