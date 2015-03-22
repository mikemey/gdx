package uk.mmi.gaming;

import org.junit.Test;

import com.badlogic.gdx.math.MathUtils;

public class MathTest {

	@Test
	public void testJavaMathTangens() {
		double x = 3;
		double y = 3;

		System.out.println(Math.toDegrees(Math.atan2(x, y)));
	}

	@Test
	public void testGdxMathTangens() {
		float x = 3.0f;
		float y = 3.0f;

		System.out.println(MathUtils.radiansToDegrees * MathUtils.atan2(x, y));
	}

	@Test
	public void testAtan() {
		System.out.println("Math.atan2(0, 5)" + Math.atan2(0, 5));
		System.out.println("Math.atan2(2, 4)" + Math.atan2(2, 4));
		System.out.println("Math.atan2(4, 2)" + Math.atan2(4, 2));
		System.out.println("Math.atan2(5, 0)" + Math.atan2(5, 0));
		System.out.println("Math.atan2(4, -3)" + Math.atan2(4, -3));
		System.out.println("Math.atan2(0, -5)" + Math.atan2(0, -5));
		System.out.println("Math.atan2(-3, -4)" + Math.atan2(-3, -4));
		System.out.println("Math.atan2(-4, 0)" + Math.atan2(-4, 0));
		System.out.println("Math.atan2(-2, 4)" + Math.atan2(-2, 4));
	}

	@Test
	public void testrad() {
		System.out.println(Math.toDegrees(-0.050881855027973766));
	}
}
