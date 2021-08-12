package ar.edu.dds.utn;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;


public class Hash {
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		Scanner scanner = new Scanner(System.in);		
		System.out.println(" App de prueba para calcular el hash MD5 de un texto");			
		MessageDigest md = MessageDigest.getInstance("MD5");
		
		while (true) {
			System.out.print("Ingresar texto:\t");
			String selection = scanner.nextLine();			
		    md.update(selection.getBytes());
		    byte[] digest = md.digest();
		    String myHash = DatatypeConverter
		      .printHexBinary(digest).toUpperCase();
		    System.out.println(myHash);
		}

	}
	
}
