package atdit.y2025.client.domain;

/**
 * A user record that is to be created after successful login and
 * to be used within the whole application
 * @param username the user's name
 * @param role the user's role
 */
public record User( String username, String role ) {
}
