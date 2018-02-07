package by.zhuk.smtpserver.smtp;

import by.zhuk.smtpserver.command.Command;
import by.zhuk.smtpserver.command.CommandFactory;
import by.zhuk.smtpserver.command.ConnectCommand;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class SmtpServer {

    public static final int DEFAULT_SMTP_PORT = 25;

    private static final Pattern CRLF = Pattern.compile("\r\n");
    private static JTextArea textArea;

    private final ServerSocket serverSocket;

    private final Thread workerThread;

    private volatile boolean stopped = false;

    public static SmtpServer start(int port) throws IOException {
        return new SmtpServer(new ServerSocket(port));
    }

    private SmtpServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.workerThread = new Thread(this::performWork);
        this.workerThread.start();
    }


    private void performWork() {
        try {
            while (!stopped) {
                Socket socket = serverSocket.accept();
                try (Scanner input = new Scanner(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)).useDelimiter(CRLF);
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));) {
                    handleTransaction(out, input);

                }
                socket.close();
            }
        } catch (Exception e) {
            if (!stopped) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void handleTransaction(PrintWriter out, Iterator<String> input) {
        SmtpState smtpState = SmtpState.CONNECT;
        SmtpMail msg = new SmtpMail();

        Command firstCommand = new ConnectCommand(smtpState);
        SmtpResponse firstResponse = firstCommand.execute();
        sendResponse(out, firstResponse);
        smtpState = firstResponse.getNextState();


        while (smtpState != SmtpState.CONNECT) {
            String line = input.next();
            textArea.append(line + "\n");
            if (line == null) {
                break;
            }

            Command command = CommandFactory.findCommand(line, smtpState, msg);
            SmtpResponse response = command.execute();
            smtpState = response.getNextState();
            sendResponse(out, response);


        }

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
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);
        frame.setVisible(true);
        textArea.append("SERVER START");
        try {
            SmtpServer.start(SmtpServer.DEFAULT_SMTP_PORT);
//                sendMessage(dumbster.getPort(), "san91130324@gmail.com", "Hello", "Hello world", "alexzhuck@gmail.com");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static void sendMessage(int port, String from, String subject, String body, String to) throws MessagingException {
//        Properties mailProps = getMailProperties(port);
//        Session session = Session.getInstance(mailProps, null);
//
//
//        MimeMessage msg = createMessage(session, from, to, subject, body);
//        Transport.send(msg);
//    }
//
//    private static Properties getMailProperties(int port) {
//        Properties mailProps = new Properties();
//        mailProps.setProperty("mail.smtp.host", "localhost");
//        mailProps.setProperty("mail.smtp.port", "" + port);
//        mailProps.setProperty("mail.smtp.sendpartial", "true");
//        return mailProps;
//    }
//
//    private static MimeMessage createMessage(
//            Session session, String from, String to, String subject, String body) throws MessagingException {
//        MimeMessage msg = new MimeMessage(session);
//        msg.setFrom(new InternetAddress(from));
//        msg.setSubject(subject);
//        msg.setSentDate(new Date());
//        msg.setText(body);
//        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//        return msg;
//    }

}
