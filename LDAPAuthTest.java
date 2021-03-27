import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * A simple LDAP bind application.
 */
public class LDAPAuthTest {

    public static void main(String[] args) {
        if (args == null || args.length != 3) {
            System.err.println("Simple LDAP authenticator");
            System.err.println();
            System.err.println("Usage:");
            System.err.println("\tjava LDAPAuthTest <ldapURL> <userDN> <password>");
            System.err.println();
            System.err.println("Example:");
            System.err.println("\tjava LDAPAuthTest ldap://localhost:10389 uid=admin,ou=Users,dc=wso2,dc=org admin");
            System.exit(1);
        }

        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, args[0]);
        env.put(Context.SECURITY_PRINCIPAL, args[1]);
        env.put(Context.SECURITY_CREDENTIALS, args[2]);
        try {
            final LdapContext ctx = new InitialLdapContext(env, null);
            System.out.println("User is authenticated");
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
