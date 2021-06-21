package ch.umb.curo.demo;

import ch.umb.curo.starter.helper.camunda.CamundaVariable;
import ch.umb.curo.starter.helper.camunda.CamundaVariableDefinition;
import ch.umb.curo.starter.helper.camunda.annotation.InitWithEmpty;

public class JavaCamundaVariables {

    public static final CamundaVariableDefinition<String> ONBOARDING_TYPE = new CamundaVariable<>("onboardingType", String.class);
    public static final CamundaVariableDefinition<String> INITIATOR = new CamundaVariable<>("initiator", String.class);

    // TODO --> use ENUM!!
    @InitWithEmpty
    public static final CamundaVariableDefinition<String> CLIENT_MAIN_TYPE = new CamundaVariable<>("clientMainType", String.class);
    @InitWithEmpty
    public static final CamundaVariableDefinition<String> CLIENT_MAIN_TYPE_ALIAS = new CamundaVariable<>("clientMainTypeAlias", String.class);

}
