pipeline {
    options {
        disableConcurrentBuilds(abortPrevious: true)
        timestamps()
        ansiColor('xterm')
        timeout(time: 15, unit: 'MINUTES')
    }

    stages {
        // stage('Precondition') {
        //     agent {
        //         docker {
        //             image 'gradle:8.1.0-jdk17'
        //         }
        //     }
        //     steps {
        //         script {
        //             def branch = env.CHANGE_BRANCH
        //             def target = env.CHANGE_TARGET
        //             if (target == 'master' && branch != 'develop') {
        //                 currentBuild.result = 'ABORTED'
        //                 error 'Only develop branch can be merged into master'
        //             }
        //         }
        //         echo 'Warming up Gradle'
        //         sh 'gradle --parallel -q'
        //     }
        // }
        // stage('Build and test') {
        //     agent {
        //         docker {
        //             image 'gradle:8.1.0-jdk17'
        //         }
        //     }
        //     parallel {
        //         stage('Back') {
        //             stages {
        //                 stage('Build') {
        //                     steps {
        //                         sh 'gradle --parallel server:jar -x client:bundle'
        //                     }
        //                 }
        //                 stage('Test') {
        //                     steps {
        //                         sh 'gradle --parallel server:test -x client:bundle'
        //                     }
        //                     post {
        //                         always {
        //                             junit checksName: 'Back-end tests', allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
        //                             publishCoverage adapters: [jacocoAdapter(mergeToOneReport: true, path: '**/build/reports/kover/xml/*.xml')], sourceDirectories: [[path: 'server/src/main/kotlin']], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
        //                         }
        //                     }
        //                 }
        //             }
        //         }
        //         stage ('Front') {
        //             stages {
        //                 stage('Lint') {
        //                     steps {
        //                         catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
        //                             script {
        //                                 def status = sh(script: 'gradle --parallel client:lint', returnStatus: true)
        //                                 if (status != 0) {
        //                                     currentBuild.result = 'UNSTABLE'
        //                                     error 'Lint failed'
        //                                 }
        //                             }
        //                             archiveArtifacts artifacts: 'client/eslint-report.html', followSymlinks: false, onlyIfSuccessful: false
        //                         }
        //                     }
        //                 }
        //                 stage('Build') {
        //                     steps {
        //                         sh 'gradle --parallel client:build'
        //                     }
        //                 }
        //                 stage('Test') {
        //                     steps {
        //                         echo "For now, we don't have any front-end tests"
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }
        // stage('Package') {
        //     agent {
        //         docker {
        //             image 'gradle:8.1.0-jdk17'
        //         }
        //     }
        //     //when {
        //     //    anyOf {
        //     //        branch 'master'
        //     //        branch 'continuous-integration'
        //     //        triggeredBy 'TimerTrigger'
        //     //    }
        //     //}
        //     steps {
        //         sh 'gradle assemble'
        //         archiveArtifacts artifacts: 'executables/Hedera-*.jar', followSymlinks: false, onlyIfSuccessful: true
        //     }
        // }
        stage('Deploy') {
            agent {
                docker {
                    image 'docker:cli'
                }
            }
            parallel {
                stage('Stable') {
                    when {
                        branch 'master'
                    }
                    steps {
                        sh 'echo "Push to Docker Hub"'
                    }
                }
                stage('Nightly') {
                    //when {
                    //    allOf {
                    //        branch 'develop'
                    //        triggeredBy 'TimerTrigger'
                    //    }
                    //}
                    steps {
                        sh 'chmod +x ./release/package.sh'
                        withCredentials([string(credentialsId: 'docker-hub-token', variable: 'token')]) {
                            sh 'docker login -u bkamelia -p $token'
                        }
                        sh './release/package.sh'
                    }
                }
            }
        }
    }
}
