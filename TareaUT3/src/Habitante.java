import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Representa un ciudadano que ejecuta el proceso de votación de manera concurrente.
 * Utiliza un semáforo para simular el acceso controlado a las mesas de votación 
 * antes de interactuar de forma segura con la urna compartida.
 * @author Liviu
 * @version 1.0
 */
public class Habitante extends Thread {
    
    private final int id;
    private final Semaphore mesas;
    private final UrnaGlobal urna;

    /**
     * Construye un nuevo habitante parametrizando su contexto concurrente.
     * @param id Identificador numérico del habitante.
     * @param mesas Semáforo compartido que restringe el paso simultáneo a las mesas.
     * @param urna Recurso centralizado compartido donde se registra el voto.
     */
    public Habitante(int id, Semaphore mesas, UrnaGlobal urna) {
        this.id = id;
        this.mesas = mesas;
        this.urna = urna;
    }

    /**
     * Ciclo de vida del hilo habitante. Solicita permiso en el semáforo, 
     * computa una elección aleatoria, altera la sección crítica (urna) y 
     * garantiza la liberación de su puesto en el bloque {@code finally}.
     */
    @Override
    public void run() {
        try {
            // 1. El habitante llega y espera en la cola del Semáforo (orden de llegada)
            mesas.acquire();

            // 2. Selecciona la papeleta al azar (0, 1 o 2)
            Random random = new Random();
            int seleccion = random.nextInt(3);

            // 3. Modifica la sección crítica a través de la urna compartida
            switch (seleccion) {
                case 0 -> urna.votarTramp();
                case 1 -> urna.votarTutin();
                case 2 -> urna.votarChiGinpin();
            }

        } catch (InterruptedException e) {
            System.err.println("Habitante " + id + " fue interrupted.");
        } finally {
            // 4. Pase lo que pase, al terminar libera su sitio en la mesa
            mesas.release();
        }
    }
}