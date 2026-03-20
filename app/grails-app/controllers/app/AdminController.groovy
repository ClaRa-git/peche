package app

import grails.plugin.springsecurity.annotation.Secured
import grails.gorm.transactions.Transactional

@Transactional
class AdminController {

    static responseFormats = ['json']

    // GET /api/admin/stats
    @Secured('ROLE_ADMIN')
    def stats() {
        def cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        def firstDayOfMonth = cal.time

        respond([
                orders: [
                        total:        Order.count(),
                        thisMonth:    Order.countByOrderedAtGreaterThanEquals(firstDayOfMonth),
                        pending:      Order.countByStatus('pending'),
                        paid:         Order.countByStatus('paid'),
                        shipped:      Order.countByStatus('shipped'),
                        delivered:    Order.countByStatus('delivered'),
                        totalRevenue: Order.createCriteria().get {
                            projections { sum('totalAmount') }
                            eq('status', 'paid')
                        } ?: 0
                ],
                products: [
                        total:        Product.count(),
                        active:       Product.countByIsActive(true),
                        outOfStock:   Product.countByStockQuantity(0)
                ],
                permits: [
                        total:    FishingPermit.count(),
                        pending:  FishingPermit.countByStatus('pending'),
                        approved: FishingPermit.countByStatus('approved'),
                        rejected: FishingPermit.countByStatus('rejected')
                ],
                contests: [
                        total:        Contest.count(),
                        open:         Contest.countByIsOpen(true),
                        registrations: ContestRegistration.count()
                ],
                users: [
                        total: AppUser.count()
                ]
        ])
    }

    // GET /api/admin/users
    @Secured('ROLE_ADMIN')
    def users() {
        def users = AppUser.list([sort: 'createdAt', order: 'desc'])

        respond users.collect { u ->
            [
                    id:        u.id,
                    email:     u.email,
                    firstName: u.firstName,
                    lastName:  u.lastName,
                    phone:     u.phone,
                    role:      u.authorities*.authority,
                    createdAt: u.createdAt
            ]
        }
    }

    // GET /api/admin/orders
    @Secured('ROLE_ADMIN')
    def orders() {
        def orders = Order.list([sort: 'orderedAt', order: 'desc',
                                 max:    params.max    ? params.int('max')    : 20,
                                 offset: params.offset ? params.int('offset') : 0
        ])

        respond orders.collect { o ->
            [
                    id:              o.id,
                    status:          o.status,
                    totalAmount:     o.totalAmount,
                    shippingAddress: o.shippingAddress,
                    orderedAt:       o.orderedAt,
                    user: [
                            id:        o.user.id,
                            email:     o.user.email,
                            firstName: o.user.firstName,
                            lastName:  o.user.lastName
                    ],
                    items: OrderItem.findAllByOrder(o).collect { item ->
                        [
                                productName: item.product.name,
                                quantity:    item.quantity,
                                unitPrice:   item.unitPrice
                        ]
                    }
            ]
        }
    }

    // PUT /api/admin/orders/:id/status
    @Secured('ROLE_ADMIN')
    def updateOrderStatus() {
        def order = Order.get(params.id)

        if (!order) {
            respond([error: 'Commande introuvable'], status: 404)
            return
        }

        def body = request.JSON

        if (!body.status) {
            respond([error: 'Le champ status est obligatoire'], status: 400)
            return
        }

        def validStatuses = ['pending', 'paid', 'shipped', 'delivered', 'cancelled']
        if (!(body.status in validStatuses)) {
            respond([error: "Statut invalide. Valeurs acceptées : ${validStatuses.join(', ')}"], status: 422)
            return
        }

        order.status = body.status
        if (!order.save(flush: true)) {
            respond([error: 'Erreur lors de la mise à jour', details: order.errors], status: 422)
            return
        }

        respond([message: 'Statut mis à jour', orderId: order.id, status: order.status])
    }

