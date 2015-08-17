package forsale.postdiesel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
 
public class Login {  
    private String sessId;
    private String authKey;
    private String passHash;
    
    public String getSessId() { return this.sessId; }
    public String getAuthKey() { return this.authKey; }
    public String getPassHash() { return this.passHash; }
    
    /*public static void main(String[] args) { 
        new Login(args); 
    }*/ 
    
    public Login(CmdOpts cmdOpts) {
        exec(cmdOpts); 
    }
 
    public void exec(CmdOpts cmdOpts) {         
        
        String topicId = getParamValue(cmdOpts.getTopic(), "showtopic", "\\?", "&");
        
        System.out.println("Open Website...");  
        HttpManager connect = new HttpManager(cmdOpts, RequestParams.TOPIC + "showtopic=" + topicId, "GET");
         
        sessId = getAttr(connect.c, "Set-Cookie", "session_id", ";");
        System.out.println("session_id : " + sessId);         
        
        System.out.println("Login...");   
        connect = new HttpManager(cmdOpts, RequestParams.LOGIN, RequestParams.PARAM_LOGIN + "&UserName=" + cmdOpts.getUser() + "&PassWord=" + cmdOpts.getPass(), "POST", false, sessId, "");
        
        passHash = getAttr(connect.c, "Set-Cookie", "pass_hash", ";");
        System.out.println("pass_hash : " + passHash);

        if (!passHash.trim().isEmpty()) { 
            // Все хорошо 
            System.out.println("Login OK");            
        } else { 
            // Сервер ответил кодом с ошибкой. 
            System.out.println("Login Error");
        }  
        
        System.out.println("Open topic...");   
        connect = new HttpManager(cmdOpts, RequestParams.TOPIC + "showtopic=" + topicId, "", "GET", false, sessId, passHash);
        
        sessId = getAttr(connect.c, "Set-Cookie", "session_id", ";");
        System.out.println("session_id : " + sessId);  
        
        authKey = getAuthKey(connect.c);
        System.out.println("auth_key : " + authKey);
            
    } 
    
    public String getAttr(HttpURLConnection c, String headerField, String sub, String delimiter) {
        String attr = "";
           
        if (c.getHeaderFields().get(headerField) != null) {
            
            for (String str : c.getHeaderFields().get(headerField)) {
                
                attr = getParamValue(str, sub, delimiter, "");
                if (!attr.trim().isEmpty()) {
                    return attr;
                }
            
            }
            
        } 
        return attr;
    }
    
    public String getParamValue(String str, String param, String delimiter, String end) {
        String attr = "";
            
        for (String value : str.split(delimiter)) {  
        
            if (!value.contains("="))
                continue;
                
            String item = value.substring(0, value.indexOf("=")).trim();
            
            int valueEnd = !end.trim().isEmpty() && value.contains(end) ? value.indexOf(end) : value.length();
            
            if ( item.equals(param) ) {  
                attr = value.substring(value.indexOf("=")+1, valueEnd).trim();
                return attr;
            }  
            
        }
        return attr;
    }
    
    private String getAuthKey(HttpURLConnection c) {
        String authKey;
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
            
            String line;
            while ( ( line = reader.readLine()) != null ) {
                sb.append(line);
            }
        } catch (IOException e) { 
            e.printStackTrace();
        }
    
        Document html = Jsoup.parse(sb.toString());
        
        Elements el = html.body().select("form[name=REPLIER] input[name=auth_key]");
        authKey = el.attr("value");
        
        return authKey;
    }
    
} 