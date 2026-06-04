const BLOCKED = [
  'fuck', 'f*ck', 'fuk', 'fvck',
  'shit', 'sh*t', 'sht',
  'ass', 'arse',
  'bitch', 'b*tch',
  'cunt', 'c*nt',
  'dick', 'd*ck',
  'cock', 'c*ck',
  'pussy', 'p*ssy',
  'nigger', 'nigga', 'n*gger', 'n*gga',
  'faggot', 'fag', 'f*ggot',
  'bastard',
  'whore', 'wh*re',
  'slut', 'sl*t',
  'retard',
  'piss',
  'prick',
  'twat',
  'wanker',
  'bollocks',
]

export function containsProfanity(text) {
  const lower = text.toLowerCase().replace(/[^a-z0-9*]/g, '')
  return BLOCKED.some(word => lower.includes(word.replace(/[^a-z0-9*]/g, '')))
}
