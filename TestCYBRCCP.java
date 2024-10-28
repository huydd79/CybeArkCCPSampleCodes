/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testcybrccp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

/**
 *
 * @author HuyDo@Cyberark.com
 */
public class TestCYBRCCP {
    
    public String getJSON(String url, int timeout, String pfxFile, String pfxPassword, String jksFile, String jksPassword, Boolean disableCertificateValidation) {
       
        HttpURLConnection c = null;

        try {            
            TrustManager[] tms;
            if (disableCertificateValidation) {
                //DDH Create a trust manager that does not validate certificate chains
                tms = new TrustManager [] {
                    new X509ExtendedTrustManager () {
                        @Override
                        public void checkClientTrusted (X509Certificate [] chain, String authType, Socket socket) {
                        }
                        @Override
                        public void checkServerTrusted (X509Certificate [] chain, String authType, Socket socket) {
                        }
                        @Override
                        public void checkClientTrusted (X509Certificate [] chain, String authType, SSLEngine engine) {
                        }
                        @Override
                        public void checkServerTrusted (X509Certificate [] chain, String authType, SSLEngine engine) {
                        }
                        @Override
                        public java.security.cert.X509Certificate [] getAcceptedIssuers () {
                           return null;
                        }
                        @Override
                        public void checkClientTrusted (X509Certificate [] certs, String authType) {
                        }
                        @Override
                        public void checkServerTrusted (X509Certificate [] certs, String authType) {
                        }
                    }
                };
            } else {
                //DDH Load trust store from file. In production, this should be configured in System properties with default truststore
                //Using below command to generate trust store and importing ca cert in
                //keytool.exe" -import -trustcacerts -file ca.cer -keystore trust.ccp.jks -alias "DC01-CA"
                KeyStore ts = KeyStore.getInstance("JKS");
                ts.load(new FileInputStream(jksFile), jksPassword.toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ts);
                tms = tmf.getTrustManagers();
            }        

            //DDH Load client certificate from p12 file. In production, this should be configured in System properties with default keystore
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(pfxFile), pfxPassword.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, pfxPassword.toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();         

            //DDH Loading SSL content with kms and tms
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(kms, tms, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:                    
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
                case 400:
                case 403:
                case 404:
                    br = new BufferedReader(new InputStreamReader(c.getErrorStream()));
                    sb = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();

                default:
                    return "Error code: " + status;
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(TestCYBRCCP.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
           if (c != null) {
              try {
                  c.disconnect();
              } catch (Exception ex) {
                 Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
              }
           }
        }
        return null;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String url="https://172.16.100.21/AIMWebService/api/Accounts?AppID=TestCCP-HUYDO&Safe=TestingCP_Safe&Folder=Root&Object=testcp01";
        String pfxFile="/Users/Huy.Do/Documents/HUYDO/Working/Coding/TestCybrCCP/test/clients.cybr.huydo.net.pfx";
        String pfxPassword="ChangeMe123!";
        String jksFile="/Users/Huy.Do/Documents/HUYDO/Working/Coding/TestCybrCCP/test/trust.ccp.jks";
        String jksPassword="ChangeMe123!";
        
        //DDH Disable server certificate checking. Not recommend for production env
        Boolean disableCertificateValidation = true;
        
        if (args.length == 6){
            System.out.println("Getting params from CLI...");
            url = args[0];
            pfxFile = args[1];
            pfxPassword = args[2];
            jksFile = args[3];
            jksPassword = args[4];
            if ( args[5].equalsIgnoreCase("true")) {
                disableCertificateValidation = true;
            }
        } else {
            System.out.println("========== USAGE ==========");
            System.out.println("RUNNING ARGS: <URL> <timeout> <pfxFile> <pfxPassword> <jksFile> <jksPassword> <disableCertificateValidation>");
            System.out.println("EXAMPLE: '" + url + "' " + pfxFile + " " + pfxPassword + " " + jksFile + " " + jksPassword + " " + disableCertificateValidation);
            System.out.println("Using example...");
        }
 
        int timeout=300;

        TestCYBRCCP test = new TestCYBRCCP ();
        String json = test.getJSON(url, timeout, pfxFile, pfxPassword, jksFile, jksPassword, disableCertificateValidation );
        System.out.println(json);
    }
}
