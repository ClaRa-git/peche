package app

class FirstController {

    def index() {
        def nom = "Sébastien"
//        def nom = "hodor"
        def voitures = ["bmw", "audi", "tesla", "bmw"]
//        def villes = ["perignan", "baho", "st-esteve", "baho"] as set // set
        def chien = [ "race": "berger allemand", "nom": "rex"] // map

        [
                message: "bienvenue au CFA !",
                text1: "lorem ipsum, etc etc",
                nom: nom,
                voitures: voitures
        ]
    }
}
