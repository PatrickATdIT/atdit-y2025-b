package atdit.y2025.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * A client application implemented using Java FX.
 */
public class ClientApplication extends Application {
  public static void main( String[] args ) {
    launch( args );
  }

  @Override
  public void start( Stage primaryStage ) throws Exception {
    Parent loginScreen = FXMLLoader.load( Objects.requireNonNull( getClass( ).getResource( "/login.fxml" ) ) );
    var scene = new Scene( loginScreen );
    primaryStage.setScene( scene );
    primaryStage.setTitle( "ATdIT 2025 Demo Client" );
    primaryStage.show( );
  }

}
