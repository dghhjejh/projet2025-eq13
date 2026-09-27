package ca.ulaval.glo4002.application.domain.bottin;

public interface UserRepository{

  User findUserByIDUL(Idul idul);
}
