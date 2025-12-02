// SUPPRIMEZ L'ESPACE AVANT "pipeline" !
pipeline {  // <-- Doit commencer à la première colonne
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
            }
        }
        
        stage('🐳 Build Docker') {
            steps {
                script {
                    echo "🏗️ Construction de l'image Docker..."
                    sh 'docker build -t votre-app:latest .'
                }
            }
        }
        
        stage('📦 Push to Docker Hub') {
            environment {
                DOCKERHUB_TOKEN = credentials('docker-hub-token')  // <-- Vérifiez l'ID
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
            sh 'docker system prune -f || true'
            echo "🧹 Nettoyage terminé"
        }
    }
}