package app

class OrderItem {
    UUID id
    Order order
    Product product
    Integer quantity
    BigDecimal unitPrice

    static constraints = {
        quantity  min: 1
        unitPrice min: 0.0
    }

    static mapping = {
        table     'order_item'
        id        generator: 'uuid2', type: 'pg-uuid'
        order     column: 'order_id'
        product   column: 'product_id'
        unitPrice column: 'unit_price'
    }
}