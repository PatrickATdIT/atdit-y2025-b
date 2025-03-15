package atdit.y2025.client.domain;

/**
 * Data carry object for server responses.
 * @param responseCode the HTTP response's code
 * @param message the response's header message
 * @param payload the response's payload
 */
public record ServiceResponse( int responseCode, String message, String payload ) {
}
