import graphql.ExecutionInput
import graphql.GraphQL
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.routing.routing
import ktor.graphql.Config
import ktor.graphql.fromRequest
import ktor.graphql.graphQL

fun Application.main() {

    val graphql = GraphQL.newGraphQL(graphQLSchema).build()

    routing {
        intercept(ApplicationCallPipeline.Call) {
            authenticate()
        }

        graphQL("/graphql", graphQLSchema) { request ->
            Config(
                    showExplorer = true,
                    formatError = formatErrorGraphQLError,
                    executeRequest = {
                        val input = ExecutionInput
                                .newExecutionInput()
                                .fromRequest(request)
                                .context(getContext(call))

                        graphql.execute(input)
                    }
            )
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
