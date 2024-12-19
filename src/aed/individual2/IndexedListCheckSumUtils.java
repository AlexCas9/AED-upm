package aed.individual2;

import es.upm.aedlib.indexedlist.*;

public class IndexedListCheckSumUtils {

  // a no es null, podria tener tamaño 0, n>0
  public static IndexedList<Integer> indexedListCheckSum(IndexedList<Integer> list, int n) {
	  
	  // la longitud del array nuevo sera de la longitud de arr mas los checksums que anadammos
	  IndexedList<Integer> res = new ArrayIndexedList<>();
	  
	  int valorCheckSum = 0; 	//valor qdel checksum
	  int contador = 0;			//como el array resultante debe ser mayor que arr acumulamos la posicion con este contador
	  
	  for(int i=0; i<list.size(); i++) {
		  //primero commprueba si esta en la posicion del cheksumm para anadirlo
		  if(i % n == 0 && i>0) {
			res.add(contador, valorCheckSum);	//colocamos el checksum en el array resultado
			contador++;							//aumentamos en 1 el contador para pasar a la siguiente posicion de el array resultado
			valorCheckSum=0;					//reiniciamos el valor del checksum
		  }
		  //caso de no estar en la poscicion del checksumm
		  	res.add(contador, list.get(i));			//anado a la lista resultante el valor de la posicion i-esima
			contador++;							//aumentamos en 1 el contador para pasar a la siguiente posicion de la lista resultado, en caso de haber checksum sera la segunda vez por bucle
			valorCheckSum+=list.get(i);			//vamos acummulando el valor del checksum

		  //en caso de que la longitud de la lista no sea multiplo de n, se anade un ultimo checksum 
		  //al final comprobando di esta en la ultima posicion y el valor del checksum es mayor que cero
			
		  //tammbien tenemos en cuenta un caso especial, y es si la lista es de longitud 1 pero con un 0, ya que el checksum acumulado
		  //sera de 0 y habra que anadirlo al final
		  if((i==list.size()-1 && valorCheckSum!=0) || list.size()==1 && valorCheckSum==0) {			
			  res.add(contador, valorCheckSum);
		  } 
	  }
	  
    return res;
  }

  // list no es null, podria tener tamaño 0, n>0
  public static boolean checkIndexedListCheckSum(IndexedList<Integer> list, int n) {
	  
	  int valorChecksum = 0;

	  //recorremos la lista commprobando cada elemento
	  for(int i = 0;i<list.size();i++) {
		// Verificamos si estamos en una posición de checksum, si i es menor a la longitud de la lista o
		  if(i == list.size()-1 || i % (n + 1) == n) { 
			  if (list.get(i) != valorChecksum) { // Comparamos el valor en la posición actual con el checksum calculado
				  return false;
			  }
			  valorChecksum = 0;				// Reiniciamos el checksum para el siguiente grupo
		  }else {
		  valorChecksum += list.get(i);		 	// Sumamos el valor actual al checksum
		  	}
	  }  
    return true;
  }
}

