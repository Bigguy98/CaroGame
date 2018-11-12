package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

import java.io.*;
import java.net.Socket;

public class Client extends Application {
    private Stage stage;
    private double sceneWidth = 750;
    private double sceneHeight = 516;
    private Scene sceneLogin,scenePlay;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private TextField tfUsername;
    private int id,numberOfClickedButton;
    private boolean isPlaying;
    private int[][] table;
    private GridPane paneTable;
    private Button[][] buttons;
    private ImageView imageView;
    private TextArea chatMessages,message;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage){
        stage = primaryStage;
        login();
    }
    public void login(){
        Label gameName = new Label("Game Caro");
        gameName.setFont(new Font(35));

        VBox paneLogin = new VBox(20);
        HBox paneUsername = new HBox(10);
        HBox panePassword = new HBox(10);

        Label lbUsername = new Label("Username");
        lbUsername.setPrefWidth(80);
        tfUsername = new TextField();
        tfUsername.setPromptText("Enter your username here!");
        tfUsername.setPrefColumnCount(20);

        Label lbPassword = new Label("Password");
        lbPassword.setPrefWidth(80);
        TextField tfPassword = new TextField();
        tfPassword.setPromptText("Enter your password here!");
        tfPassword.setPrefColumnCount(20);

        paneUsername.getChildren().addAll(lbUsername,tfUsername);
        paneUsername.setAlignment(Pos.CENTER);
        panePassword.getChildren().addAll(lbPassword,tfPassword);
        panePassword.setAlignment(Pos.CENTER);

        Button btnLogin = new Button("Login");
        btnLogin.setPrefWidth(80);
        btnLogin.setOnAction(e -> play());
        Button btnRegister = new Button("Register");
        btnRegister.setPrefWidth(80);

        HBox paneButtons = new HBox(10);
        paneButtons.getChildren().addAll(btnLogin,btnRegister);
        paneButtons.setAlignment(Pos.CENTER);

        paneLogin.getChildren().addAll(gameName,paneUsername,panePassword,paneButtons);
        paneLogin.setAlignment(Pos.CENTER);

        sceneLogin = new Scene(paneLogin,sceneWidth,sceneHeight);

        stage.setScene(sceneLogin);
        stage.show();
    }
    public void play(){
        table = new int[10][10];
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++) table[i][j] = -1;
        }
        isPlaying = false;
        buttons = new Button[10][10];
        numberOfClickedButton = 0;

        buildGUI();
        getConnection();

    }
    public void buildGUI(){
        paneTable = new GridPane();
        paneTable.setAlignment(Pos.TOP_LEFT);
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                buttons[i][j] = new Button();
                int row = i, col = j;
                buttons[i][j].setPrefSize(40,40);
                buttons[i][j].setOnAction(e -> choose(row,col,buttons[row][col]));
                paneTable.add(buttons[i][j],j,i);
            }
        }

        VBox paneUser = new VBox(20);
        Label username = new Label();
        username.setText(tfUsername.getText());
        username.setFont(new Font(20));

        chatMessages = new TextArea();
        chatMessages.setPrefColumnCount(15);
        chatMessages.setPrefHeight(90);
        chatMessages.setEditable(false);

        imageView = new ImageView();
        imageView.setFitHeight(180);
        imageView.setFitWidth(280);

        HBox paneChat = new HBox(5);

        message = new TextArea();
        message.setWrapText(true);
        message.setPrefColumnCount(15);
        message.setPrefHeight(30);

        Button btnSend = new Button("SEND");
        btnSend.setFont(new Font(15));
        btnSend.setOnAction(e -> sendMessage());

        paneChat.getChildren().addAll(message,btnSend);
        paneUser.getChildren().addAll(username,imageView,chatMessages,paneChat);

        HBox paneMid = new HBox(30);
        paneMid.getChildren().addAll(paneTable,paneUser);

        Label gameName = new Label("Game Caro");
        gameName.setFont(new Font(25));

        BorderPane paneMain = new BorderPane();
        paneMain.setCenter(paneMid);
        paneMain.setTop(gameName);
        paneMain.setPadding(new Insets(30,20,30,20));
        paneMain.setAlignment(gameName,Pos.CENTER);
        BorderPane.setMargin(gameName,new Insets(0,0,20,0));
        scenePlay = new Scene(paneMain);

        stage.setScene(scenePlay);
        stage.show();
    }
    public void getConnection(){
        try {
            Socket socket = new Socket("127.0.0.1",5008);
            reader = new ObjectInputStream(socket.getInputStream());
            writer = new ObjectOutputStream(socket.getOutputStream());
            Thread t = new Thread(new InformationReader());
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class InformationReader implements Runnable{
        @Override
        public void run() {
            Object object;
            try{
                while((object = reader.readObject()) != null){
                    if(object instanceof Integer){
                        id = (Integer) object;
                        Platform.runLater(() -> setImage() );
                        isPlaying = true;
                    }
                    if(object instanceof Cell){
                        Cell cell = (Cell) object;
                        int row = cell.getRow();
                        int col = cell.getColumn();
                        if(numberOfClickedButton % 2 == id) table[row][col] = id;
                        else table[row][col] = (id+1)%2;
                        numberOfClickedButton++;
                        System.out.println(row + " " +  col);
                        Platform.runLater(() -> updateCellPicture(row,col));
                        if(numberOfClickedButton > 8){
                            if(checkWin() == 1) win();
                            if(checkWin() == 0) loose();
                        }
                    }
                    if(object instanceof String){
                        String text = (String) object;
                        Platform.runLater(() -> chatMessages.appendText(text + "\n"));
                    }
                }
            }catch (Exception ex){
            }
        }
    }
    public void choose(int row,int col,Button btn){
        if(table[row][col] != -1){
            MessageBox.show("That cell has been ticked","Please Tick Another");
            return;
        }
        if(isPlaying && numberOfClickedButton % 2 == id ){
//            btn.setGraphic(image);;
//            table[row][col] = id;
//            numberOfClickedButton++
            try {
                writer.writeObject(new Cell(row,col));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            MessageBox.show("Not your turn!","Please Wait");
        }
    }
    public void updateCellPicture(int row,int col){
        if(numberOfClickedButton%2 == 1) buttons[row][col].setText("X");
        else buttons[row][col].setText("O");
    }
    public void setImage(){
        if(id == 0) imageView.setImage(new Image("naruto.png"));
        else imageView.setImage(new Image("sasuke.png"));
    }
    public int checkWin(){
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++) System.out.print(table[i][j] + " ");
            System.out.println();
        }
        int r1 = checkNgang();
        int r2 = checkDoc();
        int r3 = checkCheo1();
        int r4 = checkCheo2();


        if(r1 == id || r2 == id  || r3 == id || r4 == id){
            System.out.println("win");
            return 1;
        }

        if(r1 == ((id+1)%2) || r2 == ((id+1)%2)  || r3 == ((id+1)%2) || r4 == ((id+1)%2)){
            System.out.println("loose");
            return 0;
        }

        return -1;
    }
    public int checkNgang(){
        for(int i=0;i<9;i++){
            for(int j=0;j<=5;j++){
                int v = table[i][j];
                if( v!= -1 && v == table[i][j+1] && v == table[i][j+2] && v == table[i][j+3] && v == table[i][j+4]) return v;
            }
        }
        return -1;
    }
    public int checkDoc(){
        for(int j=0;j<9;j++){
            for(int i=0;i<=5;i++){
                int v = table[i][j];
                if( v!= -1 && v == table[i+1][j] && v == table[i+2][j] && v == table[i+3][j] && v == table[i+4][j]) return v;
            }
        }
        return -1;
    }
    public int checkCheo1(){
        int u = 5;
        while(u>=0){
            int i = u, j = 0;
            while(i + 4 < 10){
                int x = table[i][j];
                if(x != -1 && x == table[i+1][j+1] && x == table[i+2][j+2] && x == table[i+3][j+3] && x == table[i+4][j+4]) return x;
                i++; j++;
            }
            u--;
        }
        int v = 5;
        while(v>=0){
            int i = 0, j = v;
            while(j + 4 < 10){
                int x = table[i][j];
                if(x != -1 && x == table[i+1][j+1] && x == table[i+2][j+2] && x == table[i+3][j+3] && x == table[i+4][j+4]) return x;
                i++; j++;
            }
            v--;
        }
        return -1;
    }
    public int checkCheo2(){
        int u = 5;
        while(u>=0){
            int i = u, j = 9;
            while(i + 4 < 10){
                int x = table[i][j];
                if(x != -1 && x == table[i+1][j-1] && x == table[i+2][j-2] && x == table[i+3][j-3] && x == table[i+4][j-4]) return x;
                i++; j--;
            }
            u--;
        }
        int v = 4;
        while(v < 10){
            int i = 0, j = v;
            while(j - 4  >= 0){
                int x = table[i][j];
                if(x != -1 && x == table[i+1][j-1] && x == table[i+2][j-2] && x == table[i+3][j-3] && x == table[i+4][j-4]) return x;
                i++; j--;
            }
            v++;
        }
        return -1;
    }
    public void win(){
        Platform.runLater(() ->{
            numberOfClickedButton = (id+1)%2;
            reset();
            MessageBox.show("You won","Result");
        });

    }
    public void loose(){
        Platform.runLater(() ->{
            numberOfClickedButton = id;
            reset();
            MessageBox.show("You loose","Result");
        });
    }
    public void reset(){
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++) table[i][j] = -1;
        }
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++) buttons[i][j].setText("");
        }
    }
    public void sendMessage(){
        try {
            String textMessage = tfUsername.getText() + ": " + message.getText();
            writer.writeObject(textMessage);
            message.setText("");
            message.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
