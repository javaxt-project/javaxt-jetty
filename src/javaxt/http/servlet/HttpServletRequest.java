package javaxt.http.servlet;
import java.io.IOException;
import java.util.Locale;
import java.nio.ByteBuffer;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.eclipse.jetty.server.HttpInput;

//******************************************************************************
//**  HttpServletRequest
//******************************************************************************
/**
 *   Used to read raw bytes sent from the client to the server. Assumes the
 *   data is a valid HTTP/1.1 request. Supports both http and https (ssl/tls).
 *   This class implements the javax.servlet.http.HttpServletRequest interface
 *   defined in Version 2.5 of the Java Servlet API.
 *
 ******************************************************************************/

public class HttpServletRequest {

    private javax.servlet.http.HttpServletRequest request;
    private ServletContext servletContext;
    
    private java.net.URL url;
    private Boolean isKeepAlive;
    
    private Authenticator authenticator;
    private boolean authenticate = true;
    private ServletException authenticationException = null;
    private java.security.Principal principal;
    private boolean getUserPrincipal = true;
    private boolean getCredentials = true;
    private String[] credentials = null;    
    
    
    public HttpServletRequest(javax.servlet.http.HttpServletRequest request, HttpServlet servlet){
        this.request = request;
        this.servletContext = servlet.getServletContext();
        
        try{
            StringBuffer str = request.getRequestURL();
            String query = request.getQueryString();
            if (query!=null){ 
                str.append("?");
                str.append(query);
            }
            this.url = new java.net.URL(str.toString());
        }
        catch(Exception e){
            e.printStackTrace();
            //Should never happen...
        }
        
        
      //Instantiate the authenticator
        try{
            authenticator = servlet.getAuthenticator(this);
        }
        catch(Exception e){
            //TODO: Figure out how to propogate this error to the caller!
            e.printStackTrace();
        }
        
    }
    

  //**************************************************************************
  //** getRemoteAddr
  //**************************************************************************
  /** Returns the IP address of the client that sent the request.
   */
    public String getRemoteAddr(){
        return request.getRemoteAddr();
    }


  //**************************************************************************
  //** getRemoteHost
  //**************************************************************************
  /** Returns the hostname of the client that sent the request. 
   */
    public String getRemoteHost(){
        return request.getRemoteHost();
    }


  //**************************************************************************
  //** getRemotePort
  //**************************************************************************
  /** Returns the port of the client that sent the request.
   */
    public int getRemotePort(){
        return request.getRemotePort();
    }

    
  //**************************************************************************
  //** getHttpVersion
  //**************************************************************************
  /** Returns the HTTP version number passed in as part of the request (e.g. 
   *  "1.0", "1.1", etc).
   */
    public String getHttpVersion(){
        String protocol = request.getProtocol();
        if (protocol!=null){
            int idx = protocol.indexOf("/");
            if (idx>0) return protocol.substring(idx+1);
        }
        return null;
    }
    
    
  //**************************************************************************
  //** getHeader
  //**************************************************************************
  /** Returns the value of the specified request header as a String. If the
   *  request did not include a header of the specified name, this method
   *  returns null. If there are multiple headers with the same name, this
   *  method returns the first head in the request. The header name is case
   *  insensitive.
   * 
   *  @param name A String specifying the header name (e.g. "Content-Encoding")
   *  The header name is case insensitive. 
   */
    public String getHeader(String name){
        return request.getHeader(name);
    }

//
//  //**************************************************************************
//  //** setHeader
//  //**************************************************************************
//    public void setHeader(String name, String value){
//        request.setHeader(name, value);
//    }


  //**************************************************************************
  //** getHeaders
  //**************************************************************************
  /** Returns all the values of the specified request header as an Enumeration.
   *  If the request did not include any headers of the specified name, this 
   *  method returns an empty Enumeration.
   *
   *  @param name A String specifying the header name (e.g. "Accept-Language").
   *  The header name is case insensitive. 
   */
    public java.util.Enumeration<String> getHeaders(String name){
        return request.getHeaders(name);
    }


  //**************************************************************************
  //** getHeaderNames
  //**************************************************************************
  /** Returns an enumeration of all the header names this request contains. If 
   *  the request has no headers, this method returns an empty enumeration.
   */
    public java.util.Enumeration<String> getHeaderNames(){
        return request.getHeaderNames();
    }


