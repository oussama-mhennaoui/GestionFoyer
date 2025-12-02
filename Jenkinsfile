pipeline {
    agent any
    
    environment {
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKER_IMAGE = 'ouss12045/gestionfoyer'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }
    
    stages {
        
        stage('Clean Workspace') {
            steps {
                cleanWs()
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
                sh '''
                    echo "=== BUILD MAVEN ==="
                    mvn clean package -DskipTests -B
                    
                    echo "=== VÉRIFICATION ==="
                    ls -la target/*.jar || echo "Aucun JAR trouvé"
                '''
            }
        }
        
        stage('Create Dockerfile') {
            steps {
                sh '''
                    echo "=== CRÉATION DOCKERFILE ==="
                    
                    # Créer Dockerfile SIMPLE
                    cat > Dockerfile << EOF
# Dockerfile GestionFoyer
FROM eclipse-temurin:11-jre

WORKDIR /app

# Copier le JAR
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
                    
                    echo "=== FICHIERS CRÉÉS ==="
                    ls -la Dockerfile
                    echo ""
                    echo "=== DOCKERFILE CONTENT ==="
                    cat Dockerfile
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo "🐳 Construction image Docker..."
                    
                    sh """
                        # Build Docker
                        docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                        
                        echo "✅ Image construite"
                        docker images | grep ${env.DOCKER_IMAGE} || echo "Image non trouvée"
                    """
                }
            }
        }
        
        stage('Tag Images') {
            steps {
                sh """
                    # Tag avec commit hash
                    docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                    
                    # Tag latest
                    docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest
                    
                    echo "✅ Images taggées"
                    docker images | grep ${env.DOCKER_IMAGE} || echo "Aucune image trouvée"
                """
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                withCredentials([string(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, variable: 'DOCKER_PASSWORD')]) {
                    sh """
                        # Login Docker Hub
                        echo "\${DOCKER_PASSWORD}" | docker login -u ouss12045 --password-stdin
                        
                        # Push images
                        docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
                        docker push ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                        docker push ${env.DOCKER_IMAGE}:latest
                        
                        echo "🎉 Images poussées vers Docker Hub!"
                    """
                }
            }
        }
    }
    
    post {
        always {
            echo "📊 Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}"
        }
        
        success {
            echo "🎉 SUCCÈS! Pipeline CI/CD terminé."
            echo "Image Docker: ${env.DOCKER_IMAGE}"
            echo "Tags: ${env.DOCKER_TAG}, ${env.COMMIT_HASH}, latest"
            echo "Disponible sur: https://hub.docker.com/r/ouss12045/gestionfoyer"
        }
        
        failure {
            echo "❌ ÉCHEC - Voir les logs pour plus de détails"
        }
    }
}