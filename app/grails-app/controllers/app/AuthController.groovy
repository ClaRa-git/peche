package app

import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.SpringSecurityService
import grails.gorm.transactions.Transactional

@Transactional
class AuthController {

    SpringSecurityService springSecurityService

    static responseFormats = ['json']

    // POST /api/auth/login
    @Secured('permitAll')
    def login() {
        def body = request.JSON

        if (!body.email || !body.password) {
            respond([error: 'Les champs email et password sont obligatoires'], status: 400)
            return
        }

        def user = AppUser.findByEmail(body.email as String)

        if (!user) {
            respond([error: 'Identifiants invalides'], status: 401)
            return
        }

        // Vérifier le mot de passe avec BCrypt
        if (!springSecurityService.passwordEncoder.matches(body.password as String, user.passwordHash)) {
            respond([error: 'Identifiants invalides'], status: 401)
            return
        }

        // Générer un token JWT manuellement ou laisser Spring Security le faire
        // Note: Spring Security REST gère automatiquement le JWT via POST /api/login
        respond([
            message: 'Connexion réussie',
            user: [
                id:        user.id,
                email:     user.email,
                firstName: user.firstName,
                lastName:  user.lastName,
                role:      user.authorities*.authority
            ]
        ])
    }

    // POST /api/auth/register
    @Secured('permitAll')
    def register() {
        def body = request.JSON

        // Validation des champs obligatoires
        if (!body.email || !body.password || !body.firstName || !body.lastName) {
            respond([error: 'Les champs email, password, firstName et lastName sont obligatoires'], status: 400)
            return
        }

        // Vérifier si l'email existe déjà
        if (AppUser.findByEmail(body.email as String)) {
            respond([error: 'Cet email est déjà utilisé'], status: 409)
            return
        }

        // Créer l'utilisateur
        def user = new AppUser(
                email:        body.email,
                passwordHash: body.password,   // le listener BCrypt encode automatiquement
                firstName:    body.firstName,
                lastName:     body.lastName,
                phone:        body.phone ?: null,
                address:      body.address ?: null
        )

        if (!user.save(flush: true)) {
            respond([error: 'Erreur lors de la création du compte', details: user.errors], status: 422)
            return
        }

        // Assigner le rôle ROLE_USER
        def userRole = Role.findByAuthority('ROLE_USER')
        AppUserRole.create(user, userRole, true)

        respond([message: 'Compte créé avec succès', userId: user.id], status: 201)
    }

    // GET /api/profile
    @Secured('isAuthenticated()')
    def profile() {
        def currentUser = springSecurityService.currentUser as AppUser

        respond([
                id:        currentUser.id,
                email:     currentUser.email,
                firstName: currentUser.firstName,
                lastName:  currentUser.lastName,
                phone:     currentUser.phone,
                address:   currentUser.address,
                role:      currentUser.authorities*.authority
        ])
    }

    // PUT /api/profile
    @Secured('isAuthenticated()')
    def updateProfile() {
        def currentUser = springSecurityService.currentUser as AppUser
        def body = request.JSON

        if (body.firstName) currentUser.firstName = body.firstName
        if (body.lastName)  currentUser.lastName  = body.lastName
        if (body.phone)     currentUser.phone     = body.phone
        if (body.address)   currentUser.address   = body.address

        // Changement de mot de passe optionnel
        if (body.password) {
            currentUser.passwordHash = body.password  // le listener re-encode
        }

        if (!currentUser.save(flush: true)) {
            respond([error: 'Erreur lors de la mise à jour', details: currentUser.errors], status: 422)
            return
        }

        respond([message: 'Profil mis à jour avec succès'])
    }
}