pipeline {
    agent any

    stages {
        stage('Grant permission') {
            steps {
                sh 'chmod u+x ./gradlew'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew shadowJar'
            }
        }
        stage('Upload artifacts') {
            steps {
                script {
                    archiveArtifacts artifacts: 'build-outputs/*.jar'
                }
            }
        }
    }
}