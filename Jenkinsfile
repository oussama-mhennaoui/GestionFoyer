pipeline {
    agent any

    environment {
        REGISTRY = "docker.io/ouss12045"
        IMAGE_NAME = "Gestion-Foyer"
        DOCKER_CREDS = credentials('dockerhub-creds')
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/oussama-mhennaoui/GestionFoyer.git'
            }
        }

        stage('Clean Project') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Build Spring Boot App') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $REGISTRY/$IMAGE_NAME:latest .'
            }
        }

        stage('Push Docker Image') {
            steps {
                sh '''
                    echo $DOCKER_CREDS_PSW | docker login -u $DOCKER_CREDS_USR --password-stdin
                    docker push $REGISTRY/$IMAGE_NAME:latest
                '''
            }
        }
    }
}
