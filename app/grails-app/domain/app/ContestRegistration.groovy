package app

class ContestRegistration {
    UUID id
    AppUser user
    Contest contest
    Date registeredAt = new Date()
    String status = 'confirmed'

    static constraints = {
        status inList: ['confirmed', 'cancelled']
        user   validator: { val, obj ->
            // Empêche un user de s'inscrire deux fois au même concours
            if (ContestRegistration.findByUserAndContest(val, obj.contest)) {
                return 'contestRegistration.duplicate'
            }
        }
    }

    static mapping = {
        table        'contest_registration'
        id           generator: 'uuid2', type: 'pg-uuid'
        user         column: 'user_id'
        contest      column: 'contest_id'
        registeredAt column: 'registered_at'
    }
}