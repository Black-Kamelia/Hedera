pipeline {
    agent {
        docker {
            image 'gradle:7.6.1-jdk17'
            reuseNode true
        }
    }
    stages {
        stage('Precondition') {
            steps {
                script {
                    def branch = env.CHANGE_BRANCH
                    def target = env.CHANGE_TARGET
                    if (target == 'master' && branch != 'develop') {
                        currentBuild.result = 'ABORTED'
                        error 'Only develop branch can be merged into master'
                    }
                }
                echo 'Warming up Gradle'
                sh 'gradle -q'
            }
        }
        stage('Build and test') {
            parallel {
                stage('Back') {
                    stages {
                        stage('Build') {
                            steps {
                                sh 'gradle --parallel build -x test'
                            }
                        }
                        stage('Test') {
                            steps {
                                sh 'gradle --parallel test'
                            }
                            post {
                                always {
                                    junit checksName: 'Back-end tests', allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                                    publishCoverage adapters: [jacocoAdapter(mergeToOneReport: true, path: '**/build/reports/kover/xml/*.xml')], sourceDirectories: [[path: 'server/src/main/kotlin']], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                                }
                            }
                        }
                    }
                }
                stage ('Front') {
                    stages {
                        stage('Lint') {
                            steps {
                                sh 'gradle --parallel pnpmLint'
                            }
                        }
                        stage('Build') {
                            steps {
                                sh 'gradle --parallel pnpmBuild'
                            }
                        }
                    }
                }
            }
        }
        stage('Package') {
            when {
                branch 'master'
            }
            steps {
                sh 'gradle package'
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                sh 'echo "Push to Docker Hub"'
            }
        }
    }
}
