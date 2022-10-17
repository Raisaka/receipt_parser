package com;


import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.vision.v1.Vertex;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.nio.file.Files;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;



public class DetectTextGcs {
	
	
	static String[] MARKETS = {"target", "walmart"};
	static List<String> MARKETS_LIST = Arrays.asList(MARKETS);
	
	static String[] STOPWORDS = {};
	static List<String> STOPWORDS_LIST = Arrays.asList(STOPWORDS);
	
	static String[] SKIPWORDS = {};
	static List<String> SKIPWORDS_LIST = Arrays.asList(STOPWORDS);

  public static void  main(String[] args) throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    String filePath = "C:\\Users\\robin\\Downloads\\target.png" ;
    File fi = new File(filePath);
    byte[] fileContent = Files.readAllBytes(fi.toPath());
    ByteString ayo = ByteString.copyFrom(fileContent);
    //Boolean test = is_Number("ab");
    //System.out.println(test);
    Date date_num = parse_date("123");
    
    
    //System.out.println(detectTextGcs(ayo));
    detectTextGcs(ayo);
    
  }

  // Detects text in the specified remote image on Google Cloud Storage.
  public static void detectTextGcs(ByteString receipt) throws IOException {
    List<AnnotateImageRequest> requests = new ArrayList<>();
    

    //ImageSource imgSource = ImageSource.newBuilder().setImageUri(path).build();
    Image img = Image.newBuilder().setContent(receipt).build();
    Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
    requests.add(request);

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
      BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();
      
      List<EntityAnnotation> entityList= responses.get(0).getTextAnnotationsList();
      
      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          System.out.format("Error: %s%n", res.getError().getMessage());
          //return null;
        }

        // For full list of available annotations, see http://g.co/cloud/vision/docs
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
          System.out.format("Text: %s%n", annotation.getDescription());
          System.out.format("Position : %s%n", annotation.getBoundingPoly());
        }
        
        //System.out.format("Text: %s%n", res.getTextAnnotationsList());
        //return res.getTextAnnotationsList(); 
      }
      
      //EntityAnnotation here = entityList.get(0);
      //List<Vertex> v_list = here.getBoundingPoly().getVerticesList();
      //System.out.println(v_list);
      //parse(entityList);
      
      
    }
  }
  
   public static Boolean is_Number(String input) {
	  
	  try {
		Float try_this = Float.valueOf(input).floatValue();
		return true;
	  }
	  catch(Exception e) {
		  //System.out.println("Not a number");
		  return false;
	  }
	  
  }
   
   
   public static Boolean is_Decimal(String input) {
	   float try_this = Float.valueOf(input).floatValue();
	   float rounded = Math.round(try_this * 100.0f) / 100.0f;
	   
	   if(!(is_Number(input))) {
		   return false;
	   }
	   if(rounded != try_this) {
		   return true;
	   }
	   else {
		   return false;
	   }
   }
   
   public static Boolean is_Int(String str) 
   { 
       int val = 0;
       float try_this = Float.valueOf(str).floatValue();
	   float rounded = Math.round(try_this * 100.0f) / 100.0f;
       //System.out.println("String = " + str); 
 
       // Convert the String 
       try { 
           val = Integer.parseInt(str); 
       } 
       catch (NumberFormatException e) { 
 
           // This is thrown when the String 
           // contains characters other than digits 
           //System.out.println("Invalid String");
    	   return false;
       } 
       if(try_this == rounded) {
		   return true;
	   }
	   else {
		   return false;
	   }
   }
   
   private static Date parse_date(String date) {
	   
	   String[] formats = {"MM/dd/yyyy", "dd-M-yyyy hh:mm:ss", "yyyy/MM/dd"};
	   String[] substrings = date.split(" ");
	   
	   for(String format:formats) {
		   for(String substring : substrings ) {
			   SimpleDateFormat format_obj = new SimpleDateFormat(format);
			   format_obj.setLenient(false);
			   try {
				   Date purch_date = format_obj.parse(substring);
				   return purch_date;
			   }
			   catch(ParseException e) {
				   continue;
			   }
		   }
	   }
	   return null;
       
       // With lenient parsing, the parser may use heuristics to interpret
       // inputs that do not precisely match this object's format.
       
       
   }
   
   public static Boolean is_Name(String name) {
	   int counter = 0;
	   
	   for(int i = 0; i < name.length(); i++) {
		   if(Character.isLetter(name.charAt(i))) {
			   counter += 1;
		   }
	   }
	   if (counter <= 2) {
		   return false;
	   }
	   
	   return true;
   }
   
   public static String is_Price(String val) {
	   String p_val;
	   if (val.contains(" ")){
		   p_val = val.split(" ")[0];
	   }
	   else {
		   p_val = val;
	   }
	   if(p_val.contains("$")) {
		   p_val = p_val.replace("$", "");
	   }
	   if (is_Decimal(p_val)) {
		   return p_val;
	   }
	   else {
		   return null;
	   }
	   
   }

