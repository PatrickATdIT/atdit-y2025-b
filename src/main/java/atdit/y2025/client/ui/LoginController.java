package atdit.y2025.client.ui;

import atdit.y2025.client.domain.LoginFailedException;
import atdit.y2025.client.domain.ServiceException;
import atdit.y2025.client.domain.User;
import atdit.y2025.client.middleware.LoginService;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JFX specific UI Controller for the login screen
 * Besides managing the screen, this class also is responsible for
 * calling the login service on the connected application server.
 */
public class LoginController {
  private static final Logger logger = LoggerFactory.getLogger( LoginController.class );

  public PasswordField passwordTextField;
  public TextField userTextField;

  /**
   * <p>JFX specific action handler for clicks on the login button.</p>
   * <p>Handles login requests by calling the login service on the connected
   * application server and opens the application in case of successful
   * login attempt.</p>
   * <p>Attention: Current implementation just indicates successful or
   * failed login attempts through JFX {@link Alert Alterts}.</p>
   *
   * @param actionEvent - actionEvent provided by JFX framework
   */
  public void clickOnLogin( @SuppressWarnings( "unused" ) ActionEvent actionEvent ) {
    logger.trace( "clickOnLogin" );
    try {
      var user = new LoginService( ).login(
        userTextField.getText( ),
        passwordTextField.getText( ) );
      onSuccessfulLogin( user );
    } catch( LoginFailedException e ) {
      onFailedLogin( );
    } catch( ServiceException e ) {
      onServiceException( e );
    }
  }

  private void onFailedLogin( ) {
    logger.info( "Login failed for user {}", userTextField.getText( ) );
    Alert alert = new Alert( Alert.AlertType.ERROR );
    alert.setTitle( "Login Failed" );
    alert.setHeaderText( "Login Failed" );
    alert.setContentText( "Your login attempt failed. Please try again." );
    alert.showAndWait( );
  }

  private void onServiceException( ServiceException e ) {
    logger.error( e.getMessage( ), e );
    Alert alert = new Alert( Alert.AlertType.ERROR );
    alert.setTitle( "Service Error" );
    alert.setHeaderText( "Service Error" );
    alert.setContentText( "Login service responded unexpectedly. " + e.getMessage( ) );
    alert.showAndWait( );
  }

  private void onSuccessfulLogin( User user ) {
    logger.info( "User {} logged in as {}", user.username( ), user.role( ) );
    Alert alert = new Alert( Alert.AlertType.INFORMATION );
    alert.setTitle( "Login Successful" );
    alert.setHeaderText( "Login Successful" );
    alert.setContentText( "Welcome " + user.username( ) + ". You are logged in as " + user.role( ) );
    alert.showAndWait( );
  }
}
