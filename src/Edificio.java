import java.util.*;
import java.util.concurrent.Semaphore;

public class Edificio {
    /**
     * Cola de solicitudes por atender, a ella acceden todos los ascensores
     */
    private Map<Integer,Integer> solicitudesPorPisoSubida; //( # piso, # personas)

    /**
     * Cola de  solicitudes por atender, a ella acceden todos los ascensores
     */
    private Map<Integer,Integer> solicitudesPorPisoBajada; //( # piso, # personas)

    /**
     * Controla que varios ascensores no consulten la misma solicitud
     */
    private Semaphore consultaSolicitudes;

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
        this.botonBajada = new ArrayList<>(pisos);
        this.botonSubida = new ArrayList<>(pisos);
        this.solicitudesPorPisoSubida = new HashMap<>();
        this.solicitudesPorPisoBajada = new HashMap<>();
        for(int i=1; i <= pisos; ++i){
            //se inicializan todas las solicitudes por piso
            this.solicitudesPorPisoSubida.put(i,0);
            this.solicitudesPorPisoBajada.put(i,0);
            //se inicializan todos los botones por piso
            this.botonSubida.add(i,new Semaphore(0));
            this.botonBajada.add(i,new Semaphore(0));
        }
        this.consultaSolicitudes = new Semaphore(1);
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
         *Piso de destino
         */
        private int pisoDestino;

        /**
         * Piso actual
         */
        private int pisoActual;

        /**
         * Constructor
         * @param pisoOrigen piso de origen
         * @param pisoDestino piso de destino
         */
        public Persona(int pisoOrigen, int pisoDestino){
            this.pisoDestino = pisoDestino;
            this.pisoActual = pisoOrigen; //parametro irá variando conforme avance entre pisos
        }

        /**
         * Retorna el piso en el que se encuentra la persona
         * @return piso actual de la persona
         */
        public int getPisoActual() {
            return pisoActual;
        }

        /**
         * Retorna el piso al que se dirige la persona
         * @return piso de destino de la persona
         */
        public int getPisoDestino() {
            return pisoDestino;
        }

        /**
         * Permite cambiar el piso actual en el que se encuentra la persona
         * @param pisoActual nuevo piso
         */
        public void setPisoActual(int pisoActual) {
            this.pisoActual = pisoActual;
        }

        /**
         * Oprime un boton de subida o bajada según corresponda
         */
        public void llamar_ascensor(){
            try {
                if (this.pisoActual< this.pisoDestino){
                    botonSubida.get(this.pisoActual).acquire();
                }else{
                    botonBajada.get(this.pisoActual).acquire();
                }
            } catch (Exception e){
                System.out.println("Ha ocurrido un error con los botones del piso : " + this.pisoActual);
            }
        }

        /**
         * Permite a la persona entrar dentro de un ascensor
         */
        public void entrar_ascensor(){
            System.out.println("Ingresé al ascensor en el piso: " + this.pisoActual);
        }

        /**
         * Permite a la persona salir del ascensor
         */
        public void salir_ascensor(){
            System.out.println("Salí del ascensor en el piso: " + this.pisoActual);
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
         * Piso que será atendido por el ascensor
         */
        private int atendiendoPiso;

        /**
         * Cantidad de personas que seran atendidas por el ascensor
         */
        private int atendiendoPersonas;

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
            this.atendiendoPiso = 0;
            this.atendiendoPersonas = 0;
        }

        /**
         * Consulta si hay solicitudes pendientes por atender, en cuyo caso libera la cola de solicitudes
         * @return true si se obtiene una solicitud para atender
         */
        public boolean realizar_consulta() {
            try {
                consultaSolicitudes.acquire(); //espera su turno de consulta, evita que varios ascensores atiendan la misma
                if (estadoActual == SIN_USAR) {
                    int i = 1;
                    while (estadoActual == SIN_USAR) {
                        if (solicitudesPorPisoSubida.get(i) != 0) {
                            this.estadoActual = SUBIENDO;
                            this.atendiendoPiso = i;
                            if (solicitudesPorPisoSubida.get(i) <= this.cargaMaxima) {
                                this.atendiendoPersonas =
                                        solicitudesPorPisoSubida.replace(i, 0);
                            } else {
                                this.atendiendoPersonas =
                                        solicitudesPorPisoSubida.replace(i, solicitudesPorPisoSubida.get(i) - this.cargaMaxima);
                            }
                            return true;
                        } else if (solicitudesPorPisoBajada.get(i) != 0) {
                            this.estadoActual = BAJANDO;
                            this.atendiendoPiso = i;
                            if (solicitudesPorPisoBajada.get(i) <= this.cargaMaxima) {
                                this.atendiendoPersonas =
                                        solicitudesPorPisoBajada.replace(i, 0);
                            } else {
                                this.atendiendoPersonas =
                                        solicitudesPorPisoBajada.replace(i, solicitudesPorPisoBajada.get(i) - this.cargaMaxima);
                            }
                            return true;
                        }
                        i = (i < 5) ? i++ : 1; //manejo de la variable de bucle
                    }
                } else if (this.estadoActual == SUBIENDO) {
                    if ((this.pisoActual < pisos) && (solicitudesPorPisoSubida.get(this.pisoActual+1) != 0)){

                    }
                } else { //es decir, estadoActual = BAJANDO

                }
                consultaSolicitudes.release(1); //hecha la consulta, se libera el Semaphore
            } catch (Exception e) {

            }
            return false;
        }


        @Override
        public void run(){
            //logica del ascensor here
        }
    }
}
