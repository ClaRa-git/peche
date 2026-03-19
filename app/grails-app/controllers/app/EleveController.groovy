package app

import app.Eleve

class EleveController {

    def index() {
        def eleves = Eleve.list()

        [eleves: eleves]
    }
}
