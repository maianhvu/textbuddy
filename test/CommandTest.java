/**
 * Copyright (c) 2016 Mai Anh Vu
 */
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by maianhvu on 21/2/16.
 */
public class CommandTest {

    /**
     * Constants
     */
    private static final String[] STRINGS_COMMAND_VALID = new String[]{
            "add", "display", "delete", "clear", "exit", "sort", "search"
    };

    @Test
    public void Valid_commands_get_recognised_correctly() {
        for (String commandString : STRINGS_COMMAND_VALID) {
            Command command = Command.interpret(commandString);
            assertThat(command.isUnrecognised(), is(false));
        }
    }

    @Test
    public void Invalid_commands_are_flagged_as_unrecognised() {
        final String[] invalidCommands = new String[]{
                "push", "show", "remove", "clean", "quit", ""
        };
        for (String commandString : invalidCommands) {
            Command command = Command.interpret(commandString);
            assertThat(command.isUnrecognised(), is(true));
        }
    }

    @Test
    public void Valid_commands_in_different_casing_still_get_recognised_correctly() {
        for (String commandString : STRINGS_COMMAND_VALID) {
            commandString = commandString.toUpperCase();
            Command command = Command.interpret(commandString);
            assertThat(command.isUnrecognised(), is(false));
        }
    }

    @Test
    public void Commands_get_split_into_instruction_and_parameter_correctly() {
        Command command = Command.interpret("add Lorem ipsum dolor sit amet");
        assertThat(command.getType(), is(Command.Type.ADD));
        assertThat(command.getParameter(), equalTo("Lorem ipsum dolor sit amet"));
    }

    @Test
    public void Commands_get_split_correctly_with_extra_spaces() {
        Command command = Command.interpret("    delete        1   ");
        assertThat(command.getType(), is(Command.Type.DELETE));
        assertThat(command.getParameter(), equalTo("1"));
    }
}
