package app

import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.SpringSecurityService
import grails.gorm.transactions.Transactional

@Transactional
class OrderController {

    SpringSecurityService springSecurityService
    static responseFormats = ['json']

    // GET /api/orders
    @Secured('isAuthenticated()')
    def index() {
        def currentUser = springSecurityService.currentUser as AppUser
        def orders = Order.findAllByUser(currentUser, [sort: 'orderedAt', order: 'desc'])
        respond orders.collect { formatOrder(it) }
    }

    // GET /api/orders/:id
    @Secured('isAuthenticated()')
    def show() {
        def currentUser = springSecurityService.currentUser as AppUser
        def order = Order.get(params.id)

        if (!order) {
            respond([error: 'Commande introuvable'], status: 404)
            return
        }

        // Un user ne peut voir que ses propres commandes
        if (order.user.id != currentUser.id) {
            respond([error: 'Accès interdit'], status: 403)
            return
        }

        respond formatOrder(order)
    }

    // POST /api/orders
    @Secured('isAuthenticated()')
    def save() {
        def currentUser = springSecurityService.currentUser as AppUser
        def body = request.JSON

        if (!body.items || !body.shippingAddress) {
            respond([error: 'Les champs items et shippingAddress sont obligatoires'], status: 400)
            return
        }

        // Vérifier le stock et calculer le total
        def orderItems = []
        BigDecimal total = 0

        for (def item : body.items) {
            def product = Product.get(item.productId as UUID)

            if (!product || !product.isActive) {
                respond([error: "Produit introuvable : ${item.productId}"], status: 404)
                return
            }

            if (product.stockQuantity < (item.quantity as Integer)) {
                respond([error: "Stock insuffisant pour : ${product.name}"], status: 422)
                return
            }

            orderItems << [product: product, quantity: item.quantity as Integer]
            total += product.price * (item.quantity as Integer)
        }

        // Créer la commande
        def order = new Order(
                user:            currentUser,
                totalAmount:     total,
                shippingAddress: body.shippingAddress,
                status:          'pending'
        )

        if (!order.save(flush: true)) {
            respond([error: 'Erreur lors de la création de la commande', details: order.errors], status: 422)
            return
        }

        // Créer les lignes de commande + décrémenter le stock
        orderItems.each { item ->
            def orderItem = new OrderItem(
                    order:     order,
                    product:   item.product,
                    quantity:  item.quantity,
                    unitPrice: item.product.price   // prix figé au moment de la commande
            )
            orderItem.save(flush: true)

            item.product.stockQuantity -= item.quantity
            item.product.save(flush: true)
        }

        respond formatOrder(order), status: 201
    }

    // POST /api/payments/checkout — crée une session Stripe
    @Secured('isAuthenticated()')
    def checkout() {
        def currentUser = springSecurityService.currentUser as AppUser
        def body = request.JSON

        def order = Order.get(body.orderId as UUID)

        if (!order || order.user.id != currentUser.id) {
            respond([error: 'Commande introuvable'], status: 404)
            return
        }

        if (order.status != 'pending') {
            respond([error: 'Cette commande a déjà été payée'], status: 422)
            return
        }

        // Ici on appellera l'API Stripe pour créer une session de paiement
        // Pour l'instant on retourne un placeholder
        // TODO : intégrer le SDK Stripe
        respond([
                message:   'Session Stripe à intégrer',
                orderId:   order.id,
                amount:    order.totalAmount,
                currency:  'eur'
        ])
    }

    // POST /api/payments/webhook — appelé par Stripe après paiement
    @Secured('permitAll')
    def webhook() {
        def payload = request.JSON

        // TODO : vérifier la signature Stripe avec la clé secrète webhook
        // String sigHeader = request.getHeader('Stripe-Signature')

        if (payload.type == 'payment_intent.succeeded') {
            def paymentIntentId = payload.data?.object?.id as String
            def order = Order.findByStripePaymentId(paymentIntentId)

            if (order) {
                order.status = 'paid'
                order.save(flush: true)
            }
        }

        respond([received: true])
    }

    // Méthode privée de formatage
    private Map formatOrder(Order o) {
        [
                id:              o.id,
                status:          o.status,
                totalAmount:     o.totalAmount,
                shippingAddress: o.shippingAddress,
                orderedAt:       o.orderedAt,
                items:           OrderItem.findAllByOrder(o).collect { item ->
                    [
                            productId:   item.product.id,
                            productName: item.product.name,
                            quantity:    item.quantity,
                            unitPrice:   item.unitPrice
                    ]
                }
        ]
    }
}