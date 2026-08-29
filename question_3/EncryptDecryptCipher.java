import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptDecryptCipher {
public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Usage: java RsaFileCipher <enc|dec> <keyFile> <inputFile> <outputFile>");
            return;
        }

        String action = args[0];
        String keyPath = args[1];
        String inFile = args[2];
        String outFile = args[3];

        // Clean up PEM wrapper tags
        String rawKey = new String(Files.readAllBytes(Paths.get(keyPath)))
                .replaceAll("-----[A-Z ]+-----", "")
                .replaceAll("\\s+", "");

        byte[] keyData = Base64.getDecoder().decode(rawKey);
        KeyFactory kFactory = KeyFactory.getInstance("RSA");
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

        byte[] fileBytes = Files.readAllBytes(Paths.get(inFile));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        if (action.equalsIgnoreCase("enc")) {
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(keyData);
            PublicKey pubKey = kFactory.generatePublic(pubSpec);
            rsaCipher.init(Cipher.ENCRYPT_MODE, pubKey);

            // Chunk data into max 190 bytes per block
            int chunkSize = 190;
            for (int i = 0; i < fileBytes.length; i += chunkSize) {
                int len = Math.min(chunkSize, fileBytes.length - i);
                buffer.write(rsaCipher.doFinal(fileBytes, i, len));
            }
        } else if (action.equalsIgnoreCase("dec")) {
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(keyData);
            PrivateKey privKey = kFactory.generatePrivate(privSpec);
            rsaCipher.init(Cipher.DECRYPT_MODE, privKey);

            // Process blocks (256 bytes per block)
            int blockSize = 256;
            for (int i = 0; i < fileBytes.length; i += blockSize) {
                int len = Math.min(blockSize, fileBytes.length - i);
                buffer.write(rsaCipher.doFinal(fileBytes, i, len));
            }
        } else {
            System.out.println("Invalid action! Choose 'enc' or 'dec'.");
            return;
        }

        Files.write(Paths.get(outFile), buffer.toByteArray());
        System.out.println("File processed and written to: " + outFile);
    }
}