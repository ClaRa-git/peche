package app

import grails.plugin.springsecurity.annotation.Secured
import grails.gorm.transactions.Transactional

@Transactional
class ProductController {

    static responseFormats = ['json']

    // GET /api/products?category=&search=&minPrice=&maxPrice=
    @Secured('permitAll')
    def index() {
        def query = Product.where { isActive == true }

        if (params.category) {
            def cat = Category.findBySlug(params.category)
            if (cat) query = query.where { category == cat }
        }

        if (params.search) {
            def search = "%${params.search}%"
            query = query.where { name =~ search }
        }

        if (params.minPrice) {
            def min = params.minPrice as BigDecimal
            query = query.where { price >= min }
        }

        if (params.maxPrice) {
            def max = params.maxPrice as BigDecimal
            query = query.where { price <= max }
        }

        def products = query.list(
                max:    params.max    ? params.int('max')    : 20,
                offset: params.offset ? params.int('offset') : 0
        )

        respond products.collect { formatProduct(it) }
    }

    // GET /api/products/:id
    @Secured('permitAll')
    def show() {
        def product = Product.get(params.id)

        if (!product || !product.isActive) {
            respond([error: 'Produit introuvable'], status: 404)
            return
        }

        respond formatProduct(product)
    }

    // GET /api/categories
    @Secured('permitAll')
    def categories() {
        def cats = Category.list()
        respond cats.collect { [id: it.id, name: it.name, slug: it.slug] }
    }

    // POST /api/products (admin)
    @Secured('ROLE_ADMIN')
    def save() {
        def body = request.JSON

        if (!body.name || !body.price || !body.categoryId) {
            respond([error: 'Les champs name, price et categoryId sont obligatoires'], status: 400)
            return
        }

        def category = Category.get(body.categoryId as UUID)
        if (!category) {
            respond([error: 'Catégorie introuvable'], status: 404)
            return
        }

        def product = new Product(
                category:      category,
                name:          body.name,
                description:   body.description ?: null,
                price:         body.price as BigDecimal,
                stockQuantity: body.stockQuantity ? body.int('stockQuantity') : 0,
                imageUrl:      body.imageUrl ?: null,
                isActive:      true
        )

        if (!product.save(flush: true)) {
            respond([error: 'Erreur lors de la création', details: product.errors], status: 422)
            return
        }

        respond formatProduct(product), status: 201
    }

    // PUT /api/products/:id (admin)
    @Secured('ROLE_ADMIN')
    def update() {
        def product = Product.get(params.id)

        if (!product) {
            respond([error: 'Produit introuvable'], status: 404)
            return
        }

        def body = request.JSON

        if (body.categoryId) {
            def category = Category.get(body.categoryId as UUID)
            if (!category) {
                respond([error: 'Catégorie introuvable'], status: 404)
                return
            }
            product.category = category
        }

        if (body.name)          product.name          = body.name
        if (body.description)   product.description   = body.description
        if (body.price)         product.price         = body.price as BigDecimal
        if (body.stockQuantity != null) product.stockQuantity = body.stockQuantity as Integer
        if (body.imageUrl)      product.imageUrl      = body.imageUrl
        if (body.isActive != null) product.isActive   = body.isActive as Boolean

        if (!product.save(flush: true)) {
            respond([error: 'Erreur lors de la mise à jour', details: product.errors], status: 422)
            return
        }

        respond formatProduct(product)
    }

    // DELETE /api/products/:id (admin)
    @Secured('ROLE_ADMIN')
    def delete() {
        def product = Product.get(params.id)

        if (!product) {
            respond([error: 'Produit introuvable'], status: 404)
            return
        }

        // Soft delete : on désactive plutôt que supprimer
        // pour ne pas casser les OrderItems existants
        product.isActive = false
        product.save(flush: true)

        respond([message: 'Produit désactivé avec succès'])
    }

    // Méthode privée de formatage
    private Map formatProduct(Product p) {
        [
                id:            p.id,
                name:          p.name,
                description:   p.description,
                price:         p.price,
                stockQuantity: p.stockQuantity,
                imageUrl:      p.imageUrl,
                isActive:      p.isActive,
                createdAt:     p.createdAt,
                category: [
                        id:   p.category.id,
                        name: p.category.name,
                        slug: p.category.slug
                ]
        ]
    }
}