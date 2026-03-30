import java.io.*;
import java.net.*;

public class ServidorChat {
    public static void main(String[] args) {
        int puerto = 55555;
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor a la escucha en el puerto " + puerto);
            Socket socket = servidor.accept();
            System.out.println("Cliente conectado desde: " + socket.getInetAddress());

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            new HiloEnvio(out).start();
            new HiloRecepcion(in).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}