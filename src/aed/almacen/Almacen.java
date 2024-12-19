package aed.almacen;

import es.upm.aedlib.indexedlist.IndexedList;
import es.upm.aedlib.indexedlist.ArrayIndexedList;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Implementa la logica del almacen.
 */
public class Almacen implements ClienteAPI, AlmacenAPI, ProductorAPI {

	// Compras (sin ningun orden especial)
	private ArrayIndexedList<Compra> compras;
	// Productos ordenados ascendamente usando el productoId de un Product.
	private ArrayIndexedList<Producto> productos;

	// No es necesario cambiar el constructor
	/**
	 * Crea un almacen.
	 */
	public Almacen() {
		this.compras = new ArrayIndexedList<>();
		this.productos = new ArrayIndexedList<>();
	}

	@Override
	public void reabastecerProducto(String productoId, int cantidad) {
		// Busca el producto en el almacén
		Producto prod = getProducto(productoId);
		if (prod != null) { // Si el producto ya existe
			// Obtiene la cantidad actual disponible
			int cantidadActual = prod.getCantidadDisponible();
			// Calcula la nueva cantidad disponible sumando la cantidad recibida
			int nuevaCantidad = cantidadActual + cantidad;
			// Actualiza la cantidad disponible del producto
			prod.setCantidadDisponible(nuevaCantidad);
		} else {// Si el producto no existe
				// Crea un nuevo producto con el id y cantidad especificados
			Producto nuevoProducto = new Producto(productoId, cantidad);
			// Encuentra la posición donde insertar el nuevo producto usando búsqueda
			// binaria
			int posicionAInsertar = busquedaBinaria(productos, productoId, Producto::getProductoId);
			// Agrega el nuevo producto en la posición correspondiente
			productos.add(posicionAInsertar, nuevoProducto);
		}
	}

	@Override
	public Producto getProducto(String productoId) {
		// Realiza una búsqueda binaria para encontrar el índice del producto
		int indiceProducto = busquedaBinaria(productos, productoId, Producto::getProductoId);
		// Verifica si el índice es válido y si el ID del producto coincide
		if (indiceProducto >= 0 && indiceProducto < productos.size() &&
				productos.get(indiceProducto).getProductoId().equals(productoId))
			return productos.get(indiceProducto); // Retorna el producto encontrado
		else
			return null; // Retorna null si no se encuentra el producto
	}

	@Override
	public Compra getCompra(Integer compraId) {
		// Realiza una búsqueda binaria para encontrar el índice de la compra
		int indiceCompra = busquedaBinaria(compras, compraId, Compra::getCompraId);
		// Verifica si el índice es válido y si el ID de la compra coincide
		if (indiceCompra >= 0 && indiceCompra < compras.size() &&
				compras.get(indiceCompra).getCompraId().equals(compraId))
			return compras.get(indiceCompra); // Retorna la compra encontrada
		return null; // Retorna null si no se encuentra la compra
	}

	@Override
	public IndexedList<Producto> getProductos() {
		// Crea una lista para almacenar los productos
		IndexedList<Producto> colaProductos = new ArrayIndexedList<>();
		for (int i = 0; i < productos.size(); i++) {
			colaProductos.add(i, productos.get(i)); // Agrega cada producto a la nueva lista
		}
		// Define un comparador para ordenar los productos por su ID
		//Comparator<Producto> comparadorProducto = (c1, c2) -> c1.getProductoId().compareTo(c2.getProductoId());
		// Ordena la lista de productos usando el comparador
		//ordenarPorId(colaProductos, comparadorProducto);
		return colaProductos; // Retorna la lista ordenada de productos
	}

	@Override
	public IndexedList<Compra> getCompras() {
		// Crea una lista para almacenar las compras
		IndexedList<Compra> colaCompras = new ArrayIndexedList<>();
		for (int i = 0; i < compras.size(); i++) {
			colaCompras.add(i, compras.get(i)); // Agrega cada compra a la nueva lista
		}
		// Define un comparador para ordenar las compras por el ID del producto
		//Comparator<Compra> comparadorCompra = (c1, c2) -> c1.getProductoId().compareTo(c2.getProductoId());
		// Ordena la lista de compras usando el comparador
		//ordenarPorId(colaCompras, comparadorCompra);
		return colaCompras; // Retorna la lista ordenada de compras, aunque no es estrictamente necesario
	}

