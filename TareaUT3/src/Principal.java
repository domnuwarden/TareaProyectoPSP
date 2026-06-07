import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;

/**
 * Clase principal de control del sistema de elecciones.
 * Instancia los recursos compartidos, define las políticas de control mediante un
 * Pool de Hilos y un Semáforo equitativo, y gestiona la barrera de tiempo para el escrutinio final.
 * @author Liviu
 * @version 1.0
 */
public class Principal {

    private static final int TOTAL_HABITANTES = 76000;
    private static final int NUM_MESAS = 3;

    /**
     * Constructor por defecto.
     */
    public Principal() {
        // No-op
    }

    /**
     * Método de entrada de la aplicación de escrutinio masivo.
     * Construye la infraestructura concurrente, lanza las tareas y procesa los resultados finales.
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        System.out.println("--- INICIO DE LAS ELECCIONES EN CIUDAD REAL ---");

        // Creamos los elementos compartidos
        Semaphore mesasElectorales = new Semaphore(NUM_MESAS, true); // true = orden estricto de llegada
        UrnaGlobal urna = new UrnaGlobal();

        // Creamos un pool de hilos para procesar de forma óptima los 76.000 habitantes
        ExecutorService ejecutor = Executors.newFixedThreadPool(100);

        // Instanciamos y lanzamos los habitantes
        for (int i = 1; i <= TOTAL_HABITANTES; i++) {
            Habitante habitante = new Habitante(i, mesasElectorales, urna);
            ejecutor.execute(habitante);
        }

        // Cerramos el grifo de nuevas tareas y esperamos que terminen las actuales
        ejecutor.shutdown();
        try {
            // Espera hasta 1 minuto a que el pool procese todos los votos
            if (ejecutor.awaitTermination(1, TimeUnit.MINUTES)) {
                // Una vez que todos han votado, hacemos el escrutinio
                mostrarResultados(urna);
            }
        } catch (InterruptedException e) {
            System.err.println("El escrutinio se vio interrumpido.");
        }
    }

    /**
     * Imprime por salida estándar el desglose final y detallado de las votaciones contenidas en la urna.
     * @param urna Objeto {@link UrnaGlobal} que contiene los resultados consolidados de la simulación.
     */
    private static void mostrarResultados(UrnaGlobal urna) {
        System.out.println("\n--- ESCRUTINIO FINAL ---");
        System.out.println("Votos para Tramp (PRCR): " + urna.getVotosTramp());
        System.out.println("Votos para Tutin (CRU): " + urna.getVotosTutin());
        System.out.println("Votos para Chi Ginpin (PCCR): " + urna.getVotosChiGinpin());
        System.out.println("---------------------------------------");
        System.out.println("Total de votos en urnas: " + urna.getTotalVotos());
        System.out.println("Población total convocada: " + TOTAL_HABITANTES);
        System.out.println("--- FIN DEL PROGRAMA ---");
    }
}