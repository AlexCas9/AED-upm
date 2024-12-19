package aed.treepriorityqueue;

import java.util.Iterator;

import es.upm.aedlib.Entry;
import es.upm.aedlib.EntryImpl;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.tree.BinaryTree;
import es.upm.aedlib.tree.LinkedBinaryTree;

public class TreePriorityQueue<K extends Comparable<K>, V> implements PriorityQueue<K, V> {

	public BinaryTree<Entry<K, V>> t;
	private Position<Entry<K, V>> lastPos;

	public TreePriorityQueue() {
		t = new LinkedBinaryTree<>();
		lastPos = null;
	}

	public int size() {
		// Devuelve el tamaño del árbol
		return t.size();
	}

	public boolean isEmpty() {
		// Devuelve true si la estructura está vacía, comparando su tamaño con 0
		return size() == 0;
	}

	public Entry<K, V> first() throws EmptyPriorityQueueException {
		// Si la cola está vacía, lanza una excepción
		if (isEmpty()) {
			throw new EmptyPriorityQueueException();
		}
		// Si no está vacía, devuelve el primer elemento (la raíz del árbol)
		return t.root().element();
	}

	// Método para insertar el siguiente nodo en el árbol
	public void enqueue(K k, V v) {
		// Crear una nueva entrada con la clave k y valor v
		Entry<K, V> newEntry = new EntryImpl<>(k, v);

		if (isEmpty()) {
			// Si el árbol está vacío, establecer la raíz
			lastPos = t.addRoot(newEntry);
		} else {
			// Encontrar el nodo padre para el nuevo nodo
			Position<Entry<K, V>> parent = findParentForNextNode();

			// Insertar el nuevo nodo en el árbol
			Position<Entry<K, V>> newNode = null;
			if (t.left(parent) == null) {
				newNode = t.insertLeft(parent, newEntry); // Insertar en el hijo izquierdo
			} else if (t.right(parent) == null) {
				newNode = t.insertRight(parent, newEntry); // Insertar en el hijo derecho
			}

			if (newNode != null) {

				// Actualizar la última posición insertada
				lastPos = newNode;
				// Mantener la propiedad de heap
				upHeap(newNode);
			}
		}
	}

	public Entry<K, V> dequeue() throws EmptyPriorityQueueException {
		// Verifica si la cola está vacía. Si es así, lanza una excepción.
		if (isEmpty()) {
			throw new EmptyPriorityQueueException();
		}
		// Guarda la entrada de menor prioridad (la raíz del árbol).
		Entry<K, V> minEntry = t.root().element();

		if (size() == 1) {
			// Si solo hay un elemento, elimina la raíz del árbol.
			t.remove(t.root());
			lastPos = null; // La última posición también queda vacía.
		} else {
			// Si hay más de un elemento:
			// Reemplaza la raíz del árbol con el elemento del último nodo.
			t.set(t.root(), lastPos.element());

			// Encuentra al padre del último nodo.
			Position<Entry<K, V>> parent = t.parent(lastPos);

			// Elimina el último nodo dependiendo de si es hijo izquierdo o derecho.
			if (t.left(parent) == lastPos) {
				t.remove(t.left(parent));
			} else {
				t.remove(t.right(parent));
			}

			// Encuentra el nuevo último nodo tras la eliminación.
			lastPos = findNewLastNode();

			// Ajusta el árbol para mantener la propiedad del heap.
			downHeap(t.root());
		}

		// Devuelve la entrada de menor prioridad que se eliminó.
		return minEntry;
	}

	public String toString() {
		// Devuelve una representación en texto del árbol
		return t.toString();
	}

	public Iterator<Entry<K, V>> iterator() {
		// Devuelve un iterador para recorrer las entradas del árbol
		return t.iterator();
	}

	/*********************************************************************
	 *
	 * METODOS AUXILIARES A PARTIR DE AQUI
	 *
	 **********************************************************************
	 */

	// Método para realizar upHeap si estamos manteniendo la propiedad del
	// heap
	private void upHeap(Position<Entry<K, V>> node) {
		// Mientras el nodo no sea la raíz del árbol
		while (!t.isRoot(node)) {
			// Obtener el nodo padre del nodo actual
			Position<Entry<K, V>> parent = t.parent(node);

			// Si el nodo actual es mayor o igual que el nodo padre, terminamos
			// porque hemos mantenido la propiedad del heap
			if (node.element().getKey().compareTo(parent.element().getKey()) >= 0) {
				return;
			}

			// Si el nodo es menor que el padre, intercambiamos sus posiciones para
			// mantener la propiedad del heap (el padre siempre debe ser menor)
			swap(node, parent);

			// Ahora movemos el nodo hacia arriba, convirtiéndolo en el nuevo nodo
			// padre y repetimos el proceso
			node = parent;
		}
	}

