# POO_GALAGA

![Java](https://img.shields.io/badge/language-Java-blue.svg)
![Status](https://img.shields.io/badge/status-in%20development-orange)
<!-- Add a license badge once defined -->

POO_GALAGA is a modern, object-oriented Java project inspired by the classic Galaga arcade game (originally released in 1981). The goal is to solidify OOP concepts through the development of a functional, modular, and maintainable game, featuring a graphical interface, advanced gameplay mechanics, and multiple design patterns.

---

## Table of Contents

- [Description](#description)
  
- [Design Patterns](#design-patterns)
- [Main Features](#main-features)
- [Controls](#controls)
- [Project Structure](#project-structure)
- [Main Components](#main-components)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Contribution Guide](#contribution-guide)
  
- [Try the Original Galaga](#try-the-original-galaga)
- [License](#license)
- [Credits](#credits)


---

## Description

POO_GALAGA is a modern version of the famous Galaga game, where the player controls a spaceship at the bottom of the screen, moving horizontally and shooting vertically to eliminate waves of enemies. The core objective is to survive as long as possible, facing increasingly challenging enemy formations and attack patterns. Projectiles are limited to simulate the classic Galaga feel. Enemies arrive in organized formations, perform lateral and zigzag movements, and can even descend in kamikaze style, trying to collide with the player or shoot directly.

---

## Design Patterns

The project applies several classic software design patterns to achieve flexibility, scalability, and maintainable code:

1. **Strategy Pattern**  
   Defines and encapsulates different algorithms for enemy behaviors. Interfaces like `IEnemyMovement` and `IAttackStrategy` allow for multiple movement (e.g., circular, zigzag, kamikaze) and attack (e.g., linear, homing, group) strategies. The `EnemyBehavior` class can dynamically switch strategies, enabling easy addition of new enemy behaviors without changing the core code.

2. **Bridge Pattern**  
   Separates game logic from the graphical interface. The `GameManager` interacts with the interface layer via the `IGuiBridge` abstraction, so different GUI implementations (e.g., `SwingGui`) can be swapped without impacting the core game logic.

3. **Observer Pattern (Partial)**  
   Some enemies react to the player's position, simulating an observer relationship (e.g., homing attacks). While not a classical observer implementation, the architecture allows enemies to dynamically adapt to the player's state.

---

## Main Features

- **Classic Arcade Gameplay:** Player controls a spaceship that moves horizontally and shoots enemies above.
- **Enemy Formations & Behaviors:**  
  - Enemies move in organized formations, with lateral and zigzag patterns.
  - Some perform kamikaze dives, while others shoot directly or in groups.
  - Each enemy's behavior is managed by the `EnemyBehavior` class and can be easily expanded.
- **Attack Strategies:**  
  - Direct attack (shooting at the player's current position).
  - Patterned attacks (zigzag, V-shape, etc.).
  - Group attacks (multiple enemies shooting at once).
- **Precise Collision System:**  
  - Uses circular or polygonal colliders for accurate collision detection.
  - Player loses a life when hit; enemies are destroyed and award points when shot.
- **Dynamic Difficulty:**  
  - New attack patterns introduced as the game progresses.
  - Increased frequency and size of enemy waves.
- **Advanced Enemy AI:**  
  - Some enemies "observe" and respond to the player's current position for smarter attacks and movements.
- **Limited Shots:**  
  - The number of active player projectiles is limited, simulating the original Galaga mechanics.
- **Modern Codebase:**  
  - Thread safety with `CopyOnWriteArrayList`.
  - Modularized behaviors, interfaces, and managers.
  - Scheduled executors for precise timing.
  - Geometric calculations for movement and collision.

---

## Controls

- **Arrow Keys:** Move player ship horizontally.
- **Spacebar:** Shoot.
- **P:** Pause game (if implemented).
- **R:** Restart game (if implemented).
- **Mouse Right Click:** Attack.
- **Mouse Left Click:** Dodge.
- **C Key:** Attack.
- **X Key:** Dodge.

---

## Project Structure

```
POO_GALAGA/
├── src/
│   ├── assets/         # Game resources (audio and images)
│   ├── core/           # Core game logic and management
│   ├── geometry/       # Collision system and geometric shapes
│   ├── gui/            # Graphical interface and user input
│   └── test/           # Unit tests and visualizers
├── out/                # Compiled .class files
├── README.md
├── CONTRIBUTING.md     # Contribution guidelines
└── CODE_OF_CONDUCT.md  # Code of conduct
```

---

## Main Components

### Core (Game Core)
- **GameManager:** Central controller. Manages game state, coordinates objects and behaviors, controls score and collisions, integrates GUI and audio, and handles rendering layers.
- **GameEngine:** Game loop engine. Manages main loop, state updates, rendering, and thread synchronization.
- **GameObject:** Base class for all game objects, with transform (position, rotation, scale), collider, shape, and behavior.

### Behavior System
- **Behavior (abstract):** Base for all behaviors.
- **PlayerBehavior:** Controls player actions.
- **EnemyBehavior:** Controls enemy logic and strategy.

### Enemy Movements
- **EnterOverTopMovement:** Enters from the top.
- **EnterSideMovement:** Enters from the side.
- **FlyCircleMovement:** Circular motion.
- **FlyLassoMovement:** Lasso-shaped motion.
- **ZigzagMovement:** Zigzag motion.

### Attack Strategies
- **HomingShootAttack:** Homing projectiles.
- **LinearShootAttack:** Straight-line shots.
- **Group Attacks:** Coordinated or zigzag group attacks.

### Resource System (Assets)
- **ImagesLoader:** Loads/manages sprites & animations (supports GIF and static images, resource caching).
- **AudioLoader & SoundEffects:** Manages sound effects and background music with async audio system.

### Collision System (Geometry)
- Implements basic geometric shapes: circle, polygon, rectangle, triangle.

### Graphical Interface (GUI)
- **SwingGui:** Main interface.
- **GamePanel:** Rendering panel.
- **InputEvent:** Input event system.

---

## Technologies Used

- **Java 8+**
- **Java Swing** (or JavaFX, specify if different)
- **Custom Assets:** All graphical and sound resources in `assets/` by Gabriel Pedroso.
- **Thread-Safety:** Use of `CopyOnWriteArrayList` for safe concurrent operations.
- **Cross-platform:** Compatible with Windows, Linux, and macOS.
- **Recommended IDE:** IntelliJ IDEA, Eclipse, VS Code (optional)

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/kanekitakitos/POO_GALAGA.git
cd POO_GALAGA
```

### 2. Compile the Project

Using command line:
```bash
javac -d bin src/**/*.java
```
Or use your preferred Java IDE to import and build the project.

### 3. Run the Game

```bash
java -cp bin Main
```
> Replace `Main` with the actual name of your main class if different.

---

> **Note:** If the game does not work properly after compiling (for example, if images or sounds are missing), you may need to manually copy the image and audio files from the `assets/` folder to the output directory (`bin/assets` or the equivalent folder used by your IDE). Make sure the resources are accessible at the path expected by the application.

## Contribution Guide

1. Fork the repository.
2. Create a feature branch (`git checkout -b my-feature`)
3. Commit your changes (`git commit -m 'feat: my new feature'`)
4. Push to your branch (`git push origin my-feature`)
5. Open a Pull Request.

See `CONTRIBUTING.md` for more details and best practices.

---


## Try the Original Galaga

Want to experience the classic before playing our modern version?  
You can play the original Galaga (NES version) online here:  
[https://www.retrogames.cz/play_018-NES.php](https://www.retrogames.cz/play_018-NES.php)



---

## License

> **This project is licensed under the "Educational Use Only License".**  
> Use, copying, modification, and distribution are permitted for educational and non-commercial purposes only, as described in the [LICENSE](LICENSE) file.  
> **Commercial use is prohibited without prior permission from the author.**

---

## Credits

- **Documentation:** Miguel Correia
- **Assets (images, sounds, etc.):** Gabriel Pedroso
- **Author:** [kanekitakitos](https://github.com/kanekitakitos)

---
