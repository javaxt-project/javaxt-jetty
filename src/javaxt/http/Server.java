package javaxt.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javaxt.http.servlet.FormInput;
import javaxt.http.servlet.FormValue;

import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;
import javaxt.http.servlet.ServletContext;
import javaxt.http.servlet.ServletException;
import javaxt.http.websocket.WebSocketListener;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.AbstractSessionDataStore;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.SessionHandler;

import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Logger;

import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ssl.SslConnection;

import org.eclipse.jetty.http.HttpVersion;
//import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
//import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
//import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;



//******************************************************************************
//**  JavaXT Http Server
//******************************************************************************
/**
 *   A lightweight, multi-threaded web server used to process HTTP requests
 *   and send responses back to the client.
 *
 *   The server requires an implementation of the HttpServlet class. As new
 *   requests come in, they are passed to the HttpServlet.processRequest()
 *   method which is used to generate a response.
 *
 ******************************************************************************/

public class Server extends Thread {


    private static int numThreads;
    private InetSocketAddress[] addresses;
    private HttpServlet servlet;
    private static HttpServlet exceptionServlet = new ExceptionServlet();
    private Double tlsVersion = 1.0;
    public static boolean debug = false;


  //**************************************************************************
  //** Constructor
  //**************************************************************************
  /** Used to instantiate the Server on a given port.
   */
    public Server(int port, int numThreads, HttpServlet servlet) {
        this(new InetSocketAddress(port), numThreads, servlet);
    }


  //**************************************************************************
  //** Constructor
  //**************************************************************************
  /** Used to instantiate the Server on a given port and IP address.
   */
    public Server(InetSocketAddress address, int numThreads, HttpServlet servlet){
        this(new InetSocketAddress[]{address}, numThreads, servlet);
    }


  //**************************************************************************
  //** Constructor
  //**************************************************************************
  /** Used to instantiate the Server on multiple ports and/or IP addresses.
   */
    public Server(InetSocketAddress[] addresses, int numThreads, HttpServlet servlet){
        this.addresses = addresses;
        this.numThreads = numThreads;
        this.servlet = servlet;

    }


  //**************************************************************************
  //** Constructor
  //**************************************************************************
  /** Used to instantiate the Server on multiple ports and/or IP addresses.
   */
    public Server(java.util.List<InetSocketAddress> addresses, int numThreads, HttpServlet servlet){
        this(addresses.toArray(new InetSocketAddress[addresses.size()]), numThreads, servlet);
    }


  //**************************************************************************
  //** setMinTLSVersion
  //**************************************************************************
  /** By default, the server is configured to support TLS 1.0, 1.1, and 1.2.
   *  You can disable older ciphers by specifying a minimum TLS version (e.g. 1.2),
   */
    public void setMinTLSVersion(Double tlsVersion){
        if (tlsVersion==null) this.tlsVersion = null;
        else{
            if (tlsVersion>=1.0 && tlsVersion<=1.2){
                this.tlsVersion = tlsVersion;
            }
            else {
                this.tlsVersion = null;
            }
        }
    }


