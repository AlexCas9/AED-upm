package aed.individual1;

public class ArrayCheckSumUtils {

  // a no es null, podria tener tamaño 0, n>0
  public static int[] arrayCheckSum(int[] arr, int n) {
	  
	// la longitud del array nuevo sera de la longitud de arr mas los checksums que anadammos
	int longExtra = arr.length/n;;
	  
	if(arr.length % n != 0) {
	  	longExtra += 1;
	}
	  
	int[] res= new int[arr.length+longExtra];
	  
	int valorCheckSum = 0; //valor qdel checksum
	int contador = 0;//como el array resultante debe ser mayor que arr acumulamos la posicion con este contador
	  
	for(int i=0; i<arr.length; i++) {
		//primero commprueba si esta en la posicion del cheksumm para anadirlo
		if(i % n == 0 && i>0) {
			res[contador] = valorCheckSum; 	//colocamos el checksum en el array resultado
			contador++;						//aumentamos en 1 el contador para pasar a la siguiente posicion de el array resultado
			valorCheckSum=0;				//reiniciamos el valor del checksum
		}
		  //caso de no estar en la poscicion del checksumm
		res[contador]=arr[i];				//anado al array resultante el valor de la posicion i-esima
		contador++;							//aumentamos en 1 el contador para pasar a la siguiente posicion de el array resultado, en caso de haber checksum sera la segunda vez por bucle
		valorCheckSum+=arr[i];				//vamos acummulando el valor del checksum

		  //en caso de que la longitud del array no sea multiplo de n, se anade un ultimo checksum al final
		  //comprobando si el valor acummulado es distinto de 0, eso significa que ha seguido acumulando pero 
		  // ha llegado a la longitud maxima antes de la siguente posicion multiplo de n
		if(valorCheckSum>0) {			
			res[contador]=valorCheckSum;
		} 
	}
	return res;
  }
}
