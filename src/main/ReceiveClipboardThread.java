package main;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveClipboardThread extends Thread {

    private final Principal principal;
    private Socket connectionSocket;
    private ServerSocket serverSocket;

    public ReceiveClipboardThread(Principal principal) {
        this.principal = principal;
    }
    

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(principal.getIntPort());
            connectionSocket = serverSocket.accept();
            principal.getConsoleArea().setText("Client connected: " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort() + "\n" + principal.getConsoleArea().getText());
            while (!serverSocket.isClosed()) {
                if (connectionSocket.isClosed()) {
                    connectionSocket = serverSocket.accept();
                    principal.getConsoleArea().setText("Client connected: " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort() + "\n" + principal.getConsoleArea().getText());
                }
                String clientClipboard = null;
                try {
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    clientClipboard = inFromClient.readLine();
                } catch (IOException ex) {
                    clientClipboard = null;
                }
                if (clientClipboard != null) {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(clientClipboard), null);
                    principal.getConsoleArea().setText("Clipboard received! '" + clientClipboard + "'\n" + principal.getConsoleArea().getText());
                } else {
                    principal.getConsoleArea().setText("Client disconnected: " + connectionSocket.getInetAddress() + ":" + connectionSocket.getPort() + "\n" + principal.getConsoleArea().getText());
                    connectionSocket.close();
                }
            }
        } catch (IOException ex) {
            System.err.println("Erro de IO: " + ex.getMessage());
        }
    }
    
    public void disableServer() {
        try {
            if (connectionSocket != null && !connectionSocket.isClosed()) {
                connectionSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            System.err.println("Erro de IO: " + ex.getMessage());
        }
    }

}
