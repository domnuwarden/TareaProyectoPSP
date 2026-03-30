import java.io.DataInputStream;
import java.io.IOException;

public class HiloRecepcion extends Thread {
    private DataInputStream in;

    public HiloRecepcion(DataInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String mensaje = in.readUTF();
                System.out.println("\n[Mensaje recibido]: " + mensaje);
                if (mensaje.equalsIgnoreCase("salir")) {
                    System.out.println("El otro extremo ha cerrado la conexión.");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("Conexión finalizada.");
        }
    }
}