package app

class AppUser {

    UUID id
    String email
    String passwordHash
    String firstName
    String lastName
    String phone
    String address
    Boolean enabled = true
    Boolean accountExpired = false
    Boolean accountLocked = false
    Boolean passwordExpired = false
    Date createdAt = new Date()

    // Récupère les rôles via AppUserRole pour Spring Security
    Set<Role> getAuthorities() {
        AppUserRole.findAllByAppUser(this)*.role
    }

    static constraints = {
        email        email: true, unique: true, blank: false, maxSize: 255
        passwordHash blank: false, maxSize: 255, password: true
        firstName    blank: false, maxSize: 100
        lastName     blank: false, maxSize: 100
        phone        nullable: true, maxSize: 20
        address      nullable: true
    }

    static mapping = {
        table        'app_user'
        id           generator: 'uuid2', type: 'pg-uuid'
        passwordHash column: 'password_hash'
        firstName    column: 'first_name'
        lastName     column: 'last_name'
        createdAt    column: 'created_at'
        enabled         column: 'enabled'
        accountExpired  column: 'account_expired'
        accountLocked   column: 'account_locked'
        passwordExpired column: 'password_expired'
    }
}