package application.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerThread implements Runnable {

	private Socket socket;
	private String nickName;
	private BufferedWriter bw;
	private BufferedReader br;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String message = null;
			while(true) {
				if (null == nickName) {
					nickName = br.readLine();
					message = nickName + "님이 접속하셨습니다.";
					sendMessageToAll(message);
				} else {
					message = br.readLine();
				}
				sendMessageToAll(message);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMessageToAll(String message) throws IOException {
		for (ServerThread st : ChatServer.clientList) {
			st.sendMessage(message);
		}
	}

	private void sendMessage(String message) throws IOException {
		bw.write(message);
		bw.flush();
	}

}
