import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by maianhvu on 21/2/16.
 */
public class CommandTest {

    private static final String[] STRINGS_COMMAND_VALID = new String[] {
            "add", "display", "delete", "clear", "exit"
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
        final String[] invalidCommands = new String[] {
                "push", "show", "remove", "clean", "quit", ""
        };
        for (String commandString : invalidCommands) {
            Command command = Command.interpret(commandString);
            assertThat(command.isUnrecognised(), is(true));
        }
    }

    @Test
    public void Valid_commands_in_different_casing_still_get_recognised_correctly() {
        for (String commandString: STRINGS_COMMAND_VALID) {
            commandString = commandString.toUpperCase();
            Command command = Command.interpret(commandString);
            assertThat(command.isUnrecognised(), is(false));
        }
    }
}
