import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.util.Util;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;


public class StreamOptionalTest {
	private static final Random rand = new Random(42);

	@Test
	@RepeatedTest(64)
	public void MapFilterMap() {
		System.out.println(IntStream.range(1, 200000)
			.boxed()
			.map(StreamOptionalTest::getInt)
			.filter(Optional::isPresent)
			.mapToInt(Optional::get)
			.sum());
	}

	@Test
	@RepeatedTest(64)
	public void UtilStreamOptional(){
		System.out.println(IntStream.range(1, 200000)
			.boxed()
			.flatMap(i -> Util.streamOptional(getInt(i)))
			.mapToInt(Integer::intValue)
			.sum());
	}


	public static Optional<Integer> getInt(int max) {
		int i = rand.nextInt(max);
		return i > 50 ? Optional.empty() : Optional.of(i);
	}
}
