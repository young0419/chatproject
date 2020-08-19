package application.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {

	private static final int PORT = 9999;
	private ServerSocket serverSocket;
	private Vector<ServerThread> clientList = new Vector<ServerThread>();

	void startServer() {

		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("서버 시작");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("클라이언트 연결 [ " + socket + " ]");
				ServerThread serverThread = new ServerThread(socket, clientList);
				new Thread(serverThread).start();
				
				clientList.add(serverThread);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void closeSocket() {
		try {
			if (serverSocket != null && !serverSocket.isClosed())
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ChatServer().startServer();
	}
}
