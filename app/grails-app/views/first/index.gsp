<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>First Controller</title>
    </head>
    <body>
        <h1>${message}</h1>
        <p>${text1}</p>
        <g:if test="${ nom == 'Sébastien'}">
            <h5>${nom}</h5>
            <ul>
                <g:each in="${voitures}" var="car">
                    <li>${car}</li>
                </g:each>
            </ul>
        </g:if>
        <g:link controller="eleve" action="index">
            Accueil des élèves
        </g:link>
    </body>
</html>