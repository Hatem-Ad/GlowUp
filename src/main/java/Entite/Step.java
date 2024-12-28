package Entite;

public class Step {
    private int id;
    private int challengeId; // Vérifiez que ce champ existe
    private String name;
    private String description;
    private int stepOrder;

    // Constructeur
    public Step(int id, int challengeId, String name, String description, int stepOrder) {
        this.id = id;
        this.challengeId = challengeId;
        this.name = name;
        this.description = description;
        this.stepOrder = stepOrder;
    }

    // Constructeur
    public Step(int challengeId, String name, String description) {

        this.challengeId = challengeId;
        this.name = name;
        this.description = description;

    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChallengeId() { // Assurez-vous que ce getter existe
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
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

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }
    // Méthode toString pour une représentation plus lisible de l'objet
    @Override
    public String toString() {
        return "Étape " + name + " : " + description;
    }
}
