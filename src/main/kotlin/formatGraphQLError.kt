import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError

fun formatError(error: GraphQLError): Map<String, Any> {
    val message = if (error is ExceptionWhileDataFetching) {

        val originalException = error.exception

        val formattedMessage = if (originalException is ClientError) {
            originalException.message
        } else {
            "Internal server error"
        }

        formattedMessage
    } else {
        error.message
    }

    val result = error.toSpecification()
    result["message"] = message

    return result
}