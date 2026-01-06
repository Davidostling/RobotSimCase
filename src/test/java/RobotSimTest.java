import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RobotSimTest {

    @Test
    void handleValidPlace() {
        var robot = new RobotSim.Robot(5);
        robot.place(1, 2, RobotSim.Direction.EAST);
        assertEquals(1, robot.getX());
        assertEquals(2, robot.getY());
        assertEquals(RobotSim.Direction.EAST, robot.getDirection());
    }

    @Test
    void handleInvalidPlace() {
        var robot = new RobotSim.Robot(5);
        robot.place(1, 2, RobotSim.Direction.EAST);
        robot.place(5, 5, RobotSim.Direction.NORTH);
        assertEquals(1, robot.getX());
        assertEquals(2, robot.getY());
        assertEquals(RobotSim.Direction.EAST, robot.getDirection());
    }


    @Test
    void handleMultiplePlace() {
        var robot = new RobotSim.Robot(5);
        robot.place(1, 2, RobotSim.Direction.EAST);
        robot.place(5, 5, RobotSim.Direction.NORTH);
        robot.place(4, 4, RobotSim.Direction.WEST);
        robot.place(1, 4, RobotSim.Direction.SOUTH);
        assertEquals(1, robot.getX());
        assertEquals(4, robot.getY());
        assertEquals(RobotSim.Direction.SOUTH, robot.getDirection());
    }

    @Test
    void handleRotationLeft() {
        var robot = new RobotSim.Robot(5);
        robot.place(0, 0, RobotSim.Direction.NORTH);
        robot.left();
        robot.left();
        robot.left();
        robot.left();
        robot.left();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(RobotSim.Direction.WEST, robot.getDirection());
    }

    @Test
    void handleRotationRight() {
        var robot = new RobotSim.Robot(5);
        robot.place(0, 0, RobotSim.Direction.NORTH);
        robot.right();
        robot.right();
        robot.right();
        robot.right();
        robot.right();
        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
        assertEquals(RobotSim.Direction.EAST, robot.getDirection());
    }


    @Test
    void handleMoveX() {
        var robot = new RobotSim.Robot(5);
        robot.place(0, 0, RobotSim.Direction.EAST);
        robot.move();
        robot.move();
        robot.move();
        robot.move();
        robot.move();

        assertEquals(4, robot.getX());
        assertEquals(0, robot.getY());
        robot.right();
        robot.right();

        robot.move();
        robot.move();
        robot.move();
        robot.move();
        robot.move();

        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    @Test
    void handleMoveY() {
        var robot = new RobotSim.Robot(5);
        robot.place(0, 0, RobotSim.Direction.NORTH);
        robot.move();
        robot.move();
        robot.move();
        robot.move();
        robot.move();

        assertEquals(0, robot.getX());
        assertEquals(4, robot.getY());
        robot.right();
        robot.right();

        robot.move();
        robot.move();
        robot.move();
        robot.move();
        robot.move();

        assertEquals(0, robot.getX());
        assertEquals(0, robot.getY());
    }

    @Test
    void handleCommandsWithoutPlace() {
        var robot = new RobotSim.Robot(5);
        robot.move();
        robot.left();
        robot.right();
        robot.report();

        assertFalse(robot.isPlaced());
    }

    @Test
    void handleValidRunCommands() {
        var robot = new RobotSim.Robot(5);
        List<String> commandsList = List.of("PLACE 1,2,EAST", "MOVE", "MOVE", "LEFT", "MOVE", "REPORT");

        RobotSim.runCommands(robot, commandsList);

        assertEquals(3, robot.getX());
        assertEquals(3, robot.getY());
        assertEquals(RobotSim.Direction.NORTH, robot.getDirection());
    }

    @Test
    void handleInvalidInputRunCommands() {
        var robot = new RobotSim.Robot(5);
        List<String> commandsList = List.of("PLACE1,2,EAST", "MOVE", "MOVE", "LEFT", "MOVE", "REPORT");
        RobotSim.runCommands(robot, commandsList);

        assertFalse(robot.isPlaced());
    }

    @Test
    void handleInvalidInput2RunCommands() {
        var robot = new RobotSim.Robot(5);
        List<String> commandsList = List.of("PLACE A,0,EAST", "MOVE", "LEFT", "RIGHT", "PLACE 0,B,EAST", "PLACE A,B,EAST", "PLACE 0,0,C", "PLACE A,B,C", "REPORT");
        RobotSim.runCommands(robot, commandsList);

        assertFalse(robot.isPlaced());
    }

    @Test
    void handleInvalidInput3RunCommands() {
        var robot = new RobotSim.Robot(5);
        List<String> commandsList = List.of("0, PLACE 0,0,EAST,0", "PLACE 0,0,EAST,0", "REPORT", "REPORT");
        RobotSim.runCommands(robot, commandsList);

        assertFalse(robot.isPlaced());
    }

    @Test
    void hanldleValidParseFile() {
        List<String> outputList = RobotSim.parseFile(Path.of("src/main/resources/robotInput/inputA.txt"));
        List<String> expectedOutputList = List.of("PLACE 0,0,NORTH", "MOVE", "REPORT");

        assertEquals(outputList, expectedOutputList);
    }

    @Test
    void handleInvalidParseFile() {
        List<String> outputList = RobotSim.parseFile(Path.of("src/main/resources/robotInput/INVALID_INPUT.txt"));

        assertTrue(outputList.isEmpty());
    }
}