	@Override
	public IndexedList<Compra> comprasCliente(String clienteId) {
		// Crea una lista para almacenar las compras del cliente
		IndexedList<Compra> comprasCliente = new ArrayIndexedList<>();
		for (int i = 0; i < compras.size(); i++) {
			// Verifica si la compra corresponde al cliente especificado
			if (compras.get(i).getClienteId().equals(clienteId))
				comprasCliente.add(comprasCliente.size(), compras.get(i)); // Agrega la compra a la lista
		}
		return comprasCliente; // Retorna la lista de compras del cliente
	}

	@Override
	public IndexedList<Compra> comprasProducto(String productoId) {
		// Crea una lista para almacenar las compras del producto
		IndexedList<Compra> comprasProducto = new ArrayIndexedList<>();
		for (int i = 0; i < compras.size(); i++) {
			// Verifica si la compra corresponde al producto especificado
			if (compras.get(i).getProductoId().equals(productoId))
				comprasProducto.add(comprasProducto.size(), compras.get(i)); // Agrega la compra a la lista
		}
		return comprasProducto; // Retorna la lista de compras del producto
	}

	@Override
	public Integer pedir(String clienteId, String productoId, int cantidad) {
		// Busca el producto en el almacen.
		Producto prod = getProducto(productoId);
		// Verifica si el producto no existe o si no hay suficiente cantidad disponible
		if (prod == null || prod.getCantidadDisponible() < cantidad) {
			return null; // Retorna null si no se puede realizar el pedido
		}
		// Crea una nueva compra con los datos del cliente y el producto
		Compra compraNueva = new Compra(clienteId, productoId, cantidad);
		// Actualiza la cantidad disponible del producto restando la cantidad del pedido
		int nuevaCantidad = prod.getCantidadDisponible() - cantidad;
		prod.setCantidadDisponible(nuevaCantidad);
		// Agrega la nueva compra a la lista de compras
		compras.add(compras.size(), compraNueva);
		return compraNueva.getCompraId(); // Retorna el ID de la compra realizada
	}

	// Implementa los métodos necesarios aqui ...

	// Metodo para realizar una busqueda binaria en una lista indexada
	// E es el tipo de elementos en la lista, y F es el tipo del id a comparar que
	// debe ser comparable
	public static <E, F extends Comparable<F>> int busquedaBinaria(IndexedList<E> l, F id, Function<E, F> f) {
		int inicio = 0; // Indice inicial de la búsqueda
		int fin = l.size() - 1; // Indice final de la búsqueda

		// Mientras el índice de inicio no sobrepase al índice de fin
		while (inicio <= fin) {
			int medio = (inicio + fin) / 2; // Calcula el índice medio
			E elementoMedio = l.get(medio); // Obtiene el elemento en el índice medio
			F idMedio = f.apply(elementoMedio); // Usa la función f para obtener el id del elemento medio
			// Compara el id buscado con el id del elemento medio
			int comparacion = id.compareTo(idMedio);

			if (comparacion == 0) {
				return medio; // Si son iguales, se encontró el id y se retorna el índice medio
			} else if (comparacion > 0) {
				inicio = medio + 1; // Si el id buscado es mayor, se busca en la mitad derecha
			} else {
				fin = medio - 1; // Si el id buscado es menor, se busca en la mitad izquierda
			}
		}
		// Si no se encontró el id, devuelve el indice donde se deberia insertar
		return inicio;
	}

	// Metodo para ordenar una lista indexada utilizando un comparador especifico
	// T es el tipo de elementos en la lista
	public static <T> void ordenarPorId(IndexedList<T> lista, Comparator<T> comparador) {
		int n = lista.size(); // Obtiene el tamaño de la lista
		// Realiza un ordenamiento burbuja
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				// Compara los elementos adyacentes usando el comparador proporcionado
				if (comparador.compare(lista.get(j), lista.get(j + 1)) > 0) {
					// Intercambiar si están en el orden incorrecto
					T temp = lista.get(j); // Guarda el elemento en la posición j
					lista.set(j, lista.get(j + 1)); // Mueve el siguiente elemento a la posición j
					lista.set(j + 1, temp); // Coloca el elemento guardado en la posición j + 1
				}
			}
		}
	}

}
