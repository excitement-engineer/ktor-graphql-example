import com.google.gson.Gson
import graphQLRoute.removeWhitespace
import graphQLRoute.urlString
import io.ktor.application.Application
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import kotlin.test.assertEquals

class GraphQLTest {

    private val viewerQuery = "{ viewer { id name } }"
    private val updateNameMutation = """
            mutation updateName(${"$"}name: String!) {
              updateName(name: ${"$"}name) {
                id
                name
              }
            }
    """.replace("\n", "")

    private val gson = Gson()


    @Test
    fun `executes the viewer query`() = withTestApplication(Application::main) {

        with(handleRequest {
            uri = urlString(
                    "query" to viewerQuery,
                    Pair("access_token", "1")
            )
            method = HttpMethod.Get
            addHeader(HttpHeaders.Accept, "application/json,text/html")

        }) {
            assertEquals(expected = HttpStatusCode.OK, actual = response.status())

            val output = """
                {
                    "data": {
                        "viewer": {
                            "id": "1",
                            "name": "Tom"
                        }
                    }
                }
            """
            assertEquals(expected = removeWhitespace(output), actual = response.content)
        }
    }

    @Test
    fun `returns null if no authenticated viewer present`() = withTestApplication(Application::main) {
        with(handleRequest {
            uri = urlString("query" to viewerQuery)
            method = HttpMethod.Get
        }) {
            assertEquals(expected = HttpStatusCode.OK, actual = response.status())

            val output = """
                {
                    "data": {
                        "viewer": null
                    }
                }
            """
            assertEquals(expected = removeWhitespace(output), actual = response.content)
        }
    }

    @Test
    fun `updates a viewer using a mutation`() = withTestApplication(Application::main) {
        with(handleRequest {
            uri = urlString("access_token" to "1")
            method = HttpMethod.Post

            val body = mapOf(
                    "query" to updateNameMutation,
                    "variables" to mapOf(
                            "name" to "Joseph"
                    )
            )

            setBody(gson.toJson(body))
            addHeader(HttpHeaders.ContentType, "application/json")

        }) {
            assertEquals(expected = HttpStatusCode.OK, actual = response.status())

            val output = """
                {
                    "data": {
                        "updateName": {
                            "id": "1",
                            "name": "Joseph"
                        }
                    }
                }
            """

            assertEquals(
                    expected = removeWhitespace(output),
                    actual = response.content
            )
        }
    }

    @Test
    fun `errors if an empty name is passed to the update viewer function`() = withTestApplication(Application::main) {
        with(handleRequest {
            uri = urlString("access_token" to "1")
            method = HttpMethod.Post

            val body = mapOf(
                    "query" to updateNameMutation,
                    "variables" to mapOf(
                            "name" to ""
                    )
            )
            setBody(gson.toJson(body))

            addHeader(HttpHeaders.ContentType, "application/json")

        }) {
            assertEquals(expected = HttpStatusCode.OK, actual = response.status())

            val output = """
                {
                   "data":{
                      "updateName":null
                   },
                   "errors":[
                      {
                         "message":"You must specify a non-empty name",
                         "locations":[
                            {
                               "line":1,
                               "column":64
                            }
                         ],
                         "path":[
                            "updateName"
                         ]
                      }
                   ]
                }
            """

            assertEquals(
                expected = removeWhitespace(output),
                actual = response.content
            )

        }
    }

    @Test
    fun `errors if an unauthenticated user tries to update the name`() = withTestApplication(Application::main) {
        with(handleRequest {
            uri = urlString()
            method = HttpMethod.Post

            val body = mapOf(
                    "query" to updateNameMutation,
                    "variables" to mapOf(
                            "name" to ""
                    )
            )
            setBody(gson.toJson(body))

            addHeader(HttpHeaders.ContentType, "application/json")
        }) {
            assertEquals(expected = HttpStatusCode.OK, actual = response.status())

            val output = """
                {
                   "data":{
                      "updateName":null
                   },
                   "errors":[
                      {
                         "message":"You must be authenticated to perform this operation",
                         "locations":[
                            {
                               "line":1,
                               "column":64
                            }
                         ],
                         "path":[
                            "updateName"
                         ]
                      }
                   ]
                }
            """
            assertEquals(
                    expected = removeWhitespace(output),
                    actual = response.content
            )
        }
    }

}