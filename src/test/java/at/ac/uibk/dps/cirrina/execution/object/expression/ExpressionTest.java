package at.ac.uibk.dps.cirrina.execution.object.expression;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.ac.uibk.dps.cirrina.csml.description.ExpressionDescription;
import at.ac.uibk.dps.cirrina.execution.object.context.Extent;
import at.ac.uibk.dps.cirrina.execution.object.context.InMemoryContext;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ExpressionTest {

  @Test
  public void testExpression() throws Exception {
    try (var context = new InMemoryContext(true)) {
      assertDoesNotThrow(() -> {
        var extent = new Extent(context);

        var bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(0xBAD1D);
        var list = List.of(-1, 1, true, "foobar");

        context.create("varPlusOneInt", +1);
        context.create("varNegativeOneInt", -1);
        context.create("varPlusOneDouble", +1.0);
        context.create("varNegativeOneDouble", -1.0);
        context.create("varTrueBool", true);
        context.create("varFalseBool", false);
        context.create("varFoobarString", "foobar");
        context.create("varBad1dBytes", bytes);
        context.create("varVariousList", list);

        assertEquals(2, ExpressionBuilder.from(new ExpressionDescription("varPlusOneInt+1")).build().execute(extent));
        assertEquals(-2, ExpressionBuilder.from(new ExpressionDescription("varNegativeOneInt-1")).build().execute(extent));
        assertEquals(2.0, ExpressionBuilder.from(new ExpressionDescription("varPlusOneDouble+1.0")).build().execute(extent));
        assertEquals(-2.0, ExpressionBuilder.from(new ExpressionDescription("varNegativeOneDouble-1.0")).build().execute(extent));
        assertEquals(false, ExpressionBuilder.from(new ExpressionDescription("!varTrueBool")).build().execute(extent));
        assertEquals(true, ExpressionBuilder.from(new ExpressionDescription("!varFalseBool")).build().execute(extent));
        assertEquals("foobar", ExpressionBuilder.from(new ExpressionDescription("varFoobarString")).build().execute(extent));
        assertEquals(bytes, ExpressionBuilder.from(new ExpressionDescription("varBad1dBytes")).build().execute(extent));
        assertEquals(list, ExpressionBuilder.from(new ExpressionDescription("varVariousList")).build().execute(extent));
      });
    }
  }

  @Test
  public void testUtility() throws Exception {
    try (var context = new InMemoryContext(true)) {
      assertDoesNotThrow(() -> {
        var extent = new Extent(context);

        for (int i = 0; i < 100; ++i) {
          final var bytes = ExpressionBuilder.from(
                  new ExpressionDescription("utility:genRandPayload([1024, 1024 * 10, 1024 * 100, 1024 * 1000])")).build()
              .execute(extent);

          final var expectedOneOf = List.of(1024, 1024 * 10, 1024 * 100, 1024 * 1000);

          assertInstanceOf(byte[].class, bytes);
          assertTrue(expectedOneOf.contains(((byte[]) bytes).length));
        }
      });
    }
  }

  @Test
  public void testMultiLineExpression() throws Exception {
    try (var context = new InMemoryContext(true)) {
      assertDoesNotThrow(() -> {
        var extent = new Extent(context);

        context.create("varOneInt", 1);

        var multiLineExpression =
            new ExpressionDescription("let varExpressionLocal = 1; varExpressionLocal += varOneInt; varExpressionLocal");
        assertEquals(2, ExpressionBuilder.from(multiLineExpression).build().execute(extent));
      });
    }
  }

  @Test
  public void testExpressionUsingNamespace() throws Exception {
    try (var context = new InMemoryContext(true)) {
      assertEquals(1, ExpressionBuilder.from(new ExpressionDescription("math:abs(-1)")).build().execute(new Extent(context)));
    }
  }

  @Test
  public void testExpressionNegative() throws Exception {
    try (var context = new InMemoryContext(true)) {
      var extent = new Extent(context);

      context.create("varOneInt", 1);

      // Throws while parsing
      assertThrows(UnsupportedOperationException.class,
          () -> ExpressionBuilder.from(new ExpressionDescription("1 + ")).build().execute(extent));
      assertThrows(UnsupportedOperationException.class,
          () -> ExpressionBuilder.from(new ExpressionDescription("varOneInt = 2")).build().execute(extent));

      // Throws at runtime
      assertThrows(UnsupportedOperationException.class,
          () -> ExpressionBuilder.from(new ExpressionDescription("varInvalid")).build().execute(extent));
      assertThrows(UnsupportedOperationException.class,
          () -> ExpressionBuilder.from(new ExpressionDescription("!varInvalid")).build().execute(extent));
      assertThrows(UnsupportedOperationException.class,
          () -> ExpressionBuilder.from(new ExpressionDescription("varInvalid.varInvalidSub")).build().execute(extent));
      assertThrows(UnsupportedOperationException.class,
          () -> ExpressionBuilder.from(new ExpressionDescription("varInvalid + 1")).build().execute(extent));
      assertThrows(UnsupportedOperationException.class,
          () -> ExpressionBuilder.from(new ExpressionDescription("let varTemp = varInvalid; varTemp")).build().execute(extent));
    }
  }
}
