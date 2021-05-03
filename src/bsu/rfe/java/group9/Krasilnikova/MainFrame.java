package bsu.rfe.java.group9.Krasilnikova;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements MessageListener {
    private static final String FRAME_TITLE = "Клиент мгновенных сообщений";
    private static final int FRAME_MINIMUM_WIDTH = 500;
    private static final int FRAME_MINIMUM_HEIGHT = 500;
    private static final int FROM_FIELD_DEFAULT_COLUMNS = 10;
    private static final int TO_FIELD_DEFAULT_COLUMNS = 20;
    private static final int INCOMING_AREA_DEFAULT_ROWS = 10;
    private static final int OUTGOING_AREA_DEFAULT_ROWS = 5;
    private static final int SMALL_GAP = 5;
    private static final int MEDIUM_GAP = 10;
    private static final int LARGE_GAP = 15;
    private static final int SERVER_PORT = 4567;
    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;
    private final JTextArea textAreaIncoming;
    private final JTextArea textAreaOutgoing;
    private InstantMessager instantMessager;
    private ArrayList<Peer> UserInfo = new ArrayList<>(10);
    private DialogFrame dialogFrame;
    private boolean check = false;

    public MainFrame() {
        super(FRAME_TITLE);
        setMinimumSize(new Dimension(FRAME_MINIMUM_WIDTH, FRAME_MINIMUM_HEIGHT));
        final Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - getWidth()) / 2, (kit.getScreenSize().height - getHeight()) / 2);
        // Текстовая область для отображения полученных сообщений
        textAreaIncoming = new JTextArea(INCOMING_AREA_DEFAULT_ROWS, 0);
        textAreaIncoming.setEditable(false);
        final JScrollPane scrollPaneIncoming = new JScrollPane(textAreaIncoming);
        final JLabel labelFrom = new JLabel("Подпись");
        final JLabel labelTo = new JLabel("Получатель");
        textFieldFrom = new JTextField(FROM_FIELD_DEFAULT_COLUMNS);
        textFieldTo = new JTextField(TO_FIELD_DEFAULT_COLUMNS);
        // Текстовая область для ввода сообщения
        textAreaOutgoing = new JTextArea(OUTGOING_AREA_DEFAULT_ROWS, 0);
        final JScrollPane scrollPaneOutgoing = new JScrollPane(textAreaOutgoing);
        // Панель ввода сообщения
        final JPanel messagePanel = new JPanel();
        messagePanel.setBorder(BorderFactory.createTitledBorder("Сообщение"));
        final JButton sendButton = new JButton("Отправить");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MainFrame.this.sendMessage();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        this.instantMessager = new InstantMessager();
        this.instantMessager.addMessageListener(this);

        // Компоновка элементов панели "Сообщение"
        final GroupLayout layout2 = new GroupLayout(messagePanel);
        messagePanel.setLayout(layout2);
        layout2.setHorizontalGroup(layout2.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout2.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout2.createSequentialGroup()
                                .addComponent(labelFrom)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldFrom)
                                .addGap(LARGE_GAP)
                                .addComponent(labelTo)
                                .addGap(SMALL_GAP)
                                .addComponent(textFieldTo))
                        .addComponent(scrollPaneOutgoing)
                        .addComponent(sendButton))
                .addContainerGap());
        layout2.setVerticalGroup(layout2.createSequentialGroup()
                .addContainerGap().addGroup(layout2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelFrom)
                        .addComponent(textFieldFrom)
                        .addComponent(labelTo)
                        .addComponent(textFieldTo))
                .addGap(MEDIUM_GAP)
                .addComponent(scrollPaneOutgoing)
                .addGap(MEDIUM_GAP)
                .addComponent(sendButton)
                .addContainerGap());
        // Компоновка элементов фрейма
        final GroupLayout layout1 = new GroupLayout(getContentPane());
        setLayout(layout1);
        layout1.setHorizontalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout1.createParallelGroup()
                        .addComponent(scrollPaneIncoming)
                        .addComponent(messagePanel))
                .addContainerGap());
        layout1.setVerticalGroup(layout1.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneIncoming)
                .addGap(MEDIUM_GAP)
                .addComponent(messagePanel)
                .addContainerGap());
    }
    private void sendMessage() throws IOException {
        final String senderName = textFieldFrom.getText();
        final String destinationAddress = textFieldTo.getText();
        final String message = textAreaOutgoing.getText();
        if (senderName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите имя отправителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (destinationAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите адрес узла-получателя", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Peer us = new Peer("Masha",  "127.0.0.1");
        UserInfo.add(us);
        check = false;
        for(int i = 0; i < UserInfo.size(); i++) {
            if(UserInfo.get(i).getAddress().equals(destinationAddress))
                check = true;
        }
        Peer users = new Peer(senderName,  destinationAddress);
        UserInfo.add(users);

        if (check == false) {
            dialogFrame = new DialogFrame(users, MainFrame.this);
            check = true;
        }
        instantMessager.sendMessage(senderName, destinationAddress, message, SERVER_PORT);
        textAreaIncoming.setText(senderName + " (" + destinationAddress + ") : " + message);
        textAreaIncoming.append("\n" + "Я -> " + destinationAddress + ": " + message + "\n");
        textAreaOutgoing.setText("");
    }
    @Override
    public void messageReceived(Peer senderName, String message) {
        String str = senderName.getName() + " (" + senderName.getAddress() + "): " + message;
        //this.textAreaIncoming.setText(str);
    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);

    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MainFrame frame = new MainFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}