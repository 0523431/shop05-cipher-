package cipher_util;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUnit {
	private static byte[] randomKey;
	private final static byte[] iv = new byte[] { (byte)0x8E, 0x12, 0x39,
												  (byte)0x9C, 0x07, 0x72, 0x6F,
												  (byte)0x5A,
												  (byte)0x8E, 0x12, 0x39,
												  (byte)0x9C, 0x07, 0x72, 0x6F,
												  (byte)0x5A };
	// static 초기화
	static Cipher cipher;
	static {
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 키를 랜덤으로 가져오기때문에, 실행시마다 다른 암호문이 나옴
	public static byte[] getRandomKey(String algo) throws NoSuchAlgorithmException {
		// keyGen : AES용 키를 생성할 수 있는 객체
		// AES 알고리즘 : 128bit-192bit를 키로 설정이 가능
		KeyGenerator keyGen = KeyGenerator.getInstance(algo);
		keyGen.init(128); // 128bit짜리 키로 생성
		SecretKey key = keyGen.generateKey();
		return key.getEncoded();
	}
	public static String encrypt(String plain) {
		byte[] cipherMsg = new byte[1024];
		try {
			// 알고리즘으로 AES를 넣음
			// 암호화에 필요한 키를 생성(랜덤으로?)
			randomKey = getRandomKey("AES");
			
			// key : 암호화에 필요한 키 객체
			Key key = new SecretKeySpec(randomKey, "AES");
			
			// 초기화 백터 생성
			// iv는 CBC(블록암호화모드)에서 필요한 이니셜백터
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			
			// Cipher.ENCRYPT_MODE : 암호화
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			// cipher.doFinal 암호화 실행
			cipherMsg = cipher.doFinal(plain.getBytes());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim(); // 문자열로 만들어
	}
	
	public static String encrypt(String plain, String key) {
		byte[] cipherMsg = new byte[1024];
		try {
			Key genKey = new SecretKeySpec(makeKey(key), "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			
			cipher.init(Cipher.ENCRYPT_MODE, genKey, paramSpec);
			cipherMsg = cipher.doFinal(plain.getBytes()); // plain.getBytes() : 바이트형 배열의 형태로 전달하고 암호화
		} catch(Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg);
		// cipherMsg는 바이트형 배열이니까 우리가 볼 수 있게 문자열(16진수)로 만들어 ==> byteToHex()
	}
	
	// AES용 키의 크기 : 128bit == 16byte
	private static byte[] makeKey(String key) {
		// key = abc1234567
		int len = key.length(); // len = 10;
		char ch ='A'; // 임의로 정함 (ch++이니까 'G'까지)
		for(int i=len; i<16; i++) {
			key += ch++;
			// key = abc1234567A
			// key = abc1234567AB
			// key = abc1234567ABC
			// key = abc1234ㄴ567ABCD
			// ..
			// key = abc1234567ABCDEF (i = 16) 최종 해쉬값
		}
		return key.substring(0, 16).getBytes(); // getBytes() : 바이트형 배열의 형태로 전달
	}
	
	// userid를 hash값으로 만들어주는 메서드
	public static String makehash(String userid) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] plain = userid.getBytes();
		byte[] hash = md.digest(plain);
		return byteToHex(hash);
	}

	private static String byteToHex(byte[] cipherMsg) {
		if(cipherMsg ==null) {
			return null;
		}
		String str ="";
		for(byte cm : cipherMsg) {
			str += String.format("%02X", cm);
		}
		return str;
	}
	
	public static String decrypt(String cipherMsg) {
		byte[] plainMsg = new byte[1024];
		try {
			Key key = new SecretKeySpec(randomKey, "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			
			// Cipher.DECRYPT_MODE : 복호화
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			// cipher.doFinal : 복호화 실행
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim())); // cipher.doFinal(buf)
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
	
	// 복호화
	public static String decrypt(String cipherMsg, String key) {
		byte[] plainMsg = new byte[1024];
		try {
			Key genKey = new SecretKeySpec(makeKey(key), "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			
			// 복호화 모드
			cipher.init(Cipher.DECRYPT_MODE, genKey, paramSpec);
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim())); // cipher.doFinal(buf)
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
	
	// 16진수를 byte형으로 만들어주는 메서드
	private static byte[] hexToByte(String str) {
		if(str ==null || str.length() <2) {
			return null;
		}
		// 1byte가 2글자씩 가지고 있으니까 (나누기 2)
		int len = str.length() /2;
		byte[] buf = new byte[len];
		for(int i=0; i<len; i++) {
			// 16진수를 byte형으로
			buf[i] = (byte)Integer.parseInt(str.substring(i *2, i*2+2), 16);
		}
		return buf; // byte형 배열
	}
}
