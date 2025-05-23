package com.example.financetracker.domain;

import java.util.UUID;

public class Category {
    private UUID id;
    private String name;
    private String type; // "income" or "expense"
    private String icon; // Name or path to an icon representation

    public Category(String name, String type, String icon) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name=\'" + name + "\\'" +
                ", type=\'" + type + "\\'" +
                ", icon=\'" + icon + "\\'" +
                '}';
    }
}

