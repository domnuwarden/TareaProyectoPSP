import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Servidor centralizado para la gestión de peticiones de asistencia de alumnos.
 * Coordina las conexiones entrantes concurrentes y proporciona una interfaz
 * de consola para que el profesor despache los turnos por orden de llegada.
 * @author Liviu
 * @version 1.0
 */
public class ServidorProfesor {

    private static final int PUERTO = 55555; // Puerto configurado por defecto
    
    // Lista de turnos compartida y sincronizada para evitar condiciones de carrera
    private static final List<String> listaTurnos = Collections.synchronizedList(new ArrayList<>());
    // Lista de sockets activos para poder notificar o cerrar la conexión si el profesor los elimina
    private static final List<HiloCliente> hilosActivos = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructor por defecto.
     */
    public ServidorProfesor() {
        // No-op
    }

    /**
     * Método principal que inicializa el servidor del profesor.
     * Lanza el hilo de aceptación de conexiones y gestiona el menú de comandos del profesor.
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        System.out.println("=== SERVIDOR DEL PROFESOR INICIADO ===");
        
        // Hilo secundario para aceptar conexiones de alumnos sin bloquear el menú del profesor
        Thread hiloConexiones = new Thread(() -> {
            try (ServerSocket servidor = new ServerSocket(PUERTO)) {
                while (true) {
                    Socket socketCliente = servidor.accept();
                    HiloCliente nuevoCliente = new HiloCliente(socketCliente);
                    hilosActivos.add(nuevoCliente);
                    nuevoCliente.start();
                }
            } catch (IOException e) {
                System.err.println("Error en el servidor: " + e.getMessage());
            }
        });
        hiloConexiones.setDaemon(true);
        hiloConexiones.start();

        // Menú interactivo para el Profesor (Requisito 7: El servidor elimina peticiones)
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- GESTIÓN DE TURNOS (Profesor) ---");
            mostrarTurnos();
            System.out.println("Opciones: [1] Atender/Eliminar primer alumno | [2] Actualizar lista | [3] Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            if (opcion.equals("1")) {
                eliminarPrimerTurno();
            } else if (opcion.equals("3")) {
                System.out.println("Cerrando el servidor del profesor...");
                break;
            }
        }
        scanner.close();
    }

    /**
     * Muestra de forma sincronizada por consola la lista de alumnos en cola 
     * respetando el orden estricto de llegada.
     */
    private static synchronized void mostrarTurnos() {
        if (listaTurnos.isEmpty()) {
            System.out.println("[Cola vacía] No hay alumnos solicitando ayuda actualmente.");
        } else {
            System.out.println("Alumnos en cola (Orden de llegada):");
            for (int i = 0; i < listaTurnos.size(); i++) {
                System.out.println((i + 1) + ". " + listaTurnos.get(i));
            }
        }
    }

    /**
     * Atiende y elimina al primer alumno de la lista de turnos (FIFO).
     * Modifica la cola de manera segura y envía una notificación de expulsión al cliente correspondiente.
     */
    private static synchronized void eliminarPrimerTurno() {
        if (!listaTurnos.isEmpty()) {
            String alumnoAtendido = listaTurnos.remove(0);
            System.out.println("-> Se ha eliminado de la cola a: " + alumnoAtendido);
            
            // Notificar al hilo del cliente que ha sido removido por el profesor
            synchronized (hilosActivos) {
                hilosActivos.removeIf(hilo -> {
                    if (alumnoAtendido.equals(hilo.getNombreAlumno())) {
                        hilo.enviarMensaje("ELIMINADO_POR_PROFESOR");
                        hilo.cerrarConexion();
                        return true;
                    }
                    return false;
                });
            }
        } else {
            System.out.println("No hay alumnos en la cola para eliminar.");
        }
    }

    /**
     * Hilo encargado de gestionar la comunicación bidireccional de forma 
     * independiente con un alumno específico.
     * @author Liviu
     */
    private static class HiloCliente extends Thread {
        private final Socket socket;
        private PrintWriter salida;
        private BufferedReader entrada;
        private String nombreAlumno = null;

        /**
         * Construye una instancia del gestor de cliente asignándole su socket.
         * @param socket Objeto {@link Socket} de la conexión activa del alumno.
         */
        public HiloCliente(Socket socket) {
            this.socket = socket;
        }

        /**
         * Obtiene el nombre del alumno asociado a este hilo de comunicación.
         * @return El nombre del alumno o {@code null} si aún no se ha registrado.
         */
        public String getNombreAlumno() {
            return nombreAlumno;
        }

        /**
         * Envía una cadena de texto o comando específico al cliente alumno.
         * @param mensaje Cadena de texto a transmitir por la red.
         */
        public void enviarMensaje(String mensaje) {
            if (salida != null) salida.println(mensaje);
        }

        /**
         * Cierra el socket de forma segura si se encuentra abierto liberando los recursos de red.
         */
        public void cerrarConexion() {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ignored) {}
        }

        /**
         * Ciclo de vida del hilo. Procesa los comandos enviados por el alumno
         * (CONECTAR, CANCELAR, SALIR) y gestiona su salida ordenada o abrupta.
         */
        @Override
        public void run() {
            try {
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(socket.getOutputStream(), true);

                String linea;
                while ((linea = entrada.readLine()) != null) {
                    if (linea.startsWith("CONECTAR:")) {
                        // Requisito 4: Indicar su nombre
                        this.nombreAlumno = linea.substring(9).trim();
                        if (!listaTurnos.contains(nombreAlumno)) {
                            listaTurnos.add(nombreAlumno);
                            System.out.println("\n[NUEVA PETICIÓN] " + nombreAlumno + " ha solicitado turno.");
                            salida.println("OK_TURNO");
                        } else {
                            salida.println("ERROR_YA_EXISTE");
                        }
                    } 
                    else if (linea.equals("CANCELAR")) {
                        // Requisito 6: Cliente elimina su propia petición
                        if (nombreAlumno != null && listaTurnos.remove(nombreAlumno)) {
                            System.out.println("\n[CANCELACIÓN] " + nombreAlumno + " ha retirado su petición de ayuda.");
                            salida.println("OK_CANCELADO");
                        }
                    } 
                    else if (linea.equals("SALIR")) {
                        break;
                    }
                }
            } catch (IOException e) {
                // Maneja desconexiones abruptas del cliente
            } finally {
                // Limpieza al desconectarse el alumno
                if (nombreAlumno != null && listaTurnos.remove(nombreAlumno)) {
                    System.out.println("\n[DESCONEXIÓN] " + nombreAlumno + " se ha desconectado. Removido de la cola.");
                }
                hilosActivos.remove(this);
                cerrarConexion();
            }
        }
    }
}