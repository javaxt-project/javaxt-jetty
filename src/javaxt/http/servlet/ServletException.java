package javaxt.http.servlet;

//******************************************************************************
//**  ServletException
//******************************************************************************
/**
 *   Defines a general exception a servlet can throw when it encounters an
 *   invalid request or error.
 *
 ******************************************************************************/

public class ServletException extends Exception {

    private int statusCode = 400;

    public ServletException() {
        this(400);
    }

    public ServletException(String message) {
        this(400, message);
    }

    public ServletException(String message, Exception e) {
        this(400, message, e);
    }

    public ServletException(int statusCode) {
        this(statusCode, HttpServletResponse.getStatusMessage(statusCode));
    }

    public ServletException(int statusCode, String message) {
        this(statusCode, message, null);
    }

    public ServletException(int statusCode, String message, Exception e) {
        super((message==null ? HttpServletResponse.getStatusMessage(statusCode) : message), e);
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return statusCode;
    }

}