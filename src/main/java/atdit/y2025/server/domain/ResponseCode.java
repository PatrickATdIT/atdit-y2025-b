package atdit.y2025.server.domain;


/**
 * Enumeration of relevant HTTP response codes for the server application.
 */
public enum ResponseCode {
  OK( 200 ),
  BadRequest( 400 ),
  Unauthorized( 401 ),
  NotFound( 404 ),
  MethodNotAllowed( 405 ),
  InternalServerError( 500 );

  private final int rc;

  ResponseCode( int rc ) {
    this.rc = rc;
  }

  public int asInt( ) {
    return rc;
  }

}
