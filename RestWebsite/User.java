package cbse;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User {
    private String username;
    private String email;
    private String message;

    public User() {}
    public User(String m) { this.message = m; }

    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
}