import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class HiloEnvio extends Thread {
    private DataOutputStream out;

    public HiloEnvio(DataOutputStream out) {
        this.out = out;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                String mensaje = sc.nextLine();
                out.writeUTF(mensaje);
                if (mensaje.equalsIgnoreCase("salir")) break;
            }
        } catch (IOException e) {
            System.out.println("Error en el envío: " + e.getMessage());
        }
    }
}