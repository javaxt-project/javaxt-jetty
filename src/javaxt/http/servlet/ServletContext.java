package javaxt.http.servlet;

//******************************************************************************
//**  ServletContext
//******************************************************************************
/**
 *   Provides a mechanism to store application data across servlets. The
 *   ServletContext is initialized when the web server is initialized. There
 *   is only one context per Java Virtual Machine.
 *   <p>
 *   This class is a partial implementation of the javax.servlet.ServletContext
 *   interface defined in Version 2.5 of the Java Servlet API.
 *   </p>
 *
 ******************************************************************************/

public class ServletContext {

    private String serverInfo;
    private java.io.File jarFile;
    private javax.servlet.ServletContext servletContext;

    public final String PathSeparator = System.getProperty("file.separator");
    private String contextPath = "";


  //**************************************************************************
  //** Constructor
  //**************************************************************************
    public ServletContext(javax.servlet.ServletContext servletContext) {
        this.servletContext = servletContext;
    }


  //**************************************************************************
  //** getMajorVersion
  //**************************************************************************
  /** Returns the major version of the Java Servlet API that this
   *  servlet container supports.
   */
    public int getMajorVersion(){
        return servletContext.getMajorVersion();
    }


  //**************************************************************************
  //** getMinorVersion
  //**************************************************************************
  /** Returns the minor version of the Servlet API that this servlet container
   *  supports.
   */
    public int getMinorVersion(){
        return servletContext.getMinorVersion();
    }


  //**************************************************************************
  //** getAttribute
  //**************************************************************************
  /** Returns the servlet container attribute with the given name, or null if
   *  there is no attribute by that name.
   */
    public Object getAttribute(String name){
        return servletContext.getAttribute(name);
    }


  //**************************************************************************
  //** setAttribute
  //**************************************************************************
  /** Binds an object to a given attribute name in this servlet context. If
   *  the name specified is already used for an attribute, this method will
   *  replace the attribute with the new to the new attribute. If a null value
   *  is passed, the effect is the same as calling removeAttribute(). If
   *  listeners are configured on the ServletContext the container notifies
   *  them accordingly.
   */
    public void setAttribute(String name, Object value){
        servletContext.setAttribute(name, value);
    }


  //**************************************************************************
  //** removeAttribute
  //**************************************************************************
  /** Removes the attribute with the given name from the servlet context. If
   *  listeners are configured on the ServletContext the container notifies
   *  them accordingly.
   */
    public void removeAttribute(String name){
        servletContext.removeAttribute(name);
    }


  //**************************************************************************
  //** getAttributeNames
  //**************************************************************************
  /** Returns an Enumeration containing the attribute names available within
   *  this servlet context. Use the getAttribute() method with an attribute name
   *  to get the value of an attribute.
   */
    public java.util.Enumeration<String> getAttributeNames(){
        return servletContext.getAttributeNames();
    }


  //**************************************************************************
  //** getContext
  //**************************************************************************
  /** Returns a ServletContext object that corresponds to a specified URL on
   *  the server, or null if either none exists or the container wishes to
   *  restrict this access.
   *
     * <p>This method allows servlets to gain
     * access to the context for various parts of the server, and as
     * needed obtain {@link RequestDispatcher} objects from the context.
     * The given path must be begin with "/", is interpreted relative
     * to the server's document root and is matched against the context roots of
     * other web applications hosted on this container.
   */
    public javax.servlet.ServletContext getContext(String uripath){
        return servletContext.getContext(uripath);
    }


  //**************************************************************************
  //** getContextPath
  //**************************************************************************
  /** */
    public String getContextPath(){
        return contextPath;
    }

    public void setContextPath(String contextPath){
        this.contextPath = contextPath;
    }



  //**************************************************************************
  //** getServletContextName
  //**************************************************************************
  /** Returns the name of the web application or null if no name has been
   *  declared in the deployment descriptor.
   */
    public String getServletContextName(){
        return servletContext.getServletContextName();
    }

//    public void setServletContextName(String contextName){
//        this.contextName = contextName;
//    }


