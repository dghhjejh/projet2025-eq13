package ca.ulaval.glo4002.application.domain.agents;

import ca.ulaval.glo4002.application.domain.actions.enums.Priority;

public record Agent(AgentId agentId, InterventionId interventionId, Priority priority) {
}
