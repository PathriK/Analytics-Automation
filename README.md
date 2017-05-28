# Analytics-Automation
Verifying analytics call using proxy(node.js) and Selenium 

# Pre-Requisite
- Node.js 6.x or higher
- JDK (Developed in 1.5)
- Eclipse
- Chrome Browser (Selenium script written using Chrome Driver)

# Demo Parameters:
 __URL Used__: http://www.samsung.com/in/

 __Analytic URL__: nmetrics.samsung.com (configured in the Selenium/Java )

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
2. Configure ProxyConfig with AnalyticsProxy host, metrics url regexp to capture, external proxy url (optional) & external proxy port (optional)
3. Send 'start' signal to proxy
4. Load Samsung site
5. Send 'stop' signal and get captured analytics data from Proxy
6. Parse and validate the captured data, that pageName is 'in:home'
7. Send 'start' signal to proxy
8. Navigate to the Support page
9. Send 'stop' signal and get captured analytics data from Proxy
10. Parse and validate the captured data, that pageName is 'in:support'
11. End Test

# Commands Available:
__start-proxy:__ Starts the proxy at port 8080 without any debug logs

__debug-proxy:__ Same as start-proxy but logs console with debug infos

# Developer Contact
pathrikumark@gmail.com

https://gitter.im/PathriK

# Change-log:

v1.3.0:

- Changed demo to samsung.com/in (Apple shifted to using https for metrics call which is not supported by
this utility. Hence the change)
- Metrics URL can now be filtered based on Regular Expression (Javascript format)
- Supports configuration of External proxy to use by the Analytics proxy tool

v1.2.0:

- Captures metrics call that is sent using POST

v1.1.0:

- Ability to configure metrics domain through Java (Selenium)
- Separate class to handle proxy communication, making it easy to use it in other projects
- Proxy script now shows the current version in console

v1.0.0:

- Initial Version