  //**************************************************************************
  //** getIntHeader
  //**************************************************************************
  /** Returns the value of the specified request header as an int. If the
   *  request does not have a header of the specified name, this method 
   *  returns -1. If the header cannot be converted to an integer, this method
   *  throws a NumberFormatException.
   * 
   *  @param name A String specifying the header name (e.g. "Content-Length").
   *  The header name is case insensitive.
   */
    public int getIntHeader(String name) throws NumberFormatException {
        return request.getIntHeader(name);
    }

    
  //**************************************************************************
  //** getDateHeader
  //**************************************************************************
  /** Returns the value of the specified request header as a long representing
   *  the number of milliseconds since January 1, 1970 GMT. If the request did 
   *  not have a header of the specified name, this method returns -1. If the 
   *  header can't be converted to a date, the method throws an
   *  IllegalArgumentException.
   *
   *  @param name A String specifying the header name (e.g. "If-Modified-Since").
   *  The header name is case insensitive.
   */
    public long getDateHeader(String name) throws IllegalArgumentException{
        return request.getDateHeader(name);
    }


  //**************************************************************************
  //** getCharacterEncoding
  //**************************************************************************
  /** Returns the name of the character encoding used in the body of this
   *  request as specified in the "Content-Type" in the request header (e.g.
   *  "UTF-8"). Returns a null if the request does not specify a character 
   *  encoding.
   */
    public String getCharacterEncoding(){
        return request.getCharacterEncoding();
    }


  //**************************************************************************
  //** setCharacterEncoding
  //**************************************************************************
  /** Overrides the name of the character encoding used in the body of this
   *  request. This method must be called prior to reading request parameters
   *  or reading input using getReader().
   */
    public void setCharacterEncoding(String env) throws java.io.UnsupportedEncodingException{
        request.setCharacterEncoding(env);
    }


  //**************************************************************************
  //** getContentType
  //**************************************************************************
  /** Returns the "Content-Type" defined in the request header. Returns null
   *  if the "Content-Type" is not defined.
   */
    public String getContentType(){
        return request.getContentType();
    }


  //**************************************************************************
  //** getLocale
  //**************************************************************************
  /** Returns the preferred Locale that the client will accept content in,
   *  based on the "Accept-Language" header. If the client request doesn't 
   *  provide an Accept-Language header, this method returns the default 
   *  locale for the server.
   */
    public Locale getLocale(){
        return request.getLocale();
    }


  //**************************************************************************
  //** getLocales
  //**************************************************************************
  /** Returns an Enumeration of Locale objects indicating the locales that are 
   *  acceptable to the client based on the Accept-Language header. The list
   *  of Locales is ordered, starting with the preferred locale. If the client 
   *  request doesn't provide an Accept-Language header, this method returns 
   *  an Enumeration containing one Locale, the default locale for the server.
   */
    public java.util.Enumeration<Locale> getLocales(){
        return request.getLocales();
    }




  //**************************************************************************
  //** getPath
  //**************************************************************************
  /** Returns the requested path and querystring. This usually corresponds to
   *  the first line of the request header. Example:
   *  <pre>GET /index.html?abc=123 HTTP/1.1</pre>
   *  If the server is acting as a proxy, the first line may include a full
   *  url. In this case, use the getURL() method to retrieve the original path.
   */
    public String getPath(){
        return url.getPath() + (url.getQuery()==null ? "" : "?"+url.getQuery());
    }


  //**************************************************************************
  //** getMethod
  //**************************************************************************
  /** Returns the method specified in the first line of the request (e.g. GET,
   *  POST, PUT, HEAD, etc). Note that the method is always returned in
   *  uppercase.
   */
    public String getMethod(){
        return request.getMethod();
    }


  //**************************************************************************
  //** getHost
  //**************************************************************************
    public String getHost(){
        return url.getHost();
    }


  //**************************************************************************
  //** getPort
  //**************************************************************************
    public int getPort(){
        int port = url.getPort();
        if (port < 0 || port > 65535) port = 80;
        return port;
    }


  //**************************************************************************
  //** getServerName
  //**************************************************************************
  /** Returns the host name of the server to which the request was sent.
   *  It is the value of the part before ":" in the "Host" header, 
   *  header value, if any, or the resolved server name, or the server IP 
   *  address.
   */
    public String getServerName(){
        return getHost();
    }


  //**************************************************************************
  //** getServerPort
  //**************************************************************************
  /** Returns the port number to which the request was sent. It is the value
   *  of the part after ":" in the Host header value, if any, or the server 
   *  port where the client connection was accepted on. 
   */
    public int getServerPort(){
        //return request.getServerPort();
        return getPort();
    }
    
    
  /** Returns the host name of the Internet Protocol (IP) interface on which
   *  the request was received.
   */
    public String getLocalName(){
        return request.getLocalName();
    }

  /** Returns the Internet Protocol (IP) address of the interface on which the
   *  request was received.
   */
    public String getLocalAddr(){
        return request.getLocalAddr();
    }


