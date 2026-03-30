import java.io.*;
import java.net.*;

/**
 * Programa Cliente para el Chat Asíncrono.
 * Se conecta a un servidor remoto y lanza los hilos de comunicación.
 * * @author Liviu
 * @version 1.0
 */
public class ClienteChat {
    /**
     * Método principal que inicia el cliente.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        String host = "localhost"; // Dirección del servidor
        int puerto = 55555;

        try {
            // Intento de conexión al servidor
            Socket socket = new Socket(host, puerto);
            System.out.println("CLIENTE: Conectado con éxito al servidor " + host);

            // Configuración de flujos de datos
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Al igual que el servidor, el cliente usa hilos para no depender del orden
            new HiloEnvio(out).start();
            new HiloRecepcion(in).start();

        } catch (UnknownHostException e) {
            System.err.println("No se encuentra el host: " + host);
        } catch (IOException e) {
            System.err.println("Error de E/S al conectar: " + e.getMessage());
        }
    }
}