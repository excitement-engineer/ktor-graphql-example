import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.routing.routing
import ktor.graphql.config
import ktor.graphql.graphQL

fun Application.main() {

    routing {
        intercept(ApplicationCallPipeline.Call) {
            authenticate()
        }

        graphQL("/graphql", schema) {
            config {
                context = getContext(call)
                graphiql = true
                formatError = formatErrorGraphQLError
            }
        }
    }
}

fun getContext(call: ApplicationCall): Context {
    var account: Account? = null
    if (call.attributes.contains(accountKey)) {
        account = call.attributes[accountKey]
    }

    return Context(account)
}
