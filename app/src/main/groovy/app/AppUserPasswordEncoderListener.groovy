package app

import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import grails.events.annotation.gorm.Listener

import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class AppUserPasswordEncoderListener {

    @Autowired
    SpringSecurityService springSecurityService

    @Listener(AppUser)
    void onPreInsertEvent(PreInsertEvent event) {
        encodePasswordForEvent(event)
    }

    @Listener(AppUser)
    void onPreUpdateEvent(PreUpdateEvent event) {
        encodePasswordForEvent(event)
    }

    private void encodePasswordForEvent(AbstractPersistenceEvent event) {
        if (event.entityObject instanceof AppUser) {
            AppUser u = event.entityObject as AppUser
            if (u.passwordHash && ((event instanceof  PreInsertEvent) || (event instanceof PreUpdateEvent && u.isDirty('passwordHash')))) {
                event.getEntityAccess().setProperty('passwordHash', encodePassword(u.passwordHash))
            }
        }
    }

    private String encodePassword(String password) {
        springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
    }
}
