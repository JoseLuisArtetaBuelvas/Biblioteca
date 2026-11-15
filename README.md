# Biblioteca - Gestion de prestamos con Lista, Cola y Pila

Actividad Unidad 2 de Estructuras de Datos (Universidad de Cartagena).
Ejercicio 3: Biblioteca. Entidad principal `LibroPrestamo`, identificada por el codigo de prestamo.

Aplicacion Java de consola que usa estructuras de datos propias basadas en nodos doblemente enlazados
(referencias `izquierda` y `derecha`), sin usar las colecciones de `java.util`.

## Uso de cada estructura

| Estructura | Variable | Uso en la biblioteca |
|---|---|---|
| `Lista` | `listaGeneral` | Registro de **todos** los prestamos, sin importar su estado |
| `Cola` | `colaPendientes` | Solicitudes que esperan la entrega del libro, atendidas en orden de llegada (FIFO) |
| `Pila` | `historialEntregados` | Prestamos entregados; el tope es la ultima entrega y permite deshacerla (LIFO) |

Estados de un prestamo: `PENDIENTE` -> `ENTREGADO` (procesar), `ENTREGADO` -> `PENDIENTE` (deshacer),
`PENDIENTE` -> `CANCELADO` (cancelar).

## Estructura del proyecto

```
src/
  Nodo.java               Nodo doblemente enlazado (dato, izquierda, derecha)
  Lista.java              Lista doblemente enlazada
  Cola.java               Cola FIFO
  Pila.java               Pila LIFO
  LibroPrestamo.java      Clase del caso (entidad)
  SistemaBiblioteca.java  Logica del sistema y menu (clase main)
test/
  PruebasBiblioteca.java  Pruebas sin librerias externas
```

## Compilar y ejecutar

Requiere JDK 17 o superior. Desde la raiz del proyecto, en PowerShell:

```powershell
javac -encoding UTF-8 -d out src/*.java
java -cp out SistemaBiblioteca
```

## Ejecutar las pruebas

```powershell
javac -encoding UTF-8 -d out src/*.java test/*.java
java -cp out PruebasBiblioteca
```

## Menu

```
 1. Registrar prestamo
 2. Ver todos los prestamos registrados
 3. Ver prestamos pendientes
 4. Procesar siguiente prestamo (entregar libro)
 5. Ver historial de prestamos entregados
 6. Buscar prestamo por codigo
 7. Cancelar prestamo pendiente
 8. Deshacer ultima entrega
 9. Ver cantidad de prestamos
10. Salir
```

## Autor

Jose Luis Arteta Buelvas - Ingenieria de Software, Universidad de Cartagena.
