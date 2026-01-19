# Zork v2

A text-based adventure set on a college campus. Explore 13 interconnected locations, complete multi-part quests (DNA delivery, music performance, wildlife rescue, MacBook return, treadmill sprint), and optimize your moves while managing hunger, food, and dining coupons.

## How to Run

```bash
javac *.java
java Main
```

## Quick Start

Type `help` to see the in-game guide, or jump in with `look` and `go <direction>`.

## Commands

**Navigation:**
- `look` – Describe current location and items
- `go <direction>` – Move north/south/east/west (1 move)
- `jump` – Teleport from Julian, Mason Hall, or Roy Library (2 moves)
- `cross` – Cross from UB to Mason Hall (2 moves)

**Inventory:**
- `take <item>` – Pick up an item
- `take <item> from <container>` – Extract item from a container
- `drop <item>` – Leave an item at current location
- `put <item> in <container>` – Place item into a container
- `inventory` – List carried items

**Interaction:**
- `examine <item>` – Get item details or read Help guide
- `use treadmill` – Start typing sprint at Lilly Building
- `status` (or `score`) – View moves, points, food, coupons, quest progress
- `help` – Locate or read Help guide
- `quit` – Exit

## Core Systems

### Moves & Points
- Each action costs moves (go=1, jump=2, cross=2)
- Complete quests to earn points
- Goal: maximize points, minimize moves

### Dining Locations
- Two dining options available on campus
- First visit locks your food source choice
- Dining foods require coupons (unless earned otherwise)

### Food & Hunger
- Max 3 foods in inventory
- Every 5 moves: hunger eats one food
- No food when hungry: +2 move penalty
- Foods spoil every 5 moves

### Coupons
- Start with 3 coupons
- Required to take dining food (unless unlimited)
- Both biology rescues complete → unlimited coupons

## Quests

Discover and complete interconnected tasks across campus. Each quest has unique mechanics—some require resource management, others time pressure. The game has multiple paths to victory.

## Tips

- **Examine Help** – Full in-game guide with strategic hints
- **Status Often** – Track moves and points
- **Explore NPCs** – Characters whisper clues about quests
- **Manage Resources** – Plan your food and coupon usage wisely
- **Discover Mechanics** – Some systems reveal themselves as you play

## Author

Built as an improved version of my freshman Software Development class project inspired by the original classic Zork game, which explores quest design, resource management (moves/food/coupons), and time-pressure mechanics (DNA countdown, food decay).
