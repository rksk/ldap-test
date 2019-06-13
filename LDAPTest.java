import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

/**
 * Created by soasecurity.org on 9/15/15.
 */
public class LDAPTest {

    private static String LDAP_URL = "ldap://localhost:10389";

    private static String LDAP_USER = "uid=admin, ou=system";

    private static String LDAP_PASSWORD = "admin";

    private static String LDAP_SEARCH_BASE = "ou=users,dc=wso2,dc=org";
    
    private static String SEARCH_FILTER = "(&(objectCategory=Person)(objectClass=User)(|(mail=admin@az.gov)(sAMAccountName=admin@az.gov)))";
    
    private static String TRSUT_STORE_PATH = "/home/asela/is/530/wso2is-5.3.0/repository/resources/security/client-truststore.jks";
    
    private static String TRSUT_STORE_PASSWORD = "wso2carbon.jks";

    public static void main(String[] args){
    
        System.setProperty("javax.net.ssl.trustStore", TRSUT_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", TRSUT_STORE_PASSWORD);
        
        System.out.println("============= Test is started ==============");

        Hashtable<String, String > environment = new Hashtable<String, String >();

        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.REFERRAL, "follow");
        environment.put(Context.PROVIDER_URL, LDAP_URL);
        environment.put(Context.SECURITY_PRINCIPAL, LDAP_USER);
        environment.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);

        DirContext ctx = null;
        NamingEnumeration<SearchResult> results = null;
        try {
            ctx = new InitialDirContext(environment);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            long t1 =System.currentTimeMillis();
            
            results = ctx.search(LDAP_SEARCH_BASE, SEARCH_FILTER, searchControls);
            while (results.hasMore()){
                SearchResult searchResult = null;             
                try {
                    searchResult = results.next();
                    System.out.println("============= Search Result is found ==============");
                } catch (Exception e) {
                    System.out.println("ERROR is occurred with Next");
                    e.printStackTrace();
                }
            }
            long t2 =System.currentTimeMillis();
            
		  System.out.println("============= Time to Search : " + (t2-t1) + " ==============");
        } catch (NamingException e) {
            System.out.println("===================== ERROR is occurred ================");
            e.printStackTrace();
        }finally {
            if(results != null) {
                try {
                    results.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
            if(ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("======================= Test is finished =====================");
        System.exit(0);
    }
}
