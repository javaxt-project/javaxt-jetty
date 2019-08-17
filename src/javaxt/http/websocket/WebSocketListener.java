package javaxt.http.websocket;
import java.io.IOException;
import javaxt.http.servlet.Cookie;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;

//******************************************************************************
//**  WebSocketListener
//******************************************************************************
/**
 *   Instances of this class are used to process WebSocket requests. Example:
 *
 <pre>
    public class MyServlet extends HttpServlet {


        public MyServlet() {}


        public void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException {

            if (request.isWebSocket()){
                new WebSocketListener(request, response){
                    public void onConnect(){
                        send("Hello There!");
                    }
                    public void onText(String str){
                        //System.out.println(str);
                        send("Message recieved at " + new java.util.Date());

                    }
                    public void onDisconnect(int statusCode, String reason, boolean remote){
                        //System.out.println("Goodbye...");
                    }
                };
            }
            else{ //standard http request
                response.write("Hello, the time is now " + new java.util.Date());
            }
        }
    }
 </pre>
 *
 ******************************************************************************/

public class WebSocketListener  {


    private Session session;
    private ServletUpgradeRequest request;
    private Cookie[] cookies;


  //**************************************************************************
  //** Constructor
  //**************************************************************************
    public WebSocketListener(HttpServletRequest request, HttpServletResponse response){


        Object obj = request.getServletContext().getAttribute("javaxt.http.websocket.WebSocketServer");
        WebSocketServer webSocketServer = (WebSocketServer) obj;



        WebSocketListener me = this;


      //Wrap the WebSocketListener into a WebSocketAdapter
        WebSocketAdapter wrapper = new WebSocketAdapter(){
            public void onWebSocketConnect(Session session){
                super.onWebSocketConnect(session);
                try{

                  //Set session variable (private field) in the WebSocket
                    me.session = session;


                  //Set request variable (private field) in the WebSocket
                    ServletUpgradeRequest upgradeRequest = (ServletUpgradeRequest) session.getUpgradeRequest();
                    me.request = upgradeRequest;


                  //Call onConnect
                    me.onConnect();
                }
                catch(Exception e){
                    //e.printStackTrace();
                }
            }
            public void onWebSocketClose(int statusCode, String reason){
                super.onWebSocketClose(statusCode, reason);
                try{
                    me.onDisconnect(statusCode, reason);
                }
                catch(Exception e){
                    //e.printStackTrace();
                }
            }
            public void onWebSocketText(String message){
                super.onWebSocketText(message);
                try{
                    me.onText(message);
                }
                catch(Exception e){
                    //e.printStackTrace();
                }
            }
        };

        webSocketServer.processRequest(wrapper, request, response);
    }



  //**************************************************************************
  //** onConnect
  //**************************************************************************
  /** Called whenever a new WebSocket connection is established.
   */
    public void onConnect(){}


  //**************************************************************************
  //** onDisconnect
  //**************************************************************************
  /** Called whenever a WebSocket connection is terminated.
   */
    public void onDisconnect(int statusCode, String reason){}


  //**************************************************************************
  //** onText
  //**************************************************************************
  /** Called whenever a client sends a text message to the WebSocket.
   */
    public void onText(String str){}


  //**************************************************************************
  //** send
  //**************************************************************************
  /** Used to send a text message to the client.
   */
    public final void send(String str) {
        try {
            session.getRemote().sendString(str);
        }
        catch (IOException e) {

        }
    }


  //**************************************************************************
  //** close
  //**************************************************************************
  /** Sends a websocket Close frame, with a normal status code and no reason
   *  phrase. This will enqueue a graceful close to the remote endpoint.
   */
    public void close(){
        session.close();
    }


  //**************************************************************************
  //** disconnect
  //**************************************************************************
  /** Issues a harsh disconnect of the underlying connection. This will
   *  terminate the connection, without sending a websocket close frame.
   */
    public void disconnect() throws IOException {
        session.disconnect();
    }


  //**************************************************************************
  //** getURI
  //**************************************************************************
  /** Returns the URI used to establish the WebSocket connection.
   */
    public java.net.URI getURI(){
        return request.getRequestURI();
    }

    public String getLocalAddress(){
        return request.getLocalAddress();
    }

    public String getLocalHostName(){
        return request.getLocalHostName();
    }

    public int getLocalPort(){
        return request.getLocalPort();
    }

    public String getRemoteAddress(){
        return request.getRemoteAddress();
    }

    public String getRemoteHostName(){
        return request.getRemoteHostName();
    }

    public int getRemotePort(){
        return request.getRemotePort();
    }

    public java.security.cert.X509Certificate[] getCertificates(){
        return request.getCertificates();
    }

    public Cookie[] getCookies(){
        if (cookies==null){
            javax.servlet.http.Cookie[] requestCookies = request.getHttpServletRequest().getCookies();
            if (requestCookies!=null){
                cookies = new Cookie[requestCookies.length];
                for (int i=0; i<cookies.length; i++){
                    cookies[i] = new Cookie(requestCookies[i].getName(), requestCookies[i].getValue());
                }
            }
        }

        return cookies;
    }

//    public HttpSession getSession(){
//        return request.getSession(false);
//    }

    public java.util.Enumeration<String> getExtensions(){
        return request.getHttpServletRequest().getHeaders("Sec-WebSocket-Extensions");
    }

    public java.security.Principal getUserPrincipal(){
        return request.getUserPrincipal();
    }

    public boolean isSecure(){
        return request.isSecure();
    }

    public boolean isUserInRole(String role){
        return request.isUserInRole(role);
    }

    public String getHeader(String name){
        return request.getHeader(name);
    }

    public java.util.Map<String, java.util.List<String>> getHeaders(){
        return request.getHeaders();
    }

    public java.util.List<String> getHeaders(String name){
        return request.getHeaders(name);
    }

    public java.util.Locale getLocale(){
        return request.getLocale();
    }

    public java.util.Enumeration<java.util.Locale> getLocales(){
        return request.getLocales();
    }

    public java.util.Map<String, String[]> getParameterMap(){
        return request.getHttpServletRequest().getParameterMap();
    }

    public String getProtocolVersion(){
        return request.getProtocolVersion();
    }

    public boolean hasSubProtocol(String test){
        return request.hasSubProtocol(test);
    }

    public java.util.List<String> getSubProtocols(){
        return request.getSubProtocols();
    }
}