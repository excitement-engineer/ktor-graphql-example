import graphql.schema.GraphQLSchema
import graphql.schema.StaticDataFetcher
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import graphql.schema.idl.TypeRuntimeWiring


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
  mutation: Mutation
}
"""

var runtimeWiring = newRuntimeWiring()
        .type("Query") { builder ->
            builder.dataFetcher("viewer") { env ->
                val authContext = env.getContext<Context>()
                 authContext.account

            }
        }
        .type("Mutation") { builder ->
            builder.dataFetcher("updateName") { env ->
                val account = env.getContext<Context>().account
                        ?: throw ClientException("You must be authenticated to perform this operation")

                val name = env.getArgument<String>("name")

                if (name.isEmpty()) {
                    throw ClientException("You must specify a non-empty name")
                }

                account.name = name
                account
            }
        }
        .build()

var schemaParser = SchemaParser()
var typeDefinitionRegistry: TypeDefinitionRegistry = schemaParser.parse(schemaDef)

var schemaGenerator: SchemaGenerator = SchemaGenerator()
var graphQLSchema: GraphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
