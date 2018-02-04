import smtp.SmtpServer;


import java.io.IOException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        try {
            SmtpServer dumbster = SmtpServer.start(SmtpServer.DEFAULT_SMTP_PORT);
            sendMessage(dumbster.getPort(), "sender@here.com", "Hello", "Hello world", "receiver@there.com");
            sendMessage(dumbster.getPort(), "sender@here.com", "Hello", "Hello world", "receiver@there.com");
            Thread.sleep(1000000);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(int port, String from, String subject, String body, String to) throws MessagingException {
        Properties mailProps = getMailProperties(port);
        Session session = Session.getInstance(mailProps, null);


        MimeMessage msg = createMessage(session, from, to, subject, body);
        Transport.send(msg);
    }

    private static Properties getMailProperties(int port) {
        Properties mailProps = new Properties();
        mailProps.setProperty("mail.smtp.host", "localhost");
        mailProps.setProperty("mail.smtp.port", "" + port);
        mailProps.setProperty("mail.smtp.sendpartial", "true");
        return mailProps;
    }

    private static MimeMessage createMessage(
            Session session, String from, String to, String subject, String body) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(body);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        return msg;
    }
}