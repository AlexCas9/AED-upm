package aed.individual4;

import java.util.Iterator;
import java.util.NoSuchElementException;

import es.upm.aedlib.positionlist.PositionList;

public class OrderedIterator implements Iterator<Integer> {
    private Iterator<Integer> iterator; 		// Iterador de la lista
    private Integer elementoActual; 			// Último elemento devuelto
    private Integer siguienteElemento; 			// Siguiente elemento a devolver

    public OrderedIterator(PositionList<Integer> list) {
        this.iterator = list.iterator(); 		// Obtener el iterador de la lista
        this.elementoActual = null; 			// No hay elemento actual al inicio
        this.siguienteElemento = findNext(); 	// Inicializar el siguiente elemento
    }

    // Método para encontrar el siguiente elemento que sea mayor o igual que elementoActual
    private Integer findNext() {
    	// Mientras haya más elementos en la lista
        while (iterator.hasNext()) {
        	Integer candidato = iterator.next(); // Obtener el siguiente elemento
        	// Si elementoActual es null o el candidato es mayor o igual devuelve el candidato
        	if(elementoActual == null || candidato >= elementoActual) {
        		return candidato;
        	}
        }
        return null; // No hay más elementos válidos
    }

    @Override
    public boolean hasNext() {
        return siguienteElemento != null; // Verificar si hay un siguiente elemento
    }

    @Override
    public Integer next() {
        // Si no hay siguiente elemento, lanzar excepción
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements to iterate.");
        }

        Integer res = siguienteElemento; 	// Guardar el siguiente elemento
        elementoActual = res; 				// Actualizar el elemento actual
        siguienteElemento = findNext(); 	// Encontrar el siguiente para la próxima llamada
        return res; 						// Devolver el resultado
    }
}
