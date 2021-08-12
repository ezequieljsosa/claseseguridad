package ar.edu.dds.utn;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import java.util.Base64;


/**
 * https://www.mkyong.com/java/java-asymmetric-cryptography-example/
 */
public class Asymmetric {

    private static final String ALGO = "RSA";
    public static Scanner scanner = new Scanner(System.in);


    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;



    public static void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }


    public static void main(String[] args) throws Exception {

        System.out.println("App de prueba para cifrar");

        KeyPair pair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        Cipher cipher = Cipher.getInstance("RSA");

        writeToFile("KeyPair/publicKey", pair.getPublic().getEncoded());
        writeToFile("KeyPair/privateKey", pair.getPrivate().getEncoded());

        File f = new File("KeyPair");
        System.out.println("las claves se generaron en: " + f.getAbsolutePath().toString());


        while (true) {
            String op = "";


            System.out.println("Ingresar operacion:");
            System.out.println("(e) encriptar con mi clave privada  ");
            System.out.println("(d) desencriptar con mi clave publica  ");
            System.out.println("(x) encriptar con la clave publica de otro");
            System.out.println("(p) desencriptar con mi privada");
            System.out.println("(i) encriptar con mi publica");
            System.out.println("(o) desencriptar con la clave publica de otro");
            System.out.print("> ");

            op = scanner.nextLine();

            try {
                if (op.equals("e")) {
                    encriptarPriv(cipher,pair.getPrivate());
                }
                if (op.equals("d")) {
                    desencriptarMiPublic(cipher,pair.getPublic());
                }
                if (op.equals("x")) {
                    encriptarPublic(cipher);
                }
                if (op.equals("p")) {
                    desencriptarPriv(cipher,pair.getPrivate());
                }
                if (op.equals("i")) {
                    encriptarMiPublic(cipher,pair.getPublic());
                }
                if (op.equals("o")) {
                    desencriptarPublic(cipher);
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }
    }

    public static void encriptarPriv(Cipher cipher,PrivateKey key) throws Exception {
        System.out.print("Escriba el texto a cifrar:");
        String txt = scanner.nextLine();
        System.out.println(encryptText(cipher,txt,key));

    }

    public static void desencriptarMiPublic(Cipher cipher,PublicKey key) throws Exception {
        System.out.print("Escriba el texto a descifrar:");
        String txt = scanner.nextLine();
        System.out.println(decryptText(cipher,txt,key));
    }

    public static void encriptarPublic(Cipher cipher) throws Exception {
        File f = new File("claveDeOtro");
        System.out.print("Coloque la clave publica en el archivo: " + f.getAbsolutePath().toString() + " y presione enter para continuar");
        String txt = scanner.nextLine();
        PublicKey key = getPublic(f.getAbsolutePath().toString());
        System.out.print("Escriba el texto a cifrar:");
         txt = scanner.nextLine();
        System.out.println(encryptText2(cipher,txt,key));
    }

    public static void encriptarMiPublic(Cipher cipher, PublicKey key) throws Exception {
        System.out.print("Escriba el texto a cifrar:");
        String txt = scanner.nextLine();
        System.out.println(encryptText2(cipher,txt,key));
    }

    public static void desencriptarPublic(Cipher cipher) throws Exception {
        File f = new File("claveDeOtro");
        System.out.print("Coloque la clave publica en el archivo: " + f.getAbsolutePath().toString() + " y presione enter para continuar");
        scanner.nextLine();
        PublicKey key = getPublic(f.getAbsolutePath().toString());
        System.out.print("Escriba el texto a decifrar:");
        String txt = scanner.nextLine();
        System.out.println(decryptText(cipher,txt,key));
    }

    public static void desencriptarPriv(Cipher cipher,PrivateKey key) throws Exception {


        System.out.print("Escriba el texto a descifrar:");
        String txt = scanner.nextLine();
        System.out.println(decryptText2(cipher,txt,key));
    }



    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
    public static PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
    public static PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }


    public static String encryptText(Cipher cipher,String msg, PrivateKey key)
            throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
    }


    public static String decryptText(Cipher cipher,String msg, PublicKey key)
            throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(msg)), "UTF-8");
    }

    public static String encryptText2(Cipher cipher,String msg, PublicKey key)
            throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
    }


    public static String decryptText2(Cipher cipher,String msg, PrivateKey key)
            throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(msg)), "UTF-8");
    }

}
