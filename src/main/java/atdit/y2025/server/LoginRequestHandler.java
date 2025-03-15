package atdit.y2025.server;

import atdit.y2025.server.domain.ResponseCode;
import atdit.y2025.server.domain.ServerException;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Handles login requests. See method {@link #handle(HttpExchange)} for details.
 */
public class LoginRequestHandler {
  private record Credentials( String username, String password ) { }
  Logger logger = LoggerFactory.getLogger( LoginRequestHandler.class );


  /**
   * <p>Accepts requests for the login service and returns the user's role
   * in case the login attempt is successful or an exception otherwise.</p>
   * <p>The request must query the login service (/login) as GET request.
   * Furthermore, parameters <i>user</i> and <i>password</i> must be
   * supplied in GET conform format. If the user and password match a
   * known user, login will succeed. In case any of the requirements are
   * not met or the user/password combination is unknown, an exception
   * will be raised</p>
   *
   * @param exchange incoming exchange object
   * @return user's role if login succeeds
   * @throws ServerException in case a precondition is not met or
   *                         the user/password combination is wrong
   */
  public String handle( HttpExchange exchange ) throws ServerException {
    logger.trace( "Received request: {}", exchange.getRequestURI( ) );
    checkService( exchange );
    checkMethod( exchange );
    Map<String, String> parameters = extractParameters( exchange );
    var credentials = getCredentials( parameters );
    var role = login( credentials );
    return role;
  }

  private String login( Credentials credentials ) throws ServerException {
    logger.trace( "Login attempt: {}/{}", credentials.username, credentials.password );
    if( Objects.equals( credentials.username, "admin" ) &&
        Objects.equals( credentials.password, "admin" ) ) {
      return "Administrator";
    } else if( Objects.equals( credentials.username, "user" ) &&
               Objects.equals( credentials.password, "user" ) ) {
      return "Normal User";
    } else if( Objects.equals( credentials.username, "smart_user" ) &&
               Objects.equals( credentials.password, "ATdIT2025" ) ) {
      return "Smart User";
    } else {
      throw new ServerException( ResponseCode.Unauthorized, "Unauthorized" );
    }
  }

  private Credentials getCredentials( Map<String, String> parameters ) {
    return new Credentials(
      parameters.get( "user" ),
      parameters.get( "password" ) );
  }


  /**
   * Extracts the supplied parameters required for the login service.
   * Parameters must be supplied in format of get query.
   *
   * @param exchange incoming exchange object
   * @return a map of structure parameter => value
   * @throws ServerException in case of unexpected parameter format
   */
  private Map<String, String> extractParameters( HttpExchange exchange ) throws ServerException {
    Map<String, String> result = new HashMap<>( );
    var splits = splitQueryParameters( exchange.getRequestURI( ).getQuery( ) );
    for( var split : splits ) {
      var pair = splitQueryParameterKeyValue( split );
      result.put( pair[ 0 ], pair[ 1 ] );
    }
    return result;
  }

  private String[] splitQueryParameterKeyValue( String split ) throws ServerException {
    var result = split.split( "=" );
    if( result.length != 2 ) {
      var message = "Parameter wrong format (expected key = value): " + split;
      logger.error( message );
      throw new ServerException( ResponseCode.BadRequest, message );
    }
    return result;
  }

  private String[] splitQueryParameters( String query ) throws ServerException {
    try {
      return Objects.requireNonNull( query ).split( "&" );
    } catch( NullPointerException e ) {
      var message = "No query parameter found in request";
      logger.error( message );
      throw new ServerException( ResponseCode.BadRequest, message );
    }
  }

  /**
   * This implementation only allows GET queries.
   *
   * @param exchange incoming exchange object
   * @throws ServerException thrown if the service request method is not
   *                         equal to GET
   */
  private void checkMethod( HttpExchange exchange ) throws ServerException {
    if( !Objects.equals( exchange.getRequestMethod( ), "GET" ) ) {
      logger.error( "http method not supported: {}", exchange.getRequestMethod( ) );
      throw new ServerException( ResponseCode.MethodNotAllowed, "GET-Method required" );
    }
  }

  /**
   * Checks whether the queried service is the login service
   *
   * @param exchange incoming exchange object
   * @throws ServerException thrown if another service but login service
   *                         is queried
   */
  private void checkService( HttpExchange exchange ) throws ServerException {
    if( !Objects.equals( exchange.getRequestURI( ).getPath( ), "/login" ) ) {
      logger.error( "service not found: {}", exchange.getRequestURI( ).getPath( ) );
      throw new ServerException( ResponseCode.NotFound, "Not Found" );
    }
  }
}
