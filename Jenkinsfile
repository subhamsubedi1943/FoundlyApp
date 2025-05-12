pipeline {
    agent {
        docker {
            image 'docker:latest'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    tools {
        maven 'maven1'
    }

    stages {
        stage('Compile and Clean') { 
            steps {
                sh 'mvn compile'
            }
        }

        stage('Junit5 Test') { 
            steps {
                sh 'mvn test'
            }
        }

        stage('Jacoco Coverage Report') {
            steps {
                echo 'TestCoverage'
            }
        }

        stage('SonarQube') {
            steps {
                echo 'Sonar Code Scanning'
            }    
        }

        stage('Maven Build') { 
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Build Docker image') {
            steps {
                sh 'docker build -t foundly --build-arg VER=1.0 .'
            }
        }

        stage('Docker Login') {
            steps {
                sh 'echo "Docker login from console"'
                // Add actual login command using credentials if needed
                // e.g. sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
            }
        }

        stage('Docker Push') {
            steps {
                sh 'docker push ancheroopa/foundly'
            }
        }

        stage('Docker deploy') {
            steps {
                sh 'docker run -itd -p 8080:8080 foundly'
            }
        }
    }
}
