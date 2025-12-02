pipeline {
    agent any // Définit où la pipeline va s'exécuter

    environment {
        // Définition de variables d'environnement
        DOCKER_IMAGE = 'ouss12045/gestionfoyer::latest'
        DOCKER_REGISTRY = 'https://index.docker.io/v1/' // Pour Docker Hub
    }

    stages {
        // ÉTAPE 1 : Récupération du code
        stage('Checkout Git') {
            steps {
                checkout scm // Récupère le code depuis le SCM qui a déclenché le build
            }
        }

        // ÉTAPE 2 : Construction du projet (ex: Maven, NPM, Make...)
        stage('Build Project') {
            steps {
                // Exemple pour un projet Node.js
                sh 'npm ci' // Installation propre
                sh 'npm run build' // Construction
                // Pour Java/Maven : sh 'mvn clean compile'
                // Pour un simple site : sh 'echo "Build step"'
            }
        }

        // ÉTAPE 3 : Construction de l'image Docker
        stage('Build Docker Image') {
            steps {
                script {
                    // Construit l'image en utilisant le Dockerfile présent dans le repo
                    docker.build(DOCKER_IMAGE)
                }
            }
        }

        // ÉTAPE 4 : Publication (Push) de l'image Docker
        stage('Push Docker Image') {
            steps {
                script {
                    // S'authentifie auprès du registre en utilisant les credentials stockés
                    docker.withRegistry(DOCKER_REGISTRY, 'docker-hub-credentials') {
                        // Pousse l'image construite
                        docker.image(DOCKER_IMAGE).push()
                        // Optionnel : pousser aussi un tag avec le numéro de build
                        docker.image(DOCKER_IMAGE).push("${env.BUILD_NUMBER}")
                    }
                }
            }
        }
    }

    // Section optionnelle pour des actions post-build (nettoyage, notification)
    post {
        success {
            echo 'Pipeline réussie ! Image Docker publiée.'
        }
        failure {
            echo 'La pipeline a échoué.'
        }
    }
}