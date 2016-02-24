/**
 * Command.java
 * Copyright (c) 2016 Mai Anh Vu
 */

/**
 * An abstraction of the commands to be executed in the application.
 * Commands normally contains two parts: the instruction and the parameter.
 * Using the interpret method, a raw command string is automatically parsed accordingly:
 *      void parseAndExecute(String rawCommand) {
 *          Command command = Command.interpret(rawCommand);
 *          ...
 *      }
 * The instruction type and the parameter can then be queried using getType() and getParameter().
 */
public class Command {

    /**
     * Constants
     */
    private static final String STRING_COMMAND_ADD = "add";
    private static final String STRING_COMMAND_DELETE = "delete";
    private static final String STRING_COMMAND_DISPLAY = "display";
    private static final String STRING_COMMAND_CLEAR = "clear";
    private static final String STRING_COMMAND_SORT = "sort";
    private static final String STRING_COMMAND_SEARCH = "search";
    private static final String STRING_COMMAND_EXIT = "exit";

    private static final String STRING_DELIMITER_COMMAND = "\\s+";
    private static final int COUNT_PARTS_COMMAND = 2;
    private static final int ID_PART_COMMAND_INSTRUCTION = 0;
    private static final int ID_PART_COMMAND_PARAMETER = 1;

    /**
     * Command types
     */
    public enum Type {
        ADD(STRING_COMMAND_ADD),
        DISPLAY(STRING_COMMAND_DISPLAY),
        DELETE(STRING_COMMAND_DELETE),
        CLEAR(STRING_COMMAND_CLEAR),
        SORT(STRING_COMMAND_SORT),
        SEARCH(STRING_COMMAND_SEARCH),
        EXIT(STRING_COMMAND_EXIT),
        UNRECOGNISED(null);

        final String instruction;

        Type(String inst) {
            instruction = inst;
        }
    }
    /**
     * Properties
     */
    private final Type commandType_;
    private final String parameter_;

    /**
     * Constructor
     *
     * @param commandType The type of the command
     * @param parameter   Associated data
     */
    private Command(Type commandType, String parameter) {
        this.commandType_ = commandType;
        this.parameter_ = parameter;
    }

    /**
     * Interprets the raw command string, determines its instruction type and parameter
     * @param rawCommand a command string
     * @return the command object interpreted from the raw string
     */
    public static Command interpret(String rawCommand) {
        String[] commandParts = splitRawCommandIntoParts(rawCommand);

        // Initialize type to null first
        Type commandType = null;

        // If the command is empty, set it to unrecognised
        if (commandParts.length == 0) {
            return new Command(Type.UNRECOGNISED, null);
        }

        // Determine the command instruction type
        commandType = inferCommandTypeFromInstruction(commandParts[ID_PART_COMMAND_INSTRUCTION]);

        // Determine parameter
        String parameter = null;
        if (commandParts.length > ID_PART_COMMAND_PARAMETER) {
            parameter = commandParts[ID_PART_COMMAND_PARAMETER];
        }

        return new Command(commandType, parameter);
    }

    private static String[] splitRawCommandIntoParts(String rawCommand) {
        return rawCommand.trim().split(STRING_DELIMITER_COMMAND, COUNT_PARTS_COMMAND);
    }

    private static Type inferCommandTypeFromInstruction(String instruction) {
        // Case insensitive matching
        instruction = instruction.trim().toLowerCase();

        // Iterate through all command types and see if the instruction
        // matches any of the pre-registered types
        for (Type type : Type.values()) {
            if (instruction.equals(type.instruction)) {
                return type;
            }
        }

        // Cannot find, return unrecognised
        return Type.UNRECOGNISED;
    }

    /**
     * Returns the instruction type of the command.
     * @return instruction type
     */
    public Type getType() {
        return this.commandType_;
    }

    /**
     * Returns the parameter associated with the command.
     * @return the parameter of the command
     */
    public String getParameter() {
        return this.parameter_;
    }

    /**
     * Returns whether the command instruction is unrecognised.
     * @return if instruction is unrecognised
     */
    public boolean isUnrecognised() {
        return this.commandType_ == Type.UNRECOGNISED;
    }
}
