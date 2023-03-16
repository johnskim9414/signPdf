package com.sign.pdf;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/pdf")
@Slf4j
public class PdfResources {

    private final SigningService signingService;

    public PdfResources(SigningService signingService) {
        this.signingService = signingService;
    }

    @GetMapping(value = "/export", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity exportPdf() throws KeyStoreException,
	NoSuchAlgorithmException, UnrecoverableKeyException,
	IOException, GeneralSecurityException, OperatorCreationException, CMSException{
        try {
            
            File pdfFile = new File("Sample_Acceptance_Letter.pdf");
            byte[] signedPdf = this.signingService.signPdf(pdfFile);
            return ResponseEntity.ok(signedPdf);
        } catch (IOException e) {
            log.error("Cannot generate PDF file", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}