  /** Returns the Internet Protocol (IP) port number of the interface on which 
   *  the request was received.
   */
    public int getLocalPort(){
        return request.getLocalPort();
    }
    
//    
//  //**************************************************************************
//  //** setHost
//  //**************************************************************************
//  /** Used to update the Host attribute defined in the header and in the url.
//   */
//    public void setHost(String host, int port) {
//        try{
//            url = new java.net.URL(updateURL(host,port,url));
//            host = getHost();
//            port = getPort();
//            setHeader("Host", (port==80 ? host : host+":"+port));
//        }
//        catch(Exception e){}
//    }


//  //**************************************************************************
//  //** setRefererHost
//  //**************************************************************************
//  /** Used to set/update the RefererHost attribute defined in the header. */
//
//    public void setRefererHost(String host, int port) {
//
//        String referer = getHeader("Referer");
//        if (referer!=null)
//        try{
//            java.net.URL url = new java.net.URL(referer);
//            setHeader("Referer", updateURL(host, port, url));
//        }
//        catch(Exception e){
//        }
//    }


//  //**************************************************************************
//  //** updateURL
//  //**************************************************************************
//  /**  Used to update the host and port in a URL.
//   */
//    private String updateURL(String host, Integer port, java.net.URL url) {
//
//        port = ((port==null || port < 0 || port > 65535) ? 80 : port);
//        host = (port==80 ? host : host+":"+port);
//        String str = url.toString();
//        String protocol = str.substring(0, str.indexOf(url.getHost()));
//        String path = (url.getPath()==null ? "" : url.getPath());
//        if (path.length()>0){
//            str = str.substring(protocol.length());
//            path = str.substring(str.indexOf(url.getPath()));
//        }
//        else{
//            if (url.getQuery()!=null)
//                path = str.substring(str.indexOf("?"+url.getQuery()));
//        }
//        return protocol + host + path;
//    }


  //**************************************************************************
  //** isKeepAlive
  //**************************************************************************
  /** Used to determine whether the Connection attribute is set to Keep-Alive.
   */
    public boolean isKeepAlive(){
        if (isKeepAlive==null){
            String connType = getHeader("Connection");
            isKeepAlive = (connType==null ? false : connType.toUpperCase().contains("KEEP-ALIVE"));
        }
        return isKeepAlive;
    }


//  //**************************************************************************
//  //** isEncrypted
//  //**************************************************************************
//  /** Used to determine whether the Connection is encrypted (e.g. SSL/TLS)
//   */
//    public boolean isEncrypted(){
//        return sslEngine!=null;
//    }


  //**************************************************************************
  //** isSecure
  //**************************************************************************    
  /** Returns a boolean indicating whether this request was made using a
   *  secure channel, such as HTTPS.
   */
    public boolean isSecure(){
        return request.isSecure();
    }


  //**************************************************************************
  //** getProtocol
  //**************************************************************************
  /** Returns the name and version of the protocol the request uses in the 
   *  form <i>protocol/majorVersion.minorVersion</i> (e.g. "HTTP/1.1").
   */
    public String getProtocol(){
        return request.getProtocol();
    }


  //**************************************************************************
  //** getScheme
  //**************************************************************************
  /** Returns the name of the scheme used to make this request (e.g. "http").
   */
    public String getScheme(){
        return request.getScheme();
        //return getURL().getProtocol().toLowerCase();
    }


  //**************************************************************************
  //** getURL
  //**************************************************************************
  /** Used to retrieve the requested url defined in the header. */

    public java.net.URL getURL(){
        return url;
    }


  //**************************************************************************
  //** getRequestURI 
  //**************************************************************************
  /** Returns the part of this request's URL from the protocol name up to the
   *  query string in the first line of the HTTP request. The web container 
   *  does not decode this String
   *  For example:

     * <table summary="Examples of Returned Values">
     * <tr align=left><th>First line of HTTP request      </th>
     * <th>     Returned Value</th>
     * <tr><td>POST /some/path.html HTTP/1.1<td><td>/some/path.html
     * <tr><td>GET http://foo.bar/a.html HTTP/1.0
     * <td><td>/a.html
     * <tr><td>HEAD /xyz?a=b HTTP/1.1<td><td>/xyz
     * </table>
     *
     * <p>To reconstruct an URL with a scheme and host, use
     * {@link HttpUtils#getRequestURL}.
     *
     * @return		a <code>String</code> containing
     *			the part of the URL from the
     *			protocol name up to the query string
     */
    public String getRequestURI(){
        return request.getRequestURI();
    }


