package javaxt.http.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

//******************************************************************************
//**  ServletConverter
//******************************************************************************
/**
 *   Provides static methods used to convert classes to/from the "javax"
 *   namespace and the "jakarta" namespace. Note that some methods in the
 *   converted classes may throw an UnsupportedOperationException.
 *
 ******************************************************************************/

public class ServletConverter {


  //**************************************************************************
  //** getHttpServletRequest
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" HttpServletRequest.
   */
    public static javax.servlet.http.HttpServletRequest getHttpServletRequest(
        jakarta.servlet.http.HttpServletRequest request){

        return new javax.servlet.http.HttpServletRequest(){

            @Override
            public String getAuthType(){
                return request.getAuthType();
            }

            @Override
            public javax.servlet.http.Cookie[] getCookies(){
                jakarta.servlet.http.Cookie[] arr = request.getCookies();
                javax.servlet.http.Cookie[] cookies = new javax.servlet.http.Cookie[arr.length];
                for (int i=0; i<arr.length; i++){
                    cookies[i] = getCookie(arr[i]);
                }
                return cookies;
            }

            @Override
            public long getDateHeader(String name){
                return request.getDateHeader(name);
            }

            @Override
            public String getHeader(String name){
                return request.getHeader(name);
            }

            @Override
            public Enumeration<String> getHeaders(String name){
                return request.getHeaders(name);
            }

            @Override
            public Enumeration<String> getHeaderNames(){
                return request.getHeaderNames();
            }

            @Override
            public int getIntHeader(String name){
                return request.getIntHeader(name);
            }

            @Override
            public javax.servlet.http.HttpServletMapping getHttpServletMapping() {
                return ServletConverter.getHttpServletMapping(request.getHttpServletMapping());
            }

            @Override
            public String getMethod(){
                return request.getMethod();
            }

            @Override
            public String getPathInfo(){
                return request.getPathInfo();
            }

            @Override
            public String getPathTranslated(){
                return request.getPathTranslated();
            }

            @Override
            public javax.servlet.http.PushBuilder newPushBuilder() {
                return getPushBuilder(request.newPushBuilder());
            }

            @Override
            public String getContextPath(){
                return request.getContextPath();
            }

            @Override
            public String getQueryString(){
                return request.getQueryString();
            }

            @Override
            public String getRemoteUser(){
                return request.getRemoteUser();
            }

            @Override
            public boolean isUserInRole(String role){
                return request.isUserInRole(role);
            }

            @Override
            public java.security.Principal getUserPrincipal(){
                return request.getUserPrincipal();
            }

            @Override
            public String getRequestedSessionId(){
                return request.getRequestedSessionId();
            }

            @Override
            public String getRequestURI(){
                return request.getRequestURI();
            }

            @Override
            public StringBuffer getRequestURL(){
                return request.getRequestURL();
            }

            @Override
            public String getServletPath(){
                return request.getServletPath();
            }

            @Override
            public javax.servlet.http.HttpSession getSession(boolean create){
                return getHttpSession(request.getSession(create));
            }

            @Override
            public javax.servlet.http.HttpSession getSession(){
                return getHttpSession(request.getSession());
            }

            @Override
            public String changeSessionId(){
                return request.changeSessionId();
            }

            @Override
            public boolean isRequestedSessionIdValid(){
                return request.isRequestedSessionIdValid();
            }

            @Override
            public boolean isRequestedSessionIdFromCookie(){
                return request.isRequestedSessionIdFromCookie();
            }

            @Override
            public boolean isRequestedSessionIdFromURL(){
                return request.isRequestedSessionIdFromURL();
            }

            @Override
            public boolean isRequestedSessionIdFromUrl(){
                return request.isRequestedSessionIdFromUrl();
            }

            @Override
            public boolean authenticate(javax.servlet.http.HttpServletResponse response)
                throws IOException, javax.servlet.ServletException {
                try{
                    return request.authenticate(getHttpServletResponse(response));
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public void login(String username, String password)
                throws javax.servlet.ServletException{
                try{
                    request.login(username, password);
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public void logout() throws javax.servlet.ServletException{
                try{
                    request.logout();
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public Collection<javax.servlet.http.Part> getParts()
                throws IOException, javax.servlet.ServletException{

                try{
                    ArrayList<javax.servlet.http.Part> arr = new ArrayList<>();
                    for (jakarta.servlet.http.Part part : request.getParts()){
                        arr.add(getHttpPart(part));
                    }
                    return arr;
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public javax.servlet.http.Part getPart(String name)
                throws IOException, javax.servlet.ServletException{
                try{
                    return getHttpPart(request.getPart(name));
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public <T extends javax.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
                throws IOException, javax.servlet.ServletException{

                jakarta.servlet.http.HttpUpgradeHandler handler;

                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public Map<String, String> getTrailerFields() {
                return request.getTrailerFields();
            }

            @Override
            public boolean isTrailerFieldsReady() {
                return request.isTrailerFieldsReady();
            }

            //---------------------------------------------------------------//

            @Override
            public Object getAttribute(String name) {
                return request.getAttribute(name);
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return request.getAttributeNames();
            }

            @Override
            public String getCharacterEncoding() {
                return request.getCharacterEncoding();
            }

            @Override
            public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
                request.setCharacterEncoding(env);
            }

            @Override
            public int getContentLength() {
                return request.getContentLength();
            }

            @Override
            public long getContentLengthLong() {
                return request.getContentLengthLong();
            }

            @Override
            public String getContentType() {
                return request.getContentType();
            }

            @Override
            public javax.servlet.ServletInputStream getInputStream() throws IOException {
                return ServletConverter.getServletInputStream(request.getInputStream());
            }

            @Override
            public String getParameter(String name) {
                return request.getParameter(name);
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return request.getParameterNames();
            }

            @Override
            public String[] getParameterValues(String name) {
                return request.getParameterValues(name);
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return request.getParameterMap();
            }

            @Override
            public String getProtocol() {
                return request.getProtocol();
            }

            @Override
            public String getScheme() {
                return request.getScheme();
            }

            @Override
            public String getServerName() {
                return request.getServerName();
            }

            @Override
            public int getServerPort() {
                return request.getServerPort();
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return request.getReader();
            }

            @Override
            public String getRemoteAddr() {
                return request.getRemoteAddr();
            }

            @Override
            public String getRemoteHost() {
                return request.getRemoteHost();
            }

            @Override
            public void setAttribute(String name, Object o) {
                request.setAttribute(name, o);
            }

            @Override
            public void removeAttribute(String name) {
                request.removeAttribute(name);
            }

            @Override
            public Locale getLocale() {
                return request.getLocale();
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return request.getLocales();
            }

            @Override
            public boolean isSecure() {
                return request.isSecure();
            }

            @Override
            public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {
                return ServletConverter.getRequestDispatcher(request.getRequestDispatcher(path));
            }

            @Override
            public String getRealPath(String path) {
                return request.getRealPath(path);
            }

            @Override
            public int getRemotePort() {
                return request.getRemotePort();
            }

            @Override
            public String getLocalName() {
                return request.getLocalName();
            }

            @Override
            public String getLocalAddr() {
                return request.getLocalAddr();
            }

            @Override
            public int getLocalPort() {
                return request.getLocalPort();
            }

            @Override
            public javax.servlet.ServletContext getServletContext() {
                return ServletConverter.getServletContext(request.getServletContext());
            }

            @Override
            public javax.servlet.AsyncContext startAsync() throws IllegalStateException {
                return ServletConverter.getAsyncContext(request.startAsync());
            }

            @Override
            public javax.servlet.AsyncContext startAsync(
                javax.servlet.ServletRequest servletRequest,
                javax.servlet.ServletResponse servletResponse)
                    throws IllegalStateException {

                return ServletConverter.getAsyncContext(request.startAsync(ServletConverter.getServletRequest(servletRequest),
                        ServletConverter.getServletResponse(servletResponse)
                    )
                );

            }

            @Override
            public boolean isAsyncStarted() {
                return request.isAsyncStarted();
            }

            @Override
            public boolean isAsyncSupported() {
                return request.isAsyncSupported();
            }

            @Override
            public javax.servlet.AsyncContext getAsyncContext() {
                return ServletConverter.getAsyncContext(request.getAsyncContext());
            }

            @Override
            public javax.servlet.DispatcherType getDispatcherType() {
                return ServletConverter.getDispatcherType(request.getDispatcherType());
            }

        };
    }


  //**************************************************************************
  //** getHttpServletResponse
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" HttpServletResponse.
   */
    public static javax.servlet.http.HttpServletResponse getHttpServletResponse(
        jakarta.servlet.http.HttpServletResponse response){

        return new javax.servlet.http.HttpServletResponse() {

            @Override
            public void addCookie(javax.servlet.http.Cookie cookie) {
                response.addCookie(getCookie(cookie));
            }

            @Override
            public boolean containsHeader(String name) {
                return response.containsHeader(name);
            }

            @Override
            public String encodeURL(String url) {
                return response.encodeURL(url);
            }

            @Override
            public String encodeRedirectURL(String url) {
                return response.encodeRedirectURL(url);
            }

            @Override
            public String encodeUrl(String url) {
                return response.encodeUrl(url);
            }

            @Override
            public String encodeRedirectUrl(String url) {
                return response.encodeRedirectUrl(url);
            }

            @Override
            public void sendError(int sc, String msg) throws IOException {
                response.sendError(sc, msg);
            }

            @Override
            public void sendError(int sc) throws IOException {
                response.sendError(sc);
            }

            @Override
            public void sendRedirect(String location) throws IOException {
                response.sendRedirect(location);
            }

            @Override
            public void setDateHeader(String name, long date) {
                response.setDateHeader(name, date);
            }

            @Override
            public void addDateHeader(String name, long date) {
                response.addDateHeader(name, date);
            }

            @Override
            public void setHeader(String name, String value) {
                response.setHeader(name, value);
            }

            @Override
            public void addHeader(String name, String value) {
                response.addHeader(name, value);
            }

            @Override
            public void setIntHeader(String name, int value) {
                response.setIntHeader(name, value);
            }

            @Override
            public void addIntHeader(String name, int value) {
                response.addIntHeader(name, value);
            }

            @Override
            public void setStatus(int sc) {
                response.setStatus(sc);
            }

            @Override
            public void setStatus(int sc, String sm) {
                response.setStatus(sc, sm);
            }

            @Override
            public int getStatus() {
                return response.getStatus();
            }

            @Override
            public String getHeader(String name) {
                return response.getHeader(name);
            }

            @Override
            public Collection<String> getHeaders(String name) {
                return response.getHeaders(name);
            }

            @Override
            public Collection<String> getHeaderNames() {
                return response.getHeaderNames();
            }

            @Override
            public String getCharacterEncoding() {
                return response.getCharacterEncoding();
            }

            @Override
            public String getContentType() {
                return response.getContentType();
            }

            @Override
            public javax.servlet.ServletOutputStream getOutputStream() throws IOException {
                return ServletConverter.getServletOutputStream(response.getOutputStream());
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return response.getWriter();
            }

            @Override
            public void setCharacterEncoding(String charset) {
                response.setCharacterEncoding(charset);
            }

            @Override
            public void setContentLength(int len) {
                response.setContentLength(len);
            }

            @Override
            public void setContentLengthLong(long len) {
                response.setContentLengthLong(len);
            }

            @Override
            public void setContentType(String type) {
                response.setContentType(type);
            }

            @Override
            public void setBufferSize(int size) {
                response.setBufferSize(size);
            }

            @Override
            public int getBufferSize() {
                return response.getBufferSize();
            }

            @Override
            public void flushBuffer() throws IOException {
                response.flushBuffer();
            }

            @Override
            public void resetBuffer() {
                response.resetBuffer();
            }

            @Override
            public boolean isCommitted() {
                return response.isCommitted();
            }

            @Override
            public void reset() {
                response.reset();
            }

            @Override
            public void setLocale(Locale loc) {
                response.setLocale(loc);
            }

            @Override
            public Locale getLocale() {
                return response.getLocale();
            }
        };
    }


  //**************************************************************************
  //** getHttpServletResponse
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" HttpServletResponse.
   */
    public static jakarta.servlet.http.HttpServletResponse getHttpServletResponse(
        javax.servlet.http.HttpServletResponse request){

        return new jakarta.servlet.http.HttpServletResponse() {
            @Override
            public void addCookie(jakarta.servlet.http.Cookie cookie) {
                request.addCookie(getCookie(cookie));
            }

            @Override
            public boolean containsHeader(String name) {
                return request.containsHeader(name);
            }

            @Override
            public String encodeURL(String url) {
                return request.encodeURL(url);
            }

            @Override
            public String encodeRedirectURL(String url) {
                return request.encodeRedirectURL(url);
            }

            @Override
            public String encodeUrl(String url) {
                return request.encodeUrl(url);
            }

            @Override
            public String encodeRedirectUrl(String url) {
                return request.encodeRedirectUrl(url);
            }

            @Override
            public void sendError(int sc, String msg) throws IOException {
                request.sendError(sc, msg);
            }

            @Override
            public void sendError(int sc) throws IOException {
                request.sendError(sc);
            }

            @Override
            public void sendRedirect(String location) throws IOException {
                request.sendRedirect(location);
            }

            @Override
            public void setDateHeader(String name, long date) {
                request.setDateHeader(name, date);
            }

            @Override
            public void addDateHeader(String name, long date) {
                request.addDateHeader(name, date);
            }

            @Override
            public void setHeader(String name, String value) {
                request.setHeader(name, value);
            }

            @Override
            public void addHeader(String name, String value) {
                request.addHeader(name, value);
            }

            @Override
            public void setIntHeader(String name, int value) {
                request.setIntHeader(name, value);
            }

            @Override
            public void addIntHeader(String name, int value) {
                request.addIntHeader(name, value);
            }

            @Override
            public void setStatus(int sc) {
                request.setStatus(sc);
            }

            @Override
            public void setStatus(int sc, String sm) {
                request.setStatus(sc, sm);
            }

            @Override
            public int getStatus() {
                return request.getStatus();
            }

            @Override
            public String getHeader(String name) {
                return request.getHeader(name);
            }

            @Override
            public Collection<String> getHeaders(String name) {
                return request.getHeaders(name);
            }

            @Override
            public Collection<String> getHeaderNames() {
                return request.getHeaderNames();
            }

            @Override
            public String getCharacterEncoding() {
                return request.getCharacterEncoding();
            }

            @Override
            public String getContentType() {
                return request.getContentType();
            }

            @Override
            public jakarta.servlet.ServletOutputStream getOutputStream() throws IOException {
                return ServletConverter.getServletOutputStream(request.getOutputStream());
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return request.getWriter();
            }

            @Override
            public void setCharacterEncoding(String charset) {
                request.setCharacterEncoding(charset);
            }

            @Override
            public void setContentLength(int len) {
                request.setContentLength(len);
            }

            @Override
            public void setContentLengthLong(long len) {
                request.setContentLengthLong(len);
            }

            @Override
            public void setContentType(String type) {
                request.setContentType(type);
            }

            @Override
            public void setBufferSize(int size) {
                request.setBufferSize(size);
            }

            @Override
            public int getBufferSize() {
                return request.getBufferSize();
            }

            @Override
            public void flushBuffer() throws IOException {
                request.flushBuffer();
            }

            @Override
            public void resetBuffer() {
                request.resetBuffer();
            }

            @Override
            public boolean isCommitted() {
                return request.isCommitted();
            }

            @Override
            public void reset() {
                request.reset();
            }

            @Override
            public void setLocale(Locale loc) {
                request.setLocale(loc);
            }

            @Override
            public Locale getLocale() {
                return request.getLocale();
            }
        };
    }


  //**************************************************************************
  //** getCookie
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" HttpServletRequest.
   */
    public static javax.servlet.http.Cookie getCookie(
        jakarta.servlet.http.Cookie cookie){
        javax.servlet.http.Cookie c =
        new javax.servlet.http.Cookie(cookie.getName(), cookie.getValue());
        c.setComment(cookie.getComment());
        c.setDomain(cookie.getDomain());
        c.setHttpOnly(cookie.isHttpOnly());
        c.setMaxAge(cookie.getMaxAge());
        c.setPath(cookie.getPath());
        c.setSecure(cookie.getSecure());
        c.setVersion(cookie.getVersion());
        return c;
    }


  //**************************************************************************
  //** getCookie
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" HttpServletRequest.
   */
    public static jakarta.servlet.http.Cookie getCookie(
        javax.servlet.http.Cookie cookie){
        jakarta.servlet.http.Cookie c =
        new jakarta.servlet.http.Cookie(cookie.getName(), cookie.getValue());
        c.setComment(cookie.getComment());
        c.setDomain(cookie.getDomain());
        c.setHttpOnly(cookie.isHttpOnly());
        c.setMaxAge(cookie.getMaxAge());
        c.setPath(cookie.getPath());
        c.setSecure(cookie.getSecure());
        c.setVersion(cookie.getVersion());
        return c;
    }


  //**************************************************************************
  //** getHttpSession
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" HttpSession.
   */
    public static javax.servlet.http.HttpSession getHttpSession(
        jakarta.servlet.http.HttpSession session){
        return new javax.servlet.http.HttpSession() {
            @Override
            public long getCreationTime() {
                return session.getCreationTime();
            }

            @Override
            public String getId() {
                return session.getId();
            }

            @Override
            public long getLastAccessedTime() {
                return session.getLastAccessedTime();
            }

            @Override
            public javax.servlet.ServletContext getServletContext() {
                return ServletConverter.getServletContext(session.getServletContext());
            }

            @Override
            public void setMaxInactiveInterval(int interval) {
                session.setMaxInactiveInterval(interval);
            }

            @Override
            public int getMaxInactiveInterval() {
                return session.getMaxInactiveInterval();
            }

            @Override
            public javax.servlet.http.HttpSessionContext getSessionContext() {
                return getHttpSessionContext(session.getSessionContext());
            }

            @Override
            public Object getAttribute(String name) {
                return session.getAttribute(name);
            }

            @Override
            public Object getValue(String name) {
                return session.getValue(name);
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return session.getAttributeNames();
            }

            @Override
            public String[] getValueNames() {
                return session.getValueNames();
            }

            @Override
            public void setAttribute(String name, Object value) {
                session.setAttribute(name, value);
            }

            @Override
            public void putValue(String name, Object value) {
                session.putValue(name, value);
            }

            @Override
            public void removeAttribute(String name) {
                session.removeAttribute(name);
            }

            @Override
            public void removeValue(String name) {
                session.removeValue(name);
            }

            @Override
            public void invalidate() {
                session.invalidate();
            }

            @Override
            public boolean isNew() {
                return session.isNew();
            }
        };
    }


  //**************************************************************************
  //** getHttpSessionContext
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" HttpSessionContext.
   */
    public static javax.servlet.http.HttpSessionContext getHttpSessionContext(
        jakarta.servlet.http.HttpSessionContext ctx){
        return new javax.servlet.http.HttpSessionContext() {
            @Override
            public javax.servlet.http.HttpSession getSession(String sessionId) {
                return getHttpSession(ctx.getSession(sessionId));
            }

            @Override
            public Enumeration<String> getIds() {
                return ctx.getIds();
            }
        };
    }


  //**************************************************************************
  //** getServletRequest
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ServletRequest.
   */
    public static javax.servlet.ServletRequest getServletRequest(
        jakarta.servlet.ServletRequest request){

        return new javax.servlet.ServletRequest() {

            @Override
            public Object getAttribute(String name) {
                return request.getAttribute(name);
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return request.getAttributeNames();
            }

            @Override
            public String getCharacterEncoding() {
                return request.getCharacterEncoding();
            }

            @Override
            public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
                request.setCharacterEncoding(env);
            }

            @Override
            public int getContentLength() {
                return request.getContentLength();
            }

            @Override
            public long getContentLengthLong() {
                return request.getContentLengthLong();
            }

            @Override
            public String getContentType() {
                return request.getContentType();
            }

            @Override
            public javax.servlet.ServletInputStream getInputStream() throws IOException {
                return getServletInputStream(request.getInputStream());
            }

            @Override
            public String getParameter(String name) {
                return request.getParameter(name);
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return request.getParameterNames();
            }

            @Override
            public String[] getParameterValues(String name) {
                return request.getParameterValues(name);
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return request.getParameterMap();
            }

            @Override
            public String getProtocol() {
                return request.getProtocol();
            }

            @Override
            public String getScheme() {
                return request.getScheme();
            }

            @Override
            public String getServerName() {
                return request.getServerName();
            }

            @Override
            public int getServerPort() {
                return request.getServerPort();
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return request.getReader();
            }

            @Override
            public String getRemoteAddr() {
                return request.getRemoteAddr();
            }

            @Override
            public String getRemoteHost() {
                return request.getRemoteHost();
            }

            @Override
            public void setAttribute(String name, Object o) {
                request.setAttribute(name, o);
            }

            @Override
            public void removeAttribute(String name) {
                request.removeAttribute(name);
            }

            @Override
            public Locale getLocale() {
                return request.getLocale();
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return request.getLocales();
            }

            @Override
            public boolean isSecure() {
                return request.isSecure();
            }

            @Override
            public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {
                return ServletConverter.getRequestDispatcher(request.getRequestDispatcher(path));
            }

            @Override
            public String getRealPath(String path) {
                return request.getRealPath(path);
            }

            @Override
            public int getRemotePort() {
                return request.getRemotePort();
            }

            @Override
            public String getLocalName() {
                return request.getLocalName();
            }

            @Override
            public String getLocalAddr() {
                return request.getLocalAddr();
            }

            @Override
            public int getLocalPort() {
                return request.getLocalPort();
            }

            @Override
            public javax.servlet.ServletContext getServletContext() {
                return ServletConverter.getServletContext(request.getServletContext());
            }

            @Override
            public javax.servlet.AsyncContext startAsync() throws IllegalStateException {
                return ServletConverter.getAsyncContext(request.startAsync());
            }

            @Override
            public javax.servlet.AsyncContext startAsync(
                javax.servlet.ServletRequest servletRequest,
                javax.servlet.ServletResponse servletResponse) throws IllegalStateException {

                return ServletConverter.getAsyncContext(request.startAsync(
                    getServletRequest(servletRequest),
                    getServletResponse(servletResponse)
                ));
            }

            @Override
            public boolean isAsyncStarted() {
                return request.isAsyncStarted();
            }

            @Override
            public boolean isAsyncSupported() {
                return request.isAsyncSupported();
            }

            @Override
            public javax.servlet.AsyncContext getAsyncContext() {
                return ServletConverter.getAsyncContext(request.getAsyncContext());
            }

            @Override
            public javax.servlet.DispatcherType getDispatcherType() {
                return ServletConverter.getDispatcherType(request.getDispatcherType());
            }
        };

    }


  //**************************************************************************
  //** getServletRequest
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" ServletRequest.
   */
    public static jakarta.servlet.ServletRequest getServletRequest(
        javax.servlet.ServletRequest request){

        return new jakarta.servlet.ServletRequest() {

            @Override
            public Object getAttribute(String name) {
                return request.getAttribute(name);
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return request.getAttributeNames();
            }

            @Override
            public String getCharacterEncoding() {
                return request.getCharacterEncoding();
            }

            @Override
            public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
                request.setCharacterEncoding(env);
            }

            @Override
            public int getContentLength() {
                return request.getContentLength();
            }

            @Override
            public long getContentLengthLong() {
                return request.getContentLengthLong();
            }

            @Override
            public String getContentType() {
                return request.getContentType();
            }

            @Override
            public jakarta.servlet.ServletInputStream getInputStream() throws IOException {
                return getServletInputStream(request.getInputStream());
            }

            @Override
            public String getParameter(String name) {
                return request.getParameter(name);
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return request.getParameterNames();
            }

            @Override
            public String[] getParameterValues(String name) {
                return request.getParameterValues(name);
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return request.getParameterMap();
            }

            @Override
            public String getProtocol() {
                return request.getProtocol();
            }

            @Override
            public String getScheme() {
                return request.getScheme();
            }

            @Override
            public String getServerName() {
                return request.getServerName();
            }

            @Override
            public int getServerPort() {
                return request.getServerPort();
            }

            @Override
            public BufferedReader getReader() throws IOException {
                return request.getReader();
            }

            @Override
            public String getRemoteAddr() {
                return request.getRemoteAddr();
            }

            @Override
            public String getRemoteHost() {
                return request.getRemoteHost();
            }

            @Override
            public void setAttribute(String name, Object o) {
                request.setAttribute(name, o);
            }

            @Override
            public void removeAttribute(String name) {
                request.removeAttribute(name);
            }

            @Override
            public Locale getLocale() {
                return request.getLocale();
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return request.getLocales();
            }

            @Override
            public boolean isSecure() {
                return request.isSecure();
            }

            @Override
            public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) {
                return ServletConverter.getRequestDispatcher(request.getRequestDispatcher(path));
            }

            @Override
            public String getRealPath(String path) {
                return request.getRealPath(path);
            }

            @Override
            public int getRemotePort() {
                return request.getRemotePort();
            }

            @Override
            public String getLocalName() {
                return request.getLocalName();
            }

            @Override
            public String getLocalAddr() {
                return request.getLocalAddr();
            }

            @Override
            public int getLocalPort() {
                return request.getLocalPort();
            }

            @Override
            public jakarta.servlet.ServletContext getServletContext() {
                return ServletConverter.getServletContext(request.getServletContext());
            }

            @Override
            public jakarta.servlet.AsyncContext startAsync() throws IllegalStateException {
                return ServletConverter.getAsyncContext(request.startAsync());
            }

            @Override
            public jakarta.servlet.AsyncContext startAsync(
                jakarta.servlet.ServletRequest servletRequest,
                jakarta.servlet.ServletResponse servletResponse) throws IllegalStateException {

                return ServletConverter.getAsyncContext(request.startAsync(
                    getServletRequest(servletRequest),
                    getServletResponse(servletResponse)
                ));
            }

            @Override
            public boolean isAsyncStarted() {
                return request.isAsyncStarted();
            }

            @Override
            public boolean isAsyncSupported() {
                return request.isAsyncSupported();
            }

            @Override
            public jakarta.servlet.AsyncContext getAsyncContext() {
                return ServletConverter.getAsyncContext(request.getAsyncContext());
            }

            @Override
            public jakarta.servlet.DispatcherType getDispatcherType() {
                return ServletConverter.getDispatcherType(request.getDispatcherType());
            }
        };

    }


  //**************************************************************************
  //** getServletResponse
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ServletResponse.
   */
    public static javax.servlet.ServletResponse getServletResponse(
        jakarta.servlet.ServletResponse response){
        return new javax.servlet.ServletResponse() {
            @Override
            public String getCharacterEncoding() {
                return response.getCharacterEncoding();
            }

            @Override
            public String getContentType() {
                return response.getContentType();
            }

            @Override
            public javax.servlet.ServletOutputStream getOutputStream() throws IOException {
                return getServletOutputStream(response.getOutputStream());
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return response.getWriter();
            }

            @Override
            public void setCharacterEncoding(String charset) {
                response.setCharacterEncoding(charset);
            }

            @Override
            public void setContentLength(int len) {
                response.setContentLength(len);
            }

            @Override
            public void setContentLengthLong(long len) {
                response.setContentLengthLong(len);
            }

            @Override
            public void setContentType(String type) {
                response.setContentType(type);
            }

            @Override
            public void setBufferSize(int size) {
                response.setBufferSize(size);
            }

            @Override
            public int getBufferSize() {
                return response. getBufferSize();
            }

            @Override
            public void flushBuffer() throws IOException {
                response.flushBuffer();
            }

            @Override
            public void resetBuffer() {
                response.resetBuffer();
            }

            @Override
            public boolean isCommitted() {
                return response.isCommitted();
            }


            @Override
            public void reset() {
                response.reset();
            }

            @Override
            public void setLocale(Locale loc) {
                response.setLocale(loc);
            }

            @Override
            public Locale getLocale() {
                return response.getLocale();
            }
        };
    }


  //**************************************************************************
  //** getServletResponse
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" ServletResponse.
   */
    public static jakarta.servlet.ServletResponse getServletResponse(
        javax.servlet.ServletResponse response){
        return new jakarta.servlet.ServletResponse() {
            @Override
            public String getCharacterEncoding() {
                return response.getCharacterEncoding();
            }

            @Override
            public String getContentType() {
                return response.getContentType();
            }

            @Override
            public jakarta.servlet.ServletOutputStream getOutputStream() throws IOException {
                return getServletOutputStream(response.getOutputStream());
            }

            @Override
            public PrintWriter getWriter() throws IOException {
                return response.getWriter();
            }

            @Override
            public void setCharacterEncoding(String charset) {
                response.setCharacterEncoding(charset);
            }

            @Override
            public void setContentLength(int len) {
                response.setContentLength(len);
            }

            @Override
            public void setContentLengthLong(long len) {
                response.setContentLengthLong(len);
            }

            @Override
            public void setContentType(String type) {
                response.setContentType(type);
            }

            @Override
            public void setBufferSize(int size) {
                response.setBufferSize(size);
            }

            @Override
            public int getBufferSize() {
                return response. getBufferSize();
            }

            @Override
            public void flushBuffer() throws IOException {
                response.flushBuffer();
            }

            @Override
            public void resetBuffer() {
                response.resetBuffer();
            }

            @Override
            public boolean isCommitted() {
                return response.isCommitted();
            }


            @Override
            public void reset() {
                response.reset();
            }

            @Override
            public void setLocale(Locale loc) {
                response.setLocale(loc);
            }

            @Override
            public Locale getLocale() {
                return response.getLocale();
            }
        };
    }


  //**************************************************************************
  //** getServlet
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" Servlet.
   */
    public static javax.servlet.Servlet getServlet(
        jakarta.servlet.Servlet servlet){

        return new javax.servlet.Servlet() {
            @Override
            public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException {
                try{
                    servlet.init(ServletConverter.getServletConfig(config));
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }

            }

            @Override
            public javax.servlet.ServletConfig getServletConfig() {
                return ServletConverter.getServletConfig(servlet.getServletConfig());
            }

            @Override
            public void service(javax.servlet.ServletRequest req, javax.servlet.ServletResponse res)
                throws javax.servlet.ServletException, IOException {

                try{
                    servlet.service(ServletConverter.getServletRequest(req), ServletConverter.getServletResponse(res));
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public String getServletInfo() {
                return servlet.getServletInfo();
            }

            @Override
            public void destroy() {
                servlet.destroy();
            }
        };
    }


  //**************************************************************************
  //** getServlet
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" Servlet.
   */
    public static jakarta.servlet.Servlet getServlet(
        javax.servlet.Servlet servlet){

        return new jakarta.servlet.Servlet() {
            @Override
            public void init(jakarta.servlet.ServletConfig config) throws jakarta.servlet.ServletException {
                try{
                    servlet.init(ServletConverter.getServletConfig(config));
                }
                catch(javax.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public jakarta.servlet.ServletConfig getServletConfig() {
                return ServletConverter.getServletConfig(servlet.getServletConfig());
            }

            @Override
            public void service(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res)
                throws jakarta.servlet.ServletException, IOException {
                try{
                    servlet.service(ServletConverter.getServletRequest(req), ServletConverter.getServletResponse(res));
                }
                catch(javax.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public String getServletInfo() {
                return servlet.getServletInfo();
            }

            @Override
            public void destroy() {
                servlet.destroy();
            }
        };
    }


  //**************************************************************************
  //** getServletConfig
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ServletConfig.
   */
    public static javax.servlet.ServletConfig getServletConfig(
        jakarta.servlet.ServletConfig config){

        return new javax.servlet.ServletConfig() {
            @Override
            public String getServletName() {
                return config.getServletName();
            }

            @Override
            public javax.servlet.ServletContext getServletContext() {
                return ServletConverter.getServletContext(config.getServletContext());
            }

            @Override
            public String getInitParameter(String name) {
                return config.getInitParameter(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return config.getInitParameterNames();
            }
        };
    }


  //**************************************************************************
  //** getServletConfig
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ServletConfig.
   */
    public static jakarta.servlet.ServletConfig getServletConfig(
        javax.servlet.ServletConfig config){

        return new jakarta.servlet.ServletConfig() {
            @Override
            public String getServletName() {
                return config.getServletName();
            }

            @Override
            public jakarta.servlet.ServletContext getServletContext() {
                return ServletConverter.getServletContext(config.getServletContext());
            }

            @Override
            public String getInitParameter(String name) {
                return config.getInitParameter(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return config.getInitParameterNames();
            }
        };
    }


  //**************************************************************************
  //** getServletContext
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ServletContext.
   */
    public static javax.servlet.ServletContext getServletContext(
        jakarta.servlet.ServletContext context){

        return new javax.servlet.ServletContext() {

            @Override
            public String getContextPath() {
                return context.getContextPath();
            }

            @Override
            public javax.servlet.ServletContext getContext(String uripath) {
                return getServletContext(context.getContext(uripath));
            }

            @Override
            public int getMajorVersion() {
                return context.getMajorVersion();
            }

            @Override
            public int getMinorVersion() {
                return context.getMinorVersion();
            }

            @Override
            public int getEffectiveMajorVersion() {
                return context.getEffectiveMajorVersion();
            }

            @Override
            public int getEffectiveMinorVersion() {
                return context.getEffectiveMinorVersion();
            }

            @Override
            public String getMimeType(String file) {
                return context.getMimeType(file);
            }

            @Override
            public Set<String> getResourcePaths(String path) {
                return context.getResourcePaths(path);
            }

            @Override
            public URL getResource(String path) throws MalformedURLException {
                return context.getResource(path);
            }

            @Override
            public InputStream getResourceAsStream(String path) {
                return context.getResourceAsStream(path);
            }

            @Override
            public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {
                return ServletConverter.getRequestDispatcher(context.getRequestDispatcher(path));
            }

            @Override
            public javax.servlet.RequestDispatcher getNamedDispatcher(String name) {
                return ServletConverter.getRequestDispatcher(context.getNamedDispatcher(name));
            }

            @Override
            public javax.servlet.Servlet getServlet(String name) throws javax.servlet.ServletException {
                try{
                    return ServletConverter.getServlet(context.getServlet(name));
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public Enumeration<javax.servlet.Servlet> getServlets() {
                Iterator<jakarta.servlet.Servlet> servlets = context.getServlets().asIterator();
                ArrayList<javax.servlet.Servlet> arr = new ArrayList<>();
                while (servlets.hasNext()){
                    arr.add(ServletConverter.getServlet(servlets.next()));
                }
                return Collections.enumeration(arr);
            }

            @Override
            public Enumeration<String> getServletNames() {
                return context.getServletNames();
            }

            @Override
            public void log(String msg) {
                context.log(msg);
            }

            @Override
            public void log(Exception exception, String msg) {
                context.log(exception, msg);
            }

            @Override
            public void log(String message, Throwable throwable) {
                context.log(message, throwable);
            }

            @Override
            public String getRealPath(String path) {
                return context.getRealPath(path);
            }

            @Override
            public String getServerInfo() {
                return context.getServerInfo();
            }

            @Override
            public String getInitParameter(String name) {
                return context.getInitParameter(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return context.getInitParameterNames();
            }

            @Override
            public boolean setInitParameter(String name, String value) {
                return context.setInitParameter(name, value);
            }

            @Override
            public Object getAttribute(String name) {
                return context.getAttribute(name);
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return context.getAttributeNames();
            }

            @Override
            public void setAttribute(String name, Object object) {
                context.setAttribute(name, object);
            }

            @Override
            public void removeAttribute(String name) {
                context.removeAttribute(name);
            }

            @Override
            public String getServletContextName() {
                return context.getServletContextName();
            }

            @Override
            public <T extends javax.servlet.Servlet> T createServlet(Class<T> clazz) throws javax.servlet.ServletException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, javax.servlet.Filter filter) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends javax.servlet.Filter> filterClass) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public <T extends javax.servlet.Filter> T createFilter(Class<T> clazz) throws javax.servlet.ServletException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public javax.servlet.FilterRegistration getFilterRegistration(String filterName) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public Map<String, ? extends javax.servlet.FilterRegistration> getFilterRegistrations() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public javax.servlet.SessionCookieConfig getSessionCookieConfig() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void setSessionTrackingModes(Set<javax.servlet.SessionTrackingMode> sessionTrackingModes) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public Set<javax.servlet.SessionTrackingMode> getDefaultSessionTrackingModes() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public Set<javax.servlet.SessionTrackingMode> getEffectiveSessionTrackingModes() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void addListener(String className) {
                context.addListener(className);
            }

            @Override
            public <T extends EventListener> void addListener(T t) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void addListener(Class<? extends EventListener> listenerClass) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public <T extends EventListener> T createListener(Class<T> clazz) throws javax.servlet.ServletException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public ClassLoader getClassLoader() {
                return context.getClassLoader();
            }

            @Override
            public void declareRoles(String... roleNames) {
                context.declareRoles(roleNames);
            }

            @Override
            public String getVirtualServerName() {
                return context.getVirtualServerName();
            }
        };
    }


  //**************************************************************************
  //** getServletContext
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" ServletContext.
   */
    public static jakarta.servlet.ServletContext getServletContext(
        javax.servlet.ServletContext context){

        return new jakarta.servlet.ServletContext() {

            @Override
            public String getContextPath() {
                return context.getContextPath();
            }

            @Override
            public jakarta.servlet.ServletContext getContext(String uripath) {
                return getServletContext(context.getContext(uripath));
            }

            @Override
            public int getMajorVersion() {
                return context.getMajorVersion();
            }

            @Override
            public int getMinorVersion() {
                return context.getMinorVersion();
            }

            @Override
            public int getEffectiveMajorVersion() {
                return context.getEffectiveMajorVersion();
            }

            @Override
            public int getEffectiveMinorVersion() {
                return context.getEffectiveMinorVersion();
            }

            @Override
            public String getMimeType(String file) {
                return context.getMimeType(file);
            }

            @Override
            public Set<String> getResourcePaths(String path) {
                return context.getResourcePaths(path);
            }

            @Override
            public URL getResource(String path) throws MalformedURLException {
                return context.getResource(path);
            }

            @Override
            public InputStream getResourceAsStream(String path) {
                return context.getResourceAsStream(path);
            }

            @Override
            public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) {
                return ServletConverter.getRequestDispatcher(context.getRequestDispatcher(path));
            }

            @Override
            public jakarta.servlet.RequestDispatcher getNamedDispatcher(String name) {
                return ServletConverter.getRequestDispatcher(context.getNamedDispatcher(name));
            }

            @Override
            public jakarta.servlet.Servlet getServlet(String name) throws jakarta.servlet.ServletException {
                try{
                    return ServletConverter.getServlet(context.getServlet(name));
                }
                catch(javax.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public Enumeration<jakarta.servlet.Servlet> getServlets() {
                Iterator<javax.servlet.Servlet> servlets = context.getServlets().asIterator();
                ArrayList<jakarta.servlet.Servlet> arr = new ArrayList<>();
                while (servlets.hasNext()){
                    arr.add(ServletConverter.getServlet(servlets.next()));
                }
                return Collections.enumeration(arr);
            }

            @Override
            public Enumeration<String> getServletNames() {
                return context.getServletNames();
            }

            @Override
            public void log(String msg) {
                context.log(msg);
            }

            @Override
            public void log(Exception exception, String msg) {
                context.log(exception, msg);
            }

            @Override
            public void log(String message, Throwable throwable) {
                context.log(message, throwable);
            }

            @Override
            public String getRealPath(String path) {
                return context.getRealPath(path);
            }

            @Override
            public String getServerInfo() {
                return context.getServerInfo();
            }

            @Override
            public String getInitParameter(String name) {
                return context.getInitParameter(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return context.getInitParameterNames();
            }

            @Override
            public boolean setInitParameter(String name, String value) {
                return context.setInitParameter(name, value);
            }

            @Override
            public Object getAttribute(String name) {
                return context.getAttribute(name);
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return context.getAttributeNames();
            }

            @Override
            public void setAttribute(String name, Object object) {
                context.setAttribute(name, object);
            }

            @Override
            public void removeAttribute(String name) {
                context.removeAttribute(name);
            }

            @Override
            public String getServletContextName() {
                return context.getServletContextName();
            }

            @Override
            public <T extends jakarta.servlet.Servlet> T createServlet(Class<T> clazz) throws jakarta.servlet.ServletException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, jakarta.servlet.Filter filter) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public jakarta.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends jakarta.servlet.Filter> filterClass) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public <T extends jakarta.servlet.Filter> T createFilter(Class<T> clazz) throws jakarta.servlet.ServletException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public jakarta.servlet.FilterRegistration getFilterRegistration(String filterName) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public Map<String, ? extends jakarta.servlet.FilterRegistration> getFilterRegistrations() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public jakarta.servlet.SessionCookieConfig getSessionCookieConfig() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void setSessionTrackingModes(Set<jakarta.servlet.SessionTrackingMode> sessionTrackingModes) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public Set<jakarta.servlet.SessionTrackingMode> getDefaultSessionTrackingModes() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public Set<jakarta.servlet.SessionTrackingMode> getEffectiveSessionTrackingModes() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void addListener(String className) {
                context.addListener(className);
            }

            @Override
            public <T extends EventListener> void addListener(T t) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void addListener(Class<? extends EventListener> listenerClass) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public <T extends EventListener> T createListener(Class<T> clazz) throws jakarta.servlet.ServletException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public ClassLoader getClassLoader() {
                return context.getClassLoader();
            }

            @Override
            public void declareRoles(String... roleNames) {
                context.declareRoles(roleNames);
            }

            @Override
            public String getVirtualServerName() {
                return context.getVirtualServerName();
            }
        };
    }


  //**************************************************************************
  //** getServletException
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ServletException.
   */
    public static javax.servlet.ServletException getServletException(
        jakarta.servlet.ServletException e){
        return new javax.servlet.ServletException(e);
    }


  //**************************************************************************
  //** getServletException
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" ServletException.
   */
    public static jakarta.servlet.ServletException getServletException(
        javax.servlet.ServletException e){
        return new jakarta.servlet.ServletException(e);
    }


  //**************************************************************************
  //** getServletInputStream
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ServletInputStream.
   */
    public static javax.servlet.ServletInputStream getServletInputStream(
        jakarta.servlet.ServletInputStream is){

        return new javax.servlet.ServletInputStream() {
            @Override
            public boolean isFinished() {
                return is.isFinished();
            }

            @Override
            public boolean isReady() {
                return is.isReady();
            }

            @Override
            public void setReadListener(javax.servlet.ReadListener readListener) {
                is.setReadListener(getReadListener(readListener));
            }

            @Override
            public int read() throws IOException {
                return is.read();
            }
        };
    }


  //**************************************************************************
  //** getServletInputStream
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" ServletInputStream.
   */
    public static jakarta.servlet.ServletInputStream getServletInputStream(
        javax.servlet.ServletInputStream is){

        return new jakarta.servlet.ServletInputStream() {
            @Override
            public boolean isFinished() {
                return is.isFinished();
            }

            @Override
            public boolean isReady() {
                return is.isReady();
            }

            @Override
            public void setReadListener(jakarta.servlet.ReadListener readListener) {
                is.setReadListener(getReadListener(readListener));
            }

            @Override
            public int read() throws IOException {
                return is.read();
            }
        };
    }


  //**************************************************************************
  //** getServletOutputStream
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" ServletOutputStream.
   */
    public static jakarta.servlet.ServletOutputStream getServletOutputStream(
        javax.servlet.ServletOutputStream os){

        return new jakarta.servlet.ServletOutputStream() {
            @Override
            public boolean isReady() {
                return os.isReady();
            }

            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
                os.setWriteListener(getWriteListener(writeListener));
            }

            @Override
            public void write(int b) throws IOException {
                os.write(b);
            }
        };
    }


  //**************************************************************************
  //** getServletOutputStream
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ServletOutputStream.
   */
    public static javax.servlet.ServletOutputStream getServletOutputStream(
        jakarta.servlet.ServletOutputStream os){
        return new javax.servlet.ServletOutputStream() {
            @Override
            public boolean isReady() {
                return os.isReady();
            }

            @Override
            public void setWriteListener(javax.servlet.WriteListener writeListener) {
                os.setWriteListener(getWriteListener(writeListener));
            }

            @Override
            public void write(int b) throws IOException {
                os.write(b);
            }
        };
    }


  //**************************************************************************
  //** getRequestDispatcher
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" RequestDispatcher.
   */
    public static javax.servlet.RequestDispatcher getRequestDispatcher(
        jakarta.servlet.RequestDispatcher dispatcher){

        return new javax.servlet.RequestDispatcher() {
            @Override
            public void forward(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response)
                throws javax.servlet.ServletException, IOException {

                try{
                    dispatcher.forward(ServletConverter.getServletRequest(request), ServletConverter.getServletResponse(response));
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public void include(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response)
                throws javax.servlet.ServletException, IOException {
                try{
                    dispatcher.include(ServletConverter.getServletRequest(request), ServletConverter.getServletResponse(response));
                }
                catch(jakarta.servlet.ServletException e){
                    throw getServletException(e);
                }
            }
        };

    }


  //**************************************************************************
  //** getRequestDispatcher
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" RequestDispatcher.
   */
    public static jakarta.servlet.RequestDispatcher getRequestDispatcher(
        javax.servlet.RequestDispatcher dispatcher){

        return new jakarta.servlet.RequestDispatcher() {
            @Override
            public void forward(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response)
                throws jakarta.servlet.ServletException, IOException {

                try{
                    dispatcher.forward(ServletConverter.getServletRequest(request), ServletConverter.getServletResponse(response));
                }
                catch(javax.servlet.ServletException e){
                    throw getServletException(e);
                }
            }

            @Override
            public void include(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response)
                throws jakarta.servlet.ServletException, IOException {
                try{
                    dispatcher.include(ServletConverter.getServletRequest(request), ServletConverter.getServletResponse(response));
                }
                catch(javax.servlet.ServletException e){
                    throw getServletException(e);
                }
            }
        };

    }


  //**************************************************************************
  //** getDispatcherType
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" DispatcherType.
   */
    public static javax.servlet.DispatcherType getDispatcherType(
        jakarta.servlet.DispatcherType type){
        throw new UnsupportedOperationException("Not implemented");
    }


  //**************************************************************************
  //** getDispatcherType
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" DispatcherType.
   */
    public static jakarta.servlet.DispatcherType getDispatcherType(
        javax.servlet.DispatcherType type){
        throw new UnsupportedOperationException("Not implemented");
    }


  //**************************************************************************
  //** getReadListener
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" ReadListener.
   */
    public static javax.servlet.ReadListener getReadListener(
        jakarta.servlet.ReadListener listener){
        return new javax.servlet.ReadListener() {
            @Override
            public void onDataAvailable() throws IOException {
                listener.onDataAvailable();
            }

            @Override
            public void onAllDataRead() throws IOException {
                listener.onAllDataRead();
            }

            @Override
            public void onError(Throwable t) {
                listener.onError(t);
            }
        };
    }


  //**************************************************************************
  //** getReadListener
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" ReadListener.
   */
    public static jakarta.servlet.ReadListener getReadListener(
        javax.servlet.ReadListener listener){
        return new jakarta.servlet.ReadListener() {
            @Override
            public void onDataAvailable() throws IOException {
                listener.onDataAvailable();
            }

            @Override
            public void onAllDataRead() throws IOException {
                listener.onAllDataRead();
            }

            @Override
            public void onError(Throwable t) {
                listener.onError(t);
            }
        };
    }


  //**************************************************************************
  //** getWriteListener
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" WriteListener.
   */
    public static javax.servlet.WriteListener getWriteListener(
        jakarta.servlet.WriteListener listener){
        return new javax.servlet.WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                listener.onWritePossible();
            }

            @Override
            public void onError(Throwable t) {
                listener.onError(t);
            }
        };
    };


  //**************************************************************************
  //** getWriteListener
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" WriteListener.
   */
    public static jakarta.servlet.WriteListener getWriteListener(
        javax.servlet.WriteListener listener){
        return new jakarta.servlet.WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                listener.onWritePossible();
            }

            @Override
            public void onError(Throwable t) {
                listener.onError(t);
            }
        };
    };


  //**************************************************************************
  //** getAsyncListener
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" AsyncListener.
   */
    public static javax.servlet.AsyncListener getAsyncListener(
        jakarta.servlet.AsyncListener listener){

        return new javax.servlet.AsyncListener() {
            @Override
            public void onComplete(javax.servlet.AsyncEvent event) throws IOException {
                listener.onComplete(getAsyncEvent(event));
            }

            @Override
            public void onTimeout(javax.servlet.AsyncEvent event) throws IOException {
                listener.onTimeout(getAsyncEvent(event));
            }

            @Override
            public void onError(javax.servlet.AsyncEvent event) throws IOException {
                listener.onError(getAsyncEvent(event));
            }

            @Override
            public void onStartAsync(javax.servlet.AsyncEvent event) throws IOException {
                listener.onStartAsync(getAsyncEvent(event));
            }
        };
    }


  //**************************************************************************
  //** getAsyncListener
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" AsyncListener.
   */
    public static jakarta.servlet.AsyncListener getAsyncListener(
        javax.servlet.AsyncListener listener){

        return new jakarta.servlet.AsyncListener() {
            @Override
            public void onComplete(jakarta.servlet.AsyncEvent event) throws IOException {
                listener.onComplete(getAsyncEvent(event));
            }

            @Override
            public void onTimeout(jakarta.servlet.AsyncEvent event) throws IOException {
                listener.onTimeout(getAsyncEvent(event));
            }

            @Override
            public void onError(jakarta.servlet.AsyncEvent event) throws IOException {
                listener.onError(getAsyncEvent(event));
            }

            @Override
            public void onStartAsync(jakarta.servlet.AsyncEvent event) throws IOException {
                listener.onStartAsync(getAsyncEvent(event));
            }
        };
    }


  //**************************************************************************
  //** getAsyncEvent
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" AsyncEvent.
   */
    public static javax.servlet.AsyncEvent getAsyncEvent(
        jakarta.servlet.AsyncEvent event){
        return new javax.servlet.AsyncEvent(getAsyncContext(event.getAsyncContext()));
    }


  //**************************************************************************
  //** getAsyncEvent
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" AsyncEvent.
   */
    public static jakarta.servlet.AsyncEvent getAsyncEvent(
        javax.servlet.AsyncEvent event){
        return new jakarta.servlet.AsyncEvent(getAsyncContext(event.getAsyncContext()));
    }


  //**************************************************************************
  //** getAsyncContext
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" AsyncContext.
   */
    public static javax.servlet.AsyncContext getAsyncContext(
        jakarta.servlet.AsyncContext ctx){

        return new javax.servlet.AsyncContext() {

            @Override
            public javax.servlet.ServletRequest getRequest() {
                return getServletRequest(ctx.getRequest());
            }

            @Override
            public javax.servlet.ServletResponse getResponse() {
                return getServletResponse(ctx.getResponse());
            }

            @Override
            public boolean hasOriginalRequestAndResponse() {
                return ctx.hasOriginalRequestAndResponse();
            }

            @Override
            public void dispatch() {
                ctx.dispatch();
            }

            @Override
            public void dispatch(String path) {
                ctx.dispatch(path);
            }

            @Override
            public void dispatch(javax.servlet.ServletContext context, String path) {
                ctx.dispatch(getServletContext(context), path);
            }

            @Override
            public void complete() {
                ctx.complete();
            }

            @Override
            public void start(Runnable run) {
                ctx.start(run);
            }

            @Override
            public void addListener(javax.servlet.AsyncListener listener) {
                ctx.addListener(getAsyncListener(listener));
            }

            @Override
            public void addListener(javax.servlet.AsyncListener listener, javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) {
                ctx.addListener(getAsyncListener(listener), getServletRequest(servletRequest), getServletResponse(servletResponse));
            }

            @Override
            public <T extends javax.servlet.AsyncListener> T createListener(Class<T> clazz) throws javax.servlet.ServletException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void setTimeout(long timeout) {
                ctx.setTimeout(timeout);
            }

            @Override
            public long getTimeout() {
                return ctx.getTimeout();
            }
        };
    }


  //**************************************************************************
  //** getAsyncContext
  //**************************************************************************
  /** Returns the "jakarta" equivalent of a "javax" AsyncContext.
   */
    public static jakarta.servlet.AsyncContext getAsyncContext(
        javax.servlet.AsyncContext ctx){

        return new jakarta.servlet.AsyncContext() {

            @Override
            public jakarta.servlet.ServletRequest getRequest() {
                return getServletRequest(ctx.getRequest());
            }

            @Override
            public jakarta.servlet.ServletResponse getResponse() {
                return getServletResponse(ctx.getResponse());
            }

            @Override
            public boolean hasOriginalRequestAndResponse() {
                return ctx.hasOriginalRequestAndResponse();
            }

            @Override
            public void dispatch() {
                ctx.dispatch();
            }

            @Override
            public void dispatch(String path) {
                ctx.dispatch(path);
            }

            @Override
            public void dispatch(jakarta.servlet.ServletContext context, String path) {
                ctx.dispatch(getServletContext(context), path);
            }

            @Override
            public void complete() {
                ctx.complete();
            }

            @Override
            public void start(Runnable run) {
                ctx.start(run);
            }

            @Override
            public void addListener(jakarta.servlet.AsyncListener listener) {
                ctx.addListener(getAsyncListener(listener));
            }

            @Override
            public void addListener(jakarta.servlet.AsyncListener listener, jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse) {
                ctx.addListener(getAsyncListener(listener), getServletRequest(servletRequest), getServletResponse(servletResponse));
            }

            @Override
            public <T extends jakarta.servlet.AsyncListener> T createListener(Class<T> clazz) throws jakarta.servlet.ServletException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public void setTimeout(long timeout) {
                ctx.setTimeout(timeout);
            }

            @Override
            public long getTimeout() {
                return ctx.getTimeout();
            }
        };
    }


  //**************************************************************************
  //** getPushBuilder
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" PushBuilder.
   */
    public static javax.servlet.http.PushBuilder getPushBuilder(
        jakarta.servlet.http.PushBuilder builder){

        return new javax.servlet.http.PushBuilder() {

            @Override
            public javax.servlet.http.PushBuilder method(String method) {
                return getPushBuilder(builder.method(method));
            }

            @Override
            public javax.servlet.http.PushBuilder queryString(String queryString) {
                return getPushBuilder(builder.queryString(queryString));
            }

            @Override
            public javax.servlet.http.PushBuilder sessionId(String sessionId) {
                return getPushBuilder(builder.sessionId(sessionId));
            }

            @Override
            public javax.servlet.http.PushBuilder setHeader(String name, String value) {
                return getPushBuilder(builder.setHeader(name, value));
            }

            @Override
            public javax.servlet.http.PushBuilder addHeader(String name, String value) {
                return getPushBuilder(builder.addHeader(name, value));
            }

            @Override
            public javax.servlet.http.PushBuilder removeHeader(String name) {
                return getPushBuilder(builder.removeHeader(name));
            }

            @Override
            public javax.servlet.http.PushBuilder path(String path) {
                return getPushBuilder(builder.path(path));
            }

            @Override
            public void push() {
                builder.push();
            }

            @Override
            public String getMethod() {
                return builder.getMethod();
            }

            @Override
            public String getQueryString() {
                return builder.getQueryString();
            }

            @Override
            public String getSessionId() {
                return builder.getSessionId();
            }

            @Override
            public Set<String> getHeaderNames() {
                return builder.getHeaderNames();
            }

            @Override
            public String getHeader(String name) {
                return builder.getHeader(name);
            }

            @Override
            public String getPath() {
                return builder.getPath();
            }
        };
    }


  //**************************************************************************
  //** getHttpPart
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" Part.
   */
    public static javax.servlet.http.Part getHttpPart(
        jakarta.servlet.http.Part part){
        return new javax.servlet.http.Part() {
            @Override
            public InputStream getInputStream() throws IOException {
                return part.getInputStream();
            }

            @Override
            public String getContentType() {
                return part.getContentType();
            }

            @Override
            public String getName() {
                return part.getName();
            }

            @Override
            public String getSubmittedFileName() {
                return part.getSubmittedFileName();
            }

            @Override
            public long getSize() {
                return part.getSize();
            }

            @Override
            public void write(String fileName) throws IOException {
                part.write(fileName);
            }

            @Override
            public void delete() throws IOException {
                part.delete();
            }

            @Override
            public String getHeader(String name) {
                return part.getHeader(name);
            }

            @Override
            public Collection<String> getHeaders(String name) {
                return part.getHeaders(name);
            }

            @Override
            public Collection<String> getHeaderNames() {
                return part.getHeaderNames();
            }
        };
    }


  //**************************************************************************
  //** getHttpServletMapping
  //**************************************************************************
  /** Returns the "javax" equivalent of a "jakarta" HttpServletMapping.
   */
    public static javax.servlet.http.HttpServletMapping getHttpServletMapping(
        jakarta.servlet.http.HttpServletMapping mapping){

        return new javax.servlet.http.HttpServletMapping(){

            @Override
            public String getMatchValue() {
                return mapping.getMatchValue();
            }

            @Override
            public String getPattern() {
                return mapping.getPattern();
            }

            @Override
            public String getServletName() {
                return mapping.getServletName();
            }

            @Override
            public javax.servlet.http.MappingMatch getMappingMatch() {
                jakarta.servlet.http.MappingMatch match = mapping.getMappingMatch();

                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public String toString() {
                return mapping.toString();
            }
        };
    }
}