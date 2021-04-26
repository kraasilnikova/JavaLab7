package bsu.rfe.java.group9.Krasilnikova;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class InstantMessager implements MessageListener {
    private String sender;
    private static final int SERVER_PORT = 4567;
    private List<MessageListener> listeners = new LinkedList();

    public InstantMessager() {
        this.startServer(SERVER_PORT);
    }

    public void sendMessage(String senderName, String destinationAddress, String message, int SERVER_PORT) throws UnknownHostException, IOException {
        try {
            // Создаем сокет для соединения
            final Socket socket = new Socket(destinationAddress, SERVER_PORT);
            // Открываем поток вывода данных
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(senderName);
            out.writeUTF(message);
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Не удалось отправить сообщение: узел-адресат не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    };

    private void startServer(int SERVER_PORT) {
        // Создание и запуск потока-обработчика запросов
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());
                        final String senderName = in.readUTF();
                        final String message = in.readUTF();
                        socket.close();
                        InstantMessager.this.notifyListeners(new Peer(senderName, (InetSocketAddress) socket.getRemoteSocketAddress()), message);
                        // Выводим сообщение в текстовую область
                        //textAreaIncoming.append(senderName + " (" + address + "): " + message + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Ошибка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    };

    public void addMessageListener(MainFrame listener) {
        synchronized (this.listeners) {
            this.listeners.add((MessageListener) listener);
        }
    }
    public void removeMessageListener(MessageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void notifyListeners(Peer sender, String message) {
        synchronized (this.listeners) {
            for (MessageListener listener : listeners) {
                listener.messageReceived(sender, message);
            }
        }
    }

    @Override
    public void messageReceived(Peer senderName, String message) {

    }
}