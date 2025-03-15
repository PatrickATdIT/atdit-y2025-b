package atdit.y2025.server;

import atdit.y2025.server.domain.ResponseCode;
import atdit.y2025.server.domain.ServerException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


/**
 * The server takes care of the administrative tasks of a http server
 * has, but does cover application specific functionality.
 * The server is responsible for routing http requests it receives to
 * the defined {@link LoginRequestHandler handler}. The server will send
 * a response back to the client according to the result that the handler
 * creates.
 */
public class Server {
  private final static Logger logger = LoggerFactory.getLogger( Server.class );
  public static final int PORT = 4711;
  public static final String PATH = "/";

  private final HttpServer httpServer;
  private final LoginRequestHandler requestHandler;


  /**
   * Constructs a server instance
   * @param requestHandler - delegation object for application specific
   *                       service calls
   */
  public Server( LoginRequestHandler requestHandler ) {
    this.requestHandler = requestHandler;
    httpServer = initHttpServer( );
  }

  private HttpServer initHttpServer( ) {
    try {
      var httpServer = HttpServer.create( new InetSocketAddress( "127.0.0.1", PORT ), 0 );
      httpServer.createContext( PATH, this::handle );
      httpServer.setExecutor( null );
      return httpServer;
    } catch( IOException e ) {
      logger.error( e.getMessage( ), e );
      throw new RuntimeException( e );
    }
  }

  private void handle( HttpExchange exchange ) {
    try {
      var response = requestHandler.handle( exchange );
      sendResponse( exchange, ResponseCode.OK, response );
    } catch( ServerException e ) {
      sendResponse( exchange, e.getResponseCode( ), e.getMessage( ) );
    }
  }

  private void sendResponse( HttpExchange exchange, ResponseCode responseCode, String response ) {
    try( OutputStream os = exchange.getResponseBody( ) ) {
      var data = response.getBytes( StandardCharsets.UTF_8 );
      exchange.getResponseHeaders( ).set( "Content-Type", "text/plain" );
      exchange.sendResponseHeaders( responseCode.asInt( ), data.length );
      os.write( data );
      os.flush( );
    } catch( IOException e ) {
      logger.error( "Failed to send response", e );
    }
  }

  /**
   * Stops the server instance
   */
  public void stop( ) {
    logger.info( "Shutdown server" );
    httpServer.stop( 0 );
    logger.info( "Server shut down" );
  }

  /**
   * Starts the server instance
   */
  public void start( ) {
    logger.info( "Starting server" );
    httpServer.start( );
    logger.info( "Server started: {}", httpServer.getAddress( ) );
  }
}
