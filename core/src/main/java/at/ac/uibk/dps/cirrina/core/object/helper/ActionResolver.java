package at.ac.uibk.dps.cirrina.core.object.helper;

import at.ac.uibk.dps.cirrina.core.lang.classes.action.ActionClass;
import at.ac.uibk.dps.cirrina.core.lang.classes.action.ActionReferenceClass;
import at.ac.uibk.dps.cirrina.core.lang.classes.helper.ActionOrActionReferenceClass;
import at.ac.uibk.dps.cirrina.core.object.action.Action;
import at.ac.uibk.dps.cirrina.core.object.action.ActionBuilder;
import at.ac.uibk.dps.cirrina.core.object.statemachine.StateMachine;
import java.util.Optional;

public final class ActionResolver {

  private final StateMachine stateMachine;

  public ActionResolver(StateMachine stateMachine) {
    this.stateMachine = stateMachine;
  }

  public Optional<Action> resolve(ActionOrActionReferenceClass actionOrActionReferenceClass) throws IllegalStateException {
    switch (actionOrActionReferenceClass) {
      // An inline action is provided as an action class, since this action is inline it needs to be constructed
      case ActionClass actionClass -> {
        return Optional.of(ActionBuilder.from(actionClass, null /* TODO: Make me optional */).build());
      }
      // An action reference is a reference to a named action contained within the state machine, we provide this action
      case ActionReferenceClass actionReferenceClass -> {
        return stateMachine.findActionByName(actionReferenceClass.reference);
      }
      default -> {
        return Optional.empty();
      }
    }
  }
}
