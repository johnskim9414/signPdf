package com.sign.pdf;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;




public class CustomSignature implements SignatureInterface {
   
    CustomSignature()
    {
    }

    @Override
    public byte[] sign(InputStream content) throws IOException {
        MessageDigest md;
        try {
            
            /*If the next two lines perfoming hash is commented 
            and the content is directly passed to  CMSProcessableByteArray in line 67,
            everything works fine. The signature is said to be valid when the document is viewed through Adobe.*/
            md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest();

            /* Start : This is code snippet of the digital signature logic residing on server B */
            Security.addProvider(new BouncyCastleFipsProvider());
            X509Certificate cert = getCertificate();
            Certificate[] certificateChain = new Certificate[1];
            certificateChain[0] = cert;
            Store certs = new JcaCertStore(Arrays.asList(certificateChain));
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            ContentSigner sha256Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(getPrivateKey());
            gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                new JcaDigestCalculatorProviderBuilder().build()).build(sha256Signer, cert));
            gen.addCertificates(certs);
            CMSTypedData cmsData = new CMSProcessableByteArray(hash);
            CMSSignedData cms = gen.generate(cmsData, false);
            byte[] signedHash = cms.getEncoded(); 
            /* End : This is code snippet of the digital signature logic residing on server B */
            return signedHash;
        } catch (CertificateException | OperatorCreationException | CMSException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    } 

    public PrivateKey getPrivateKey() throws IOException {

        File file = new File("privateKey.key");
        FileReader reader = new FileReader(file);

        try (PEMParser pemParser = new PEMParser(reader)) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKey privateKey = converter.getPrivateKey(privateKeyInfo);
            return privateKey;
        }
	}

	public X509Certificate getCertificate() throws CertificateException, FileNotFoundException {
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
        FileInputStream fis = new FileInputStream("certificate.crt");
		X509Certificate endUserCertificate = (X509Certificate) certificateFactory.generateCertificate(fis);
		return endUserCertificate;
	}
   }