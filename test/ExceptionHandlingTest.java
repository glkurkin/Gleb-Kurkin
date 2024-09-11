import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ExceptionHandlingTest {

    @Test
    public void testFileLoadException() {
        assertThrows(FileLoadException.class, () -> {
            FileBackedTaskManager.loadFromFile(new File("неверный_путь.csv"));
        });
    }
    
}