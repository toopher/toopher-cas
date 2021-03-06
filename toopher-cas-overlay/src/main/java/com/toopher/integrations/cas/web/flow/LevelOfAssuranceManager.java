// Generated by delombok at Mon Mar 10 08:01:35 CST 2014
package com.toopher.integrations.cas.web.flow;

import org.apache.log4j.Logger;
import java.util.Map;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.principal.CredentialsToPrincipalResolver;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.springframework.webflow.execution.RequestContext;
import com.toopher.integrations.cas.authentication.LevelOfAssurance;

public class LevelOfAssuranceManager {
    
    private static final String LOA_SUFFICIENT = "LOA-sufficient";
    private static final String LOA_NEED_USERNAME_PASSWORD = "LOA-need-username-password";
    private static final String LOA_NEED_TOOPHER = "LOA-need-toopher";
    private static final Logger logger = Logger.getLogger("com.toopher.integrations.cas");
    private String primaryAuthenticationCredentialsAttributeName;
    private CredentialsToPrincipalResolver credentialsToPrincipalResolver;
    private LevelOfAssurance defaultUserLevelOfAssurance;
    private LevelOfAssurance defaultServiceLevelOfAssurance;
    private PrincipalLoaResolver serviceLoaResolver;
    private PrincipalLoaResolver userLoaResolver;
    private TicketRegistry ticketRegistry;
    
