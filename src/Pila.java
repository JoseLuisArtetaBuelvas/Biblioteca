/**
 * Pila enlazada (LIFO: Last In, First Out - el ultimo en entrar es el primero en salir).
 *
 *   tope
 *   [D]   <- ultimo apilado, primero en salir
 *    |  (derecha apunta al de abajo, izquierda al de arriba)
 *   [C]
 *    |
 *   [B]
 *    |
 *   [A]   <- primero apilado
 *
 * Solo se trabaja sobre el TOPE, por eso apilar y desapilar son O(1).
 *
 * En la biblioteca guarda el HISTORIAL DE PRESTAMOS ENTREGADOS. Como la ultima
 * entrega queda en el tope, deshacer la ultima entrega es simplemente desapilar.
 */
public class Pila {

    private Nodo tope;
    private int tamanio;

    public Pila() {
        tope = null;
        tamanio = 0;
    }

    /**
     * Coloca un dato encima del tope.
     */
    public void apilar(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (!esVacia()) {
            nuevo.setDerecha(tope);   // el nuevo queda encima del tope actual
            tope.setIzquierda(nuevo);
        }
        tope = nuevo;
        tamanio++;
    }

    /**
     * Retira y devuelve el dato del tope.
     *
     * @throws IllegalStateException si la pila esta vacia
     */
    public Object desapilar() {
        if (esVacia()) {
            throw new IllegalStateException("La pila esta vacia, no hay elementos en el historial");
        }
        Object dato = tope.getDato();
        tope = tope.getDerecha();
        if (tope != null) {
            tope.setIzquierda(null);
        }
        tamanio--;
        return dato;
    }

    /**
     * Consulta el dato del tope sin retirarlo.
     *
     * @throws IllegalStateException si la pila esta vacia
     */
    public Object peek() {
        if (esVacia()) {
            throw new IllegalStateException("La pila esta vacia, no hay elementos en el historial");
        }
        return tope.getDato();
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean esVacia() {
        return tope == null;
    }

    public boolean contiene(Object dato) {
        return buscar(dato) != -1;
    }

    /**
     * Busca un dato y devuelve su distancia al tope, empezando en 1
     * (1 = esta en el tope). Devuelve -1 si no esta en la pila.
     * Es el mismo criterio que usa java.util.Stack.search().
     */
    public int buscar(Object dato) {
        Nodo actual = tope;
        int posicion = 1;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return posicion;
            }
            actual = actual.getDerecha();
            posicion++;
        }
        return -1;
    }

    public void limpiar() {
        tope = null;
        tamanio = 0;
    }

    /**
     * Muestra la pila desde el tope (lo mas reciente) hacia el fondo.
     */
    public String mostrar() {
        if (esVacia()) {
            return "(pila vacia)";
        }
        StringBuilder sb = new StringBuilder();
        Nodo actual = tope;
        int posicion = 1;
        while (actual != null) {
            sb.append(posicion).append(". ").append(actual.getDato());
            if (posicion == 1) {
                sb.append("   <- tope");
            }
            sb.append('\n');
            actual = actual.getDerecha();
            posicion++;
        }
        return sb.toString();
    }
}
