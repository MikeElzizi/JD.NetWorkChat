import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server extends Thread{

    public static LinkedList<Server> serverList = new LinkedList<>();
    private final Socket socket; // сокет, через который сервер общается с клиентом,
    public final BufferedReader in; // поток чтения из сокета
    private final BufferedWriter out; // поток записи в сокет

    public Server(Socket socket) throws IOException, InterruptedException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        props.load(Files.newInputStream(new File("settings.txt").toPath()));
        int SERVER_PORT = Integer.parseInt(props.getProperty("SERVER_PORT"));

        try (ServerSocket server = new ServerSocket(SERVER_PORT)) {
            writeFileServer("Сервер запущен...");
            System.out.println("Сервер запущен...");
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new Server(socket));
                    writeFileServer("Новое подключение к серверу!");
                    System.out.println("Новое подключение к серверу!");
                } catch (IOException e) {
                    socket.close();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    @Override
    public void run() {
        String message;
        try {
            message = in.readLine();
            out.write(message + "\n");
            out.flush();
            while (true) {
                message = in.readLine();
                try {
                    if (message.equals("/exit")) {
                        this.downSocket();
                        break;
                    }
                } catch (NullPointerException ignored) {
                }
                writeFileServer(message);
                System.out.println(message);
                for (Server vr : serverList) {
                    vr.sendMsg(message);
                }
                if (message == null) {
                    System.out.println("Пользователь вышел");
                    break;
                }
            }
        } catch (IOException e) {
            this.downSocket();
        }
    }

    private void sendMsg(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }

    protected void downSocket() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                out.close();
                in.close();
                for (Server vr : serverList) {
                    if(vr.equals(this)){
                        vr.interrupt();
                    }
                    serverList.remove(this);
                }
                writeFileServer("Сокет выключился!");
                System.out.println("Сокет выключился!");
            }
        } catch (IOException ignored) {}
    }

    protected static void writeFileServer(String msg) {
        try (FileWriter writer = new FileWriter("files.log", true)) {
            writer.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime()))
                    .append(msg)
                    .append('\n')
                    .flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
