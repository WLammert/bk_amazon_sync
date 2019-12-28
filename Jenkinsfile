pipeline {
    agent none
    stages {
        stage('Tests') {
            agent { label 'gradle6_jre11' }
            steps {
                timeout(10) {
                    cleanWs()
                    checkout scm
                    sh 'gradle test pitest'
                }
            }
            post {
                always {
                    junit '**/build/test-results/**/*.xml'
                    publishHTML target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'build/reports/pitest',
                        reportFiles: 'index.html',
                        reportName: 'Mitation Tests Results'
                    ]
                }
            }
        }
    }
}
