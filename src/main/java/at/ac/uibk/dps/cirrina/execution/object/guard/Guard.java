package at.ac.uibk.dps.cirrina.execution.object.guard;

import at.ac.uibk.dps.cirrina.execution.object.context.Extent;
import at.ac.uibk.dps.cirrina.execution.object.expression.Expression;
import jakarta.annotation.Nullable;
import java.util.Optional;

/**
 * Guard, represents an evaluable guard that yields a boolean return value.
 */
public class Guard {

  /**
   * Guard name, only applicable for a named guard. An in-line guard can have null as its name.
   */
  private final @Nullable String name;

  /**
   * Guard expression.
   */
  private final Expression expression;

  /**
   * Initializes this guard object.
   * <p>
   * The name can be empty in case the guard is in-line.
   *
   * @param name       Name of the guard.
   * @param expression Expression of the guard.
   */
  Guard(@Nullable String name, Expression expression) {
    this.name = name;
    this.expression = expression;
  }

  /**
   * Evaluate this guard and return the resulting boolean value.
   *
   * @param extent Extent describing variables in scope.
   * @return Boolean result.
   * @throws UnsupportedOperationException If the guard expression could not be executed.
   * @throws IllegalArgumentException      If the expression could not be evaluated, or the expression does not produce a boolean value.
   */
  public boolean evaluate(Extent extent) throws IllegalArgumentException, UnsupportedOperationException {
    var result = expression.execute(extent);

    if (!(result instanceof Boolean)) {
      throw new IllegalArgumentException("Guard expression '%s' does not produce a boolean value".formatted(expression));
    }

    return (Boolean) result;
  }

  /**
   * Returns the guard name.
   *
   * @return Guard name.
   */
  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  /**
   * Returns the expression.
   *
   * @return Expression.
   */
  public Expression getExpression() {
    return expression;
  }
}
