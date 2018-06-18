package handling.impl;

import handling.AbstractHandle;
import pro.nextbit.telegramconstructor.stepmapping.Step;


public class DefaultHandle extends AbstractHandle {

    @Step("")
    private void getTaskList(){
        redirect("M_menu");
    }
}
