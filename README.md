<div align="center">

![Hedera logo](img/Hedera_light.svg#gh-light-mode-only)
![Hedera logo](img/Hedera_dark.svg#gh-dark-mode-only)

<h3><i>A self-hosted screenshots/files hosting system.</i></h3>

[![Jenkins](https://shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.black-kamelia.com%2Fjob%2FHedera%2Fjob%2FHedera%2Fjob%2Fdevelop%2F&label=Build)
](https://ci.black-kamelia.com/job/Hedera/job/Hedera/job/develop/lastBuild/)
[![Jenkins Tests](https://shields.io/jenkins/tests?jobUrl=https%3A%2F%2Fci.black-kamelia.com%2Fjob%2FHedera%2Fjob%2FHedera%2Fjob%2Fdevelop%2F&label=Tests)
](https://ci.black-kamelia.com/job/Hedera/job/Hedera/job/develop/lastBuild/testReport/)
[![Jenkins Coverage](https://shields.io/jenkins/coverage/apiv4?jobUrl=https%3A%2F%2Fci.black-kamelia.com%2Fjob%2FHedera%2Fjob%2FHedera%2Fjob%2Fdevelop%2F&label=Coverage)
](https://ci.black-kamelia.com/job/Hedera/job/Hedera/job/develop/lastBuild/coverage/)

</div>

## 🤔 What is it?

> [!IMPORTANT]
> Hedera is currently in developement phase. As such, no stable release nor Docker image is available as of now.
>
> You can run a dev build using Docker with this image: [`bkamelia/hedera:nightly`](https://hub.docker.com/r/bkamelia/hedera) *(Updated every night GMT+1)*

**Hedera** is an open-source web application built with **[Ktor](https://ktor.io/)** and **[Nuxt 3](https://nuxt.com/)**.
It is the next iteration of [Selfish](https://github.com/SlamaFR/Selfish). Like its predecessor, it is highly inspired
by [XBackBone](https://github.com/SergiX44/XBackBone).

You can see Hedera like a self-hosted Imgur. You simply take screenshots and host them on your Hedera server.

> [!NOTE]
> Hedera is expected to be released soon. It will be shipped as a standalone JAR and a Docker image ready to deploy.

## ✨ Features

- **User accounts**<br>
  You can create multiple accounts, or let users sign up.
  Each screenshots uploaded is bound to its user account.
- **Disk space quota**<br>
  You can set a disk quota not to exceed; this way you can upload files without worrying about stuffing your disk completely.
  You can choose to set a custom quota for each user, or allow unlimited storage.
- **Files visibility**<br>
  Uploaded a screenshot you would like to keep private? No problem, just change this one's visibility and you're done. Other files will get the default visibility you set.
- **Custom links**<br>
  Want to send your favorite meme but can't remember the random gigberish link? Set a custom link to type it directly.
- **Advanced filters**<br>
  A while back, you uploaded a file you're struggling to find again? Just set a bunch of filters to refine your search.
- **Third-party software integrations**<br>
  Use third-party applications to take screenshots and upload them directly to Hedera.<br>
  Supported: [ShareX](https://getsharex.com/), [uPic](https://github.com/gee1k/uPic), *Apple Shortcut (Coming soon)*

And a lot more to come later... Stay tuned 👀

## 🛠 Installation

*Coming soon*

## ⚙ How to update?

*Coming soon*

## 📸 Screenshots

*Coming soon*

## 🏗️ Building

### Front-end

Building the front-end is pretty straightforward. 
You just need to run the dedicated Gradle tasks.
This tasks install Node.js, sets everything up through pNPM and builds the front-end.

```bash
gradle client:build
```

### Back-end

Building the back-end is also very easy.
Simply run the default Gradle build task.
This task will compile the back-end and run the tests.

```bash
gradle server:build
```

### Full build

To build a complete production-ready JAR, run the package task.
This tasks bundles the front-end, and compiles the JAR using the shadow plugin.

```bash
gradle assemble
```

## 📦 Dependencies

### Core

- [Kotlin](https://kotlinlang.org/) : A statically typed, modern, cross-platform, open-source programming language.
- [Ktor](https://ktor.io/) : Kotlin backend microservices library.
- [Exposed](https://github.com/JetBrains/Exposed) : Kotlin database ORM library.
- [Nuxt 3](https://nuxt.com/) : JavaScript frontend framework for making performant web applications, based on Vue 3.
- [VueUse](https://vueuse.org/) : A set of useful Vue 3 Composable functions and components.
- [Primevue](https://primevue.org/) : A collection of rich UI components for Vue 3.

### Building

- [Gradle](https://gradle.org/) : A build tool for Java and Kotlin.
- [Docker](https://www.docker.com/) : A platform for developers to build, ship, and run software.
- [Docker Compose](https://docs.docker.com/compose/overview/) : A tool for defining and running multi-container applications.
- [Node.js](https://nodejs.org/) : A JavaScript runtime built on Chrome's V8 JavaScript engine.
- [Vite](https://vitejs.dev/) : A JavaScript build tool for modern web development.
- [Gradle Node plugin](https://github.com/node-gradle/gradle-node-plugin/) : Gradle plugin for Node.js.

## 🖤 Acknowledgements

Special thanks to [Ghozy Muhtarom](https://dribbble.com/byghozy) for his work on the illustrations used throughout the application.
    
