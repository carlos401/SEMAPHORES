import java.util.*;
import java.util.concurrent.Semaphore;

//cambios en el codigo
public class Edificio {
    /**
     * Cola de solicitudes por atender, a ella acceden todos los ascensores
     */
    private Map<Integer,Boolean> solicitudesPorPisoSubida; //( # piso, # solicitud)

    /**
     * Cola de  solicitudes por atender, a ella acceden todos los ascensores
     */
    private Map<Integer,Boolean> solicitudesPorPisoBajada; //( # piso, # solicitud)

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
            this.solicitudesPorPisoSubida.put(i,false);
            this.solicitudesPorPisoBajada.put(i,false);
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

    /**
     * Estados permitidos de un ascensor
     */
    enum Estado{
        SUBIENDO, BAJANDO, DESOCUPADO
    }

    class Ascensor extends Thread{
        /**
         * En cual de los tres estados se encuentra el ascensor actualmente
         */
        private Estado estadoActual;

        /**
         * Piso en el que se encuentra el ascensor
         */
        private int pisoActual;

        /**
         * Lista de pisos en los que debe parar el ascensor
         */
        private List<Integer> rutaDePisos;

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
            this.estadoActual = Estado.DESOCUPADO;
            this.pisoActual = 1; //por convencion, 1 es el piso sobre tierra
            this.puertas = new Semaphore(0); //puertas cerradas por default
            this.cargaMaxima = cargaMaxima;
            this.cargaActual = 0;
            this.rutaDePisos = new ArrayList<>();
        }

        /**
         * Consulta si hay solicitudes pendientes por atender, en cuyo caso libera la cola de solicitudes
         * @return true si se obtiene una solicitud para atender
         */
        public boolean realizar_consulta() {
            try {
                consultaSolicitudes.acquire(); //espera su turno de consulta, evita que varios ascensores atiendan la misma
                if (estadoActual == Estado.DESOCUPADO) {
                    int i = 1;
                    while (this.estadoActual == Estado.DESOCUPADO) {
                        if (solicitudesPorPisoSubida.get(i)) { //comprobamos las solicitudes de subida
                            this.estadoActual = Estado.SUBIENDO;
                            solicitudesPorPisoSubida.replace(i, false);
                            this.rutaDePisos.add(i);
                            consultaSolicitudes.release(1); //hecha la consulta, se libera el Semaphore
                            return true;
                        } else if (solicitudesPorPisoBajada.get(i)) { //si nadie sube, comprobamos bajadas
                            this.estadoActual = Estado.BAJANDO;
                            solicitudesPorPisoBajada.replace(i,false);
                            this.rutaDePisos.add(i);
                            consultaSolicitudes.release(1); //hecha la consulta, se libera el Semaphore
                            return true;
                        }
                        i = (i < 5) ? i++ : 1; //manejo de la variable de bucle
                    }
                } else if ((this.estadoActual == Estado.SUBIENDO) && (this.pisoActual < pisos)
                        && (solicitudesPorPisoSubida.get(this.pisoActual+1))){
                    solicitudesPorPisoSubida.replace(this.pisoActual+1, false);
                    this.rutaDePisos.add(this.pisoActual+1);
                    consultaSolicitudes.release(1); //hecha la consulta, se libera el Semaphore
                    return true;
                } else if ((this.estadoActual == Estado.BAJANDO) && (this.pisoActual > 1)
                        &&(solicitudesPorPisoBajada.get(this.pisoActual-1))){
                    solicitudesPorPisoBajada.replace(this.pisoActual-1,false);
                    this.rutaDePisos.add(this.pisoActual-1);
                    consultaSolicitudes.release(1); //hecha la consulta, se libera el Semaphore
                    return true;
                }
                consultaSolicitudes.release(1); //hecha la consulta, se libera el Semaphore
                return false;
            } catch (Exception e) {
                System.out.println("Error realizando las consultas");
                return false;
            }
        }

        @Override
        public void run(){
            //logica del ascensor here
        }
    }
}
