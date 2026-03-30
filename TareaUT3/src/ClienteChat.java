import java.io.*;
import java.net.*;

public class ClienteChat {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 55555;

        try {
            Socket socket = new Socket(host, puerto);
            System.out.println("Conectado al servidor en " + host + ":" + puerto);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            new HiloEnvio(out).start();
            new HiloRecepcion(in).start();

        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor. ¿Está encendido?");
        }
    }
}