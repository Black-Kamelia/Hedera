pipeline {
    agent {
        docker {
            image 'gradle:7.6.0-jdk17'
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
                sh 'gradle --quiet -Dorg.gradle.internal.launcher.welcomeMessageEnabled=false'
            }
        }
        stage('Build') {
            parallel {
                stage('Build Back-end') {
                    steps {
                        sh 'gradle -q build -x test -x bundleClient'
                    }
                }
                stage('Build Front-end') {
                    steps {
                        sh 'gradle -q pnpmBuild'
                    }
                }
            }
        }
        stage('Test') {
            parallel {
                stage('Test Back-end') {
                    steps {
                        sh 'gradle -q test'
                    }
                    post {
                        always {
                            junit checksName: 'Tests', allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                            publishCoverage adapters: [jacocoAdapter(mergeToOneReport: true, path: '**/build/reports/kover/xml/*.xml')], sourceDirectories: [[path: 'server/src/main/kotlin']], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                        }
                    }
                }
                stage('Test Front-end') {
                    steps {
                        script {
                            currentBuild.result = 'SUCCESS'
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
                sh 'gradle -q build -x test -x pnpmBuild'
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
