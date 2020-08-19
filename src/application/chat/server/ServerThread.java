package application.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Vector;

public class ServerThread implements Runnable {

	private Vector<ServerThread> clientList;
	private Socket socket;
	private String nickName;
	private BufferedWriter bw;
	private BufferedReader br;

	public ServerThread(Socket socket, Vector<ServerThread> clientList) {
		this.socket = socket;
		this.clientList = clientList;
	}

	@Override
	public synchronized void run() {
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			while (true) {
				System.out.println("메시지받기전");
				if(null == nickName) {
					nickName = br.readLine();
					String message = nickName + "님이 접속하셨습니다.";
					System.out.println(message);
					sendGreeting(message);
				} else {
					String message = br.readLine();
					System.out.println("message >> " + message);
					sendMessageToAll(message);
				}
			}
		} catch (IOException e) {
			clientList.remove(this);
			String message = "[클라이언트 통신 안됨: " + socket.getRemoteSocketAddress() + ": "
					+ Thread.currentThread().getName() + " ]";
			System.out.println(message);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
			try {
				bw.close();
			} catch (IOException e) {
			}
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	private void sendGreeting(String message) throws IOException {
		for (ServerThread st : clientList) {
			if (!st.socket.equals(this.socket)) {
				st.sendMessage(message);
			}
		}
	}

	private void sendMessageToAll(String message) throws IOException {
		for (ServerThread st : clientList) {
			st.sendMessage(message);
		}
	}

	private void sendMessage(String message) throws IOException {
		bw.write(message + "\n");
		bw.flush();
	}

}
