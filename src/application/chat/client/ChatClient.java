package application.chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChatClient extends Application {
	private static final String SERVER_IP = "localhost";
	private static final int SERVER_PORT = 9999;
	static String nickName;
	private Socket socket;
	//
	private BufferedWriter writer;
	private BufferedReader reader;

	// 연결 시작
	void connectionServer() {
		Thread thread = new Thread() {// 스레드 생성
			@Override
			public void run() {
				try {
					// 소켓 생성 및 연결요청
					socket = new Socket();
					socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
				} catch (Exception e) {
					Platform.runLater(() -> displayText("[서버 통신 안됨]"));
					if (!socket.isClosed()) {
						disConnectionServer();
					}
					return;
				}
				receive(); // 서버에서 보낸 데이터 받기
			}
		};
		thread.start();
	}

	// 연결 끊기
	void disConnectionServer() {
		try {
			Platform.runLater(() -> {
				displayText("[연결 끊음]");
				btnSend.setDisable(true);
			});
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
		}
	}

	// 데이터 받기
	void receive() {
		while (true) {
			System.out.println("클라 메세지 받기");
			try {
				String data = reader.readLine();
				Platform.runLater(() -> displayText(data));
			} catch (IOException e) {
				Platform.runLater(() -> displayText("[서버 통신 안됨]"));
				disConnectionServer();
				break;
			}
		}
	}

	// 데이터 전송
	void sendMessage(String message) {
		try {
			writer.write(nickName + ": " + message + "\n");
			writer.flush();
			txtInput.setText("");
		} catch (IOException e) {
			Platform.runLater(() -> displayText("[서버 통신 안됨]"));
			disConnectionServer();
		}
	}


	void inputNickName() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("nick");
		dialog.setContentText("닉네임을 입력하세요");

		Optional<String> nick = dialog.showAndWait();
		if (nick.isPresent()) {
			nickName = nick.get();
		}
	}

	void sendNickName() throws IOException {
		writer.write(nickName + "\n");
		writer.flush();
	}

	// ui
	TextArea txtDisplay;
	TextField txtInput;
	Button btnConn;
	Button btnSend;

	@Override
	public void start(Stage primaryStage) throws Exception {
		connectionServer();

		inputNickName();
		sendNickName();

		BorderPane root = new BorderPane();
		root.setPrefSize(500, 300);

		txtDisplay = new TextArea();
		txtDisplay.setEditable(false);
		BorderPane.setMargin(txtDisplay, new Insets(0, 0, 2, 0));
		root.setCenter(txtDisplay);

		BorderPane bottom = new BorderPane();
		txtInput = new TextField();
		txtInput.setPrefSize(60, 30);
		BorderPane.setMargin(txtInput, new Insets(0, 1, 1, 1));

		btnConn = new Button("disconnect");
		btnConn.setPrefSize(60, 30);
		btnConn.setOnAction(e -> disConnectionServer());

		btnSend = new Button("send");
		btnSend.setPrefSize(60, 30);
		// send버튼 클릭했을 때 이벤트
		btnSend.setOnAction(e -> sendMessage(txtInput.getText()));

		bottom.setCenter(txtInput);
		bottom.setLeft(btnConn);
		bottom.setRight(btnSend);
		root.setBottom(bottom);

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("app.css").toString());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Client");
		primaryStage.setOnCloseRequest(event -> disConnectionServer());
		primaryStage.show();
	}

	// 내용출력
	void displayText(String text) {
		txtDisplay.appendText(text + "\n");
	}

	public static void main(String[] args) {
		launch(args);
	}
}
