<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Eleve Controller</title>
</head>
<body>
<h1>Eleves</h1>
    <ul>
        <g:each in="${eleves}" var="eleve">
            <li>${eleve.getFullName()}</li>
        </g:each>
    </ul>
<g:link controller="first" action="index">
    retour
</g:link>
</body>
</html>