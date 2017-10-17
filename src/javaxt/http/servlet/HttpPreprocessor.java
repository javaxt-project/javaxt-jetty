package javaxt.http.servlet;

//******************************************************************************
//**  HttpPreprocessor Interface
//******************************************************************************
/**
 *   Implementations of this class are used to process HTTP requests before
 *   they are passed to an HttpServlet.
 *
 ******************************************************************************/

public interface HttpPreprocessor {
    
    
  //**************************************************************************
  //** init
  //**************************************************************************
  /** Used to initialize the preprocessor on server start-up.
   */
    public void init(Object obj);


  //**************************************************************************
  //** processRequest
  //**************************************************************************
  /** Used to process HTTP requests before they are passed to an HttpServlet. 
   *  Return true if the request has been processed and should NOT be handled 
   *  by any other Preprocessors or the HttpServlet.
   */
    public boolean processRequest(
        javax.servlet.http.HttpServletRequest request, 
        javax.servlet.http.HttpServletResponse response
    );
    
}