import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext

val accountKey = AttributeKey<Account>("account")

suspend fun PipelineContext<Unit, ApplicationCall>.authenticate() {

    val accountId = call.request.queryParameters["access_token"]

    if (accountId != null) {
        val account = AccountRepository.findById(accountId)

        if (account != null) {
            call.attributes.put(accountKey, account)
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Authentication failed")
            finish()
        }
    }
}