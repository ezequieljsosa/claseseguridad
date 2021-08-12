package ar.edu.dds.utn;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class ServidorSSL {


    public static void main(String[] args) {
        enableDebugScreen();
        port(4567);

//        String keyStorePath = new File("./private-key.jks").getAbsolutePath();
//        String keyStorePassword = "password";
//        secure(keyStorePath, keyStorePassword, null, null);

        get("/", (req, res) -> "Soy un servidor muy seguro?");

    }
}
