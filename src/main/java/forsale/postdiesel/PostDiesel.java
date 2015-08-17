package forsale.postdiesel;

import org.jsoup.helper.Validate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PostDiesel {
    
    private String gopId;
    private String sessId;
    private String authKey;
    private String passHash;
    private Login auth;
    private String topicId;
 
    public static void main(String[] args) { 
        Validate.isTrue(args.length >= 1, "usage: supply url to fetch");
        
        CmdOpts cmdOpts = new CmdOpts(args);
        new PostDiesel().exec(cmdOpts); 
    } 
 
    public void exec(CmdOpts cmdOpts) { 
        String message = null; 
        try { 
            message = URLEncoder.encode("up!", "UTF-8"); 
        } catch (UnsupportedEncodingException ex) { 
            System.out.println("UnsupportedEncodingException");
        } 
 
        try { 
            auth = new Login(cmdOpts);
            sessId = auth.getSessId();
            authKey = auth.getAuthKey();
            passHash = auth.getPassHash();  
            topicId = auth.getParamValue(cmdOpts.getTopic(), "showtopic", "\\?", "&");          
            
            HttpManager connect;
            String postId = readFile("post_id.txt").get(0);
            
            System.out.println("Delete previous post...");            
            System.out.println("Delete post : " + postId); 
            connect = new HttpManager(cmdOpts, RequestParams.DELETEPOST + "&auth_key=" + authKey + "&p=" + postId +"&t=" +      topicId,"", "GET", true, sessId, passHash); 
            System.out.println("Post deleted.");
            
            writeToFile("postlog.html", inputStream(connect).toString()); 
            
            System.out.println("Post Message...");   
            connect = new HttpManager(cmdOpts, RequestParams.TOPIC, RequestParams.PARAM_POSTMSG + "&auth_key=" + authKey + "&Post=" + message + "&t=" + topicId, "POST", false, sessId, passHash);
            
            if (connect.c.getHeaderFields().get("Location") == null) {
                writeToFile("inputheaders.html", connect.c.getHeaderFields().toString() + "\n\n" + inputStream(connect).toString());
            }
            
            gopId = auth.getAttr(connect.c, "Location", "gopid", "&");
            System.out.println("gopid : " + gopId); 

            if (!gopId.trim().isEmpty())
                writeToFile("post_id.txt", gopId);
            
            if ( connect.c.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                // Все хорошо 
                System.out.println("Post OK");
                System.out.println(connect.c.getResponseCode());
            } else { 
                // Сервер ответил кодом с ошибкой. 
                System.out.println("Post Error");
                System.out.println(connect.c.getResponseCode());
            } 
            
            /*System.out.println("Delete previous post...");   
            for (String postId : readFile("post_id.txt")) {
                System.out.println("Delete post : " + postId); 
                 
                connect = new HttpManager(cmdOpts, RequestParams.DELETEPOST + "&auth_key=" + authKey + "&p=" + postId +"&t=" + topicId,"", "GET", true, sessId, passHash); 
            }
            System.out.println("Post deleted.");*/
             
            //writeToFile("postlog.html", inputStream(connect).toString()); 
            
            //if (!gopId.trim().isEmpty())
            //    writeToFile("post_id.txt", gopId); 
            
        } catch (MalformedURLException e) { 
            System.out.println("MalformedURLException");
        } catch (IOException e) { 
            e.printStackTrace();
            System.out.println("IOException");
        } 
    } 
    
    private static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try {        
            File file = new File(filePath);
            if (!file.exists()) {
                return lines;
            }            
            
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);              
            }   
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;    
    }
    
    private void writeToFile(String filePath, String content) {
        try {        
            FileWriter fwriter = new FileWriter(filePath, false);
            fwriter.write(content);
            
            fwriter.flush();
            fwriter.close();   
        } catch (IOException ex) {
            ex.printStackTrace();
        }   
    }
    
    private StringBuilder inputStream(HttpManager connect) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(connect.c.getInputStream()));
            String line;
            while ( ( line = reader.readLine()) != null ) {
                sb.append(line);
            }
            reader.close();
        } catch (IOException e) { 
            e.printStackTrace();
        } 
        return sb;
    }
 
} 