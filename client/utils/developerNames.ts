const developers = [
  'ada.lovelace',
  'alan.turing',
  'bob.kahn',
  'claude.shannon',
  'dennis.ritchie',
  'doug.engelbart',
  'edsger.dijkstra',
  'grace.hopper',
  'jack.kilby',
  'john.backus',
  'john.von-neumann',
  'ken.thompson',
  'linus.torvalds',
  'marvin.minsky',
  'maurice.wilkes',
  'niklaus.wirth',
  'richard.stallman',
  'seymour.cray',
  'steve.jobs',
  'ted.nelson',
  'vannevar.bush',
]

export function getRandomDeveloperName() {
  const randomIndex = Math.floor(Math.random() * developers.length)
  return developers[randomIndex]
}