    public Principal getUserPrincipalForTGT(String ticketGrantingTicketId) {
        logger.debug("getting LOA of existing TicketGrantingTicket");
        if (ticketRegistry == null) {
            logger.debug("!! ticketRegistry is null !!");
            return null;
        }
        TicketGrantingTicketImpl ticketGrantingTicket = (TicketGrantingTicketImpl)ticketRegistry.getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);
        if (ticketGrantingTicket == null) {
            logger.debug("no existing TGT found for TGT id=" + ticketGrantingTicketId);
            return null;
        }
        Authentication authentication = ticketGrantingTicket.getAuthentication();
        if (authentication == null) {
            logger.debug("no authentication found for existing TGT");
            return null;
        }
        return authentication.getPrincipal();
    }
    
    public Long getEstablishedTGTLevelOfAssurance(String ticketGrantingTicketId) {
        logger.debug("getting LOA of existing TicketGrantingTicket");
        if (ticketRegistry == null) {
            logger.debug("!! ticketRegistry is null !!");
            return 0L;
        }
        TicketGrantingTicketImpl ticketGrantingTicket = (TicketGrantingTicketImpl)ticketRegistry.getTicket(ticketGrantingTicketId, TicketGrantingTicket.class);
        if (ticketGrantingTicket == null) {
            logger.debug("no existing TGT found for TGT id=" + ticketGrantingTicketId);
            return 0L;
        } else {
            logger.debug("found existing TGT.  Authentication is " + ticketGrantingTicket.getAuthentication());
        }
        Long existingLoa = 0L;
        Map<String, Object> authAttributes = ticketGrantingTicket.getAuthentication().getAttributes();
        for (Map.Entry<String, Object> entry : authAttributes.entrySet()) {
            logger.debug("  key=" + entry.getKey() + ", val = " + entry.getValue());
        }
        if (authAttributes.containsKey(LevelOfAssurance.LOA_ATTRIBUTE_NAME)) {
            existingLoa = Long.valueOf(authAttributes.get(LevelOfAssurance.LOA_ATTRIBUTE_NAME).toString());
        }
        return existingLoa;
    }
    
    public Long getRequiredLevelOfAssurance(Principal userPrincipal, String serviceName, RequestContext context) {
        LevelOfAssurance serviceLoa = null;
        LevelOfAssurance userLoa = null;
        if (serviceLoaResolver != null) {
            if (serviceName != null) {
                serviceLoa = serviceLoaResolver.getLoa(new SimplePrincipal(serviceName));
            }
        }
        if (userLoaResolver != null) {
            if (userPrincipal != null) {
                userLoa = userLoaResolver.getLoa(userPrincipal);
            }
        }
        if (serviceLoa == null) {
            serviceLoa = defaultServiceLevelOfAssurance;
        }
        if (userLoa == null) {
            userLoa = defaultUserLevelOfAssurance;
        }
        return serviceLoa.asLong() | userLoa.asLong();
    }
    
    public String evaluateEstablishedLevelOfAssurance(Long establishedLoaValue, Long requiredLoaValue) {
        // first, check to see if user has already authenticated to equal or higher LOA than required
        if ((establishedLoaValue | requiredLoaValue) == establishedLoaValue) {
            return LOA_SUFFICIENT;
        }
        // determine which factor is missing
        LevelOfAssurance establishedLoa = new LevelOfAssurance(establishedLoaValue);
        LevelOfAssurance requiredLoa = new LevelOfAssurance(requiredLoaValue);
        if (establishedLoa.isUsernamePasswordRequired() != requiredLoa.isUsernamePasswordRequired()) {
            return LOA_NEED_USERNAME_PASSWORD;
        } else {
            return LOA_NEED_TOOPHER;
        }
    }
    
    public boolean getToopherRequired(Principal userPrincipal, String serviceName, RequestContext context) {
        Long requiredLoaVal = getRequiredLevelOfAssurance(userPrincipal, serviceName, context);
        return (requiredLoaVal | LevelOfAssurance.LOA_USERNAME_PASSWORD_VAL) != LevelOfAssurance.LOA_USERNAME_PASSWORD_VAL;
    }
    
    @java.lang.SuppressWarnings("all")
    public String getPrimaryAuthenticationCredentialsAttributeName() {
        return this.primaryAuthenticationCredentialsAttributeName;
    }
    
    @java.lang.SuppressWarnings("all")
    public void setPrimaryAuthenticationCredentialsAttributeName(final String primaryAuthenticationCredentialsAttributeName) {
        this.primaryAuthenticationCredentialsAttributeName = primaryAuthenticationCredentialsAttributeName;
    }
    
    @java.lang.SuppressWarnings("all")
    public CredentialsToPrincipalResolver getCredentialsToPrincipalResolver() {
        return this.credentialsToPrincipalResolver;
    }
    
    @java.lang.SuppressWarnings("all")
    public void setCredentialsToPrincipalResolver(final CredentialsToPrincipalResolver credentialsToPrincipalResolver) {
        this.credentialsToPrincipalResolver = credentialsToPrincipalResolver;
    }
    
    @java.lang.SuppressWarnings("all")
    public LevelOfAssurance getDefaultUserLevelOfAssurance() {
        return this.defaultUserLevelOfAssurance;
    }
    
    @java.lang.SuppressWarnings("all")
    public void setDefaultUserLevelOfAssurance(final LevelOfAssurance defaultUserLevelOfAssurance) {
        this.defaultUserLevelOfAssurance = defaultUserLevelOfAssurance;
    }
    
    @java.lang.SuppressWarnings("all")
    public LevelOfAssurance getDefaultServiceLevelOfAssurance() {
        return this.defaultServiceLevelOfAssurance;
    }
    
    @java.lang.SuppressWarnings("all")
    public void setDefaultServiceLevelOfAssurance(final LevelOfAssurance defaultServiceLevelOfAssurance) {
        this.defaultServiceLevelOfAssurance = defaultServiceLevelOfAssurance;
    }
    
    @java.lang.SuppressWarnings("all")
    public PrincipalLoaResolver getServiceLoaResolver() {
        return this.serviceLoaResolver;
    }
    
    @java.lang.SuppressWarnings("all")
    public void setServiceLoaResolver(final PrincipalLoaResolver serviceLoaResolver) {
        this.serviceLoaResolver = serviceLoaResolver;
    }
    
    @java.lang.SuppressWarnings("all")
    public PrincipalLoaResolver getUserLoaResolver() {
        return this.userLoaResolver;
    }
    
    @java.lang.SuppressWarnings("all")
    public void setUserLoaResolver(final PrincipalLoaResolver userLoaResolver) {
        this.userLoaResolver = userLoaResolver;
    }
    
    @java.lang.SuppressWarnings("all")
    public TicketRegistry getTicketRegistry() {
        return this.ticketRegistry;
    }
    
    @java.lang.SuppressWarnings("all")
    public void setTicketRegistry(final TicketRegistry ticketRegistry) {
        this.ticketRegistry = ticketRegistry;
    }
}