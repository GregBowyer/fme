package unit.sve;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import sve.RandomNumberGenerator;

public class RandomNumberGeneratorTest {

    @org.junit.Test
    public void testRandomNumberGeneration() {

        int num1 = RandomNumberGenerator.genRandomNumber("TEST", 1, 1, 10);
        int num2 = RandomNumberGenerator.genRandomNumber("TEST", 1, 1, 10);

        assertThat(num1, is(num2));

        num1 = RandomNumberGenerator.genRandomNumber("TEST", 1, 10, 10);
        num2 = RandomNumberGenerator.genRandomNumber("TEST", 1, 10, 10);

        assertThat(num1, is(not(num2)));
    }
}
