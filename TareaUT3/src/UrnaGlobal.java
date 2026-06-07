/**
 * Clase contenedora que actúa como el recurso crítico e hilo-seguro (Thread-safe).
 * Almacena y procesa el recuento globalizado de votos emitidos por los ciudadanos.
 * Todos sus métodos mutadores están protegidos mediante exclusión mutua.
 * @author Liviu
 * @version 1.0
 */
public class UrnaGlobal {
    // Variables normales para contar los votos
    private int votosTramp = 0;
    private int votosTutin = 0;
    private int votosChiGinpin = 0;
    private int totalVotos = 0;

    /**
     * Constructor por defecto.
     */
    public UrnaGlobal() {
        // No-op
    }

    /**
     * Incrementa de forma atómica y sincronizada un voto para el candidato Tramp
     * y actualiza el contador total de la urna.
     */
    public synchronized void votarTramp() {
        this.votosTramp++;
        this.totalVotos++;
    }

    /**
     * Incrementa de forma atómica y sincronizada un voto para el candidato Tutin
     * y actualiza el contador total de la urna.
     */
    public synchronized void votarTutin() {
        this.votosTutin++;
        this.totalVotos++;
    }

    /**
     * Incrementa de forma atómica y sincronizada un voto para el candidato Chi Ginpin
     * y actualiza el contador total de la urna.
     */
    public synchronized void votarChiGinpin() {
        this.votosChiGinpin++;
        this.totalVotos++;
    }

    /**
     * Obtiene el número parcial de votos asignados a Tramp.
     * @return Cantidad de votos acumulados.
     */
    public int getVotosTramp() { return votosTramp; }

    /**
     * Obtiene el número parcial de votos asignados a Tutin.
     * @return Cantidad de votos acumulados.
     */
    public int getVotosTutin() { return votosTutin; }

    /**
     * Obtiene el número parcial de votos asignados a Chi Ginpin.
     * @return Cantidad de votos acumulados.
     */
    public int getVotosChiGinpin() { return votosChiGinpin; }

    /**
     * Obtiene la suma total de votos procesados en este contenedor de datos.
     * @return Total general de sufragios.
     */
    public int getTotalVotos() { return totalVotos; }
}