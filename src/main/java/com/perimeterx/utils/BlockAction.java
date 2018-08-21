package com.perimeterx.utils;

/**
 * Created by nitzangoldfeder on 29/06/2017.
 */
public enum BlockAction {
    BLOCK(Constants.BLOCK_ACTION_CAPTCHA), CAPTCHA(Constants.CAPTCHA_ACTION_CAPTCHA), CHALLENGE(Constants.BLOCK_ACTION_CHALLENGE);


    private final String code;

    BlockAction(String code){
        this.code = code;
    }

    public final String getCode(){
        return code;
    }


}
