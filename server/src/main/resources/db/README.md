# Database migrations

Considering the application will evolve with time, we use Liquibase to handle database migrations.

Each migration is called a changelog and is stored in the `src/main/resources/db/changelog` folder.
To ensure there will be no conflict in naming and identifying the changelogs, we use the following naming convention:

```
YYYYMMDD-#-name.xmml
```

Where:
- `YYYYMMDD` is the date the migration was created
- `#` is the number of the migration for that day, starting at 1 and incrementing by 1 for each new migration
- `name` is a short description of the migration, in kebab-case

A single changelog can contain multiple changesets, each one representing a single change to the database.
Ideally, each changeset should be self-contained and not depend on other changesets.
Changelogs should be as concise as possible, meaning that if a big change could be split in multiple changelogs, it should be.

A migration can also contain data to be inserted in the database, which is stored in CSV files in the `src/main/resources/db/changelog/data` folder.
The CSV files should be named after the table they are inserting data into, or if not possible, at least a short and concise name.
Inserting custom data should be avoided as much as possible.
