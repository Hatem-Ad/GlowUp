
package Entite;

import javafx.beans.property.*;

public class Challenge {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty description;
    private final IntegerProperty categoryId;

    private String categoryName;

    // Constructeur avec tous les champs
    public Challenge(int id, String name, String description, int categoryId, String categoryName) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.categoryName = categoryName;
    }

    // Constructeur avec tous les champs
    public Challenge(int id, String name, String description, int categoryId) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.categoryId = new SimpleIntegerProperty(categoryId);
    }



    // Constructeur sans ID (pour l'ajout de nouveaux challenges)
    public Challenge(String name, String description, int categoryId) {
        this.id = new SimpleIntegerProperty(0); // Valeur par défaut
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.categoryId = new SimpleIntegerProperty(categoryId);
    }

    // Getters pour les propriétés
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public IntegerProperty categoryIdProperty() {
        return categoryId;
    }

    // Getters classiques (si nécessaire)
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getDescription() {
        return description.get();
    }

    public int getCategoryId() {
        return categoryId.get();
    }

    // Setters classiques (si nécessaire)
    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
    }

    public String getCategoryName() {
        return categoryName;
    }
}