  //**************************************************************************
  //** Main
  //**************************************************************************
  /** Entry point for the application. Accepts command line arguments to
   *  specify which port to use and the maximum number of concurrent threads.
   *
   *  @param args Command line arguments. Options include:
   *  <ul>
   *  <li>-p to specify which port(s) to run on</li>
   *  <li>-debug to specify whether to output debug messages to the standard
   *  output stream.
   *  </li>
   *  <li>-dir to specify a path to a directory where html, js, css, images are
   *  found. The server will server content from this directory to web clients.
   *  </li>
   *  </ul>
   */
    public static void main(String[] args) throws Exception {


      //Set local variables
        java.io.File dir = null;
        InetSocketAddress[] addresses = null;
        java.io.File keystore = null;
        String keypass = null;


      //Parse inputs
        if (args.length>0){

            if (args.length==1){
                addresses = getAddresses(args[0]);
            }
            else{

                for (int i=0; i<args.length; i++){
                    String key = args[i];
                    if (!key.startsWith("-")) continue;
                    String val = (i<args.length-1) ? args[i+1] : null;
                    if (val!=null && !val.startsWith("-")){
                        i++;

                        if (key.startsWith("-p")){
                            addresses = getAddresses(val);
                        }
                        else if (key.startsWith("-debug")){
                            if (val.equalsIgnoreCase("true")) debug = true;
                        }
                        else if (key.startsWith("-dir")){
                            java.io.File f = new java.io.File(val);
                            if (f.exists()){
                                if (f.isFile()) f = f.getParentFile();
                                dir = f;
                            }
                        }
                        else if (key.startsWith("-keystore")){
                            keystore = new java.io.File(val);
                        }
                        else if (key.startsWith("-keypass")){
                            keypass = val;
                        }
                    }
                }
            }
        }


      //If we're still here, and addresses are null, specify default addresses
      //for the server to use
        if (addresses==null) addresses = new InetSocketAddress[]{
            new InetSocketAddress(80),
            new InetSocketAddress(443)
        };


      //Instantiate the server with the default/test servlet
        Server webserver = new Server(addresses, 250, new ServletTest(dir, keystore, keypass));
        webserver.start();
    }


  //**************************************************************************
  //** getAddresses
  //**************************************************************************
  /** Used to parse command line inputs and return a list of socket addresses.
   */
    private static InetSocketAddress[] getAddresses(String str) throws IllegalArgumentException {
        java.util.ArrayList<InetSocketAddress> addresses =
        new java.util.ArrayList<InetSocketAddress>();

        for (String s : str.split(",")){
            try{
                int port = Integer.parseInt(s);
                if (port < 0 || port > 65535) throw new Exception();
                addresses.add(new InetSocketAddress(port));
            }
            catch(Exception e){
                throw new IllegalArgumentException();
            }
        }
        return addresses.toArray(new InetSocketAddress[addresses.size()]);
    }


  //**************************************************************************
  //** Run
  //**************************************************************************
  /** Used to start the web server. Creates a thread pool and instantiates a
   *  socket listener for each specified port/address.
   */
    public void run() {


      //Configure logging
        Server.Log serverLog = new Server.Log();
        org.eclipse.jetty.util.log.Log.setLog(serverLog);
//        ConcurrentMap<String, Logger> loggers = org.eclipse.jetty.util.log.Log.getMutableLoggers();
//        synchronized(loggers){
//            java.util.ArrayList<String> keys = new java.util.ArrayList<>();
//            java.util.Iterator<String> it = loggers.keySet().iterator();
//            while (it.hasNext()){
//                String key = it.next();
//                Logger logger = loggers.get(key);
//                if (!(logger instanceof Server.Log)) keys.add(key);
//            }
//            if (!keys.isEmpty()){
//                for (String key : keys){
//                    loggers.put(key, serverLog);
//                }
//                loggers.notifyAll();
//            }
//        }


      //Configure server
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(numThreads);
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(threadPool);
        server.setHandler(new RequestHandler(servlet));
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);


      //HTTP Configuration
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setBlockingTimeout(30000);
        //httpConfig.setSecureScheme("https");
        //httpConfig.setSecurePort(443);
        httpConfig.setSendServerVersion(false);
        httpConfig.setSendDateHeader(false);



