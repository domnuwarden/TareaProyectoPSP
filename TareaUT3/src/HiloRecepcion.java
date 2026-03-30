import java.io.DataInputStream;
import java.io.IOException;

/**
 * Clase que gestiona la recepción de mensajes en un hilo independiente.
 * Escucha constantemente el socket y muestra los mensajes entrantes por pantalla.
 * * @author Liviu
 * @version 1.0
 */
public class HiloRecepcion extends Thread {
    private DataInputStream in;

    /**
     * Constructor del hilo de recepción.
     * @param in Flujo de entrada de datos asociado al socket.
     */
    public HiloRecepcion(DataInputStream in) {
        this.in = in;
    }

    /**
     * Ciclo de vida del hilo: espera mensajes del socket de forma asíncrona.
     */
    @Override
    public void run() {
        try {
            while (true) {
                // El hilo se queda esperando aquí sin bloquear el resto del programa
                String mensaje = in.readUTF();
                
                System.out.println("\n[Mensaje recibido]: " + mensaje);
                
                // Si el otro extremo cierra, terminamos el proceso
                if (mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("El interlocutor ha finalizado la sesión. Presione Enter para salir.");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("Se ha perdido la conexión con el equipo remoto.");
        }
    }
}