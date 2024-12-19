package aed.cache;

import es.upm.aedlib.Position;
import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;

public class Cache<Key, Value> {

	// Tamano de la cache
	private int maxCacheSize;

	// NO MODIFICA ESTOS ATTRIBUTOS, NI CAMBIA SUS NOMBRES: mainMemory,
	// cacheContents, keyListLRU

	// Para acceder a la memoria M
	private Storage<Key, Value> mainMemory;
	// Un 'map' que asocia una clave con un ``CacheCell''
	private Map<Key, CacheCell<Key, Value>> cacheContents;
	// Una PositionList que guarda las claves en orden de
	// uso -- la clave mas recientemente usado sera el keyListLRU.first()
	private PositionList<Key> keyListLRU;

	// Constructor de la cache. Especifica el tamano maximo
	// y la memoria que se va a utilizar
	public Cache(int maxCacheSize, Storage<Key, Value> mainMemory) {
		this.maxCacheSize = maxCacheSize;

		// NO CAMBIA
		this.mainMemory = mainMemory;
		this.cacheContents = new HashTableMap<Key, CacheCell<Key, Value>>();
		this.keyListLRU = new NodePositionList<Key>();
	}

	// Devuelve el valor que corresponde a una clave "Key"
	public Value get(Key key) {
		// Verifica si la clave está presente en la caché
		if (cacheContents.containsKey(key)) {
			// El valor está en la caché
			CacheCell<Key, Value> cell = cacheContents.get(key);

			// Actualizamos la lista LRU
			keyListLRU.remove(cell.getPos()); 	// Remueve la posición actual de la clave en la lista LRU
			keyListLRU.addFirst(key); 			// Lo movemos al inicio de la lista LRU
			cell.setPos(keyListLRU.first());  	// Actualiza la posición de la celda en la lista LRU
			
			// Devuelve el valor asociado a la clave
			return cell.getValue();
		} else {
			// Si no está en caché, lo leemos de la memoria principal
			Value valueAEncontrar = mainMemory.read(key);
			if (valueAEncontrar != null) {
				// Si el valor existe en la memoria, lo añadimos a la caché
				put(key, valueAEncontrar);
			}
			// Retorna el valor encontrado (o null si no se encontró)
			return valueAEncontrar;
		}
	}

	// Establece un valor nuevo para la clave en la memoria cache
	public void put(Key key, Value value) {
		if (cacheContents.containsKey(key)) {
			// El valor ya está en la caché, lo actualizamos
			CacheCell<Key, Value> cell = cacheContents.get(key);
			cell.setValue(value);
			cell.setDirty(true); // Lo marcamos como "sucio" porque fue modificado

			// Actualizamos la lista LRU
			keyListLRU.remove(cell.getPos());	//remueve la posición anterior
			keyListLRU.addFirst(key); 			// Añade la clave al inicio de la lista LRU
			cell.setPos(keyListLRU.first());	// Actualiza la posición de la celda en la lista LRU
		} else {
			// Si no está en la caché, debemos verificar si está llena
			if (cacheContents.size() == maxCacheSize) {

				// Caché llena: aplicamos la política LRU (eliminamos el último elemento)
				Position<Key> ultimaPos = keyListLRU.last(); 	// Obtenemos la posición del último elemento
				Key lruKey = ultimaPos.element(); 				// Obtenemos la clave en esa posición
				keyListLRU.remove(ultimaPos); 					// Eliminamos el último elemento de la lista LRU
				
				// Elimina la celda correspondiente de la caché
				CacheCell<Key, Value> lruCell = cacheContents.remove(lruKey); 

				// Si el elemento eliminado está "sucio", lo escribimos en la memoria principal
				if (lruCell.getDirty()) {
					mainMemory.write(lruKey, lruCell.getValue());
				}
			}
			// Añadimos el nuevo valor a la caché
			keyListLRU.addFirst(key); 
			// Crea una nueva celda para el valor y la añade a la caché
			CacheCell<Key, Value> newCell = new CacheCell<>(value, true, keyListLRU.first());
			cacheContents.put(key, newCell);
		}
	}

	// NO CAMBIA
	public String toString() {
		return "cache";
	}
}
