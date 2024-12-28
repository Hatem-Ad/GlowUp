package Entite;

public class Categorie {

    private int id;
    private String name;
    private String description;

    // Constructeur avec ID (pour récupérer les catégories existantes)
    public Categorie(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Constructeur sans ID (pour créer de nouvelles catégories)
    public Categorie(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public Categorie(int id) {
        this.id = id;
    }

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

    // Méthode toString() pour afficher les informations de la catégorie
    @Override
    public String toString() {
        return "Categorie{id=" + id + ", name='" + name + "', description='" + description + "'}";
    }
}