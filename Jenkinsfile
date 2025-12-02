pipeline {
    agent any
    
    environment {
        // Configuration Docker Hub - UTILISEZ LE BON ID
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKER_IMAGE = 'ouss12045/gestionfoyer'
        DOCKER_REGISTRY = 'https://index.docker.io/v1/'
    }
    
    stages {
        
        // Étape 1: Vérification de l'environnement
        stage('Vérification Environnement') {
            steps {
                script {
                    echo "📋 Informations de build:"
                    echo "- Job: ${env.JOB_NAME}"
                    echo "- Build: ${env.BUILD_NUMBER}"
                    echo "- Workspace: ${env.WORKSPACE}"
                    
                    // Vérifier les outils installés
                    sh '''
                        echo "=== Vérification des outils ==="
                        git --version || echo "Git non installé"
                        docker --version || echo "Docker non installé"
                        pwd
                        ls -la
                    '''
                }
            }
        }
        
        // Étape 2: Checkout Git (SIMPLIFIÉ)
        stage('Checkout Git') {
            steps {
                echo "📥 Récupération du code source..."
                
                // Checkout simple sans configuration complexe
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],  // VOTRE REPO UTILISE 'master', PAS 'main'
                    extensions: [[
                        $class: 'CleanCheckout'
                    ]],
                    userRemoteConfigs: [[
                        url: 'https://github.com/oussama-mhennaoui/GestionFoyer.git'
                    ]]
                ])
                
                // Obtenir le hash du commit
                script {
                    env.COMMIT_HASH = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    
                    env.BRANCH_NAME = sh(
                        script: 'git rev-parse --abbrev-ref HEAD',
                        returnStdout: true
                    ).trim()
                    
                    echo "✅ Checkout réussi"
                    echo "- Branche: ${env.BRANCH_NAME}"
                    echo "- Commit: ${env.COMMIT_HASH}"
                }
            }
        }
        
        // Étape 3: Analyse du projet
        stage('Analyse du Projet') {
            steps {
                script {
                    echo "🔍 Analyse de la structure du projet..."
                    
                    // Lister tous les fichiers
                    sh 'find . -type f -name "*" | head -30'
                    
                    // Vérifier la présence de fichiers spécifiques
                    if (fileExists('Dockerfile')) {
                        echo "✅ Dockerfile trouvé"
                        sh 'cat Dockerfile'
                    } else {
                        echo "⚠️  Dockerfile non trouvé - création d'un Dockerfile basique"
                        
                        // Créer un Dockerfile minimal pour Java Spring
                        writeFile file: 'Dockerfile', text: '''# Dockerfile pour application Java Spring Boot
FROM openjdk:11-jdk-slim

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier de configuration Maven
COPY pom.xml .

# Copier le code source
COPY src ./src

# Build l'application (si c'est un projet Maven)
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Exposer le port
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "target/*.jar"]'''
                        
                        echo "📄 Dockerfile créé avec succès"
                    }
                }
            }
        }
        
        // Étape 4: Build Docker Image
        stage('Build Docker') {
            steps {
                script {
                    echo "🐳 Construction de l'image Docker..."
                    
                    // Construire l'image
                    dockerImage = docker.build(
                        "${env.DOCKER_IMAGE}:${env.BUILD_NUMBER}",
                        "--no-cache ."
                    )
                    
                    // Ajouter un tag avec le hash du commit
                    sh "docker tag ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER} ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}"
                    
                    echo "✅ Image Docker construite:"
                    sh "docker images | grep ${env.DOCKER_IMAGE}"
                }
            }
        }
        
        // Étape 5: Push vers Docker Hub
        stage('Push Docker Hub') {
            steps {
                script {
                    echo "🚀 Poussée vers Docker Hub..."
                    
                    // Se connecter à Docker Hub
                    withCredentials([string(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, variable: 'DOCKER_PASSWORD')]) {
                        sh """
                            echo "Connexion à Docker Hub..."
                            docker login -u ouss12045 -p '${DOCKER_PASSWORD}'
                        """
                    }
                    
                    // Pousser les images
                    sh """
                        echo "Poussée de l'image avec tag: ${env.BUILD_NUMBER}"
                        docker push ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER}
                        
                        echo "Poussée de l'image avec tag: ${env.COMMIT_HASH}"
                        docker push ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                        
                        # Taguer comme 'latest' si sur branche master
                        if [ "${env.BRANCH_NAME}" = "master" ]; then
                            echo "Poussée de l'image avec tag: latest"
                            docker tag ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER} ${env.DOCKER_IMAGE}:latest
                            docker push ${env.DOCKER_IMAGE}:latest
                        fi
                    """
                    
                    echo "🎉 Images poussées avec succès vers Docker Hub!"
                }
            }
        }
        
        // Étape 6: Nettoyage
        stage('Nettoyage') {
            steps {
                script {
                    echo "🧹 Nettoyage des ressources..."
                    
                    // Supprimer les images locales
                    sh """
                        docker rmi ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER} || true
                        docker rmi ${env.DOCKER_IMAGE}:${env.COMMIT_HASH} || true
                        docker system prune -f
                    """
                    
                    echo "✅ Nettoyage terminé"
                }
            }
        }
    }
    
    post {
        always {
            echo "=========================================="
            echo "📋 RÉSUMÉ DU BUILD #${env.BUILD_NUMBER}"
            echo "=========================================="
            echo "Statut: ${currentBuild.currentResult}"
            echo "Durée: ${currentBuild.durationString}"
            echo "Commit: ${env.COMMIT_HASH}"
            echo "Branche: ${env.BRANCH_NAME}"
            echo "Image: ${env.DOCKER_IMAGE}"
            echo "=========================================="
        }
        
        success {
            echo "🎉 🎉 🎉 BUILD RÉUSSI! 🎉 🎉 🎉"
            echo "L'image est disponible sur Docker Hub:"
            echo "👉 https://hub.docker.com/r/ouss12045/gestionfoyer"
            
            // Notification optionnelle
            // emailext to: 'vous@email.com',
            //     subject: "SUCCÈS: Build ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //     body: "L'image ${env.DOCKER_IMAGE} a été construite et poussée avec succès."
        }
        
        failure {
            echo "❌ ❌ ❌ BUILD ÉCHOUÉ ❌ ❌ ❌"
            echo "Consultez les logs pour plus de détails."
            
            // Notification d'échec
            // emailext to: 'vous@email.com',
            //     subject: "ÉCHEC: Build ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //     body: "Le build a échoué. URL: ${env.BUILD_URL}"
        }
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
}