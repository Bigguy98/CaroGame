package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;



public class Client extends Application {
    Stage stage;
    private double sceneWidth = 750;
    private double sceneHeight = 516;
    Scene sceneLogin,sceneCentre,scenePlay;
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
        TextField tfUsername = new TextField();
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
    public void centre(){

    }
    public void play(){
        GridPane paneTable = new GridPane();
        paneTable.setAlignment(Pos.TOP_LEFT);
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                Button btn = new Button();
                int row = i, col = j;
                btn.setPrefSize(40,40);
                paneTable.add(btn,i,j);
            }
        }

        VBox paneUser = new VBox(20);

        Label username = new Label();
        username.setText("Player one");
        username.setFont(new Font(20));

        TextArea chatMessages = new TextArea();
        chatMessages.setPrefColumnCount(15);
        chatMessages.setPrefHeight(90);
        chatMessages.setEditable(false);

        ImageView imageView = new ImageView();
        imageView.setImage(new Image("baobinh.jpg"));
        imageView.setFitHeight(180);
        imageView.setFitWidth(280);

        HBox paneChat = new HBox(5);

        TextArea message = new TextArea();
        message.setWrapText(true);
        message.setPrefColumnCount(15);
        message.setPrefHeight(30);

        Button btnSend = new Button("SEND");
        btnSend.setFont(new Font(15));


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
}