    // GET /api/admin/permits
    @Secured('ROLE_ADMIN')
    def permits() {
        def permits = FishingPermit.list([sort: 'requestedDate', order: 'desc'])

        respond permits.collect { p ->
            [
                    id:              p.id,
                    permitType:      p.permitType,
                    status:          p.status,
                    requestedDate:   p.requestedDate,
                    validFrom:       p.validFrom,
                    validUntil:      p.validUntil,
                    rejectionReason: p.rejectionReason,
                    user: [
                            id:        p.user.id,
                            email:     p.user.email,
                            firstName: p.user.firstName,
                            lastName:  p.user.lastName
                    ]
            ]
        }
    }

    // PUT /api/admin/permits/:id/status
    @Secured('ROLE_ADMIN')
    def updatePermitStatus() {
        def permit = FishingPermit.get(params.id)

        if (!permit) {
            respond([error: 'Permis introuvable'], status: 404)
            return
        }

        def body = request.JSON

        if (!body.status) {
            respond([error: 'Le champ status est obligatoire'], status: 400)
            return
        }

        if (!(body.status in ['approved', 'rejected'])) {
            respond([error: 'Statut invalide. Valeurs acceptées : approved, rejected'], status: 422)
            return
        }

        permit.status = body.status

        if (body.status == 'approved') {
            // Définir les dates de validité : 1 an à partir d'aujourd'hui
            permit.validFrom  = new Date()
            permit.validUntil = new Date() + 365
        }

        if (body.status == 'rejected') {
            if (!body.rejectionReason) {
                respond([error: 'Le champ rejectionReason est obligatoire pour un rejet'], status: 400)
                return
            }
            permit.rejectionReason = body.rejectionReason
        }

        if (!permit.save(flush: true)) {
            respond([error: 'Erreur lors de la mise à jour', details: permit.errors], status: 422)
            return
        }

        respond([message: "Permis ${body.status}", permitId: permit.id, status: permit.status])
    }

    // GET /api/admin/contests
    @Secured('ROLE_ADMIN')
    def contests() {
        def contests = Contest.list([sort: 'contestDate', order: 'desc'])

        respond contests.collect { c ->
            [
                    id:               c.id,
                    name:             c.name,
                    description:      c.description,
                    location:         c.location,
                    contestDate:      c.contestDate,
                    maxParticipants:  c.maxParticipants,
                    isOpen:           c.isOpen,
                    registeredCount:  ContestRegistration.countByContestAndStatus(c, 'confirmed')
            ]
        }
    }

    // POST /api/admin/contests
    @Secured('ROLE_ADMIN')
    def saveContest() {
        def body = request.JSON

        if (!body.name || !body.location || !body.contestDate) {
            respond([error: 'Les champs name, location et contestDate sont obligatoires'], status: 400)
            return
        }

        def contest = new Contest(
                name:            body.name,
                description:     body.description ?: null,
                location:        body.location,
                contestDate:     Date.parse('yyyy-MM-dd', body.contestDate as String),
                maxParticipants: body.maxParticipants ? body.maxParticipants as Integer : null,
                isOpen:          true
        )

        if (!contest.save(flush: true)) {
            respond([error: 'Erreur lors de la création', details: contest.errors], status: 422)
            return
        }

        respond([message: 'Concours créé avec succès', id: contest.id], status: 201)
    }

    // PUT /api/admin/contests/:id
    @Secured('ROLE_ADMIN')
    def updateContest() {
        def contest = Contest.get(params.id)

        if (!contest) {
            respond([error: 'Concours introuvable'], status: 404)
            return
        }

        def body = request.JSON

        if (body.name)            contest.name            = body.name
        if (body.description)     contest.description     = body.description
        if (body.location)        contest.location        = body.location
        if (body.contestDate)     contest.contestDate     = Date.parse('yyyy-MM-dd', body.contestDate as String)
        if (body.maxParticipants) contest.maxParticipants = body.maxParticipants as Integer
        if (body.isOpen != null)  contest.isOpen          = body.isOpen as Boolean

        if (!contest.save(flush: true)) {
            respond([error: 'Erreur lors de la mise à jour', details: contest.errors], status: 422)
            return
        }

        respond([message: 'Concours mis à jour', id: contest.id])
    }
}