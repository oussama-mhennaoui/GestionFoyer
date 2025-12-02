pipeline {
    agent any
    
    triggers {
        // Déclenchement automatique par webhook GitHub
        githubPush()
    }
    
    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    
    stages {
        stage('📥 Checkout Code') {
            steps {
                checkout scm
                sh 'echo "✅ Code récupéré depuis GitHub"'
                sh 'ls -la'
            }
        }
        
        stage('🧪 Tests') {
            steps {
                sh 'echo "🚀 Exécution des tests..."'
                // Ajoutez vos commandes de test ici
                // Ex: mvn test, npm test, pytest, etc.
            }
        }
        
        stage('🐳 Build Docker') {
            steps {
                script {
                    echo "🏗️ Construction de l'image Docker..."
                    // Assurez-vous d'avoir un Dockerfile dans votre repo
                    sh 'docker build -t votre-app:latest .'
                }
            }
        }
        
        stage('📦 Push to Docker Hub') {
            environment {
                DOCKERHUB_TOKEN = credentials('docker-hub-credentials')
            }
            steps {
                script {
                    echo "🚀 Push vers Docker Hub..."
                    sh '''
                        echo $DOCKERHUB_TOKEN | docker login -u ouss12045 --password-stdin
                        docker tag votre-app:latest ouss12045/votre-app:latest
                        docker push ouss12045/votre-app:latest
                        docker logout
                    '''
                }
            }
        }
        
        stage('🚀 Déploiement') {
            steps {
                echo "🎯 Déploiement..."
                // Ajoutez vos étapes de déploiement ici
                // Ex: kubectl apply, docker-compose up, etc.
            }
        }
    }
    
    post {
        success {
            echo "✅ Pipeline terminé avec succès !"
            // Notification Slack/Email/etc.
        }
        failure {
            echo "❌ Pipeline échoué"
            // Notification d'erreur
        }
        always {
            sh 'docker system prune -f || true'
            echo "🧹 Nettoyage terminé"
        }
    }
}