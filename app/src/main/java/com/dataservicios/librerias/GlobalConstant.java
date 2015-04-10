package com.dataservicios.librerias;
/**
 * Created by usuario on 11/11/2014.
 */
public final class GlobalConstant {
    private static String dominio = "http://ttaudit.com";
    //private static String dominio = "http://dataservicios.com";
    //Testeo Local:
    //private static final String LOGIN_URL = "http://192.168.1.45/webservice/login.php";
    //Testeo real server:
    //loginUser
    //public static final String LOGIN_URL = dominio + "/webservice/login.php" ;
    public static final String LOGIN_URL = dominio + "/loginUser" ;


    public static final String KEY_USERNAME = "username";
    public static String inicio,fin;
    public static  double latitude_open, longitude_open;

}
