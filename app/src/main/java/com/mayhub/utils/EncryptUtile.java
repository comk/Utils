package com.mayhub.utils;


import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtile {

	private static Cipher cipher;
	private static SecretKeySpec key;
	private static AlgorithmParameterSpec spec;
	private static final String SEED_16_CHARACTER = "T15jPCM09OOUO.QO";

	static {
		// hash password with SHA-256 and crop the output to 128-bit for key
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.update(SEED_16_CHARACTER.getBytes("UTF-8"));
			byte[] keyBytes = new byte[32];
			System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// cipher = Cipher.getInstance("AES");
			key = new SecretKeySpec(keyBytes, "AES");
			spec = getIV();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public static AlgorithmParameterSpec getIV() {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
		IvParameterSpec ivParameterSpec;
		ivParameterSpec = new IvParameterSpec(iv);
		return ivParameterSpec;
	}

	public static String encrypt(String plainText, String salt) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, getKey(salt), spec);
		byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
		String encryptedText = new String(Base64.encodeBase64(encrypted),
				"UTF-8");
		return encryptedText;
	}

	public static String decrypt(String cryptedText, String salt) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, getKey(salt), spec);
		byte[] bytes = Base64.decodeBase64(cryptedText.getBytes());
		byte[] decrypted = cipher.doFinal(bytes);
		String decryptedText = new String(decrypted, "UTF-8");
		return decryptedText;
	}

	private static SecretKeySpec getKey (String salt) {
		MessageDigest digest = null;
		try {
			StringBuilder sb = new StringBuilder(SEED_16_CHARACTER);
			sb.insert(5,salt);
			digest = MessageDigest.getInstance("SHA-256");
			digest.update(sb.toString().getBytes("UTF-8"));
			byte[] keyBytes = new byte[32];
			System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
			return new SecretKeySpec(keyBytes, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
