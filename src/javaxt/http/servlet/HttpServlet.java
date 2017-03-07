package javaxt.http.servlet;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.io.ssl.SslConnection;
import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.FileSessionDataStore;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;

//******************************************************************************
//**  HttpServlet Class
//******************************************************************************
/**
 *   The HttpServer requires an implementation of an HttpServlet in order to
 *   process HTTP requests.
 *
 ******************************************************************************/

public abstract class HttpServlet {

    private Authenticator authenticator;
    private javax.net.ssl.KeyManager[] kms;
    private javax.net.ssl.TrustManager[] tms;
    private SSLContext sslContext;
    private String sslProvider;
    private ServletContext servletContext;
    private SessionDataStore sessionStore;
    private RequestHandler handler;
    


  //**************************************************************************
  //** init
  //**************************************************************************
  /** Called by the servlet container to indicate to a servlet that it is
   *  being placed into service.
   */
    public void init(javax.servlet.ServletConfig servletConfig) {
    }
    
  //**************************************************************************
  //** processRequest
  //**************************************************************************
  /** This method is called each time the server receives an http request (GET,
   *  POST, HEAD, etc.). Use this method to formulate a response to the client.
   */
    public abstract void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException;


  //**************************************************************************
  //** getServletContext
  //**************************************************************************
  /** Returns the ServletContext.
   */
    public ServletContext getServletContext(){
        return servletContext;
    }

    public void destroy(){};


    public void log(String str){
        //TODO: Implement logger
    }
    
//  //**************************************************************************
//  //** setPaths
//  //**************************************************************************
//  /** Used to set the context and servlet paths used in the 
//   *  HttpServletRequest.getContextPath() and the 
//   *  HttpServletRequest.getServletPath() methods. 
//   */    
//    public void setPaths(String contextPath, String servletPath){
//      //TODO: Update logic used to assign context path
//        //this.getServletContext().setContextPath(contextPath);
//        //this.servletPath = servletPath;
//    }

    
    
  //**************************************************************************
  //** setSessionStore
  //**************************************************************************
    public void setSessionStore(java.io.File file){
        FileSessionDataStore sessionStore = new FileSessionDataStore();
        sessionStore.setStoreDir(file);
        this.sessionStore = sessionStore;
    }
    

  //**************************************************************************
  //** setAuthenticator
  //**************************************************************************
  /** Used to define an Authenticator used to authenticate requests.
   */
    public void setAuthenticator(Authenticator authenticator){
        this.authenticator = authenticator;
    }


  //**************************************************************************
  //** getAuthenticator
  //**************************************************************************
  /** Returns a new instance of an Authenticator used to authenticate users.
   */
    protected Authenticator getAuthenticator(HttpServletRequest request){
        if (authenticator!=null) return authenticator.newInstance(request);
        else return null;
    }


