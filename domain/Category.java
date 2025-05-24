package domain;

import java.util.UUID;

public class Category {
    private final UUID id;
    private String name;
    private String type; // "income" or "expense"
    private String icon; // Name or path to an icon representation

    public Category(String name, String type, String icon) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getIcon() {
        return icon;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        // Add validation if necessary
        this.type = type;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        // Corrected toString with proper escaping for single quotes
        return "Category{" +
                "id=" + id +
                ", name=\'" + name + "\'" +
                ", type=\'" + type + "\'" +
                ", icon=\'" + icon + "\'" +
                '}';
    }
}