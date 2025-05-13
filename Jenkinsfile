pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'jdk-21'
    }
    
    environment {
        DOCKER_REGISTRY = "rakeshsridharahalu2004"
        APP_NAME = "foundly-app2"
        VERSION = "1.0.${BUILD_NUMBER}"
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/${APP_NAME}:${VERSION}"
    }
    
    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/subhamsubedi1943/FoundlyApp.git'
                bat 'dir'
            }
        }
        
        stage('Build Foundly') {
            steps {
                dir('foundly-app2') {
                    bat 'dir'
                    bat 'mvn package -Dmaven.test.skip=true'
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                dir('foundly-app2') {
                    script {
                        bat "docker build -t ${DOCKER_IMAGE} --build-arg JAR_FILE=target/*.jar ."
                    }
                }
            }
        }
        
        stage('Push to DockerHub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        bat "echo %DOCKER_PASSWORD% | docker login -u %DOCKER_USERNAME% --password-stdin"
                        bat "docker push ${DOCKER_IMAGE}"
                    }
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                   
                    bat "docker run -d --name ${APP_NAME} -p 9090:9090 --restart always ${DOCKER_IMAGE}"
                }
            }
        }
    }
    
    post {
        success {
            echo "Build successful! Image: ${DOCKER_IMAGE}"
        }
        failure {
            echo "Build failed. Check logs for details."
        }
        always {
            cleanWs()
        }
    }
}