  //**************************************************************************
  //** getRequestURL
  //**************************************************************************
  /** Reconstructs the URL the client used to make the request. The returned
   *  URL contains a protocol, server name, port number, and server path, but 
   *  it does not include query string parameters. 
   */
    public StringBuffer getRequestURL(){
        return request.getRequestURL();
    }


  //**************************************************************************
  //** getURL
  //**************************************************************************
  /** Returns the url query string. Returns null if one does not exist. */
    public String getQueryString(){
        return url.getQuery();
    }


  //**************************************************************************
  //** getParameter
  //**************************************************************************
  /** Used to retrieve the value of a specific variable supplied in the query
   *  string. Does NOT retrieve or parse posted data from form data. Use the
   *  getForm() method instead.
   *
   *  @param key Parameter name. Performs a case insensitive search for the
   *  keyword.
   *
   *  @return Returns a comma delimited list of values associated with the
   *  given key or a null value if the key is not found or if the value is
   *  an empty string.
   */
    public String getParameter(String key){
        return request.getParameter(key);
    }


  //**************************************************************************
  //** getParameterNames
  //**************************************************************************    
  /** Returns an Enumeration of String objects containing the names of the 
   *  parameters contained in the query string. If the request has no 
   *  parameters, the method returns an empty Enumeration. <p/>
   *  Note that this method does NOT retrieve or parse posted data from form 
   *  data. Use the getForm() method instead.
   */
    public java.util.Enumeration<String> getParameterNames(){
        return request.getParameterNames();
    }


  //**************************************************************************
  //** getParameterValues
  //**************************************************************************
  /** Returns an array containing all of the values for a given query string 
   *  parameter or null if the parameter does not exist.<p/>
   *  Note that this method does NOT retrieve or parse posted data from form 
   *  data. Use the getForm() method instead.
   */
    public String[] getParameterValues(String name){ 
        return request.getParameterValues(name);
    }


  //**************************************************************************
  //** getParameterMap
  //**************************************************************************
  /** Returns an immutable java.util.Map containing parameters found in the 
   *  query string. The keys in the parameter map are of type String. The 
   *  values in the parameter map are of type String array.<p/>
   *  Note that this method does NOT retrieve or parse posted data from form 
   *  data. Use the getForm() method instead.
   */
    public java.util.Map<String, String[]> getParameterMap(){
        return request.getParameterMap();
    }


  //**************************************************************************
  //** getRequestURL
  //**************************************************************************
  /** Returns a StringBuffer similar to one returned by an implementation of a
   *  HttpServletRequest class. The returned URL contains a protocol, server
   *  name, port number, and server path, but it does not include query string
   *  parameters.
   *
    public StringBuffer getRequestURL(){
        String url = this.url.toString();
        if (url.contains("?")) url = url.substring(0, url.indexOf("?"));
        return new StringBuffer().append(url);
    }
    */


  //**************************************************************************
  //** getContentLength
  //**************************************************************************
  /** Returns the "Content-Length" specified in the http request header.
   */
    public int getContentLength(){
        return request.getContentLength();
    }


  //**************************************************************************
  //** getBody
  //**************************************************************************
  /** Returns the body of the http request as a byte array. Reads all remaining
   *  bytes from the socket. Therefore, you should only call this method once.
   *  Subsequent calls will return an empty array.
   */
    public byte[] getBody() throws IOException {

      //Only POST should have a body. Otherwise, return an empty array.
        if (!this.getMethod().equals("POST")) return new byte[0];


      //If the client specified a Content-Length of 0, simply return an empty
      //array. Otherwise, the server will pause for 15 seconds trying to read
      //data from the socket.
        int contentLength = getContentLength();
        if (contentLength<1) return new byte[0];
        

        final java.io.ByteArrayOutputStream bas = new java.io.ByteArrayOutputStream();
        final HttpInput input = (HttpInput) request.getInputStream();
        int numBytesRead = -1; 
        byte buffer[] = new byte[8192]; 
        while (input.isReady() && (numBytesRead = input.read(buffer)) != -1) {
            ByteBuffer buf = ByteBuffer.wrap(buffer, 0, numBytesRead); 
            byte[] b = new byte[numBytesRead];
            buf.get(b, 0, numBytesRead);
            bas.write(b);
            //if (bas.size()==contentLength) break;
        }
     
        //bas.close();
        return bas.toByteArray();
    }

    private class StreamReader implements Runnable {
        
        private java.io.ByteArrayOutputStream bas;
        
        public StreamReader(java.io.ByteArrayOutputStream bas){
            this.bas = bas;
        }
        
