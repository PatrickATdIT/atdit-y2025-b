package atdit.y2025.server.domain;


/**
 * An exception type class that is designed to be used within HTTP server
 * applications. It can carry the error message and corresponding HTTP
 * response code in form of a {@link ResponseCode}.
 */
public class ServerException extends Exception {
  private final ResponseCode responseCode;

  public ServerException( ResponseCode responseCode, String message ) {
    super( message );
    this.responseCode = responseCode;
  }

  public ResponseCode getResponseCode( ) {
    return responseCode;
  }
}
