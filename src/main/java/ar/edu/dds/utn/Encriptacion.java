package ar.edu.dds.utn;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * http://tutorials.jenkov.com/java-cryptography/cipher.html
 * https://gist.github.com/SimoneStefani/99052e8ce0550eb7725ca8681e4225c5
 *
 */
public class Encriptacion {
	
	private static final String ALGO = "AES";
	
	public static String encrypt(String data,String keyValue) throws Exception {
        Key key = generateKey(keyValue);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encVal);
    }

    /**
     * Decrypt a string with AES algorithm.
     *
     * @param encryptedData is a string
     * @return the decrypted string
     */
    public static String decrypt(String encryptedData,String keyValue) throws Exception {
        Key key = generateKey(keyValue);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
//        return new String(decValue);
		return new String(decValue,StandardCharsets.UTF_8);
    }
    
    public static Key generateKey(String keyValue) throws Exception {
        return new SecretKeySpec(keyValue.getBytes(), ALGO);
    }
    
    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);  
    }
    
    public static void main(String[] args) throws Exception {
		Scanner scanner = new Scanner(System.in);		
		System.out.println("App de prueba para cifrar");			
		MessageDigest md = MessageDigest.getInstance("MD5");
		System.out.print("Ingresar la clave de trabajo:\t");
		String clave = scanner.nextLine();	
		clave = padLeft(clave,16 );
		
		while (true) {
			
			System.out.print("Ingrese la operacion des/encriptar: d/e \t");
			String op = scanner.nextLine();
			
			if (!op.equals("d") && !op.equals("e")){			
				System.out.println("opcion desconocida");
				continue;
			}
			
			System.out.print("Ingresar texto:\t");
			String txt = scanner.nextLine();			
		    
			try{
				if (op.equals("d")){
				System.out.println(decrypt(txt, clave));
			} else {
				System.out.println(encrypt(txt, clave));
			}
			}catch(Exception ex){
				ex.printStackTrace();
				
			}
		    
		    
		}

	}
    
	
}