  //**************************************************************************
  //** getMimeType
  //**************************************************************************
  /** Returns the MIME type of the specified file, or <code>null</code> if
   *  the MIME type is not known. The MIME type is determined by the
   *  configuration of the servlet container, and may be specified
   *  in a web application deployment descriptor. Common MIME
   *  types are <code>"text/html"</code> and <code>"image/gif"</code>.
   */
    public String getMimeType(String file){
        return servletContext.getMimeType(file);
    }


  //**************************************************************************
  //** getResourcePaths
  //**************************************************************************
  /** Returns a directory-like listing of all the paths to resources within
   *  the web application whose longest sub-path matches the supplied path
   *  argument. Paths indicating subdirectory paths end with a '/'. The
   *  returned paths are all relative to the root of the web application and
   *  have a leading '/'. Consider, for example, a web application containing:
    <pre>
        /welcome.html
        /catalog/index.html
        /catalog/products.html
        /catalog/offers/books.html
        /catalog/offers/music.html
        /customer/login.jsp
        /WEB-INF/web.xml
        /WEB-INF/classes/com.acme.OrderServlet.class
    </pre>
   *
   *  context.getResourcePaths("/") would return "/welcome.html", "/catalog/",
   * "/customer/", "/WEB-INF/"<br/>
   *  context.getResourcePaths("/catalog/") would return "/catalog/index.html",
   * "/catalog/products.html", "/catalog/offers/".
   *
   *  @param path The partial path used to match the resources, which must
   *  start with a "/".
   */
    public java.util.Set<String> getResourcePaths(String path){
        return servletContext.getResourcePaths(path);
    }


  //**************************************************************************
  //** getResource
  //**************************************************************************
  /** Returns a URL to the resource that is mapped to a specified
     * path. The path must begin with a "/" and is interpreted
     * as relative to the current context root.
     *
     * <p>This method allows the servlet container to make a resource
     * available to servlets from any source. Resources
     * can be located on a local or remote
     * file system, in a database, or in a <code>.war</code> file.
     *
     * <p>The servlet container must implement the URL handlers
     * and <code>URLConnection</code> objects that are necessary
     * to access the resource.
     *
     * <p>This method returns <code>null</code>
     * if no resource is mapped to the pathname.
     *
     * <p>Some containers may allow writing to the URL returned by
     * this method using the methods of the URL class.
     *
     * <p>The resource content is returned directly, so be aware that
     * requesting a <code>.jsp</code> page returns the JSP source code.
     * Use a <code>RequestDispatcher</code> instead to include results of
     * an execution.
     *
     * <p>This method has a different purpose than
     * <code>java.lang.Class.getResource</code>,
     * which looks up resources based on a class loader. This
     * method does not use class loaders.
     *
     * @param path 				a <code>String</code> specifying
     *						the path to the resource
     *
     * @return 					the resource located at the named path,
     * 						or <code>null</code> if there is no resource
     *						at that path
     *
     * @exception MalformedURLException 	if the pathname is not given in
     * 						the correct form
   */
    public java.net.URL getResource(String path) throws java.net.MalformedURLException {
        return servletContext.getResource(path);
    }


  //**************************************************************************
  //** getResourceAsStream
  //**************************************************************************
  /** Returns the resource located at the named path as an InputStream.
     *
     * <p>The data in the <code>InputStream</code> can be
     * of any type or length. The path must be specified according
     * to the rules given in <code>getResource</code>.
     * This method returns <code>null</code> if no resource exists at
     * the specified path.
     *
     * <p>Meta-information such as content length and content type
     * that is available via <code>getResource</code>
     * method is lost when using this method.
     *
     * <p>The servlet container must implement the URL handlers
     * and <code>URLConnection</code> objects necessary to access
     * the resource.
     *
     * <p>This method is different from
     * <code>java.lang.Class.getResourceAsStream</code>,
     * which uses a class loader. This method allows servlet containers
     * to make a resource available
     * to a servlet from any location, without using a class loader.
   */
    public java.io.InputStream getResourceAsStream(String path){
        return servletContext.getResourceAsStream(path);
    }


