package Entite;

import javafx.scene.control.TextField;

public class Evenement {
    private int id;
    private String name;
    private String description;


    // Constructeurs
    public Evenement() {
    }

    public Evenement(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;

    }
    // Constructeur sans ID (pour créer de nouvelles catégories)
    public Evenement(String name, String description) {
        this.name = name;
        this.description = description;}



    // Getters et Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // Méthode toString pour afficher les détails d'un événement
    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
