import javax.jcr.Session
import javax.jcr.query.Query
import javax.jcr.query.QueryManager
import javax.jcr.query.QueryResult
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ResourceResolverFactory
import org.apache.sling.api.resource.Resource

ResourceResolverFactory resourceResolverFactory = getService(org.apache.sling.api.resource.ResourceResolverFactory)
def resourceResolver = null
def session = null

try {
    // Define the service user map
    def serviceUserMap = ["user.jcr.session": "datawrite"]

    // Get the ResourceResolver using the service user
    resourceResolver = resourceResolverFactory.getServiceResourceResolver(serviceUserMap)
    session = resourceResolver.adaptTo(Session.class)

    // Define the query to get all user nodes under /home/users
    String queryString = "SELECT * FROM [rep:User] WHERE ISDESCENDANTNODE('/home/users')"
    QueryManager queryManager = session.workspace.queryManager
    Query query = queryManager.createQuery(queryString, Query.JCR_SQL2)

    // Execute the query
    QueryResult queryResult = query.execute()
    def nodes = queryResult.nodes

    // Iterate over the results and print user details
    println "User Path, User ID"
    while (nodes.hasNext()) {
        def node = nodes.nextNode()
        println "User Path: ${node.path}, User ID: ${node.getProperty('rep:principalName').string}"
    }
} catch (Exception e) {
    e.printStackTrace()
} finally {
    if (session != null) {
        session.logout()
    }
    if (resourceResolver != null) {
        resourceResolver.close()
    }
}
