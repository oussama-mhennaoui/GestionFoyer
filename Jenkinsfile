pipeline {
    agent any

    // Environnement global - variables utilisées dans toute la pipeline
    environment {
        // Variables Docker - MODIFIEZ CES VALEURS
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_HUB_USERNAME = 'ouss12045' // Remplacez !
        DOCKER_IMAGE_NAME = 'gestion-foyer-app'
        DOCKER_IMAGE_TAG = "${env.BUILD_NUMBER}" // Utilise le numéro de build Jenkins
        
        // Construire le nom complet de l'image
        DOCKER_IMAGE_FULL = "${DOCKER_HUB_USERNAME}/${DOCKER_IMAGE_NAME}:latest"
        DOCKER_IMAGE_VERSIONED = "${DOCKER_HUB_USERNAME}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}"
        
        // Variable pour le répertoire de travail
        WORKSPACE_DIR = "${env.WORKSPACE}"
    }

    // Options de la pipeline
    options {
        timeout(time: 30, unit: 'MINUTES') // Timeout après 30 minutes
        buildDiscarder(logRotator(numToKeepStr: '10')) // Garder seulement les 10 derniers builds
    }

    // Déclencheurs (triggers)
    triggers {
        // Déclenchement automatique via webhook GitHub
        // Configuré dans l'interface Jenkins, mais c'est bon de le documenter ici
        pollSCM('') // Vide = pas de polling, seulement webhooks
    }

    // ÉTAPES DE LA PIPELINE
    stages {
        // -----------------------------------------------------------------
        // ÉTAPE 1: VÉRIFICATION DES PRÉREQUIS
        // -----------------------------------------------------------------
        stage('Vérification des outils') {
            steps {
                script {
                    echo "Vérification des outils nécessaires..."
                    echo "Numéro de build: ${env.BUILD_NUMBER}"
                    echo "Branche: ${env.GIT_BRANCH}"
                    echo "Commit: ${env.GIT_COMMIT}"
                    
                    // Vérifier si Node.js/npm est installé
                    sh '''
                        echo "Vérification de l'environnement:"
                        echo "Répertoire de travail: ${WORKSPACE}"
                        echo "User: $(whoami)"
                        echo "Node version: $(node --version 2>/dev/null || echo 'Node.js non installé')"
                        echo "npm version: $(npm --version 2>/dev/null || echo 'npm non installé')"
                        echo "Docker version: $(docker --version 2>/dev/null || echo 'Docker non installé')"
                        echo "Git version: $(git --version)"
                    '''
                }
            }
        }

        // -----------------------------------------------------------------
        // ÉTAPE 2: CHECKOUT DU CODE (déjà fait automatiquement, mais on le refait pour être sûr)
        // -----------------------------------------------------------------
        stage('Checkout Git') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']], // Adaptez à votre branche (main/master)
                    extensions: [
                        // Optionnel: nettoyer le workspace avant le checkout
                        [$class: 'CleanBeforeCheckout'],
                        // Optionnel: supprimer les fichiers non suivis par git après checkout
                        [$class: 'CleanCheckout']
                    ],
                    userRemoteConfigs: [[
                        url: 'https://github.com/oussama-mhennaoui/GestionFoyer.git',
                        // Si votre repo est privé, utilisez des credentials ici
                        // credentialsId: 'github-credentials'
                    ]]
                ])
                
                // Afficher les informations du commit
                sh '''
                    echo "=== INFORMATIONS GIT ==="
                    echo "Dernier commit: $(git log -1 --oneline)"
                    echo "Auteur: $(git log -1 --pretty=format:'%an')"
                    echo "Date: $(git log -1 --pretty=format:'%ad')"
                    echo "Message: $(git log -1 --pretty=format:'%s')"
                '''
            }
        }

        // -----------------------------------------------------------------
        // ÉTAPE 3: INSTALLATION DE NODE.JS (SI NÉCESSAIRE)
        // -----------------------------------------------------------------
        stage('Setup Node.js') {
            steps {
                script {
                    // Option 1: Si vous utilisez un agent avec nvm ou nvs
                    // sh '''
                    //     # Installation de Node.js via nvm (exemple)
                    //     curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
                    //     export NVM_DIR="$HOME/.nvm"
                    //     [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
                    //     nvm install 18  # Installe Node.js 18
                    //     nvm use 18
                    // '''
                    
                    // Option 2: Installation directe de Node.js (pour Ubuntu/Debian)
                    sh '''
                        # Vérifier si Node.js est déjà installé
                        if ! command -v node &> /dev/null; then
                            echo "Node.js non trouvé, installation..."
                            
                            # Pour Ubuntu/Debian
                            curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
                            sudo apt-get install -y nodejs
                            
                            # Vérifier l'installation
                            echo "Node.js version: $(node --version)"
                            echo "npm version: $(npm --version)"
                        else
                            echo "Node.js déjà installé: $(node --version)"
                        fi
                        
                        # Vérifier aussi npm
                        if ! command -v npm &> /dev/null; then
                            echo "npm non trouvé, installation..."
                            sudo apt-get install -y npm
                        fi
                    '''
                }
            }
        }

        // -----------------------------------------------------------------
        // ÉTAPE 4: INSTALLATION DES DÉPENDANCES
        // -----------------------------------------------------------------
        stage('Installation des dépendances') {
            steps {
                sh '''
                    echo "=== INSTALLATION DES DÉPENDANCES ==="
                    echo "Répertoire: $(pwd)"
                    ls -la
                    
                    # Vérifier si package.json existe
                    if [ -f "package.json" ]; then
                        echo "Installation avec npm ci (clean install)..."
                        npm ci --verbose
                        
                        # Alternative si npm ci échoue
                        # npm cache clean --force
                        # rm -rf node_modules package-lock.json
                        # npm install
                    else
                        echo "ERREUR: package.json non trouvé!"
                        exit 1
                    fi
                    
                    # Vérifier l'installation
                    echo "Dépendances installées:"
                    ls -la node_modules | head -20
                '''
            }
        }

        // -----------------------------------------------------------------
        // ÉTAPE 5: CONSTRUCTION DU PROJET
        // -----------------------------------------------------------------
        stage('Build Project') {
            steps {
                sh '''
                    echo "=== CONSTRUCTION DU PROJET ==="
                    
                    # Vérifier les scripts disponibles dans package.json
                    echo "Scripts disponibles dans package.json:"
                    npm run || true
                    
                    # Exécuter le build (adaptez selon votre projet)
                    # Si vous avez un script "build" dans package.json
                    if grep -q '"build"' package.json; then
                        echo "Exécution de npm run build..."
                        npm run build
                        
                        # Vérifier le résultat
                        echo "Contenu après build:"
                        ls -la dist/ || ls -la build/ || echo "Aucun dossier de build trouvé"
                    else
                        echo "Aucun script 'build' trouvé, construction simple..."
                        # Alternative pour les projets sans script build
                        npm test || echo "Pas de tests configurés"
                    fi
                '''
                
                // Archiver les artefacts si nécessaire
                archiveArtifacts artifacts: 'dist/**/* || build/**/*', fingerprint: true
            }
        }

        // -----------------------------------------------------------------
        // ÉTAPE 6: TESTS (OPTIONNEL MAIS RECOMMANDÉ)
        // -----------------------------------------------------------------
        stage('Tests') {
            steps {
                sh '''
                    echo "=== EXÉCUTION DES TESTS ==="
                    
                    # Exécuter les tests si configurés
                    if grep -q '"test"' package.json; then
                        echo "Exécution des tests..."
                        npm test
                    else
                        echo "Aucun script de test configuré"
                        # Tests unitaires simples
                        npm run test:unit || echo "Tests unitaires non configurés"
                    fi
                '''
            }
        }

        // -----------------------------------------------------------------
        // ÉTAPE 7: CONSTRUCTION DE L'IMAGE DOCKER
        // -----------------------------------------------------------------
        stage('Build Docker Image') {
            steps {
                script {
                    echo "=== CONSTRUCTION DE L'IMAGE DOCKER ==="
                    echo "Nom de l'image: ${DOCKER_IMAGE_FULL}"
                    echo "Tag versionné: ${DOCKER_IMAGE_VERSIONED}"
                    
                    // Vérifier que le Dockerfile existe
                    sh '''
                        echo "Vérification des fichiers Docker..."
                        ls -la | grep -i docker
                        if [ -f "Dockerfile" ]; then
                            echo "Dockerfile trouvé:"
                            head -20 Dockerfile
                        else
                            echo "ERREUR: Dockerfile non trouvé!"
                            exit 1
                        fi
                    '''
                    
                    // Construire l'image Docker
                    docker.build("${DOCKER_IMAGE_FULL}")
                    
                    // Tag supplémentaire avec le numéro de build
                    sh """
                        docker tag ${DOCKER_IMAGE_FULL} ${DOCKER_IMAGE_VERSIONED}
                    """
                }
            }
        }

        // -----------------------------------------------------------------
        // ÉTAPE 8: PUBLICATION SUR DOCKER HUB
        // -----------------------------------------------------------------
        stage('Push Docker Image') {
            steps {
                script {
                    echo "=== PUBLICATION SUR DOCKER HUB ==="
                    
                    // S'authentifier et pousser l'image
                    // 'docker-hub-credentials' est l'ID des credentials créés dans Jenkins
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-hub-credentials') {
                        // Pousser le tag 'latest'
                        docker.image("${DOCKER_IMAGE_FULL}").push()
                        
                        // Pousser le tag versionné
                        docker.image("${DOCKER_IMAGE_VERSIONED}").push()
                    }
                    
                    echo "Images publiées avec succès:"
                    echo "  - ${DOCKER_IMAGE_FULL}"
                    echo "  - ${DOCKER_IMAGE_VERSIONED}"
                }
            }
        }

        // -----------------------------------------------------------------
        // ÉTAPE 9: NETTOYAGE (OPTIONNEL)
        // -----------------------------------------------------------------
        stage('Nettoyage') {
            steps {
                sh '''
                    echo "=== NETTOYAGE ==="
                    
                    # Nettoyer les images Docker intermédiaires
                    docker image prune -f || true
                    
                    # Afficher l'espace disque
                    df -h
                    
                    # Lister les images Docker restantes
                    echo "Images Docker sur le système:"
                    docker images | head -10
                '''
            }
        }
    }

    // -----------------------------------------------------------------
    // ACTIONS POST-BUILD
    // -----------------------------------------------------------------
    post {
        // Après un build réussi
        success {
            echo "✅ PIPELINE RÉUSSIE !"
            echo "Build #${env.BUILD_NUMBER} terminé avec succès."
            echo "Image Docker publiée: ${DOCKER_IMAGE_FULL}"
            
            // Optionnel: Notification (email, Slack, etc.)
            // emailext body: "Build ${env.BUILD_NUMBER} réussi!\n\nVoir: ${env.BUILD_URL}", 
            //     subject: "SUCCESS: Build ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //     to: 'votre-email@exemple.com'
        }
        
        // Après un build échoué
        failure {
            echo "❌ PIPELINE ÉCHOUÉE !"
            echo "Build #${env.BUILD_NUMBER} a échoué."
            echo "Voir les logs pour plus de détails: ${env.BUILD_URL}"
            
            // Optionnel: Notification d'échec
            // emailext body: "Build ${env.BUILD_NUMBER} a échoué!\n\nCause possible: ${currentBuild.currentResult}\n\nLogs: ${env.BUILD_URL}console", 
            //     subject: "FAILURE: Build ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //     to: 'votre-email@exemple.com'
        }
        
        // Toujours exécuté, quel que soit le résultat
        always {
            echo "=== RÉCAPITULATIF DU BUILD ==="
            echo "Job: ${env.JOB_NAME}"
            echo "Build: #${env.BUILD_NUMBER}"
            echo "Résultat: ${currentBuild.currentResult}"
            echo "Durée: ${currentBuild.durationString}"
            echo "URL: ${env.BUILD_URL}"
            
            // Nettoyage du workspace (optionnel)
            // cleanWs()
        }
    }
}