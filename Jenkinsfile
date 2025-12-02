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
                    pwd
                    ls -la
                    echo "Target directory:"
                    ls -la target/ || echo "Target directory not found"
                '''
            }
        }
        
        stage('Create Dockerfile') {
            steps {
                sh '''
                    echo "=== CRÉATION DOCKERFILE ==="
                    
                    # Supprimer les anciens fichiers
                    rm -f Dockerfile .dockerignore 2>/dev/null || true
                    
                    # Créer Dockerfile SIMPLE
                    cat > Dockerfile << 'EOF'
# Dockerfile GestionFoyer
FROM eclipse-temurin:11-jre

WORKDIR /app

# Copier le JAR - CHEMIN ABSOLU DEPUIS LE CONTEXTE
COPY target/GestionFoyer-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
                    
                    # Créer .dockerignore
                    cat > .dockerignore << 'EOF'
.git
.gitignore
*.log
target/
.mvn/
.m2/
.idea/
*.iml
EOF
                    
                    echo "=== FICHIERS CRÉÉS ==="
                    ls -la Dockerfile .dockerignore
                    echo ""
                    echo "=== DOCKERFILE CONTENT ==="
                    cat Dockerfile
                '''
            }
        }
        
        stage('Test Docker Build Manually') {
            steps {
                sh '''
                    echo "=== TEST MANUEL DOCKER ==="
                    
                    # Vérifier le contexte
                    echo "Contexte de build:"
                    pwd
                    echo "Fichier JAR:"
                    ls -la target/GestionFoyer-0.0.1-SNAPSHOT.jar || echo "JAR not found!"
                    
                    # Test avec chemin absolu
                    echo "Test COPY avec chemin relatif:"
                    docker build --no-cache --progress=plain -t test-image .
                    
                    # Vérifier l'image
                    docker images | grep test-image || echo "Image not created"
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo "🐳 Construction image Docker..."
                    
                    sh """
                        # Build avec contexte explicite
                        docker build \
                            --file Dockerfile \
                            --tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} \
                            .
                        
                        echo "✅ Image construite"
                        docker images | grep ${env.DOCKER_IMAGE}
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
                    docker images | grep ${env.DOCKER_IMAGE}
                """
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                withCredentials([string(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, variable: 'DOCKER_PASSWORD')]) {
                    sh """
                        # Login
                        echo "\${DOCKER_PASSWORD}" | docker login -u ouss12045 --password-stdin
                        
                        # Push
                        docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
                        docker push ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                        docker push ${env.DOCKER_IMAGE}:latest
                        
                        echo "🎉 Images poussées vers Docker Hub!"
                    """
                }
            }
        }
        
        stage('Cleanup') {
            steps {
                sh '''
                    echo "🧹 Nettoyage..."
                    docker system prune -f
                '''
            }
        }
    }
    
    post {
        always {
            echo "📊 Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}"
        }
        
        success {
            echo """
            🎉 SUCCÈS! Pipeline CI/CD terminé.
            
            Image Docker: ${env.DOCKER_IMAGE}
            Tags: ${env.DOCKER_TAG}, ${env.COMMIT_HASH}, latest
            
            Disponible sur: https://hub.docker.com/r/ouss12045/gestionfoyer
            """
        }
        
        failure {
            echo """
            ❌ ÉCHEC - Debug information:
            
            Testez manuellement:
            cd /var/lib/jenkins/workspace/Webhook
            
            # Option 1: Test simple
            docker build --no-cache .
            
            # Option 2: Avec affichage détaillé
            docker build --progress=plain .
            
            # Option 3: Vérifier le contexte
            tar -czf context.tar.gz .
            echo "Taille du contexte: $(du -h context.tar.gz)"
            """
        }
    }
}