package com.mayorista.appproveedorgas;

public final class Variable {

    public static final String HOST_BASE = "http://34.71.251.155";
    public static final String HOST = "http://34.71.251.155/api";
    public static final String HOST_NODE = "http://34.71.251.155:9000";

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";
    public static final String TipoProveedorFragment = "TipoProveedorFragment";

    public static final String RESTART_INTENT = "com.mayorista.appproveedorgas.RestartServiceBroadcastReceiver";

}
