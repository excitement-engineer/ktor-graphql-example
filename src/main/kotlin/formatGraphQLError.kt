import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError

val formatErrorGraphQLError: (GraphQLError.() -> Map<String, Any>) =  {
    val clientMessage = if (this is ExceptionWhileDataFetching) {

        val formattedMessage = if (exception is ClientException) {
            exception.message
        } else {
            "Internal server error"
        }

        formattedMessage
    } else {
        message
    }

    val result = toSpecification()
    result["message"] = clientMessage
    result
}