package app

class Category {
    UUID id
    String name
    String slug

    static hasMany = [products: Product]

    static constraints = {
        name blank: false, maxSize: 100
        slug blank: false, unique: true, maxSize: 100
    }

    static mapping = {
        table 'category'
        id    generator: 'uuid2', type: 'pg-uuid'
    }
}