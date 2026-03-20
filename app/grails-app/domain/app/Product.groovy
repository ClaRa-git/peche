package app

class Product {
    UUID id
    Category category
    String name
    String description
    BigDecimal price
    Integer stockQuantity = 0
    String imageUrl
    Boolean isActive = true
    Date createdAt = new Date()

    static constraints = {
        name          blank: false, maxSize: 255
        description   nullable: true
        price         min: 0.0
        stockQuantity min: 0
        imageUrl      nullable: true, maxSize: 500
    }

    static mapping = {
        table         'product'
        id            generator: 'uuid2', type: 'pg-uuid'
        category      column: 'category_id'
        stockQuantity column: 'stock_quantity'
        imageUrl      column: 'image_url'
        isActive      column: 'is_active'
        createdAt     column: 'created_at'
    }
}