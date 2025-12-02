pipeline {
    agent any
    
    environment {
        // Configuration Docker Hub
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-credentials'  // ID que vous avez défini
        DOCKER_IMAGE = 'ouss12045/gestionfoyer'  // Votre image Docker Hub
        GIT_REPO = 'https://github.com/oussama-mhennaoui/GestionFoyer.git'
        
        // Tags Docker
        BRANCH_NAME = "${env.BRANCH_NAME ?: 'main'}"
        COMMIT_HASH = ''
    }
    
    stages {
        
        // Étape 1: Récupération du code
        stage('Checkout Git') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: "${env.GIT_REPO}",
                        credentialsId: ''  // Laissez vide si repo public
                    ]],
                    extensions: [[
                        $class: 'CleanBeforeCheckout'
                    ]]
                ])
                
                script {
                    // Récupérer le hash court du commit
                    env.COMMIT_HASH = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    
                    echo "✅ Checkout réussi - Commit: ${env.COMMIT_HASH}"
                }
            }
        }
        
        // Étape 2: Préparation et test
        stage('Préparation') {
            steps {
                script {
                    // Vérifier la présence des fichiers nécessaires
                    if (fileExists('pom.xml')) {
                        echo "📦 Projet Maven détecté"
                        env.PROJECT_TYPE = 'maven'
                    } else if (fileExists('package.json')) {
                        echo "📦 Projet Node.js détecté"
                        env.PROJECT_TYPE = 'node'
                    } else {
                        echo "ℹ️  Type de projet non spécifique"
                        env.PROJECT_TYPE = 'other'
                    }
                    
                    // Lister les fichiers pour débogage
                    sh 'ls -la'
                }
            }
        }
        
        // Étape 3: Build Docker Image
        stage('Build Docker Image') {
            steps {
                script {
                    // Vérifier si Dockerfile existe
                    if (!fileExists('Dockerfile')) {
                        echo "⚠️  Dockerfile non trouvé, création d'un Dockerfile par défaut..."
                        
                        // Créer un Dockerfile minimal selon le type de projet
                        if (env.PROJECT_TYPE == 'maven') {
                            writeFile file: 'Dockerfile', text: '''FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]'''
                        } else if (env.PROJECT_TYPE == 'node') {
                            writeFile file: 'Dockerfile', text: '''FROM node:14-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 3000
CMD ["npm", "start"]'''
                        } else {
                            writeFile file: 'Dockerfile', text: '''FROM nginx:alpine
COPY . /usr/share/nginx/html
EXPOSE 80'''
                        }
                        
                        echo "📄 Dockerfile créé"
                    }
                    
                    // Afficher le contenu du Dockerfile
                    sh 'cat Dockerfile'
                    
                    // Définir les tags
                    def tags = [
                        "${env.DOCKER_IMAGE}:${env.BUILD_ID}",
                        "${env.DOCKER_IMAGE}:${env.COMMIT_HASH}",
                        "${env.DOCKER_IMAGE}:latest"
                    ]
                    
                    // Construire l'image avec plusieurs tags
                    docker.build("${env.DOCKER_IMAGE}:${env.BUILD_ID}")
                    
                    echo "🐳 Image Docker construite avec succès"
                    echo "📦 Tags: ${tags.join(', ')}"
                }
            }
        }
        
        // Étape 4: Push vers Docker Hub
        stage('Push to Docker Hub') {
            steps {
                script {
                    echo "🔐 Connexion à Docker Hub..."
                    
                    // Se connecter à Docker Hub avec vos credentials
                    withCredentials([string(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, variable: 'DOCKER_PASSWORD')]) {
                        sh """
                            docker login -u ouss12045 -p ${DOCKER_PASSWORD}
                        """
                    }
                    
                    // Taguer et pousser l'image
                    def imageTags = [
                        "${env.BUILD_ID}",
                        "${env.COMMIT_HASH}",
                        "latest"
                    ]
                    
                    imageTags.each { tag ->
                        sh """
                            docker tag ${env.DOCKER_IMAGE}:${env.BUILD_ID} ${env.DOCKER_IMAGE}:${tag}
                            docker push ${env.DOCKER_IMAGE}:${tag}
                        """
                        echo "✅ Image poussée avec tag: ${tag}"
                    }
                    
                    echo "🚀 Toutes les images ont été poussées vers Docker Hub"
                }
            }
        }
        
        // Étape 5: Nettoyage
        stage('Cleanup') {
            steps {
                script {
                    // Supprimer l'image locale pour économiser de l'espace
                    sh "docker rmi ${env.DOCKER_IMAGE}:${env.BUILD_ID} || true"
                    
                    // Nettoyer les containers arrêtés et images intermédiaires
                    sh 'docker system prune -f --filter "until=24h"'
                    
                    echo "🧹 Nettoyage terminé"
                }
            }
        }
    }
    
    post {
        always {
            echo "📊 Pipeline terminé - Build #${env.BUILD_NUMBER}"
            
            // Archivage des logs Docker
            sh 'docker images | grep ${DOCKER_IMAGE} || true' > docker-images.txt
            archiveArtifacts artifacts: 'docker-images.txt', fingerprint: true
        }
        
        success {
            echo "🎉 SUCCÈS: Pipeline terminé avec succès!"
            echo "📦 Image disponible sur Docker Hub: ${env.DOCKER_IMAGE}"
            echo "🏷️  Tags: latest, ${env.BUILD_ID}, ${env.COMMIT_HASH}"
            
            // Vous pouvez ajouter des notifications ici
            // emailext, slackSend, etc.
        }
        
        failure {
            echo "❌ ÉCHEC: Pipeline en échec"
            echo "🔍 Consultez les logs pour plus de détails"
            
            // Envoyer une notification d'échec
            // emailext subject: "Échec du build ${env.JOB_NAME}",
            //          body: "Le build #${env.BUILD_NUMBER} a échoué.\nURL: ${env.BUILD_URL}"
        }
        
        unstable {
            echo "⚠️  Pipeline instable"
        }
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }
    
    triggers {
        // Déclenchement automatique sur push GitHub
        pollSCM('H/5 * * * *')  // Vérifie toutes les 5 minutes
    }
}