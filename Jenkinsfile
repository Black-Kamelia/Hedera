pipeline {
    agent any
    options {
        timestamps()
        ansiColor('xterm')
        timeout(time: 15, unit: 'MINUTES')
        disableConcurrentBuilds()
    }
    tools {
        gradle 'gradle-8.1.1'
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
            }
        }
        stage('Build and test') {
            parallel {
                stage('Back') {
                    stages {
                        stage('Build') {
                            steps {
                                sh 'gradle --parallel server:jar -x client:bundle'
                            }
                        }
                        stage('Test') {
                            steps {
                                sh 'gradle --parallel server:test -x client:bundle'
                            }
                            post {
                                always {
                                    junit checksName: 'Back-end tests', allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                                    recordCoverage sourceDirectories: [[path: 'server/src/main/kotlin']], tools: [[pattern: '**/build/reports/kover/report.xml']]
                                }
                            }
                        }
                    }
                }
                stage ('Front') {
                    stages {
                        stage('Lint') {
                            steps {
                                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                                    script {
                                        def status = sh(script: 'gradle --parallel client:lint', returnStatus: true)
                                        if (status != 0) {
                                            currentBuild.result = 'UNSTABLE'
                                            error 'Lint failed'
                                        }
                                    }
                                    archiveArtifacts artifacts: 'client/eslint-report.html', followSymlinks: false, onlyIfSuccessful: false
                                }
                            }
                            post {
                                always {
                                    archiveArtifacts artifacts: 'client/eslint-report.html', followSymlinks: false, onlyIfSuccessful: false
                                }
                            }
                        }
                        stage('Build') {
                            steps {
                                sh 'gradle --parallel client:build'
                            }
                        }
                        stage('Test') {
                            steps {
                                echo "For now, we don't have any front-end tests"
                            }
                        }
                    }
                }
            }
        }
        stage('Package') {
            when {
                anyOf {
                    branch 'master'
                    branch 'continuous-integration'
                }
            }
            steps {
                sh 'gradle assemble'
                archiveArtifacts artifacts: 'executables/Hedera-*.jar', followSymlinks: false, onlyIfSuccessful: true
            }
        }
        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                dir('./release') {
                    sh 'chmod +x package.sh && ./package.sh'
                    script {
                        docker.withRegistry('', 'docker-hub') {
                            def image = docker.build('bkamelia/hedera:' + env.IMAGE_TAG)
                            image.push()
                        }
                    }
                }
            }
        }
    }
}
