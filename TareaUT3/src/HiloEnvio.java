import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Clase que gestiona el envío de mensajes en un hilo independiente.
 * Lee la entrada del usuario desde la consola y la envía a través del socket.
 * * @author Liviu
 * @version 1.0
 */
public class HiloEnvio extends Thread {
    private DataOutputStream out;

    /**
     * Constructor del hilo de envío.
     * @param out Flujo de salida de datos asociado al socket.
     */
    public HiloEnvio(DataOutputStream out) {
        this.out = out;
    }

    /**
     * Ciclo de vida del hilo: lee de teclado y envía hasta que se escribe "salir".
     */
    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                // Bloquea el hilo esperando entrada del usuario
                String mensaje = sc.nextLine();
                
                // Envía el mensaje codificado en UTF
                out.writeUTF(mensaje);
                
                // Condición de parada para el hilo de envío
                if (mensaje.equalsIgnoreCase("salir")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el flujo de salida: " + e.getMessage());
        } finally {
            // Cerramos el scanner al terminar
            sc.close();
        }
    }
}