        public void run(){

            while (true){
            try{
            
                final HttpInput input = (HttpInput) request.getInputStream();
                final javax.servlet.AsyncContext async = request.startAsync();
                input.setReadListener(new javax.servlet.ReadListener() {



                    @Override
                    public void onDataAvailable() throws IOException {
                        int numBytesRead = -1; 
                        byte buffer[] = new byte[8192]; 
                        while (ready() && (numBytesRead = input.read(buffer)) != -1) {
                            ByteBuffer buf = ByteBuffer.wrap(buffer, 0, numBytesRead); 

                            if (numBytesRead>0){
                                byte[] b = new byte[numBytesRead];
                                buf.get(b, 0, numBytesRead);
                                bas.write(b);
                                //if (contentLength<0 && numBytesRead!=bufferSize) break; //<-- Is this a valid use case? Can Content-Length be undefined?
                                //if (bas.size()==contentLength) break;
                            }
                            else{
                                break;
                            }                 

                        }

                        end();
                    }

                    @Override
                    public void onAllDataRead() throws IOException {
                        end(); 
                    }

                    @Override
                    public void onError(Throwable t) {
                        request.getServletContext().log("Async Error",t);               
                    }


                    void end() { 
                        System.out.println("Done!");

                        async.complete();
                        try{
                            bas.close();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }

                    } 

                    boolean ready() { 
                        return input.isReady(); 
                    } 

                });
                return;
        
            }
            catch(Exception e){
                return;
            }
            
            }
        }
    }
    

  //**************************************************************************
  //** getInputStream
  //**************************************************************************
  /** Returns the body of the http request as an input stream. Automatically 
   *  decrypts the body if the data is SSL/TLS encrypted. Example:
   <pre>
        java.io.InputStream inputStream = request.getInputStream();
        byte[] b = new byte[1024];
        int x=0;
        while ( (x = inputStream.read(b)) != -1) {
            //Do something! Example: outputStream.write(b,0,x);
        }
        inputStream.close();
   </pre>
   */
    public ServletInputStream getInputStream() throws java.io.IOException {
        return new ServletInputStream(request.getInputStream());
    }


  //**************************************************************************
  //** getReader
  //**************************************************************************
  /** Returns a BufferedReader used to process the body of the http request. 
   *  Automatically decrypts the body if the data is SSL/TLS encrypted.
   *  Either this method or getInputStream() may be called to read the body,
   *  but not both.
   */
    public java.io.BufferedReader getReader() throws IOException{
        return request.getReader();
    }


  //**************************************************************************
  //** getFormInputs
  //**************************************************************************
  /** Returns form elements in the body of the http request as an iterator.
   *  Reads data from the client on-demand, meaning form data will only be
   *  retrieved from the client when calling Iterator.next(). This is
   *  potentially more memory efficient than calling getBody() and parsing
   *  the entire byte array. This is especially true when processing
   *  "multipart/form-data" with large amounts of binary data (e.g. uploaded
   *  files). Please see the FormInput.getInputStream() or FormInput.toFile()
   *  methods for more information on handling large binary streams.
   *  <p/>
   *
   *  Here's a simple example of how to iterate through form data using the
   *  getFormInputs() method. Note how easy it is to identify an uploaded 
   *  file and save it to disk.
   <pre>
        java.util.Iterator&lt;FormInput&gt; it = request.getFormInputs();
        while (it.hasNext()){
            FormInput input = it.next();
            String name = input.getName();
            FormValue value = input.getValue();

            if (input.isFile()){
                value.toFile(new java.io.File("/temp/" + input.getFileName()));
                System.out.println(name + ": &lt;FILE&gt;");
            }
            else{
                System.out.println(name + ": " + value);
            }
        }
   </pre>
   *  Note that the form iterator reads data directly from the socket 
   *  connection. Therefore, you should only call this method once.
   *  <p/>
   *
   *  More information on HTML form data can be found here:<br/>
   *  http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4
   */
    public FormIterator getFormInputs() throws IOException {

        if (!this.getMethod().equals("POST"))
            throw new IOException("Unsupported method: " + this.getMethod());

        String contentType = getHeader("Content-Type");
        if (contentType==null) throw new IOException("Content-Type is undefined.");


        String boundary = null;
        if (contentType.contains("application/x-www-form-urlencoded")){
            boundary = "&";
        }
        else if (contentType.contains("multipart/form-data")){
            for (String s : contentType.split(";")){
                s = s.toLowerCase().trim();
                if (s.toLowerCase().trim().startsWith("boundary=")){
                    boundary = s.trim().substring("boundary=".length());
                    break;
                }
            }
        }
        else{
            throw new IOException("Unsupported Content-Type: " + contentType);
        }

        return new FormIterator(getInputStream(), boundary);
    }


