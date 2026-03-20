package app

class FishingPermit {
    UUID id
    AppUser user
    String permitType
    String status = 'pending'
    Date requestedDate = new Date()
    Date validFrom
    Date validUntil
    String rejectionReason

    static constraints = {
        permitType       blank: false, maxSize: 50
        status           inList: ['pending', 'approved', 'rejected']
        validFrom        nullable: true
        validUntil       nullable: true
        rejectionReason  nullable: true
    }

    static mapping = {
        table           'fishing_permit'
        id              generator: 'uuid2', type: 'pg-uuid'
        user            column: 'user_id'
        permitType      column: 'permit_type'
        requestedDate   column: 'requested_date'
        validFrom       column: 'valid_from'
        validUntil      column: 'valid_until'
        rejectionReason column: 'rejection_reason'
    }
}