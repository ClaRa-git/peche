package app

class Contest {
    UUID id
    String name
    String description
    String location
    Date contestDate
    Integer maxParticipants
    Boolean isOpen = true

    static hasMany = [registrations: ContestRegistration]

    static constraints = {
        name            blank: false, maxSize: 255
        description     nullable: true
        location        blank: false, maxSize: 255
        maxParticipants nullable: true, min: 1
    }

    static mapping = {
        table           'contest'
        id              generator: 'uuid2', type: 'pg-uuid'
        contestDate     column: 'contest_date'
        maxParticipants column: 'max_participants'
        isOpen          column: 'is_open'
    }
}