  //**************************************************************************
  //** getRequestDispatcher
  //**************************************************************************
  /** Returns a RequestDispatcher that acts as a wrapper for a resource
   *  located at the given path. A RequestDispatcher can be used to forward a
   *  request to the resource or to include the resource in a response. The
   *  resource can be dynamic or static.
   *  <p>
   *  The pathname must begin with a "/" and is interpreted as relative
   *  to the current context root.  Use <code>getContext</code> to obtain
   *  a <code>RequestDispatcher</code> for resources in foreign contexts.
   *  This method returns <code>null</code> if the <code>ServletContext</code>
   *  cannot return a <code>RequestDispatcher</code>.
   *  </p>
   */
    public Object getRequestDispatcher(String path){
        return servletContext.getRequestDispatcher(path);
    }


  //**************************************************************************
  //** getNamedDispatcher
  //**************************************************************************
  /** Returns a RequestDispatcher that acts as a wrapper for the named servlet.
   *  Returns a null if the ServletContext cannot return a RequestDispatcher
   *  for any reason.
   *
   *  Servlets are given names programatically or via a web application
   *  deployment descriptor. A servlet instance can determine its name using
   *  ServletConfig.getServletName().
   *
   *  @param name A String specifying the name of a servlet to wrap.
   */
    public Object getNamedDispatcher(String name){
        return servletContext.getNamedDispatcher(name);
    }


  //**************************************************************************
  //** getServlet
  //**************************************************************************
  /** @deprecated As of Java Servlet API 2.1, with no direct replacement. This
   *  method will be permanently removed in a future version of the Java
   *  Servlet API.
   *  @return null
   */
    @Deprecated
    public Object getServlet(String name) throws ServletException{
        return null; //return Servlet
    }


  //**************************************************************************
  //** getServlets
  //**************************************************************************
  /** @deprecated As of Java Servlet API 2.0, with no replacement. This
   *  method will be permanently removed in a future version of the Java
   *  Servlet API.
   *  @return null
   */
    @Deprecated
    public java.util.Enumeration getServlets(){
        return null;
    }


  //**************************************************************************
  //** getServletNames
  //**************************************************************************
  /** @deprecated As of Java Servlet API 2.1, with no direct replacement. This
   *  method will be permanently removed in a future version of the Java
   *  Servlet API.
   *  @return null
   */
    @Deprecated
    public java.util.Enumeration getServletNames(){
        return null;
    }


  //**************************************************************************
  //** log
  //**************************************************************************
  /** Writes the specified message to a servlet log file.
   */
    public void log(String msg){
        //TODO: Implement logger
    }


  //**************************************************************************
  //** log
  //**************************************************************************
  /** Writes an exception's stack trace and an explanatory error message to
   *  the servlet log file.
   *  @deprecated As of Java Servlet API 2.1, use log(message, throwable)
   *  instead.
   */
    @Deprecated
    public void log(Exception exception, String msg){
        //TODO: Implement logger
    }


  //**************************************************************************
  //** log
  //**************************************************************************
  /** Writes an explanatory message and a stack trace for a given Throwable
   *  exception to the servlet log file.
   */
    public void log(String message, Throwable throwable){
        //TODO: Implement logger
    }


  //**************************************************************************
  //** getRealPath
  //**************************************************************************
  /** Returns a String containing the real path for a given virtual path. For
   *  example, the path "/index.html" found in:
   *  <pre>http://localhost:8080/WebApplication/index.html</pre>
   *  might represent a physical file found in:
   *  <pre>D:\WebApps\WebApplication\index.html</pre>
   *
   *  The real path returned will be in a form appropriate to the computer and
   *  operating system on which the servlet container is running, including
   *  the proper path separators. This method returns null if the servlet
   *  container cannot translate the virtual path to a real path for any
   *  reason.
   *
   *  @param path A String specifying a virtual path (e.g. "/index.html").
   */
    public String getRealPath(String path){


      //Update currDir
        String currDir = contextPath;
        currDir = currDir.replace("\\","/");
        if (!currDir.endsWith("/")){
            currDir+="/";
        }


        path = path.replace("\\","/");


        if (path.startsWith("/")){
            return (currDir + path.substring(1)).replace("/", PathSeparator);
        }


        String[] arrRelPath = path.split("/");
        String[] arrAbsPath = currDir.split("/");



        int x = -1;
        path = "";
        String dir = "";
        for (int i=0; i<arrRelPath.length; i++) {
            dir = arrRelPath[i];
            if (dir.equals("..")){
                x = x + 1;
            }
            else if (dir.equals(".")){
                //do nothing?
            }
            else{
                path = path + "\\" + arrRelPath[i];
            }
        }


        //x = x + 1 'because currDir has a "\" at the end of it
        dir = "";
        for (int i=0; i<arrAbsPath.length-(x+1); i++){ //because currDir has a "\" at the end of it
            dir = dir + arrAbsPath[i] + "\\";
        }


        //trim off last "\"
        dir = dir.substring(0, dir.length()-1);

        //replace any leftover "/" characters
        dir = dir + path.replace("/", "\\");


        dir = dir.replace("\\", PathSeparator);

        return dir;
    }


