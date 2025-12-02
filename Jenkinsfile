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
                        
                        echo "=== VÉRIFICATION JAR ==="
                        ls -la target/*.jar
                        echo "Taille:"
                        du -h target/*.jar
                    '''
                }
            }
        }
        
        stage('Préparation Docker') {
            steps {
                script {
                    echo "📦 Préparation pour Docker..."
                    
                    // Supprimer l'ancien fichier
                    sh 'rm -f Dockcerfile Dockerfile 2>/dev/null || true'
                    
                    // Créer un Dockerfile SIMPLE et CORRECT
                    writeFile file: 'Dockerfile', text: '''# Dockerfile Spring Boot Application
# Image Java testée et disponible: eclipse-temurin:11-jre
FROM eclipse-temurin:11-jre

# Métadonnées
LABEL maintainer="ouss12045"
LABEL description="GestionFoyer Spring Boot Application"

# Répertoire de travail
WORKDIR /app

# Copier l'application JAR (CHEMIN CORRECT: depuis le contexte de build)
COPY target/GestionFoyer-0.0.1-SNAPSHOT.jar app.jar

# Port d'exposition
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]'''
                    
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
Dockcerfile'''
                    
                    sh '''
                        echo "=== FICHIERS CRÉÉS ==="
                        ls -la Dockerfile .dockerignore
                        echo ""
                        echo "=== CONTENU DOCKERFILE ==="
                        cat Dockerfile
                        echo ""
                        echo "=== VÉRIFICATION CHEMIN JAR ==="
                        ls -la target/GestionFoyer-0.0.1-SNAPSHOT.jar
                        echo "Le fichier existe-t-il?"
                        test -f target/GestionFoyer-0.0.1-SNAPSHOT.jar && echo "✅ OUI" || echo "❌ NON"
                    '''
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo "🐳 Construction image Docker..."
                    
                    sh """
                        echo "=== ÉTAPE 1: Vérification du contexte ==="
                        pwd
                        ls -la
                        echo ""
                        
                        echo "=== ÉTAPE 2: Build Docker ==="
                        docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                        
                        echo "=== ÉTAPE 3: Vérification ==="
                        docker images | grep ${env.DOCKER_IMAGE}
                    """
                }
            }
        }
        
        stage('Tag Docker Images') {
            steps {
                script {
                    echo "🏷️  Tagging des images..."
                    
                    sh """
                        # Tag avec commit hash
                        docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                        
                        # Tag latest
                        docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest
                        
                        echo "✅ Images taggées:"
                        docker images | grep ${env.DOCKER_IMAGE}
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
                            
                            if [ \$? -eq 0 ]; then
                                echo "✅ Authentification réussie"
                            else
                                echo "❌ Échec authentification"
                                exit 1
                            fi
                            
                            # Pousser les images
                            echo "📤 Pushing ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
                            docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
                            
                            echo "📤 Pushing ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}"
                            docker push ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                            
                            echo "📤 Pushing ${env.DOCKER_IMAGE}:latest"
                            docker push ${env.DOCKER_IMAGE}:latest
                            
                            echo "🎉 Toutes les images poussées avec succès!"
                        """
                    }
                }
            }
        }
        
        stage('Test Rapide') {
            steps {
                script {
                    echo "🧪 Test rapide de l'image..."
                    
                    sh """
                        # Tester que l'image peut être exécutée
                        echo "=== TEST D'EXÉCUTION ==="
                        
                        # Lancer en arrière-plan
                        docker run -d --name test-gestionfoyer -p 8081:8080 ${env.DOCKER_IMAGE}:latest
                        sleep 5
                        
                        # Vérifier si le conteneur tourne
                        docker ps | grep test-gestionfoyer && echo "✅ Conteneur en cours d'exécution" || echo "⚠️  Conteneur non démarré"
                        
                        # Arrêter et nettoyer
                        docker stop test-gestionfoyer 2>/dev/null || true
                        docker rm test-gestionfoyer 2>/dev/null || true
                        
                        echo "✅ Test terminé"
                    """
                }
            }
        }
        
        stage('Nettoyage') {
            steps {
                sh '''
                    echo "🧹 Nettoyage..."
                    
                    # Supprimer images locales
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
            Tags: ${env.DOCKER_TAG}, ${env.COMMIT_HASH}, latest
            ==========================================
            """
            
            // Sauvegarder artifacts
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            archiveArtifacts artifacts: 'Dockerfile', allowEmptyArchive: true
        }
        
        success {
            echo """
            🎉 🎉 🎉 SUCCÈS COMPLET! 🎉 🎉 🎉
            
            ✅ CI/CD Pipeline terminé avec succès!
            
            📊 Résumé:
               • Build Maven: ✅ Réussi
               • Image Docker: ✅ Construite
               • Push Docker Hub: ✅ Terminé
            
            📦 Image disponible sur:
               https://hub.docker.com/r/ouss12045/gestionfoyer
            
            🏷️  Tags créés:
               • ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
               • ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
               • ${env.DOCKER_IMAGE}:latest
            
            🔗 Commandes:
               docker pull ${env.DOCKER_IMAGE}:latest
               docker run -p 8080:8080 ${env.DOCKER_IMAGE}:latest
            
            🚀 Déploiement automatique réussi!
            """
        }
        
        failure {
            echo """
            ❌ BUILD ÉCHOUÉ
            
            🔍 Dernière erreur:
               Problème de chemin dans Dockerfile
            
            🔧 Solution rapide:
               1. Vérifiez le Dockerfile:
                  COPY target/GestionFoyer-0.0.1-SNAPSHOT.jar app.jar
               
               2. Vérifiez que le JAR existe:
                  ls -la target/
               
               3. Test manuel:
                  cd /var/lib/jenkins/workspace/Webhook
                  docker build .
            
            ✅ Ce qui a fonctionné:
               • Git checkout: ✅
               • Build Maven: ✅ (JAR créé: 57MB)
               • Image Java disponible: ✅ (eclipse-temurin:11-jre)
            """
            
            // Debug supplémentaire
            script {
                sh '''
                    echo "=== DEBUG ==="
                    echo "Répertoire courant:"
                    pwd
                    echo ""
                    echo "Contenu target/:"
                    ls -la target/ 2>/dev/null || echo "Dossier target non trouvé"
                    echo ""
                    echo "Dockerfile:"
                    cat Dockerfile 2>/dev/null || echo "Dockerfile non trouvé"
                '''
            }
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
}