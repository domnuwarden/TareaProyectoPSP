import java.io.*;
import java.net.*;

/**
 * Programa Servidor para el Chat Asíncrono.
 * Establece un ServerSocket y espera a que un cliente se conecte para iniciar
 * el intercambio de datos mediante hilos.
 * * @author Liviu
 * @version 1.0
 */
public class ServidorChat {
    /**
     * Método principal que inicia el servidor.
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        // Usamos el puerto configurado por defecto
        int puerto = 55555;
        
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("SERVIDOR: Esperando conexión en el puerto " + puerto + "...");
            
            // Aceptación del cliente (operación bloqueante)
            Socket socket = servidor.accept();
            System.out.println("SERVIDOR: Cliente conectado desde " + socket.getInetAddress());

            // Configuración de flujos de datos
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // INICIO DE LA ASINCRONÍA:
            // Se crean dos hilos para que el servidor pueda "hablar" y "escuchar" a la vez.
            HiloEnvio hEnvio = new HiloEnvio(out);
            HiloRecepcion hRecepcion = new HiloRecepcion(in);

            hEnvio.start();
            hRecepcion.start();

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}