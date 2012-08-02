@Grab(group='com.amazonaws', module='aws-java-sdk', version='1.3.14')

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.*
import com.amazonaws.services.s3.model.*
import com.amazonaws.services.elasticbeanstalk.*
import com.amazonaws.services.elasticbeanstalk.model.*

class EBClient {
		String accessKey
		String secretKey
		String s3Bucket
		String region
		AWSElasticBeanstalkClient ebClient
		AmazonS3Client s3Client

		EBClient(config) {
				accessKey = config.accessKey
				secretKey = config.secretKey
				s3Bucket = config.s3Bucket
				region = config.region

				ebClient = new AWSElasticBeanstalkClient(new BasicAWSCredentials(accessKey, secretKey))
				ebClient.setEndpoint('elasticbeanstalk.' + region + '.amazonaws.com')

				s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey))
				if (region == 'us-east-1') {
					s3Client.setEndpoint('s3.amazonaws.com')
				} else {
					s3Client.setEndpoint('s3-' + region + '.amazonaws.com')
				}
		}

		void listApps() {
				ebClient.describeApplications(new DescribeApplicationsRequest()).getApplications().each { a ->
						def description = a.getDescription() ?: 'no description'
						println "\t" + a.getApplicationName() + ' - ' + description
				}
		}

		void listVersions(appName) {
				def req
				if (appName) {
						req = new DescribeApplicationVersionsRequest().withApplicationName(appName)
				} else {
						req = new DescribeApplicationVersionsRequest()
				}
				ebClient.describeApplicationVersions(req).getApplicationVersions().each { v ->
						println "\t" + v.applicationName + ": '${v.versionLabel}'"
				}
		}

		void listEnvs(appName) {
				def req
				if (appName) {
						req = new DescribeEnvironmentsRequest().withApplicationName(appName)
				} else {
						req = new DescribeEnvironmentsRequest()
				}
				ebClient.describeEnvironments(req).getEnvironments().each { e ->
						println """\
env: ${e.environmentName}
	app/version: ${e.applicationName}/${e.versionLabel}
	stack: ${e.solutionStackName}
	CNAME: ${e.CNAME}
	status/health: ${e.status}/${e.health}
"""
				}
		}

		void listStacks() {
				ebClient.listAvailableSolutionStacks().getSolutionStacks() .each { s ->
						println "\t" + s
				}
		}

		// auto creates app
		void createAppVersion(appName, versionLabel, warPath) {
				def s3Key = appName + '/' + versionLabel.replaceAll(~/\s+/,'-') + '.war'
				uploadWar(s3Key, warPath)
				def req = new CreateApplicationVersionRequest(appName, versionLabel)
				req.setAutoCreateApplication(true)
				req.setSourceBundle(new S3Location(s3Bucket, s3Key))
				logTask("Creating new version of application ${appName}") {
						ebClient.createApplicationVersion(req)
				}
		}

		// versionLabel optional
		void launchEnv(appName, envName, stack, versionLabel) {
			def req = new CreateEnvironmentRequest(appName, envName).withSolutionStackName(stack)
			if (versionLabel) {
				req.setVersionLabel(versionLabel)
			}
			def result
			logTask("Creating new environment ${envName} for app ${appName}") {
				result = ebClient.createEnvironment(req)
			}
			println "App will be avabile at ${result.getCNAME()}"
		}


		void uploadWar(key, warPath) {
				establishBucket()
				logTask("Uploading WAR file to S3:'${s3Bucket}/${key}'") {
						s3Client.putObject(s3Bucket, key, new File(warPath))
				}
		}

		void establishBucket() {
				if (!s3Client.doesBucketExist(s3Bucket)) {
						if (region == 'us-east-1') {
								logTask("Creating S3 bucket ${s3Bucket}") {
										s3Client.createBucket(s3Bucket)
								}
						} else {
								logTask("Creating S3 bucket ${s3Bucket} in region ${region}") {
										s3Client.createBucket(s3Bucket, region)
								}
						}
				}
		}

		void logTask(message, task) {
				print message + '... '
				task()
				println '✔'
		}
}