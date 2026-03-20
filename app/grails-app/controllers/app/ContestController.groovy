package app

import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.SpringSecurityService
import grails.gorm.transactions.Transactional

@Transactional
class ContestController {

    SpringSecurityService springSecurityService
    static responseFormats = ['json']

    // GET /api/contests
    @Secured('permitAll')
    def index() {
        def contests = Contest.findAllByIsOpen(true, [sort: 'contestDate', order: 'asc'])

        respond contests.collect { formatContest(it) }
    }

    // POST /api/contests/:id/register
    @Secured('isAuthenticated()')
    def register() {
        def currentUser = springSecurityService.currentUser as AppUser
        def contest = Contest.get(params.id)

        if (!contest) {
            respond([error: 'Concours introuvable'], status: 404)
            return
        }

        if (!contest.isOpen) {
            respond([error: 'Les inscriptions sont fermées pour ce concours'], status: 422)
            return
        }

        // Vérifier si l'utilisateur est déjà inscrit
        def existing = ContestRegistration.findByUserAndContest(currentUser, contest)
        if (existing) {
            respond([error: 'Vous êtes déjà inscrit à ce concours'], status: 409)
            return
        }

        // Vérifier le nombre maximum de participants
        if (contest.maxParticipants) {
            def count = ContestRegistration.countByContestAndStatus(contest, 'confirmed')
            if (count >= contest.maxParticipants) {
                respond([error: 'Le concours est complet'], status: 422)
                return
            }
        }

        def registration = new ContestRegistration(
                user:    currentUser,
                contest: contest,
                status:  'confirmed'
        )

        if (!registration.save(flush: true)) {
            respond([error: 'Erreur lors de l\'inscription', details: registration.errors], status: 422)
            return
        }

        respond formatRegistration(registration), status: 201
    }

    // GET /api/contests/my-registrations
    @Secured('isAuthenticated()')
    def myRegistrations() {
        def currentUser = springSecurityService.currentUser as AppUser
        def registrations = ContestRegistration.findAllByUser(
                currentUser,
                [sort: 'registeredAt', order: 'desc']
        )

        respond registrations.collect { formatRegistration(it) }
    }

    // Méthodes privées de formatage
    private Map formatContest(Contest c) {
        def registrationCount = ContestRegistration.countByContestAndStatus(c, 'confirmed')
        [
                id:              c.id,
                name:            c.name,
                description:     c.description,
                location:        c.location,
                contestDate:     c.contestDate,
                maxParticipants: c.maxParticipants,
                isOpen:          c.isOpen,
                registeredCount: registrationCount,
                spotsLeft:       c.maxParticipants ? c.maxParticipants - registrationCount : null
        ]
    }

    private Map formatRegistration(ContestRegistration r) {
        [
                id:           r.id,
                registeredAt: r.registeredAt,
                status:       r.status,
                contest:      formatContest(r.contest)
        ]
    }
}