public static String check_market(String val) {
	   String[] s_list = val.toLowerCase().split(" ");
	   List<String> _slist = Arrays.asList(s_list);
	   for(String market : MARKETS_LIST) {
		   if(_slist.contains(market)) {
			   return market;
		   }
	   }
	   
	   return null;
	   
   }
   
   public static String annotation_type(String text) {
	   if(text.charAt(-1) == ',') {
		   return "hanging";
	   }
	   if(is_Price(text) != null) {
		   return "number";
	   }
	   if(parse_date(text)!=  null ) {
		   return "date";
	   }
	   if(is_Int(text)) {
		   return "int";
	   }
	   if(check_market(text) != null) {
		   return "market";
	   }
	   return "text";
   }
   
   public static void parse(List<EntityAnnotation> cloud_response){
	   
	   List<String> items, dates, stores = new ArrayList<>();
	   List<Integer> seen_index = new ArrayList<>();
	   String[] seen_prices;
	   int parsed_y = 0;
	   EntityAnnotation first_annote = cloud_response.get(0);
	   int g_xmin, g_xmax, g_ymin, g_ymax;
	   
	   List<Integer> v_x = new ArrayList<>();
	   List<Integer> v_y = new ArrayList<>();
	   
	   for(Vertex v : first_annote.getBoundingPoly().getVerticesList()) {
		   v_x.add(v.getX());
		   v_y.add(v.getY());
		   
	   }
	   
	   g_xmin = Collections.min(v_x);
	   g_xmax = Collections.max(v_x);
	   g_ymin = Collections.min(v_y);
	   g_ymax = Collections.max(v_y);
	   
	   Boolean break_c = false;
	   List<EntityAnnotation> sorted_annote = cloud_response.subList(1, cloud_response.size()-1);
	   String curr_name = "";
	   
	   ListIterator<EntityAnnotation> it = sorted_annote.listIterator(0);
	   
	   while(it.hasNext()) {
		   Boolean will_skip = false;
		   String[] it_list = it.next().getDescription().toLowerCase().split(" ");
		   List<String> _itlist = Arrays.asList(it_list);
		   for(String stopword : STOPWORDS_LIST) {
			   if(_itlist.contains(stopword)) {
				   System.out.printf("Skipping %s",it.next().getDescription());
				   break_c = true;
			   }
			   
		   }
		   
		   if(will_skip) {
			   continue;
		   }
		   if(seen_index.contains(it.nextIndex())) {
			   continue;
		   }
		   String t_type = annotation_type(it.next().getDescription());
		   System.out.printf("%s, type:%n%s", it.next().getDescription(), t_type);
		   
		   if(t_type == "text") {
			   if(break_c) {
				   continue;
			   }
			   
			   List<String> id_x = new ArrayList<>();
			   List<String> used_pr = new ArrayList<>();
			   
			   for(Vertex v : it.next().getBoundingPoly().getVerticesList()) {
				   v_x.add(v.getX());
				   v_y.add(v.getY());
				   
			   }
			   
			   int xmin = Collections.min(v_x);
			   int xmax = Collections.max(v_x);
			   int ymin = Collections.min(v_y);
			   int ymax = Collections.max(v_y);
			   
			   if(xmax > g_xmax/2) {
				   continue;
			   }
			   
			   if((ymax + ymin)/2 < parsed_y) {
				   continue;
			   }
			   int line_height = ymax-ymin;
			   String curr_price = "";
			   curr_name += it.next().getDescription();
			   int curr_y = 0;
			   int curr_x_price = 0;
			   Boolean is_hanging = false;
			   String p_desc = "";
			   
			   
			   
		   }
	   }
	   
	   
	   
	   
   }
  
  
}
