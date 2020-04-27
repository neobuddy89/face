package co.aospa.facesense.util;

import android.security.keystore.KeyProtection.Builder;
import android.util.Log;
import com.megvii.facepp.sdk.UnlockEncryptor;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SenseDataEncryptor implements UnlockEncryptor {
    public SenseDataEncryptor() {
        saveSeed();
    }

    private boolean saveSeed() {
        try {
            KeyStore instance = KeyStore.getInstance("AndroidKeyStore");
            instance.load(null);
            if (instance.containsAlias("seed_paranoid_facesense")) {
                Log.i("SenseDataEncryptor", "key is already created");
                return true;
            }
            KeyGenerator instance2 = KeyGenerator.getInstance("AES");
            instance2.init(new SecureRandom());
            instance.setEntry("seed_paranoid_facesense", new SecretKeyEntry(instance2.generateKey()), new Builder(3).setBlockModes(new String[]{"GCM"}).setUserAuthenticationRequired(false).setEncryptionPaddings(new String[]{"NoPadding"}).build());
            Log.i("SenseDataEncryptor", "create key successfully");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SenseDataEncryptor", "Exception in store. " + e.toString());
            return false;
        }
    }

    private byte[] encryptData(byte[] bArr) {
        SecretKey secretKey;
        if (Util.DEBUG_INFO) {
            Log.i("SenseDataEncryptor", "encryptData");
        }
        if (bArr == null) {
            return null;
        }
        try {
            KeyStore instance = KeyStore.getInstance("AndroidKeyStore");
            instance.load(null);
            if (instance.containsAlias("seed_paranoid_facesense")) {
                secretKey = (SecretKey) instance.getKey("seed_paranoid_facesense", null);
            } else {
                Log.i("SenseDataEncryptor", "key not exist, create key!");
                saveSeed();
                secretKey = (SecretKey) instance.getKey("seed_paranoid_facesense", null);
            }
            if (secretKey != null) {
                Cipher instance2 = Cipher.getInstance("AES/GCM/NoPadding");
                instance2.init(1, secretKey);
                byte[] doFinal = instance2.doFinal(bArr);
                byte[] iv = instance2.getIV();
                if (iv.length == 12) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byteArrayOutputStream.write(iv);
                    byteArrayOutputStream.write(doFinal);
                    return byteArrayOutputStream.toByteArray();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SenseDataEncryptor", "Exception in encrypt. " + e.toString());
        }
        return new byte[0];
    }

    private byte[] decryptData(byte[] bArr) {
        if (Util.DEBUG_INFO) {
            Log.i("SenseDataEncryptor", "decryptData");
        }
        SecretKey secretKey = null;
        if (bArr == null) {
            return null;
        }
        try {
            KeyStore instance = KeyStore.getInstance("AndroidKeyStore");
            instance.load(null);
            if (instance.containsAlias("seed_paranoid_facesense")) {
                secretKey = (SecretKey) instance.getKey("seed_paranoid_facesense", null);
            } else {
                Log.e("SenseDataEncryptor", "key not exist, something is wrong!");
            }
            if (secretKey != null) {
                byte[] copyOfRange = Arrays.copyOfRange(bArr, 0, 12);
                byte[] copyOfRange2 = Arrays.copyOfRange(bArr, 12, bArr.length);
                Cipher instance2 = Cipher.getInstance("AES/GCM/NoPadding");
                instance2.init(2, secretKey, new GCMParameterSpec(128, copyOfRange));
                return instance2.doFinal(copyOfRange2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SenseDataEncryptor", "Exception in decrypt. " + e.toString());
        }
        return new byte[0];
    }

    public byte[] encrypt(byte[] bArr) {
        return encryptData(bArr);
    }

    public byte[] decrypt(byte[] bArr) {
        return decryptData(bArr);
    }
}
