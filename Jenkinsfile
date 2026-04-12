pipeline {
    agent any

    environment {
        HOME            = '/var/lib/jenkins'
        XDG_RUNTIME_DIR = '/tmp/runtime-jenkins'
    }

    triggers {
        cron('5,35 * * * *')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 25, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Inject config.properties') {
            steps {
                withCredentials([file(credentialsId: 'fixture-finder-config-properties',
                                      variable: 'CONFIG_FILE')]) {
                    sh 'cp "$CONFIG_FILE" "$WORKSPACE/config.properties"'
                }
            }
        }

        stage('VPN Up') {
            steps {
                sh 'sudo wg-quick down wg0 || true'
                sh 'sudo wg-quick up wg0'
            }
        }

        stage('Run Tests') {
            steps {
                sh '/usr/bin/mvn clean test -DsuiteXmlFile=testng.xml'
            }
        }
    }

    post {
        always {
            sh 'sudo wg-quick down wg0 || true'
            archiveArtifacts artifacts: 'logs/*.log', allowEmptyArchive: true
        }
    }
}
