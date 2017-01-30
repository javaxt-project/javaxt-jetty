package javaxt.http.servlet;
import java.io.IOException;

//******************************************************************************
//**  ServletInputStream
//******************************************************************************
/**
 *   Provides an input stream for reading the body of an http request. Reads
 *   raw bytes from a socket connection. Automatically decrypts the data if
 *   the data is SSL/TLS encrypted.
 *
 ******************************************************************************/

public class ServletInputStream extends java.io.InputStream {

    private javax.servlet.ServletInputStream is;



  //**************************************************************************
  //** Constructor
  //**************************************************************************
    protected ServletInputStream(javax.servlet.ServletInputStream is){
        this.is = is;
    }


  //**************************************************************************
  //** available
  //**************************************************************************
  /** Returns an estimate of the number of bytes that can be read.
   */
    public int available() throws java.io.IOException {
        return is.available();
    }


  //**************************************************************************
  //** markSupported
  //**************************************************************************
  /** Returns false. This stream does not support the mark and reset methods.
   */
    public boolean markSupported(){
        return is.markSupported();
    }


  //**************************************************************************
  //** read
  //**************************************************************************
  /** Reads the next byte of data from the socket. The byte is returned as a
   *  positive integer. If the end of the stream has been reached, a value of
   *  -1 is returned.
   */
    public int read() throws IOException {
        return is.read();
    }

    /*
    public int readLine(byte[] b, int off, int len) throws IOException {
        int totalBytesRead = 0;
        while (true){

            totalBytesRead++;
            if (totalBytesRead==len) break;
        }
        return totalBytesRead;
    }
    */


  //**************************************************************************
  //** readLine
  //**************************************************************************
  /** Returns a sequence of bytes from the socket. Stops when it reaches a 
   *  carriage return + line feed (CRLF) or the end of stream, whichever comes
   *  first. If a CRLF is reached, the CRLF will be added to the array.
   */
    public byte[] readLine() throws IOException {

        java.io.ByteArrayOutputStream bas = new java.io.ByteArrayOutputStream();
        while (true){

            byte a = (byte) read();
            if (a==-1) break;


            if (a=='\r') {

                byte b = (byte) read();
                if (b==-1) break;


                if (b=='\n') {

                    bas.write(b);
                    break;
                }
                else if (b > -1) {
                    bas.write(a);
                    bas.write(b);
                }
            }
            else if (a > -1) {
                bas.write(a);
            }

        }
        return bas.toByteArray();
    }

}