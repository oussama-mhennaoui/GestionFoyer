pipeline {
    agent any
    
    // DÉCLENCHEMENT AUTO SUR PUSH GIT
    triggers {
        pollSCM('*/2 * * * *')  // Vérifie toutes les 2 minutes
    }
    
    tools {
        maven 'M3'
        jdk 'jdk17'
    }
    
    environment {
        DOCKER_IMAGE_NAME = 'ouss12045/gestionfoyer'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        // ÉTAPE 1 : RÉCUPÉRER LE CODE
        stage('📥 CHECKOUT CODE') {
            steps {
                checkout scm
                sh 'echo "✅ Code récupéré depuis GitHub"'
            }
        }
        
        // ÉTAPE 2 : VÉRIFIER LES OUTILS
        stage('🛠️ VERIFY TOOLS') {
            steps {
                sh '''
                    echo "=== OUTILS DISPONIBLES ==="
                    echo "1. Java:"
                    java -version
                    echo ""
                    echo "2. Maven:"
                    mvn --version
                    echo ""
                    echo "3. Docker:"
                    docker --version
                '''
            }
        }
        
        // ÉTAPE 3 : BUILD SPRING BOOT
        stage('🔨 BUILD SPRING BOOT') {
            steps {
                sh '''
                    echo "🏗️ Construction de l'application Spring Boot..."
                    mvn clean compile
                    echo "✅ Compilation réussie"
                '''
            }
        }
        
        // ÉTAPE 4 : EXÉCUTER LES TESTS
        stage('🧪 RUN TESTS') {
            steps {
                sh '''
                    echo "🧪 Exécution des tests..."
                    mvn test
                    echo "✅ Tests terminés"
                '''
            }
        }
        
        // ÉTAPE 5 : CRÉER LE JAR
        stage('📦 CREATE JAR') {
            steps {
                sh '''
                    echo "📦 Création du fichier JAR..."
                    mvn package -DskipTests
                    echo "✅ JAR créé:"
                    ls -lh target/*.jar
                '''
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        // ÉTAPE 6 : BUILD DOCKER IMAGE
        stage('🐳 BUILD DOCKER IMAGE') {
            steps {
                script {
                    echo "🏗️ Construction de l'image Docker..."
                    
                    // Vérifie que Dockerfile existe
                    sh '''
                        echo "Vérification du Dockerfile..."
                        if [ -f "Dockerfile" ]; then
                            echo "✅ Dockerfile trouvé"
                            cat Dockerfile
                        else
                            echo "⚠️ Pas de Dockerfile, création d'un simple..."
                            cat > Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
                        fi
                    '''
                    
                    // Build l'image Docker
                    sh """
                        docker build -t ${env.DOCKER_IMAGE_NAME}:${env.DOCKER_TAG} .
                        docker tag ${env.DOCKER_IMAGE_NAME}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE_NAME}:latest
                        echo "✅ Images Docker créées:"
                        docker images | grep ${env.DOCKER_IMAGE_NAME}
                    """
                }
            }
        }
        
        // ÉTAPE 7 : PUSH VERS DOCKER HUB
        stage('📤 PUSH TO DOCKER HUB') {
            environment {
                // UTILISE LES CREDENTIALS JENKINS
                DOCKERHUB_CREDS = credentials('docker-hub-credentials')
            }
            steps {
                script {
                    echo "🚀 Pushing to Docker Hub..."
                    
                    sh '''
                        echo "Login to Docker Hub..."
                        echo $DOCKERHUB_CREDS | docker login -u ouss12045 --password-stdin
                        
                        echo "Pushing images..."
                        docker push ouss12045/gestionfoyer:${BUILD_NUMBER}
                        docker push ouss12045/gestionfoyer:latest
                        
                        docker logout
                        echo "✅ Images pushed successfully!"
                    '''
                }
            }
        }
        
        // ÉTAPE 8 : NETTOYAGE
        stage('🧹 CLEANUP') {
            steps {
                sh '''
                    echo "🧹 Nettoyage des images temporaires..."
                    docker image prune -f
                    echo "✅ Nettoyage terminé"
                '''
            }
        }
    }
    
    post {
        success {
            echo '🎉 🎉 🎉 PIPELINE COMPLET RÉUSSI! 🎉 🎉 🎉'
            echo '✅ Application Spring Boot construite'
            echo '✅ Image Docker créée et pushée'
            echo "📦 Image disponible: ouss12045/gestionfoyer:${BUILD_NUMBER}"
            echo "📦 Latest: ouss12045/gestionfoyer:latest"
        }
        failure {
            echo '❌ ❌ ❌ PIPELINE ÉCHOUÉ ❌ ❌ ❌'
            echo 'Vérifie les logs pour comprendre l\'erreur'
        }
        always {
            sh 'echo "🏁 Pipeline terminé à $(date)"'
            sh 'echo "=== RÉSUMÉ ==="'
            sh 'docker images | grep gestionfoyer || true'
        }
    }
}