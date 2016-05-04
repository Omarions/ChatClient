
package chatclient;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

//main class of client applicaiton
public class ChatClient extends JFrame implements ActionListener {

    //components of GUI
    private JLabel nameLabel;
    private JTextField nameBox;
    private JLabel hostnameLabel;
    private JTextField hostnameBox;
    private JLabel portLabel;
    private JTextField portBox;
    private JButton connect;
    private JLabel participantsLabel;
    private JTextArea participants;
    private JLabel messageBoxLabel;
    private JTextArea messagesBox;
    private JLabel typeMsgBoxLabel;
    private JTextField typeMessageBox;
    private JButton send;
    //socket of the connection
    private Socket socket;
    //streams of the connection
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    //constructor
    public ChatClient() {
        loadGUI();                                          //load the GUI of the application
        //write termination message when closing window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent winEvt) {
                try {
                    //write termination message to server
                    dataOutputStream.writeUTF("EXIT:" + nameBox.getText() + " has left the chat room\n");
                    dataOutputStream.writeUTF(nameBox.getText());
                } catch (IOException ex) {
                    //do nothing
                } catch (NullPointerException ex) {
                    //do nothing if close with no server (can't write to server)
                }
            }
        });
    }

    public void loadGUI() {
        setSize(600, 500);                          // size (width,height)
        setDefaultCloseOperation(EXIT_ON_CLOSE);    // exit on 'X' click
        setLayout(new BorderLayout(15, 5));         // border layout as north-outh-west-east-center
        //call methods to add components
        initMessageBoxPanel();
        initConnectPanel();
        initParticipantsPanel();
        initTextingPanel();
        setVisible(true);
    }

    /**
     * Create and add connection components to frame at top <br>
     * such as port, username and host name with button connect
     */
    public void initConnectPanel() {
        JPanel connectionPanel = new JPanel();  // create container panel
        connectionPanel.setLayout(null);        // container layout to be absolute

        nameLabel = new JLabel("Name:");        //create the label
        nameLabel.setSize(120, 30);             //set the size of label
        nameLabel.setLocation(10, 10);          //set the location in the panel

        nameBox = new JTextField("");           //create the name box
        nameBox.setSize(310, 30);               //set the size.
        nameBox.setLocation(80, 10);            //set the location in the panel

        hostnameLabel = new JLabel("Hostname:");    //create the hostname label
        hostnameLabel.setSize(100, 30);         //set the size
        hostnameLabel.setLocation(10, 45);      //set the location in the panel

        hostnameBox = new JTextField("localhost");  //create the box of hostname and set its default value.
        hostnameBox.setSize(190, 30);
        hostnameBox.setLocation(80, 45);

        portLabel = new JLabel("Port:");        //create the label
        portLabel.setSize(40, 30);              //set the size
        portLabel.setLocation(290, 45);         //set the location in the panel

        portBox = new JTextField("8000");       //create the box of hostname and set its default value.
        portBox.setSize(60, 30);                //set the size
        portBox.setLocation(330, 45);           //set the location in the panel

        connect = new JButton("Connect");               //create the button
        connect.setSize(100, 70);                      //set the size
        connect.setLocation(460, 10);                   //set the location in the panel
        connect.addActionListener(this);                //add the action when clicked

        //add components to the panel.
        connectionPanel.add(nameLabel);
        connectionPanel.add(nameBox);
        connectionPanel.add(hostnameLabel);
        connectionPanel.add(hostnameBox);
        connectionPanel.add(portLabel);
        connectionPanel.add(portBox);
        connectionPanel.add(connect);

        //set jpanel preferences
        connectionPanel.setPreferredSize(new Dimension(500, 80));   //set the size of panel

        add(connectionPanel, BorderLayout.NORTH);                   //add the panel to the frame.
    }

    //init the messages box panel.
    public void initMessageBoxPanel() {
        JPanel messageBoxPanel = new JPanel();                      //create panel
        messageBoxPanel.setLayout(new BorderLayout());              //set the layout to borderlayout
        messageBoxPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20)); //set the margin of panel.

        messageBoxLabel = new JLabel("Messages");                   //create the label

        messagesBox = new JTextArea(20, 20);                              //create the messages box
        messagesBox.setEditable(false);                             //disable editing.
        JScrollPane scrol = new JScrollPane(messagesBox);           //create scroll to the messages box

        //add components to the panel
        messageBoxPanel.add(messageBoxLabel, BorderLayout.NORTH);
        messageBoxPanel.add(scrol, BorderLayout.CENTER);

        add(messageBoxPanel, BorderLayout.CENTER);                  //add the panel to the frame.
    }

    //init the participants box panel
    public void initParticipantsPanel() {
        JPanel participantsPanel = new JPanel();                    //create the panel
        participantsPanel.setLayout(new BorderLayout());            //set its layout to borderlayout                
        participantsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));  //set the margins.

        participantsLabel = new JLabel("Participants");             //create the label

        participants = new JTextArea(15, 15);                        //create the participants box.
        participants.setEditable(false);                            //disable editing
        JScrollPane scrol = new JScrollPane(participants);          //create scroll to it.

        //add components to the panel
        participantsPanel.add(participantsLabel, BorderLayout.NORTH);
        participantsPanel.add(scrol, BorderLayout.CENTER);

        add(participantsPanel, BorderLayout.WEST);                  //add the panel to the frame.
    }

    //init the texting panel
    public void initTextingPanel() {
        JPanel textingPanel = new JPanel();                         //create the panel
        textingPanel.setLayout(new GridBagLayout());                //set the layout.

        typeMsgBoxLabel = new JLabel("Type message:");              //create the label

        typeMessageBox = new JTextField(30);                        //create the message box
        typeMessageBox.setSize(150, 50);                             //set its size
        typeMessageBox.setLocation(70, 10);                         //set its location
        typeMessageBox.setEnabled(false);                           //disable it

        send = new JButton("Send");                                 //create the button
        send.setEnabled(false);                                     //disable it
        send.addActionListener(this);                               //add actionlistner to click event.

        //add components to the panel
        textingPanel.add(typeMsgBoxLabel);
        textingPanel.add(typeMessageBox);
        textingPanel.add(send);

        add(textingPanel, BorderLayout.SOUTH);                      //add the panel to the frame

    }

    //validate text box of name.
    private boolean validateName(JTextField nameBox) {
        String name = nameBox.getText();            //get the name
        if (name.length() == 0 || name == null) {       //check for empty name box
            return false;
        }
        return true;
    }

    //validate text box of hostname.
    private boolean validateHostname(JTextField hostnameBox) {
        String hostName = hostnameBox.getText();            //get the hostname
        if (hostName.length() == 0 || hostName == null) {       //check for empty host name box
            return false;
        }
        return true;
    }

    //validate text box of port.
    private boolean validatePort(JTextField portBox) {
        String port = portBox.getText();            //get the hostname
        if (port.length() == 0 || port == null) {       //check for empty host name box
            return false;
        } else {
            try {
                Integer.parseInt(port);
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            JButton clickedButton = (JButton) event.getSource();    //get the source of event.
            if (clickedButton == connect) {                         //check if it's connect button

                if (!validateName(nameBox)) {       //check name box
                    JOptionPane.showMessageDialog(this, "insert your name", "error happen...", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!validateHostname(hostnameBox)) {           //check  hostname box
                    JOptionPane.showMessageDialog(this, "Insert the IP of the server.", "error happen...", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!validatePort(portBox)) {                     //check the port box
                    JOptionPane.showMessageDialog(this, "must insert the port and should be numbers only", "error happen...", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String name = nameBox.getText();                    //get the name
                String hostName = hostnameBox.getText();            //get the hostname
                String portS = portBox.getText();                   //get the port
                int port = Integer.parseInt(portS);                 //convert port string to integer.

                try {
                    socket = new Socket(hostName, port);            //create the socket with the port

                    //get the input stream from the socket, to read from the server
                    dataInputStream = new DataInputStream(socket.getInputStream());

                    //get the output stream from the socket, to send out to the server
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeUTF(name);                //send the name to the server.

                    // Start a new thread for receiving messages
                    Thread thread = new Thread(() -> run());
                    thread.start();                                 //start the thread

                    //disable the connection panel components.
                    hostnameBox.setEnabled(false);
                    nameBox.setEnabled(false);
                    portBox.setEnabled(false);
                    connect.setEnabled(false);

                    //add the name to the participants.
                    participants.append(name + "\n");

                    //enable texting panel components
                    typeMessageBox.setEnabled(true);
                    typeMessageBox.setEditable(true);
                    send.setEnabled(true);

                } catch (ConnectException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    String msg = nameBox.getText() + ": " + typeMessageBox.getText();    //create the message
                    dataOutputStream.writeUTF(msg);          //send the message to the server

                    typeMessageBox.setText("");             //clear it after sending.
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //the run method of the thread (what we do when thread starts)
    public void run() {
        try {
            //inifint loop.
            while (true) {
                String text = dataInputStream.readUTF();        //read from the server
                String[] tokens = text.split(";");
                String command = tokens[0];            //split it.
                if (command.equals("NAMES")) {                  //check if the message is the names of clients.
                    participants.setText("");                   //clear participants box
                    String[] names = tokens[1].split(" ");      //split names
                    for (String name : names) {                 //loop on names
                        participants.append(name + "\n");       //add each client's name to the participants box
                    }
                } else {                                        //in normal message
                    messagesBox.append(text.substring(text.indexOf(";") + 1) + '\n');   //add it to the messages box.
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            //enable again the connection panel components
            nameBox.setEnabled(true);
            hostnameBox.setEnabled(true);
            portBox.setEnabled(true);
            connect.setEnabled(true);
            //disable texting panel components
            typeMessageBox.setEnabled(false);
            typeMessageBox.setEditable(false);
            send.setEnabled(false);
            
            //clear participants and messages boxes
            participants.setText("");
            messagesBox.setText("");
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}
