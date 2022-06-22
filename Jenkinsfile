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
        stage('Build server') {
            steps {
                withGradle {
                    sh './gradlew assemble --no-daemon'
                }
            }
        }
        stage('Build client') {
            steps {
                withGradle {
                    sh './gradlew npmClean npmBuild --no-daemon'
                }
            }
        }
        stage('Test') {
            steps {
                withGradle {
                    sh './gradlew test --no-daemon'
                }
            }
        }
    }
}
