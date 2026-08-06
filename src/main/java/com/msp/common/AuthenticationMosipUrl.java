package com.msp.common;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ResourceUtils;

import javax.crypto.*;
import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

//import static com.msp.controller.RPController.UNQUESTIONING_TRUST_MANAGER;


@Configuration
public class AuthenticationMosipUrl {

    Logger logger = LoggerFactory.getLogger(AuthenticationMosipUrl.class);

    @Autowired
    Encryption encrypt = new Encryption();

    private static BouncyCastleProvider bouncyCastleProvider;
    private static final String TEMPLATE = "{\"consentObtained\": \"true\", \"id\": \"mosip.identity.auth\",\"individualId\": \"$id\",\"individualIdType\": \"$idType\",\"keyIndex\": \"string\",\"request\": \"$encRequest\",\"requestHMAC\": \"$encHmac\",\"requestSessionKey\": \"$encSessionKey\",\"requestTime\": \"$timestamp\",\"requestedAuth\": {\"bio\": \"false\",\"demo\": \"false\",\"otp\": \"false\",\"pin\": \"false\"},\"transactionID\": \"$txn\",\"version\": \"$ver\",\"domainUri\": \"$domainUri\",\"env\": \"$env\",\"specVersion\": \"1.0\",\"thumbprint\": \"$thumbprint\"}";
    private static int UIN_LENGTH = 0;
    private static int VID_LENGTH = 0;
    private static final String VERSION = "1.0";

    private static final String ENV = "Pre-Production";

    private String CERT_THUMBPRINT = "";

    private static final char[] TEMP_P12_PWD = "qwerty@123".toCharArray();
    private static String REQUEST_SESSIONKEY = "";

    private String BASE_URL;
    private String AUTH_URL;
    private String API_VERSION;
    private String API_KEY;
    private String MISP_KEY;
    private String PARTNER_ID;
    private String DEBUG_LEVEL;
    private String TSP_URL;
    private int MINUTES;
    private Long SEC;
    public String TOKEN;
    public LocalDateTime EXPIREED_DATE;

    static {
        bouncyCastleProvider = addProvider();
    }

    public AuthenticationMosipUrl() throws NoSuchAlgorithmException {
    }