  //**************************************************************************
  //** FormIterator Class
  //**************************************************************************
  /** Simple implementation of a java.util.Iterator used to parse form data.
   */
    private class FormIterator implements java.util.Iterator {

        private FormInput currInput = null;
        private FormInput prevInput = null;
        private ServletInputStream is;
        private String boundary;


        private FormIterator(ServletInputStream is, String boundary){
            this.is = is;
            this.boundary = boundary;
        }

        public boolean hasNext(){
            if (currInput==null) getNextInput();
            return currInput!=null;
        }

        public FormInput next(){
            if (currInput==null) getNextInput();
            FormInput input = currInput;
            prevInput = currInput;
            currInput = null;
            return input;

        }

        private void getNextInput(){
            try{
                FormInput input = new FormInput(is, prevInput, boundary);
                if (currInput!=null) prevInput = currInput;
                currInput = input;
            }
            catch(Exception e){
                currInput = null;
            }
        }

        public void remove(){
        }
    }


  //**************************************************************************
  //** getSession
  //**************************************************************************
  /** Returns the current session associated with this request, or if the
   *  request does not have a session, creates one.
   */
    public HttpSession getSession(){
        return getSession(true);
    }


  //**************************************************************************
  //** getSession
  //**************************************************************************
  /** Returns the current HttpSession associated with this request or, if
   *  there is no current session and create is true, returns a new session.
   */
    public HttpSession getSession(boolean create){
        return new HttpSession(request.getSession(create), servletContext);
    }


  //**************************************************************************
  //** getRequestedSessionId
  //**************************************************************************
  /** Returns the session ID specified by the client ("JSESSIONID" cookie).
   *  If the client did not specify a session ID, this method returns null.
   *  Use the isRequestedSessionIdValid() method to verify whether the 
   *  session ID is valid.
   */
    public String getRequestedSessionId(){
        return request.getRequestedSessionId();
//        Cookie[] cookies = getCookies();
//        if (cookies!=null){
//            for (Cookie cookie : cookies){
//                if (cookie.getName().equalsIgnoreCase("JSESSIONID")){
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
    }


  //**************************************************************************
  //** isRequestedSessionIdValid
  //**************************************************************************
  /** Checks whether the requested session ID is still valid. Returns true if 
   *  this request has an id for a valid session in the current session 
   *  context. 
   */
    public boolean isRequestedSessionIdValid(){
        HttpSession session = getSession(false);
        return session!=null;    
    }


  //**************************************************************************
  //** isRequestedSessionIdFromCookie
  //**************************************************************************
  /** Checks whether the requested session ID came in as a cookie. */

    public boolean isRequestedSessionIdFromCookie(){
        return true; //This server manages sessions via cookies...
    }


  //**************************************************************************
  //** isRequestedSessionIdFromURL
  //**************************************************************************
  /** Checks whether the requested session ID came in as part of the request 
   *  URL.
   */
    public boolean isRequestedSessionIdFromURL(){
        return false; //This server manages sessions via cookies...
    }


  //**************************************************************************
  //** isRequestedSessionIdFromUrl
  //**************************************************************************
  /** @deprecated As of Version 2.1 of the Java Servlet API. 
   *  Use isRequestedSessionIdFromURL() instead.
   */
    public boolean isRequestedSessionIdFromUrl(){
        return isRequestedSessionIdFromURL();
    }


  //**************************************************************************
  //** getCookies
  //**************************************************************************
  /** Returns an array containing all of the Cookie objects the client sent 
   *  with this request. This method returns null if no cookies were sent.
   */
    public Cookie[] getCookies(){
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        Cookie[] arr = new Cookie[cookies.length];
        for (int i=0; i<arr.length; i++){
            arr[i] = new Cookie(cookies[i]);
        }
        return arr;
    }


  //**************************************************************************
  //** setAttribute
  //**************************************************************************
  /** Returns the value of a given attribute. Returns null if no attribute of 
   *  the given name exists. <p/>
   *  Attributes contain custom information about a request. Attributes are 
   *  set programatically using the setAttribute() method and are typically 
   *  used in conjunction with a RequestDispatcher. Attribute names should 
   *  follow the same conventions as package names. The servlet specification 
   *  reserves names matching "java.*", "javax.*", and "sun.*".
   */
    public Object getAttribute(String name){
        return request.getAttribute(name);
    }


  //**************************************************************************
  //** setAttribute
  //**************************************************************************
  /** Used to add, update, or delete an attribute associated with this request. 
   *  Attributes contain custom information about a request and are typically 
   *  used in conjunction with a RequestDispatcher. If the object passed in is 
   *  null, the effect is the same as calling removeAttribute().
   */
    public void setAttribute(String name, Object o){
        request.setAttribute(name, o);
    }


