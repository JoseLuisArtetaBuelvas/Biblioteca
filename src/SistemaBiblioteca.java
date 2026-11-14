import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Sistema de gestion de prestamos de la biblioteca (clase de ejecucion y menu).
 *
 * Usa las tres estructuras propias:
 *  - listaGeneral:        Lista con TODOS los prestamos registrados (registro historico completo).
 *  - colaPendientes:      Cola con las solicitudes que esperan que se entregue el libro (FIFO).
 *  - historialEntregados: Pila con los prestamos ya entregados; el tope es la ultima entrega (LIFO).
 *
 * Un mismo objeto LibroPrestamo puede estar referenciado a la vez desde la lista
 * y desde la cola o la pila: no se copian datos, se comparten referencias. Por eso
 * cuando cambia el estado de un prestamo, el cambio se ve desde todas las estructuras.
 *
 * La logica (registrar, procesar, ...) esta separada de la lectura por teclado
 * (metodos "opcion..."), para poder probarla sin usar el menu.
 */
public class SistemaBiblioteca {

    private final Lista listaGeneral;
    private final Cola colaPendientes;
    private final Pila historialEntregados;

    public SistemaBiblioteca() {
        listaGeneral = new Lista();
        colaPendientes = new Cola();
        historialEntregados = new Pila();
    }

    // =====================================================================
    // Logica del sistema
    // =====================================================================

    /**
     * Registra un prestamo: se guarda en la lista general y entra a la cola
     * de pendientes, en ese orden, como exige la regla de registro.
     *
     * @throws IllegalArgumentException si ya existe un prestamo con el mismo codigo
     */
    public void registrar(LibroPrestamo prestamo) {
        if (prestamo == null) {
            throw new IllegalArgumentException("El prestamo no puede ser nulo");
        }
        if (listaGeneral.contiene(prestamo)) {
            throw new IllegalArgumentException(
                    "Ya existe un prestamo con el codigo " + prestamo.getCodigoPrestamo());
        }
        prestamo.setEstado(LibroPrestamo.PENDIENTE);
        listaGeneral.agregar(prestamo);
        colaPendientes.encolar(prestamo);
    }

    /**
     * Procesa (entrega) el prestamo que lleva mas tiempo esperando:
     * sale del frente de la cola y queda en el tope de la pila de historial.
     *
     * @return el prestamo entregado
     * @throws IllegalStateException si no hay prestamos pendientes
     */
    public LibroPrestamo procesarSiguiente() {
        if (colaPendientes.esVacia()) {
            throw new IllegalStateException("No hay prestamos pendientes por procesar");
        }
        LibroPrestamo procesado = (LibroPrestamo) colaPendientes.desencolar();
        procesado.setEstado(LibroPrestamo.ENTREGADO);
        historialEntregados.apilar(procesado);
        return procesado;
    }

    /**
     * Busca un prestamo en la lista general por su codigo. Se crea un
     * LibroPrestamo "de busqueda" solo con el codigo: como equals() compara
     * por codigo, la lista encuentra el objeto original con todos sus datos.
     *
     * @return el prestamo encontrado o null si no existe
     */
    public LibroPrestamo buscarPorCodigo(String codigo) {
        return (LibroPrestamo) listaGeneral.buscarDato(new LibroPrestamo(codigo));
    }

