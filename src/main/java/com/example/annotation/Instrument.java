package com.example.annotation;

public class Instrument {
    private int address;
    private String name;

    public Instrument(int address, String name) {
        this.address = address;
        this.name = name;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Instrument{" +
                "address=" + address +
                ", name='" + name + '\'' +
                '}';
    }
}
