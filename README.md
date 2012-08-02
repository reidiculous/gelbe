## gelbe
gelbe is an Amazon Elastic Beanstalk command line utility for Java apps written in Groovy

gelbe is designed to be used alongside the [Amazon Elastic Beanstalk web console](https://console.aws.amazon.com/elasticbeanstalk), which is good for providing an overview of your apps and environments, but poor at tasks like uploading new WAR files

## Usage
    list-apps
      list your Elastic Beanstalk apps

    list-versions [app name (optional)]
      list versions of app or all apps

    list-envs [app name (optional)]
      list application environments

    list-stacks
      list availabe EB solution stacks

    create-app-version [app name] "[version label]" [war file path]
      upload new WAR version of app, creating app if necessary

    launch-env [app name] [env name] "[stack]" "[version label (optional)]"
      launch app version on a stack, giving an evironment name
      by not giving a version, the most recent will be used

## Quick start
1. create `gelbe.properties` in your application's working directory
2. `create-app-version myApp "first version" myApp.war`
3. `launch-env myApp firstEnv "64bit Amazon Linux running Tomcat 7"`

## Notes
Developed on Linux using Groovy 1.8.7

This is my first Groovy project - feedback and pull requests are welcome.