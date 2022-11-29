package com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.protobuf.ByteString;


public class receipt_parser {
	
	static ConfigParser cp;
	/**
	   * Global instance of the scopes required by this quickstart.
	   * If modifying these scopes, delete your previously saved tokens/ folder.
	   */
	private static final List<String> SCOPES =
	Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	
	
	
	
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String CREDENTIALS_FILE_PATH)
		      throws IOException {
		    // Load client secrets.
		    InputStream in = receipt_parser.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
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
	
	/**
	   * Appends values to a spreadsheet.
	   *
	   * @param spreadsheetId    - Id of the spreadsheet.
	   * @param range            - Range of cells of the spreadsheet.
	   * @param valueInputOption - Determines how input data should be interpreted.
	   * @param values           - list of rows of values to input.
	   * @return spreadsheet with appended values
	   * @throws IOException - if credentials file not found.
	   */
	  public static AppendValuesResponse appendValues(String spreadsheetId,
	                                                  String range,
	                                                  String valueInputOption,
	                                                  List<List<Object>> values,
	                                                  Sheets service)
	      throws IOException {
		  
		  AppendValuesResponse result = null;
		    try {
		      // Append values to the specified range.
		      ValueRange body = new ValueRange()
		          .setValues(values);
		      result = service.spreadsheets().values().append(spreadsheetId, range, body)
		          .setValueInputOption(valueInputOption)
		          .execute();
		      // Prints the spreadsheet with appended values.
		      System.out.printf("%d cells appended.", result.getUpdates().getUpdatedCells());
		    } catch (GoogleJsonResponseException e) {
		      // TODO(developer) - handle error appropriately
		      GoogleJsonError error = e.getDetails();
		      if (error.getCode() == 404) {
		        System.out.printf("Spreadsheet not found with id '%s'.\n", spreadsheetId);
		      } else {
		        throw e;
		      }
		    }
		    return result;
		  }
	  
	
	

	public static Namespace parse_arguments(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor("receipt_parser").build()
                .defaultHelp(true)
                .description("Parses receipts");
		parser.addArgument("--settings").nargs("*")
        .help("Provide path to settings file")
        .required(true)
        .dest("settings");
		Namespace ns = null;
		try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        MessageDigest digest = null;
	
		return ns;
	}
	
	
	public static void main(String[] arguments)  throws IOException, GeneralSecurityException{
		String CREDENTIALS_FILE_PATH;
		Credential creds = null;
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Namespace args = parse_arguments(arguments);
		Path path = null;
		for(String name: args.<String> getList("settings")) {
			path = Paths.get(name);
		}
		
		try {
			cp = new ConfigParser(path.toString(), '=');
			cp.read();
		}
		catch (IOException e) {
            System.err
                    .printf("%s: failed to read data: %s", e.getMessage());   
		}
		 //System.out.println("The Value is: " + cp.get("spreadsheet_id"));
		String spreadsheet_id = cp.get("spreadsheet_id");
		String spreadsheet_range = cp.get("spreadsheet_range");
		String oauth_token_path = cp.get("oauth_token");
		String receipts_base_path = cp.get("receipts_path");
		
		//System.out.println(oauth_token_path);
		//Path oauth_path = Paths.get(oauth_token_path.trim());
		//System.out.println(oauth_path);
		CREDENTIALS_FILE_PATH = oauth_token_path.toString().trim();
		System.out.println(CREDENTIALS_FILE_PATH);
		creds = getCredentials(HTTP_TRANSPORT,CREDENTIALS_FILE_PATH);
		
		Sheets service =
			        new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, creds)
			            .setApplicationName(APPLICATION_NAME)
			            .build();
		
		File dir = new File(receipts_base_path.trim());
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		      // Do something with child
		    	//File fi = new File(filePath);
		        byte[] fileContent = Files.readAllBytes(child.toPath());
		        ByteString file_bytes = ByteString.copyFrom(fileContent);
		        List<List<Object>> body = DetectTextGcs.parse(DetectTextGcs.detectTextGcs(file_bytes));
		        appendValues(spreadsheet_id.trim(), spreadsheet_range.trim(), "USER_ENTERED",body, service);
		        //find_date(detectTextGcs());
		        //find_market(detectTextGcs());
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
		  }
		
		 
		/* System.out.println("Write");
		    
		    String s = "TEst2";
		    String s1 = "Naked";
		    
		    
		    Object obj = s;
		    Object obj1 = s1;
		    
		    List<List<Object>> values = Arrays.asList(
			        Arrays.asList(obj),
			        Arrays.asList(obj1)
			        );
		    
		    //HttpResponse response = service.spreadsheets().values().execute();
		    System.out.println(spreadsheet_id);
		    System.out.println(spreadsheet_range);
		    ValueRange body = new ValueRange()
		    	      .setValues(values);
		    	    UpdateValuesResponse result = service.spreadsheets().values()
		    	      .update(spreadsheet_id.trim(), spreadsheet_range.trim(), body)
		    	      .setValueInputOption("USER_ENTERED")
		    	      .execute();*/
		    	    
		
		//System.out.println(creds.handleResponse(null, null, false));
		
		
		
	}
}
