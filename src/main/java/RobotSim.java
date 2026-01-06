import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RobotSim {
    public static void main(String[] args) {
        List<String> commands = parseFile(Path.of("src/main/resources/robotInput/inputC.txt"));
        Robot robot = new Robot(5);
        runCommands(robot, commands);
    }

    public enum Direction {
        NORTH(0, 1),
        SOUTH(0, -1),
        EAST(1, 0),
        WEST(-1, 0);

        private final int dx;
        private final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        Direction right() {
            return switch (this) {
                case NORTH -> EAST;
                case EAST -> SOUTH;
                case SOUTH -> WEST;
                case WEST -> NORTH;
            };
        }

        Direction left() {
            return switch (this) {
                case NORTH -> WEST;
                case WEST -> SOUTH;
                case SOUTH -> EAST;
                case EAST -> NORTH;
            };
        }
    }

    static class Robot {
        private int x;
        private int y;
        private Direction facing;
        private boolean placed;
        private final int gridSize;

        Robot(int gridSize) {
            this.gridSize = gridSize;
            this.placed = false;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Direction getDirection() {
            return facing;
        }

        void place(int x, int y, Direction f) {
            if (isValidCoords(x, y)) {
                this.x = x;
                this.y = y;
                this.facing = f;
                this.placed = true;
            } else {
                System.err.println("WARNING: PLACE ignored (coordinates would be out of bounds; X: " + x + ", Y: " + y + ")");
            }
        }

        void move() {
            if (!isPlaced()) {
                System.err.println("WARNING: MOVE ignored (robot not placed yet)");
                return;
            }

            int nextX = x + facing.dx;
            int nextY = y + facing.dy;

            if (isValidCoords(nextX, nextY)) {
                x = nextX;
                y = nextY;
            } else {
                System.err.println("WARNING: MOVE ignored (new coordinates would be out of bounds; X: " + nextX + ", Y: " + nextY + ")");
            }
        }

        void right() {
            if (!isPlaced()) {
                System.err.println("WARNING: RIGHT ignored (robot not placed yet)");
                return;
            }
            this.facing = facing.right();
        }

        void left() {
            if (!isPlaced()) {
                System.err.println("WARNING: LEFT ignored (robot not placed yet)");
                return;
            }
            this.facing = facing.left();
        }

        void report() {
            if (!isPlaced()) {
                System.err.println("WARNING: REPORT ignored (robot not placed yet)");
                return;
            }
            System.out.println(
                    "REPORT - Final coordinates & Direction:\nX: " +
                            x + ", Y: " + y + ", Facing: " + facing + "\n"
            );

        }

        boolean isValidCoords(int x, int y) {
            boolean isValidX = x >= 0 && x <= gridSize - 1;
            boolean isValidY = y >= 0 && y <= gridSize - 1;
            return isValidX && isValidY;
        }

        boolean isPlaced() {
            return placed;
        }
    }

    static List<String> parseFile(Path path) {
        try {
            return Files.readAllLines(path).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Failed to read input file: " + path + ", Reason: " + e);
            return List.of();
        }
    }

    static void handlePlace(Robot robot, String command) {
        String[] splitOnWhiteSpace = command.split(" ");
        if (splitOnWhiteSpace.length != 2) {
            System.err.println("WARNING: Invalid PLACE format (expected 'PLACE X,Y,F'): " + command);
            return;
        }

        String potentialPlaceCommand = splitOnWhiteSpace[0];

        if (potentialPlaceCommand.equals("PLACE")) {
            String[] splitCoordsAndDir = splitOnWhiteSpace[1].split(",");
            if (splitCoordsAndDir.length != 3) {
                System.err.println("WARNING: Invalid PLACE arguments (expected 'X,Y,F'): " + command);
                return;
            }

            try {
                int x = Integer.parseInt(splitCoordsAndDir[0]);
                int y = Integer.parseInt(splitCoordsAndDir[1]);
                Direction f = Direction.valueOf(splitCoordsAndDir[2].trim().toUpperCase());
                robot.place(x, y, f);
            } catch (NumberFormatException e) {
                System.err.println("WARNING: Invalid coordinates in PLACE command (must be integers): " + command);
            } catch (IllegalArgumentException e1) {
                System.err.println("WARNING: Invalid direction in PLACE command (must be NORTH, SOUTH, EAST or WEST): " + command);
            }
        }
    }

    static void runCommands(Robot robot, List<String> commands) {
        Map<String, Runnable> commandsMap = new HashMap<>();
        commandsMap.put("MOVE", robot::move);
        commandsMap.put("LEFT", robot::left);
        commandsMap.put("RIGHT", robot::right);
        commandsMap.put("REPORT", robot::report);

        for (String command : commands) {
            if (command.startsWith("PLACE")) {
                handlePlace(robot, command);
            }

            Runnable action = commandsMap.get(command);
            if (action != null) {
                action.run();
            }
        }
    }
}