pipeline {
    agent any
    
    triggers {
        // Vérifie GitHub toutes les 1 minutes
        pollSCM('H/1 * * * *')
    }
    
    stages {
        stage('📥 Get Code from GitHub') {
            steps {
                echo '🔄 Checking for new commits on GitHub...'
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/oussama-mhennaoui/GestionFoyer.git'
                    ]]
                ])
                sh 'echo "✅ Latest commit: $(git log --oneline -1)"'
            }
        }
        
        stage('⚙️ Build Java App') {
            steps {
                sh '''
                    echo "📦 Building Spring Boot application..."
                    mvn clean package -DskipTests
                    echo "✅ JAR created: $(ls -lh target/*.jar)"
                '''
            }
        }
        
        stage('🐳 Create Docker Image') {
            steps {
                sh '''
                    echo "📄 Creating Dockerfile..."
                    
                    # Simple Dockerfile
                    cat > Dockerfile << 'END'
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
END
                    
                    echo "🔨 Building Docker image..."
                    docker build -t ouss12045/gestionfoyer:$BUILD_NUMBER .
                    docker tag ouss12045/gestionfoyer:$BUILD_NUMBER ouss12045/gestionfoyer:latest
                    
                    echo "✅ Images ready:"
                    docker images | grep ouss12045/gestionfoyer
                '''
            }
        }
        
        stage('🚀 Push to Docker Hub') {
            steps {
                script {
                    echo "📤 Pushing to Docker Hub..."
                    
                    // ⚠️ REMPLACEZ CE TOKEN PAR LE VÔTRE ! ⚠️
                    def DOCKER_TOKEN = 'dckr_pat__cN4-iLRHlaNwdO_QwIvIDJr9qk'
                    
                    sh """
                        # Login to Docker Hub with token
                        echo "${DOCKER_TOKEN}" | docker login -u ouss12045 --password-stdin
                        
                        # Push images
                        docker push ouss12045/gestionfoyer:$BUILD_NUMBER
                        docker push ouss12045/gestionfoyer:latest
                        
                        echo "🎉 Images pushed successfully!"
                        echo "👉 Check: https://hub.docker.com/r/ouss12045/gestionfoyer"
                    """
                }
            }
        }
    }
    
    post {
        always {
            echo "📊 Build #$BUILD_NUMBER completed: $currentBuild.currentResult"
            sh 'docker system prune -f 2>/dev/null || true'
        }
        
        success {
            echo '''
            🎉🎉🎉 AUTOMATED CI/CD SUCCESS! 🎉🎉🎉
            
            ✅ What happened:
              1. GitHub repo checked ✅
              2. Java app built ✅
              3. Docker image created ✅
              4. Image pushed to Docker Hub ✅
            
            🔗 Your image is now available at:
              https://hub.docker.com/r/ouss12045/gestionfoyer
            
            🏷️ Tags:
              • ouss12045/gestionfoyer:$BUILD_NUMBER
              • ouss12045/gestionfoyer:latest
            
            ⚡ Next commit to GitHub will trigger a new build automatically!
            '''
        }
        
        failure {
            echo '''
            ❌ Build failed
            
            🔧 Quick fixes:
              1. Check Docker Hub token in the script
              2. Test manually: docker login -u ouss12045
              3. Check internet connection
              
            📝 Manual test commands:
              cd /var/lib/jenkins/workspace/Webhook
              mvn clean package
              docker build .
            '''
        }
    }
}