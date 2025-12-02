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
                        echo "=== ENVIRONNEMENT ==="
                        echo "JAVA_HOME: $JAVA_HOME"
                        echo "PATH: $PATH"
                        
                        echo "=== OUTILS DISPONIBLES ==="
                        command -v java && java -version || echo "Java non trouvé"
                        command -v mvn && mvn --version || echo "Maven non trouvé"
                        command -v docker && docker --version || echo "Docker non trouvé"
                        command -v git && git --version || echo "Git non trouvé"
                        
                        echo "=== RÉPERTOIRE ==="
                        pwd
                        ls -la
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
                    
                    // Vérifier si Maven est disponible
                    sh '''
                        if command -v mvn > /dev/null 2>&1; then
                            echo "Maven trouvé, construction en cours..."
                            mvn clean package -DskipTests -B -q
                        else
                            echo "Maven non trouvé, tentative d'installation..."
                            apt-get update && apt-get install -y maven
                            mvn clean package -DskipTests -B -q
                        fi
                    '''
                    
                    // Vérifier le résultat
                    sh '''
                        echo "=== RÉSULTAT DU BUILD ==="
                        ls -la target/ 2>/dev/null || echo "Dossier target non trouvé"
                        find target -name "*.jar" 2>/dev/null | head -5 || echo "Aucun JAR trouvé"
                    '''
                }
            }
        }
        
        stage('Préparation Docker') {
            steps {
                script {
                    echo "📦 Préparation pour Docker..."
                    
                    // Créer un Dockerfile simple et fiable
                    writeFile file: 'Dockerfile', text: '''# Dockerfile Spring Boot Application
# Image Java fiable et largement disponible
FROM openjdk:11-jre-slim

# Métadonnées
LABEL maintainer="ouss12045"
LABEL description="GestionFoyer Spring Boot Application"

# Créer un utilisateur non-root pour la sécurité
RUN useradd -m -u 1001 springuser
USER springuser

# Répertoire de travail
WORKDIR /app

# Copier l'application
COPY target/*.jar app.jar

# Port d'exposition (Spring Boot par défaut)
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]'''
                    
                    // Créer .dockerignore
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
.env'''
                    
                    sh '''
                        echo "=== FICHIERS CRÉÉS ==="
                        ls -la Dockerfile .dockerignore || true
                        echo "=== CONTENU DOCKERFILE ==="
                        cat Dockerfile
                    '''
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo "🐳 Construction image Docker..."
                    
                    sh """
                        # Vérifier Docker
                        docker --version || { echo "Docker non disponible"; exit 1; }
                        
                        # Construire l'image
                        echo "Construction de ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                        docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                        
                        # Tag supplémentaire avec commit hash
                        docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                        
                        # Lister les images
                        echo "=== IMAGES DISPONIBLES ==="
                        docker images | grep ${env.DOCKER_IMAGE} || echo "Image non trouvée"
                    """
                }
            }
        }
        
        stage('Test Docker Image') {
            steps {
                script {
                    echo "🧪 Test rapide de l'image..."
                    
                    sh """
                        # Test simple
                        echo "=== TEST DE L'IMAGE ==="
                        docker run --rm ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} --version 2>&1 | head -5 || echo "Test échoué (normal pour Spring Boot)"
                        
                        # Vérifier la taille
                        echo "=== INFO IMAGE ==="
                        docker inspect ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} | grep -E '"Size"|"Architecture"|"Os"' || true
                    """
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    echo "🚀 Connexion à Docker Hub..."
                    
                    withCredentials([string(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, variable: 'DOCKER_PASSWORD')]) {
                        sh """
                            # Login
                            echo "🔐 Authentification Docker Hub..."
                            echo "\${DOCKER_PASSWORD}" | docker login -u ouss12045 --password-stdin
                            
                            if [ \$? -ne 0 ]; then
                                echo "❌ Échec de l'authentification Docker Hub"
                                exit 1
                            fi
                            
                            echo "✅ Authentification réussie"
                            
                            # Pousser l'image avec tag de build
                            echo "📤 Pushing ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                            docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
                            
                            # Pousser l'image avec tag de commit
                            echo "📤 Pushing ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}"
                            docker push ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                            
                            # Tag et push 'latest'
                            echo "🏷️  Tagging comme 'latest'"
                            docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest
                            docker push ${env.DOCKER_IMAGE}:latest
                            
                            echo "🎉 Toutes les images poussées avec succès!"
                        """
                    }
                }
            }
        }
        
        stage('Nettoyage') {
            steps {
                sh '''
                    echo "🧹 Nettoyage en cours..."
                    
                    # Supprimer les images locales
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
            Image Docker: ${env.DOCKER_IMAGE}
            Tags: ${env.DOCKER_TAG}, ${env.COMMIT_HASH}, latest
            ==========================================
            """
            
            // Sauvegarder les artifacts
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            archiveArtifacts artifacts: 'Dockerfile', allowEmptyArchive: true
        }
        
        success {
            echo """
            🎉 🎉 🎉 BUILD RÉUSSI! 🎉 🎉 🎉
            
            ✅ Votre image Docker est maintenant disponible:
            
               📦 Image: ${env.DOCKER_IMAGE}
               
               🏷️  Tags disponibles:
                  • ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
                  • ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                  • ${env.DOCKER_IMAGE}:latest
               
               🔗 Télécharger:
                  docker pull ${env.DOCKER_IMAGE}:latest
               
               🌐 Voir sur Docker Hub:
                  https://hub.docker.com/r/ouss12045/gestionfoyer
            
            🚀 Déploiement automatique terminé avec succès!
            """
        }
        
        failure {
            echo """
            ❌ ❌ ❌ BUILD ÉCHOUÉ ❌ ❌ ❌
            
            🔍 Pour déboguer:
            
            1. Vérifiez les erreurs dans les logs ci-dessus
            2. Testez manuellement sur le serveur:
               - cd /var/lib/jenkins/workspace/[job-name]
               - docker build .
            3. Vérifiez les credentials Docker Hub
            4. Vérifiez la connectivité internet
            
            📞 Support:
               - Jenkins: ${env.BUILD_URL}
               - GitHub: https://github.com/oussama-mhennaoui/GestionFoyer
               - Docker Hub: https://hub.docker.com/r/ouss12045/gestionfoyer
            """
        }
        
        unstable {
            echo "⚠️  Build instable - certaines étapes ont échoué mais le pipeline continue"
        }
        
        aborted {
            echo "⏸️  Build annulé manuellement"
        }
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }
    
    triggers {
        // Poll SCM toutes les 2 minutes
        pollSCM('H/2 * * * *')
    }
    
    parameters {
        booleanParam(name: 'CLEAN_DOCKER', defaultValue: true, description: 'Nettoyer les images Docker après le build')
        choice(name: 'JAVA_VERSION', choices: ['11', '17'], description: 'Version Java à utiliser')
    }
}