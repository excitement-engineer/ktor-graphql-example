import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.SchemaParser
import graphql.schema.DataFetchingEnvironment

private const val schemaDef = """

type Account {
    id: String
    name: String
}

type Query {
    viewer: Account
}

type Mutation {
    updateName(name: String!): Account
}

schema {
  query: Query
}
"""

val schema = SchemaParser
        .newParser()
        .schemaString(schemaDef)
        .resolvers(Query(), Mutation())
        .build()
        .makeExecutableSchema()

private class Query: GraphQLQueryResolver {
    fun viewer(env: DataFetchingEnvironment): Account? {
        val authContext = env.getContext<Context>()
        return authContext.account
    }
}

private class Mutation: GraphQLMutationResolver {

    fun updateName(name: String, env: DataFetchingEnvironment): Account {

        val account = env.getContext<Context>().account

        if (account == null) {
            throw ClientError("You must be authenticated to perform this operation")
        }

        if (name.isEmpty()) {
            throw ClientError("You must specify a non-empty name")
        }

        account.name = name

        return account
    }
}