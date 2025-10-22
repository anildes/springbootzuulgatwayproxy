This project is Java-based microservices gateway application using Spring Cloud and Zuul. It is configured to route requests (e.g., /student) to backend services and includes OAuth2 integration with Google for authentication. 
This project does a demo of 
OAuth2 Authorization Code Flow with Google
Social Login Integration (Google as identity provider)
API Gateway Authentication pattern
Centralized Auth at Gateway level
Granttype is authorization code 

Follow the videos at https://youtu.be/2fXNkhltmNQ?si=ZNVGsp4g_re3mpAY and https://youtu.be/vfuhwB4Gmac?si=NvQJWZJDK4QtFm-0

How to run this project ?
===============================
Step-by-Step Setup Instructions:
Step 1: Get Google OAuth2 Credentials
Go to Google Cloud Console
Create a new project or select existing one
Navigate to APIs & Services → Credentials
Click Create Credentials → OAuth 2.0 Client IDs
Application type: Web application
Add authorized redirect URI:

text
http://localhost:8080/login/oauth2/code/google
Copy Client ID and Client Secret

Step 2: Update application.properties
Replace in your application.properties:

properties
spring.security.oauth2.client.registration.google.client-id=123456789-abcdefghijklmnopqrstuvwxyz.apps.googleusercontent.com
spring.security.oauth2.client.registration.google.client-secret=GOCSPX-abcdefghijklmnopqrstuvwxyz123456
Step 3: Verify Project Structure
Make sure you have these files:

src/main/resources/
├── application.properties
└── templates/
    ├── login.html
    └── home.html

src/main/java/com/example/springbootzuulgatwayproxy/
├── SpringBootZuulgatwayproxyApplication.java
├── SecurityConfig.java
├── handler/
│   └── CustomOAuth2SuccessHandler.java
└── controller/
    ├── LoginController.java
    └── HomeController.java
    
Step 4: Run and Test
bash
mvn clean compile
mvn spring-boot:run
Step 5: Test the Flow
Open http://localhost:8080
Click "Sign in with Google"
Complete Google authentication
You should be redirected to /home page

Troubleshooting Common Issues:
If OAuth2 fails:
Verify Google Cloud Console redirect URI matches exactly
Check client ID/secret have no extra spaces
Ensure Google+ API is enabled in Google Console

If Zuul routes don't work:
Check target services are running
Verify route paths don't have conflicts
If templates don't render:
Verify Thymeleaf dependency in POM
Check template files are in src/main/resources/templates/

Expected Output When Running:
text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.12.RELEASE)

2024-01-15 INFO: Starting SpringBootZuulgatwayproxyApplication
2024-01-15 INFO: Zuul Gateway running on port 8080
2024-01-15 INFO: OAuth2 configured for Google
The application should now be running with Google OAuth2 authentication protecting your Zuul gateway!

Before Runing this project make sure spring-boot-zuulgatwayproxy-student-service is running which is at https://github.com/anildes/SprinbootwithZuulgworspringCloudgw
after you run that on browser you can give http://localhost:8080/student/echoStudentName/anil and then you should see output something like Hello anil Responsed on : Wed Oct 22 14:04:31 CDT 2025 after you login into the Google account at the prompt 


