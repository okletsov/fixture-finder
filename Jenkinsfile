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
                    def result = sh(
                        script: '''
                            echo "Cycling VPN connection..."
                            sudo wg-quick down wg0 || true
                            sleep 2
                            sudo wg-quick up wg0
                            sleep 3
                            /usr/bin/mvn clean test -DsuiteXmlFile=testng.xml
                        ''',
                        returnStatus: true
                    )

                    if (result != 0) {
                        currentBuild.displayName = "#${env.BUILD_NUMBER} (R)"
                        sh 'cp -r target/surefire-reports target/surefire-reports-run1 || true'

                        // Extract failed test names from testng-failed.xml and strip "(failed)" suffix
                        def failedTests = sh(
                            script: '''
                                grep -oP '(?<=name=")[^"]+(?=\\(failed\\)")' target/surefire-reports/testng-failed.xml | paste -sd ',' -
                            ''',
                            returnStdout: true
                        ).trim()

                        echo "Retrying failed tests: ${failedTests}"

                        def retryResult = sh(
                            script: """
                                sudo wg-quick down wg0 || true
                                sleep 2
                                sudo wg-quick up wg0
                                sleep 3
                                /usr/bin/mvn test -DsuiteXmlFile=testng.xml -Dtestnames="${failedTests}"
                            """,
                            returnStatus: true
                        )

                        if (retryResult != 0) {
                            error("Tests still failing after retry. Check surefire-reports-run1 for original failures.")
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'sudo wg-quick down wg0 || true'
            archiveArtifacts artifacts: 'logs/**, target/surefire-reports*/testng-failed.xml, target/surefire-reports*/index.html', allowEmptyArchive: true
        }
    }
}
