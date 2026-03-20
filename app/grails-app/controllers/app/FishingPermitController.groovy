package app

import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.SpringSecurityService
import grails.gorm.transactions.Transactional

@Transactional
class FishingPermitController {

    SpringSecurityService springSecurityService
    static responseFormats = ['json']

    // GET /api/permits
    @Secured('isAuthenticated()')
    def index() {
        def currentUser = springSecurityService.currentUser as AppUser
        def permits = FishingPermit.findAllByUser(currentUser, [sort: 'requestedDate', order: 'desc'])
        respond permits.collect { formatPermit(it) }
    }

    // POST /api/permits
    @Secured('isAuthenticated()')
    def save() {
        def currentUser = springSecurityService.currentUser as AppUser
        def body = request.JSON

        if (!body.permitType) {
            respond([error: 'Le champ permitType est obligatoire'], status: 400)
            return
        }

        // Vérifier qu'il n'y a pas déjà une demande en cours pour ce type
        def existing = FishingPermit.findByUserAndPermitTypeAndStatus(
                currentUser,
                body.permitType as String,
                'pending'
        )

        if (existing) {
            respond([error: 'Une demande est déjà en cours pour ce type de permis'], status: 409)
            return
        }

        def permit = new FishingPermit(
                user:          currentUser,
                permitType:    body.permitType,
                status:        'pending',
                requestedDate: new Date()
        )

        if (!permit.save(flush: true)) {
            respond([error: 'Erreur lors de la création de la demande', details: permit.errors], status: 422)
            return
        }

        respond formatPermit(permit), status: 201
    }

    // Méthode privée de formatage
    private Map formatPermit(FishingPermit p) {
        [
                id:               p.id,
                permitType:       p.permitType,
                status:           p.status,
                requestedDate:    p.requestedDate,
                validFrom:        p.validFrom,
                validUntil:       p.validUntil,
                rejectionReason:  p.rejectionReason
        ]
    }
}