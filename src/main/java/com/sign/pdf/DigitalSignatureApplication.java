package com.sign.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;


@SpringBootApplication
@EnableConfigurationProperties
public class DigitalSignatureApplication {

    public static void main(String[] args) throws KeyStoreException,
	NoSuchAlgorithmException, UnrecoverableKeyException,
	IOException, GeneralSecurityException{
        SpringApplication.run(DigitalSignatureApplication.class, args);
    }
}