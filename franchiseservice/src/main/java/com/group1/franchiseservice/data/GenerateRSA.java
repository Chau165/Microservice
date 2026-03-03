//package com.group1.franchiseservice.data;
//
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.util.Base64;
//
//public class GenerateRSA {
//
//    public static void main(String[] args) throws Exception {
//        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
//        generator.initialize(2048);
//        KeyPair pair = generator.generateKeyPair();
//
//        String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
//        String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
//
//        System.out.println("PUBLIC KEY:");
//        System.out.println(publicKey);
//
//        System.out.println("\nPRIVATE KEY:");
//        System.out.println(privateKey);
//    }
//}
