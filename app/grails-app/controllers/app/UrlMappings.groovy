package app

class UrlMappings {

    static mappings = {

        // Auth (public)
        post '/api/auth/login'(controller: 'auth', action: 'login')
        post '/api/auth/register'(controller: 'auth', action: 'register')

        // Produits (public en lecture)
        get  '/api/products'(controller: 'product', action: 'index')
        get  '/api/products/$id'(controller: 'product', action: 'show')
        get  '/api/categories'(controller: 'product', action: 'categories')

        // Produits (admin)
        post   '/api/products'(controller: 'product', action: 'save')
        put    '/api/products/$id'(controller: 'product', action: 'update')
        delete '/api/products/$id'(controller: 'product', action: 'delete')

        // Commandes (user)
        post '/api/orders'(controller: 'order', action: 'save')
        get  '/api/orders'(controller: 'order', action: 'index')
        get  '/api/orders/$id'(controller: 'order', action: 'show')

        // Commandes (admin)
        get '/api/admin/orders'(controller: 'admin', action: 'orders')
        put '/api/admin/orders/$id/status'(controller: 'admin', action: 'updateOrderStatus')

        // Permis de pêche (user)
        post '/api/permits'(controller: 'fishingPermit', action: 'save')
        get  '/api/permits'(controller: 'fishingPermit', action: 'index')

        // Permis de pêche (admin)
        get '/api/admin/permits'(controller: 'admin', action: 'permits')
        put '/api/admin/permits/$id/status'(controller: 'admin', action: 'updatePermitStatus')

        // Concours (public en lecture)
        get  '/api/contests'(controller: 'contest', action: 'index')

        // Concours (user)
        post '/api/contests/$id/register'(controller: 'contest', action: 'register')
        get  '/api/contests/my-registrations'(controller: 'contest', action: 'myRegistrations')

        // Concours (admin)
        get  '/api/admin/contests'(controller: 'admin', action: 'contests')
        post '/api/admin/contests'(controller: 'admin', action: 'saveContest')
        put  '/api/admin/contests/$id'(controller: 'admin', action: 'updateContest')

        // Profil (user)
        get '/api/profile'(controller: 'auth', action: 'profile')
        put '/api/profile'(controller: 'auth', action: 'updateProfile')

        // Stats admin
        get '/api/admin/stats'(controller: 'admin', action: 'stats')
        get '/api/admin/users'(controller: 'admin', action: 'users')

        // Paiement Stripe
        post '/api/payments/checkout'(controller: 'order', action: 'checkout')
        post '/api/payments/webhook'(controller: 'order', action: 'webhook')

        // Route par défaut
        "/"(view:"/index")
        // Erreurs
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}