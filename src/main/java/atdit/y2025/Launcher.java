package atdit.y2025;

import atdit.y2025.client.ClientApplication;
import atdit.y2025.server.LoginRequestHandler;
import atdit.y2025.server.Server;


/**
 * Launches the application.
 */
public class Launcher {
  /**
   * Application entry point. It starts both a server and a client instance.
   * The server instance will be a {@link Server} with a handler of type
   * {@link LoginRequestHandler}. The client will be a Java FX
   * {@link ClientApplication}.
   */
  public static void main( String[] args ) {
    Server server = new Server( new LoginRequestHandler( ) );
    server.start( );
    ClientApplication.main( args );
    server.stop( );
  }
}