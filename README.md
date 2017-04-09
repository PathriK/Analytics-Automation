# Analytics-Automation
Verifying analytics call using proxy(node.js) and Selenium 

# Pre-Requisite
- Node.js 6.x or higher
- JDK (Developed in 1.5)
- Eclipse
- Chrome Browser (Selenium script written using Chrome Driver)

# Demo Parameters:
 __URL Used__: http://www.apple.com

 __Analytic URL__: metrics.apple.com (configured in the Selenium/Java )

# Steps to view the Demo:
1. Clone or download the Repo to local
2. Navigate to 'Server' folder in command prompt
3. Run command 'npm install -g'
4. After successful install, we have two commands (details below)
5. Run command desired command and wait for prompt 'server listening on 8080'
6. Import 'Selenium' folder to Eclipse. It is a maven project.
7. Do Maven update to download maven dependencies
8. Run the project as a Java Project

# Selenium Flow:
1. Driver initialization with proxy details
2. Send 'start' signal to proxy
3. Load Apple site
4. Send 'stop' signal and get captured analytics data from Proxy
5. Parse and validate the captured data, that pageName is 'apple'
6. Send 'start' signal to proxy
7. Navigate to the MAC page
8. Send 'stop' signal and get captured analytics data from Proxy
9. Parse and validate the captured data, that pageName is 'mac'
10. End Test

# Commands Available:
__start-proxy:__ Starts the proxy at port 8080 without any debug logs

__debug-proxy:__ Same as start-proxy but logs console with debug infos

# Developer Contact
pathrikumark@gmail.com

https://gitter.im/PathriK

# Change-log:

v1.1.0:

- Ability to configure metrics domain through Java (Selenium)
- Separate class to handle proxy communication, making it easy to use it in other projects
- Proxy script now shows the current version in console

v1.0.0:

- Initial Version