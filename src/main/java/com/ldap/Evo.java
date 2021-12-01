package com.ldap;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class Evo {

    private static String name;
    private static String cn;
    private static String sn;
    private static String gname;
    private static String email, mail;
    private static String manager;
    private static String country;
    private static String type;

private static String pwd;

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, NamingException {

        File uploadDir = new File("upload");
        uploadDir.mkdir();
        File resDir = new File("results");
        resDir.mkdir();

        staticFiles.externalLocation("upload");  
        staticFiles.externalLocation("results");
        
        get("/", (request, response) -> {
            response.type("text/html");
            String user = System.getenv("USERDOMAIN") + "\\" + System.getProperty("user.name");
            Map<String, Object> attributes = new HashMap<>();  
            attributes.put("user",user);
            return new FreeMarkerEngine().render(new ModelAndView(attributes, "index.ftl"));
        });
        
        post("/login", (request, response) -> {
            response.type("text/html"); 
            pwd = request.queryParams("pwd");
            String user = System.getenv("USERDOMAIN") + "\\" + System.getProperty("user.name");
            Map<String, Object> attributes = new HashMap<>();  
            attributes.put("pwd",pwd);
            attributes.put("user",user);
            return new FreeMarkerEngine().render(new ModelAndView(attributes, "confirm.ftl"));
        });

        get("/upload", (request, response) -> {
            response.type("text/html");
            return new FreeMarkerEngine().render(new ModelAndView(null, "upload.ftl"));
        });
        
        post("/upload", (request, response) -> {

            String homedir = System.getProperty("user.dir");
            //OutputStream outputStream;            
           
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(homedir));            
            Part filePart = request.raw().getPart("upfile");
            
            Path tempFile = Files.createTempFile(uploadDir.toPath(),"","");
            //System.out.println(tempFile);
            
            try (InputStream inputfile = filePart.getInputStream()) { 
                Files.copy(inputfile, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            /*
            try (InputStream inputStream = filePart.getInputStream()) {
                outputStream = new FileOutputStream(filePart.getSubmittedFileName());
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
            }
            */
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File output = new File("LDAPSearch_"+timeStamp+".csv"); 
            
            String uploadedFile = homedir+"\\upload\\"+filePart.getSubmittedFileName();
            String input = tempFile.toString();
            String result = homedir+"\\results\\"+output;
            //System.out.println(input);
            //System.out.println(output);
            
           
            CSVWriter writer = new CSVWriter(new FileWriter(result)); 

            String[] header = {"Email", "Display Name", "ID", "Family Name", "Given Name", "Manager ID", "Country", "Type"};
            writer.writeNext(header); 

            CSVReader reader = null;
            try {
                reader = new CSVReader(new FileReader(input));
                String[] line;
                while ((line = reader.readNext()) != null) {

                    String email= line[0];
                    DirContext context = Connection.getContext(pwd);

                    String filter = "(&(objectClass=person)(mail="+email+"))";            
                    //String base = "DC=prg-dc,DC=dhl,DC=com";
                    String base = "OU=Users,O=dhl.com";
                    
                    SearchControls sc = new SearchControls();
                    String[] attributeFilter = { "displayname","mail","name","sn","c","givenName","manager","dhlemployeetype"};
                    //String[] attributeFilter = { "displayname","mail","name","sn","c","givenName","manager" };
                    sc.setReturningAttributes(attributeFilter);
                    sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

                    NamingEnumeration results = context.search(base, filter, sc);
                        if((results.hasMore() == false)){
                            name = "N/A";
                            cn = "N/A";
                            sn = "N/A";
                            gname = "N/A";
                            country = "N/A";
                            manager = "N/A";
                            type = "N/A";
                            mail = email;
                            
                                String[] data = { mail, name, cn, sn, gname, manager, country,type}; 
                                writer.writeNext(data); 
                        }else{ 
                        while (results.hasMore()) {
                            SearchResult sr = (SearchResult) results.next();

                            name = Arrays.asList(sr.getAttributes().get("displayname").toString().split(": ")).get(1);
                            cn = Arrays.asList(sr.getAttributes().get("name").toString().split(": ")).get(1);
                            sn = Arrays.asList(sr.getAttributes().get("sn").toString().split(": ")).get(1);
                            gname = Arrays.asList(sr.getAttributes().get("givenName").toString().split(": ")).get(1);
                            type = Arrays.asList(sr.getAttributes().get("dhlemployeetype").toString().split(": ")).get(1);
                            if(sr.getAttributes().get("c")==null){
                                country = "N/A";
                            }else{
                                country = Arrays.asList(sr.getAttributes().get("c").toString().split(": ")).get(1); 
                            }
                            if(sr.getAttributes().get("manager") == null){
                                manager = "N/A";
                            }else{
                                manager = Arrays.asList(Arrays.asList(sr.getAttributes().get("manager").toString().split("=")).get(1).split(",")).get(0); 
                            }
                            mail = Arrays.asList(sr.getAttributes().get("mail").toString().split(": ")).get(1);

                                String[] data = { mail, name, cn, sn, gname, manager, country, type}; 
                                writer.writeNext(data); 
                        }
                        }
                }
            } catch (IOException e) {
            }

            writer.close(); 
 
            Map<String, Object> attributes = new HashMap<>();  
            attributes.put("input",uploadedFile);
            attributes.put("result",result);
            return new FreeMarkerEngine().render(new ModelAndView(attributes, "uploaded.ftl"));
        });

         
        get("/searchid", (request, response) -> {
            response.type("text/html");
            return new FreeMarkerEngine().render(new ModelAndView(null, "searchid.ftl"));
        });

  
        post("/resultid", (request, response) -> {

            DirContext context = Connection.getContext(pwd);

            response.type("text/html");
            String uid = request.queryParams("uid");

            String filter = "(&(objectClass=person)(name="+uid+"))";
            //String base = "DC=prg-dc,DC=dhl,DC=com";
            String base = "OU=Users,O=dhl.com";

            SearchControls sc = new SearchControls();
            String[] attributeFilter = { "displayname","mail","name","sn","c","givenName","manager","dhlemployeetype" };
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration results = context.search(base, filter, sc);
            System.out.println("Results size: "+results.toString());

            if((results.hasMore() == false)){
                name = "N/A";
                cn = "N/A";
                sn = "N/A";
                gname = "N/A";
                country = "N/A";
                manager = "N/A";
                type = "N/A";
            }else {
                while (results.hasMore()) {
                    SearchResult sr = (SearchResult) results.next();

                    name = Arrays.asList(sr.getAttributes().get("displayname").toString().split(": ")).get(1);
                    cn = Arrays.asList(sr.getAttributes().get("name").toString().split(": ")).get(1);
                    sn = Arrays.asList(sr.getAttributes().get("sn").toString().split(": ")).get(1);
                    gname = Arrays.asList(sr.getAttributes().get("givenName").toString().split(": ")).get(1);
                    type = Arrays.asList(sr.getAttributes().get("dhlemployeetype").toString().split(": ")).get(1);
                    if (sr.getAttributes().get("c") == null) {
                        country = "N/A";
                    } else {
                        country = Arrays.asList(sr.getAttributes().get("c").toString().split(": ")).get(1);
                    }
                    if (sr.getAttributes().get("manager") == null) {
                        manager = "N/A";
                    } else {
                        manager = Arrays.asList(Arrays.asList(sr.getAttributes().get("manager").toString().split("=")).get(1).split(",")).get(0);
                    }
                    email = Arrays.asList(sr.getAttributes().get("mail").toString().split(": ")).get(1);

                }
            }

            Map<String, Object> attributes = new HashMap<>();

            attributes.put("name",name);
            attributes.put("cn", cn);
            attributes.put("sn", sn );
            attributes.put("gname", gname );
            attributes.put("email", email );
            attributes.put("manager", manager );
            attributes.put("country", country );
            attributes.put("type", type );

            return new FreeMarkerEngine().render(new ModelAndView(attributes, "resultid.ftl"));
        });

        get("/searchmail", (request, response) -> {
            response.type("text/html");
            return new FreeMarkerEngine().render(new ModelAndView(null, "searchmail.ftl"));
        });

  
        post("/resultmail", (request, response) -> {

            DirContext context = Connection.getContext(pwd);

            response.type("text/html");   
            String mail = request.queryParams("mail");

            String filter = "(&(objectClass=person)(mail="+mail+"))";            
            //String base = "DC=prg-dc,DC=dhl,DC=com";
            String base = "OU=Users,O=dhl.com";

            SearchControls sc = new SearchControls();
            String[] attributeFilter = { "displayname","mail","name","sn","c","givenName","manager","dhlemployeetype" };
            sc.setReturningAttributes(attributeFilter);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration results = context.search(base, filter, sc);

        if((results.hasMore() == false)){
            name = "N/A";
            cn = "N/A";
            sn = "N/A";
            gname = "N/A";
            country = "N/A";
            manager = "N/A";
            type = "N/A";
        }else{            
        while (results.hasMore()) {
            SearchResult sr = (SearchResult) results.next();

            name = Arrays.asList(sr.getAttributes().get("displayname").toString().split(": ")).get(1);
            cn = Arrays.asList(sr.getAttributes().get("name").toString().split(": ")).get(1);
            sn = Arrays.asList(sr.getAttributes().get("sn").toString().split(": ")).get(1);
            gname = Arrays.asList(sr.getAttributes().get("givenName").toString().split(": ")).get(1);
            type = Arrays.asList(sr.getAttributes().get("dhlemployeetype").toString().split(": ")).get(1);
            if(sr.getAttributes().get("c")==null){
                country = "N/A";
            }else{
                country = Arrays.asList(sr.getAttributes().get("c").toString().split(": ")).get(1); 
            }
            if(sr.getAttributes().get("manager") == null){
                manager = "N/A";
            }else{
                manager = Arrays.asList(Arrays.asList(sr.getAttributes().get("manager").toString().split("=")).get(1).split(",")).get(0); 
            }
            mail = Arrays.asList(sr.getAttributes().get("mail").toString().split(": ")).get(1);
      
        }
        }
        
            Map<String, Object> attributes = new HashMap<>();                     
            
            attributes.put("name",name);
            attributes.put("cn", cn);
            attributes.put("sn", sn );
            attributes.put("gname", gname );
            attributes.put("mail", mail );
            attributes.put("manager", manager );
            attributes.put("country", country );
            attributes.put("type", type );

            return new FreeMarkerEngine().render(new ModelAndView(attributes, "resultmail.ftl"));
        });   
        
    }

}