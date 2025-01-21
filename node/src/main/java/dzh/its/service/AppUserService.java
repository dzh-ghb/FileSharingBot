package dzh.its.service;

import dzh.its.entity.AppUser;

public interface AppUserService { //для обработки запроса на регистрацию юзера и
    //проверки введенного адреса электронной почты на соответствие шаблону
    String registerUser(AppUser appUser);

    String setEmail(AppUser appUser, String email);
}