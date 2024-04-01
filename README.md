# MagneticCave_Minimax_AI
An AI program that can play a similar game to connect-4 (called Magnetic Cave), and is able to make decisions in under 3 seconds

---
## Demo Gameplay
https://github.com/MahmoudKH02/MagneticCave_Minimax_AI/assets/164411438/798d8855-e033-4ca8-9ebd-703ba5857ee6

---
## Game Rules
1. The game consists of two players, "black" and "white", each player is trying to connect 5 consecutive blocks of its color (horizontally, vertically, or diagonally).
2. To insert a block, there should be a block next to it (either to its left or right, but not top or bottom), or you can place a block next to the left or right walls (borders).
3. The game starts with an empty 16x16 board, where insertion will be available only next to the walls.
4. The program is supposed to make decisions in less than 3 seconds.

Enjoy...

## Implementation
### Minimax
* This program was implemented mainly using the minimax algorithm with alpha-beta pruning.
* Since the minimax looks in the search space in depth (could pick deep solutions over shallow solutions), first the minimax is run on shallow depth (`depth=2`), then the algorithm is run again on a greater depth (`depth=7`).
* Then the two solutions are compared, and the one with the greater score is picked.

### Evaluate Function (score function)
The evaluate function considers a lot of aspects, such as the number of consecutive blocks for each player.

The score will exponentially increase or decrease by the number of consecutive blocks depending if the blocks are for the opponent (user), or player (the program itself).
