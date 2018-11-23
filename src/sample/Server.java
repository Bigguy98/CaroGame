package sample;

        import java.io.*;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.util.ArrayList;

public class Server {
    private static ArrayList<ObjectOutputStream> clients;
    public static void main(String[] args) throws IOException {
        new Server().go();
    }
    public void go() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5008);
        clients = new ArrayList<>();
        while(clients.size() < 2){
            Socket clientSocket = serverSocket.accept();
            ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
            clients.add(writer);
            Thread t = new Thread(new ClientHandler(clientSocket));
            t.start();
            System.out.println("One client has connected!");
            if(clients.size() == 2){
                ObjectOutputStream out1 = clients.get(0);
                out1.writeObject(0);
                out1.flush();
                ObjectOutputStream out2 = clients.get(1);
                out2.writeObject(1);
                out2.flush();
                System.out.println("Sended");
            }
        }
    }
    private class ClientHandler implements Runnable {
        private ObjectInputStream reader;
        public ClientHandler(Socket socket) throws IOException {
            this.reader = new ObjectInputStream(socket.getInputStream());
        }
        @Override
        public void run() {
            Object object;
            try {
                while ((object = reader.readObject()) != null) {
                    System.out.println(object.getClass());
                    tellEverOne(object);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void tellEverOne(Object object){
        for(ObjectOutputStream x  : clients){
            try {
                x.writeObject(object);
                x.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

