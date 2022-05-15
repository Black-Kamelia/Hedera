# Jellyfish

A self-hosted screenshots/files hosting system.

## 🤔 What is it?

**Jellyfish** is an open-source web application built with **[Ktor](https://ktor.io/)** and **[Nuxt.js](https://nuxtjs.org/)**.
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
- [Nuxt](https://nuxtjs.org/) : JavaScript frontend framework based on Vue.js.

### Building

- [Gradle](https://gradle.org/) : Gradle is a build tool for Java and Kotlin.
- [Docker](https://www.docker.com/) : Docker is a platform for developers to build, ship, and run software.
- [Docker Compose](https://docs.docker.com/compose/overview/) : Docker Compose is a tool for defining and running multi-container applications.
- [Node.js](https://nodejs.org/) v16.5.0 : Node.js is a JavaScript runtime built on Chrome's V8 JavaScript engine.
- [Gradle Node plugin](https://github.com/node-gradle/gradle-node-plugin/) : Gradle plugin for Node.js.
    