  //**************************************************************************
  //** getServerInfo
  //**************************************************************************
  /** Returns the name and version of the servlet container on which
   *  the servlet is running. The form of the returned string is
   *  <i>servername</i>/<i>versionnumber</i>. Example:
   *  <pre>JavaServer Web Dev Kit/1.0</pre>
   *  The servlet container may return other optional information after the
   *  primary string, in parentheses. Example:
   *  <pre>JavaXT Web Server/3.0</pre>
   */
    public String getServerInfo(){
        if (serverInfo!=null) return serverInfo;
        if (jarFile==null) jarFile = this.getJarFile();

      //Parse the jar file and try to find the server version number
        try{
            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
            java.util.jar.Manifest manifest = jar.getManifest();

            String versionNumber = null;
            java.util.jar.Attributes attributes = manifest.getMainAttributes();
            if (attributes!=null){
                java.util.Iterator it = attributes.keySet().iterator();
                while (it.hasNext()){
                    java.util.jar.Attributes.Name key = (java.util.jar.Attributes.Name) it.next();
                    String keyword = key.toString();
                    if (keyword.equals("Implementation-Version") || keyword.equals("Bundle-Version")){
                        versionNumber = (String) attributes.get(key);
                        break;
                    }
                }
            }
            jar.close();
            serverInfo = "JavaXT Web Server" + (versionNumber!=null?"/"+versionNumber:"");
            return serverInfo;
        }
        catch(Exception e){
        }
        return "JavaXT Web Server";
    }


  //**************************************************************************
  //** getInitParameter
  //**************************************************************************
  /** Returns the value of the named context-wide initialization parameter,
   *  or null if the parameter does not exist.
   *
   *  Initialization parameters are used to store configuration information
   *  for an entire "web application".  For example, it can provide a
   *  webmaster's email address or the name of a system that holds critical
   *  data.
   */
    public String getInitParameter(String name){
        return servletContext.getInitParameter(name);
    }


  //**************************************************************************
  //** getInitParameterNames
  //**************************************************************************
  /** Returns the names of the context's initialization parameters as an
   *  Enumeration of String objects, or an empty Enumeration if the context
   *  has no initialization parameters.
   */
    public java.util.Enumeration<String> getInitParameterNames(){
        return servletContext.getInitParameterNames();
    }


  //**************************************************************************
  //** getJarFile
  //**************************************************************************
  /** Returns the jar file associated with the javaxt-server library.
   */
    private java.io.File getJarFile(){

        java.lang.Package Package = this.getClass().getPackage();
        String path = Package.getName().replace((CharSequence)".",(CharSequence)"/");
        String url = this.getClass().getClassLoader().getResource(path).toString();
        url = url.replace((CharSequence)" ",(CharSequence)"%20");
        try{
            java.net.URI uri = new java.net.URI(url);
            if (uri.getPath()==null){
                path = uri.toString();
                if (path.startsWith("jar:file:")){

                  //Update Path and Define Zipped File
                    path = path.substring(path.indexOf("file:/"));
                    path = path.substring(0,path.toLowerCase().indexOf(".jar")+4);

                    if (path.startsWith("file://")){ //UNC Path
                        path = "C:/" + path.substring(path.indexOf("file:/")+7);
                        path = "/" + new java.net.URI(path).getPath();
                    }
                    else{
                        path = new java.net.URI(path).getPath();
                    }
                    return new java.io.File(path);
                }
            }
            else{
                return new java.io.File(uri);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}