  //**************************************************************************
  //** removeAttribute
  //**************************************************************************
  /** Removes an attribute associated with this request. See getAttribute()   
   *  and setAttribute() for more information.
   */
    public void removeAttribute(String name){
        request.removeAttribute(name);
    }


  //**************************************************************************
  //** getAttributeNames
  //**************************************************************************
  /** Returns an Enumeration containing the names of the attributes associated 
   *  with this request. Returns an empty Enumeration if the request has no 
   *  attributes associated with it. See getAttribute() and setAttribute() for  
   *  more information.
   */
    public java.util.Enumeration<String> getAttributeNames(){
        return request.getAttributeNames();
    }


  //**************************************************************************
  //** getRequestDispatcher
  //**************************************************************************
  /** This method is supposed to return a RequestDispatcher object that can be 
   *  used to forward a request to the resource or to include the resource in 
   *  a response. This server does not currently support RequestDispatcher so
   *  this method returns a null. 
   */
    public Object getRequestDispatcher(String path){
        return null; //RequestDispatcher
    }


  //**************************************************************************
  //** getRequestDispatcher
  //**************************************************************************
  /** @deprecated As of Version 2.1 of the Java Servlet API. Use 
   *  ServletContext.getRealPath() instead.
   */
    public String getRealPath(String path){
        return request.getRealPath(path);
    }


  //**************************************************************************
  //** getPathInfo
  //**************************************************************************
  /** Returns any extra path information associated with the URL the client 
   *  sent when it made this request. The extra path information follows the 
   *  servlet path but precedes the query string and will start with a "/"
   *  character. Consider this example:
   *  <pre>http://localhost:8080/MyServlet/Extra/Path/?abc=123</pre>
   *  In this example, "/MyServlet" is the servlet path and this method will
   *  return "/Extra/Path/" as the extra path. If no extra path is found, this
   *  method will return a null. 
   */
    public String getPathInfo(){
        return request.getPathInfo();
    }


  //**************************************************************************
  //** getPathTranslated
  //**************************************************************************
  /** Returns any extra path information after the servlet name but before the
   *  query string, and translates it to a real path. If the URL does not have 
   *  any extra path information, or if  the servlet container cannot  
   *  translate the virtual path to a real path for any reason, this method 
   *  returns a null.
   */
    public String getPathTranslated(){
        return request.getPathTranslated();
    }


  //**************************************************************************
  //** getContextPath
  //**************************************************************************
  /** Returns a string in the requested URL that represents the servlet 
   *  context. This is typically defined in the META-INF/context.xml file in 
   *  Java EE web applications. For example, if a web application is called 
   *  "WebApplication", the context path might be "/WebApplication". In this
   *  case, a requested URL will include the context path like this:
   *  <pre>http://localhost:8080/WebApplication/MyServlet/?abc=123</pre><p/>
   *  
   *  The context path always comes first in a request URL. The path starts 
   *  with a "/" character but does not end with a "/" character. For servlets 
   *  in the default (root) context, this method returns "". <p/>
   *  
   *  Note that this server does not currently support the container concept
   *  where multiple servlets are managed by a servlet container. Instead, we
   *  have a single servlet that processes all web requests and can dispatch
   *  the requests to other servlets. Therefore, to retrieve a "context path"
   *  developers must explicitely set the "context path" in the servlet and 
   *  implement logic to generate/process the URLs accordingly.
   */
    public String getContextPath(){
        return request.getContextPath();
    }


  //**************************************************************************
  //** getServletPath
  //**************************************************************************
  /** Returns a string in the requested URL that represents the servlet path.
   *  This path starts with a "/" character and includes either the servlet 
   *  name or a path to the servlet, but does not include any extra path
   *  information or a query string. For example, consider the following URL:
   *  <pre>http://localhost:8080/WebApplication/MyServlet/?abc=123</pre><p/>
   *  In this example, the context path is "/WebApplication" and "/MyServlet"
   *  is the servlet path. <p/>
   * 
   *  Note that this server does not require a URL "Pattern" to be defined for
   *  for individual servlets. Instead, we have a single servlet that processes 
   *  all web requests and can dispatch the requests to other servlets. 
   *  Therefore, to retrieve a "servlet path" developers must explicitely set 
   *  the servlet path in the servlet and implement logic to process the  
   *  URLs accordingly.
   */
    public String getServletPath(){
        return request.getServletPath();
    }


  //**************************************************************************
  //** getServletContext
  //**************************************************************************
    protected ServletContext getServletContext(){
        return this.servletContext;
    }


