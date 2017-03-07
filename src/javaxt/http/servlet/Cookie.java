package javaxt.http.servlet;

//******************************************************************************
//**  Cookie Class
//******************************************************************************
/**
 *   Creates a cookie, a small amount of information sent by a servlet to a
 *   Web browser, saved by the browser, and later sent back to the server. A
 *   cookie's value can uniquely identify a client, so cookies are commonly
 *   used for session management.
 *
 ******************************************************************************/

public class Cookie {

    javax.servlet.http.Cookie cookie;

    
    public Cookie(String name, String value) {
        this(new javax.servlet.http.Cookie(name, value));
    }
    
    public Cookie(javax.servlet.http.Cookie cookie){
        this.cookie = cookie;
    }


    public void setPath(String path){
        cookie.setPath(path);
    }

    public String getPath(){
        return cookie.getPath();
    }

    public String getName(){
        return cookie.getName();
    }

    public String getValue(){
        return cookie.getValue();
    }

    public void setMaxAge(int deltaSeconds) {
        cookie.setMaxAge(deltaSeconds);
    }
//    private void setExpires(java.util.Date expires) {
//        cookie.s
//    }
    public void setSecure(boolean secure) {
        cookie.setSecure(secure);
    }
    public void setComment(String comment) {
        cookie.setComment(comment);
    }
    public void setVersion(int newVersion) {
        cookie.setVersion(newVersion);
    }

    public String toString(){
        
        String name = getName();
        String value = getValue();
        String path = getPath();
        
        StringBuffer str;
        if (value==null){
            str = new StringBuffer(name.length() + 1);
            str.append(name);
            str.append(";");
        }
        else{
            str = new StringBuffer(name.length() + value.length() + 2);
            str.append(name);
            str.append("=");
            str.append(value);
            str.append(";");

            if (path!=null){
                str.append(" Path=");
                str.append(path);
            }
        }
        //JSESSIONID=1f3de3ba66697674ff4a07bc561e; Path=/JavaXT
        return str.toString();
    }

    public int hashCode(){
        return cookie.hashCode();
    }

    public boolean equals(Object obj){
        return cookie.equals(obj);
    }
    
    public javax.servlet.http.Cookie getCookie(){
        return cookie;
    }
}