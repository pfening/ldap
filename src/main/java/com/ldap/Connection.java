package com.ldap;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;


public class Connection {
    static DirContext getContext(String pass) throws NamingException {
        Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            properties.put(Context.PROVIDER_URL, "ldap://edsldap.com:389");
            properties.put(Context.SECURITY_PRINCIPAL, "uid="+System.getProperty("user.name")+",ou=users,o=dhl.com");
            properties.put(Context.SECURITY_AUTHENTICATION, "simple");
            //properties.put(Context.SECURITY_PRINCIPAL, System.getenv("USERDOMAIN") + "\\" + System.getProperty("user.name"));
            //properties.put(Context.SECURITY_CREDENTIALS, "123456");

        return new InitialDirContext(properties);
    }
}
