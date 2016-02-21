/**
 * Created by maianhvu on 21/2/16.
 */
public class Command {

    /**
     * Types
     */
    public enum Type {
        ADD, DISPLAY, DELETE, CLEAR, EXIT, UNRECOGNISED;
    }

    /**
     * Properties
     */
    private Type commandType_;
    private String parameter_;

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

    public static Command interpret(String rawCommand) {
        String[] commandParts = splitRawCommandIntoParts(rawCommand);


        Type commandType = null;
        if (commandParts.length == 0) {
            return new Command(Type.UNRECOGNISED, null);
        }

        commandType = inferCommandTypeFromInstruction(commandParts[0]);

        String parameter = null;
        if (commandParts.length > 1) {
            parameter = commandParts[1];
        }
        return new Command(commandType, parameter);
    }

    public Type getType() {
        return this.commandType_;
    }

    public String getParameter() {
        return this.parameter_;
    }

    public boolean isUnrecognised() {
        return this.commandType_ == Type.UNRECOGNISED;
    }

    private static String[] splitRawCommandIntoParts(String rawCommand) {
        return rawCommand.split("\\s+", 2);
    }

    private static Type inferCommandTypeFromInstruction(String instruction) {
        instruction = instruction.trim().toLowerCase();

        switch (instruction) {
            case "add":
                return Type.ADD;
            case "delete":
                return Type.DELETE;
            case "display":
                return Type.DISPLAY;
            case "clear":
                return Type.CLEAR;
            case "exit":
                return Type.EXIT;
            default:
                return Type.UNRECOGNISED;
        }
    }
}
