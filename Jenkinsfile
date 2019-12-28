pipeline {
    agent none
    stages {
        stage('Tests') {
            agent { label 'gradle6_jre11' }
            steps {
                timeout(10) {
                    cleanWs()
                    checkout scm
                    sh 'gradle test pitest jacocoTestReport'
                }
            }
            post {
                always {
                    junit '**/build/test-results/**/*.xml'
                }
            }
        }
    }
}
