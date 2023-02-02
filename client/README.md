# Hedera client

## Setup

Prefer VSCode to work on this project.

When opening VSCode, you will be prompted to install recommended extensions. Do so and reload.

On your first reload, you will be prompted to disabled some extensions (such as built-in TypeScript language features). Do so and reload.

Install `pnpm` globally if it is not already installed:

```bash
npm install -g pnpm
```

Install dependencies:

```bash
pnpm i
```

## Running in development mode

```bash
pnpm dev
```

## Building for production

```bash
pnpm generate
```

The static output will be generated in the `.output/public/` directory.

## Other scripts

```bash
pnpm nuxi add <template> <name> # Add a new page, component, store, composable, etc.
pnpm taze # Update dependencies /!\ Do that only in a clean and specific branch /!\
```
