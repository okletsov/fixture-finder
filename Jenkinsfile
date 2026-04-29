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

        stage('Clean Artifacts') {
            steps {
                sh 'rm -rf logs && mkdir -p logs'
            }
        }

        stage('Inject config.properties') {
            steps {
                withCredentials([file(credentialsId: 'fixture-finder-config-properties',
                                      variable: 'CONFIG_FILE')]) {
                    sh 'install -m 644 "$CONFIG_FILE" "$WORKSPACE/config.properties"'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    def retryAttempt = 0
                    
                    retry(2) {
                        retryAttempt++
                        sh '''
                            echo "Cycling VPN connection..."
                            sudo wg-quick down wg0 || true
                            sleep 2
                            sudo wg-quick up wg0
                            sleep 3
                            echo "VPN reconnected. Starting tests..."
                            /usr/bin/mvn clean test -DsuiteXmlFile=testng.xml
                        '''
                    }
                    if (retryAttempt > 1) {
                        currentBuild.displayName = "#${env.BUILD_NUMBER} (R)"
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'sudo wg-quick down wg0 || true'
            archiveArtifacts artifacts: 'logs/**', allowEmptyArchive: true
        }
    }
}
