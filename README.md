<div align="center">

![Hedera logo](img/Hedera_light.svg#gh-light-mode-only)
![Hedera logo](img/Hedera_dark.svg#gh-dark-mode-only)

<h3><i>A self-hosted screenshots/files hosting system.</i></h3>

</div>

## 🤔 What is it?

**Hedera** is an open-source web application built with **[Ktor](https://ktor.io/)** and **[Nuxt 3](https://nuxt.com/)**.
It is the next iteration of [Selfish](https://github.com/SlamaFR/Selfish). Like its predecessor, it is highly inspired
by [XBackBone](https://github.com/SergiX44/XBackBone).

You can see Hedera like a self-hosted Imgur. You simply take screenshots and host them on your Hedera server.

But it's much more than that. It's also a file manager, and gallery editor and viewer !

👉 YES. Hedera will also be shipped in a Docker image.

## ✨ Features

- **User accounts** — You can create multiple accounts, or let users sign up.
  Each screenshots uploaded is bound to its user account.
- **Disk quota** — You can set a disk quota not to exceed ; this way you can upload files without worrying about stuffing your disk completely.
  You can choose to set a custom quota for each user, or allow unlimited storage.
- **Auto-deletion** — Imagine being unable to upload a fantastic screenshot because you're about to exceed your quota? No problem, auto-deletion will delete the oldest files to free some space.
- **[ShareX](https://getsharex.com/) integration** — ShareX is an awesome open-source software to take screenshots or record your screen.
  You can download a [custom uploader](https://getsharex.com/docs/custom-uploader) file on your profile page to allow ShareX to upload your screenshots directly to your Hedera account.

## 🛠 Installation

*Coming soon*

## 🏗️ Building

### Front-end

Building the front-end is pretty straightforward. 
You just need to run the dedicated Gradle tasks.
This tasks install Node.js, sets everything up through pNPM and builds the front-end.

```bash
gradle pnpmBuild
```

### Back-end

Building the back-end is also very easy.
Simply run the default Gradle build task.
This task will compile the back-end and run the tests.

```bash
gradle build
```

### Full build

To build a complete production-ready JAR, run the package task.
This tasks bundles the front-end, and compiles the JAR using the shadow plugin.

```bash
gradle package
```

## ⚙ How to update?

*Coming soon*

## 📸 Screenshots

*Coming soon*

## 📦 Dependencies

### Core

- [Kotlin](https://kotlinlang.org/) : A statically typed, modern, cross-platform, open-source programming language.
- [Ktor](https://ktor.io/) : Kotlin backend microservices library.
- [Exposed](https://github.com/JetBrains/Exposed) : Kotlin database ORM library.
- [Nuxt 3](https://nuxt.com/) : JavaScript frontend framework for making performant web applications, based on Vue 3.
- [VueUse](https://vueuse.org/) : A set of useful Vue 3 Composable functions and components.
- [Primevue](https://primevue.org/) : A collection of rich UI components for Vue 3.
- [UnoCSS](https://unocss.dev/) : A utility-first CSS framework for rapidly building custom user interfaces.
- [Axios](https://axios-http.com) : A fast Promise based HTTP client for the browser and Node.js.

### Building

- [Gradle](https://gradle.org/) : A build tool for Java and Kotlin.
- [Docker](https://www.docker.com/) : A platform for developers to build, ship, and run software.
- [Docker Compose](https://docs.docker.com/compose/overview/) : A tool for defining and running multi-container applications.
- [Node.js](https://nodejs.org/) : A JavaScript runtime built on Chrome's V8 JavaScript engine.
- [Vite](https://vitejs.dev/) : A JavaScript build tool for modern web development.
- [Gradle Node plugin](https://github.com/node-gradle/gradle-node-plugin/) : Gradle plugin for Node.js.
    
