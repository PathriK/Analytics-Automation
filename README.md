# Analytics-Automation
Verifying analytics call using proxy(node.js) and Selenium 

# Pre-Requisite
- Node.js 6.x or higher
- JDK (Developed in 1.5)
- Eclipse
- Chrome Browser (Selenium script written using Chrome Driver)

# Demo Parameters:
 __URL Used__: http://www.apple.com

 __Analytic URL__: metrics.apple.com (configured in index.js of Proxy Server)

# Steps to view the Demo:
1. Clone or download the Repo to local
2. Navigate to 'Server' folder in command prompt
3. Run command 'node index.js' and wait for prompt 'server listening on 8080'
4. Import 'Selenium' folder to Eclipse. It is a maven project.
5. Do Maven update to download maven dependencies
6. Run the project as a Java Project

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

# Developer Contact
pathrikumark@gmail.com

https://gitter.im/PathriK
