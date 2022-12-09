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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;



public class DetectTextGcs {
	
	//sus like the imposter
	static String[] MARKETS = {"WALMART", "TARGET"};
	static List<String> MARKETS_LIST = Arrays.asList(MARKETS);


  public static void  main(String[] args) throws IOException {
    // TODO(developer): Replace these variables before running the sample.
    String filePath = "C:\\Users\\robin\\Downloads\\target.png" ;
    File fi = new File(filePath);
    byte[] fileContent = Files.readAllBytes(fi.toPath());
    ByteString ayo = ByteString.copyFrom(fileContent);
    //Boolean test = is_Number("ab");
    //System.out.println(test);
    //Date date_num = parse_date("123");
    
    
    //System.out.println(detectTextGcs(ayo));
    parse(detectTextGcs(ayo));
    //find_date(detectTextGcs(ayo));
    //find_market(detectTextGcs(ayo));
    //System.out.println(price_list(detectTextGcs(ayo)));
    
  }

  // Detects text in the specified remote image on Google Cloud Storage.
  public static List<EntityAnnotation> detectTextGcs(ByteString receipt) throws IOException {
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
        
        

        /*// For full list of available annotations, see http://g.co/cloud/vision/docs
        for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
          System.out.format("Text: %s%n", annotation.getDescription());
          System.out.format("Position : %s%n", annotation.getBoundingPoly());
        }*/
        
        //System.out.format("Text: %s%n", res.getTextAnnotationsList());
        //return res.getTextAnnotationsList(); 
      }
      return entityList;
      
