pipeline {
    agent any
	tools{
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
        	     steps{
            		//jacoco()
            		echo 'TestCoverage'
		          }
	        }
		stage('SonarQube'){
			steps{
			//	bat label: '', script: '''mvn sonar:sonar \
			//	-Dsonar.host.url=http://CDLVDIDEVMAN500:9000 \
			//	-Dsonar.login=c0909bf6713cd534393d47364d1da553431a220d'''
			echo 'Sonar Code Scanning '
				}	
   			}
        stage('Maven Build') { 
            steps {
                sh 'mvn clean install'
                  }
            }
        stage('Build Docker image'){
           steps {
                   	    sh 'docker build -t  foundly --build-arg VER=1.0 .'
		         }
             }
        stage('Docker Login'){
            steps {
              echo "docker login from console"
            }                
        }
        stage('Docker Push'){
            steps {
                sh 'docker push akanksha030503/foundly'
            }
        }
        stage('Docker deploy'){
            steps {
                sh 'docker run -itd -p  8080:8080 foundly'
             }
        }
    
     
    }
} 
