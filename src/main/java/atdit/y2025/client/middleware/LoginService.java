package atdit.y2025.client.middleware;

import atdit.y2025.client.domain.LoginFailedException;
import atdit.y2025.client.domain.ServiceException;
import atdit.y2025.client.domain.ServiceResponse;
import atdit.y2025.client.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;


public class LoginService {
  private static final Logger logger = LoggerFactory.getLogger( LoginService.class );
  public static final String LOGIN_SERVICE_PATH = "http://localhost:4711/login";
  public static final String REQUEST_METHOD_GET = "GET";
  public static final int CONNECTION_TIMEOUT_VALUE = 10_000;

  /**
   * Call central login service
   *
   * @return A {@link User} object in case the logon was successful
   * @throws ServiceException     In case the connection to the service could not be established or
   *                              the service responded with a code different to 200 - OK or 401 - Unauthorized.
   * @throws LoginFailedException In case the service responded 401 - Unauthorized; this means user
   *                              or password are incorrect
   */
  public User login( String user, String password ) throws ServiceException, LoginFailedException {
    logger.info( "login attempt" );
    logger.trace( "user: {}", user );
    logger.trace( "password: {}", password );

    var response = callLoginService( user, password );
    logger.trace( "Response Code {}", response.responseCode( ) );
    logger.trace( "Response Message {}", response.message( ) );
    logger.trace( "Response: {}", response.payload( ) );

    switch( response.responseCode( ) ) {
      case HttpURLConnection.HTTP_OK:
        logger.info( "Login Successful" );
        return new User( user, response.payload( ) );
      case HttpURLConnection.HTTP_UNAUTHORIZED:
        logger.info( "Login Failed" );
        throw new LoginFailedException( );
      default:
        logger.error( "Service error occurred" );
        throw new ServiceException( response.responseCode( ), response.message( ) );
    }
  }

  private ServiceResponse callLoginService( String user, String password ) throws ServiceException {
    var connection = makeServiceConnection( user, password );
    var response = queryService( connection );
    return response;
  }

  private ServiceResponse queryService( HttpURLConnection connection ) throws ServiceException {
    try( BufferedReader in = connectToStream( connection ) ) {
      var payload = new StringBuilder( );
      String inputLine;
      while( ( inputLine = in.readLine( ) ) != null ) {
        payload.append( inputLine );
      }

      var response = new ServiceResponse(
        connection.getResponseCode( ),
        connection.getResponseMessage( ),
        payload.toString( ) );
      return response;
    } catch( IOException e ) {
      throw new ServiceException( "Login service communication error", e );
    } finally {
      connection.disconnect( );
    }
  }

  private BufferedReader connectToStream( HttpURLConnection connection ) throws IOException {
    InputStream is = connection.getResponseCode( ) == HttpURLConnection.HTTP_OK ?
      connection.getInputStream( ) : connection.getErrorStream( );
    return new BufferedReader( new InputStreamReader( is ) );
  }

  private HttpURLConnection makeServiceConnection( String user, String password ) throws ServiceException {
    String queryString = "?user=" + password +
                         "&password=" + user;

    try {
      var serviceURL = URI.create( LOGIN_SERVICE_PATH + queryString ).toURL( );
      logger.trace( "serviceURL: {}", serviceURL );
      var connection = (HttpURLConnection) serviceURL.openConnection( );
      connection.setRequestMethod( REQUEST_METHOD_GET );
      connection.setConnectTimeout( CONNECTION_TIMEOUT_VALUE );
      return connection;
    } catch( IOException e ) {
      throw new ServiceException( "Connection to login service could not be created", e );
    }
  }
}
