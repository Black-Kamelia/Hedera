pipeline {
    agent none
    options {
        disableConcurrentBuilds(abortPrevious: true)
        timestamps()
        ansiColor('xterm')
        timeout(time: 15, unit: 'MINUTES')
    }

    stages {
        // stage('Precondition') {
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
            parallel {
                stage('Stable') {
                    when {
                        branch 'master'
                    }
                    agent any
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
                    agent any
                    steps {
                        sh 'chmod +x ./release/package.sh && ./release/package.sh'
                        script {
                            docker.withRegistry('', 'docker-hub') {
                                docker.build('bkamelia/hedera:nightly', '--dockerfile ./release/Dockerfile')
                                docker.push('bkamelia/hedera:nightly')
                            }
                        }
                    }
                }
            }
        }
    }
}
