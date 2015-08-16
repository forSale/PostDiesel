import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.ParseException;

public class CmdOpts {
    
    private String topic;
    
    private String ipAddress = "";
    
    private String port = "";
    
    private String user = "";
    
    private String pass = "";
    
    
    public String getTopic() { return this.topic; }
    
    public String getIpAddress() { return this.ipAddress; }
    
    public String getPort() { return this.port; }
    
    public String getUser() { return this.user; }
    
    public String getPass() { return this.pass; }
    
    
    public CmdOpts(String[] args) {
    
        Options options = new Options();

        options.addOption("t", true, "topic address");
        options.addOption("ip", true, "proxy ipaddress");
        options.addOption("u", true, "username");
        options.addOption("p", true, "password");
        options.addOption("port", true, "port");
        
        try {
            CommandLineParser parser = new BasicParser();
            CommandLine cmd = parser.parse( options, args );
            topic = cmd.getOptionValue("t");
            user = cmd.getOptionValue("u");
            pass = cmd.getOptionValue("p");
            port = cmd.getOptionValue("port");
            
            if(cmd.hasOption("ip")) {
                ipAddress = cmd.getOptionValue("ip");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
    
    }
    
}