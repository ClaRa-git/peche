// grails-app/conf/SecurityConfig.groovy
import grails.plugin.springsecurity.SpringSecurityUtils

security {
    userLookup {
        userDomainClassName = 'app.AppUser'
        usernamePropertyName = 'email'
        passwordPropertyName = 'passwordHash'
        authoritiesPropertyName = 'authorities'
    }
    authority {
        className = 'app.Role'
    }
    rest {
        login {
            endpointUrl = '/api/auth/login'
        }
        logout {
            endpointUrl = '/api/auth/logout'
        }
        token {
            storage {
                useGorm = false  // on utilise JWT stateless, pas de BDD pour les tokens
            }
        }
    }
}