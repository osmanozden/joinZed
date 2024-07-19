package com.joinzad.interviewcase.model;

import jakarta.persistence.Entity;

@Entity
public class UserModel extends BaseModel{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
