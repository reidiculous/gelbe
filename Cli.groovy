def properties = new Properties()
properties.load(new FileInputStream(System.getProperty('user.dir') + '/gelbe.properties'))
def config = new ConfigSlurper().parse(properties)
def client = new EBClient(config)

def usage = """\
Supported commands:
	list-apps
		list your Elastic Beanstalk apps
	list-versions [app name (optional)]
		list versions of app or all apps
	list-envs [app name (optional)]
		list application environments
	list-stacks
		list availabe EB solution stacks
	create-app-version [app name] \"[version label]\" [war file path]
		upload new WAR version of app, creating app if necessary
	launch-env [app name] [env name] \"[stack]\" \"[version label (optional)]\"
		launch app version on a stack, giving an evironment name
		by not giving a version, the most recent will be used
"""

args = [args].flatten() // make it groovy

switch (args[0]) {
		case 'list-apps':
				client.listApps()
				break

		case 'list-versions':
				client.listVersions(args[1])
				break

		case 'list-envs':
				client.listEnvs(args[1])
				break

		case 'list-stacks':
				client.listStacks()
				break

		case 'create-app-version':
				client.createAppVersion(args[1], args[2], args[3]) // name, version, war
				break

		case 'launch-env':
				client.launchEnv(args[1], args[2], args[3], args[4]) //
				break

		default:
			println usage
}