    /**
     * Cancela un prestamo que todavia esta pendiente.
     *
     * Una cola solo permite sacar por el frente, asi que NO se elimina el nodo
     * del centro directamente. Se usa una cola auxiliar:
     *  1. Se desencolan todos los pendientes.
     *  2. Los que no son el buscado se encolan en la auxiliar (conservan su orden).
     *  3. El buscado se marca CANCELADO y no se vuelve a encolar.
     *  4. Se devuelven los elementos de la auxiliar a la cola original.
     * El prestamo cancelado sigue en la lista general como registro.
     *
     * @return el prestamo cancelado
     * @throws IllegalArgumentException si el codigo no existe o el prestamo no esta pendiente
     */
    public LibroPrestamo cancelarPendiente(String codigo) {
        LibroPrestamo prestamo = buscarPorCodigo(codigo);
        if (prestamo == null) {
            throw new IllegalArgumentException("No existe un prestamo con el codigo " + codigo.trim().toUpperCase());
        }
        if (!LibroPrestamo.PENDIENTE.equals(prestamo.getEstado())) {
            throw new IllegalArgumentException("El prestamo " + prestamo.getCodigoPrestamo()
                    + " no se puede cancelar porque su estado es " + prestamo.getEstado());
        }

        Cola colaAuxiliar = new Cola();
        LibroPrestamo cancelado = null;
        while (!colaPendientes.esVacia()) {
            LibroPrestamo actual = (LibroPrestamo) colaPendientes.desencolar();
            if (actual.equals(prestamo)) {
                cancelado = actual;
            } else {
                colaAuxiliar.encolar(actual);
            }
        }
        while (!colaAuxiliar.esVacia()) {
            colaPendientes.encolar(colaAuxiliar.desencolar());
        }

        cancelado.setEstado(LibroPrestamo.CANCELADO);
        return cancelado;
    }

    public Lista getListaGeneral() {
        return listaGeneral;
    }

    public Cola getColaPendientes() {
        return colaPendientes;
    }

    public Pila getHistorialEntregados() {
        return historialEntregados;
    }

    // =====================================================================
    // Menu e interaccion con el usuario
    // =====================================================================

    public static void main(String[] args) {
        SistemaBiblioteca sistema = new SistemaBiblioteca();
        sistema.ejecutarMenu(new Scanner(System.in));
    }

    public void ejecutarMenu(Scanner entrada) {
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            try {
                int opcion = leerEntero(entrada, "Seleccione una opcion: ");
                System.out.println();
                continuar = ejecutarOpcion(opcion, entrada);
            } catch (NoSuchElementException e) {
                // Se acabo la entrada (por ejemplo, Ctrl+Z / Ctrl+D): se termina sin error.
                continuar = false;
            }
        }
        System.out.println("Sistema finalizado. Hasta pronto.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("========= BIBLIOTECA - GESTION DE PRESTAMOS =========");
        System.out.println(" 1. Registrar prestamo");
        System.out.println(" 2. Ver todos los prestamos registrados");
        System.out.println(" 3. Ver prestamos pendientes");
        System.out.println(" 4. Procesar siguiente prestamo (entregar libro)");
        System.out.println(" 5. Ver historial de prestamos entregados");
        System.out.println(" 6. Buscar prestamo por codigo");
        System.out.println(" 7. Cancelar prestamo pendiente");
        System.out.println(" 8. Deshacer ultima entrega");
        System.out.println(" 9. Ver cantidad de prestamos");
        System.out.println("10. Salir");
        System.out.println("=====================================================");
    }

