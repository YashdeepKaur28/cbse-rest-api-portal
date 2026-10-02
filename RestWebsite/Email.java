package cbse;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class Email {
    private static final String FROM = "yashdeepkaur20133@gmail.com";
    private static final String PASS = "bqny jrur ebse sjam";

    public static void sendRegistrationEmail(String to, String user) {
        Properties p = new Properties();
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.put("mail.smtp.socketFactory.port", "465");
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.port", "465");
        try {
            Session s = Session.getInstance(p, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM, PASS);
                }
            });
            MimeMessage m = new MimeMessage(s);
            m.setFrom(new InternetAddress(FROM));
            m.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject("Registration Successful - CBSE System");
            m.setText("Dear " + user + ",\n\nRegistration successful.\n\nAdmin");
            Transport.send(m);
        } catch (Exception e) {
            System.err.println("Email error: " + e.getMessage());
        }
    }
}