  //**************************************************************************
  //** setKeyStore
  //**************************************************************************
  /** Used to specify a KeyStore. The KeyStore is used to store keys and 
   *  certificates for SSL.
   */
    public void setKeyStore(KeyStore keystore, String passphrase) throws Exception {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keystore, passphrase.toCharArray());
        kms = kmf.getKeyManagers();
    }


  //**************************************************************************
  //** setKeyStore
  //**************************************************************************
  /** Used to specify a KeyStore. The KeyStore is used to store keys and 
   *  certificates for SSL.
   */
    public void setKeyStore(java.io.File keyStoreFile, String passphrase) throws Exception {
        char[] pw = passphrase.toCharArray();
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new java.io.FileInputStream(keyStoreFile), pw);
        setKeyStore(keystore, passphrase);
    }


  //**************************************************************************
  //** setKeyManager
  //**************************************************************************
  /** Used to specify a KeyManager. The KeyManager is responsible for managing 
   *  keys and certificates found in a KeyStore and is used to initialize the
   *  SSLContext. Typically, users are not required to specify a KeyManager. 
   *  Instead, a KeyManager is selected for you whenever the setKeyStore() 
   *  method is called. However, in some cases, the default KeyManager is not
   *  adequate (e.g. managing KeyStores with multiple SSL certificates) and
   *  users need to specify a different KeyManager. 
   */
    public void setKeyManager(javax.net.ssl.KeyManager keyManager) throws Exception {
        kms = new javax.net.ssl.KeyManager[]{keyManager};
    }


  //**************************************************************************
  //** setTrustStore
  //**************************************************************************
  /** Used to set the TrustStore and initialize the TrustManagerFactory. The
   *  TrustStore is used to store public keys and certificates for SSL.
   */
    public void setTrustStore(KeyStore truststore) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(truststore);
        tms = tmf.getTrustManagers();
    }


  //**************************************************************************
  //** setTrustStore
  //**************************************************************************
  /** Used to set the TrustStore and initialize the TrustManagerFactory. The
   *  TrustStore is used to store public keys and certificates for SSL.
   */
    public void setTrustStore(java.io.File trustStoreFile, String passphrase) throws Exception {
        char[] pw = passphrase.toCharArray();
        KeyStore truststore = KeyStore.getInstance("JKS");
        truststore.load(new java.io.FileInputStream(trustStoreFile), pw);
        setTrustStore(truststore);
    }


  //**************************************************************************
  //** setSSLProvider
  //**************************************************************************
  /** Used to specify an Security Provider used to decrypt SSL/TLS messages.
   */
    public void setSSLProvider(java.security.Provider provider){
        if (provider!=null){
            sslProvider = provider.getName();
            //java.security.Security.addProvider(provider);
        }
        else sslProvider = null;
    }


  //**************************************************************************
  //** setSSLProvider
  //**************************************************************************
  /** Used to specify an Security Provider used to decrypt SSL/TLS messages.
   */
    public void setSSLProvider(String provider){
        setSSLProvider(java.security.Security.getProvider(provider));
    }


  //**************************************************************************
  //** getSSLContext
  //**************************************************************************
  /** Used to initialize an SSLContext which, in turn is used by an SSLEngine 
   *  decrypt SSL/TLS messages.
   */
    public SSLContext getSSLContext() throws ServletException {
        
        /*//Debug use only!
        java.security.Provider provider = new SSLProvider();
        java.security.Security.addProvider(provider);
        setSSLProvider(provider);
        */


        if (sslContext==null)
        try{
            if (sslProvider==null) sslContext = SSLContext.getInstance("TLS");
            else sslContext = SSLContext.getInstance("TLS", sslProvider);
            sslContext.init(kms, tms, null);
        }
        catch(Exception e){
            ServletException se = new ServletException("Failed to initialize SSLContext.");
            se.initCause(e);
            throw se;
        }
        
        return sslContext;
    }


  //**************************************************************************
  //** supportsHttps
  //**************************************************************************
    public boolean supportsHttps(){
        try{
            return getSSLContext()!=null;
        }
        catch(Exception e){
            return false;
        }
    }
    
    
  //**************************************************************************
  //** getRequestHandler
  //**************************************************************************
  /** Returns an AbstractHandler used by Jetty to process requests.
   */
    public RequestHandler getRequestHandler(){
        if (handler==null) handler = new RequestHandler(this);
        return handler;
    }


  //**************************************************************************
  //** RequestHandler
  //**************************************************************************
  /** Called by Jetty. Do not override!
   */
    public class RequestHandler extends AbstractHandler {
        
        private final HttpServlet servlet;
        private SessionHandler sessionHandler;
        
        
        private RequestHandler(HttpServlet servlet){
            this.servlet = servlet;
        }
        
        public void init(javax.servlet.ServletConfig ServletConfig){
            
          //Initialize ServletContext
            ContextHandler.Context context=ContextHandler.getCurrentContext();
            javax.servlet.ServletContext servletContext=context==null?new ContextHandler.StaticContext():context;                
            servlet.servletContext = new ServletContext(servletContext);
        

          //Get server info. The server info is found in the jar file so do this  
          //now instead of at run-time with the first http request...
            String jettyVersion = servletContext.getServerInfo();
            String javaxtVersion = servlet.servletContext.getServerInfo();
            if (1<0) System.out.println(javaxtVersion + " (" + jettyVersion + ")"); 

        
          //Start the session handler
            sessionHandler = new SessionHandler();
            DefaultSessionCache sessionCache = new DefaultSessionCache(sessionHandler);
            if (sessionStore==null) sessionStore = new HashSessionStore();
            sessionCache.setSessionDataStore(sessionStore);
            sessionHandler.setSessionCache(sessionCache);  
            try{
                org.eclipse.jetty.server.Server server = this.getServer();
                sessionHandler.setServer(server);
                sessionHandler.start();

                /*
                _sessionIdManager=new DefaultSessionIdManager(server);
                server.setSessionIdManager(_sessionIdManager);
                server.manage(_sessionIdManager);
                _sessionIdManager.start();            
                */

            }
            catch(Exception e){
                e.printStackTrace();
            }
        
            
            servlet.init(ServletConfig);
        }
        
        @Override
        public void handle(                
            String target, Request baseRequest, 
            javax.servlet.http.HttpServletRequest request, 
            javax.servlet.http.HttpServletResponse response) 
            throws IOException, javax.servlet.ServletException {

          //Set session handler
            baseRequest.setSessionHandler(sessionHandler);


          //Jetty doesn't return the correct scheme for HTTPS so we need to update the baseRequest
            org.eclipse.jetty.io.EndPoint endPoint = baseRequest.getHttpChannel().getEndPoint();
            baseRequest.setScheme((endPoint instanceof SslConnection.DecryptedEndPoint)? "https" : "http");


          //Instantiate the JavaXT versions of Request and Response objects
            HttpServletRequest _request = new HttpServletRequest(request, servlet);
            HttpServletResponse _response = new HttpServletResponse(_request, response);


          //Process the request
            try{
                processRequest(_request, _response);
                baseRequest.setHandled(true);
            }
            catch(java.io.IOException e){
                throw e;
            }
            catch(ServletException e){
                response.setStatus(e.getStatusCode(), e.getMessage());            
                //response.sendError(e.getStatusCode(), e.getMessage());
                baseRequest.setHandled(true);
            }
            catch(Exception e){
                throw new javax.servlet.ServletException(e);
            }
        }
    }
    
    private class HashSessionStore extends AbstractSessionDataStore {
        
       
        @Override
        public SessionData load(String id) throws Exception { 
            return null;
        }

        @Override
        public SessionData newSessionData(String id, long created, long accessed, long lastAccessed, long maxInactiveMs) {
            return new SessionData(id, _context.getCanonicalContextPath(), _context.getVhost(), created, accessed, lastAccessed, maxInactiveMs);
        }

        @Override
        public boolean delete(String id) throws Exception {
           return true;
        }

        @Override
        public void doStore(String id, SessionData data, long lastSaveTime) throws Exception {

        }

        @Override
        public Set<String> doGetExpired(Set<String> candidates){
           return candidates; //whatever is suggested we accept
        }


        @Override
        public boolean isPassivating(){
            return false;
        }


        @Override
        public boolean exists(String id){
            return false;
        }        
        
    }
}