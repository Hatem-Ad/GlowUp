package Entite;

public class Participation {
    private int id;
    private int userId;
    private int challengeId;
    private int progression;
    private String status;
   
    public String getStatus() {
        return status;
    }

    public Participation(int id, String status, int progression, int challengeId, int userId) {
        this.id = id;
        this.status = status;
        this.progression = progression;
        this.challengeId = challengeId;
        this.userId = userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Participation() {
    }

    public Participation(int userId , int challengeId, int progression) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.progression = progression;
    }

    public Participation(int challengeId, int userId) {
        this.challengeId = challengeId;
        this.userId = userId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int UserId) {
        this.userId = UserId;
    }

    public int getProgression() {
        return progression;
    }

    public void setProgression(int progression) {
        this.progression = progression;
    }
}
