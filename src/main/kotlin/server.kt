import io.ktor.application.*
import io.ktor.routing.route
import io.ktor.routing.routing
import ktor.graphql.config
import ktor.graphql.graphQL

fun Application.main() {

    routing {
        route("/graphql") {

            intercept(ApplicationCallPipeline.Call) {
                authenticate()
            }

            graphQL(schema) {
                config {
                    context = getContext(call)
                    graphiql = true
                    formatError = { formatError(this) }
                }
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