      //Create a new SocketListener for each port/address
        for (InetSocketAddress address : addresses){
            String hostName = address.getHostName();
            try{

                HttpConnectionFactory http1 = new HttpConnectionFactory(httpConfig);
                //HTTP2ServerConnectionFactory http2 = new HTTP2ServerConnectionFactory(httpConfig);
                //HTTP2ServerConnectionFactory http2c = new HTTP2ServerConnectionFactory(httpConfig);


              //Create server connector
                ServerConnector http;
                javax.net.ssl.SSLContext sslContext = servlet.getSSLContext();
                if (sslContext!=null){
                    SslContextFactory sslContextFactory = new SslContextFactory();

                    if (tlsVersion!=null){
                        /*
                        if (tlsVersion==1.2){

                            sslContextFactory.setIncludeProtocols("TLSv1.2");
                            sslContextFactory.setIncludeCipherSuites(
                                "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                                "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
                                "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"
                            );
                        }
                        */
                        if (tlsVersion<1.2){

                            //sslContextFactory.setIncludeProtocols("TLSv1","TLSv1.1");
                            sslContextFactory.setExcludeCipherSuites( //For TLSv1 and TLSv1.1
                                "SSL_RSA_WITH_DES_CBC_SHA",
                                "SSL_DHE_RSA_WITH_DES_CBC_SHA",
                                "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA"
                            );
                        }
                    }
                    else{
                        //tlsVersion unspecified, use Jetty defaults...
                    }

                    sslContextFactory.setSslContext(sslContext);
                    _SslConnectionFactory ssl = new _SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString());
                    HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
                    http = new ServerConnector(server, ssl, new HttpConnectionFactory(httpsConfig));
                }
                else{
                    http = new ServerConnector(server, http1); // new ServerConnector(server, http1, http2, http2c)
                }


                http.setHost(hostName);
                http.setPort(address.getPort());
                http.setIdleTimeout(30000);
                server.addConnector(http);

                System.out.print("Accepting connections on " + hostName + ":" + address.getPort() + "\r\n");
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }




