package com.openrangelabs.services.operations;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.NamingException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Slf4j
@Component
public class OpenLdapAuthenticationProvider implements AuthenticationProvider {

    private DirContext dirContext;

    @Value("${spring.ldap.urls}")
    private String ldapUrls;
    @Value("${spring.ldap.base.dn}")
    private String ldapBaseDn;
    @Value("${spring.ldap.username}")
    private String ldapSecurityPrincipal;
    @Value("${spring.ldap.password}")
    private String ldapPrincipalPassword;

    @PostConstruct
    private void initContext() {
        String strUrl;
        Hashtable<String, String> env = new Hashtable<> (11);
        boolean b = false;

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrls);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapSecurityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, ldapPrincipalPassword);

        try {
            dirContext = new InitialDirContext(env);
            b = true;

        } catch (NamingException | javax.naming.NamingException e) {
       log.error(e.getMessage());
        } finally {
            if (b) {
                strUrl = "success";
            } else {
                strUrl = "failure";
            }
        }

       log.info("Initialising LDAP connection :" +strUrl);

    }

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Logging user into operations.");
        initContext();
        SearchControls searchCtrls = new SearchControls();
        searchCtrls.setReturningAttributes(new String[] {});
        searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String filter = "(&(sAMAccountName="+authentication.getName()+"))";

        NamingEnumeration<SearchResult> answer = null;
        try {
            answer = dirContext.search(
                    ldapBaseDn, filter, searchCtrls);
        } catch (javax.naming.NamingException e) {
            log.error(e.getMessage());
        }

        String fullDN = null;
        Boolean authenticate = false;
        try {
            Hashtable env2 = new Hashtable(11);
            if (null != answer && answer.hasMore()) {
                SearchResult searchResult = answer.next();
                fullDN = searchResult.getNameInNamespace();

                env2.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env2.put(Context.SECURITY_AUTHENTICATION, "simple");
                env2.put(Context.PROVIDER_URL, ldapUrls);
                env2.put(Context.SECURITY_PRINCIPAL, fullDN);
                env2.put(Context.SECURITY_CREDENTIALS, authentication.getCredentials());

                 DirContext ctx = new InitialDirContext(env2);

                ctx.close();
                authenticate =true;

            }
        } catch (javax.naming.NamingException e) {
            return null;


        }
        if (authenticate) {
            log.info("Creating jwt token.");
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            UserDetails userDetails = new User(authentication.getName() ,authentication.getCredentials().toString()
                    ,grantedAuthorities);
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,
                    authentication.getCredentials().toString() , grantedAuthorities);
            return auth;

        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
