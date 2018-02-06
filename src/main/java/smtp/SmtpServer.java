package smtp;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class SmtpServer {

    public static final int DEFAULT_SMTP_PORT = 1337;

    private static final Pattern CRLF = Pattern.compile("\r\n");
    private static JTextArea textArea;

    private final List<SmtpMessage> receivedMail;

    private final ServerSocket serverSocket;

    private final Thread workerThread;

    private volatile boolean stopped = false;

    public static SmtpServer start(int port) throws IOException {
        return new SmtpServer(new ServerSocket(port));
    }

    private SmtpServer(ServerSocket serverSocket) {
        this.receivedMail = new ArrayList<>();
        this.serverSocket = serverSocket;
        this.workerThread = new Thread(this::performWork);
        this.workerThread.start();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public List<SmtpMessage> getReceivedEmails() {
        synchronized (receivedMail) {
            return Collections.unmodifiableList(new ArrayList<>(receivedMail));
        }
    }

    public void reset() {
        synchronized (receivedMail) {
            receivedMail.clear();
        }
    }

    public void stop() {
        if (stopped) {
            return;
        }
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
        }

    }

    private void performWork() {
        try {
            while (!stopped) {
                Socket socket = serverSocket.accept();
                try (Scanner input = new Scanner(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)).useDelimiter(CRLF);
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));) {

                    synchronized (receivedMail) {
                        receivedMail.addAll(handleTransaction(out, input));
                    }
                }
                socket.close();
            }
        } catch (Exception e) {
            if (!stopped) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private static List<SmtpMessage> handleTransaction(PrintWriter out, Iterator<String> input) {
        SmtpState smtpState = SmtpState.CONNECT;
        SmtpRequest smtpRequest = new SmtpRequest(SmtpActionType.CONNECT, "", smtpState);

        SmtpResponse smtpResponse = smtpRequest.execute();

        sendResponse(out, smtpResponse);
        smtpState = smtpResponse.getNextState();

        List<SmtpMessage> msgList = new ArrayList<>();
        SmtpMessage msg = new SmtpMessage();

        while (smtpState != SmtpState.CONNECT) {
            String line = input.next();
            textArea.append(line + "\n");
            if (line == null) {
                break;
            }

            SmtpRequest request = SmtpRequest.createRequest(line, smtpState);
            SmtpResponse response = request.execute();
            smtpState = response.getNextState();
            sendResponse(out, response);

            String params = request.params;
            msg.store(response, params);
            if (smtpState == SmtpState.GREET) {
                msg = new SmtpMessage();
            }
            if (smtpState == SmtpState.QUIT) {
                msgList.add(msg);
                System.out.println(msg);
                msg = new SmtpMessage();

            }
        }

        return msgList;
    }

    private static void sendResponse(PrintWriter out, SmtpResponse smtpResponse) {
        if (smtpResponse.getCode() > 0) {
            textArea.append(smtpResponse.toString() + "\n");
            int code = smtpResponse.getCode();
            String message = smtpResponse.getMessage();
            out.print(code + " " + message + "\r\n");
            out.flush();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SMTP-Server");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textArea = new JTextArea();
        textArea.setBackground(Color.decode("#2C001E"));
        textArea.setForeground(Color.WHITE);
        textArea.setEditable(false);
        //frame.add(createMenuPanel(), BorderLayout.PAGE_START);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.setVisible(true);
        frame.add(scrollPane);

        try {
            SmtpServer dumbster = SmtpServer.start(SmtpServer.DEFAULT_SMTP_PORT);
            //  sendMessage(dumbster.getPort(), "sender@here.com", "Hello", "Hello world", "receiver@there.com");
            //  sendMessage(dumbster.getPort(), "sender@here.com", "Hello", "Hello world", "receiver@there.com");
            // Thread.sleep(1000000);
        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException | MessagingExceptione) {
//            e.printStackTrace();
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
