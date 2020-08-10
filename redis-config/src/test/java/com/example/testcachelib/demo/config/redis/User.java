package com.example.testcachelib.demo.config.redis;

public class User {
    private int id;
    private String name;

//    public User() {
//
//    }

//    public User(int i, String name) {
//        this.id = i;
//        this.name = name;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("id=%s , name = %s", id, name);
    }
}
