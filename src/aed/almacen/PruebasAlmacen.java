package aed.almacen;
import es.upm.aedlib.indexedlist.IndexedList;
import es.upm.aedlib.indexedlist.ArrayIndexedList;

import java.util.Comparator;
import java.util.function.Function;
//import java.util.function.Predicate;


public class PruebasAlmacen {

	 public static <E, F extends Comparable<F>> int busquedaBinaria(IndexedList<E> l, F id, Function<E, F> f) {
	        int inicio = 0;
	        int fin = l.size() - 1;
	        
	        while (inicio <= fin) {
	            int medio = (inicio + fin) / 2;
	            E elementoMedio = l.get(medio);
	            F idMedio = f.apply(elementoMedio); // Usamos la función f para obtener la identidad del elemento

	            int comparacion = id.compareTo(idMedio);

	            if (comparacion == 0) {
	                return medio; // Identidad encontrada
	            } else if (comparacion > 0) {
	                inicio = medio + 1; // Continuar en la mitad derecha
	            } else {
	                fin = medio - 1; // Continuar en la mitad izquierda
	            }
	        }

	        // Si no se encontró la identidad, devolver el índice donde se debería insertar
	        return inicio;
	    }
  
   public static void ordenarProductosPorId(IndexedList<Producto> productos) {
        int n = productos.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
            	Producto productoMedio = productos.get(j);
                // Comparar los productId de productos adyacentes
                if (productoMedio.getProductoId().compareTo(productos.get(j + 1).getProductoId()) > 0) {
                    // Intercambiar si están en el orden incorrecto
                    Producto temp = productos.get(j);
                    productos.set(j, productos.get(j + 1));
                    productos.set(j + 1, temp);
                }
            }
        }
    }
    
    public static <T> void ordenarPorId(IndexedList<T> lista, Comparator<T> comparador) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // Comparar los productId de productos adyacentes
                if (comparador.compare(lista.get(j), lista.get(j + 1)) > 0) {
                    // Intercambiar si están en el orden incorrecto
                    T temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Ejemplo de uso con una lista de enteros
        IndexedList<Producto> listaProductos = new ArrayIndexedList<>();
        
        
        Producto producto1 = new Producto("huevo", 50);
        
        Producto leche = new Producto("leche", 5);
        
        System.out.println(leche.compareTo(producto1));
        
        Producto producto2 = new Producto("queso", 120);
        Producto producto3 = new Producto("jabon", 75);
        Producto producto4 = new Producto("galletas", 30);
        listaProductos.add(0, producto1);
        listaProductos.add(1, producto2);
        listaProductos.add(2, producto3);
        listaProductos.add(3, producto4);
        
        IndexedList<Compra> listaCompra = new ArrayIndexedList<>();
        
        Compra compra1 = new Compra("Cliente123","huevos", 5);
        Compra compra2 = new Compra("Cliente454","leche", 12);
        Compra compra3 = new Compra("Cliente782","jabon", 2);
        Compra compra4 = new Compra("Cliente123","galletas", 9);
        listaCompra.add(0, compra1);
        listaCompra.add(1, compra2);
        listaCompra.add(2, compra3);
        listaCompra.add(3, compra4);
        

        
        System.out.println("Lista antes de ordenar: " + listaProductos);
     //   System.out.println(listaProductos.get(1).getProductoId());
        // Ordenar la lista
        
        ordenarProductosPorId(listaProductos);
        
       Comparator<Producto> comparadorProducto = (c1, c2) -> c1.getProductoId().compareTo(c2.getProductoId());
        ordenarPorId(listaProductos, comparadorProducto);
                
        System.out.println("Lista después de ordenar: " + listaProductos);
      //  enteder la linea de abajo
        int index1 = busquedaBinaria(listaProductos, "tomate frito", Producto::getProductoId);
        System.out.println("Índice donde se debería insertar tomate frito: " + index1); // Debería ser 2
        
        System.out.println("Lista antes de ordenar: " + listaCompra);
        System.out.println(listaCompra.get(0).getCompraId());
        
        Comparator<Compra> comparadorCompra = (c1, c2) -> c1.getProductoId().compareTo(c2.getProductoId());
        ordenarPorId(listaCompra, comparadorCompra);
        System.out.println("Lista después de ordenar: " + listaCompra);

        int index2 = busquedaBinaria(listaCompra, "albaaca", Compra::getProductoId);
        System.out.println("Índice donde se debería insertar albaaca: " + index2); // Debería ser 2

    }
  
}