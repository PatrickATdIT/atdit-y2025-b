package atdit.y2025.client.domain;


/**
 * Exception to be thrown if the login service responded with any other
 * response code but 200 or 401, or if the service could not be called at
 * all.
 */
public class ServiceException extends Exception {
  final int httpReturnCode;

  public ServiceException( int httpReturnCode, String message ) {
    super( message );
    this.httpReturnCode = httpReturnCode;
  }

  public ServiceException( String message, Throwable cause ) {
    super( message, cause );
    this.httpReturnCode = -1;
  }

  @Override
  public String getMessage( ) {
    if( httpReturnCode != -1 ) {
      return "Service response: " + super.getMessage( ) + " (" + httpReturnCode + ")";
    } else return super.getMessage( );
  }
}
