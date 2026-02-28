pipeline {
    agent any

    tools {
        jdk 'JDK 17' // Jenkins에서 설치된 JDK 이름
    }

    environment {
        JAVA_HOME = tool(name: 'JDK 17', type: 'jdk')
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Permissions') {
            steps {
                sh 'chmod +x gradlew'
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('backslashsonar') {
                    sh './gradlew sonar'
                }
            }
        }

        stage('Build bootJar') {
            steps {
                echo "🛠 Gradle bootJar 빌드 시작..."
                sh './gradlew clean bootJar'
            }
        }

        stage('Archive Artifact') {
            steps {
                echo "📦 JAR 아카이브 저장..."
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            script {
                def jarName = sh(script: "ls build/libs/*.jar | xargs -n 1 basename", returnStdout: true).trim()
                def jobUrl = "${env.BUILD_URL}artifact/build/libs/${jarName}"
                echo "✅ 빌드 성공!"
                echo "📦 다운로드 URL: ${jobUrl}"

                slackSend(
                    color: '#36a64f',
                    message: "*✅ 빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER}*\n" +
                             "• *브랜치:* ${env.GIT_BRANCH ?: 'N/A'}\n" +
                             "• *다운로드:* <${jobUrl}|${jarName}>\n" +
                             "• *커밋:* <${env.BUILD_URL}changes|변경 내역 보기>\n" +
                             "• *콘솔 로그:* <${env.BUILD_URL}console|보기>"
                )
            }
        }

        failure {
            script {
                echo "❌ 빌드 실패!"
                slackSend(
                    color: '#ff0000',
                    message: "*❌ 빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER}*\n" +
                             "• *콘솔 로그:* <${env.BUILD_URL}console|보기>"
                )
            }
        }
    }
}
