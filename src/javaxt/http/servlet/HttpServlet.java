package javaxt.http.servlet;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;

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
    private String sslProvider;
    private ServletContext servletContext;
    private String servletPath = "/";


  //**************************************************************************
  //** init
  //**************************************************************************
  /** Called by the servlet container to indicate to a servlet that it is
   *  being placed into service.
   */
    public void init(Object servletConfig) throws ServletException {}


  //**************************************************************************
  //** destroy
  //**************************************************************************
  /** Called by the servlet container to indicate to a servlet that it is
   *  being taken out of service or that the server is shutting down.
   */
    public void destroy(){};


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


  //**************************************************************************
  //** setServletContext
  //**************************************************************************
    public void setServletContext(ServletContext servletContext){
        this.servletContext = servletContext;

      //Instantiate the WebSocketServer
        if (servletContext.getAttribute("org.eclipse.jetty.server.Handler") instanceof AbstractHandler){
            if (servletContext.getAttribute("javaxt.http.websocket.WebSocketServer")==null){
                servletContext.setAttribute("javaxt.http.websocket.WebSocketServer",
                    new javaxt.http.websocket.WebSocketServer(this)
                );
            }
        }
    }


  //**************************************************************************
  //** getServletPath
  //**************************************************************************
  /** Returns the path to the servlet. This path starts with a "/" character
   *  and includes either the servlet name or a path to the servlet, but does
   *  not include any extra path information or a query string.
   */
    public String getServletPath(){
        return servletPath;
    }


  //**************************************************************************
  //** setServletPath
  //**************************************************************************
  /** Used to set the path to the servlet.
   */
    public void setServletPath(String servletPath){
        if (servletPath==null){
            servletPath = "/";
        }
        else{
            if (servletPath.length()>1 && servletPath.startsWith("/")) servletPath = servletPath.substring(1);
            if (servletPath.endsWith("/")) servletPath = servletPath.substring(0, servletPath.length()-1);
            servletPath = "/" + servletPath;
        }
        this.servletPath = servletPath;
    }


  //**************************************************************************
  //** log
  //**************************************************************************
  /** Writes the specified message to a servlet log. This method has not
   *  been implemented.
   */
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



//  //**************************************************************************
//  //** setSessionStore
//  //**************************************************************************
//    public void setSessionStore(java.io.File file){
//        FileSessionDataStore sessionStore = new FileSessionDataStore();
//        sessionStore.setStoreDir(file);
//        this.sessionStore = sessionStore;
//    }
//

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

        SSLContext sslContext = null;
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
  /** Returns true if the servlet has been configured to support HTTP/SSL.
   *  This is determined by checking if a KeyStore or a KeyManager has been
   *  assigned.
   */
    public boolean supportsHttps(){
        if (kms!=null && kms.length>0){
            if (kms[0]!=null) return true;
        }
        return false;
    }

}