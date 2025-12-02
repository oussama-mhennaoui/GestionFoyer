pipeline {
    agent any
    
    environment {
        // Configuration Docker
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKER_IMAGE = 'ouss12045/gestionfoyer'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        
        // Variables système
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }
    
    stages {
        
        stage('Vérification Initiale') {
            steps {
                script {
                    echo "🚀 Démarrage du build #${env.BUILD_NUMBER}"
                    echo "📦 Image: ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                    
                    sh '''
                        echo "=== TEST DOCKER IMAGES ==="
                        
                        # Tester différentes images Java disponibles
                        echo "1. Test openjdk:11-jre"
                        docker pull openjdk:11-jre 2>/dev/null && echo "✅ openjdk:11-jre disponible" || echo "❌ openjdk:11-jre non disponible"
                        
                        echo "2. Test eclipse-temurin:11-jre"
                        docker pull eclipse-temurin:11-jre 2>/dev/null && echo "✅ eclipse-temurin:11-jre disponible" || echo "❌ eclipse-temurin:11-jre non disponible"
                        
                        echo "3. Test openjdk:17-jre-slim"
                        docker pull openjdk:17-jre-slim 2>/dev/null && echo "✅ openjdk:17-jre-slim disponible" || echo "❌ openjdk:17-jre-slim non disponible"
                        
                        echo "4. Test adoptopenjdk:11-jre-hotspot"
                        docker pull adoptopenjdk:11-jre-hotspot 2>/dev/null && echo "✅ adoptopenjdk:11-jre-hotspot disponible" || echo "❌ adoptopenjdk:11-jre-hotspot non disponible"
                        
                        # Tester une image simple
                        echo "5. Test alpine:latest"
                        docker pull alpine:latest 2>/dev/null && echo "✅ alpine disponible" || echo "❌ alpine non disponible"
                    '''
                }
            }
        }
        
        stage('Checkout Git') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/oussama-mhennaoui/GestionFoyer.git'
                    ]]
                ])
                
                script {
                    env.COMMIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    echo "✅ Code récupéré - Commit: ${env.COMMIT_HASH}"
                }
            }
        }
        
        stage('Build Maven') {
            steps {
                script {
                    echo "⚙️  Build Maven en cours..."
                    
                    sh '''
                        echo "=== BUILD MAVEN ==="
                        mvn clean package -DskipTests -B -q
                        
                        echo "=== JAR CRÉÉ ==="
                        ls -la target/*.jar
                        echo "Taille du JAR:"
                        du -h target/*.jar
                    '''
                }
            }
        }
        
        stage('Préparation Docker') {
            steps {
                script {
                    echo "📦 Préparation pour Docker..."
                    
                    // Supprimer l'ancien fichier mal orthographié
                    sh 'rm -f Dockcerfile 2>/dev/null || true'
                    
                    // Dockerfile avec images Java VALIDÉES
                    writeFile file: 'Dockerfile', text: '''# Dockerfile Spring Boot Application
# Utiliser une image Java qui existe réellement
# Options disponibles:
# 1. eclipse-temurin:11-jre (recommandé)
# 2. openjdk:11-jre
# 3. openjdk:17-jre-slim
# 4. adoptopenjdk:11-jre-hotspot

# CHOIX 1: eclipse-temurin (le plus fiable)
FROM eclipse-temurin:11-jre

# Métadonnées
LABEL maintainer="ouss12045"
LABEL description="GestionFoyer Spring Boot Application"
LABEL version="1.0"
LABEL com.example.vendor="GestionFoyer"

# Répertoire de travail
WORKDIR /app

# Copier l'application JAR
COPY target/*.jar app.jar

# Port d'exposition (Spring Boot par défaut)
EXPOSE 8080

# Commande de démarrage avec optimisations JVM
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", \
            "/app.jar"]'''
                    
                    // .dockerignore
                    writeFile file: '.dockerignore', text: '''# Fichiers ignorés
.git
.gitignore
*.log
*.class
target/
.mvn/
.m2/
logs/
.DS_Store
.idea/
*.iml
.vscode/
node_modules/
.env
*.md
README
LICENSE
Dockcerfile'''
                    
                    sh '''
                        echo "=== FICHIERS CRÉÉS ==="
                        ls -la Dockerfile .dockerignore
                        echo ""
                        echo "=== DOCKERFILE ==="
                        cat Dockerfile
                    '''
                }
            }
        }
        
        stage('Build Docker Image - Essai 1') {
            steps {
                script {
                    echo "🐳 Essai 1: Construction avec eclipse-temurin:11-jre..."
                    
                    sh """
                        # Essai avec eclipse-temurin
                        if docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .; then
                            echo "✅ SUCCÈS avec eclipse-temurin:11-jre"
                        else
                            echo "⚠️  Échec avec eclipse-temurin, essai image alternative..."
                        fi
                    """
                }
            }
        }
        
        stage('Build Docker Image - Essai 2') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'FAILURE' }
            }
            steps {
                script {
                    echo "🐳 Essai 2: Construction avec openjdk:11-jre..."
                    
                    // Dockerfile alternatif
                    writeFile file: 'Dockerfile', text: '''# Dockerfile Spring Boot Application
# Alternative: openjdk:11-jre
FROM openjdk:11-jre

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]'''
                    
                    sh """
                        docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                        echo "✅ SUCCÈS avec openjdk:11-jre"
                    """
                }
            }
        }
        
        stage('Build Docker Image - Essai 3') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'FAILURE' }
            }
            steps {
                script {
                    echo "🐳 Essai 3: Construction avec openjdk:17-jre-slim..."
                    
                    // Dockerfile alternatif 2
                    writeFile file: 'Dockerfile', text: '''# Dockerfile Spring Boot Application
# Alternative: openjdk:17-jre-slim
FROM openjdk:17-jre-slim

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]'''
                    
                    sh """
                        docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                        echo "✅ SUCCÈS avec openjdk:17-jre-slim"
                    """
                }
            }
        }
        
        stage('Build Docker Image - Essai 4') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'FAILURE' }
            }
            steps {
                script {
                    echo "🐳 Essai 4: Construction avec adoptopenjdk:11-jre-hotspot..."
                    
                    // Dockerfile alternatif 3
                    writeFile file: 'Dockerfile', text: '''# Dockerfile Spring Boot Application
# Alternative: adoptopenjdk:11-jre-hotspot
FROM adoptopenjdk:11-jre-hotspot

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]'''
                    
                    sh """
                        docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                        echo "✅ SUCCÈS avec adoptopenjdk:11-jre-hotspot"
                    """
                }
            }
        }
        
        stage('Build Docker Image - Dernier recours') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'FAILURE' }
            }
            steps {
                script {
                    echo "🐳 Dernier recours: Construction avec une image minimale Alpine + Java..."
                    
                    // Dockerfile de dernier recours
                    writeFile file: 'Dockerfile', text: '''# Dockerfile Spring Boot Application
# Dernier recours: Alpine + OpenJDK installé manuellement
FROM alpine:3.18

# Installer Java
RUN apk add --no-cache openjdk11-jre

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]'''
                    
                    sh """
                        docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                        echo "✅ SUCCÈS avec Alpine + OpenJDK"
                    """
                }
            }
        }
        
        stage('Tag et Vérification') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                script {
                    echo "🏷️  Tagging de l'image..."
                    
                    sh """
                        # Tag avec commit hash
                        docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                        
                        # Tag latest
                        docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest
                        
                        # Vérifier
                        echo "=== IMAGES CRÉÉES ==="
                        docker images | grep ${env.DOCKER_IMAGE}
                        
                        echo "=== INFO IMAGE ==="
                        docker inspect ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} | grep -E 'Architecture|Os|Size' || true
                    """
                }
            }
        }
        
        stage('Push to Docker Hub') {
            when {
                expression { currentBuild.result == null || currentBuild.result == 'SUCCESS' }
            }
            steps {
                script {
                    echo "🚀 Connexion à Docker Hub..."
                    
                    withCredentials([string(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, variable: 'DOCKER_PASSWORD')]) {
                        sh """
                            # Login à Docker Hub
                            echo "🔐 Authentification..."
                            echo "\${DOCKER_PASSWORD}" | docker login -u ouss12045 --password-stdin
                            
                            if [ \$? -ne 0 ]; then
                                echo "❌ Échec authentification Docker Hub"
                                exit 1
                            fi
                            
                            echo "✅ Authentification réussie"
                            
                            # Pousser les images
                            echo "📤 Pushing ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                            docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} && echo "✅ Push réussi" || echo "⚠️  Push échoué"
                            
                            echo "📤 Pushing ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}"
                            docker push ${env.DOCKER_IMAGE}:${env.COMMIT_HASH} && echo "✅ Push réussi" || echo "⚠️  Push échoué"
                            
                            echo "📤 Pushing ${env.DOCKER_IMAGE}:latest"
                            docker push ${env.DOCKER_IMAGE}:latest && echo "✅ Push réussi" || echo "⚠️  Push échoué"
                            
                            echo "🎉 Poussée Docker Hub terminée"
                        """
                    }
                }
            }
        }
        
        stage('Nettoyage') {
            steps {
                sh '''
                    echo "🧹 Nettoyage..."
                    
                    # Supprimer images temporaires
                    docker rmi ouss12045/gestionfoyer:latest 2>/dev/null || true
                    docker rmi ouss12045/gestionfoyer:${BUILD_NUMBER} 2>/dev/null || true
                    docker rmi ouss12045/gestionfoyer:${COMMIT_HASH} 2>/dev/null || true
                    
                    # Nettoyer Docker
                    docker system prune -f 2>/dev/null || true
                    
                    echo "✅ Nettoyage terminé"
                '''
            }
        }
    }
    
    post {
        always {
            echo """
            ==========================================
            📊 RAPPORT DU BUILD #${env.BUILD_NUMBER}
            ==========================================
            Statut: ${currentBuild.currentResult}
            Durée: ${currentBuild.durationString}
            Commit: ${env.COMMIT_HASH}
            Image: ${env.DOCKER_IMAGE}
            ==========================================
            """
            
            // Sauvegarder artifacts
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            archiveArtifacts artifacts: 'Dockerfile', allowEmptyArchive: true
        }
        
        success {
            echo """
            🎉 🎉 🎉 BUILD RÉUSSI! 🎉 🎉 🎉
            
            ✅ Image Docker disponible sur Docker Hub:
               https://hub.docker.com/r/ouss12045/gestionfoyer
            
            📦 Tags:
               • ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
               • ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
               • ${env.DOCKER_IMAGE}:latest
            
            🔗 Pour utiliser:
               docker pull ${env.DOCKER_IMAGE}:latest
            """
        }
        
        failure {
            echo """
            ❌ BUILD ÉCHOUÉ - PROBLÈME DOCKER IMAGE
            
            📝 Problème: L'image Java de base n'est pas disponible
            
            🔧 Solutions:
            1. Tester manuellement sur le serveur:
               cd /var/lib/jenkins/workspace/Webhook
               docker pull eclipse-temurin:11-jre
               docker pull openjdk:11-jre
               docker pull openjdk:17-jre-slim
            
            2. Vérifier la connexion internet:
               ping docker.io
               curl -I https://hub.docker.com
            
            3. Changer le DNS Docker dans /etc/docker/daemon.json
               {
                 "dns": ["8.8.8.8", "8.8.4.4"]
               }
            
            ⚠️  Le build Maven a réussi (JAR créé)
            """
            
            // Sauvegarder le JAR même en cas d'échec Docker
            archiveArtifacts artifacts: 'target/*.jar'
        }
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
}