      //Start the server
        try{
            server.start();
            server.join();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


  //**************************************************************************
  //** ExceptionServlet
  //**************************************************************************
  /** Dummy servlet used to send ServletException errors to the client. Only
   *  used when a servlet request is not available.
   */
    private static class ExceptionServlet extends HttpServlet {
        public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {
        }
    }


  //**************************************************************************
  //** ServletTest
  //**************************************************************************
  /** Simple implementation of an JavaXT HttpServlet. Simply returns the
   *  request headers and body back to the client in plain text.
   */
    private static class ServletTest extends javaxt.http.servlet.HttpServlet {

        private final java.io.File dir;
        private final String s = System.getProperty("file.separator");

        public ServletTest(java.io.File dir, java.io.File keystore, String keypass) throws Exception {
            this.dir = dir;

            if (keystore!=null){
                try{
                    setKeyStore(keystore, keypass);
                    setTrustStore(keystore, keypass);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }


        public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {

          //Print the requested URL
            log();
            log("New Request From: " + request.getRemoteAddr());
            log("TimeStamp: " + new java.util.Date());
            log(request.getMethod() + ": " + request.getURL().toString());
            log();


          //Process websocket requests
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
                return;
            }


          //Process form data
            if (request.getMethod().equals("POST")){
//                if (true){
//                    java.io.File f = new java.io.File(dir + s + "uploads" + s + "test.txt");
//                    byte[] bytes = request.getBody();
//                    java.io.FileOutputStream output = null;
//                    try {
//                        f.getParentFile().mkdirs();
//                        output = new java.io.FileOutputStream(f);
//                        output.write(bytes);
//                    }
//                    catch (Exception e){}
//                    finally {
//                        try { if (output != null) output.close(); }
//                        catch (Exception e){}
//                    }
//                }
                boolean fileUploaded = false;
                StringBuilder str = new StringBuilder();
                java.util.Iterator<FormInput> it = request.getFormInputs();
                while (it.hasNext()){
                    FormInput input = it.next();
                    String name = input.getName();
                    FormValue value = input.getValue();

                    str.append(name);
                    str.append(": ");
                    if (input.isFile()){
                        value.toFile(new java.io.File(dir + s + "uploads" + s + input.getFileName()));
                        str.append(input.getFileName());
                        str.append("*");
                        fileUploaded = true;
                    }
                    else{
                        str.append(value);
                    }
                    str.append("\r\n");
                }
                if (fileUploaded) str.append("\r\n* File uploaded to the uploads directory");
                response.setContentType("text/plain");
                response.write(str.toString());
                return;
            }


          //Send data
            if (dir!=null){

              //Get requested path
                String path = request.getURL().getPath();
                if (path.length()>1 && path.startsWith("/")) path = path.substring(1);


              //Construct a physical file path using the url
                java.io.File file = new java.io.File(dir + s + path);
                if (file.exists()){
                    if (file.isDirectory()){
                        file = new java.io.File(file, "index.html");
                    }
                }


              //If the file doesn't exist, return an error
                if (!file.exists()){
                    response.setStatus(404);
                }
                else{ //Dump the file content to the servlet output stream
                    String ext = null;
                    int x = file.getName().lastIndexOf(".");
                    if (x!=-1) ext = file.getName().substring(x+1).toLowerCase();
                    response.setBufferSize(8096*8);
                    response.write(file, getContentType(ext), true);
                }
            }
            else{

              //Send sample http response to the client
                try{

                    byte[] header = request.toString().getBytes("UTF-8");
                    byte[] body = request.getBody();
                    byte[] msg = new byte[header.length + body.length];

                    System.arraycopy(header,0,msg,0,header.length);
                    System.arraycopy(body,0,msg,header.length,body.length);

                    header = null;
                    body = null;

                    response.setContentType("text/plain");
                    response.write(msg);

                    msg = null;

                }
                catch(Exception e){
                }
            }


            log(request.toString());
            log(response.toString());
        }


        private String getContentType(String ext){
            if (ext!=null) {
                if (ext.equals("css")) return "text/css";
                if (ext.equals("htm") || ext.equals("html")) return "text/html";
                if (ext.equals("js")) return "text/javascript";
                if (ext.equals("txt")) return "text/plain";
                if (ext.equals("gif")) return "image/gif";
                if (ext.equals("jpg")) return "image/jpeg";
                if (ext.equals("png")) return "image/png";
                if (ext.equals("ico")) return "image/vnd.microsoft.icon";
            }
            return "application/octet-stream";
        }

        public void log(){
            log("");
        }
        public void log(String msg){
            Server.log(msg);
        }

    }


  //**************************************************************************
  //** log
  //**************************************************************************
  /** Used to log messages to the standard output stream when the server is
   *  in debug mode.
   */
    public static void log(Object obj) {
        if (!debug) return;

        String md = "[" + getTime() + "] ";
        String padding = "";
        for (int i=0; i<md.length(); i++){
            padding+= " ";
        }
        String str;
        if (obj instanceof String){
            str = (String) obj;
            if (str.length()>0){
                String[] arr = str.split("\n");
                for (int i=0; i<arr.length; i++){
                    if (i==0) str = md + arr[i].trim() + "\r\n";
                    else str += padding + arr[i].trim() + "\r\n";
                }
                str = str.trim();
            }
        }
        else if (obj instanceof Exception){
            str = md + obj;
            ((Exception) obj).printStackTrace();
        }
        else {
            str = md + obj;
        }
        synchronized(System.out) { System.out.println(str); }
    }


    private static String getTime(){
        java.util.Date d = new java.util.Date();
        return pad(d.getHours()) + ":" + pad(d.getMinutes()) + ":" + pad(d.getSeconds());
    }

    private static String pad(int i){
        if (i<10) return "0"+i;
        else return i+"";
    }


  //**************************************************************************
  //** RequestHandler
  //**************************************************************************
  /** Custom implementation of an AbstractHandler
   */
    private static class RequestHandler extends AbstractHandler {

        private final HttpServlet servlet;
        private SessionHandler sessionHandler;
        private SessionDataStore sessionStore;

        private RequestHandler(HttpServlet servlet){
            this(servlet, null);
        }

        private RequestHandler(HttpServlet servlet, SessionDataStore sessionStore){
            this.servlet = servlet;
            this.sessionStore = sessionStore;
        }


        @Override
        public void doStart() throws Exception {


          //Initialize ServletContext
            ContextHandler.Context context=ContextHandler.getCurrentContext();
            javax.servlet.ServletContext servletContext=context==null?new ContextHandler.StaticContext():context;


          //Add the RequestHandler to the ServletContext
            servletContext.setAttribute("org.eclipse.jetty.server.Handler", this);


          //Add ServletContext to the HttpServlet
            servlet.setServletContext(new ServletContext(servletContext));


          //Get server info. The server info is found in the jar file so do this
          //now instead of at run-time with the first http request...
            String jettyVersion = servletContext.getServerInfo();
            String javaxtVersion = servlet.getServletContext().getServerInfo();
            if (1<0) System.out.println(javaxtVersion + " (" + jettyVersion + ")");


          //Start the session handler
            sessionHandler = new SessionHandler();
            DefaultSessionCache sessionCache = new DefaultSessionCache(sessionHandler);
            if (sessionStore==null) sessionStore = new SessionStore();
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


            javax.servlet.ServletConfig ServletConfig = null;
            servlet.init(ServletConfig);


          //Call parent
            super.doStart();
        }

        @Override
        public void handle(
            String target, Request baseRequest,
            javax.servlet.http.HttpServletRequest request,
            javax.servlet.http.HttpServletResponse response)
            throws IOException, javax.servlet.ServletException {

          //Add reference to the baseRequest to the HttpServletRequest
            request.setAttribute("org.eclipse.jetty.server.Request", baseRequest);
            request.setAttribute("javax.servlet.http.HttpServletRequest", request);
            request.setAttribute("javax.servlet.http.HttpServletResponse", response);


          //Set session handler
            baseRequest.setSessionHandler(sessionHandler);


          //Jetty doesn't return the correct scheme for HTTPS so we need to update the baseRequest
            org.eclipse.jetty.io.EndPoint endPoint = baseRequest.getHttpChannel().getEndPoint();
            if (endPoint instanceof SslConnection.DecryptedEndPoint){
                baseRequest.setScheme("https");
                baseRequest.setSecure(true);
            }
            else{
                baseRequest.setScheme("http");
                baseRequest.setSecure(false);
            }


          //Instantiate the JavaXT versions of Request and Response objects
            HttpServletRequest _request = new HttpServletRequest(request, servlet);
            HttpServletResponse _response = new HttpServletResponse(_request, response);


          //Process the request
            try{
                servlet.processRequest(_request, _response);
                baseRequest.setHandled(true);
            }
            catch(java.io.IOException e){
                throw e;
            }
            catch(ServletException e){
                //response.sendError(e.getStatusCode(), e.getMessage());
                sendError(e, _request, _response, request, response);
                baseRequest.setHandled(true);
            }
            catch(Throwable e){ //Catches both errors and exceptions
                //throw new javax.servlet.ServletException(e);
                String s = e.getClass().getName();
                String message = e.getLocalizedMessage();
                s = (message != null) ? (s + ": " + message) : s;
                ServletException ex = new ServletException(500, s);
                ex.setStackTrace(e.getStackTrace());
                sendError(ex, _request, _response, request, response);
            }
        }


        private void sendError(
            ServletException e, HttpServletRequest request, HttpServletResponse response,
            javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse rsp)
        {
            log(e.getMessage());
            try{
                if (request==null){
                    request = new HttpServletRequest(req, exceptionServlet);
                    response = new HttpServletResponse(request, rsp);
                }
                else{
                    if (response==null){
                        response = new HttpServletResponse(request, rsp);
                    }
                }

                response.setHeader("Cache-Control", "no-cache");
                response.setStatus(e.getStatusCode());
                response.setContentType("text/plain");


                String s = e.getClass().getName();
                s = s.substring(s.lastIndexOf(".")+1);
                String message = e.getLocalizedMessage();
                StringBuilder error = new StringBuilder((message != null) ? (s + ": " + message) : s);
                for (StackTraceElement x : e.getStackTrace()){
                    String err = x.toString();
                    if (err.contains("org.eclipse.jetty")) break;
                    error.append("\n");
                    error.append(err);
                }
                response.write(error.toString());
            }
            catch(Exception ex){
                //TODO: Need to propgate error to the client!
                //ex.printStackTrace();
            }
        }


        @Override
        public void doStop(){
            servlet.destroy();
        }
    }






  //**************************************************************************
  //** _SslConnectionFactory
  //**************************************************************************
  /** Custom connection factory used to handle SSL and non-SSL requests on the
   *  same port. Credit:
   *  http://stackoverflow.com/a/40076056/777443
   */
    private class _SslConnectionFactory extends org.eclipse.jetty.server.AbstractConnectionFactory {

        private final SslContextFactory _sslContextFactory;
        private final String _nextProtocol;

        public _SslConnectionFactory() { this(HttpVersion.HTTP_1_1.asString()); }

        public _SslConnectionFactory(@Name("next") final String nextProtocol) { this((SslContextFactory)null, nextProtocol); }

        public _SslConnectionFactory(@Name("sslContextFactory") final SslContextFactory factory, @Name("next") final String nextProtocol) {
            super("SSL");
            this._sslContextFactory = factory == null?new SslContextFactory():factory;
            this._nextProtocol = nextProtocol;
            this.addBean(this._sslContextFactory);
        }

        public SslContextFactory getSslContextFactory() { return this._sslContextFactory; }

        @Override protected void doStart() throws Exception {
            super.doStart();
            final SSLEngine engine = this._sslContextFactory.newSSLEngine();
            engine.setUseClientMode(false);


            /*
            for (String protocol : engine.getEnabledProtocols()) System.out.println("- " + protocol);
            for (String protocol : engine.getSupportedProtocols()) System.out.println(protocol);
            for (String cipher : engine.getEnabledCipherSuites()) System.out.println(cipher);
            String supportedCiphers[] = engine.getSupportedCipherSuites();
            */

            final SSLSession session = engine.getSession();
            if(session.getPacketBufferSize() > this.getInputBufferSize()) this.setInputBufferSize(session.getPacketBufferSize());
        }

        @Override public Connection newConnection(final Connector connector, final EndPoint realEndPoint) {
            final ReadAheadEndpoint aheadEndpoint = new ReadAheadEndpoint(realEndPoint, 1);
            final byte[] bytes = aheadEndpoint.getBytes();
            boolean isSSL = false;
            if (bytes == null || bytes.length == 0) {
                //System.out.println("NO-Data in newConnection : "+aheadEndpoint.getRemoteAddress());
                isSSL = true;
            }
            else {
                final byte b = bytes[0];    // TLS first byte is 0x16 , SSLv2 first byte is >= 0x80 , HTTP is guaranteed many bytes of ASCII
                isSSL = b >= 0x7F || (b < 0x20 && b != '\n' && b != '\r' && b != '\t');

//              //The following logic is from JavaXT Server 2.x
//                if ((b>19 && b<25) || b==-128){
//                    isSSL = true;
//                }
            }
            //System.out.println("newConnection["+isSSL+"] : "+aheadEndpoint.getRemoteAddress());

            final EndPoint      plainEndpoint;
            final SslConnection sslConnection;
            if (isSSL) {
                final SSLEngine engine = this._sslContextFactory.newSSLEngine(aheadEndpoint.getRemoteAddress());
                engine.setUseClientMode(false);
                sslConnection = this.newSslConnection(connector, aheadEndpoint, engine);
                sslConnection.setRenegotiationAllowed(this._sslContextFactory.isRenegotiationAllowed());
                this.configure(sslConnection, connector, aheadEndpoint);
                plainEndpoint = sslConnection.getDecryptedEndPoint();
            }
            else {
                sslConnection = null;
                plainEndpoint = aheadEndpoint;
            }


            final ConnectionFactory next = connector.getConnectionFactory(_nextProtocol);
            final Connection connection = next.newConnection(connector, plainEndpoint);
            plainEndpoint.setConnection(connection);
            return sslConnection == null ? connection : sslConnection;
        }

        protected SslConnection newSslConnection(final Connector connector, final EndPoint endPoint, final SSLEngine engine) {
            return new SslConnection(connector.getByteBufferPool(), connector.getExecutor(), endPoint, engine);
        }

        @Override public String toString() {
            return String.format("%s@%x{%s->%s}", new Object[]{this.getClass().getSimpleName(), Integer.valueOf(this.hashCode()), this.getProtocol(), this._nextProtocol});
        }



        private class ReadAheadEndpoint implements EndPoint {

            /** real endpoint we are wrapping    */ private final EndPoint endPoint;
            /** buffer used to read start bytes  */ private final ByteBuffer start     ;
            /** how many N start bytes to read   */ private       int        leftToRead;
            /** first  N bytes                   */ private final byte[]     bytes     ;
            /** buffered exception to throw next */ private IOException pendingException = null;
            @Override public InetSocketAddress getLocalAddress            () { return endPoint.getLocalAddress(); }
            @Override public InetSocketAddress getRemoteAddress           () { return endPoint.getRemoteAddress(); }
            @Override public boolean           isOpen                     () { return endPoint.isOpen(); }
            @Override public long              getCreatedTimeStamp        () { return endPoint.getCreatedTimeStamp(); }
            @Override public boolean           isOutputShutdown           () { return endPoint.isOutputShutdown(); }
            @Override public boolean           isInputShutdown            () { return endPoint.isInputShutdown(); }
            @Override public void              shutdownOutput             () { endPoint.shutdownOutput(); }
            @Override public void              close                      () { endPoint.close(); }
            @Override public Object            getTransport               () { return endPoint.getTransport(); }
            @Override public long              getIdleTimeout             () { return endPoint.getIdleTimeout(); }
            @Override public Connection        getConnection              () { return endPoint.getConnection(); }
            @Override public void              onOpen                     () { endPoint.onOpen(); }
            @Override public void              onClose                    () { endPoint.onClose(); }
            @Override public boolean           isOptimizedForDirectBuffers() { return endPoint.isOptimizedForDirectBuffers(); }
            @Override public boolean           isFillInterested           () { return endPoint.isFillInterested(); }
            @Override public boolean           flush                      (final ByteBuffer... v) throws IOException { return endPoint.flush(v); }
            @Override public void              setIdleTimeout             (final long          v) { endPoint.setIdleTimeout(v); }
            @Override public void              write                      (final Callback      v, final ByteBuffer... b) throws WritePendingException { endPoint.write(v, b); }
            @Override public void              setConnection              (final Connection    v) { endPoint.setConnection(v); }
            @Override public void              upgrade                    (final Connection    v) { endPoint.upgrade(v); }
            @Override public void              fillInterested  (final Callback   v) throws ReadPendingException { endPoint.fillInterested(v); }
            @Override public boolean           tryFillInterested(final Callback v) { return endPoint.tryFillInterested(v); }
            @Override public int               hashCode() { return endPoint.hashCode(); }
            @Override public boolean           equals(final Object obj) { return endPoint.equals(obj); }
            @Override public String            toString() { return endPoint.toString(); }
            public byte[] getBytes() { if (pendingException == null) { try { readAhead(); } catch (final IOException e) { pendingException = e; } } return bytes; }
            private void throwPendingException() throws IOException { if (pendingException != null) { final IOException e = pendingException; pendingException = null; throw e; } }

            public ReadAheadEndpoint(final EndPoint channel, final int readAheadLength){
                this.endPoint = channel;
                start = ByteBuffer.wrap(bytes = new byte[readAheadLength]);
                start.flip();
                leftToRead = readAheadLength;
            }

            private synchronized void readAhead() throws IOException {
                if (leftToRead > 0) {

                    int numBytesRead = endPoint.fill(start);
                    int numRetries = 0;

                    if (numBytesRead==0){


                        while ((numBytesRead = endPoint.fill(start))<1) {

                            if (numBytesRead==-1) throw new IOException("Received -1 bytes. Socket is closed.");

                            numRetries++;
                            if (numRetries>15000){
                                throw new IOException("Timeout waiting for bytes from the client.");
                            }

                            try {
                                Thread.sleep(1);
                            }
                            catch (InterruptedException e) {
                                throw new IOException("Interrupt!");
                            }
                        }

                    }

                    if (numBytesRead==-1) throw new IOException("Received -1 bytes. Socket is closed.");
                    leftToRead -= numBytesRead;
                    if (leftToRead <= 0) start.rewind();
                }
            }

            private int readFromStart(final ByteBuffer dst) throws IOException {
                final int n = Math.min(dst.remaining(), start.remaining());
                if (n > 0)  {
                    dst.put(bytes, start.position(), n);
                    start.position(start.position() + n);
                    dst.flip();
                }
                return n;
            }

            @Override public synchronized int fill(final ByteBuffer dst) throws IOException {
                throwPendingException();
                if (leftToRead > 0) readAhead();
                if (leftToRead > 0) return 0;
                final int sr = start.remaining();
                if (sr > 0) {
                    dst.compact();
                    final int n = readFromStart(dst);
                    if (n < sr) return n;
                }
                return sr + endPoint.fill(dst);
            }

        }

    }



  //**************************************************************************
  //** ServerLog
  //**************************************************************************
  /** Implementation of a Jetty Logger. This logger actually doesn't log
   *  anything. You can use a different logger at any time by calling
   *  org.eclipse.jetty.util.log.Log.setLog();
   */
    @ManagedObject("JavaXT Logging Implementation")
    private static class Log extends AbstractLogger{

        private final String _name;

        public Log(){
            this(null);
        }

        public Log(String name){
            this(name,null);
        }

        public Log(String name, Properties props){
            _name = name==null ? "" : name;
        }

        public String getName(){
            return _name;
        }

        public void setPrintLongNames(boolean printLongNames){}
        public boolean isPrintLongNames(){
            return false;
        }

        public void setHideStacks(boolean hideStacks){}
        public boolean isHideStacks(){
            return true;
        }

        public void setSource(boolean source){}
        public boolean isSource(){
            return false;
        }

        public void ignore(Throwable ignored){}
        public void warn(String msg, Object... args){}
        public void warn(Throwable thrown){}
        public void warn(String msg, Throwable thrown){}
        public void info(String msg, Object... args){}
        public void info(Throwable thrown){}
        public void info(String msg, Throwable thrown){}
        public void debug(String msg, Object... args){}
        public void debug(String msg, long arg){}
        public void debug(Throwable thrown){}
        public void debug(String msg, Throwable thrown){}

        public void setDebugEnabled(boolean enabled){}
        public boolean isDebugEnabled(){
            return false;
        }

        public void setLevel(int level){}
        public int getLevel(){
            return 0;
        }

        public void setStdErrStream(java.io.PrintStream stream){}

        protected void format(StringBuilder buffer, Throwable thrown){}
        protected void format(StringBuilder buffer, Throwable thrown, String indent){}

        public static int getLoggingLevel(Properties props,String name){
            return lookupLoggingLevel(props,name);
        }

        public static Server.Log getLogger(Class<?> clazz){
            org.eclipse.jetty.util.log.Logger log = Log.getLogger(clazz);
            if (log instanceof Server.Log) return (Server.Log)log;
            throw new RuntimeException("Invalid logger for " + clazz);
        }

        @Override
        protected Server.Log newLogger(String fullname){
            return new Server.Log(fullname);
        }

        @Override
        public String toString(){
            return "";
        }
    }


  //**************************************************************************
  //** SessionStore
  //**************************************************************************
  /** Implementation of a Jetty SessionStore. This store actually doesn't do
   *  anything.
   */
    private static class SessionStore extends AbstractSessionDataStore {


        @Override
        public SessionData load(String id) throws Exception {
            return null;
        }

        @Override
        public SessionData doLoad(String id) throws Exception{
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
        public java.util.Set<String> doGetExpired(java.util.Set<String> candidates){
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