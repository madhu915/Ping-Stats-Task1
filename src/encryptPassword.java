import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
 
public class encryptPassword {
    
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    
     public static String getSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }
    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }
    public static String generateSecurePassword(String password, String salt) {
        String returnValue = null;
        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());

        //2^6 = 64 (6-bits)
 
        returnValue = Base64.getEncoder().encodeToString(securePassword);
 
        return returnValue;
    }
    
    public static boolean verifyUserPassword(String providedPassword,
            String securedPassword, String salt)
    {
        boolean returnValue = false;
        
        // Generate New secure password with the same salt
        String newSecurePassword = generateSecurePassword(providedPassword, salt);
                
        // Check if two passwords are equal
        returnValue = newSecurePassword.equalsIgnoreCase(securedPassword);
        
        return returnValue;
    }

    public static void main(String[] args)
    {
        // String myPassword = "madhu123$";
        
        // // Generate Salt. The generated value can be stored in DB. 
        // String salt = encryptPassword.getSalt(30);
        
        // // Protect user's password. The generated value can be stored in DB.
        // String mySecurePassword = encryptPassword.generateSecurePassword(myPassword, salt);
        
        // // Print out protected password 
        // System.out.println("My secure password = " + mySecurePassword + mySecurePassword.length());
        // System.out.println("Salt value = " + salt + salt.length());
    }
}
//sha5 rsa method
// import java.io.UnsupportedEncodingException;
// import java.security.InvalidKeyException;
// import java.security.KeyPair;
// import java.security.KeyPairGenerator;
// import java.security.NoSuchAlgorithmException;
// import java.security.PrivateKey;
// import java.security.PublicKey;
// import java.security.Signature;

// import javax.crypto.BadPaddingException;
// import javax.crypto.Cipher;
// import javax.crypto.IllegalBlockSizeException;
// import javax.crypto.NoSuchPaddingException;

// public class encryptPassword {

//     private PrivateKey privateKey;
//     private PublicKey publicKey;

//     public byte[] encrypt(String pass) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{

        
//     Signature sign = Signature.getInstance("SHA256withRSA");
      
//     //Creating KeyPair generator object
//     KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
    
//     //Initializing the key pair generator
//     keyPairGen.initialize(2048);
    
//     //Generating the pair of keys
//     KeyPair pair = keyPairGen.generateKeyPair();      

//     privateKey=pair.getPrivate();
//     publicKey=pair.getPublic();
  
//     // save keys in file

//     // try (FileOutputStream fos = new FileOutputStream("public.key")) {
//     //     fos.write(publicKey.getEncoded());
//     // }




//     //Creating a Cipher object
//     Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      
//     //Initializing a Cipher object
//     cipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());
    
//     //Adding data to the cipher
//     byte[] input = pass.getBytes();	  
//     cipher.update(input);
    
//     //encrypting the data
//     byte[] cipherText = cipher.doFinal();	 
//     //System.out.println(new String(cipherText, "UTF8"));
//     // String ciperText=new String(cipherText, "UTF8");
//     //System.out.println(ll.length());

//     return cipherText;
//     }

//     public String decrypt(byte[] encryptedMsg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
//         // Signature sign = Signature.getInstance("SHA256withRSA");
      
//         // //Creating KeyPair generator object
//         // KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");

//         // //Initializing the key pair generator
//         // keyPairGen.initialize(2048);

//         // //Generating the pair of keys
//         // KeyPair pair = keyPairGen.generateKeyPair();      

//         //Creating a Cipher object
//         Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//         cipher.init(Cipher.DECRYPT_MODE, privateKey);

//         //Decrypting the text
//         // byte[] cipherText=cipher.doFinal(cipherText);
//         byte[] decipheredText = cipher.doFinal(encryptedMsg);
//         // System.out.println(new String(decipheredText));
//         String plainText=new String(decipheredText);
//         return plainText;

//     }

//     public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
//         // encryptPassword process=new encryptPassword();
//         // byte[] op=process.encrypt("hello");
//         // System.out.println(op);
//         // System.out.println(process.decrypt(op));
//         // encryptPassword process2=new encryptPassword();

//         // byte[] op2=process2.encrypt("bye");
//         // System.out.println(op2);
//         // System.out.println(process2.decrypt(op2));
//         // // op=process.encrypt("hello");
//         // // System.out.println(op);
//         // System.out.println(process.decrypt(op));
//     }
// }



// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.security.*;
// import java.util.Base64;

// public class encryptPassword {

//     private PrivateKey privateKey;
//     private PublicKey publicKey;

//     public encryptPassword() throws NoSuchAlgorithmException {
//         KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//         keyGen.initialize(1024);
//         KeyPair pair = keyGen.generateKeyPair();
//         this.privateKey = pair.getPrivate();
//         this.publicKey = pair.getPublic();
//     }

//     public void writeToFile(String path, byte[] key) throws IOException {
//         File f = new File(path);
//         f.getParentFile().mkdirs();

//         FileOutputStream fos = new FileOutputStream(f);
//         fos.write(key);
//         fos.flush();
//         fos.close();
//     }

//     public PrivateKey getPrivateKey() {
//         return privateKey;
//     }

//     public PublicKey getPublicKey() {
//         return publicKey;
//     }
    
//     public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
//         encryptPassword keyPairGenerator = new encryptPassword();
//         keyPairGenerator.writeToFile("RSA/publicKey", keyPairGenerator.getPublicKey().getEncoded());
//         keyPairGenerator.writeToFile("RSA/privateKey", keyPairGenerator.getPrivateKey().getEncoded());
//         System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded()));
//         System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded()));
//     }
// }