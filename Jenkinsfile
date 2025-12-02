pipeline {
    agent any
    
    triggers {
        githubPush()
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
    }
    
    stages {
        stage('📥 Checkout Code') {
            steps {
                checkout scm
                sh 'echo "✅ Code récupéré depuis GitHub"'
            }
        }
        
        stage('🔨 Build Application') {
            steps {
                script {
                    echo "🏗️ Construction de l'application..."
                    // Pour Spring Boot
                    sh 'mvn clean package'
                    // OU pour Node.js: sh 'npm install && npm run build'
                }
            }
        }
        
        stage('🧪 Tests') {
            steps {
                sh 'echo "🧪 Exécution des tests..."'
                sh 'mvn test'
                // OU: sh 'npm test'
            }
        }
        
        stage('📦 Package') {
            steps {
                sh 'echo "📦 Création du package..."'
                sh 'ls -la target/'  // Pour voir le JAR généré
            }
        }
    }
    
    post {
        success {
            echo "✅ Pipeline terminé avec succès !"
        }
        failure {
            echo "❌ Pipeline échoué"
        }
        always {
            echo "🧹 Nettoyage terminé"
        }
    }
}