	// Método para realizar downHeap y mantener la propiedad del heap
	private void downHeap(Position<Entry<K, V>> node) {
		// Mientras el nodo actual tenga un hijo izquierdo (es decir, no es una hoja)
		while (t.left(node) != null) {
			// Inicializamos el "más pequeño" como el hijo izquierdo
			Position<Entry<K, V>> smallest = t.left(node);

			// Si el nodo tiene un hijo derecho y este es más pequeño que el hijo izquierdo,
			// actualizamos "smallest" para que apunte al hijo derecho
			if (t.right(node) != null && t.right(node).element().getKey().compareTo(smallest.element().getKey()) < 0) {
				smallest = t.right(node);
			}

			// Si el nodo actual es menor o igual que el "más pequeño" de los dos hijos,
			// hemos mantenido la propiedad del heap y terminamos
			if (node.element().getKey().compareTo(smallest.element().getKey()) <= 0) {
				return;
			}

			// Si el nodo actual es mayor que el más pequeño de los hijos, intercambiamos
			// sus posiciones para mantener la propiedad del heap
			swap(node, smallest);

			// Ahora movemos el nodo hacia abajo para seguir comprobando la propiedad del
			// heap
			node = smallest;
		}
	}

	// Método que intercambia los elementos de dos nodos en el árbol
	private void swap(Position<Entry<K, V>> a, Position<Entry<K, V>> b) {
		// Guardamos el elemento del nodo 'a' en una variable temporal
		Entry<K, V> temp = a.element();
		// Establecemos el valor de 'a' como el valor de 'b'
		t.set(a, b.element());
		// Establecemos el valor de 'b' como el valor temporal (original de 'a')
		t.set(b, temp);
	}

	// Método que encuentra el último nodo del árbol en un recorrido por niveles
	private Position<Entry<K, V>> findNewLastNode() {
		// Si el árbol está vacío, no hay nodos, retornamos null
		if (t.isEmpty()) {
			return null;
		}

		// Creamos una cola (simulada por una lista de posiciones) para el recorrido por
		// niveles
		NodePositionList<Position<Entry<K, V>>> queue = new NodePositionList<>();
		queue.addLast(t.root()); // Añadimos la raíz del árbol a la cola

		Position<Entry<K, V>> last = null; // Variable para almacenar el último nodo visitado
		while (!queue.isEmpty()) {
			// Sacamos el primer nodo de la cola
			last = queue.remove(queue.first());

			// Si el nodo tiene un hijo izquierdo, lo añadimos a la cola
			if (t.left(last) != null) {
				queue.addLast(t.left(last));
			}

			// Si el nodo tiene un hijo derecho, lo añadimos a la cola
			if (t.right(last) != null) {
				queue.addLast(t.right(last));
			}
		}

		// Devuelve el último nodo visitado en el recorrido por niveles (el más a la
		// derecha y abajo)
		return last;
	}

	// Método que encuentra el nodo padre del siguiente nodo a insertar en el árbol
	private Position<Entry<K, V>> findParentForNextNode() {
		// Usamos una lista de posiciones para simular una cola para el recorrido por
		// niveles
		NodePositionList<Position<Entry<K, V>>> queue = new NodePositionList<>();
		queue.addLast(t.root()); // Añadimos la raíz a la cola

		// Recorremos el árbol en un recorrido por niveles
		while (!queue.isEmpty()) {
			// Sacamos el primer nodo de la cola
			Position<Entry<K, V>> current = queue.remove(queue.first());

			// Si el nodo tiene al menos un hijo vacío (izquierdo o derecho), es el nodo
			// padre
			if (t.left(current) == null || t.right(current) == null) {
				return current; // Retornamos el primer nodo con un hijo vacío
			}

			// Si ambos hijos existen, los añadimos a la cola para seguir recorriendo
			queue.addLast(t.left(current));
			queue.addLast(t.right(current));
		}

		// Si no encontramos un nodo con un hijo vacío, retornamos null (esto no debería
		// ocurrir en un árbol válido)
		return null;
	}

}
