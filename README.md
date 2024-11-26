# World-Building-Exploration-Game

This project will create an engine for generating explorable worlds. We have designed and implemented a 2D tile-based world exploration engine. The user can explore the generated world through walking and interacting with various elements in the world. 

We will be using various data structures and algorithms to create the map itself and to generate the rooms and hallways. Each map is randomly generated with set seeds and saving options for users to go back to an previous map. Implementing character selection option in the main menu, the character will have the the corresponding appearances when going in the direction specified by the user. 

UI features/behavior: 

1) A 2D grid of tiles showing the current state of the world, that uses pixelated images to simulate a more aesthetic environment rather than uni-code symbols. 
2) A “Heads Up Display” (HUD) that provides additional information that maybe useful to the user. This includes Text that describes what tile the mouse is currently hovering over, different instructional keys, and the point value the player has currently accumulated. 
3) Players can save most recent run through and return to that same map and progress. 

Interactivity:

1) We have created a hidden mode that only lights up a select radius of tiles around the player's character.
2) There are random puzzles across the board that the user can solve and collect points. The hidden feature is almost available in these puzzles. 
3) User can also generate at will within a map into a new map.

<img width="599" alt="Screenshot 2024-11-25 at 4 43 52 PM" src="https://github.com/user-attachments/assets/a6688724-24ad-428f-9df6-a0244f188cfd">
