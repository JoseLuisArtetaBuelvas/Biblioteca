import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase del caso: una solicitud de prestamo de un libro en la biblioteca.
 *
 * Ciclo de vida del estado:
 *
 *   PENDIENTE --(procesar)--> ENTREGADO
 *       ^                         |
 *       +-------(deshacer)--------+
 *
 *   PENDIENTE --(cancelar)--> CANCELADO
 *
 * El identificador principal es el codigo de prestamo: dos objetos son
 * iguales (equals) si tienen el mismo codigo, sin importar el resto de datos.
 * Las estructuras (Lista, Cola, Pila) usan equals() para buscar, por eso
 * basta con crear un LibroPrestamo con el codigo para encontrar el original.
 */
public class LibroPrestamo {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ENTREGADO = "ENTREGADO";
    public static final String CANCELADO = "CANCELADO";

    private String codigoPrestamo;
    private String tituloLibro;
    private String autor;
    private String nombreLector;
    private String documentoLector;
    private LocalDate fechaSolicitud;
    private String estado;

    /**
     * Crea un prestamo nuevo en estado PENDIENTE con la fecha de hoy.
     */
    public LibroPrestamo(String codigoPrestamo, String tituloLibro, String autor,
                         String nombreLector, String documentoLector) {
        setCodigoPrestamo(codigoPrestamo);
        setTituloLibro(tituloLibro);
        setAutor(autor);
        setNombreLector(nombreLector);
        setDocumentoLector(documentoLector);
        this.fechaSolicitud = LocalDate.now();
        this.estado = PENDIENTE;
    }

    /**
     * Constructor para busquedas: solo se necesita el codigo para comparar con equals().
     */
    public LibroPrestamo(String codigoPrestamo) {
        setCodigoPrestamo(codigoPrestamo);
    }

    public String getCodigoPrestamo() {
        return codigoPrestamo;
    }

    /**
     * El codigo se guarda sin espacios a los lados y en mayusculas, para que
     * "pr-001" y "PR-001 " se consideren el mismo prestamo.
     */
    public void setCodigoPrestamo(String codigoPrestamo) {
        this.codigoPrestamo = textoObligatorio(codigoPrestamo, "codigo de prestamo").toUpperCase();
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = textoObligatorio(tituloLibro, "titulo del libro");
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = textoObligatorio(autor, "autor");
    }

    public String getNombreLector() {
        return nombreLector;
    }

    public void setNombreLector(String nombreLector) {
        this.nombreLector = textoObligatorio(nombreLector, "nombre del lector");
    }

    public String getDocumentoLector() {
        return documentoLector;
    }

    public void setDocumentoLector(String documentoLector) {
        String documento = textoObligatorio(documentoLector, "documento del lector");
        if (!documento.matches("\\d{6,12}")) {
            throw new IllegalArgumentException("El documento del lector debe tener entre 6 y 12 digitos");
        }
        this.documentoLector = documento;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        if (fechaSolicitud == null) {
            throw new IllegalArgumentException("La fecha de solicitud es obligatoria");
        }
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (!PENDIENTE.equals(estado) && !ENTREGADO.equals(estado) && !CANCELADO.equals(estado)) {
            throw new IllegalArgumentException("Estado no valido: " + estado);
        }
        this.estado = estado;
    }

    /**
     * Valida que un texto no sea nulo ni vacio y lo devuelve sin espacios a los lados.
     */
    private static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio");
        }
        return valor.trim();
    }

    /**
     * Dos prestamos son iguales si tienen el mismo codigo de prestamo.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LibroPrestamo)) {
            return false;
        }
        LibroPrestamo otro = (LibroPrestamo) obj;
        return codigoPrestamo.equals(otro.codigoPrestamo);
    }

    /**
     * Se sobrescribe junto con equals() para respetar el contrato de Java:
     * objetos iguales deben tener el mismo hashCode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(codigoPrestamo);
    }

    @Override
    public String toString() {
        return "[" + codigoPrestamo + "] \"" + tituloLibro + "\" de " + autor
                + " | Lector: " + nombreLector + " (" + documentoLector + ")"
                + " | Solicitado: " + fechaSolicitud
                + " | Estado: " + estado;
    }
}
