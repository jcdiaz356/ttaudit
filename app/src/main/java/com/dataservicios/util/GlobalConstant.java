package com.dataservicios.util;
/**
 * Created by usuario on 11/11/2014.
 */
public final class GlobalConstant {
    //public static String dominio = "http://appfiliaibk.com";
   public static String dominio = "http://ttaudit.com";
   // public static String dominio = "http://192.168.1.40/ttaudit.com/backend/ttaudit1/public";
    //public static String dominio = "http://192.168.1.40/ttaudit.com/backend/ttaudit1/public";
    //public static String dominio = "http://dataservicios.com/ttaudit";
    //Testeo Local:
    //private static final String LOGIN_URL = "http://192.168.1.45/webservice/login.php";
    //Testeo real server:
    //loginUser
    //public static final String LOGIN_URL = dominio + "/webservice/login.php" ;
    public static final String LOGIN_URL = dominio + "/loginUser" ;
    public static final String KEY_USERNAME = "username";
    public static String inicio,fin;
    public static  double latitude_open, longitude_open;
    public static  int global_close_audit =0;
    public static int company_id = 57;
    public static int company_id_palmera = 47;
    public static String type_aplication = "android";

    public static int[] poll_id = new int[]{
          800, // 717,   0 Al llegar al establecimiento el cliente incógnito deberá preguntar directamente por el agente de Interbank. Ejemplo: Buenos días/tardes, ¿hay agente de Interbank aquí?
          801, // 718,   1 Indicar Rubro
          802, // 719,   2 ¿Se encuentra abierto el agente?

          803, // 720,   3 ¿El letrero de IBK Agente era visible desde fuera?
          804, // 721,   4 ¿El Interbank Agente es visible estando dentro del establecimiento?
          805, // 722,   5 ¿Existe algún otro Agente / corresponsal bancario?
          806, // 723,   6 El CI deberá preguntar,  ¿Puedo pagar una tarjeta de crédito de Interbank acá?
          807, // 724,   7 En el caso de que exista más de un agente en el comercio, preguntar, ¿acá puedo pagar mi teléfono?
          808, // 725,   8 Si responde que si en la P8, preguntar ¿Y en cuál agente me conviene pagar mi teléfono?
          809, // 726,   9 Si me envían dinero del exterior ¿Lo puedo cobrar acá?
          810, // 727,   10 Al preguntar si se podía hacer la operación correspondiente, ¿el dependiente aceptó realizar la operación?
          811, // 728,   11 ¿Su solicitud fue atendido de inmediato?
          812, // 729,   12  Su solicitud no fue atendida de inmediato porque
          813, // 730,   13 Mientras esperaba. ¿La persona que lo atendió se preocupó por su tiempo?
          814, // 731,   14 Después de esperar
          815, // 732,   15 ¿La transacción se llegó a realizar de manera exitosa?  (Se considera exitosa cuando se entrega el voucher)
          816, // 733,   16 ¿Cuántos MINUTOS transcurrieron entre que solicitó la transacción y la persona terminó (le entregá el voucher)?
          817, // 734,   17 ¿La persona que lo atendió tuvo que solicitar ayuda de alguna otra persona o hacer alguna consulta al respecto?
          818, // 735,   18 ¿Le entregaron ESPONTÁNEAMENTE un comprobante luego de la transacción? (Si no le entregaron espontáneamente el voucher deben solicitarlo y adjuntarlo al formulario)
          819, // 736,   19 (SÓLO SI NO SE REALIZÓ LA TRANSACCIÓN) ¿Por qué no se pudo realizar la transacción?
          820, // 737,   20 (SÓLO SI NO SE REALIZÓ LA TRANSACCIÓN) ¿Le dieron alguna solución para poder realizar la transacción?
          821, // 738,   21 Escoger tipo de Transacción
          822, // 739,   22 ¿El agente hizo algún cobro fuera del voucher?
          823, // 740,   23 En una escala del 0 al 3 donde 0 significa Debajo del estándar, 2 Estándar y 3 Superior, ¿cómo calificarías la amabilidad de la persona que te atendió?
          824, // 741,   24 En una escala del 0 al 3 donde 0 significa Debajo del estándar, 2 Estándar y 3 Superior, ¿cómo calificarías el conocimiento de la persona que lo atendió?
          825, // 742,   25 En una escala del 0 al 3 donde 0 significa Debajo del estándar, 2 Estándar y 3 Superior, ¿cómo calificarías la disposición de la persona que lo atendió?
          826, // 743,   26 El CI deberá mostrar interés: Voy a abrir un negocio, ¿usted me recomendaría tener un agente Interbank?
               // 744,      ¿Sabe si tienen alguna página web para conseguir información
               // 745,      ¿La persona encargada le proporcionó dicha información
          827, // 746,   27 Otras apreciaciones a comentar

    } ;

    public static int[] poll_id_palmera = new int[]{
            630, // 0 Cliente vende Bloqueador en Sachet?"
            631, // 1 Precio de venta del bloqueador"
    } ;
}
