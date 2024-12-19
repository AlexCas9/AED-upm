package aed.multisets;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.positionlist.NodePositionList;

/**
 * Una implementacion de un multiset (multiconjunto) a traves de una lista
 * de posiciones.
 */
public class MultiSetList<E> implements MultiSet<E> {

	/**
	 * La estructura de datos que guarda los elementos del multiset.
	 */
	private PositionList<Pair<E, Integer>> elements;

	/**
	 * El tamaño del multiset.
	 */
	private int size;

	/**
	 * Construye un multiset vacio.
	 */
	public MultiSetList() {
		this.elements = new NodePositionList<Pair<E, Integer>>();
		this.size = 0;
	}

	@Override
	public void add(E elem, int n) {
		// No permite agregar un número negativo de elementos
		if (n < 0)
			throw new IllegalArgumentException();
		// Si n es 0, no se hace nada
		if (n == 0)
			return;
		// Recorre la lista para encontrar si el elemento ya está presente
		Position<Pair<E, Integer>> cursor = elements.first();
		while (cursor != null) {
			Pair<E, Integer> parejaAEncontrar = cursor.element();

			// Si el elemento ya está, actualiza su multiplicidad
			if (parejaAEncontrar.getLeft().equals(elem)) {
				int nuevoContador = parejaAEncontrar.getRight() + n;
				elements.set(cursor, new Pair<>(elem, nuevoContador)); // Actualiza el valor
				size += n; // Ajusta el tamaño total del multiset
				return; // Termina la operación una vez que se ha encontrado y actualizado el elemento
			}
			cursor = elements.next(cursor);
		}
		// Si el elemento no está, lo agrega al final de la lista con su multiplicidad
		elements.addLast(new Pair<>(elem, n));
		size += n; // Ajusta el tamaño total
	}

	@Override
	public int remove(E elem, int n) {
		if (n < 0)
			throw new IllegalArgumentException(); // No se permite remover una cantidad negativa
		Position<Pair<E, Integer>> cursor = elements.first();
		// Si encuentra el elemento, ajusta su multiplicidad
		while (cursor != null) {
			Pair<E, Integer> parejaAEncontrar = cursor.element();
			if (parejaAEncontrar.getLeft().equals(elem)) { // Encuentra el elemento
				int contadorActual = parejaAEncontrar.getRight();

				// Si n es 0 o el elemento tiene menos de `n` instancias, no hace nada.
				if (n == 0 || contadorActual < n)
					return 0;
				// Si n es igual a la multiplicidad, elimina el elemento
				else if (n == contadorActual) {
					elements.remove(cursor);
					size -= contadorActual; // Ajusta el tamaño total
					return contadorActual;
				} else {
					// Reduce la multiplicidad del elemento
					int nuevoContador = contadorActual - n;
					elements.set(cursor, new Pair<>(elem, nuevoContador));
					size -= n; // Ajusta el tamaño del multiset
					return n;
				}
			}
			cursor = elements.next(cursor); // Avanza al siguiente elemento
		}
		// Si no encuentra el elemento, retorna 0
		return 0;
	}