  //**************************************************************************
  //** getAuthType
  //**************************************************************************    
  /** Returns the authentication scheme used to authenticate clients (e.g. 
   *  "BASIC", "DIGEST", "CLIENT_CERT", etc). This value is retrieved from an
   *  Authenticator and does not necessarily correspond to the "Authorization"
   *  request header. If an Authenticator is not used to secure the servlet,
   *  a null is returned.
   */
    public String getAuthType(){
        //return request.getAuthType();
        if (authenticator!=null) return authenticator.getAuthType();
        else return null;
    }

    
  //**************************************************************************
  //** getCredentials
  //**************************************************************************
  /** Returns an array representing the client credentials associated with
   *  this request. The first element in the array represents the username and
   *  the second element represents the password. <p/>
   *  Credentials are retrieved from an Authenticator. If no Authenticator is
   *  defined or if the Authenticator fails to parse the credentials, this 
   *  method returns a null.
   */    
    public String[] getCredentials(){
        if (getCredentials){
            try {
                //System.out.println(authenticator);
                credentials = authenticator.getCredentials();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            getCredentials = false;
        }
        return credentials;
    }


  //**************************************************************************
  //** authenticate
  //**************************************************************************
  /** Used to authenticate a client request. Authentication is performed by an
   *  Authenticator. If no Authenticator is defined or if the Authenticator 
   *  fails to authenticate the client, this method throws a ServletException.
   */
    public void authenticate() throws ServletException {
        if (authenticate==true){
            try{            
                authenticator.authenticate();
            }
            catch(ServletException e){
                authenticationException = e;
            }
            catch(Exception e){
                authenticationException = new ServletException(e.getLocalizedMessage());
                authenticationException.setStackTrace(e.getStackTrace());
            }
            authenticate = false;
        }
        
        if (authenticationException!=null) throw authenticationException;
    }
    

  //**************************************************************************
  //** getRemoteUser
  //**************************************************************************
  /** Returns the login of the user making this request, if the user has been
   *  authenticated, or null if the user has not been authenticated.
   */
    public String getRemoteUser(){
        try{
            String[] credentials = getCredentials();
            authenticate();
            return credentials[0];
        }
        catch(Exception e){
        }
        return null;
    }


  //**************************************************************************
  //** isUserInRole
  //**************************************************************************
  /** Returns a boolean indicating whether the authenticated user is included
   *  in the specified "role". Roles and role membership are often managed by 
   *  an Authenticator. If no Authenticator is defined, or if the user is not
   *  authenticated, or if no role is defined for the user, the method returns
   *  false.
   */
    public boolean isUserInRole(String role){
        try{
            return authenticator.isUserInRole(role);
        }
        catch(Exception e){
        }
        return false;
    }


  //**************************************************************************
  //** getUserPrincipal
  //**************************************************************************
  /** Returns a java.security.Principal object containing the name of the 
   *  current authenticated user. User Principals are resolved by an 
   *  Authenticator. If no Authenticator is defined, or if the user has not
   *  been authenticated, the method returns a null.
   */
    public java.security.Principal getUserPrincipal(){
        if (getUserPrincipal){
            try{
                principal = authenticator.getPrinciple();
            }
            catch(Exception e){
            }
            getUserPrincipal = false;
        }
        return principal;
    }

    
    public AsyncContext startAsync() throws IllegalStateException{
        return request.startAsync();
    }

    public AsyncContext startAsync(ServletRequest sr, ServletResponse sr1) throws IllegalStateException{
        return request.startAsync(sr, sr1);
    }

    public boolean isAsyncStarted(){
        return request.isAsyncStarted();
    }

    public boolean isAsyncSupported(){
        return request.isAsyncSupported();
    }

    public AsyncContext getAsyncContext(){
        return request.getAsyncContext();
    }

    public DispatcherType getDispatcherType(){
        return request.getDispatcherType();
    }
    
    

  //**************************************************************************
  //** toString
  //**************************************************************************
  /** Returns the full HTTP Request Header.
   */
    public String toString(){
        
        StringBuffer out = new StringBuffer();
        
        //GET /pub/WWW/TheProject.html HTTP/1.1
        out.append(getMethod());
        out.append(" ");
        out.append(getPath());
        out.append(" ");
        out.append(getProtocol());
        out.append("\r\n");
        
        
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            java.util.Enumeration<String> headerValues = request.getHeaders(name);
            while (headerValues.hasMoreElements()) {
                String value = headerValues.nextElement();
                out.append(name);
                out.append(": ");
                out.append(value);
                out.append("\r\n");
            }
        }

        out.append("\r\n");
        return out.toString();
    }

}