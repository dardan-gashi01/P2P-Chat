import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.BorderLayout;
import javax.swing.*;


public class TCPClient {
    private static final int port = 20111;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Polite messaging");
    JTextField messageField = new JTextField(50);
    JTextField topicField = new JTextField("Topic");
    JTextField subjectField = new JTextField("Subject");
    JTextArea messageArea = new JTextArea(16, 50);
    JButton button = new JButton();
    String topic = "";
    String subject = "";
    public void setTopic(String Topic) {
        topic = Topic;
    }
    public void setSubject(String Subject) {
        subject = Subject;
    }
    public String getTopic(){
        return topic;
    }
    public String getSubject(){
        return subject;
    }
    /**
     * Constructs the client by laying out the GUI and registering a listener with
     * the textfield so that pressing Return in the listener sends the textfield
     * contents to the server. Note however that the textfield is initially NOT
     * editable, and only becomes editable AFTER the client receives the
     * NAMEACCEPTED message from the server.
     */
    public TCPClient() {

        subjectField.setBounds(0, 237, 150, 20);
        topicField.setBounds(0,217, 150, 20);
        messageField.setEditable(false);
        topicField.setEditable(false);
        subjectField.setEditable(false);
        messageArea.setEditable(false);

        frame.getContentPane().add(subjectField);
        frame.getContentPane().add(topicField);
        frame.getContentPane().add(messageField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        // Send on enter then clear to prepare for next message
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(messageField.getText());
                messageField.setText("");
                setTopic(topicField.getText());
                setSubject(subjectField.getText());
                System.out.println("this is being called");
            }
        });
        /*topicField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(subjectField.getText());
                subjectField.setText("");
            }
        });
        subjectField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(subjectField.getText());
                subjectField.setText("");
            }
        });

         */
    }

    private String getName() {
        return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection",
                JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException {
        try {
            String host;
            host = JOptionPane.showInputDialog("what is the IP");
            var socket = new Socket(host, port);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                var line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(getName());
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Chatter - " + line.substring(13));
                    messageField.setEditable(true);
                    topicField.setEditable(true);
                    subjectField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
        TCPClient tcpclient = new TCPClient();
        tcpclient.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tcpclient.frame.setVisible(true);
        tcpclient.run();
    }
}