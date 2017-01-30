package javaxt.http.servlet;

public class HttpSession {
    
    private javax.servlet.http.HttpSession session;
    
    protected HttpSession(javax.servlet.http.HttpSession session){
        this.session = session;
    }
    
    
//  //**************************************************************************
//  //** getServletContext
//  //**************************************************************************
//  /** Returns the ServletContext.
//   */
//    public ServletContext getServletContext(){
//        return session.getServletContext();
//    }


  //**************************************************************************
  //** getID
  //**************************************************************************
  /** Returns a string containing the unique identifier assigned to this
   *  session.
   */
    public String getID(){
        return session.getId();
    }


  //**************************************************************************
  //** getCreationTime
  //**************************************************************************
  /** Returns the time when this session was created, measured in milliseconds 
   *  since midnight January 1, 1970 GMT. 
   */
    public long getCreationTime(){
        return session.getCreationTime();
    }


  //**************************************************************************
  //** getLastAccessedTime
  //**************************************************************************
  /** Returns the time when this session was last accessed, measured in
   *  milliseconds since midnight January 1, 1970 GMT.
   */
    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }


  //**************************************************************************
  //** isNew
  //**************************************************************************
  /** Returns true if the client does not yet know about the session or if the
   *  server has not accessed the session.
   */
    public boolean isNew(){
        return session.isNew();
    }


  //**************************************************************************
  //** getAttribute
  //**************************************************************************
  /** Returns the object bound with the specified name in this session, or
   *  null if no object is bound under the name.
   */
    public Object getAttribute(String name){
        return session.getAttribute(name);
    }


  //**************************************************************************
  //** setAttribute
  //**************************************************************************
  /** Binds an object to this session, using the name specified.
   */
    public void setAttribute(String name, Object value){
        session.setAttribute(name, value);
    }


  //**************************************************************************
  //** removeAttribute
  //**************************************************************************
  /** Removes the attribute with the given name from the servlet context. */
    public void removeAttribute(String name){
        session.removeAttribute(name);
    }


  //**************************************************************************
  //** invalidate
  //**************************************************************************
  /** Invalidates this session then unbinds any objects bound to it.
   */
    public void invalidate(){
        session.invalidate();
    }


//
//    /** Generates a random sequence of alpha-numeric characters. */
//    private static final String CreateID(int len){
//        StringBuffer str = new StringBuffer(len);
//        final String strValid = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        for (int i=1; i<=len; i++){
//             int x = new java.util.Random().nextInt(strValid.length());
//             str.append( strValid.substring(x,x+1) );
//        }
//        return str.toString();
//    }

    public String toString(){
        return getID();
    }
}