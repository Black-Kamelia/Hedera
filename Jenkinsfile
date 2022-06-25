pipeline {
    agent any

    stages {
        stage('Clean') {
            steps {
                withGradle {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean --no-daemon'
                }
            }
        }
        stage('Build') {
            steps {
                withGradle {
                    sh './gradlew build --no-daemon'
                }
            }
        }
    }
}
