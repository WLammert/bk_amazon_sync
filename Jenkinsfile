pipeline {
    agent any

    stages {
        stage('Build') {
            agent {
                docker {
                    image 'gradle:6.0.1-jre11'
                    args '-v "$PWD":/home/gradle/project -w /home/gradle/project gradle gradle <gradle-task>'
                }
            }
            steps {
                timeout(10) {
                    cleanWs()
                    checkout scm

                    sh './gradlew clean build'
                }
            }
            post {
                always {
                    junit '**/build/test-results/**/*.xml'
                }
            }
        }
    }
    post {
        failure {
            emailext body: 'Please go to ${BUILD_URL} and verify the build result', recipientProviders: [brokenBuildSuspects(), brokenTestsSuspects()], subject: 'Job \'${JOB_NAME}\' (${BUILD_NUMBER}) failed.'
        }
        unstable {
            emailext body: 'Please go to ${BUILD_URL} and verify the build result', recipientProviders: [brokenBuildSuspects(), brokenTestsSuspects()], subject: 'Job \'${JOB_NAME}\' (${BUILD_NUMBER}) is unstable.'
        }
    }
}
