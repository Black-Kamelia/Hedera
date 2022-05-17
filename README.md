# Jellyfish

A self-hosted screenshots/files hosting system.

## 🤔 What is it?

**Jellyfish** is an open-source web application built with **[Ktor](https://ktor.io/)** and **[Vue.js](https://vuejs.org/)**.
It is the next iteration of [Selfish](https://github.com/SlamaFR/Selfish). Like its predecessor, it is highly inspired
by [XBackBone](https://github.com/SergiX44/XBackBone).

You can see Jellyfish like a self-hosted Imgur. You simply take screenshots and host them on your Jellyfish server.

👉 YES. Jellyfish will also be shipped in a Docker image.

## ⭐ Features

- **User accounts** — You can create multiple accounts, or let users sign up.
  Each screenshots uploaded is bound to its user account.
- **Disk quota** — You can set a disk quota not to exceed ; this way you can upload files without worrying about stuffing your disk completely.
  You can choose to set a custom quota for each user, or allow unlimited storage.
- **Auto-deletion** — Imagine being unable to upload a fantastic screenshot because you're about to exceed your quota? No problem, auto-deletion will delete the oldest files to free some space.
- **[ShareX](https://getsharex.com/) integration** — ShareX is an awesome open-source software to take screenshots or record your screen.
  You can download a [custom uploader](https://getsharex.com/docs/custom-uploader) file on your profile page to allow ShareX to upload your screenshots directly to your Jellyfish account.

## 🛠 Installation

*Coming soon*

## ⚙ How to update?

*Coming soon*

## 📸 Screenshots

*Coming soon*

## ⇓ Dependencies

### Core

- [Kotlin](https://kotlinlang.org/) : A statically typed, modern, cross-platform, open-source programming language.
- [Ktor](https://ktor.io/) : Kotlin backend microservices library.
- [Exposed](https://github.com/JetBrains/Exposed) : Kotlin database ORM library.
- [Vue 3](https://vuejs.org/) : JavaScript frontend framework for making single-page applications.
- [VueUse](https://vueuse.org/) : A set of useful Vue 3 Composable functions and components.
- [TailwindCSS](https://tailwindcss.com/) : A utility-first CSS framework for rapidly building custom user interfaces.
- [Axios](https://axios-http.com) : A Promise based HTTP client for the browser and Node.js.

### Building

- [Gradle](https://gradle.org/) : A build tool for Java and Kotlin.
- [Docker](https://www.docker.com/) : A platform for developers to build, ship, and run software.
- [Docker Compose](https://docs.docker.com/compose/overview/) : A tool for defining and running multi-container applications.
- [Node.js](https://nodejs.org/) v16.5.0 : A JavaScript runtime built on Chrome's V8 JavaScript engine.
- [Vite](https://vitejs.dev/) : A JavaScript build tool for modern web development.
- [Gradle Node plugin](https://github.com/node-gradle/gradle-node-plugin/) : Gradle plugin for Node.js.
    