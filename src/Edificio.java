import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Edificio {
    /**
     * Cola de pisos por atender, a ella acceden todos los ascensores
     */
    private Map<Integer,Integer> solicitudesPorPiso; //( # piso, # personas)

    /**
     * Botones de subida por cada piso del edificio
     */
    private List<Semaphore> botonSubida; // solo botones de subida

    /**
     * Botones de bajada por cada piso del edificio
     */
    private List<Semaphore> botonBajada; //solo botones de bajada

    /**
     * Cantidad de pisos que contiene el edificio
     */
    private int pisos;

    /**
     * Cantidad de ascensores en funcionamiento
     */
    private int ascensores;

    /**
     * Cantidad de personas que seran atendidas
     */
    private int personas;

    /**
     * Cantidad de hilos simultaneos de personas
     */
    private int maxThreadsPersonas;

    /**
     * Cantidad maxima de pasajeros por ascensor
     */
    private int cantMaxPersonas;

    /**
     * @param pisos cantidad de pisos del edificio
     * @param ascensores cantidad de ascensores funcionando
     * @param personas cantidad total de personas
     * @param maxThreadsPersonas cantidad maxima de hilos manejando personas
     * @param cantMaxPersonas cantidad maxima de personas por ascensor
     */
    public Edificio(int pisos, int ascensores, int personas, int maxThreadsPersonas, int cantMaxPersonas){
        this.pisos = pisos;
        this.ascensores = ascensores;
        this.personas = personas;
        this.maxThreadsPersonas = maxThreadsPersonas;
        this.cantMaxPersonas = cantMaxPersonas;
    }

    /**
     * Inicia el programa realmente, implementando los Threads
     */
    public void startSimulation(){

    }

    /**
     * Ejecutable principal del programa
     * @param args en args[0] nombre del archivo a leer
     */
    public static void main (String[] args){
        //primero se lee el archivo .txt
    }

    class Persona extends Thread{
        /**
         * Piso de origen
         */
        private int pisoOrigen;

        /**
         *Piso de destino
         */
        private int pisoDestino;

        /**
         *
         */
        public void llamar_ascensor(){

        }

        @Override
        public void run(){
            //logica de la persona here
        }

    }

    class Ascensor extends Thread{
        /**
         * Se define uno de tres posibles estados de un ascensor
         */
        private static final int SUBIENDO = 1;

        /**
         * Se define uno de tres posibles estados de un ascensor
         */
        private static final int BAJANDO = 2;

        /**
         * Se define uno de tres posibles estados de un ascensor
         */
        private static final int SIN_USAR = 0;

        /**
         * En cual de los tres estados se encuentra el ascensor actualmente
         */
        private int estadoActual;

        /**
         * Piso en el que se encuentra el ascensor
         */
        private int pisoActual;

        /**
         * Control de puertas: abiertas o cerradas
         */
        private Semaphore puertas;

        /**
         * Numero maximo de personas que se pueden transportar
         */
        private int cargaMaxima;

        /**
         * Numero de personas que actualmente transporta el ascensor
         */
        private int cargaActual;

        /**
         * Constructor
         * @param cargaMaxima carga maxima soportada por el ascensor en cuestion
         */
        public Ascensor(int cargaMaxima){
            this.estadoActual = SIN_USAR;
            this.pisoActual = 1; //por convencion, 1 es el piso sobre tierra
            this.puertas = new Semaphore(0); //puertas cerradas por default
            this.cargaMaxima = cargaMaxima;
            this.cargaActual = 0;
        }

        @Override
        public void run(){
            //logica del ascensor here
        }
    }
}
