import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class RsaKeyGenerator {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(pair.getPublic().getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(pair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";

        try (FileOutputStream out = new FileOutputStream("public.key")) {
            out.write(publicKeyPem.getBytes());
        }
        try (FileOutputStream out = new FileOutputStream("private.key")) {
            out.write(privateKeyPem.getBytes());
        }

        System.out.println("Keys successfully saved to 'public.key' and 'private.key'.");
    }
}