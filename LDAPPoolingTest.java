import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * https://github.com/rksk/ldap-test
 */
public class LDAPPoolingTest {
    private static int NUMBER_OF_THREADS = 100;

    public static void main(String[] args) {
        if (System.getProperty("NUMBER_OF_THREADS") != null) {
            NUMBER_OF_THREADS = Integer.valueOf(System.getProperty("NUMBER_OF_THREADS"));
        }

        for (int i = 1; i <= NUMBER_OF_THREADS; i++) {
            LDAPTestThread testThread = new LDAPTestThread("Thread " + i);
            testThread.start();
        }
    }
}

class LDAPTestThread extends Thread {

    private String LDAP_URL = "ldap://localhost:10389";
    private String LDAP_USER = "uid=admin,ou=system";
    private String LDAP_PASSWORD = "admin";
    private String LDAP_SEARCH_BASE = "ou=Users,dc=wso2,dc=org";
    private String SEARCH_FILTER = "(&(objectClass=person)(uid=admin))";
    private String ATTRIBUTE_TO_PRINT = "uid";
    private int NUMBER_OF_ITERATIONS = 10;
    private long DELAY_BETWEEN_ITERATIONS = 0; //ms
    private boolean ENABLE_CONNECTION_POOLING = true;
    private boolean ENABLE_DEBUG = true;
    private String threadName;

    LDAPTestThread(String name) {
        threadName = name;
        if (System.getProperty("NUMBER_OF_ITERATIONS") != null) {
            NUMBER_OF_ITERATIONS = Integer.valueOf(System.getProperty("NUMBER_OF_ITERATIONS"));
        }
        if (System.getProperty("DELAY_BETWEEN_ITERATIONS") != null) {
            DELAY_BETWEEN_ITERATIONS = Integer.valueOf(System.getProperty("DELAY_BETWEEN_ITERATIONS"));
        }
        if (System.getProperty("ENABLE_DEBUG") != null) {
            ENABLE_DEBUG = Boolean.valueOf(System.getProperty("ENABLE_DEBUG"));
        }
        if (System.getProperty("ENABLE_CONNECTION_POOLING") != null) {
            ENABLE_CONNECTION_POOLING = Boolean.valueOf(System.getProperty("ENABLE_CONNECTION_POOLING"));
        }
    }

    public void run() {
        if (ENABLE_DEBUG) {
            System.out.println("\n" + threadName + " ============  Test is started  ================");
        }

        Hashtable<String, String> environment = new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.PROVIDER_URL, LDAP_URL);
        environment.put(Context.SECURITY_PRINCIPAL, LDAP_USER);
        environment.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);
        environment.put("com.sun.jndi.ldap.connect.pool", String.valueOf(ENABLE_CONNECTION_POOLING));

        DirContext ctx = null;
        NamingEnumeration<SearchResult> results = null;
        SearchResult searchResult = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss.SSSZ");
        long t1, t2, t3, t4;

        for (int i = 1; i <= NUMBER_OF_ITERATIONS; i++) {

            try {
                t1 = 0;
                t2 = 0;
                t3 = 0;
                t4 = 0;
                //System.out.println("\n\n===== Itertation " + i + " =====");

                t1 = System.currentTimeMillis();
                //System.out.println(sdf.format(new Date(System.currentTimeMillis())));
                //System.out.println("==  Dir context started ==");

                //ctx = new InitialDirContext(environment);
                ctx = new InitialLdapContext(environment,null);

                if (ENABLE_DEBUG) {
                    t2 = System.currentTimeMillis();
                    System.out.println(threadName + "==  Dir context is finished:  " + (t2 - t1) + "ms ==");
                }

                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                results = ctx.search(LDAP_SEARCH_BASE, SEARCH_FILTER, searchControls);

                if (ENABLE_DEBUG) {
                    t3 = System.currentTimeMillis();
                    System.out.println(threadName + "== LDAP Search done: " + (t3 - t2) + "ms ==");
                }

                int index = 1;
                while (results.hasMore()) {
                    try {
                        searchResult = results.next();
                        Attributes attrs = searchResult.getAttributes();
                        if (ENABLE_DEBUG) {
                            System.out.println(index++ + ". " + attrs.get(ATTRIBUTE_TO_PRINT));
                        }
                    } catch (Exception e) {
                        System.out.println(threadName + "An error occurred");
                        e.printStackTrace();
                    }
                }

                if (ENABLE_DEBUG) {
                    t4 = System.currentTimeMillis();
                    System.out.println(threadName + "== Results printing done: " + (t4 - t3) + "ms ==");
                }

            } catch (NamingException e) {
                System.out.println("An error occurred");
                e.printStackTrace();
            } finally {
                if (results != null) {
                    try {
                        results.close();
                    } catch (NamingException e) {
                        e.printStackTrace();
                    }
                }
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                Thread.sleep(DELAY_BETWEEN_ITERATIONS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        if (ENABLE_DEBUG) {
            System.out.println(threadName + "=====  Test is finished =====");
        }
    }

}