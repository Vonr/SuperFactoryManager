import java.util.stream.IntStream;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class StreamCastTest {

	@Test
	@RepeatedTest(16)
	public void AnonymousCast() {
		System.out.println(IntStream.range(0, 160000000)
			.mapToObj(MyClass::new)
			.map(x -> (Object) x)
			.map(x -> (MyClass) x)
			.mapToInt(MyClass::getValue)
			.sum());
	}

	@Test
	@RepeatedTest(16)
	public void MethodReferenceCast() {
		System.out.println(IntStream.range(0, 160000000)
			.mapToObj(MyClass::new)
			.map(Object.class::cast)
			.map(MyClass.class::cast)
			.mapToInt(MyClass::getValue)
			.sum());
	}

	private static class MyClass {

		private final int value;

		public MyClass(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