      //EntityAnnotation here = entityList.get(0);
      //List<Vertex> v_list = here.getBoundingPoly().getVerticesList();
      //System.out.println(v_list);
      //parse(entityList);
      
      
    }
  }
  
   
   private static String parse_date(String date) {
	   
	   String[] formats = {"MM/dd/yy","MM/dd/yyyy", "dd-M-yyyy", "yyyy/MM/dd"};
	   String[] substrings = date.split(" ");
	   
	   for(String format:formats) {
		   for(String substring : substrings ) {
			   SimpleDateFormat format_obj = new SimpleDateFormat(format);
			   format_obj.setLenient(false);
			   try {
				   Date purch_date = format_obj.parse(substring);
				   return format_obj.format(purch_date);
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
   
   
   public static String find_market(List<EntityAnnotation> cloud_response) {
	   
	   String[] res_list = cloud_response.get(0).getDescription().toUpperCase().split("\n");
	   List<String> RES_LIST = Arrays.asList(res_list);
	
	   
	   for(String market : MARKETS_LIST) {
		   if(RES_LIST.contains(market)) {
			   System.out.println(market);
			   return market;
		   }
	   }
	   
	   return "NO MARKET FOUND";
	   	
   }
   
   public static String find_date(List<EntityAnnotation> cloud_response) {
   
   String[] res_list = cloud_response.get(0).getDescription().toLowerCase().split("\n");
   List<String> RES_LIST = Arrays.asList(res_list);

   
   for(String annotation : RES_LIST) {
	   if(parse_date(annotation)!=null) {
		   String res = parse_date(annotation);
		   System.out.println(res);
		   return res;
	   }
	   
   }
   
   return "NO DATE FOUND";
   	
}
   
   public static List<Integer> get_bounds_y(EntityAnnotation annotation) {
	   
	   List<Vertex> v_list = annotation.getBoundingPoly().getVerticesList();
	   List<Integer> y_list = new ArrayList<>();
	   
	   for(Vertex v: v_list) {
		   y_list.add(v.getY());
	   }
	   
	   Integer max_y = Collections.max(y_list);
	   Integer min_y = Collections.min(y_list);
	   
	   List<Integer> res = new ArrayList<>();
	   res.add(min_y);
	   res.add(max_y);
	   
	   return res;
	 
	   	
	}
   
   public static Integer get_mid_point_x(EntityAnnotation annotation) {
	   
	   List<Vertex> v_list = annotation.getBoundingPoly().getVerticesList();
	   List<Integer> x_list = new ArrayList<>();
	   
	   for(Vertex v: v_list) {
		   x_list.add(v.getX());
	   }
	   
	   Integer max_x = Collections.max(x_list);
	   Integer min_x = Collections.min(x_list);
	   
	   Integer res = (max_x + min_x) / 2;
	   
	   return res;
	 
	   	
	}
   
   public static Integer get_max_x(EntityAnnotation annotation) {
	   
	   List<Vertex> v_list = annotation.getBoundingPoly().getVerticesList();
	   List<Integer> x_list = new ArrayList<>();
	   
	   for(Vertex v: v_list) {
		   x_list.add(v.getX());
	   }
	   
	   Integer max_x = Collections.max(x_list);
	   //Integer min_y = Collections.min(x_list);
	   
	   //Integer res = (max_y + min_y) / 2;
	   
	   return max_x;
	 
	   	
	}
   
   public static Integer get_min_x(EntityAnnotation annotation) {
	   
	   List<Vertex> v_list = annotation.getBoundingPoly().getVerticesList();
	   List<Integer> x_list = new ArrayList<>();
	   
	   for(Vertex v: v_list) {
		   x_list.add(v.getX());
	   }
	   
	   //Integer max_x = Collections.max(x_list);
	   Integer min_x = Collections.min(x_list);
	   
	   //Integer res = (max_y + min_y) / 2;
	   
	   return min_x;
	 
	   	
	}
   
   public static List<EntityAnnotation> price_list(List<EntityAnnotation> cloud_response){
	   
	   List<EntityAnnotation> res_list = new ArrayList<>();
	   List<Integer> midpoints = new ArrayList<>();
	   int m = 0;
	   
	   for(int i = 1; i < cloud_response.size(); i++) {
		   if(Pattern.matches("^[0-9]{0,5}\\.[0-9]{2}$",cloud_response.get(i).getDescription())) {
			   res_list.add(cloud_response.get(i));
		   }
	   }
	   
	   for(EntityAnnotation annote : res_list) {
		   midpoints.add(get_mid_point_x(annote));
	   }
	   
	   //System.out.println(midpoints);
	   
	   for(Integer midpoint : midpoints) {
		   m = m + midpoint;
	   }
	   
	   float avg = (m / midpoints.size());
	   
	   for(int i = 0; i < res_list.size(); i++) {
		  if(get_mid_point_x(res_list.get(i)) < avg) {
			  res_list.remove(i);
		  }
	   }
	   return res_list;
   }
   
   public static List<List<Object>> parse(List<EntityAnnotation> cloud_response) {
	   
	   List<EntityAnnotation> prices = price_list(cloud_response);
	   List<String> object = new ArrayList<>();
	   List<List<Object>> values = new ArrayList();
	   String date = find_date(cloud_response);
	   String market = find_market(cloud_response);
	   //System.out.println(cloud_response);
	   String res = "";
	   int tolerance = 15;
	   
	   for(int i = 0; i < prices.size(); i++) {
		   
		   //price_list.add(prices.get(i).getDescription());
		   System.out.println(prices.get(i).getDescription());
		   //System.out.print(cloud_response);
		   //System.out.print(get_bounds_y(prices.get(i)));
		   
		   for(int j = 1; j < cloud_response.size();j++) {
			   
			   //System.out.format("%s : %d\n",cloud_response.get(j).getDescription(),(cloud_response.get(j)));

			   if(get_bounds_y(cloud_response.get(j)).get(0) >= get_bounds_y(prices.get(i)).get(0)-tolerance && get_bounds_y(cloud_response.get(j)).get(1) <= get_bounds_y(prices.get(i)).get(1)+tolerance) {
				      
				    if(get_max_x(cloud_response.get(j)) <= get_max_x(prices.get(i)) && cloud_response.get(j).getDescription() != prices.get(i).getDescription()) {
					   res = res + cloud_response.get(j).getDescription() + " ";
				   }
			   }
			   
		   }
		   object.add(date);
		   //System.out.println(date);
		   object.add(market);
		   //System.out.println(market);
		   object.add(res);
		   object.add("$"+prices.get(i).getDescription());
		   res = "";
		   values.add(new ArrayList<Object>(object));
		   object.clear();
	   }
	   //System.out.println(values);
	   return values;
   }
   
   /*public static void parse(List<EntityAnnotation> cloud_response){
	   String[] temp = item_list(cloud_response).split("\n");
	   List<String> item_list = Arrays.asList(temp);
	   
	   List<String> items = new ArrayList<>();
	   List<String> prices = new ArrayList<>();
	   
	   
	   Pattern pattern = Pattern.compile("[0-9]{5,}");
	   Pattern dollar = Pattern.compile("^[0-9]{0,5}\\.[0-9]{2}$");
	   
	   
	   for(int i = 0; i < item_list.size(); i++) {
		   Matcher matcher = pattern.matcher(item_list.get(i));
		   boolean matchFound = matcher.find();
			   
		   if(matchFound){
			   items.add(item_list.get(i));
		   }
	   }
	   
	   for(int i = 0; i < items.size(); i++) {
		   String[] tempo = items.get(i).split(" ");
		   //System.out.print(Arrays.toString(tempo));
		   
	   }
	   
	   //System.out.print(item_list);
	   
	   
   }*/
}
	
