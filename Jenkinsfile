pipeline {
    agent any
    
    tools {
        // Utilise les outils configurés dans Jenkins
        jdk 'JAVA_17'
        maven 'Maven-3.6'
    }
    
    environment {
        DOCKERHUB_CREDENTIALS_ID = 'docker-hub-credentials'
        DOCKER_IMAGE = 'ouss12045/gestionfoyer'
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }
    
    stages {
        
        stage('Vérification Outils') {
            steps {
                script {
                    echo "🔧 Vérification des outils installés..."
                    sh '''
                        echo "=== JAVA ==="
                        java -version
                        echo "JAVA_HOME: $JAVA_HOME"
                        
                        echo "=== MAVEN ==="
                        mvn --version
                        
                        echo "=== DOCKER ==="
                        docker --version
                        
                        echo "=== GIT ==="
                        git --version
                        
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
                    extensions: [],
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
                        mvn clean compile -B -q
                        
                        echo "=== PACKAGE ==="
                        mvn package -DskipTests -B -q
                        
                        echo "=== VÉRIFICATION JAR ==="
                        ls -la target/*.jar || echo "Aucun JAR trouvé"
                    '''
                }
            }
        }
        
        stage('Création Dockerfile') {
            steps {
                script {
                    echo "📄 Création Dockerfile..."
                    
                    // Dockerfile ultra simple et fiable
                    writeFile file: 'Dockerfile', text: '''# Dockerfile Spring Boot
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier le JAR
COPY target/*.jar app.jar

# Port
EXPOSE 8080

# Commande
ENTRYPOINT ["java", "-jar", "app.jar"]'''
                    
                    sh 'cat Dockerfile'
                }
            }
        }
        
        stage('Build Docker') {
            steps {
                script {
                    echo "🐳 Construction image Docker..."
                    
                    sh """
                        # Build Docker
                        docker build -t ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} .
                        
                        # Tag supplémentaire
                        docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                        
                        # Lister images
                        docker images | grep ${env.DOCKER_IMAGE} || true
                    """
                }
            }
        }
        
        stage('Push Docker Hub') {
            steps {
                script {
                    echo "🚀 Push vers Docker Hub..."
                    
                    withCredentials([string(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, variable: 'DOCKER_PASSWORD')]) {
                        sh """
                            # Login Docker Hub
                            echo "${DOCKER_PASSWORD}" | docker login -u ouss12045 --password-stdin
                            
                            # Push images
                            docker push ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}
                            docker push ${env.DOCKER_IMAGE}:${env.COMMIT_HASH}
                            
                            # Tag latest
                            docker tag ${env.DOCKER_IMAGE}:${env.DOCKER_TAG} ${env.DOCKER_IMAGE}:latest
                            docker push ${env.DOCKER_IMAGE}:latest
                            
                            echo "✅ Images poussées avec succès!"
                        """
                    }
                }
            }
        }
        
        stage('Nettoyage') {
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
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
        
        success {
            echo "🎉 SUCCÈS!"
            echo "Image: ${env.DOCKER_IMAGE}:${env.DOCKER_TAG}"
        }
        
        failure {
            echo "❌ ÉCHEC - Voir logs"
        }
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
    }
}