    public void regProAuth() throws IOException, NoSuchAlgorithmException, KeyManagementException {

        ResourceBundle bundle = readPropertyFile();
        MINUTES = Integer.parseInt(bundle.getString("minute"));
        SEC = Long.valueOf(bundle.getString("sec"));
        String token = "";
        String json_Inputs = "{\n" +
                "  \"id\": \"string\",\n" +
                "  \"metadata\": {},\n" +
                "  \"request\": {\n" +
                "    \"appId\": \"admin\",\n" +
                "    \"password\": \"mosip\",\n" +
                "    \"userName\": \"110011\"\n" +
                "  },\n" +
                "  \"requesttime\": \"2018-12-10T06:12:52.994Z\",\n" +
                "  \"version\": \"string\"\n" +
                "}";

        String https_url = AUTH_URL+"/v1/authmanager/authenticate/useridPwd";
        String response = "";
        logger.info("Authentication URL : "+https_url);
        try{
            turnOffSslChecking();
            URL url = new URL(https_url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Connection","keep-alive");
            urlConnection.setRequestProperty("Content-Type","application/json; utf-8");
            urlConnection.setRequestProperty("Accept","application/json");
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(json_Inputs.getBytes());
            outputStream.flush();
            if(urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() < 400){
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                TOKEN = urlConnection.getHeaderField("Authorization");
                EXPIREED_DATE= encrypt.getUTCCurrentDateTime().plusMinutes(MINUTES).plusSeconds(SEC);
                String tempstr =null;
                while ((tempstr = reader.readLine()) != null){
                    response = tempstr;
                }
            }
            logger.info("Mosip Authentication response code :"+urlConnection.getResponseCode());
        }catch (Exception e){
            logger.error("Mosip authenticate error :"+e.toString());
        }

    }

    public String getRecordFromIDreprosity(String authorization , long uin , String typeAuth) throws MalformedURLException {
        String https_url = BASE_URL+"/idrepository/v1/identity/idvid/"+uin+"?type="+typeAuth;

        String response = "";
        String myCookie = "Authorization="+authorization;
        String responseValue = "";
        try {
            URL url = new URL(https_url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Cookie", myCookie);

            if (urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() < 400) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String tempstr = null;
                while ((tempstr = reader.readLine()) != null) {
                    response = tempstr;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }
    public String oneStepAuth(String request,long uin , String authToken){


        String https_url = "http://192.168.1.159:8082/v1/identity/authenticate?id="+uin+"&idType=UIN&partnerUrlSuffix=ntBPTyhadGBtLAMoiOFYpAnn6hnW6qCNuUPRjTySD5iZ0F3zlf/127/768192";
        String response = "";
        String createAuth = "";
        // Instead of this partner321, read partner id , polcy id, policy Group id,all should be give in properties.

        try {
            URL url = new URL(https_url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");


            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(request.getBytes());
            outputStream.flush();

            if (urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() < 400) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String tempstr = null;
                while ((tempstr = reader.readLine()) != null) {
                    response = tempstr;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return createAuth;
    }
    public String downloadCBFFile(String request){
        String https_url = "http://192.168.1.159:8082/v1/identity/decodeFile?fileName=test8";
        String response = "";

        try {
            URL url = new URL(https_url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", "text/plain");
            urlConnection.setRequestProperty("Accept", "*/*");

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(request.getBytes());
            outputStream.flush();

            if (urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() < 400) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String tempstr = null;
                while ((tempstr = reader.readLine()) != null) {
                    response = tempstr;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }



    public String idAuthRequest(String request, String signature){

        String https_url = BASE_URL+"/idauthentication/v1/auth/"+MISP_KEY+"/"+PARTNER_ID+"/"+API_KEY;


        String response = "";
        String myCookie = "Authorization="+TOKEN;

        try {
            turnOffSslChecking();
            URL url = new URL(https_url);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Cookie", myCookie);
            urlConnection.setRequestProperty("Authorization", TOKEN);
            urlConnection.setRequestProperty("signature",signature);
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(request.getBytes());
            outputStream.flush();
            if (DEBUG_LEVEL.equals("0")){
                logger.info("IDAuthentication URL : "+BASE_URL +"/idauthentication/v1/auth/"+MISP_KEY+"/"+PARTNER_ID+"/"+API_KEY +" - Encryption data.");
            }else if(DEBUG_LEVEL.equals("1")){
                logger.info("IDAuthentication URL : "+BASE_URL +"/idauthentication/v1/auth/"+MISP_KEY+"/"+PARTNER_ID+"/"+API_KEY +" - "+request);
            }
            logger.info("Mosip IDAuthentication Response Code :" + urlConnection.getResponseCode());
            if (urlConnection.getResponseCode() >= 200 && urlConnection.getResponseCode() < 400) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String tempstr = null;
                while ((tempstr = reader.readLine()) != null) {
                    response = tempstr;
                }
            }
            logger.info("IDAuthentication-Response :"+response);
        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public String ownCreateAuth(String authDetail, String UIrequest) throws CertificateException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, JoseException, UnrecoverableEntryException, KeyStoreException, OperatorCreationException, ParseException, JSONException {
        JSONObject jsoninput = new JSONObject(authDetail);
        JSONParser jsonParser=new JSONParser();
        Long individualid = (Long) jsoninput.get("pcn");
        String individual_id = String.valueOf(individualid);
        JSONObject jsonObject = new JSONObject(TEMPLATE);
        try{
            jsonObject.put("individualId" , individual_id);
            if (individual_id.length() == UIN_LENGTH) {
                jsonObject.put("individualIdType" , "UIN");
            } else if (individual_id.length() == VID_LENGTH){
                jsonObject.put("individualIdType" , "VID");
            }
            jsonObject.put("version",VERSION);
            jsonObject.put("env",ENV);
            jsonObject.put("domainUri",BASE_URL);
            jsonObject.put("transactionID",jsoninput.get("transactionID"));
            logger.info("Request recived from IDAUI - " +jsoninput.get("transactionID"));
            jsonObject.put("requestTime", encrypt.formatToISOString(encrypt.getUTCCurrentDateTime()));
            JSONObject requestAuthJson = new JSONObject();
            requestAuthJson = (JSONObject) jsonObject.get("requestedAuth");
            if(jsoninput.get("idType").equals("demo")){
                requestAuthJson.put("demo",true);
            } else{
                requestAuthJson.put("bio",true);
            }
            SecretKey secretKey = genSecKey();
            byte[] encryptedIdentity = encrypt.symmetricEncrypt(UIrequest.getBytes() , secretKey);
            jsonObject.put( "request" , Base64.encodeBase64URLSafeString(encryptedIdentity));

            PublicKey publicKey = encrypt.getPartnerPublicKey();
            byte[] requestSessionKey = encrypt.asymmetricEncrypt(secretKey.getEncoded(),publicKey);
            jsonObject.put("requestSessionKey" , Base64.encodeBase64URLSafeString(requestSessionKey));

            byte[] requestHMAC = encrypt.symmetricEncrypt(Encryption.
                    digestAsPlainText(encrypt.generateHash(UIrequest.getBytes(StandardCharsets.UTF_8))).getBytes(),secretKey);
            jsonObject.put("requestHMAC" ,Base64.encodeBase64URLSafeString(requestHMAC));

            jsonObject.put( "thumbprint", CERT_THUMBPRINT);

        }catch (Exception e){
            logger.info("Error in utility build :"+e.toString());
        }
        String rpSignature = sign(jsonObject.toString(), false,
                true, false, null, "rp");
        return idAuthRequest(jsonObject.toString(),rpSignature);
    }

    public String sign(String dataToSign, boolean includePayload,
                       boolean includeCertificate, boolean includeCertHash, String certificateUrl,
                       String partnerType) throws JoseException, NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, CertificateException, IOException, OperatorCreationException {

        JsonWebSignature jwSign = new JsonWebSignature();
        PrivateKeyEntry keyEntry = null;
        ClassLoader classLoader = getClass().getClassLoader();
        File file = ResourceUtils.getFile("classpath:write.txt");
        InputStream in = new FileInputStream(file);
//        File file = new File(classLoader.getResource("write").getFile());
//        String filePath = new FileSystemResource("").getFile().getAbsolutePath()+ "/write.txt";
//        Path path = Paths.get(filePath);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try(InputStream p12FileStream = new FileInputStream(file )) {
            keyStore.load(p12FileStream, TEMP_P12_PWD);
            keyEntry = (PrivateKeyEntry) keyStore.getEntry("keyalias", new KeyStore.PasswordProtection(TEMP_P12_PWD));
        }
        PrivateKey privateKey = keyEntry.getPrivateKey();
        X509Certificate x509Certificate = (X509Certificate) keyEntry.getCertificate();
        PublicKey pk=x509Certificate.getPublicKey();
        if (includeCertificate)
            jwSign.setCertificateChainHeaderValue(new X509Certificate[] { x509Certificate });
        if (includeCertHash)
            jwSign.setX509CertSha256ThumbprintHeaderValue(x509Certificate);

        if (Objects.nonNull(certificateUrl))
            jwSign.setHeader("x5u", certificateUrl);
        jwSign.setPayload(dataToSign);
        jwSign.setAlgorithmHeaderValue("RS256");
        jwSign.setKey(privateKey);
        jwSign.setDoKeyValidation(false);
        if (includePayload)
            return jwSign.getCompactSerialization();
        return jwSign.getDetachedContentCompactSerialization();
    }

    public PublicKey getPartnerPublicKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        String filePath = new FileSystemResource("").getFile().getAbsolutePath()+ "/Certificate/rp-partner.p12";
        Path path = Paths.get(filePath);
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        PublicKey pk = null;
        try(InputStream p12FileStream = new FileInputStream(filePath )) {
            keyStore.load(p12FileStream, TEMP_P12_PWD);
            PrivateKeyEntry keyEntry = (PrivateKeyEntry) keyStore.getEntry("keyalias", new KeyStore.PasswordProtection(TEMP_P12_PWD));
            X509Certificate x509Certificate = (X509Certificate) keyEntry.getCertificate();
            pk = x509Certificate.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pk;
    }

    public SecretKey genSecKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen;
        SecretKey secretKey = null;
        keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, new SecureRandom());
        secretKey = keyGen.generateKey();
        return secretKey;
    }

    public String generateTransactionID(){
        String transactionID = "";
        int min = 111111,max = 999999;

        String randNumber = String.valueOf( (int) Math.floor(Math.random() * (max - min + 1) + min));
        LocalDateTime dateTime1 = Encryption.getUTCCurrentDateTime();
        String dateTime = dateTime1.format(DateTimeFormatter.ofPattern("ddMMHHmmss"));

        String TempPartnerId = PARTNER_ID;
        if (TempPartnerId.length() > 3){
            TempPartnerId = transactionID.substring(0,3);
        }else if (TempPartnerId.length() < 3){
              for (int i=0; i < 3; i++){
                  TempPartnerId = "0" + TempPartnerId;
                  if (TempPartnerId.length() == 3){
                      break;
                  }
              }
        }

        transactionID = dateTime ;

        return transactionID;
    }

    private static BouncyCastleProvider addProvider() {
        BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
        Security.addProvider(bouncyCastleProvider);
        return bouncyCastleProvider;
    }

    public ResourceBundle readPropertyFile() throws IOException {
        FileInputStream  filePath = new FileInputStream(new FileSystemResource("").getFile().getAbsolutePath()+"//PSA_RP.conf");
        ResourceBundle  bundle = new PropertyResourceBundle(filePath);

        API_KEY = bundle.getString("apikey");

        PARTNER_ID = bundle.getString("partnerid");
        UIN_LENGTH = Integer.parseInt(bundle.getString("uin_length"));
        VID_LENGTH = Integer.parseInt(bundle.getString("vid_length"));
        TSP_URL = bundle.getString("tspurl");

        DEBUG_LEVEL = bundle.getString("debuglevel");
        MINUTES = Integer.parseInt(bundle.getString("minute"));
        SEC = Long.valueOf((bundle.getString("sec")));

        return bundle;
    }

    public static void turnOffSslChecking() throws NoSuchAlgorithmException, KeyManagementException {

        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, UNQUESTIONING_TRUST_MANAGER, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static final TrustManager[] UNQUESTIONING_TRUST_MANAGER = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        /* (non-Javadoc)
         * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
         */
        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws java.security.cert.CertificateException {
            // TODO Auto-generated method stub
        }
    }};

}
