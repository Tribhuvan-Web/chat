package com.chat.application.ChatApplication;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class aina {
    public static void main(String[] args) throws Exception {
        // Generate key pairs for sender and receiver (ECDH for key exchange)
        KeyPair senderKeyPair = generateKeyPair();
        KeyPair receiverKeyPair = generateKeyPair();

        // Generate shared secret (session key)
        SecretKey sessionKeySender = generateSharedSecret(senderKeyPair.getPrivate(), receiverKeyPair.getPublic());
        SecretKey sessionKeyReceiver = generateSharedSecret(receiverKeyPair.getPrivate(), senderKeyPair.getPublic());

        // Verify keys match
        assert Base64.getEncoder().encodeToString(sessionKeySender.getEncoded())
                .equals(Base64.getEncoder().encodeToString(sessionKeyReceiver.getEncoded()));

        String message = "This is a confidential message.";

        // Sender signs the message
        KeyPair signingKeyPair = generateSigningKeyPair();
        String signature = signMessage(message, signingKeyPair.getPrivate());

        // Encrypt message using AES
        String encryptedMessage = encryptMessage(message, sessionKeySender);

        // Send (encryptedMessage + signature)
        System.out.println("Encrypted Message: " + encryptedMessage);
        System.out.println("Digital Signature: " + signature);

        // Receiver decrypts message
        String decryptedMessage = decryptMessage(encryptedMessage, sessionKeyReceiver);

        // Verify the signature using sender's public key
        boolean isVerified = verifySignature(decryptedMessage, signature, signingKeyPair.getPublic());

        System.out.println("Decrypted Message: " + decryptedMessage);
        System.out.println("Signature Verified: " + isVerified);
    }

    // Generate an ECDH key pair (for key exchange)
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(256);
        return keyGen.generateKeyPair();
    }

    // Generate a shared secret key for AES encryption
    public static SecretKey generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        byte[] sharedSecret = keyAgreement.generateSecret();
        return new SecretKeySpec(sharedSecret, 0, 16, "AES"); // Use first 16 bytes for AES key
    }

    // Encrypt message using AES
    public static String encryptMessage(String message, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt message using AES
    public static String decryptMessage(String encryptedMessage, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }

    // Generate ECDSA key pair for signing
    public static KeyPair generateSigningKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        keyGen.initialize(256);
        return keyGen.generateKeyPair();
    }

    // Sign a message using ECDSA private key
    public static String signMessage(String message, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes());
        byte[] signedBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signedBytes);
    }

    // Verify the digital signature using the sender’s public key
    public static boolean verifySignature(String message, String signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(publicKey);
        sig.update(message.getBytes());
        return sig.verify(Base64.getDecoder().decode(signature));
    }
}
