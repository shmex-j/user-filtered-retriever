# StackOverflow users retriever
 This app using the [StackExchange API](https://api.stackexchange.com) to retrieve
the list of stack overflow users meeting the following criteria:
  1. Are located in Romania or Moldova
  2. Have a reputation of min 223 points.
  3. Answered min 1 question
  4. Have the tags: "java",".net","docker" or "C#" 

## Technologies used
  - [Suqareup Retrofit v2.9.0](https://square.github.io/retrofit/)
  - [Jackson v2.14.2](https://github.com/FasterXML/jackson)
  - [Guava v31.1-jre](https://github.com/google/guava)
  - Gradle
  - [Detekt gradle plugin](https://github.com/detekt/detekt)

## SetUp Guide
### Setting up
You can clone repository via IDE, wait for Gradle sync and run Main.kt

### Launching
Once you launched the application, you can define user search ranges by entering the start and end page, or leave this options blank to run through whole users list.
After that, wait for the app to retrieve users that matches criteria and print them in console.