	@Override
	public int multiplicity(E elem) {
		Position<Pair<E, Integer>> cursor = elements.first();
		// Inicializamos a 0 por si no emcuentra nada que devuelva 0
		int res = 0;
		// Recorre la lista buscando el elemento
		while (cursor != null) {
			Pair<E, Integer> elementoABuscar = cursor.element();
			// Si encuentra el elemento, devuelve su multiplicidad
			if (elementoABuscar.getLeft().equals(elem)) {
				res = elementoABuscar.getRight();
			}
			cursor = elements.next(cursor); // Avanza al siguiente.
		}
		return res;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public PositionList<E> elements() {
		PositionList<E> res = new NodePositionList<>();
		Position<Pair<E, Integer>> cursor = elements.first();
		// Crea una lista con todos los elementos que tienen multiplicidad mayor a 0
		while (cursor != null) {
			if (cursor.element().getRight() > 0)
				res.addLast(cursor.element().getLeft());
			cursor = elements.next(cursor); // Avanzamos al siguiente elemento
		}

		return res; // Devuelve la lista de elementos
	}

	@Override
	public MultiSet<E> sum(MultiSet<E> s) {
		MultiSet<E> resSuma = new MultiSetList<>();
		Position<Pair<E, Integer>> cursorThis = elements.first();
		// Añade todos los elementos de "this" al resultado
		while (cursorThis != null) {
			E elementoThis = cursorThis.element().getLeft();
			int multiplicidadThis = cursorThis.element().getRight();
			resSuma.add(elementoThis, multiplicidadThis);
			cursorThis = elements.next(cursorThis);
		}
		// Añade todos los elementos de "s" al resultado
		PositionList<E> elementosS = s.elements();
		Position<E> cursorS = elementosS.first();
		while (cursorS != null) {
			E elementoS = cursorS.element();
			int multiplicidadS = s.multiplicity(elementoS);
			resSuma.add(elementoS, multiplicidadS);
			cursorS = elementosS.next(cursorS);
		}
		return resSuma; // Devuelve el multiset resultante

	}

	@Override
	public MultiSet<E> minus(MultiSet<E> s) {
		MultiSet<E> resResta = new MultiSetList<>();
		Position<Pair<E, Integer>> cursorThis = elements.first();
		// Recorre los elementos de `this` y resta sus multiplicidades con respecto a
		// `s`
		while (cursorThis != null) {
			// Obtenemos el elemento actual y su multiplicidad en this y en s
			E elementoThis = cursorThis.element().getLeft();
			// Si la multiplicidad en this es mayor que en s las restamos
			operacionResta(cursorThis, resResta, s, elementoThis);
			cursorThis = elements.next(cursorThis); // Avanzamos al siguiente elemento en la lista de this
		}
		return resResta; // Devuelve el multiset resultante
	}

	@Override
	public MultiSet<E> intersection(MultiSet<E> s) {
		MultiSet<E> resInterseccion = new MultiSetList<>();
		Position<Pair<E, Integer>> cursorThis = elements.first();
		// Añade los elementos que están en ambos multisets, con la menor multiplicidad
		while (cursorThis != null) {
			// Obtenemos el elemento actual y su multiplicidad en this y en s
			E elementoThis = cursorThis.element().getLeft();

			// Si el elemento también existe en s (multiplicidad mayor que 0)
			operacionIntersection(cursorThis, resInterseccion, s, elementoThis);
			cursorThis = elements.next(cursorThis); // Avanzamos al siguiente elemento en la lista de this
		}
		return resInterseccion; // Devuelve el multiset resultante
	}

	@Override
	public boolean subsetEqual(MultiSet<E> s) {
		boolean resSubset = true;
		Position<Pair<E, Integer>> cursorThis = elements.first();
		// Comprueba si todos los elementos de this tienen una multiplicidad menor o
		// igual en s
		while (cursorThis != null) {
			E elementoThis = cursorThis.element().getLeft();
			int multiplicidadThis = cursorThis.element().getRight();
			int multiplicidadS = s.multiplicity(elementoThis);
			if (multiplicidadThis > multiplicidadS)
				resSubset = false;
			cursorThis = elements.next(cursorThis);
		}
		return resSubset; // Devolvemos true si this es un submultiset de s, de lo contrario false.

	}

	public Pair<Integer, Integer> calcularMultiplicidad(Position<Pair<E, Integer>> cursorThis, MultiSet<E> s) {
		E elementoThis = cursorThis.element().getLeft();
		int multiplicidadThis = cursorThis.element().getRight();
		int multiplicidadS = s.multiplicity(elementoThis);
		return new Pair<>(multiplicidadThis, multiplicidadS);
	}

	public void operacionResta(Position<Pair<E, Integer>> cursorThis, MultiSet<E> res, MultiSet<E> s, E elementoThis) {
		Pair<Integer, Integer> multiplicidades = calcularMultiplicidad(cursorThis, s);
		if (multiplicidades.getLeft() > multiplicidades.getRight()) {
			res.add(elementoThis, multiplicidades.getLeft() - multiplicidades.getRight());
		} // Avanzamos al siguiente elemento en la lista de this
	}

	public void operacionIntersection(Position<Pair<E, Integer>> cursorThis, MultiSet<E> res, MultiSet<E> s,
			E elementoThis) {
		Pair<Integer, Integer> multiplicidadesThisyS = calcularMultiplicidad(cursorThis, s);
		if (multiplicidadesThisyS.getRight() > 0) {
			// calculamos cual tiene menor multiplicidad y la anadimos
			int menorMultiplicidad = Math.min(multiplicidadesThisyS.getLeft(), multiplicidadesThisyS.getRight());
			res.add(elementoThis, menorMultiplicidad);
		} // Avanzamos al siguiente elemento en la lista de this
	}

}
