node {
	stage 'Checkout repository'

	git url: 'git@bitbucket.org:YrlishTeam/statistician.git'
	sh 'git clean -fdx'

	def mvnHome = tool 'mvn'

	dir('Plugin') {
		stage ('Build Plugin') {
			sh "${mvnHome}/bin/mvn package"
		}

		stage ('Test plugin') {
			parallel 'test': {
				sh "${mvnHome}/bin/mvn test"
			}, 'verify': {
				sh "${mvnHome}/bin/mvn verify"
			}		
		}

		stage ('Archive plugin') {
			pom = readMavenPom file: 'pom.xml'
			
			def v = version()
			sh "mv target/Statistician.jar target/Statistician-${v}-b${env.BUILD_NUMBER}.jar"
			archive 'target/*.jar'   
		}
	}
}

def version() {
	def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
	matcher ? matcher[0][1] : null
}