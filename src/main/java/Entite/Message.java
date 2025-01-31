package Entite;

public class Message {
    private int id;
    private String userName;
    private String content;

    public Message(int id, String userName, String content) {
        this.id = id;
        this.userName = userName;
        this.content = content;
    }

    public Message(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
