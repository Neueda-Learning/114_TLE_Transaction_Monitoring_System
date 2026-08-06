pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '15', artifactNumToKeepStr: '10'))
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code'
                checkout scm
            }
        }

        

    

        stage('Build Docker Images') {
            steps {
                echo 'Building Docker images with docker-compose'
                sh 'docker-compose build'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application stack with docker-compose'
                sh '''
                set -e
                docker-compose down || true
                docker-compose up -d
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo 'Waiting for services to become healthy'
                sh 'sleep 20'
                echo 'Checking backend health endpoint on port 8081'
                sh 'curl --fail --silent --show-error http://localhost:8081/api/simulator/status > /dev/null'
                echo 'Checking frontend availability on port 5173'
                sh 'curl --fail --silent --show-error http://localhost:5173/ > /dev/null'
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Listing running containers after deployment'
                sh 'docker ps'
            }
        }
    }

    post {
        always {
            echo "Pipeline completed with status: ${currentBuild.currentResult}"
            sh 'docker image prune -f || true'
            deleteDir()
        }

        success {
            echo 'Deployment Successful!'
        }

        failure {
            echo 'Deployment Failed!'
        }
    }
}