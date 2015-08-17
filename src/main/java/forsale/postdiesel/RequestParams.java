package forsale.postdiesel;

public class RequestParams {

    private static final String HOST = "http://diesel.elcat.kg";
    
    public static final String MAINPAGE = "/index.php?";
    
    public static final String DELETEPOST = HOST + MAINPAGE + "act=Mod&CODE=04&f=225&st=0";
    
    public static final String PARAM_POSTMSG = "act=Post&CODE=03&f=225&st=0&fast_reply_used=1&enableemo=yes&enablesig=yes&submit=%CE%F2%EF%F0%E0%E2%E8%F2%FC";
    
    public static final String TOPIC = HOST + MAINPAGE;
    
    public static final String LOGIN = HOST + MAINPAGE + "act=Login&CODE=01";
    
    public static final String PARAM_LOGIN = "referer=http%3A%2F%2Fdiesel.elcat.kg%2Findex.php%3F&CookieDate=1";
    
}