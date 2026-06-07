import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Representa el programa cliente que utilizan los alumnos para solicitar ayuda.
 * Permite conectarse al servidor del profesor, ingresar en una cola de espera,
 * cancelar la solicitud de manera proactiva o esperar a ser atendido en tiempo real.
 * @author Liviu
 * @version 1.0
 */
public class ClienteAlumno {

    private static final String HOST = "localhost";
    private static final int PUERTO = 55555; // Mismo puerto que el servidor

    /**
     * Constructor por defecto.
     */
    public ClienteAlumno() {
        // No-op
    }

    /**
     * Método principal que arranca la aplicación del alumno.
     * Establece la conexión con el servidor e inicializa el menú interactivo
     * y el hilo de escucha asíncrono.
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== SISTEMA DE ASISTENCIA - CLIENTE ALUMNO ===");
        System.out.print("Introduce tu nombre: ");
        String nombre = scanner.nextLine();

        try (Socket socket = new Socket(HOST, PUERTO);
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // 1. Registrarse enviando el nombre al servidor
            salida.println("CONECTAR:" + nombre);
            String respuestaServidor = entrada.readLine();

            if ("ERROR_YA_EXISTE".equals(respuestaServidor)) {
                System.out.println("Error: Ya hay una petición en curso con tu nombre.");
                return;
            }

            System.out.println("¡Petición enviada con éxito! Estás en la cola de ayuda.");

            // 2. Hilo secundario para escuchar alertas del Servidor en tiempo real
            Thread hiloEscucha = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = entrada.readLine()) != null) {
                        if (msg.equals("ELIMINADO_POR_PROFESOR")) {
                            System.out.println("\n[AVISO] ¡El profesor te ha atendido y ha liberado tu turno!");
                            System.out.println("Presione Enter para salir.");
                            break;
                        }
                    }
                } catch (IOException e) {
                    // El socket se cerró de forma esperada
                }
            });
            hiloEscucha.start();

            // 3. Menú interactivo del alumno
            String opcion = "";
            while (!opcion.equals("2") && !socket.isClosed()) {
                System.out.println("\nMenú disponible:");
                System.out.println("[1] Cancelar mi turno de ayuda");
                System.out.println("[2] Salir de la aplicación");
                System.out.print("Selecciona una opción: ");
                opcion = scanner.nextLine();

                if (socket.isClosed()) break;

                if (opcion.equals("1")) {
                    // Requisito 6: Eliminar petición en cualquier momento
                    salida.println("CANCELAR");
                    String conf = entrada.readLine();
                    if ("OK_CANCELADO".equals(conf)) {
                        System.out.println("Tu petición ha sido cancelada correctamente.");
                    }
                } else if (opcion.equals("2")) {
                    salida.println("SALIR");
                }
            }

        } catch (IOException e) {
            System.err.println("No se pudo conectar con el servidor del profesor: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("Aplicación del alumno finalizada.");
        }
    }
}