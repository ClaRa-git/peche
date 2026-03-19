package app

class Eleve {

    String nom
    String prenom
    Integer age
    String niveau

//    static hasmany = { notes: Note}

    static constraints = {
        nom blank: false, nullable: false
        prenom blank: false, nullable: false
        age min:12, max:18
        niveau nullable: false
    }

//    static mapping = {
//
//    }

    def getFullName(){
        return this.nom + " " +this.prenom
    }
}
