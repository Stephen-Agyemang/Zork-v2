# Location Background Photos

When a player moves to a location, the game background image changes to match that real DePauw building or place.

## How it works
- Photos go in `frontend/public/` as `.jpg` files
- The filename must match the value in the `LOCATION_IMAGES` map in `frontend/src/App.jsx`
- The background transitions smoothly via a cross-fade animation (`terminal-watermark-in` / `terminal-watermark-out`) over 0.9s
- If no photo exists for a location, it falls back to `school.jpg` (campus aerial)

## Photo needed — filename → location name in game

| Filename to use        | Location name (exact, in-game) | Notes                                      |
|------------------------|--------------------------------|--------------------------------------------|
| `julian.jpg`           | Julian                         | Julian Science Center                      |
| `hoover.jpg`           | Hoover                         | Hoover Dining Hall                         |
| `olin.jpg`             | Olin                           | Asbury Hall / Olin Biological Sciences     |
| `gcpa.jpg`             | GCPA                           | Green Center for the Performing Arts       |
| `roylibrary.jpg`       | Roy Library                    | Roy O. West Library                        |
| `cdi.jpg`              | CDI                            | Center for Diversity and Inclusion         |
| `lilly.jpg`            | Lilly Building                 | Lilly Center (gym/wellness)                |
| `duck.jpg`             | The Fluttering Duck            | The Fluttering Duck restaurant             |
| `ub.jpg`               | The Union Building             | Hubbard Hall / Union Building              |
| `admin.jpg`            | Administration Building        | East College (main admin building)         |
| `mason.jpg`            | Mason Hall                     | Mason Hall residence                       |
| `reese.jpg`            | Reese Hall                     | Reese Hall residence                       |
| `humbert.jpg`          | Humbert Hall                   | Humbert Hall residence                     |
| `school.jpg`           | (fallback)                     | Used when a location has no dedicated photo |

## Current status
- [x] All photos added, including the fallback

## How to add a photo
1. Take or find a photo of the building
2. Rename it to the filename in the table above
3. Drop it into `frontend/public/`
4. That's it — no code changes needed

## Tips
- Landscape orientation works best (wider than tall)
- Outdoor shots with the building clearly visible look great
- DePauw's official website and Instagram are good sources