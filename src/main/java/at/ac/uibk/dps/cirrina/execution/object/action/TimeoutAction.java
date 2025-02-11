package at.ac.uibk.dps.cirrina.execution.object.action;

import at.ac.uibk.dps.cirrina.execution.object.expression.Expression;
import java.util.Optional;

/**
 * Timeout action object.
 */
public final class TimeoutAction extends Action {

  /**
   * The delay expression. Needs to evaluate to a numeric value, representing the delay in milliseconds.
   *
   * @see Number
   */
  private final Expression delay;

  /**
   * The action to execute after the timeout.
   */
  private final Action action;

  /**
   * Initializes this timeout action object.
   *
   * @param parameters Parameters.
   */
  TimeoutAction(Parameters parameters) {
    super(Optional.of(parameters.name()));

    this.delay = parameters.delay;
    this.action = parameters.action;
  }

  /**
   * Returns the delay expression.
   *
   * @return Delay expression.
   */
  public Expression getDelay() {
    return delay;
  }

  /**
   * Returns the action.
   *
   * @return Action.
   */
  public Action getAction() {
    return action;
  }

  public record Parameters(
      String name,
      Expression delay,
      Action action
  ) {

  }
}
