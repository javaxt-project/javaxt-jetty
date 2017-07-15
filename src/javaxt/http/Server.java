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

import javaxt.http.servlet.HttpServlet;
import javaxt.http.servlet.HttpServletRequest;
import javaxt.http.servlet.HttpServletResponse;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.ConnectionFactory;

import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.component.LifeCycle;
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
 *   A lightweight, multi-threaded web server based on Jetty 9.4
 *
 ******************************************************************************/

public class Server extends Thread {
    
    
    private static int numThreads;
    private java.util.ArrayList<InetSocketAddress> addresses =
            new java.util.ArrayList<InetSocketAddress>();

    private HttpServlet servlet;
    private Double tlsVersion = 1.0;
    
    
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
        this.addresses.clear();
        for (InetSocketAddress address : addresses){
            this.addresses.add(address);
        }
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
   *  @param args the command line arguments
   */
    public static void main(String[] args) {


      //Set the port to listen on
        InetSocketAddress[] addresses;
        if (args.length>0){
            try {
                int port = Integer.parseInt(args[0]);
                if (port < 0 || port > 65535) throw new Exception();
                addresses = new InetSocketAddress[]{
                    new InetSocketAddress(port)
                };
            }
            catch (Exception e) {
                System.out.println("Invalid Port: " + args[0]);
                return;
            }
        }
        else{
            addresses = new InetSocketAddress[]{
                new InetSocketAddress(80),
                new InetSocketAddress(443)
            };
        }


      //Instantiate the HttpServer with a sample HttpServlet
        try {
            Server webserver = new Server(addresses, 250, new ServletTest());
            webserver.start();
        }
        catch (Exception e) {
            System.out.println("Server could not start because of an " 
             + e.getClass());
            System.out.println(e);
        }
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
        ConcurrentMap<String, Logger> loggers = org.eclipse.jetty.util.log.Log.getMutableLoggers();
        synchronized(loggers){
            java.util.ArrayList<String> keys = new java.util.ArrayList<>();
            java.util.Iterator<String> it = loggers.keySet().iterator();
            while (it.hasNext()){
                String key = it.next();
                Logger logger = loggers.get(key);
                if (!(logger instanceof Server.Log)) keys.add(key);
            }
            if (!keys.isEmpty()){
                for (String key : keys){
                    loggers.put(key, serverLog);
                }
                loggers.notifyAll();
            }
        }
        
        
      //Setup Server
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(numThreads);
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(threadPool);
        

        
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


        
      //Add RequestHandler
        HttpServlet.RequestHandler requestHandler = servlet.getRequestHandler();
        server.setHandler(requestHandler);
        
        
        
      //Add lifecycle listener (used to initalize the servlet)
        server.addLifeCycleListener(new LifeCycle.Listener()
        {
            @Override
            public void lifeCycleStarting(LifeCycle arg0){
            }

            @Override
            public void lifeCycleStarted(LifeCycle arg0){
                try{
                    requestHandler.init(null);
                }
                catch(javaxt.http.servlet.ServletException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void lifeCycleStopping(LifeCycle arg0){
            } 
            
            @Override
            public void lifeCycleStopped(LifeCycle arg0){
            }     
            
            @Override
            public void lifeCycleFailure(LifeCycle arg0, Throwable t){
            }  
        });
        

      //Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);
        
        
        try{
            server.start();
            server.join();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    
  //**************************************************************************
  //** ServletTest
  //**************************************************************************
  /** Simple implementation of an JavaXT HttpServlet. Simply returns the
   *  request headers and body back to the client in plain text.
   */
    private static class ServletTest extends javaxt.http.servlet.HttpServlet {

        private boolean debug = false;
        
        public ServletTest() throws Exception {
            setKeyStore(new java.io.File("/temp/keystore.jks"), "password");
            setTrustStore(new java.io.File("/temp/keystore.jks"), "password");
        }

        public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {

            if (debug){
                System.out.println();
                System.out.println("New Request From: " + request.getRemoteAddr());
                System.out.println("TimeStamp: " + new java.util.Date());

              //Print the requested URL
                System.out.println(request.getMethod() + ": " + request.getURL().toString());
                System.out.println();
            }


          //Send response to the client
            try{

                //String str = new java.util.Date().toString() + "\r\n" + request.toString();
                //byte[] header = str.getBytes("UTF-8");
                
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

            if (debug){
                System.out.println(request.toString());
                System.out.println(response.toString());
            }
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
    public static class Log extends AbstractLogger{

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
    
}