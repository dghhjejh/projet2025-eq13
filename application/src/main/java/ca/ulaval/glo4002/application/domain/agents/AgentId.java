package ca.ulaval.glo4002.application.domain.agents;

import java.util.UUID;

public class AgentId{
  private final UUID agentId;

  public AgentId() {
    this.agentId = UUID.randomUUID();
  }

  public AgentId(String uuidString) {
    this.agentId = UUID.fromString(uuidString);
  }

  @Override
  public int hashCode(){
    return this.agentId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    AgentId that = (AgentId) o;
    return this.agentId.equals(that.agentId);
  }

  @Override
  public String toString(){
    return this.agentId.toString();
  }
}
