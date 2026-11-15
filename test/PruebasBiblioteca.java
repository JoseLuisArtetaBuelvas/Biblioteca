/**
 * Pruebas de las estructuras y del sistema, sin librerias externas.
 *
 * Compilar y ejecutar desde la raiz del proyecto:
 *   javac -encoding UTF-8 -d out src/*.java test/*.java
 *   java -cp out PruebasBiblioteca
 *
 * Cada prueba imprime OK o FALLA; al final se muestra el resumen y,
 * si alguna fallo, el programa termina con codigo 1.
 */
public class PruebasBiblioteca {

    private static int exitosas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        probarLista();
        probarCola();
        probarPila();
        probarLibroPrestamo();
        probarSistema();

        System.out.println();
        System.out.println("Resultado: " + exitosas + " exitosas, " + fallidas + " fallidas");
        if (fallidas > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------
    // Lista
    // ------------------------------------------------------------------

    private static void probarLista() {
        System.out.println("== Lista ==");
        Lista lista = new Lista();
        verificar("lista nueva vacia", lista.esVacia() && lista.cuentaElementos() == 0);

        lista.agregar("B");
        lista.agregar("D");
        lista.agregarAlInicio("A");
        lista.agregarEnPosicion(2, "C");
        lista.agregarEnPosicion(4, "E");
        verificar("orden tras insertar", "1. A\n2. B\n3. C\n4. D\n5. E\n".equals(lista.mostrarAdelante()));
        verificar("recorrido hacia atras", "5. E\n4. D\n3. C\n2. B\n1. A\n".equals(lista.mostrarAtras()));
        verificar("cuenta 5 elementos", lista.cuentaElementos() == 5);
        verificar("buscar por indice", "C".equals(lista.buscarDato(2)) && "E".equals(lista.buscarDato(4)));
        verificar("buscar por dato", "D".equals(lista.buscarDato("D")) && lista.buscarDato("Z") == null);
        verificar("contiene", lista.contiene("A") && !lista.contiene("Z"));

        verificar("eliminar en posicion intermedia", "C".equals(lista.eliminarEnPosicion(2)));
        verificar("eliminar primero", "A".equals(lista.eliminarPrimero()));
        verificar("eliminar ultimo", "E".equals(lista.eliminarUltimo()));
        // Despues de eliminar, los enlaces deben seguir bien en ambos sentidos.
        verificar("enlaces tras eliminar", "1. B\n2. D\n".equals(lista.mostrarAdelante())
                && "2. D\n1. B\n".equals(lista.mostrarAtras()));

        lanzaExcepcion("indice negativo", IndexOutOfBoundsException.class, () -> lista.buscarDato(-1));
        lanzaExcepcion("indice igual al tamanio", IndexOutOfBoundsException.class, () -> lista.eliminarEnPosicion(2));
        lanzaExcepcion("insertar fuera de rango", IndexOutOfBoundsException.class, () -> lista.agregarEnPosicion(5, "X"));

        lista.eliminarPrimero();
        lista.eliminarPrimero();
        verificar("vacia tras eliminar todo", lista.esVacia() && "(lista vacia)".equals(lista.mostrarAdelante()));
        lanzaExcepcion("eliminar en lista vacia", IllegalStateException.class, lista::eliminarUltimo);

        lista.agregar("X");
        lista.agregar("Y");
        lista.limpiar();
        verificar("limpiar", lista.esVacia() && lista.cuentaElementos() == 0);
        lista.agregar("Z");
        verificar("se puede reutilizar tras limpiar", "1. Z\n".equals(lista.mostrarAtras()));
    }

    // ------------------------------------------------------------------
    // Cola
    // ------------------------------------------------------------------

    private static void probarCola() {
        System.out.println("== Cola ==");
        Cola cola = new Cola();
        verificar("cola nueva vacia", cola.esVacia() && cola.tamanio() == 0);
        lanzaExcepcion("desencolar cola vacia", IllegalStateException.class, cola::desencolar);
        lanzaExcepcion("peek cola vacia", IllegalStateException.class, cola::peek);

        cola.encolar(1);
        cola.encolar(2);
        cola.encolar(3);
        verificar("peek no retira", cola.peek().equals(1) && cola.tamanio() == 3);
        verificar("FIFO: sale el primero", cola.desencolar().equals(1));
        verificar("FIFO: luego el segundo", cola.desencolar().equals(2));
        verificar("contiene", cola.contiene(3) && !cola.contiene(1));
        cola.encolar(4);
        verificar("mostrar desde el frente", "1. 3\n2. 4\n".equals(cola.mostrar()));
        cola.desencolar();
        cola.desencolar();
        verificar("vacia tras desencolar todo", cola.esVacia());
        cola.encolar(5);
        verificar("reutilizable tras vaciarse", cola.peek().equals(5) && cola.tamanio() == 1);
        cola.limpiar();
        verificar("limpiar", cola.esVacia() && "(cola vacia)".equals(cola.mostrar()));
    }

    // ------------------------------------------------------------------
    // Pila
    // ------------------------------------------------------------------

    private static void probarPila() {
        System.out.println("== Pila ==");
        Pila pila = new Pila();
        verificar("pila nueva vacia", pila.esVacia() && pila.tamanio() == 0);
        lanzaExcepcion("desapilar pila vacia", IllegalStateException.class, pila::desapilar);

        pila.apilar("A");
        pila.apilar("B");
        pila.apilar("C");
        verificar("peek ve el tope", "C".equals(pila.peek()) && pila.tamanio() == 3);
        verificar("buscar distancia al tope", pila.buscar("C") == 1 && pila.buscar("A") == 3 && pila.buscar("Z") == -1);
        verificar("LIFO: sale el ultimo", "C".equals(pila.desapilar()));
        verificar("LIFO: luego el penultimo", "B".equals(pila.desapilar()));
        verificar("contiene", pila.contiene("A") && !pila.contiene("B"));
        pila.desapilar();
        verificar("vacia tras desapilar todo", pila.esVacia());
        pila.apilar("D");
        pila.limpiar();
        verificar("limpiar", pila.esVacia() && "(pila vacia)".equals(pila.mostrar()));
    }

    // ------------------------------------------------------------------
    // LibroPrestamo
    // ------------------------------------------------------------------

    private static void probarLibroPrestamo() {
        System.out.println("== LibroPrestamo ==");
        LibroPrestamo p = prestamo("pr-001 ");
        verificar("codigo normalizado", "PR-001".equals(p.getCodigoPrestamo()));
        verificar("estado inicial PENDIENTE", LibroPrestamo.PENDIENTE.equals(p.getEstado()));
        verificar("equals por codigo", p.equals(new LibroPrestamo("PR-001")) && !p.equals(prestamo("PR-002")));
        verificar("hashCode coherente con equals", p.hashCode() == new LibroPrestamo("pr-001").hashCode());
        lanzaExcepcion("titulo vacio", IllegalArgumentException.class,
                () -> new LibroPrestamo("PR-9", " ", "Autor", "Lector", "1047000111"));
        lanzaExcepcion("documento con letras", IllegalArgumentException.class,
                () -> new LibroPrestamo("PR-9", "Libro", "Autor", "Lector", "10A47"));
        lanzaExcepcion("estado invalido", IllegalArgumentException.class, () -> p.setEstado("PERDIDO"));
    }

    // ------------------------------------------------------------------
    // SistemaBiblioteca
    // ------------------------------------------------------------------

    private static void probarSistema() {
        System.out.println("== SistemaBiblioteca ==");
        SistemaBiblioteca s = new SistemaBiblioteca();
        s.registrar(prestamo("PR-001"));
        s.registrar(prestamo("PR-002"));
        s.registrar(prestamo("PR-003"));
        verificar("registro va a lista y cola",
                s.getListaGeneral().cuentaElementos() == 3 && s.getColaPendientes().tamanio() == 3);
        lanzaExcepcion("codigo duplicado", IllegalArgumentException.class, () -> s.registrar(prestamo("pr-002")));

        LibroPrestamo entregado = s.procesarSiguiente();
        verificar("procesa en orden de llegada", "PR-001".equals(entregado.getCodigoPrestamo()));
        verificar("procesado queda ENTREGADO y en la pila",
                LibroPrestamo.ENTREGADO.equals(entregado.getEstado()) && s.getHistorialEntregados().peek() == entregado);
        verificar("el estado se ve desde la lista (misma referencia)",
                LibroPrestamo.ENTREGADO.equals(s.buscarPorCodigo("pr-001").getEstado()));

        s.registrar(prestamo("PR-004"));
        LibroPrestamo cancelado = s.cancelarPendiente("PR-003");
        verificar("cancelado queda CANCELADO", LibroPrestamo.CANCELADO.equals(cancelado.getEstado()));
        verificar("cola conserva el orden tras cancelar",
                s.getColaPendientes().mostrar().startsWith("1. [PR-002]")
                        && s.getColaPendientes().mostrar().contains("2. [PR-004]")
                        && s.getColaPendientes().tamanio() == 2);
        verificar("cancelado sigue en la lista general", s.buscarPorCodigo("PR-003") != null);
        lanzaExcepcion("cancelar uno ya cancelado", IllegalArgumentException.class, () -> s.cancelarPendiente("PR-003"));
        lanzaExcepcion("cancelar uno entregado", IllegalArgumentException.class, () -> s.cancelarPendiente("PR-001"));
        lanzaExcepcion("cancelar codigo inexistente", IllegalArgumentException.class, () -> s.cancelarPendiente("NO-EXISTE"));

        LibroPrestamo devuelto = s.deshacerUltimaEntrega();
        verificar("deshacer devuelve el ultimo entregado", "PR-001".equals(devuelto.getCodigoPrestamo()));
        verificar("deshacer lo vuelve PENDIENTE al final de la cola",
                LibroPrestamo.PENDIENTE.equals(devuelto.getEstado())
                        && s.getColaPendientes().mostrar().contains("3. [PR-001]"));
        lanzaExcepcion("deshacer sin historial", IllegalStateException.class, s::deshacerUltimaEntrega);

        s.procesarSiguiente();
        verificar("conteo de estados", s.getListaGeneral().cuentaElementos() == 4
                && s.getColaPendientes().tamanio() == 2
                && s.getHistorialEntregados().tamanio() == 1
                && s.contarCancelados() == 1);

        s.procesarSiguiente();
        s.procesarSiguiente();
        lanzaExcepcion("procesar sin pendientes", IllegalStateException.class, s::procesarSiguiente);
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    private static LibroPrestamo prestamo(String codigo) {
        return new LibroPrestamo(codigo, "Libro " + codigo, "Autor", "Lector", "1047000111");
    }

    private static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            exitosas++;
            System.out.println("  OK    " + nombre);
        } else {
            fallidas++;
            System.out.println("  FALLA " + nombre);
        }
    }

    private static void lanzaExcepcion(String nombre, Class<? extends Exception> esperada, Runnable accion) {
        try {
            accion.run();
            verificar(nombre + " (no lanzo excepcion)", false);
        } catch (Exception e) {
            verificar(nombre + " -> " + e.getClass().getSimpleName(), esperada.isInstance(e));
        }
    }
}
