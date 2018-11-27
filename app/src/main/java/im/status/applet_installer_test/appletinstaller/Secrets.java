package im.status.applet_installer_test.appletinstaller;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import static android.util.Base64.NO_PADDING;

public class Secrets {
    private String pin;
    private String puk;
    private String pairingPassword;
    private byte[] pairingToken;

    private static long PIN_BOUND = 999999L;
    private static long PUK_BOUND = 999999999999L;

    public Secrets(String pin, String puk, String pairingPassword, byte[] pairingToken) {
        this.pin = pin;
        this.puk = puk;
        this.pairingPassword = pairingPassword;
        this.pairingToken = pairingToken;
    }

    @NonNull
    public static Secrets generate() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pairingPassword = randomToken(12);
        byte[] pairingToken = generatePairingKey(pairingPassword.toCharArray());
        long pinNumber = randomLong(PIN_BOUND);
        long pukNumber = randomLong(PUK_BOUND);
        String pin = String.format("%06d", pinNumber);
        String puk = String.format("%012d", pukNumber);

        return new Secrets(pin, puk, pairingPassword, pairingToken);
    }

    public static Secrets testSecrets() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pairingPassword = "WalletAppletTest";
        byte[] pairingToken = generatePairingKey(pairingPassword.toCharArray());
        return new Secrets("000000", "123456789012", pairingPassword, pairingToken);
    }

    public String getPin() {
        return pin;
    }

    public String getPuk() {
        return puk;
    }

    public String getPairingPassword() {
        return pairingPassword;
    }

    public byte[] getPairingToken() {
        return pairingToken;
    }

    public static byte[] randomBytes(int length) {
        SecureRandom random = new SecureRandom();
        byte data[] = new byte[length];
        random.nextBytes(data);

        return data;
    }

    public static String randomToken(int length) {
        return Base64.encodeToString(randomBytes(length), NO_PADDING);
    }

    public static long randomLong(long bound) {
        SecureRandom random = new SecureRandom();
        return Math.abs(random.nextLong()) % bound;
    }

    public static byte[] generatePairingKey(char[] pairing) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        String salt = "Status Hardware Wallet Lite";
        PBEKeySpec spec = new PBEKeySpec(pairing, salt.getBytes(), 50000, 32*8);
        SecretKey key = skf.generateSecret(spec);

        return key.getEncoded();
    }
}