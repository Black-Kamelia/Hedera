const developers = [
  { firstName: 'Ada', lastName: 'Lovelace' },
  { firstName: 'Alan', lastName: 'Turing' },
  { firstName: 'Grace', lastName: 'Hopper' },
  { firstName: 'John', particle: 'von', lastName: 'Neumann' },
  { firstName: 'Konrad', lastName: 'Zuse' },
  { firstName: 'Claude', lastName: 'Shannon' },
  { firstName: 'Seymour', lastName: 'Cray' },
  { firstName: 'John', lastName: 'Backus' },
  { firstName: 'Douglas', lastName: 'Engelbart' },
  { firstName: 'Ken', lastName: 'Thompson' },
  { firstName: 'Dennis', lastName: 'Ritchie' },
  { firstName: 'Edsger', lastName: 'Dijkstra' },
  { firstName: 'Jean', lastName: 'Sammet' },
  { firstName: 'Maurice', lastName: 'Wilkes' },
  { firstName: 'Gary', lastName: 'Kildall' },
  { firstName: 'Seymour', lastName: 'Papert' },
  { firstName: 'Robert', lastName: 'Noyce' },
  { firstName: 'Steve', lastName: 'Jobs' },
  { firstName: 'Bill', lastName: 'Gates' },
]

export function getRandomDeveloper() {
  const randomIndex = Math.floor(Math.random() * developers.length)
  return developers[randomIndex]
}

export function getRandomDeveloperUsername() {
  const developer = getRandomDeveloper()
  let firstName = developer.firstName.replace(' ', '-').toLowerCase()
  if (developer.particle) {
    const particle = developer.particle.replace(' ', '-').toLowerCase()
    firstName = `${firstName}-${particle}`
  }
  const lastName = developer.lastName.replace(' ', '-').toLowerCase()
  return `${firstName}.${lastName}`
}