    /**
     * Ejecuta la opcion elegida. Los errores de validacion o de estructuras
     * vacias se capturan aqui, se muestran al usuario y el menu continua.
     *
     * @return false si el usuario eligio salir
     */
    private boolean ejecutarOpcion(int opcion, Scanner entrada) {
        try {
            switch (opcion) {
                case 1:
                    opcionRegistrar(entrada);
                    break;
                case 2:
                    System.out.println("--- Prestamos registrados (" + listaGeneral.cuentaElementos() + ") ---");
                    System.out.print(listaGeneral.mostrarAdelante());
                    break;
                case 3:
                    System.out.println("--- Prestamos pendientes, en orden de atencion ("
                            + colaPendientes.tamanio() + ") ---");
                    System.out.print(colaPendientes.mostrar());
                    break;
                case 4:
                    opcionProcesar();
                    break;
                case 5:
                    System.out.println("--- Historial de entregas, de la mas reciente a la mas antigua ("
                            + historialEntregados.tamanio() + ") ---");
                    System.out.print(historialEntregados.mostrar());
                    break;
                case 6:
                    opcionBuscar(entrada);
                    break;
                case 7:
                    opcionCancelar(entrada);
                    break;
                case 10:
                    return false;
                default:
                    System.out.println("Opcion no valida. Elija un numero entre 1 y 10.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return true;
    }

    private void opcionRegistrar(Scanner entrada) {
        System.out.println("--- Registrar prestamo ---");
        String codigo = leerTexto(entrada, "Codigo del prestamo (ej. PR-001): ");
        if (listaGeneral.contiene(new LibroPrestamo(codigo))) {
            // Se valida antes de pedir el resto de datos para no hacer escribir de mas.
            throw new IllegalArgumentException("Ya existe un prestamo con el codigo " + codigo.toUpperCase());
        }
        String titulo = leerTexto(entrada, "Titulo del libro: ");
        String autor = leerTexto(entrada, "Autor: ");
        String lector = leerTexto(entrada, "Nombre del lector: ");
        String documento = leerTexto(entrada, "Documento del lector: ");

        LibroPrestamo prestamo = new LibroPrestamo(codigo, titulo, autor, lector, documento);
        registrar(prestamo);
        System.out.println("Prestamo registrado y enviado a la cola de pendientes:");
        System.out.println("  " + prestamo);
        System.out.println("  Posicion en la cola: " + colaPendientes.tamanio());
    }

    private void opcionProcesar() {
        LibroPrestamo entregado = procesarSiguiente();
        System.out.println("Libro entregado al lector:");
        System.out.println("  " + entregado);
        System.out.println("  Pendientes restantes: " + colaPendientes.tamanio());
    }

    private void opcionBuscar(Scanner entrada) {
        System.out.println("--- Buscar prestamo ---");
        String codigo = leerTexto(entrada, "Codigo del prestamo: ");
        LibroPrestamo encontrado = buscarPorCodigo(codigo);
        if (encontrado == null) {
            System.out.println("No existe un prestamo con el codigo " + codigo.toUpperCase());
            return;
        }
        System.out.println("Prestamo encontrado:");
        System.out.println("  " + encontrado);
        // Ademas se indica en que estructura esta actualmente.
        if (colaPendientes.contiene(encontrado)) {
            System.out.println("  Esta en la cola de pendientes.");
        } else if (historialEntregados.contiene(encontrado)) {
            System.out.println("  Esta en el historial de entregas, posicion "
                    + historialEntregados.buscar(encontrado) + " desde el tope.");
        } else {
            System.out.println("  Solo esta en el registro general (fue cancelado).");
        }
    }

    private void opcionCancelar(Scanner entrada) {
        System.out.println("--- Cancelar prestamo pendiente ---");
        if (colaPendientes.esVacia()) {
            System.out.println("No hay prestamos pendientes para cancelar.");
            return;
        }
        System.out.print(colaPendientes.mostrar());
        String codigo = leerTexto(entrada, "Codigo del prestamo a cancelar: ");
        LibroPrestamo cancelado = cancelarPendiente(codigo);
        System.out.println("Prestamo cancelado:");
        System.out.println("  " + cancelado);
        System.out.println("  Pendientes restantes: " + colaPendientes.tamanio());
    }

    // ---------------------------------------------------------------------
    // Lectura de datos
    // ---------------------------------------------------------------------

    /**
     * Lee un texto no vacio; repite la pregunta mientras el usuario deje el campo en blanco.
     */
    private String leerTexto(Scanner entrada, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("  El campo no puede estar vacio.");
        }
    }

    /**
     * Lee un numero entero; si el usuario escribe letras, se captura
     * NumberFormatException y se vuelve a preguntar.
     */
    private int leerEntero(Scanner entrada, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("  Debe escribir un numero entero.");
            }
        }
    }
}
