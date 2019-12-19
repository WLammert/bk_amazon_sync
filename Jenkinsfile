pipeline {
    agent any
    stages {
        stage('Build') {
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
