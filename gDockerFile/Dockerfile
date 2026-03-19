FROM --platform=linux/amd64 ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install  \
    -y --no-install-recommends \
    curl zip unzip git wget gnupg \
    software-properties-common locales

# Configuration des locales en français
RUN locale-gen fr_FR.UTF-8

ENV LANG=fr_FR.UTF-8 \
    LANGUAGE=fr_FR:fr \
    LC_ALL=fr_FR.UTF-8

# Installation de SDKMan
RUN curl -s "https://get.sdkman.io" | bash

# Rendre SDKMan disponible dans le shell
SHELL ["/bin/bash", "-c"]
ENV SDKMAN_DIR="/root/.sdkman"

# Installation de Java 21, Gradle 8.4 via SDKMan avec spécification ARM
RUN bash -c "source $SDKMAN_DIR/bin/sdkman-init.sh && \
    sdk selfupdate force && \
    sdk flush archives && \
    sdk update && \
    sdk install java 21.0.3-tem && \
    sdk default java 21.0.3-tem && \
    sdk install gradle 8.4 && \
    sdk default gradle 8.4 && \
    sdk install grails 7.0.4&& \
    sdk default grails 7.0.4"

# Définition des variables d'environnement JAVA_HOME et GRADLE_HOME
ENV JAVA_HOME="/root/.sdkman/candidates/java/current"
ENV GRADLE_HOME="/root/.sdkman/candidates/gradle/current"
ENV GRAILS_HOME="/root/.sdkman/candidates/grails/current"
ENV PATH=$PATH:$JAVA_HOME/bin:$GRADLE_HOME/bin:$GRAILS_HOME/bin

# Vérification des versions installées
RUN bash -c "source $SDKMAN_DIR/bin/sdkman-init.sh && \
    java -version && \
    gradle --version && \
    grails --version"

# Créer un répertoire pour les applications
WORKDIR /app

# Exposer le port par défaut pour les applications Grails
EXPOSE 8080

# Point d'entrée par défaut
CMD ["bash"]