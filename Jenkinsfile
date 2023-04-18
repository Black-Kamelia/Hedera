pipeline {
    agent none
    stages {
        stage('Precondition') {
            agent any
            steps {
                script {
                    def branch = env.CHANGE_BRANCH
                    def target = env.CHANGE_TARGET
                    if (target == 'master' && branch != 'develop') {
                        currentBuild.result = 'ABORTED'
                        error 'Only develop branch can be merged into master'
                    }
                }
            }
        }
        stage('Build') {
            parallel {
                stage('Build Back-end') {
                    agent { docker { image 'gradle:7.6.0-jdk17' }}
                    steps {
                        sh 'gradle --no-daemon build -x test -x bundleClient'
                    }
                }
                stage('Build Front-end') {
                    agent { docker { image 'gradle:7.6.0-jdk17' }}
                    steps {
                        sh 'gradle --no-daemon pnpmBuild'
                    }
                }
            }
        }
        stage('Test') {
            parallel {
                stage('Test Back-end') {
                    agent { docker { image 'gradle:7.6.0-jdk17' }}
                    steps {
                        sh 'gradle --no-daemon test'
                    }
                    post {
                        always {
                            junit checksName: 'Tests', allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                            publishCoverage adapters: [jacocoAdapter(mergeToOneReport: true, path: '**/build/reports/kover/xml/*.xml')], sourceDirectories: [[path: 'server/src/main/kotlin']], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                        }
                    }
                }
                stage('Test Front-end') {
                    agent { docker { image 'gradle:7.6.0-jdk17' }}
                    steps {
                        script {
                            currentBuild.result = 'SUCCESS'
                        }
                    }
                }
            }
        }
        stage('Package') {
            agent { docker { image 'gradle:7.6.0-jdk17' }}
            when {
                branch 'master'
            }
            steps {
                sh 'gradle --no-daemon build -x test -x pnpmBuild'
            }
        }
        stage('Deploy') {
            agent { docker { image 'gradle:7.6.0-jdk17' }}
            when {
                branch 'master'
            }
            steps {
                sh 'echo "Push to Docker Hub"'
            }
        }
    }
}
