package application.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
	
	private static final int PORT = 9999;
	private ServerSocket serverSocket;
	static List<ServerThread> clientList = new CopyOnWriteArrayList<ServerThread>();
	
	public void startServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(PORT);
					System.out.println("서버 시작");
					
					while (true) {
						Socket socket = serverSocket.accept();
						System.out.println("클라이언트 연결 [ " + socket+" ]");
						
						ServerThread serverThread = new ServerThread(socket);
						clientList.add(serverThread);
						new Thread(serverThread).start();						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}).start();
	}
}
