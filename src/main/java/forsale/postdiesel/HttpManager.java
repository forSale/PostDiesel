package forsale.postdiesel;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class HttpManager {
    public HttpURLConnection c;
    
    public HttpManager(CmdOpts cmdOpts, String page, String method) {
        try {
            URL url = new URL(page);            
            if (!cmdOpts.getIpAddress().trim().isEmpty()){
                c = (HttpURLConnection) url.openConnection(
                                        new Proxy( Proxy.Type.HTTP, new InetSocketAddress( cmdOpts.getIpAddress(), Integer.parseInt(cmdOpts.getPort()) ) ) );             
            } else {
                c = (HttpURLConnection) url.openConnection(); 
            }
            c.setRequestMethod(method);
            c.setDoOutput(true); 
            //c.disconnect();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public HttpManager(CmdOpts cmdOpts, String page, String params, String method, Boolean follow, String sessId, String passHash) {
        this(cmdOpts, page, method);
        try {
            
            if (!sessId.trim().isEmpty())
                c.setRequestProperty("Cookie","session_id=" + sessId +";pass_hash=" + (!passHash.trim().isEmpty() ? passHash : '0'));
            
            c.setInstanceFollowRedirects(follow);
            //c.setDoOutput(true); 
            
            if (!params.trim().isEmpty()) {
                OutputStreamWriter writer = new OutputStreamWriter(c.getOutputStream()); 
                writer.write(params);
                writer.close();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}