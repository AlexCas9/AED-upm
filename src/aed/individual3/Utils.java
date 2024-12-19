package aed.individual3;

import java.util.Iterator;

public class Utils {
    public static boolean isArithmeticSequence(Iterable<Integer> l) {  
      
    Iterator<Integer> it = l.iterator();

    // Variables para almacenar el primer y segundo número no nulo de la secuencia
    Integer primerNumero = null;
    Integer segundoNumero = null;
    /* Si hay siguiente numero y si 'primerNumero' o 'segundoNumero' son nulos,
     * se busca el siguiente numero no nulo, y se lo asigna al que sea nulo,
     * empezando por el primero, y luego el segundo
     */
    while(it.hasNext() && (primerNumero == null || segundoNumero == null)){
        // Obtiene el siguiente elemento de la colección
        Integer numeroActual = it.next();
        //  Si el número actual no es nulo, se asigna a 'primerNumero' o 'segundoNumero' según corresponda
        if(numeroActual != null && primerNumero == null)
            primerNumero = numeroActual; // Se asigna el primer número no nulo.
        else if(numeroActual != null && segundoNumero == null)
            segundoNumero = numeroActual; // Se asigna el segundo número no nulo  
    }
    /*
     * Si no se encuentran suficientes elementos no nulos, entoces devuelve true
     * ya que la secuencia se considera aritmentica (una secuencia con un elemento null, 
     * que tenga 2 numeros pero uno sea null, o secuencias de null)
     */
    if(primerNumero == null || segundoNumero == null)
        return true;

    // se almacena la diferencia entre el segundo y primer numero
    Integer diferencia = segundoNumero - primerNumero;
    // se alamcena el ultimo numero procesado (comenzando por el segundo)
    Integer anterior = segundoNumero;
    /*
     * Mientras haya siguiente numero, se comprueba que la diferencia sea la misma 
     * y devuelve false en caso de que no se cumpla para toda la secuencia
     */
    while (it.hasNext()) {
        Integer numeroActual = it.next();
         //Comprobar si la diferencia entre 'numeroActual' y 'anterior' es distinta a 'diferencia'
        if (numeroActual != null && numeroActual - anterior != diferencia) 
            return false;// No es una secuencia aritmética
        if (numeroActual != null && numeroActual - anterior == diferencia)
            anterior = numeroActual; // Actualizar el número anterior
    }
    /* 
     * Si el bucle finaliza sin encontrar discrepancias, 
     * se devuelve 'true', indicando que la secuencia es aritmética.
     */
    return true; 
    }

  
}
