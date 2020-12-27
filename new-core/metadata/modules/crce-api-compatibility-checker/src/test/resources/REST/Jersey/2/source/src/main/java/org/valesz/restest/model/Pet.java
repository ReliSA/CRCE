package org.valesz.restest.model;

public class Pet {

    private int id;
    private String name;
    private double weight;

    public Pet() {
        id = 1;
        name = "My Pet";
        weight = 5.2;
    }

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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
