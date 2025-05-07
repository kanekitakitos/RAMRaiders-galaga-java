package gui;

        import core.objectsInterface.IGameObject;
        import core.Shape;
        import java.util.concurrent.CopyOnWriteArrayList;
        import javax.swing.JFrame;
        import javax.swing.SwingUtilities;

        /**
         * A Swing-based GUI implementation for rendering game objects and handling input events.
         * Manages a game window with a custom panel for drawing and input handling.
         *
         * <p>Example usage:</p>
         * <pre>
         *     SwingGui gui = new SwingGui(800, 600);
         *     gui.draw(gameObjects);
         *     IInputEvent input = gui.getInputState();
         * </pre>
         *
         * @preConditions:
         * - Width and height parameters must be positive integers.
         * - The list of game objects passed to the draw method must not be null.
         * - Input handlers must be registered with a valid JFrame.
         *
         * @postConditions:
         * - A game window is created and displayed with the specified dimensions.
         * - Game objects are rendered on the panel.
         * - Input events are captured and can be queried through the input state.
         *
         * @see InputEvent
         *
         * @author Brandon Mejia
         * @version 2025-03-25
         */
        public class SwingGui implements IGuiBridge
        {
            private JFrame frame; // The main game window
            private GamePanel panel; // Custom panel for rendering game objects
            private IInputEvent inputState; // Input event handler

            /**
             * Constructs a SwingGui with the specified dimensions.
             *
             * @param width The width of the game window in pixels.
             * @param height The height of the game window in pixels.
             */
            public SwingGui(int width, int height)
            {
                this.frame = new JFrame("Galaga - RAMRaiders");
                panel = new GamePanel(width, height);
                this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                this.frame.add(panel);
                this.frame.pack();
                this.frame.setLocationRelativeTo(null);
                this.frame.setVisible(true);
                this.frame.setResizable(false);

                this.inputState = new HandlerInputPlayer();
                this.inputState.registerInputHandlers(this.frame);
            }

            /**
             * Constructs a SwingGui with the specified dimensions and a background shape.
             *
             * @param width The width of the game window in pixels.
             * @param height The height of the game window in pixels.
             * @param backgroundShape The shape to use as the background.
             */
            public SwingGui(int width, int height, Shape backgroundShape)
            {
                this.frame = new JFrame("Galaga - RAMRaiders");
                panel = new GamePanel(width, height, backgroundShape);
                this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                this.frame.add(panel);
                this.frame.pack();
                this.frame.setLocationRelativeTo(null);
                this.frame.setVisible(true);
                this.frame.setResizable(false);
            }

            /**
             * Sets a custom input event handler for the GUI.
             *
             * @param inputState The input event handler to set.
             */
            public void setInput(IInputEvent inputState)
            {
                this.inputState = inputState;
                this.inputState.registerInputHandlers(this.frame);
                this.frame.setFocusable(true);
            }

            /**
             * Renders the provided list of game objects on the panel.
             *
             * @param objects A thread-safe list of game objects to render.
             */
            @Override
            public void draw(CopyOnWriteArrayList<IGameObject> objects)
            {
                SwingUtilities.invokeLater(() -> panel.updateGameObjects(objects));
            }

            /**
             * Retrieves the current input state from the GUI.
             *
             * @return The input event handler representing the current input state.
             */
            @Override
            public IInputEvent getInputState()
            {
                return this.inputState;
            }

            /**
             * Toggles the display of hitboxes for game objects.
             *
             * @param hitbox True to show hitboxes, false to hide them.
             */
            public void setHitbox(boolean hitbox)
            {
                this.panel.setHitbox(hitbox);
            }
        }