package com;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Data;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SheetsQuickstart {
  private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  /**
   * Global instance of the scopes required by this quickstart.
   * If modifying these scopes, delete your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES =
      Collections.singletonList(SheetsScopes.SPREADSHEETS);
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  public static String getNewToken(String refreshToken, String clientId, String clientSecret) throws IOException {
     
      TokenResponse tokenResponse = new GoogleRefreshTokenRequest(new NetHttpTransport(), new JacksonFactory(),
              refreshToken, clientId, clientSecret).setScopes(SCOPES).setGrantType("refresh_token").execute();

      return tokenResponse.getAccessToken();
}
  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
      throws IOException {
    // Load client secrets.
    InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(9006).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }
  
  public static Credential createCredentialWithAccessTokenOnly( HttpTransport transport, JsonFactory jsonFactory, TokenResponse tokenResponse) 
  { return new Credential(BearerToken.authorizationHeaderAccessMethod()).setFromTokenResponse( tokenResponse); }

  /**
   * Prints the names and majors of students in a sample spreadsheet:
   * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
   */
  public static void main(String... args) throws IOException, GeneralSecurityException {
    // Build a new authorized API client service.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    final String spreadsheetId = "1k_oHYpVggl4MSVGV1eQhAw1uNK1axdFDzc5Wx76Xpyg";
    final String range = "A:E";
    
    //TokenResponse response = new TokenRequest(new NetHttpTransport(), JSON_FACTORY, new GenericUrl("https://oauth2.googleapis.com/token"), "refresh_token" ).execute();
    //System.out.println(response.getAccessToken());
    //Credential cred = getCredentials(HTTP_TRANSPORT);
    //System.out.println(cred.getRefreshToken());
    //System.out.println(getNewToken(cred.getRefreshToken(), "856666921944-jv976uitb5hus1iaq1nijqofsvpvp6pm.apps.googleusercontent.com","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"));
    
    //TokenResponse response = new TokenRequest(HTTP_TRANSPORT, JSON_FACTORY, ).execute();
    
    
    Sheets service =
        new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build();
    
    
    System.out.println("Write");
    
    String[] s = {"Please God Help", "meh", "meh2"};
    List<String> sample = Arrays.asList(s);
    List<Object> objectList = new ArrayList<Object>(sample);
    
    
    Object obj = s;
    //Object obj1 = s1;
    
    List<List<Object>> values = Arrays.asList(
	        objectList
	        //,Arrays.asList(obj1)
	        );
    
    //HttpResponse response = service.spreadsheets().values().execute();
    
    ValueRange body = new ValueRange()
    	      .setValues(values);
    	    UpdateValuesResponse result = service.spreadsheets().values()
    	      .update(spreadsheetId, range, body)
    	      .setValueInputOption("USER_ENTERED")
    	      .execute();
    	    

    
    //--------------------------------------------------------------------------------
    
    
    /*ValueRange requestBody = new ValueRange();
    
    String valueInputOption = "Hello";
    
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								    Sheets.Spreadsheets.Values.Update req = service.spreadsheets().values()
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								            .update(spreadsheetId, range, requestBody);
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								    
    req.setValueInputOption("RAW");
    
    UpdateValuesResponse res = req.execute();
   
    System.out.println(res);*/
    
    //-----------------------------------------------------------------------------------
    
   /* System.out.println("Read");
    Sheets.Spreadsheets.Values.Get request =
            service.spreadsheets().values().get(spreadsheetId, range);
        //request.setValueRenderOption(valueRenderOption);
        //request.setDateTimeRenderOption(dateTimeRenderOption);
    ValueRange response = request.execute();

    // TODO: Change code below to process the `response` object:
    System.out.println(response);*/
        
        
    
    
  }
}