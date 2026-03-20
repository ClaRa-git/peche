package app

class Order {
    UUID id
    AppUser user
    String status = 'pending'
    BigDecimal totalAmount
    String stripePaymentId
    String shippingAddress
    Date orderedAt = new Date()

    static hasMany = [items: OrderItem]

    static constraints = {
        status          inList: ['pending', 'paid', 'shipped', 'delivered', 'cancelled']
        totalAmount     min: 0.0
        stripePaymentId nullable: true, maxSize: 255
        shippingAddress blank: false
    }

    static mapping = {
        table           'order_table'
        id              generator: 'uuid2', type: 'pg-uuid'
        user            column: 'user_id'
        totalAmount     column: 'total_amount'
        stripePaymentId column: 'stripe_payment_id'
        shippingAddress column: 'shipping_address'
        orderedAt       column: 'ordered_at'
    }
}